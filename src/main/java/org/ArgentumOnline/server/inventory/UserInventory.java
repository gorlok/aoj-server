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
package org.ArgentumOnline.server.inventory;

import java.util.ArrayList;

import org.ArgentumOnline.server.Clazz;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.net.BlacksmithArmors_DATA;
import org.ArgentumOnline.server.net.BlacksmithWeapons_DATA;
import org.ArgentumOnline.server.net.CarpenterObjects_DATA;
import org.ArgentumOnline.server.protocol.BlacksmithArmorsResponse;
import org.ArgentumOnline.server.protocol.BlacksmithWeaponsResponse;
import org.ArgentumOnline.server.protocol.CarpenterObjectsResponse;
import org.ArgentumOnline.server.protocol.ShowCarpenterFormResponse;
import org.ArgentumOnline.server.protocol.WorkRequestTargetResponse;
import org.ArgentumOnline.server.user.Player;
import org.ArgentumOnline.server.user.UserGender;
import org.ArgentumOnline.server.user.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class UserInventory extends Inventory implements Constants {
	private static Logger log = LogManager.getLogger();
    
    Player player;
    
    boolean armaEquipada = false;
    boolean municionEquipada = false;
    boolean armaduraEquipada = false;
    boolean cascoEquipado = false;
    boolean escudoEquipado = false;
    boolean barcoEquipado = false;
    boolean anilloEquipado = false;

    int armaSlot = 0;
    int municionSlot = 0;
    int armaduraSlot = 0;
    int cascoSlot = 0;
    int escudoSlot = 0;
    int barcoSlot = 0;
    int espadaMataDragonesSlot = 0;
    int anilloSlot = 0;
    
    /** Creates a new instance of UserInventory */
    public UserInventory(GameServer server, Player player, int slots) {
        super(server, slots);
        this.player = player;
    }
    
    private Player player() {
    	return this.player;
    }
    
    public void setAnilloSlot(int anilloSlot) {
		this.anilloSlot = anilloSlot;
		if (this.anilloSlot > 0) {
			this.anilloEquipado = true;
		}
	}
    
    public void setMunicionSlot(int municionSlot) {
		this.municionSlot = municionSlot;
		if (this.municionSlot > 0) {
			this.municionEquipada = true;
		}
	}
    
    public void setBarcoSlot(int barcoSlot) {
		this.barcoSlot = barcoSlot;
		if (this.barcoSlot > 0) {
			this.barcoEquipado = true;
		}
	}
    
    public void setCascoSlot(int cascoSlot) {
		this.cascoSlot = cascoSlot;
		if (this.cascoSlot > 0) {
			this.cascoEquipado = true;
		}
	}
    
    public void setEscudoSlot(int escudoSlot) {
		this.escudoSlot = escudoSlot;
		if (this.escudoSlot > 0) {
			this.escudoEquipado = true;
		}
	}
    
    public void setArmaSlot(int armaSlot) {
		this.armaSlot = armaSlot;
		if (this.armaSlot > 0) {
			this.armaEquipada = true;
		}
	}
    
    public void setArmaduraSlot(int armaduraSlot) {
		this.armaduraSlot = armaduraSlot;
		if (this.armaduraSlot > 0) {
			this.armaduraEquipada = true;
		}
	}
    
    public boolean tieneAnilloEquipado() {
    	return this.anilloEquipado;
    }
    
    public boolean tieneArmaEquipada() {
        return this.armaEquipada;
    }
    
    public boolean tieneEscudoEquipado() {
        return this.escudoEquipado;
    }
    
    public boolean tieneMunicionEquipada() {
        return this.municionEquipada;
    }
    
    public boolean tieneArmaduraEquipada() {
        return this.armaduraEquipada;
    }
    
    public boolean tieneCascoEquipado() {
        return this.cascoEquipado;
    }
    
    public int getAnilloSlot() {
		return anilloSlot;
	}
    public int getArmaSlot() {
        return this.armaSlot;
    }
    public int getMunicionSlot() {
        return this.municionSlot;
    }
    public int getArmaduraSlot() {
        return this.armaduraSlot;
    }
    public int getCascoSlot() {
        return this.cascoSlot;
    }
    public int getEscudoSlot() {
        return this.escudoSlot;
    }
    public int getBarcoSlot() {
        return this.barcoSlot;
    }
    public int getEspadaMataDragonesSlot() {
        return this.espadaMataDragonesSlot;
    }
    
    public void setArma(int slot, InventoryObject obji) {
        setObjeto(slot, obji);
        this.armaSlot = slot;
        this.armaEquipada = obji.equipado;
    }
    
    public void setEscudo(int slot, InventoryObject obji) {
        setObjeto(slot, obji);
        this.escudoSlot = slot;
        this.escudoEquipado = obji.equipado;
    }
    
    public void setArmadura(int slot, InventoryObject obji) {
        setObjeto(slot, obji);
        this.armaduraSlot = slot;
        this.armaduraEquipada = obji.equipado;
    }
    
    public void setAnillo(int slot, InventoryObject obji) {
        setObjeto(slot, obji);
        this.anilloSlot = slot;
        this.anilloEquipado = obji.equipado;
    }
    //FIX BY AGUSH ;-)
    
    public ObjectInfo getAnillo() {
    	if (this.anilloSlot > 0) return findObj(this.objs[this.anilloSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getArma() {
    	if (this.armaSlot > 0) return findObj(this.objs[this.armaSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getEscudo() {
    	if (this.escudoSlot > 0) return findObj(this.objs[this.escudoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getMunicion() {
    	if (this.municionSlot > 0) return findObj(this.objs[this.municionSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getBarco() {
    	if (this.barcoSlot > 0) return findObj(this.objs[this.barcoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getCasco() {
    	if (this.cascoSlot > 0) return findObj(this.objs[this.cascoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getArmadura() {
    	if (this.armaduraSlot > 0) return findObj(this.objs[this.armaduraSlot-1].objid);
    	return null;
    }
    
    public void quitarObjsNewbie() {
        for (int j = 0; j < this.objs.length; j++) {
            if (this.objs[j].objid > 0) {
                ObjectInfo infoObj = findObj(this.objs[j].objid);
                if (infoObj.esNewbie()) {
                    quitarUserInvItem(j+1, this.objs[j].cant);
                    // Actualiza un solo slot del inventario del usuario
                    player().sendInventorySlot(j+1);
                }
            }
        }
    }
    
    public void quitarUserInvItem(int slot, int cant) {
        // Desequipar
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        
        if (this.objs[slot-1].equipado) {
            desequipar(slot);
        }
        
        // Quita un objeto
        this.objs[slot-1].cant -= cant;
        
        // ¿Se terminaron?
        if (this.objs[slot-1].cant <= 0) {
        	// No quedan mas, limpiar este slot
            this.objs[slot-1].cant = 0;
            this.objs[slot-1].objid = 0;
        }
       	player().sendInventorySlot(slot);
    }

    public void dropObj(byte slot, int cant) {
        if (cant > 0) {
            if (cant > this.objs[slot-1].cant) {
                cant = this.objs[slot-1].cant;
            }
            // Check objeto en el suelo
            Map mapa = this.server.getMap(player().pos().map);
            byte x = player().pos().x;
            byte y = player().pos().y;
            short objid = this.objs[slot-1].objid;
            if (!mapa.hasObject(x, y)) {
                if (this.objs[slot-1].equipado) {
					desequipar(slot);
				}
                mapa.agregarObjeto(objid, cant, x, y);
                quitarUserInvItem(slot, cant);
                player().sendInventorySlot(slot);
                ObjectInfo iobj = findObj(objid);
                if (player().flags().isGM()) {
					Log.logGM(player().getNick(), "Tiró la cantidad de " + cant + " unidades del objeto " + iobj.Nombre);
				}
            } else {
                player().sendMessage("No hay espacio en el piso.", FontType.FONTTYPE_INFO);
            }
        }
    }
    
    public void desequipar(int slot) {
        // Desequipar el item slot del inventario
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        if (this.objs[slot-1].objid == 0) {
			return;
		}
        
        ObjectInfo infoObj = findObj(this.objs[slot-1].objid);
        switch (infoObj.objType) {
            case Weapon:
                this.objs[slot-1].equipado = false;
                this.armaSlot = 0;
                this.armaEquipada = false;
                if (!player().flags().Mimetizado) {
                	player().infoChar().weapon = NingunArma;
	                player().sendCharacterChange();
                }
                break;
                
            case Flechas:
                this.objs[slot-1].equipado = false;
                this.municionSlot = 0;
                this.municionEquipada = false;
                break;
                
            case Anillo:
                this.objs[slot-1].equipado = false;
                this.anilloSlot = 0;
                this.anilloEquipado = false;
                break;

            case Armadura:
                this.objs[slot-1].equipado = false;
                this.armaduraSlot = 0;
                this.armaduraEquipada = false;
                player().undress();
                player().sendCharacterChange();
                break;
                
            case CASCO:
                this.objs[slot-1].equipado = false;
                this.cascoSlot = 0;
                this.cascoEquipado = false;
                if (!player().flags().Mimetizado) {
	                player().infoChar().helmet = NingunCasco;
	                player().sendCharacterChange();
                }
                break;
                
            case ESCUDO:
                this.objs[slot-1].equipado = false;
                this.escudoSlot = 0;
                this.escudoEquipado = false;
                if (!player().flags().Mimetizado) {
	                player().infoChar().shield = NingunEscudo;
	                player().sendCharacterChange();
                }
                break;
                
            case Barcos: // FIXME esto todavía va acá?
                this.objs[slot-1].equipado = false;
                this.barcoSlot = 0;
                this.barcoEquipado = false;
                break;
        }

		player().sendUpdateUserStats();
        player().sendInventorySlot(slot);
    }
    
    public void equipar(int slot) {
        // Equipa un item del inventario
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        if (this.objs[slot-1].objid == 0) {
			return;
		}
        ObjectInfo infoObj = findObj(this.objs[slot-1].objid);
        short objid = this.objs[slot-1].objid;
        if (infoObj.esNewbie() && !player().isNewbie()) {
            player().sendMessage("Solo los newbies pueden usar este objeto.", FontType.FONTTYPE_INFO);
            return;
        }
        InventoryObject obj_inv = getObjeto(slot);
        
        switch (infoObj.objType) {
            case Weapon:
                if (infoObj.clasePuedeUsarItem(player().clazz()) && player().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (player().flags().Mimetizado) {
                        	player().mimetizeChar().weapon = NingunArma;
                        } else {
                        	player().infoChar().weapon = NingunArma;
                        	player().sendCharacterChange();
                        }
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneArmaEquipada()) {
                        desequipar(this.armaSlot);
                    }
                    // Equipamos el nuevo
                    this.objs[slot-1].equipado = true;
                    this.armaEquipada = true;
                    this.armaSlot = slot;
                	player().sendWave(SOUND_SACAR_ARMA);
                    
                    if (player().flags().Mimetizado) {
                        player().mimetizeChar().weapon = infoObj.WeaponAnim; 
                    } else {
	                    player().infoChar().weapon = infoObj.WeaponAnim;
	                    player().sendCharacterChange();
                    }
                    
                } else {
                    player().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
                
            case Anillo:
                if (infoObj.clasePuedeUsarItem(player().clazz()) && player().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneAnilloEquipado()) {
                        desequipar(this.anilloSlot);
                    }
                    // Equipamos el nuevo
                    this.objs[slot-1].equipado = true;
                    this.anilloEquipado = true;
                    this.anilloSlot = slot;
                } else {
                    player().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;

            case Flechas:
                if (infoObj.clasePuedeUsarItem(player().clazz()) && player().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneMunicionEquipada()) {
                        desequipar(this.municionSlot);
                    }
                    // Equipamos el nuevo
                    this.objs[slot-1].equipado = true;
                    this.municionEquipada = true;
                    this.municionSlot = slot;
                } else {
                    player().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case Armadura:
                if (player().isSailing()) {
					return;
				}
                // Nos aseguramos que puede usarla
                if (infoObj.clasePuedeUsarItem(player().clazz()) && 
                    player().userFaction().faccionPuedeUsarItem(objid) &&
                    player().genderCanUseItem(objid) &&
                    player().checkRazaUsaRopa(objid)) {
                	
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        player().undress();
                        if (!player().flags().Mimetizado) {
                        	player().sendCharacterChange();
                        }
                        return;
                    }
                    // Quita el anterior
                    if (tieneArmaduraEquipada()) {
                        desequipar(this.armaduraSlot);
                    }
                    // Equipa el nuevo
                    this.objs[slot-1].equipado = true;
                    this.armaduraEquipada = true;
                    this.armaduraSlot = slot;
                    
                    if (player().flags().Mimetizado) {
                    	player().mimetizeChar().body = infoObj.Ropaje;
                    } else {
	                    player().infoChar().body = infoObj.Ropaje;
	                    player().sendCharacterChange();
                    }
                    player().flags().Desnudo = false;
                } else {
                    player().sendMessage("Tu clase, genero o raza no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case CASCO:
                if (player().isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(player().clazz())) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (player().flags().Mimetizado) {
                        	player().mimetizeChar().helmet = NingunCasco;
                        } else {
                            player().infoChar().helmet = NingunCasco;
                        	player().sendCharacterChange();
                        }
                        return;
                    }
                    // Quita el anterior
                    if (tieneCascoEquipado()) {
                        desequipar(this.cascoSlot);
                    }
                    // Lo equipa                    
                    this.objs[slot-1].equipado = true;
                    this.cascoEquipado = true;
                    this.cascoSlot = slot;
                    
                    if (player().flags().Mimetizado) {
                    	player().mimetizeChar().helmet = infoObj.CascoAnim;
                    } else {
	                    player().infoChar().helmet = infoObj.CascoAnim;
	                    player().sendCharacterChange();
                    }
                } else {
                    player().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case ESCUDO:
                if (player().isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(player().clazz()) && 
                		player().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (player().flags().Mimetizado) {
                        	player().mimetizeChar().shield = NingunEscudo;
                        } else {
                            player().infoChar().shield = NingunEscudo;
                        	player().sendCharacterChange();
                        }
                        return;
                    }
                    // Quita el anterior
                    if (tieneEscudoEquipado()) {
                        desequipar(this.escudoSlot);
                    }
                    // Lo equipa
                    this.objs[slot-1].equipado = true;
                    this.escudoEquipado = true;
                    this.escudoSlot = slot;
                    
                    if (player().flags().Mimetizado) {
                    	player().mimetizeChar().shield = infoObj.ShieldAnim;
                    } else {
	                    player().infoChar().shield = infoObj.ShieldAnim;
	                    player().sendCharacterChange();
                    }
                } else {
                    player().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
        }
        // Actualiza el inventario
        player().sendInventoryToUser();
    }
    
    public void desequiparArmadura() {
        if (this.armaduraSlot > 0) {
			desequipar(this.armaduraSlot);
		}
    }
    
    public void desequiparArma() {
        if (this.armaSlot > 0) {
			desequipar(this.armaSlot);
		}
    }
    
    public void desequiparCasco() {
        if (this.cascoSlot > 0) {
			desequipar(this.cascoSlot);
		}
    }
    
    public void desequiparMunicion() {
        if (this.municionSlot > 0) {
			desequipar(this.municionSlot);
		}
    }
    
    /**
     * Agrega items al inventario.
     * @param objid es el indice el objeto a agregar
     * @param cant es la cantidad del objeto a agregar
     * @return cantidad de items agregados
     */
    public int agregarItem(short objid, int cant) {
    	if (cant < 1 || objid < 1) {
			return 0;
		}
    	int agregar = cant;
    	// primero recorremos el inventario buscando slots 
    	// que ya tengan el objeto y tengan lugar libre
    	for (int i = 0; i < this.objs.length; i++) {
    		// En este slot tenemos este objeto y hay lugar?
    		if ((this.objs[i].objid == objid) && !this.objs[i].estaLleno()) {
				int agregados = (agregar > this.objs[i].espacioLibre()) ? this.objs[i].espacioLibre() : agregar;
	            this.objs[i].objid = objid;
	            this.objs[i].cant += agregados;
	            player().sendInventorySlot(i + 1);
				agregar -= agregados; // Descuento lo agregado al total que hay que agregar.
				// Si no hay nada pendiente de agregar, termino la búsqueda.
				if (agregar < 1) {
					break;
				}
    		}
    	}
    	// Si todavía falta agregar objetos, vamos a buscar lugares vacios.
    	if (agregar > 0) {
			for (int i = 0; i < this.objs.length; i++) {
	    		// Este slot está vacio?
	    		if (this.objs[i].estaVacio()) {
    				int agregados = (agregar > MAX_INVENTORY_OBJS) ? MAX_INVENTORY_OBJS : agregar;
		            this.objs[i].objid = objid;
		            this.objs[i].cant  = agregados;
		            player().sendInventorySlot(i + 1);
    				agregar -= agregados; // Descuento lo agregado al total que hay que agregar.
    				// Si no hay nada pendiente de agregar, termino la búsqueda.
    				if (agregar < 1) {
						break;
					}
	    		}
	    	}
		}
		// Si se agregaron todos los items con exito.
    	if (agregar == 0) {
    		return cant; // Devuelvo cuantos items se agregaron, que por suerte son todos :)
    	}
    	// Si no se pudo agregar nada.
    	if (agregar == cant) {
            player().sendMessage("No podes cargar mas objetos.", FontType.FONTTYPE_INFO);
            return 0; // Devuelvo cuantos items se agregaron, que es ninguno.
    	}
    	// Se pudo agregar algo, pero no había suficiente lugar en el inventario para todo.
        player().sendMessage("Solo puedes cargar parte de los objetos.", FontType.FONTTYPE_INFO);
        return cant - agregar; // Devuelvo cuantos items se agregaron, que no son todos.
    }
    
    public boolean tieneObjetosRobables() {
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo infoObj = findObj(element.objid);
                if (infoObj.objType != ObjType.Llaves && infoObj.objType != ObjType.Barcos) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void sendBlacksmithWeapons() {
    	var validWeapons = new ArrayList<BlacksmithWeapons_DATA>();
    	for (short objid : this.server.getArmasHerrero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= player().skillHerreriaEfectivo()) {
            	validWeapons.add(new BlacksmithWeapons_DATA(info.Nombre, info.LingH, info.LingP, info.LingO, objid));
            }
    	}

        player.sendPacket(
        		new BlacksmithWeaponsResponse(
					(short) validWeapons.size(), 
					validWeapons.toArray(new BlacksmithWeapons_DATA[0])));
    }
 
    public void sendCarpenterObjects() {
    	var validObjects = new ArrayList<CarpenterObjects_DATA>();
    	for (short objid : this.server.getObjCarpintero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= player().skillCarpinteriaEfectivo()) {
            	validObjects.add(new CarpenterObjects_DATA(info.Nombre, (short)info.Madera, objid));
            }
    	}

        player.sendPacket(
        		new CarpenterObjectsResponse(
        				(short) validObjects.size(), 
        				validObjects.toArray(new CarpenterObjects_DATA[0])));
    }

    public void sendBlacksmithArmors() {
    	var validArmaduras = new ArrayList<BlacksmithArmors_DATA>();
    	for (short objid : this.server.getArmadurasHerrero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= player().skillHerreriaEfectivo()) {
            	validArmaduras.add(new BlacksmithArmors_DATA(info.Nombre, info.LingH, info.LingP, info.LingO, objid));
            }
    	}

        player.sendPacket(
        		new BlacksmithArmorsResponse(
        				(short) validArmaduras.size(), 
        				validArmaduras.toArray(new BlacksmithArmors_DATA[0])));
    }

    public void dropAllItemsNoNewbies() {
        Map mapa = this.server.getMap(player().pos().map);
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo obj = findObj(element.objid);
                if (obj.itemSeCae() && !obj.esNewbie()) {
                    mapa.tirarItemAlPiso(player().pos().x, player().pos().y, element);
                }
            }
        }
    }

    final static short Manzana = 1;
    final static short ManzanaWihRespawn = 64;
	final static short ManzanaNewbie = 467;

    public void useInvItem(short slot) {
        // Usa un item del inventario
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        InventoryObject objInv = this.objs[slot-1];
        if (objInv.objid == 0) {
			return;
		}
        ObjectInfo infoObjInv = findObj(objInv.objid);
        if (infoObjInv.esNewbie() && !player().isNewbie()) {
            player().sendMessage("Solo los newbies pueden usar estos objetos.", FontType.FONTTYPE_INFO);
            return;
        }
        if (!player().counters().intervaloPermiteUsar()) {
            return;
        }
        player().flags().TargetObjInvIndex = objInv.objid;
        player().flags().TargetObjInvSlot = slot;
        
        Map map = this.server.getMap(player().pos().map);
        switch (infoObjInv.objType) {
            case UseOnce:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                // Usa el item
                player().stats().aumentarHambre(infoObjInv.MinHam);
                player().flags().Hambre = false;
                player().sendUpdateHungerAndThirst();
                // Sonido
                if (objInv.objid == Manzana || objInv.objid == ManzanaNewbie || objInv.objid == ManzanaNewbie) {
                	player().sendWave(SOUND_MORFAR_MANZANA);
                } else {
                	player().sendWave(SOUND_COMIDA);
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                break;
            case Guita:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                player().stats().addGold(objInv.cant);
                player().sendUpdateUserStats();
                quitarUserInvItem(slot, objInv.cant);
                break;
            case Weapon:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                
                if (player().stats().isTooTired()) {
					if (player().gender() == UserGender.GENERO_HOMBRE) {
						player().sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
					} else {
						player().sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
					}
					return;
                }
                
                if (!objInv.equipado) {
                    player().sendMessage("Antes de usar la herramienta deberias equipartela.", FontType.FONTTYPE_INFO);
                    return;
                }
                
                if (infoObjInv.esProyectil()) {
                	if ( ! player().isInCombatMode()) {
                		player().sendMessage("No estás en modo de combate, presiona la tecla \"C\" para pasar al modo combate.", FontType.FONTTYPE_INFO);
                		return;
                	}
                	player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Proyectiles.value()));
                	
                } else {
                	// No es un arma de proyectiles
                	if (player().flags().TargetObj != 0) {
	                	ObjectInfo targetInfo = findObj(player().flags().TargetObj);
	                	// ¿El objetivo es leña?
	                	if (targetInfo != null && targetInfo.objType == ObjType.Leña) {
	                    	// ¿Estoy usando una daga?
	                        if (infoObjInv.ObjIndex == DAGA) {
	                            player().tratarDeHacerFogata();
	                        } else {
	                        	player().sendMessage("Si quieres hacer una fogata, necesitas usar una daga común.", FontType.FONTTYPE_INFO);
	                        }
	                    }
                	}
                }
                
                // Start work with tools
                switch (infoObjInv.ObjIndex) {
                    case OBJ_INDEX_CAÑA:
                    case OBJ_INDEX_RED_PESCA:
                        player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Pesca.value()));
                        break;
                        
                    case OBJ_INDEX_HACHA_LEÑADOR:
                        player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Talar.value()));
                        break;
                        
                    case OBJ_INDEX_PIQUETE_MINERO:
                        player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Mineria.value()));
                        break;
                        
                    case OBJ_INDEX_MARTILLO_HERRERO:
                        player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Herreria.value()));
                        break;
                        
                    case OBJ_INDEX_SERRUCHO_CARPINTERO:
                    	sendCarpenterObjects();
                    	player().sendPacket(new ShowCarpenterFormResponse());
                        break;
                }
                break;
                
            case Pociones:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!player().counters().intervaloPermiteAtacar()) {
                    player().sendMessage("¡¡Debes esperar unos momentos para tomar otra poción!!", FontType.FONTTYPE_INFO);
                    return;
                }
                player().flags().TomoPocion = true;
                player().flags().TipoPocion = infoObjInv.TipoPocion;
                
                switch (player().flags().TipoPocion) {
                	// TODO enum
                    case 1: // Modif la agilidad
                        player().flags().DuracionEfecto = infoObjInv.DuracionEfecto;
                        player().stats().attr().modifyByEffect(Attribute.AGILIDAD, Util.Azar(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        break;
                    case 2: // Modif la fuerza
                        player().flags().DuracionEfecto = infoObjInv.DuracionEfecto;
                        player().stats().attr().modifyByEffect(Attribute.FUERZA, Util.Azar(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        break;
                    case 3: // Pocion roja, restaura HP
                        player().stats().addHP(Util.Azar(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        player().sendUpdateUserStats();
                        break;
                    case 4: // Pocion azul, restaura MANA
                        player().stats().aumentarMana(Util.porcentaje(player().stats().maxMana, 4)
                        		+ ( (player().stats().ELV / 2) + 40 / player().stats().ELV) );
                        player().sendUpdateUserStats();
                        break;
                    case 5: // Pocion violeta
                        if (player().flags().Envenenado) {
                            player().flags().Envenenado = false;
                            player().sendMessage("Te has curado del envenenamiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                    case 6: // Pocion negra
                        if (!player().flags().isGM()) {
                        	player().userDie();
                            player().sendMessage("Sientes un gran mareo y pierdes el conocimiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
            	player().sendWave(SOUND_BEBER);
                player().sendUpdateUserStats();
                break;
                
            case Bebidas:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                player().stats().aumentarSed(infoObjInv.MinSed);
                player().flags().Sed = false;
                player().sendUpdateHungerAndThirst();
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                player().sendWave(SOUND_BEBER);
                break;
                
            case Llaves:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (player().flags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = findObj(player().flags().TargetObj);
                // ¿El objeto cliqueado es una puerta?
                if (targetInfo.objType == ObjType.Puertas) {
                    // ¿Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // ¿Cerrada con llave?
                        byte targetX = player().flags().TargetObjX;
                        byte targetY = player().flags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == infoObjInv.Clave) {
                                map.toggleDoor(map.getObject(targetX, targetY));
                                player().flags().TargetObj = map.getObject(targetX, targetY).obj_ind;
                                player().sendMessage("Has abierto la puerta.", FontType.FONTTYPE_INFO);
                                return;
                            }
                            player().sendMessage("La llave no sirve.", FontType.FONTTYPE_INFO);
                            return;
                        }
                        if (targetInfo.Clave == infoObjInv.Clave) {
                            map.toggleDoor(map.getObject(targetX, targetY));
                            player().flags().TargetObj = map.getObject(targetX, targetY).obj_ind;
                            player().sendMessage("Has cerrado con llave la puerta.", FontType.FONTTYPE_INFO);
                            return;
                        } 
                        player().sendMessage("La llave no sirve.", FontType.FONTTYPE_INFO);
                        return;
                    }
                    player().sendMessage("No esta cerrada.", FontType.FONTTYPE_INFO);
                    return;
                }
                break;
                
            case BotellaVacia:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                Pos lugar = new Pos(player().flags().TargetX, player().flags().TargetY);
                if (!lugar.isValid() || !map.isWater(player().flags().TargetX, player().flags().TargetY)) {
                    player().sendMessage("No hay agua allí.", FontType.FONTTYPE_INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(infoObjInv.IndexAbierta, 1) == 0) {
                    map.tirarItemAlPiso(player().pos().x, player().pos().y, new InventoryObject(infoObjInv.IndexAbierta, 1));
                }
                break;
                
            case BotellaLlena:
                if (!player().isAlive()) {
                    player().sendMessage("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.FONTTYPE_INFO);
                    return;
                }
                player().stats().aumentarSed(infoObjInv.MinSed);
                player().flags().Sed = false;
                player().sendUpdateHungerAndThirst();
                quitarUserInvItem(slot, 1);
                if (agregarItem(infoObjInv.IndexCerrada, 1) == 0) {
                    map.tirarItemAlPiso(player().pos().x, player().pos().y, new InventoryObject(infoObjInv.IndexCerrada, 1));
                }
                break;
                
            case Pergaminos:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!player().flags().Hambre && !player().flags().Sed) {
                    player().agregarHechizo(slot);
                    player().sendInventoryToUser();
                } else {
                    player().sendMessage("Estas demasiado hambriento y sediento.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case Minerales:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
            	player().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_FundirMetal));
               break;
               
            case Instrumentos:
                if (!player().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                
                // ¿Es el Cuerno Real?
                if (infoObjInv.esReal()) {
                	if (player().userFaction().faccionPuedeUsarItem(objInv.objid)) {
                		if (map.isSafeMap()) {
                			player().sendMessage("No hay Peligro aquí. Es Zona Segura.", FontType.FONTTYPE_INFO);
                		}
                		player().sendWave(infoObjInv.Snd1);
                	} else {
                		player().sendMessage("Solo Miembros de la Armada Real pueden usar este cuerno.", FontType.FONTTYPE_INFO);
                	}
                	return;
                }
                
                // ¿Es el Cuerno Real?
                if (infoObjInv.esCaos()) {
                	if (player().userFaction().faccionPuedeUsarItem(objInv.objid)) {
                		if (map.isSafeMap()) {
                			player().sendMessage("No hay Peligro aquí. Es Zona Segura.", FontType.FONTTYPE_INFO);
                		}
                		player().sendWave(infoObjInv.Snd1);
                	} else {
                		player().sendMessage("Solo Miembros de la Legión Oscura pueden usar este cuerno.", FontType.FONTTYPE_INFO);
                	}
                	return;
                }
                
                // Es Laud o Tambor o Flauta
                player().sendWave(infoObjInv.Snd1);
                break;
                
            case Barcos:
            	if (player().stats().ELV < 25) {
            		if (player().clazz() != Clazz.Fisher && player().clazz() != Clazz.Pirate) {
                		player().sendMessage("Para recorrer los mares debes ser nivel 25 o superior.", FontType.FONTTYPE_INFO);
                		return;
            		} else if (player().stats().ELV < 20) {
                		player().sendMessage("Para recorrer los mares debes ser nivel 20 o superior.", FontType.FONTTYPE_INFO);
                		return;
            		}
            	}
            	
                short m = player().pos().map;
                short x = player().pos().x;
                short y = player().pos().y;
                if (((map.isLegalPos(MapPos.mxy(m, (short) (x - 1), y), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, x, (short) (y - 1)), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, (short) (x + 1), y), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, x, (short) (y + 1)), true, false)) 
                		&& !player().flags().Navegando) 
                	|| player().flags().Navegando) {
		                    this.barcoSlot = slot;
		                    player().sailingToggle();
                } else {
            		player().sendMessage("¡Debes aproximarte al agua para usar el barco!", FontType.FONTTYPE_INFO);
                }
                break;
                
            default:
                log.fatal("No se como usar este tipo de objeto: " + infoObjInv.objType);
        }
    }
    
}
