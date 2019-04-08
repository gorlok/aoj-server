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
package org.argentumonline.server.inventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Clazz;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjType;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Pos;
import org.argentumonline.server.Skill;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.protocol.ShowCarpenterFormResponse;
import org.argentumonline.server.protocol.WorkRequestTargetResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.user.UserGender;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Log;
import org.argentumonline.server.util.Util;

/**
 * @author gorlok
 */
public class UserInventory extends Inventory implements Constants {
	private static Logger log = LogManager.getLogger();
    
    private User user;
    
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
    public UserInventory(GameServer server, User user, int slots) {
        super(server, slots);
        this.user = user;
    }
    
    private User getUser() {
    	return this.user;
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
        setObject(slot, obji);
        this.armaSlot = slot;
        this.armaEquipada = obji.equipado;
    }
    
    public void setEscudo(int slot, InventoryObject obji) {
        setObject(slot, obji);
        this.escudoSlot = slot;
        this.escudoEquipado = obji.equipado;
    }
    
    public void setArmadura(int slot, InventoryObject obji) {
        setObject(slot, obji);
        this.armaduraSlot = slot;
        this.armaduraEquipada = obji.equipado;
    }
    
    public void setAnillo(int slot, InventoryObject obji) {
        setObject(slot, obji);
        this.anilloSlot = slot;
        this.anilloEquipado = obji.equipado;
    }
    
