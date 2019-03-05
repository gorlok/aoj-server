/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
		
        loadInventario(ini);
	}	

    private void loadInventario(IniFile ini) {
        String section = "NPC" + this.npcNumber;
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
                        this.npcInv.setObjeto(j+1, new InventoryObject(Short.parseShort(objid), Integer.parseInt(objcnt)));
                        //m_inv.getObjeto(j+1).objid = Short.parseShort(objid);
                        //m_inv.getObjeto(j+1).cant  = Integer.parseInt(objcnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
            }
        }
    }        
	
    public void sendNpcInventoryToUser(Player player) {
        // Enviamos el inventario del npc con el cual el user va a comerciar...
        double dto = player.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        for (byte i = 1; i <= this.npcInv.size(); i++) {
            if (this.npcInv.getObjeto(i).objid > 0) {
                // Calculamos el porc de inflacion del npc
                ObjectInfo info = findObj(this.npcInv.getObjeto(i).objid);
                double infla = (this.inflation *  info.Valor) / 100;
                double val = (info.Valor + infla) / dto;
                player.sendPacket(new ChangeNPCInventorySlotResponse( 
                		i, 
                		info.Nombre, 
                		(short) this.npcInv.getObjeto(i).cant, 
                		((int) val),
                		info.GrhIndex, 
                		this.npcInv.getObjeto(i).objid, 
                		(byte) info.objType.value(),
                		info.MaxHIT, 
                		info.MinHIT, 
                		info.MaxDef));
            } else {
                player.sendPacket(new ChangeNPCInventorySlotResponse(
                		(byte) i, "Nada", (short) 0, 0, (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0));
            }
        }
    }
    
    public void venderItem(Player player, short slot, int cant) {
        if (cant < 1) {
			return;
		}
        // NPC VENDE UN OBJ A UN USUARIO
        player.sendUpdateUserStats();
        // Calculamos el valor unitario
        if (this.npcInv.getObjeto(slot).objid == 0) {
			return;
		}
        // Calculamos el porc de inflacion del npc
        ObjectInfo info = findObj(this.npcInv.getObjeto(slot).objid);
        double dto = player.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        double infla = (this.inflation * info.Valor) / 100;
        double val = (info.Valor + infla) / dto;
        
        if (player.stats().getGold() < (val * cant)) {
            player.sendMessage("No tienes suficiente oro.", FontType.FONTTYPE_INFO);
            return;
        }
        
        if (this.npcInv.getObjeto(slot).cant > 0) {
            if (cant > this.npcInv.getObjeto(slot).cant) {
                cant = this.npcInv.getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            player.userCompraObj(this, slot, cant);
            // Actualizamos el inventario del usuario
            player.sendInventoryToUser();
            // Actualizamos el oro
            player.sendUpdateUserStats();
            // Actualizamos la ventana de comercio
            sendNpcInventoryToUser(player);
            short objid = this.npcInv.getObjeto(slot).objid;
            player.updateVentanaComercio(objid, cant);
        }
    }

    public void comprarItem(Player player, short slot, int cant) {
        // NPC COMPRA UN OBJ A UN USUARIO
        player.sendUpdateUserStats();
        if (player.userInv().getObjeto(slot).cant > 0 && !player.userInv().getObjeto(slot).equipado) {
            if (cant > 0 && cant > player.userInv().getObjeto(slot).cant) {
                cant = player.userInv().getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            npcCompraObj(player, slot, cant);
            // Actualizamos el inventario del usuario
            player.sendInventoryToUser();
            // Actualizamos el oro
            player.sendUpdateUserStats();
            sendNpcInventoryToUser(player);
            // Actualizamos la ventana de comercio
            short objid = player.userInv().getObjeto(slot).objid;
            player.updateVentanaComercio(objid, cant);
        }
    }
    
    public void npcCompraObj(Player player, short slot, int cant) {
        if (cant < 1) {
			return;
		}
        short objid = player.userInv().getObjeto(slot).objid;
        ObjectInfo info = findObj(objid);
        if (info.esNewbie()) {
            player.sendMessage("No comercio objetos para newbies.", FontType.FONTTYPE_INFO);
            return;
        }
        if (this.tipoItems != OBJ_INDEX_CUALQUIERA) {
            // ¿Son los items con los que comercia el npc?
            if (this.tipoItems != info.objType.value()) {
                player.sendMessage("No me interesa comprar ese objeto.", FontType.FONTTYPE_WARNING);
                return;
            }
        }
        int slot_inv = 0;
        // ¿Ya tiene un objeto de este tipo?
        for (int i = 1; i <= this.npcInv.size(); i++) {
            if (this.npcInv.getObjeto(i).objid == objid && this.npcInv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
                slot_inv = i;
                break;
            }
        }
        // Sino se fija por un slot vacio antes del slot devuelto
        if (slot_inv == 0) {
            slot_inv = this.npcInv.getSlotLibre();
            //If Slot <= MAX_INVENTORY_SLOTS Then Npclist(NpcIndex).Invent.NroItems = Npclist(NpcIndex).Invent.NroItems + 1
        }
        if (slot_inv > 0) { // Slot valido
            // Mete el obj en el slot
            if (this.npcInv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
                // Menor que MAX_INV_OBJS
                this.npcInv.getObjeto(slot_inv).objid = objid;
                this.npcInv.getObjeto(slot_inv).cant += cant;
                player.userInv().quitarUserInvItem(slot, cant);
                // Le sumamos al user el valor en oro del obj vendido
                //double monto = ((info.Valor / 3 + infla) * cant);
                double monto = ((info.Valor / 3) * cant);
                player.stats().addGold((int) monto);
                // tal vez suba el skill comerciar ;-)
                player.subirSkill(Skill.SKILL_Comerciar);
            } else {
                player.sendMessage("No puedo cargar tantos objetos.", FontType.FONTTYPE_INFO);
            }
        } else {
            player.userInv().quitarUserInvItem(slot, cant);
            // Le sumamos al user el valor en oro del obj vendido
            //double monto = ((info.Valor / 3 + infla) * cant);
            double monto = ((info.Valor / 3) * cant);
            player.stats().addGold((int) monto);
        }
    }

    public void quitarNpcInvItem(short slot, int cant) {
        short objid = this.npcInv.getObjeto(slot).objid;
        // Quita un Obj
        ObjectInfo info = findObj(objid);
        if (!info.esCrucial()) {
            this.npcInv.getObjeto(slot).cant -= cant;
            if (this.npcInv.getObjeto(slot).cant <= 0) {
                this.npcInv.getObjeto(slot).objid = 0;
                this.npcInv.getObjeto(slot).cant = 0;
                if (this.npcInv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        } else {
            this.npcInv.getObjeto(slot).cant -= cant;
            if (this.npcInv.getObjeto(slot).cant <= 0) {
                this.npcInv.getObjeto(slot).objid = 0;
                this.npcInv.getObjeto(slot).cant = 0;
                if (!quedanItems(objid)) {
                    this.npcInv.getObjeto(slot).objid = objid;
                    this.npcInv.getObjeto(slot).cant = encontrarCant(objid);
                }
                if (this.npcInv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        }
    }

    private void cargarInvent() {
        // Vuelve a cargar el inventario del npc NpcIndex
        IniFile ini = this.server.getNpcLoader().getIniFile(this.npcNumber, false);
        loadInventario(ini);
    }

    private boolean quedanItems(short objid) {
        for (int i = 1; i <= MAX_INVENTORY_SLOTS; i++) {
            if (this.npcInv.getObjeto(i).objid == objid) {
                return true;
            }
        }
        return false;
    }
     
    private int encontrarCant(short objid) {
    	if (DEBUG)
    		System.out.println("+++++DEBUG++++ encontrarCant() " + this.npcInv.isEmpty());
    	
        IniFile ini = this.server.getNpcLoader().getIniFile(this.npcNumber, false);
        String section = "NPC" + this.npcNumber;
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
