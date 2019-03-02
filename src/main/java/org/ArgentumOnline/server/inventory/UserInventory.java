/**
 * UserInventory.java
 *
 * Created on 29 de septiembre de 2003, 21:44
 * 
    AOJava Server
    Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
    Web site: http://www.aojava.com.ar
    
    This file is part of AOJava.

    AOJava is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AOJava is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 
 */
package org.ArgentumOnline.server.inventory;

import java.util.ArrayList;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.net.BlacksmithArmors_DATA;
import org.ArgentumOnline.server.net.BlacksmithWeapons_DATA;
import org.ArgentumOnline.server.net.CarpenterObjects_DATA;
import org.ArgentumOnline.server.protocol.BlacksmithArmorsResponse;
import org.ArgentumOnline.server.protocol.BlacksmithWeaponsResponse;
import org.ArgentumOnline.server.protocol.CarpenterObjectsResponse;
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
    
    Player due�o;
    
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
    public UserInventory(GameServer server, Player due�o, int slots) {
        super(server, slots);
        this.due�o = due�o;
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
                    this.due�o.enviarObjetoInventario(j+1);
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
        
        // �Se terminaron?
        if (this.objs[slot-1].cant <= 0) {
        	// No quedan mas, limpiar este slot
            this.objs[slot-1].cant = 0;
            this.objs[slot-1].objid = 0;
        }
       	this.due�o.enviarObjetoInventario(slot);
    }

    public void dropObj(byte slot, int cant) {
        if (cant > 0) {
            if (cant > this.objs[slot-1].cant) {
                cant = this.objs[slot-1].cant;
            }
            // Check objeto en el suelo
            Map mapa = this.server.getMap(this.due�o.pos().map);
            byte x = this.due�o.pos().x;
            byte y = this.due�o.pos().y;
            short objid = this.objs[slot-1].objid;
            if (!mapa.hayObjeto(x, y)) {
                if (this.objs[slot-1].equipado) {
					desequipar(slot);
				}
                mapa.agregarObjeto(objid, cant, x, y);
                quitarUserInvItem(slot, cant);
                this.due�o.enviarObjetoInventario(slot);
                ObjectInfo iobj = findObj(objid);
                if (this.due�o.isGM()) {
					Log.logGM(this.due�o.getNick(), "Tir� la cantidad de " + cant + " unidades del objeto " + iobj.Nombre);
				}
            } else {
                this.due�o.enviarMensaje("No hay espacio en el piso.", FontType.FONTTYPE_INFO);
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
                if (!this.due�o.flags().Mimetizado) {
	                this.due�o.infoChar().m_arma = NingunArma;
	                this.due�o.sendCharacterChange();
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
                this.due�o.cuerpoDesnudo();
                this.due�o.sendCharacterChange();
                break;
                
            case CASCO:
                this.objs[slot-1].equipado = false;
                this.cascoSlot = 0;
                this.cascoEquipado = false;
                if (!this.due�o.flags().Mimetizado) {
	                this.due�o.infoChar().m_casco = NingunCasco;
	                this.due�o.sendCharacterChange();
                }
                break;
                
            case ESCUDO:
                this.objs[slot-1].equipado = false;
                this.escudoSlot = 0;
                this.escudoEquipado = false;
                if (!this.due�o.flags().Mimetizado) {
	                this.due�o.infoChar().m_escudo = NingunEscudo;
	                this.due�o.sendCharacterChange();
                }
                break;
                
            case Barcos: // FIXME esto todav�a va ac�?
                this.objs[slot-1].equipado = false;
                this.barcoSlot = 0;
                this.barcoEquipado = false;
                break;
        }

		this.due�o.sendUpdateUserStats();
        this.due�o.enviarObjetoInventario(slot);
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
        log.debug("equipar slot " + slot);
        if (infoObj.esNewbie() && !this.due�o.esNewbie()) {
            this.due�o.enviarMensaje("Solo los newbies pueden usar este objeto.", FontType.FONTTYPE_INFO);
            return;
        }
        InventoryObject obj_inv = getObjeto(slot);
        log.debug("objeto: " + infoObj.Nombre + " objtype=" + infoObj.objType);
        log.debug("WeaponAnim: " + infoObj.WeaponAnim);
        log.debug("CascoAnim: " + infoObj.CascoAnim);
        log.debug("ShieldAnim: " + infoObj.ShieldAnim);
        
        switch (infoObj.objType) {
            case Weapon:
                log.debug("es un arma");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.userFaction().faccionPuedeUsarItem(this.due�o, objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneArmaEquipada()) {
                        desequipar(this.armaSlot);
                    }
                    this.objs[slot-1].equipado = true;
                    this.armaEquipada = true;
                    this.armaSlot = slot;
                    // Sonido
                    if (!this.due�o.flags().AdminInvisible) {
                        // El sonido solo se envia si no lo produce un admin invisible
                    	this.due�o.enviarSonido(SOUND_SACARARMA);
                    }
                    
                    if (this.due�o.flags().Mimetizado) {
                        this.due�o.mimetizeChar().m_arma = infoObj.WeaponAnim; 
                    } else {
	                    this.due�o.infoChar().m_arma = infoObj.WeaponAnim;
	                    this.due�o.sendCharacterChange();
                    }
                    
                    
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
                
            case Anillo:
                log.debug("es un anillo");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.userFaction().faccionPuedeUsarItem(this.due�o, objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneAnilloEquipado()) {
                        desequipar(this.anilloSlot);
                    }
                    this.objs[slot-1].equipado = true;
                    this.anilloEquipado = true;
                    this.anilloSlot = slot;
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;

            case Flechas:
                log.debug("son flechas");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.userFaction().faccionPuedeUsarItem(this.due�o, objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneMunicionEquipada()) {
                        desequipar(this.municionSlot);
                    }
                    this.objs[slot-1].equipado = true;
                    this.municionEquipada = true;
                    this.municionSlot = slot;
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case Armadura:
                log.debug("es una armadura");
                if (this.due�o.isSailing()) {
					return;
				}
                // Nos aseguramos que puede usarla
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && 
                    this.due�o.userFaction().faccionPuedeUsarItem(this.due�o, objid) &&
                    this.due�o.genderCanUseItem(objid) &&
                    this.due�o.checkRazaUsaRopa(objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        this.due�o.cuerpoDesnudo();
                        if (!this.due�o.flags().Mimetizado) {
                        	this.due�o.sendCharacterChange();
                        }
                        return;
                    }
                    // Quita el anterior
                    if (tieneArmaduraEquipada()) {
                        desequipar(this.armaduraSlot);
                    }
                    // Lo equipa
                    this.objs[slot-1].equipado = true;
                    this.armaduraEquipada = true;
                    this.armaduraSlot = slot;
                    
                    if (this.due�o.flags().Mimetizado) {
                    	this.due�o.mimetizeChar().m_cuerpo = infoObj.Ropaje;
                    } else {
	                    this.due�o.infoChar().m_cuerpo = infoObj.Ropaje;
	                    this.due�o.sendCharacterChange();
                    }
                    this.due�o.flags().Desnudo = false;
                } else {
                    this.due�o.enviarMensaje("Tu clase, genero o raza no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case CASCO:
                log.debug("es un casco");
                if (this.due�o.isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz())) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        if (this.due�o.flags().Mimetizado) {
                        	this.due�o.mimetizeChar().m_casco = NingunCasco;
                        } else {
                            this.due�o.infoChar().m_casco = NingunCasco;
                        	this.due�o.sendCharacterChange();
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
                    
                    if (this.due�o.flags().Mimetizado) {
                    	this.due�o.mimetizeChar().m_casco = infoObj.CascoAnim;
                    } else {
	                    this.due�o.infoChar().m_casco = infoObj.CascoAnim;
	                    this.due�o.sendCharacterChange();
                    }
                    
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case ESCUDO:
                log.debug("es un escudo");
                if (this.due�o.isSailing()) {
					return;
				}
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && 
                		this.due�o.userFaction().faccionPuedeUsarItem(this.due�o, objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        if (this.due�o.flags().Mimetizado) {
                        	this.due�o.mimetizeChar().m_escudo = NingunEscudo;
                        } else {
                            this.due�o.infoChar().m_escudo = NingunEscudo;
                        	this.due�o.sendCharacterChange();
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
                    
                    if (this.due�o.flags().Mimetizado) {
                    	this.due�o.mimetizeChar().m_escudo = infoObj.ShieldAnim;
                    } else {
	                    this.due�o.infoChar().m_escudo = infoObj.ShieldAnim;
	                    this.due�o.sendCharacterChange();
                    }
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;

                /* FIXME esto va??
            case OBJTYPE_HERRAMIENTAS:
                log.debug("es una herramienta");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.getFaccion().faccionPuedeUsarItem(this.due�o, objid)) {
                    // Si esta equipado lo quita
                    if (obj_inv.equipado) {
                        // Quitamos del inv el item
                        desequipar(slot);
                        return;
                    }
                    // Quitamos el elemento anterior
                    if (tieneHerramientaEquipada()) {
                        desequipar(this.herramientaSlot);
                    }
                    this.objs[slot-1].equipado = true;
                    this.herramientaEquipada = true;
                    this.herramientaSlot = slot;
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                */
        }
        // Actualiza
        log.debug("actualizar inventario del cliente");
        this.due�o.enviarInventario();
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
	            this.due�o.enviarObjetoInventario(i + 1);
				agregar -= agregados; // Descuento lo agregado al total que hay que agregar.
				// Si no hay nada pendiente de agregar, termino la b�squeda.
				if (agregar < 1) {
					break;
				}
    		}
    	}
    	// Si todav�a falta agregar objetos, vamos a buscar lugares vacios.
    	if (agregar > 0) {
			for (int i = 0; i < this.objs.length; i++) {
	    		// Este slot est� vacio?
	    		if (this.objs[i].estaVacio()) {
    				int agregados = (agregar > MAX_INVENTORY_OBJS) ? MAX_INVENTORY_OBJS : agregar;
		            this.objs[i].objid = objid;
		            this.objs[i].cant  = agregados;
		            this.due�o.enviarObjetoInventario(i + 1);
    				agregar -= agregados; // Descuento lo agregado al total que hay que agregar.
    				// Si no hay nada pendiente de agregar, termino la b�squeda.
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
            this.due�o.enviarMensaje("No podes cargar mas objetos.", FontType.FONTTYPE_INFO);
            return 0; // Devuelvo cuantos items se agregaron, que es ninguno.
    	}
    	// Se pudo agregar algo, pero no hab�a suficiente lugar en el inventario para todo.
        this.due�o.enviarMensaje("Solo puedes cargar parte de los objetos.", FontType.FONTTYPE_INFO);
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
    
    public void enviarArmasConstruibles() {
    	var validWeapons = new ArrayList<BlacksmithWeapons_DATA>();
    	for (short objid : this.server.getArmasHerrero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= this.due�o.skillHerreriaEfectivo()) {
            	validWeapons.add(new BlacksmithWeapons_DATA(info.Nombre, info.LingH, info.LingP, info.LingO, objid));
            }
    	}

        due�o.sendPacket(
        		new BlacksmithWeaponsResponse(
					(short) validWeapons.size(), 
					validWeapons.toArray(new BlacksmithWeapons_DATA[0])));
    }
 
    public void enviarObjConstruibles() {
    	var validObjects = new ArrayList<CarpenterObjects_DATA>();
    	for (short objid : this.server.getObjCarpintero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= this.due�o.skillCarpinteriaEfectivo()) {
            	validObjects.add(new CarpenterObjects_DATA(info.Nombre, (short)info.Madera, objid));
            }
    	}

        due�o.sendPacket(
        		new CarpenterObjectsResponse(
        				(short) validObjects.size(), 
        				validObjects.toArray(new CarpenterObjects_DATA[0])));
    }

    public void enviarArmadurasConstruibles() {
    	var validArmaduras = new ArrayList<BlacksmithArmors_DATA>();
    	for (short objid : this.server.getArmadurasHerrero()) {
            ObjectInfo info = findObj(objid);
            if (info.SkHerreria <= this.due�o.skillHerreriaEfectivo()) {
            	validArmaduras.add(new BlacksmithArmors_DATA(info.Nombre, info.LingH, info.LingP, info.LingO, objid));
            }
    	}

        due�o.sendPacket(
        		new BlacksmithArmorsResponse(
        				(short) validArmaduras.size(), 
        				validArmaduras.toArray(new BlacksmithArmors_DATA[0])));
    }

    public void tirarTodosLosItemsNoNewbies() {
        Map mapa = this.server.getMap(this.due�o.pos().map);
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo obj = findObj(element.objid);
                if (obj.itemSeCae() && !obj.esNewbie()) {
                    mapa.tirarItemAlPiso(this.due�o.pos().x, this.due�o.pos().y, element);
                }
            }
        }
    }

    public void useInvItem(short slot) {
        // Usa un item del inventario
        if (slot < 1 || slot > this.objs.length) {
			return;
		}
        InventoryObject obj = this.objs[slot-1];
        if (obj.objid == 0) {
			return;
		}
        ObjectInfo info = findObj(obj.objid);
        if (info.esNewbie() && !this.due�o.esNewbie()) {
            this.due�o.enviarMensaje("Solo los newbies pueden usar estos objetos.", FontType.FONTTYPE_INFO);
            return;
        }
        if (!this.due�o.counters().intervaloPermiteUsar()) {
            return;
        }
        this.due�o.flags().TargetObjInvIndex = obj.objid;
        this.due�o.flags().TargetObjInvSlot = slot;
        Map mapa = this.server.getMap(this.due�o.pos().map);
        switch (info.objType) {
            case UseOnce:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                // Usa el item
                this.due�o.stats().aumentarHambre(info.MinHam);
                this.due�o.flags().Hambre = false;
                this.due�o.enviarEstadsHambreSed();
                // Sonido
                this.due�o.enviarSonido(SOUND_COMIDA);
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                break;
            case Guita:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.stats().addGold(obj.cant);
                this.due�o.sendUpdateUserStats();
                quitarUserInvItem(slot, obj.cant);
                break;
            case Weapon:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (info.esProyectil()) {
               //     this.due�o.enviar(MSG_T01, SKILL_Proyectiles); // FIXME: REVISAR MEJOR, NO DEBERIA ATACAR ???
                } else {
                    if (this.due�o.flags().TargetObj == 0) {
						return;
					}
                    ObjectInfo targeInfo = findObj(this.due�o.flags().TargetObj);
                    // �El target-objeto es le�a?
                    if (targeInfo.objType == ObjType.Le�a) {
                        if (info.ObjIndex == DAGA) {
                            this.due�o.tratarDeHacerFogata();
                        }
                    }
                }
                break;
            case Pociones:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.due�o.counters().intervaloPermiteAtacar()) {
                    this.due�o.enviarMensaje("��Debes esperar unos momentos para tomar otra poci�n!!", FontType.FONTTYPE_INFO);
                    return;
                }
                this.due�o.flags().TomoPocion = true;
                this.due�o.flags().TipoPocion = info.TipoPocion;
                switch (this.due�o.flags().TipoPocion) {
                    case 1: // Modif la agilidad
                        this.due�o.flags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.due�o.stats().aumentarAtributo(ATRIB_AGILIDAD, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 2: // Modif la fuerza
                        this.due�o.flags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.due�o.stats().aumentarAtributo(ATRIB_FUERZA, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 3: // Pocion roja, restaura HP
                        // Usa el item
                        this.due�o.stats().addMinHP(Util.Azar(info.MinModificador, info.MaxModificador));
                        this.due�o.sendUpdateUserStats();
                        break;
                    case 4: // Pocion azul, restaura MANA
                        // Usa el item
                        this.due�o.stats().aumentarMana(Util.porcentaje(this.due�o.stats().maxMana, 5));
                        this.due�o.sendUpdateUserStats();
                        break;
                    case 5: // Pocion violeta
                        if (this.due�o.flags().Envenenado) {
                            this.due�o.flags().Envenenado = false;
                            this.due�o.enviarMensaje("Te has curado del envenenamiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.due�o.enviarSonido(SND_BEBER);
                this.due�o.sendUpdateUserStats();
                break;
            case Bebidas:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.stats().aumentarSed(info.MinSed);
                this.due�o.flags().Sed = false;
                this.due�o.enviarEstadsHambreSed();
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.due�o.enviarSonido(SND_BEBER);
                break;
            case Llaves:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (this.due�o.flags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = findObj(this.due�o.flags().TargetObj);
                // �El objeto clickeado es una puerta?
                if (targetInfo.objType == ObjType.Puertas) {
                    // �Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // �Cerrada con llave?
                        byte targetX = this.due�o.flags().TargetObjX;
                        byte targetY = this.due�o.flags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == info.Clave) {
                                mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                                this.due�o.flags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                                this.due�o.enviarMensaje("Has abierto la puerta.", FontType.FONTTYPE_INFO);
                                return;
                            }
                            this.due�o.enviarMensaje("La llave no sirve.", FontType.FONTTYPE_INFO);
                            return;
                        }
                        if (targetInfo.Clave == info.Clave) {
                            mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                            this.due�o.flags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                            this.due�o.enviarMensaje("Has cerrado con llave la puerta.", FontType.FONTTYPE_INFO);
                            return;
                        } 
                        this.due�o.enviarMensaje("La llave no sirve.", FontType.FONTTYPE_INFO);
                        return;
                    }
                    this.due�o.enviarMensaje("No esta cerrada.", FontType.FONTTYPE_INFO);
                    return;
                }
                break;
            case BotellaVacia:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                Pos lugar = new Pos(this.due�o.flags().TargetX, this.due�o.flags().TargetY);
                if (!lugar.isValid() || !mapa.hayAgua(this.due�o.flags().TargetX, this.due�o.flags().TargetY)) {
                    this.due�o.enviarMensaje("No hay agua all�.", FontType.FONTTYPE_INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexAbierta, 1) == 0) {
                    mapa.tirarItemAlPiso(this.due�o.pos().x, this.due�o.pos().y, new InventoryObject(info.IndexAbierta, 1));
                }
                break;
            case BotellaLlena:
                if (!this.due�o.isAlive()) {
                    this.due�o.enviarMensaje("��Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.FONTTYPE_INFO);
                    return;
                }
                this.due�o.stats().aumentarSed(info.MinSed);
                this.due�o.flags().Sed = false;
                this.due�o.enviarEstadsHambreSed();
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexCerrada, 1) == 0) {
                    mapa.tirarItemAlPiso(this.due�o.pos().x, this.due�o.pos().y, new InventoryObject(info.IndexCerrada, 1));
                }
                break;
                /* FIXME
            case OBJTYPE_HERRAMIENTAS:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (this.due�o.getEstads().stamina <= 0) {
                    this.due�o.enviarMensaje("Estas muy cansado", FontType.FONTTYPE_INFO);
                    return;
                }
                if (!obj.equipado) {
                    this.due�o.enviarMensaje("Antes de usar la herramienta deberias equipartela.", FontType.FONTTYPE_INFO);
                    return;
                }
                this.due�o.getReputacion().incPlebe(vlProleta);
                switch (info.ObjIndex) {
                    case OBJ_INDEX_CA�A:
                    	
                    	break;
                    case OBJ_INDEX_RED_PESCA:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Pesca));
                        break;
                    case OBJ_INDEX_HACHA_LE�ADOR:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Talar));
                        break;
                    case OBJ_INDEX_PIQUETE_MINERO:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Mineria));
                        break;
                    case OBJ_INDEX_MARTILLO_HERRERO:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Herreria));
                        break;
                    case OBJ_INDEX_SERRUCHO_CARPINTERO:
                        enviarObjConstruibles();
                      //  this.due�o.enviar(MSG_SFC);
                        break;
                }
                break;
                */
            case Pergaminos:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.due�o.flags().Hambre && !this.due�o.flags().Sed) {
                    this.due�o.agregarHechizo(slot);
                    this.due�o.enviarInventario();
                } else {
                    this.due�o.enviarMensaje("Estas demasiado hambriento y sediento.", FontType.FONTTYPE_INFO);
                }
                break;
            case Minerales:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
               //this.due�o.enviar(MSG_T01, SKILL_FundirMetal); FIXME
               break;
            case Instrumentos:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.enviarSonido(info.Snd1);
                break;
            case Barcos:
                short m = this.due�o.pos().map;
                short x = this.due�o.pos().x;
                short y = this.due�o.pos().y;
                if (((mapa.isLegalPos(MapPos.mxy(m, (short) (x - 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y - 1)), true) || 
                mapa.isLegalPos(MapPos.mxy(m, (short) (x + 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y + 1)), true)) &&
                !this.due�o.flags().Navegando) || this.due�o.flags().Navegando) {
                    this.barcoSlot = slot;
                    this.due�o.doNavega();
                } else {
                    this.due�o.enviarMensaje("�Debes aproximarte al agua para usar el barco!", FontType.FONTTYPE_INFO);
                }
                break;
            default:
                log.fatal("No se como usar este tipo de objeto: " + info.objType);
        }
        // Actualiza
        //this.due�o.refreshStatus();
    }
    
}