    public ObjectInfo getAnillo() {
    	if (this.anilloSlot > 0) return findObject(this.objs[this.anilloSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getArma() {
    	if (this.armaSlot > 0) return findObject(this.objs[this.armaSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getEscudo() {
    	if (this.escudoSlot > 0) return findObject(this.objs[this.escudoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getMunicion() {
    	if (this.municionSlot > 0) return findObject(this.objs[this.municionSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getBarco() {
    	if (this.barcoSlot > 0) return findObject(this.objs[this.barcoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getCasco() {
    	if (this.cascoSlot > 0) return findObject(this.objs[this.cascoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getArmadura() {
    	if (this.armaduraSlot > 0) return findObject(this.objs[this.armaduraSlot-1].objid);
    	return null;
    }
    
    public void quitarObjsNewbie() {
        for (int j = 0; j < this.objs.length; j++) {
            if (this.objs[j].objid > 0) {
                ObjectInfo infoObj = findObject(this.objs[j].objid);
                if (infoObj.esNewbie()) {
                    quitarUserInvItem(j+1, this.objs[j].cant);
                    // Actualiza un solo slot del inventario del usuario
                    getUser().sendInventorySlot(j+1);
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
       	getUser().sendInventorySlot(slot);
    }

    public void dropObj(byte slot, int cant) {
        if (cant > 0) {
            if (cant > this.objs[slot-1].cant) {
                cant = this.objs[slot-1].cant;
            }
            // Check objeto en el suelo
            Map mapa = this.server.getMap(getUser().pos().map);
            byte x = getUser().pos().x;
            byte y = getUser().pos().y;
            short objid = this.objs[slot-1].objid;
            if (!mapa.hasObject(x, y)) {
                if (this.objs[slot-1].equipado) {
					desequipar(slot);
				}
                mapa.addObject(objid, cant, x, y);
                quitarUserInvItem(slot, cant);
                getUser().sendInventorySlot(slot);
                ObjectInfo iobj = findObject(objid);
                if (getUser().getFlags().isGM()) {
					Log.logGM(getUser().getUserName(), "Tiró la cantidad de " + cant + " unidades del objeto " + iobj.Nombre);
				}
            } else {
                getUser().sendMessage("No hay espacio en el piso.", FontType.FONTTYPE_INFO);
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
        
        ObjectInfo infoObj = findObject(this.objs[slot-1].objid);
        switch (infoObj.objType) {
            case Weapon:
                this.objs[slot-1].equipado = false;
                this.armaSlot = 0;
                this.armaEquipada = false;
                if (!getUser().getFlags().Mimetizado) {
                	getUser().infoChar().weapon = NingunArma;
	                getUser().sendCharacterChange();
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
                getUser().undress();
                getUser().sendCharacterChange();
                break;
                
            case CASCO:
                this.objs[slot-1].equipado = false;
                this.cascoSlot = 0;
                this.cascoEquipado = false;
                if (!getUser().getFlags().Mimetizado) {
	                getUser().infoChar().helmet = NingunCasco;
	                getUser().sendCharacterChange();
                }
                break;
                
            case ESCUDO:
                this.objs[slot-1].equipado = false;
                this.escudoSlot = 0;
                this.escudoEquipado = false;
                if (!getUser().getFlags().Mimetizado) {
	                getUser().infoChar().shield = NingunEscudo;
	                getUser().sendCharacterChange();
                }
                break;
                
            case Barcos: // FIXME esto todavía va acá?
                this.objs[slot-1].equipado = false;
                this.barcoSlot = 0;
                this.barcoEquipado = false;
                break;
                
            default:
            	break;
        }

		getUser().sendUpdateUserStats();
        getUser().sendInventorySlot(slot);
    }
    
    public void equipar(int slot) {
        // Equipa un item del inventario
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        if (this.objs[slot-1].objid == 0) {
			return;
		}
        ObjectInfo infoObj = findObject(this.objs[slot-1].objid);
        short objid = this.objs[slot-1].objid;
        if (infoObj.esNewbie() && !getUser().isNewbie()) {
            getUser().sendMessage("Solo los newbies pueden usar este objeto.", FontType.FONTTYPE_INFO);
            return;
        }
        InventoryObject obj_inv = getObject(slot);
        
        switch (infoObj.objType) {
            case Weapon:
                if (infoObj.clasePuedeUsarItem(getUser().clazz()) && getUser().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (getUser().getFlags().Mimetizado) {
                        	getUser().mimetizeChar().weapon = NingunArma;
                        } else {
                        	getUser().infoChar().weapon = NingunArma;
                        	getUser().sendCharacterChange();
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
                	getUser().sendWave(SOUND_SACAR_ARMA);
                    
                    if (getUser().getFlags().Mimetizado) {
                        getUser().mimetizeChar().weapon = infoObj.WeaponAnim; 
                    } else {
	                    getUser().infoChar().weapon = infoObj.WeaponAnim;
	                    getUser().sendCharacterChange();
                    }
                    
                } else {
                    getUser().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
                
            case Anillo:
                if (infoObj.clasePuedeUsarItem(getUser().clazz()) && getUser().userFaction().faccionPuedeUsarItem(objid)) {
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
                    getUser().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;

            case Flechas:
                if (infoObj.clasePuedeUsarItem(getUser().clazz()) && getUser().userFaction().faccionPuedeUsarItem(objid)) {
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
                    getUser().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case Armadura:
                if (getUser().isSailing()) {
					return;
				}
                // Nos aseguramos que puede usarla
                if (infoObj.clasePuedeUsarItem(getUser().clazz()) && 
                    getUser().userFaction().faccionPuedeUsarItem(objid) &&
                    getUser().genderCanUseItem(objid) &&
                    getUser().checkRazaUsaRopa(objid)) {
                	
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        getUser().undress();
                        if (!getUser().getFlags().Mimetizado) {
                        	getUser().sendCharacterChange();
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
                    
                    if (getUser().getFlags().Mimetizado) {
                    	getUser().mimetizeChar().body = infoObj.Ropaje;
                    } else {
	                    getUser().infoChar().body = infoObj.Ropaje;
	                    getUser().sendCharacterChange();
                    }
                    getUser().getFlags().Desnudo = false;
                } else {
                    getUser().sendMessage("Tu clase, genero o raza no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case CASCO:
                if (getUser().isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(getUser().clazz())) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (getUser().getFlags().Mimetizado) {
                        	getUser().mimetizeChar().helmet = NingunCasco;
                        } else {
                            getUser().infoChar().helmet = NingunCasco;
                        	getUser().sendCharacterChange();
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
                    
                    if (getUser().getFlags().Mimetizado) {
                    	getUser().mimetizeChar().helmet = infoObj.CascoAnim;
                    } else {
	                    getUser().infoChar().helmet = infoObj.CascoAnim;
	                    getUser().sendCharacterChange();
                    }
                } else {
                    getUser().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case ESCUDO:
                if (getUser().isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(getUser().clazz()) && 
                		getUser().userFaction().faccionPuedeUsarItem(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        desequipar(slot);
                        if (getUser().getFlags().Mimetizado) {
                        	getUser().mimetizeChar().shield = NingunEscudo;
                        } else {
                            getUser().infoChar().shield = NingunEscudo;
                        	getUser().sendCharacterChange();
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
                    
                    if (getUser().getFlags().Mimetizado) {
                    	getUser().mimetizeChar().shield = infoObj.ShieldAnim;
                    } else {
	                    getUser().infoChar().shield = infoObj.ShieldAnim;
	                    getUser().sendCharacterChange();
                    }
                } else {
                    getUser().sendMessage("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            default:
            	break;
        }
        // Actualiza el inventario
        getUser().sendInventoryToUser();
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
	            getUser().sendInventorySlot(i + 1);
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
		            getUser().sendInventorySlot(i + 1);
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
            getUser().sendMessage("No podes cargar mas objetos.", FontType.FONTTYPE_INFO);
            return 0; // Devuelvo cuantos items se agregaron, que es ninguno.
    	}
    	// Se pudo agregar algo, pero no había suficiente lugar en el inventario para todo.
        getUser().sendMessage("Solo puedes cargar parte de los objetos.", FontType.FONTTYPE_INFO);
        return cant - agregar; // Devuelvo cuantos items se agregaron, que no son todos.
    }
    
    public boolean tieneObjetosRobables() {
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo infoObj = findObject(element.objid);
                if (infoObj.objType != ObjType.Llaves && infoObj.objType != ObjType.Barcos) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void dropAllItemsNoNewbies() {
        Map mapa = this.server.getMap(getUser().pos().map);
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo obj = findObject(element.objid);
                if (obj.itemSeCae() && !obj.esNewbie()) {
                    mapa.dropItemOnFloor(getUser().pos().x, getUser().pos().y, element);
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
        ObjectInfo infoObjInv = findObject(objInv.objid);
        if (infoObjInv.esNewbie() && !getUser().isNewbie()) {
            getUser().sendMessage("Solo los newbies pueden usar estos objetos.", FontType.FONTTYPE_INFO);
            return;
        }
        if (!getUser().getCounters().intervaloPermiteUsar()) {
            return;
        }
        getUser().getFlags().TargetObjInvIndex = objInv.objid;
        getUser().getFlags().TargetObjInvSlot = slot;
        
        Map map = this.server.getMap(getUser().pos().map);
        switch (infoObjInv.objType) {
            case UseOnce:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                // Usa el item
                getUser().getStats().aumentarHambre(infoObjInv.MinHam);
                getUser().getFlags().Hambre = false;
                getUser().sendUpdateHungerAndThirst();
                // Sonido
                if (objInv.objid == Manzana || objInv.objid == ManzanaNewbie || objInv.objid == ManzanaNewbie) {
                	getUser().sendWave(SOUND_MORFAR_MANZANA);
                } else {
                	getUser().sendWave(SOUND_COMIDA);
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                break;
            case Guita:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                getUser().getStats().addGold(objInv.cant);
                getUser().sendUpdateUserStats();
                quitarUserInvItem(slot, objInv.cant);
                break;
            case Weapon:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                
                if (getUser().getStats().isTooTired()) {
					if (getUser().gender() == UserGender.GENERO_MAN) {
						getUser().sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
					} else {
						getUser().sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
					}
					return;
                }
                
                if (!objInv.equipado) {
                    getUser().sendMessage("Antes de usar la herramienta deberias equipartela.", FontType.FONTTYPE_INFO);
                    return;
                }
                
                if (infoObjInv.esProyectil()) {
                	if ( ! getUser().isInCombatMode()) {
                		getUser().sendMessage("No estás en modo de combate, presiona la tecla \"C\" para pasar al modo combate.", FontType.FONTTYPE_INFO);
                		return;
                	}
                	getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Proyectiles.value()));
                	
                } else {
                	// No es un arma de proyectiles
                	if (getUser().getFlags().TargetObj != 0) {
	                	ObjectInfo targetInfo = findObject(getUser().getFlags().TargetObj);
	                	// ¿El objetivo es leña?
	                	if (targetInfo != null && targetInfo.objType == ObjType.Leña) {
	                    	// ¿Estoy usando una daga?
	                        if (infoObjInv.ObjIndex == DAGA) {
	                            getUser().tratarDeHacerFogata();
	                        } else {
	                        	getUser().sendMessage("Si quieres hacer una fogata, necesitas usar una daga común.", FontType.FONTTYPE_INFO);
	                        }
	                    }
                	}
                }
                
                // Start work with tools
                switch (infoObjInv.ObjIndex) {
                    case OBJ_INDEX_CAÑA:
                    case OBJ_INDEX_RED_PESCA:
                        getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Pesca.value()));
                        break;
                        
                    case OBJ_INDEX_HACHA_LEÑADOR:
                        getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Talar.value()));
                        break;
                        
                    case OBJ_INDEX_PIQUETE_MINERO:
                        getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Mineria.value()));
                        break;
                        
                    case OBJ_INDEX_MARTILLO_HERRERO:
                        getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Herreria.value()));
                        break;
                        
                    case OBJ_INDEX_SERRUCHO_CARPINTERO:
                    	server.getWork().sendCarpenterObjects(getUser());
                    	getUser().sendPacket(new ShowCarpenterFormResponse());
                        break;
                }
                break;
                
            case Pociones:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!getUser().getCounters().intervaloPermiteAtacar()) {
                    getUser().sendMessage("¡¡Debes esperar unos momentos para tomar otra poción!!", FontType.FONTTYPE_INFO);
                    return;
                }
                getUser().getFlags().TomoPocion = true;
                getUser().getFlags().TipoPocion = infoObjInv.TipoPocion;
                
                switch (getUser().getFlags().TipoPocion) {
                	// TODO enum
                    case 1: // Modif la agilidad
                        getUser().getFlags().DuracionEfecto = infoObjInv.DuracionEfecto;
                        getUser().getStats().attr().modifyByEffect(Attribute.AGILITY, Util.random(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        break;
                    case 2: // Modif la fuerza
                        getUser().getFlags().DuracionEfecto = infoObjInv.DuracionEfecto;
                        getUser().getStats().attr().modifyByEffect(Attribute.STRENGTH, Util.random(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        break;
                    case 3: // Pocion roja, restaura HP
                        getUser().getStats().addHP(Util.random(infoObjInv.MinModificador, infoObjInv.MaxModificador));
                        getUser().sendUpdateUserStats();
                        break;
                    case 4: // Pocion azul, restaura MANA
                        getUser().getStats().aumentarMana(Util.percentage(getUser().getStats().maxMana, 4)
                        		+ ( (getUser().getStats().ELV / 2) + 40 / getUser().getStats().ELV) );
                        getUser().sendUpdateUserStats();
                        break;
                    case 5: // Pocion violeta
                        if (getUser().getFlags().Envenenado) {
                            getUser().getFlags().Envenenado = false;
                            getUser().sendMessage("Te has curado del envenenamiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                    case 6: // Pocion negra
                        if (!getUser().getFlags().isGM()) {
                        	getUser().userDie();
                            getUser().sendMessage("Sientes un gran mareo y pierdes el conocimiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
            	getUser().sendWave(SOUND_BEBER);
                getUser().sendUpdateUserStats();
                break;
                
            case Bebidas:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                getUser().getStats().aumentarSed(infoObjInv.MinSed);
                getUser().getFlags().Sed = false;
                getUser().sendUpdateHungerAndThirst();
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                getUser().sendWave(SOUND_BEBER);
                break;
                
            case Llaves:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (getUser().getFlags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = findObject(getUser().getFlags().TargetObj);
                // ¿El objeto cliqueado es una puerta?
                if (targetInfo.objType == ObjType.Puertas) {
                    // ¿Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // ¿Cerrada con llave?
                        byte targetX = getUser().getFlags().TargetObjX;
                        byte targetY = getUser().getFlags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == infoObjInv.Clave) {
                                map.toggleDoor(map.getObject(targetX, targetY));
                                getUser().getFlags().TargetObj = map.getObject(targetX, targetY).obj_ind;
                                getUser().sendMessage("Has abierto la puerta.", FontType.FONTTYPE_INFO);
                                return;
                            }
                            getUser().sendMessage("La llave no sirve.", FontType.FONTTYPE_INFO);
                            return;
                        }
                        if (targetInfo.Clave == infoObjInv.Clave) {
                            map.toggleDoor(map.getObject(targetX, targetY));
                            getUser().getFlags().TargetObj = map.getObject(targetX, targetY).obj_ind;
                            getUser().sendMessage("Has cerrado con llave la puerta.", FontType.FONTTYPE_INFO);
                            return;
                        } 
                        getUser().sendMessage("La llave no sirve.", FontType.FONTTYPE_INFO);
                        return;
                    }
                    getUser().sendMessage("No esta cerrada.", FontType.FONTTYPE_INFO);
                    return;
                }
                break;
                
            case BotellaVacia:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                Pos lugar = new Pos(getUser().getFlags().TargetX, getUser().getFlags().TargetY);
                if (!lugar.isValid() || !map.isWater(getUser().getFlags().TargetX, getUser().getFlags().TargetY)) {
                    getUser().sendMessage("No hay agua allí.", FontType.FONTTYPE_INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(infoObjInv.IndexAbierta, 1) == 0) {
                    map.dropItemOnFloor(getUser().pos().x, getUser().pos().y, new InventoryObject(infoObjInv.IndexAbierta, 1));
                }
                break;
                
            case BotellaLlena:
                if (!getUser().isAlive()) {
                    getUser().sendMessage("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.FONTTYPE_INFO);
                    return;
                }
                getUser().getStats().aumentarSed(infoObjInv.MinSed);
                getUser().getFlags().Sed = false;
                getUser().sendUpdateHungerAndThirst();
                quitarUserInvItem(slot, 1);
                if (agregarItem(infoObjInv.IndexCerrada, 1) == 0) {
                    map.dropItemOnFloor(getUser().pos().x, getUser().pos().y, new InventoryObject(infoObjInv.IndexCerrada, 1));
                }
                break;
                
            case Pergaminos:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!getUser().getFlags().Hambre && !getUser().getFlags().Sed) {
                    getUser().agregarHechizo(slot);
                    getUser().sendInventoryToUser();
                } else {
                    getUser().sendMessage("Estas demasiado hambriento y sediento.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case Minerales:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
            	getUser().sendPacket(new WorkRequestTargetResponse(Skill.SKILL_FundirMetal));
               break;
               
            case Instrumentos:
                if (!getUser().checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                
                // ¿Es el Cuerno Real?
                if (infoObjInv.esReal()) {
                	if (getUser().userFaction().faccionPuedeUsarItem(objInv.objid)) {
                		if (map.isSafeMap()) {
                			getUser().sendMessage("No hay Peligro aquí. Es Zona Segura.", FontType.FONTTYPE_INFO);
                		}
                		getUser().sendWave(infoObjInv.Snd1);
                	} else {
                		getUser().sendMessage("Solo Miembros de la Armada Real pueden usar este cuerno.", FontType.FONTTYPE_INFO);
                	}
                	return;
                }
                
                // ¿Es el Cuerno Real?
                if (infoObjInv.esCaos()) {
                	if (getUser().userFaction().faccionPuedeUsarItem(objInv.objid)) {
                		if (map.isSafeMap()) {
                			getUser().sendMessage("No hay Peligro aquí. Es Zona Segura.", FontType.FONTTYPE_INFO);
                		}
                		getUser().sendWave(infoObjInv.Snd1);
                	} else {
                		getUser().sendMessage("Solo Miembros de la Legión Oscura pueden usar este cuerno.", FontType.FONTTYPE_INFO);
                	}
                	return;
                }
                
                // Es Laud o Tambor o Flauta
                getUser().sendWave(infoObjInv.Snd1);
                break;
                
            case Barcos:
            	if (getUser().getStats().ELV < 25) {
            		if (getUser().clazz() != Clazz.Fisher && getUser().clazz() != Clazz.Pirate) {
                		getUser().sendMessage("Para recorrer los mares debes ser nivel 25 o superior.", FontType.FONTTYPE_INFO);
                		return;
            		} else if (getUser().getStats().ELV < 20) {
                		getUser().sendMessage("Para recorrer los mares debes ser nivel 20 o superior.", FontType.FONTTYPE_INFO);
                		return;
            		}
            	}
            	
                short m = getUser().pos().map;
                short x = getUser().pos().x;
                short y = getUser().pos().y;
                if (((map.isLegalPos(MapPos.mxy(m, (short) (x - 1), y), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, x, (short) (y - 1)), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, (short) (x + 1), y), true, false) 
                		|| map.isLegalPos(MapPos.mxy(m, x, (short) (y + 1)), true, false)) 
                		&& !getUser().getFlags().Navegando) 
                	|| getUser().getFlags().Navegando) {
		                    this.barcoSlot = slot;
		                    getUser().sailingToggle();
                } else {
            		getUser().sendMessage("¡Debes aproximarte al agua para usar el barco!", FontType.FONTTYPE_INFO);
                }
                break;
                
            default:
                log.fatal("No se como usar este tipo de objeto: " + infoObjInv.objType);
        }
    }
    
}
