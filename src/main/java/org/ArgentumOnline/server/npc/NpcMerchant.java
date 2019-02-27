package org.ArgentumOnline.server.npc;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.protocol.ChangeNPCInventorySlotResponse;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;

public class NpcMerchant extends Npc {

	protected NpcMerchant(int npc_numero, GameServer server) {
		super(npc_numero, server);
	}
	
	@Override
	protected void leerNpc(IniFile ini, int npc_ind) {
		super.leerNpc(ini, npc_ind);
		
        //Inventario
        loadInventario(ini);
	}	

    private void loadInventario(IniFile ini) {
        String section = "NPC" + this.m_numero;
        int cant = ini.getInt(section, "NROITEMS");
        if (cant > 0) {
            if (cant > MAX_OBJS_X_SLOT) {
				cant = MAX_OBJS_X_SLOT;
			}
            for (int j = 0; j < cant; j++) {
                String objName = "Obj" + (j+1);
                String objTmp  = ini.getString(section, objName).split("'")[0].trim();
                int sep = objTmp.indexOf('-');
                if (sep != -1) {
					try {
                        //Separar las cadenas "ObjInd"-"Cant".
                        String objid = objTmp.substring(0, sep);
                        String objcnt = objTmp.substring(sep+1);
                        //System.out.println("DEBUG: m_numero=" + m_numero + " nombre=" + m_name);
                        //System.out.println("DEBUG: objTmp=" + objTmp);
                        //System.out.println("DEBUG: objid=" + objid);
                        //System.out.println("DEBUG: objcnt=" + objcnt);
                        this.m_inv.setObjeto(j+1, new InventoryObject(Short.parseShort(objid), Integer.parseInt(objcnt)));
                        //m_inv.getObjeto(j+1).objid = Short.parseShort(objid);
                        //m_inv.getObjeto(j+1).cant  = Integer.parseInt(objcnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
            }
        }
    }        
	
    public void enviarNpcInv(Player cliente) {
        // Sub EnviarNpcInv(ByVal UserIndex As Integer, ByVal NpcIndex As Integer)
        // Enviamos el inventario del npc con el cual el user va a comerciar...
        double dto = cliente.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        for (byte i = 1; i <= this.m_inv.size(); i++) {
        	
            if (this.m_inv.getObjeto(i).objid > 0) {
            	
                // Calculamos el porc de inflacion del npc
                ObjectInfo info = findObj(this.m_inv.getObjeto(i).objid);
                double infla = (this.m_inflacion *  info.Valor) / 100;
                double val = (info.Valor + infla) / dto;
                cliente.sendPacket(new ChangeNPCInventorySlotResponse( 
                		i, 
                		info.Nombre, 
                		(short) this.m_inv.getObjeto(i).cant, 
                		((int) val),
                		info.GrhIndex, 
                		this.m_inv.getObjeto(i).objid, 
                		(byte) info.ObjType,
                		info.MaxHIT, 
                		info.MinHIT, 
                		info.MaxDef));
            } else {
                cliente.sendPacket(new ChangeNPCInventorySlotResponse(
                		(byte) i, "Nada", (short) 0, 0, (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0));
            }
        }
    }
    
    public void venderItem(Player cliente, short slot, int cant) {
        // Sub NPCVentaItem(ByVal UserIndex As Integer, ByVal i As Integer, ByVal Cantidad As Integer, ByVal NpcIndex As Integer)
        if (cant < 1) {
			return;
		}
        // NPC VENDE UN OBJ A UN USUARIO
        cliente.sendUpdateUserStats();
        // Calculamos el valor unitario
        if (this.m_inv.getObjeto(slot).objid == 0) {
			return;
		}
        // Calculamos el porc de inflacion del npc
        ObjectInfo info = findObj(this.m_inv.getObjeto(slot).objid);
        double dto = cliente.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        double infla = (this.m_inflacion * info.Valor) / 100;
        double val = (info.Valor + infla) / dto;
        
        if (cliente.getEstads().getGold() < (val * cant)) {
            cliente.enviarMensaje("No tienes suficiente oro.", FontType.FONTTYPE_INFO);
            return;
        }
        
        if (this.m_inv.getObjeto(slot).cant > 0) {
            if (cant > this.m_inv.getObjeto(slot).cant) {
                cant = this.m_inv.getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            cliente.userCompraObj(this, slot, cant);
            // Actualizamos el inventario del usuario
            cliente.enviarInventario();
            // Actualizamos el oro
            cliente.sendUpdateUserStats();
            // Actualizamos la ventana de comercio
            enviarNpcInv(cliente);
            cliente.updateVentanaComercio(slot, (short) 0);
        }
    }

    public void comprarItem(Player cliente, short slot, int cant) {
        // Sub NPCCompraItem(ByVal UserIndex As Integer, ByVal Item As Integer, ByVal Cantidad As Integer)
        // NPC COMPRA UN OBJ A UN USUARIO
        cliente.sendUpdateUserStats();
        if (cliente.getInv().getObjeto(slot).cant > 0 && !cliente.getInv().getObjeto(slot).equipado) {
            if (cant > 0 && cant > cliente.getInv().getObjeto(slot).cant) {
                cant = cliente.getInv().getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            npcCompraObj(cliente, slot, cant);
            // Actualizamos el inventario del usuario
            cliente.enviarInventario();
            // Actualizamos el oro
            cliente.sendUpdateUserStats();
            enviarNpcInv(cliente);
            // Actualizamos la ventana de comercio
            cliente.updateVentanaComercio(slot, (short) 1);
        }
    }
    
    public void npcCompraObj(Player cliente, short slot, int cant) {
        // Sub NpcCompraObj(ByVal UserIndex As Integer, ByVal objIndex As Integer, ByVal Cantidad As Integer)
        if (cant < 1) {
			return;
		}
        short objid = cliente.getInv().getObjeto(slot).objid;
        ObjectInfo info = findObj(objid);
        if (info.esNewbie()) {
            cliente.enviarMensaje("No comercio objetos para newbies.", FontType.FONTTYPE_INFO);
            return;
        }
        if (this.m_tipoItems != OBJTYPE_CUALQUIERA) {
            // ¿Son los items con los que comercia el npc?
            if (this.m_tipoItems != info.ObjType) {
                cliente.enviarMensaje("No me interesa comprar ese objeto.", FontType.FONTTYPE_WARNING);
                return;
            }
        }
        int slot_inv = 0;
        // ¿Ya tiene un objeto de este tipo?
        for (int i = 1; i <= this.m_inv.size(); i++) {
            if (this.m_inv.getObjeto(i).objid == objid && this.m_inv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
                slot_inv = i;
                break;
            }
        }
        // Sino se fija por un slot vacio antes del slot devuelto
        if (slot_inv == 0) {
            slot_inv = this.m_inv.getSlotLibre();
            //If Slot <= MAX_INVENTORY_SLOTS Then Npclist(NpcIndex).Invent.NroItems = Npclist(NpcIndex).Invent.NroItems + 1
        }
        if (slot_inv > 0) { // Slot valido
            // Mete el obj en el slot
            if (this.m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
                // Menor que MAX_INV_OBJS
                this.m_inv.getObjeto(slot_inv).objid = objid;
                this.m_inv.getObjeto(slot_inv).cant += cant;
                cliente.getInv().quitarUserInvItem(slot, cant);
                // Le sumamos al user el valor en oro del obj vendido
                //double monto = ((info.Valor / 3 + infla) * cant);
                double monto = ((info.Valor / 3) * cant);
                cliente.getEstads().addGold((int) monto);
                // tal vez suba el skill comerciar ;-)
                cliente.subirSkill(Skill.SKILL_Comerciar);
            } else {
                cliente.enviarMensaje("No puedo cargar tantos objetos.", FontType.FONTTYPE_INFO);
            }
        } else {
            cliente.getInv().quitarUserInvItem(slot, cant);
            // Le sumamos al user el valor en oro del obj vendido
            //double monto = ((info.Valor / 3 + infla) * cant);
            double monto = ((info.Valor / 3) * cant);
            cliente.getEstads().addGold((int) monto);
        }
    }

    public void quitarNpcInvItem(short slot, int cant) {
        // Sub QuitarNpcInvItem(ByVal NpcIndex As Integer, ByVal Slot As Byte, ByVal Cantidad As Integer)
        short objid = this.m_inv.getObjeto(slot).objid;
        // Quita un Obj
        ObjectInfo info = findObj(objid);
        if (!info.esCrucial()) {
            this.m_inv.getObjeto(slot).cant -= cant;
            if (this.m_inv.getObjeto(slot).cant <= 0) {
                this.m_inv.getObjeto(slot).objid = 0;
                this.m_inv.getObjeto(slot).cant = 0;
                if (this.m_inv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        } else {
            this.m_inv.getObjeto(slot).cant -= cant;
            if (this.m_inv.getObjeto(slot).cant <= 0) {
                this.m_inv.getObjeto(slot).objid = 0;
                this.m_inv.getObjeto(slot).cant = 0;
                if (!quedanItems(objid)) {
                    this.m_inv.getObjeto(slot).objid = objid;
                    this.m_inv.getObjeto(slot).cant = encontrarCant(objid);
                }
                if (this.m_inv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        }
    }

    private void cargarInvent() {
        // Sub CargarInvent(ByVal NpcIndex As Integer)
        // Vuelve a cargar el inventario del npc NpcIndex
        IniFile ini = this.server.getNpcLoader().getIniFile(this.m_numero, false);
        loadInventario(ini);
    }

    private boolean quedanItems(short objid) {
        // Function QuedanItems(ByVal NpcIndex As Integer, ByVal objIndex As Integer) As Boolean
        for (int i = 1; i <= MAX_INVENTORY_SLOTS; i++) {
            if (this.m_inv.getObjeto(i).objid == objid) {
                return true;
            }
        }
        return false;
    }
     
    private int encontrarCant(short objid) {
    	if (DEBUG)
    		System.out.println("+++++DEBUG++++ encontrarCant() " + this.m_inv.isEmpty());
    	
        IniFile ini = this.server.getNpcLoader().getIniFile(this.m_numero, false);
        String section = "NPC" + this.m_numero;
        int cant = ini.getInt(section, "NROITEMS");
        if (cant > 0) {
            if (cant > MAX_OBJS_X_SLOT) {
                cant = MAX_OBJS_X_SLOT;
            }
            for (int j = 0; j < cant; j++) {
                String objName = "Obj" + (j+1);
                String objTmp  = ini.getString(section, objName);
                int sep = objTmp.indexOf('-');
                if (sep != -1) {
                    try {
                        // Separar las cadenas "ObjInd"-"Cant".
                        short objid_inv = Short.parseShort(objTmp.substring(0, sep));
                        int cant_inv = Integer.parseInt(objTmp.substring(sep+1));
                        if (objid_inv == objid) {
                            return cant_inv;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 0;
    }
    
	
}
