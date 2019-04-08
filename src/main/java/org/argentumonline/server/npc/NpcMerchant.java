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
package org.argentumonline.server.npc;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjType;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Skill;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.protocol.ChangeNPCInventorySlotResponse;
import org.argentumonline.server.protocol.TradeOKResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;

public class NpcMerchant extends Npc {

	protected NpcMerchant(int npc_numero, GameServer server) {
		super(npc_numero, server);
	}
	
	@Override
	protected void loadNpc(IniFile ini, int npc_ind) {
		super.loadNpc(ini, npc_ind);
		
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
                        npcInv().setObject(j+1, new InventoryObject(Short.parseShort(objid), Integer.parseInt(objcnt)));
                        //m_inv.getObjeto(j+1).objid = Short.parseShort(objid);
                        //m_inv.getObjeto(j+1).cant  = Integer.parseInt(objcnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
            }
        }
    }
    
    @Override
    public void backupNpc(IniFile ini) {
    	super.backupNpc(ini);
        String section = "NPC" + this.npcNumber;
        
    	ini.setValue(section, "NROITEMS", npcInv().getObjectsCount());
    	int size = 0;
    	for (InventoryObject iObj : npcInv()) {
    		if (!iObj.estaVacio()) {
	    		size++;
	            ini.setValue(section, "Obj" + size, iObj.objid + "-" + iObj.cant);
    		}
    	};
    }
	
    public void sendNpcInventoryToUser(User user) {
        // Enviamos el inventario del npc con el cual el user va a comerciar...
        double dto = user.getUserTrade().descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        for (byte i = 1; i <= npcInv().getSize(); i++) {
            if (npcInv().getObject(i).objid > 0) {
                // Calculamos el porc de inflacion del npc
                ObjectInfo info = findObj(npcInv().getObject(i).objid);
                double infla = (this.inflation *  info.Valor) / 100;
                double val = (info.Valor + infla) / dto;
                user.sendPacket(new ChangeNPCInventorySlotResponse( 
                		i, 
                		info.Nombre, 
                		(short) npcInv().getObject(i).cant, 
                		((int) val),
                		info.GrhIndex, 
                		npcInv().getObject(i).objid, 
                		(byte) info.objType.value(),
                		info.MaxHIT, 
                		info.MinHIT, 
                		info.MaxDef));
            } else {
                user.sendPacket(new ChangeNPCInventorySlotResponse(
                		(byte) i, "Nada", (short) 0, 0, (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0));
            }
        }
    }
    
    /** NPC vende a un Usuario */
    public void sellItemToUser(User user, short slotNpc, int amount) {
        if (amount < 1) {
			return;
		}
        // NPC VENDE UN OBJ A UN USUARIO
        user.sendUpdateUserStats();
        // Calculamos el valor unitario
        if (npcInv().getObject(slotNpc).objid == 0) {
			return;
		}
        // Calculamos el porc de inflacion del npc
        ObjectInfo info = findObj(npcInv().getObject(slotNpc).objid);
        double dto = user.getUserTrade().descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        double infla = (this.inflation * info.Valor) / 100;
        double val = (info.Valor + infla) / dto;
        
        if (user.getStats().getGold() < (val * amount)) {
            user.sendMessage("No tienes suficiente oro.", FontType.FONTTYPE_INFO);
            return;
        }
        
        if (npcInv().getObject(slotNpc).cant > 0) {
            if (amount > npcInv().getObject(slotNpc).cant) {
                amount = npcInv().getObject(slotNpc).cant;
            }
            // Agregamos el obj que compro al inventario
    		if (npcInv().getObject(slotNpc).cant <= 0) {
    			return;
    		}
    		short objid = npcInv().getObject(slotNpc).objid;
    		// ¿Ya tiene un objeto de este tipo?
    		int slot_inv = 0;
    		for (short i = 1; i <= user.getUserInv().getSize(); i++) {
    			if (user.getUserInv().getObject(i).objid == objid && (user.getUserInv().getObject(i).cant + amount) <= MAX_INVENTORY_OBJS) {
    				slot_inv = i;
    				break;
    			}
    		}
    		// Sino se fija por un slot vacio
    		if (slot_inv == 0) {
    			slot_inv = user.getUserInv().getEmptySlot();
    			if (slot_inv == 0) {
    				user.sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
    				return;
    			}
    		}
    		// Mete el obj en el slot
    		if (user.getUserInv().getObject(slot_inv).cant + amount <= MAX_INVENTORY_OBJS) {
    			// Menor que MAX_INV_OBJS
    			user.getUserInv().getObject(slot_inv).objid = objid;
    			user.getUserInv().getObject(slot_inv).cant += amount;
    			// Le sustraemos el valor en oro del obj comprado
    			double unidad = ((info.Valor + infla) / dto);
    			int monto = (int) (unidad * amount);
    			user.getStats().addGold( -monto );
    			// tal vez suba el skill comerciar ;-)
    			user.riseSkill(Skill.SKILL_Comerciar);
    			if (info.objType == ObjType.Llaves) {
    				Log.logVentaCasa(user.getUserName() + " compro " + info.Nombre);
    			}
    			removeBuyedItemFromNpcInventory(slotNpc, amount);
    		} else {
    			user.sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
    		}
    	}
        
        // Actualizamos el inventario del usuario
        user.sendInventoryToUser();
        // Actualizamos el oro
        user.sendUpdateUserStats();
        // Actualizamos la ventana de comercio
        short objid = npcInv().getObject(slotNpc).objid;
        sendNpcInventoryToUser(user);
        user.sendPacket(new TradeOKResponse());
        user.getUserTrade().updateVentanaComercio(objid, amount);
    }

    public void buyItemFromUser(User user, short slot, int cant) {
        // NPC COMPRA UN OBJ A UN USUARIO
        user.sendUpdateUserStats();
        if (user.getUserInv().getObject(slot).cant > 0 && !user.getUserInv().getObject(slot).equipado) {
            if (cant > 0 && cant > user.getUserInv().getObject(slot).cant) {
                cant = user.getUserInv().getObject(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            if (cant < 1) {
    			return;
    		}
            short objid = user.getUserInv().getObject(slot).objid;
            ObjectInfo info = findObj(objid);
            if (info.esNewbie()) {
                user.sendMessage("No comercio objetos para newbies.", FontType.FONTTYPE_INFO);
                return;
            }
            if (this.objType != OBJ_INDEX_CUALQUIERA) {
                // ¿Son los items con los que comercia el npc?
                if (this.objType != info.objType.value()) {
                    user.sendMessage("No me interesa comprar ese objeto.", FontType.FONTTYPE_WARNING);
                    return;
                }
            }
            int slot_inv = 0;
            // ¿Ya tiene un objeto de este tipo?
            for (int i = 1; i <= npcInv().getSize(); i++) {
                if (npcInv().getObject(i).objid == objid && npcInv().getObject(i).cant + cant <= MAX_INVENTORY_OBJS) {
                    slot_inv = i;
                    break;
                }
            }
            // Sino se fija por un slot vacio antes del slot devuelto
            if (slot_inv == 0) {
                slot_inv = npcInv().getEmptySlot();
                //If Slot <= MAX_INVENTORY_SLOTS Then Npclist(NpcIndex).Invent.NroItems = Npclist(NpcIndex).Invent.NroItems + 1
            }
            if (slot_inv > 0) { // Slot valido
                // Mete el obj en el slot
                if (npcInv().getObject(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
                    // Menor que MAX_INV_OBJS
                    npcInv().getObject(slot_inv).objid = objid;
                    npcInv().getObject(slot_inv).cant += cant;
                    user.getUserInv().quitarUserInvItem(slot, cant);
                    // Le sumamos al user el valor en oro del obj vendido
                    //double monto = ((info.Valor / 3 + infla) * cant);
                    double monto = ((info.Valor / 3) * cant);
                    user.getStats().addGold((int) monto);
                    // tal vez suba el skill comerciar ;-)
                    user.riseSkill(Skill.SKILL_Comerciar);
                } else {
                    user.sendMessage("No puedo cargar tantos objetos.", FontType.FONTTYPE_INFO);
                }
            } else {
                user.getUserInv().quitarUserInvItem(slot, cant);
                // Le sumamos al user el valor en oro del obj vendido
                //double monto = ((info.Valor / 3 + infla) * cant);
                double monto = ((info.Valor / 3) * cant);
                user.getStats().addGold((int) monto);
            }
            
            
            // Actualizamos el inventario del usuario
            user.sendInventoryToUser();
            // Actualizamos el oro
            user.sendUpdateUserStats();
            sendNpcInventoryToUser(user);
            // Actualizamos la ventana de comercio
        	user.getUserTrade().updateVentanaComercio(objid, cant);
        	user.sendPacket(new TradeOKResponse());
        }
    }
    
    private void removeBuyedItemFromNpcInventory(short slot, int cant) {
        short objid = npcInv().getObject(slot).objid;
        // Quita un Obj
        ObjectInfo info = findObj(objid);
        if (!info.esCrucial()) {
            npcInv().getObject(slot).cant -= cant;
            if (npcInv().getObject(slot).cant <= 0) {
                npcInv().getObject(slot).objid = 0;
                npcInv().getObject(slot).cant = 0;
                if (npcInv().isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    loadInitialInventory(); 
                }
            }
        } else {
            npcInv().getObject(slot).cant -= cant;
            if (npcInv().getObject(slot).cant <= 0) {
                npcInv().getObject(slot).objid = 0;
                npcInv().getObject(slot).cant = 0;
                if (!quedanItems(objid)) {
                	int cantNpcDat = encontrarCant(objid);
                	if (cantNpcDat > 0) {
	                    npcInv().getObject(slot).objid = objid;
						npcInv().getObject(slot).cant = cantNpcDat;
                	}
                }
                if (npcInv().isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    loadInitialInventory(); 
                }
            }
        }
    }

    private void loadInitialInventory() {
        // Vuelve a cargar el inventario del npc NpcIndex
        IniFile ini = this.server.getNpcLoader().getIniFile(this.npcNumber, false);
        loadInventario(ini);
    }

    private boolean quedanItems(short objid) {
        for (int i = 1; i <= MAX_INVENTORY_SLOTS; i++) {
            if (npcInv().getObject(i).objid == objid) {
                return true;
            }
        }
        return false;
    }
     
    private int encontrarCant(short objid) {
    	// FIXME es necesario que lea del archivo?
    	if (DEBUG)
    		System.out.println("+++++DEBUG++++ encontrarCant() " + npcInv().isEmpty());
    	
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
                        int cant_inv = Integer.parseInt(objTmp.substring(sep+1).split("'")[0].trim());
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
