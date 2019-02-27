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

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.protocol.WorkRequestTargetResponse;
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
    
    Player dueño;
    
    boolean armaEquipada = false;
    boolean municionEquipada = false;
    boolean herramientaEquipada = false;
    boolean armaduraEquipada = false;
    boolean cascoEquipado = false;
    boolean escudoEquipado = false;
    boolean barcoEquipado = false;

    int armaSlot = 0;
    int municionSlot = 0;
    int herramientaSlot = 0;
    int armaduraSlot = 0;
    int cascoSlot = 0;
    int escudoSlot = 0;
    int barcoSlot = 0;
    int espadaMataDragonesSlot = 0;
    
    /** Creates a new instance of UserInventory */
    public UserInventory(GameServer server, Player dueño, int slots) {
        super(server, slots);
        this.dueño = dueño;
    }
    
    public void setHerramientaSlot(int herramientaSlot) {
		this.herramientaSlot = herramientaSlot;
		if (this.herramientaSlot > 0) {
			this.herramientaEquipada = true;
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
    
    public boolean tieneArmaEquipada() {
        return this.armaEquipada;
    }
    
    public boolean tieneEscudoEquipado() {
        return this.escudoEquipado;
    }
    
    public boolean tieneMunicionEquipada() {
        return this.municionEquipada;
    }
    
    public boolean tieneHerramientaEquipada() {
        return this.herramientaEquipada;
    }
    
    public boolean tieneArmaduraEquipada() {
        return this.armaduraEquipada;
    }
    
    public boolean tieneCascoEquipado() {
        return this.cascoEquipado;
    }
    
    public int getArmaSlot() {
        return this.armaSlot;
    }
    public int getMunicionSlot() {
        return this.municionSlot;
    }
    public int getHerramientaSlot() {
        return this.herramientaSlot;
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
    
    //FIX BY AGUSH ;-)
    
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
    
    public ObjectInfo getHerramienta() {
    	if (this.herramientaSlot > 0) return findObj(this.objs[this.herramientaSlot-1].objid);
    	return null;
    }
    
    public void quitarObjsNewbie() {
        for (int j = 0; j < this.objs.length; j++) {
            if (this.objs[j].objid > 0) {
                ObjectInfo infoObj = findObj(this.objs[j].objid);
                if (infoObj.esNewbie()) {
                    quitarUserInvItem(j+1, this.objs[j].cant);
                    // Actualiza un solo slot del inventario del usuario
                    this.dueño.enviarObjetoInventario(j+1);
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
       	this.dueño.enviarObjetoInventario(slot);
    }

    public void dropObj(byte slot, int cant) {
        if (cant > 0) {
            if (cant > this.objs[slot-1].cant) {
                cant = this.objs[slot-1].cant;
            }
            // Check objeto en el suelo
            Map mapa = this.server.getMap(this.dueño.pos().map);
            byte x = this.dueño.pos().x;
            byte y = this.dueño.pos().y;
            short objid = this.objs[slot-1].objid;
            if (!mapa.hayObjeto(x, y)) {
                if (this.objs[slot-1].equipado) {
					desequipar(slot);
				}
                mapa.agregarObjeto(objid, cant, x, y);
                quitarUserInvItem(slot, cant);
                this.dueño.enviarObjetoInventario(slot);
                ObjectInfo iobj = findObj(objid);
                if (this.dueño.esGM()) {
					Log.logGM(this.dueño.getNick(), "Tiró la cantidad de " + cant + " unidades del objeto " + iobj.Nombre);
				}
            } else {
                this.dueño.enviarMensaje("No hay espacio en el piso.", FontType.FONTTYPE_INFO);
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
        switch (infoObj.ObjType) {
            case OBJTYPE_WEAPON:
                this.objs[slot-1].equipado = false;
                this.armaSlot = 0;
                this.armaEquipada = false;
                this.dueño.getInfoChar().m_arma = NingunArma;
                this.dueño.sendCharacterChange();
                break;
            case OBJTYPE_FLECHAS:
                this.objs[slot-1].equipado = false;
                this.municionSlot = 0;
                this.municionEquipada = false;
                break;
            case OBJTYPE_HERRAMIENTAS:
                this.objs[slot-1].equipado = false;
                this.herramientaSlot = 0;
                this.herramientaEquipada = false;
                break;
            case OBJTYPE_BARCOS:
                this.objs[slot-1].equipado = false;
                this.barcoSlot = 0;
                this.barcoEquipado = false;
                break;
            case OBJTYPE_ARMOUR:
                switch (infoObj.SubTipo) {
                    case SUBTYPE_ARMADURA:
                        this.objs[slot-1].equipado = false;
                        this.armaduraSlot = 0;
                        this.armaduraEquipada = false;
                        this.dueño.cuerpoDesnudo();
                        this.dueño.sendCharacterChange();
                        break;
                    case SUBTYPE_CASCO:
                        this.objs[slot-1].equipado = false;
                        this.cascoSlot = 0;
                        this.cascoEquipado = false;
                        this.dueño.getInfoChar().m_casco = NingunCasco;
                        this.dueño.sendCharacterChange();
                        break;
                    case SUBTYPE_ESCUDO:
                        this.objs[slot-1].equipado = false;
                        this.escudoSlot = 0;
                        this.escudoEquipado = false;
                        this.dueño.getInfoChar().m_escudo = NingunEscudo;
                        this.dueño.sendCharacterChange();
                        break;
                }
        }
        //dueño.enviarInventario();
        this.dueño.enviarObjetoInventario(slot);
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
        if (infoObj.esNewbie() && !this.dueño.esNewbie()) {
            this.dueño.enviarMensaje("Solo los newbies pueden usar este objeto.", FontType.FONTTYPE_INFO);
            return;
        }
        InventoryObject obj_inv = getObjeto(slot);
        log.debug("objeto: " + infoObj.Nombre + " objtype=" + infoObj.ObjType + " subtipo=" + infoObj.SubTipo);
        log.debug("WeaponAnim: " + infoObj.WeaponAnim);
        log.debug("CascoAnim: " + infoObj.CascoAnim);
        log.debug("ShieldAnim: " + infoObj.ShieldAnim);
        switch (infoObj.ObjType) {
            case OBJTYPE_WEAPON:
                log.debug("es un arma");
                if (infoObj.clasePuedeUsarItem(this.dueño.getClazz()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarSonido(SOUND_SACARARMA);
                    this.dueño.getInfoChar().m_arma = infoObj.WeaponAnim;
                    this.dueño.sendCharacterChange();
                } else {
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case OBJTYPE_HERRAMIENTAS:
                log.debug("es una herramienta");
                if (infoObj.clasePuedeUsarItem(this.dueño.getClazz()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case OBJTYPE_FLECHAS:
                log.debug("son flechas");
                if (infoObj.clasePuedeUsarItem(this.dueño.getClazz()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
            case OBJTYPE_ARMOUR:
                log.debug("es un armour");
                if (this.dueño.estaNavegando()) {
					return;
				}
                switch (infoObj.SubTipo) {
                    case SUBTYPE_ARMADURA: // ARMADURA
                        // Nos aseguramos que puede usarla
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClazz()) && 
                            this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid) &&
                            this.dueño.sexoPuedeUsarItem(objid) &&
                            this.dueño.checkRazaUsaRopa(objid)) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.cuerpoDesnudo();
                                this.dueño.sendCharacterChange();
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
                            this.dueño.getInfoChar().m_cuerpo = infoObj.Ropaje;
                            this.dueño.getFlags().Desnudo = false;
                            this.dueño.sendCharacterChange();
                        } else {
                            this.dueño.enviarMensaje("Tu clase, genero o raza no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                        
                    case SUBTYPE_CASCO:
                        log.debug("es un casco");
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClazz())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.getInfoChar().m_casco = NingunCasco;
                                this.dueño.sendCharacterChange();
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
                            this.dueño.getInfoChar().m_casco = infoObj.CascoAnim;
                            this.dueño.sendCharacterChange();
                        } else {
                            this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                        
                    case SUBTYPE_ESCUDO:
                        log.debug("es un escudo");
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClazz())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.getInfoChar().m_escudo = NingunEscudo;
                                this.dueño.sendCharacterChange();
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
                            this.dueño.getInfoChar().m_escudo = infoObj.ShieldAnim;
                            this.dueño.sendCharacterChange();
                        } else {
                            this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                break;
        }
        // Actualiza
        log.debug("actualizar inventario del cliente");
        this.dueño.enviarInventario();
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
    
    public void desequiparHerramienta() {
        if (this.herramientaSlot > 0) {
			desequipar(this.herramientaSlot);
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
	            this.dueño.enviarObjetoInventario(i + 1);
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
		            this.dueño.enviarObjetoInventario(i + 1);
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
            this.dueño.enviarMensaje("No podes cargar mas objetos.", FontType.FONTTYPE_INFO);
            return 0; // Devuelvo cuantos items se agregaron, que es ninguno.
    	}
    	// Se pudo agregar algo, pero no había suficiente lugar en el inventario para todo.
        this.dueño.enviarMensaje("Solo puedes cargar parte de los objetos.", FontType.FONTTYPE_INFO);
        return cant - agregar; // Devuelvo cuantos items se agregaron, que no son todos.
    }
    
    public boolean tieneObjetosRobables() {
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo infoObj = findObj(element.objid);
                if (infoObj.ObjType != OBJTYPE_LLAVES && infoObj.ObjType != OBJTYPE_BARCOS) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void enviarArmasConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getArmasHerrero().length; i++) {
            ObjectInfo info = findObj(this.server.getArmasHerrero()[i]);
            if (info.SkHerreria <= this.dueño.skillHerreriaEfectivo()) {
                if (info.ObjType == OBJTYPE_WEAPON) {
                	params.add(info.Nombre + " (" + info.MinHIT + "/" + info.MaxHIT + ")");
                	params.add(this.server.getArmasHerrero()[i]);
                } else {
                	params.add(info.Nombre);
                	params.add(this.server.getArmasHerrero()[i]);
                }
            }
        }
      //  this.dueño.enviar(MSG_LAH, params.toArray());
    }
 
    public void enviarObjConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getObjCarpintero().length; i++) {
            ObjectInfo info = findObj(this.server.getObjCarpintero()[i]);
            if (info.SkCarpinteria <= this.dueño.skillCarpinteriaEfectivo()) {
            	params.add(info.Nombre + " (" + info.Madera + ")");
            	params.add(this.server.getObjCarpintero()[i]);
            }
        }
       // this.dueño.enviar(MSG_OBR, params.toArray());
    }

    public void enviarArmadurasConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getArmadurasHerrero().length; i++) {
            ObjectInfo info = findObj(this.server.getArmadurasHerrero()[i]);
            if (info.SkHerreria <= this.dueño.skillHerreriaEfectivo()) {
            	params.add(info.Nombre + " (" + info.MinDef + "/" + info.MaxDef + ")");
            	params.add(this.server.getArmadurasHerrero()[i]);
            }
        }
      //  this.dueño.enviar(MSG_LAR, params.toArray());
    }

    public void tirarTodosLosItemsNoNewbies() {
        Map mapa = this.server.getMap(this.dueño.pos().map);
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo obj = findObj(element.objid);
                if (obj.itemSeCae() && !obj.esNewbie()) {
                    mapa.tirarItemAlPiso(this.dueño.pos().x, this.dueño.pos().y, element);
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
        if (info.esNewbie() && !this.dueño.esNewbie()) {
            this.dueño.enviarMensaje("Solo los newbies pueden usar estos objetos.", FontType.FONTTYPE_INFO);
            return;
        }
        if (!this.dueño.intervaloPermiteUsar()) {
            return;
        }
        this.dueño.getFlags().TargetObjInvIndex = obj.objid;
        this.dueño.getFlags().TargetObjInvSlot = slot;
        Map mapa = this.server.getMap(this.dueño.pos().map);
        switch (info.ObjType) {
            case OBJTYPE_USEONCE:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                // Usa el item
                this.dueño.getEstads().aumentarHambre(info.MinHam);
                this.dueño.getFlags().Hambre = false;
                this.dueño.enviarEstadsHambreSed();
                // Sonido
                this.dueño.enviarSonido(SOUND_COMIDA);
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                break;
            case OBJTYPE_GUITA:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.dueño.getEstads().addGold(obj.cant);
                this.dueño.sendUpdateUserStats();
                quitarUserInvItem(slot, obj.cant);
                break;
            case OBJTYPE_WEAPON:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (info.esProyectil()) {
               //     this.dueño.enviar(MSG_T01, SKILL_Proyectiles); // FIXME: REVISAR MEJOR, NO DEBERIA ATACAR ???
                } else {
                    if (this.dueño.getFlags().TargetObj == 0) {
						return;
					}
                    ObjectInfo targeInfo = findObj(this.dueño.getFlags().TargetObj);
                    // ¿El target-objeto es leña?
                    if (targeInfo.ObjType == OBJTYPE_LEÑA) {
                        if (info.ObjIndex == DAGA) {
                            this.dueño.tratarDeHacerFogata();
                        }
                    }
                }
                break;
            case OBJTYPE_POCIONES:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.dueño.intervaloPermiteAtacar()) {
                    this.dueño.enviarMensaje("¡¡Debes esperar unos momentos para tomar otra poción!!", FontType.FONTTYPE_INFO);
                    return;
                }
                this.dueño.getFlags().TomoPocion = true;
                this.dueño.getFlags().TipoPocion = info.TipoPocion;
                switch (this.dueño.getFlags().TipoPocion) {
                    case 1: // Modif la agilidad
                        this.dueño.getFlags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.dueño.getEstads().aumentarAtributo(ATRIB_AGILIDAD, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 2: // Modif la fuerza
                        this.dueño.getFlags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.dueño.getEstads().aumentarAtributo(ATRIB_FUERZA, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 3: // Pocion roja, restaura HP
                        // Usa el item
                        this.dueño.getEstads().addMinHP(Util.Azar(info.MinModificador, info.MaxModificador));
                        this.dueño.sendUpdateUserStats();
                        break;
                    case 4: // Pocion azul, restaura MANA
                        // Usa el item
                        this.dueño.getEstads().aumentarMana(Util.porcentaje(this.dueño.getEstads().maxMana, 5));
                        this.dueño.sendUpdateUserStats();
                        break;
                    case 5: // Pocion violeta
                        if (this.dueño.getFlags().Envenenado) {
                            this.dueño.getFlags().Envenenado = false;
                            this.dueño.enviarMensaje("Te has curado del envenenamiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.dueño.enviarSonido(SND_BEBER);
                this.dueño.sendUpdateUserStats();
                break;
            case OBJTYPE_BEBIDA:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.dueño.getEstads().aumentarSed(info.MinSed);
                this.dueño.getFlags().Sed = false;
                this.dueño.enviarEstadsHambreSed();
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.dueño.enviarSonido(SND_BEBER);
                break;
            case OBJTYPE_LLAVES:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (this.dueño.getFlags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = findObj(this.dueño.getFlags().TargetObj);
                // ¿El objeto clickeado es una puerta?
                if (targetInfo.ObjType == OBJTYPE_PUERTAS) {
                    // ¿Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // ¿Cerrada con llave?
                        byte targetX = this.dueño.getFlags().TargetObjX;
                        byte targetY = this.dueño.getFlags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == info.Clave) {
                                mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                                this.dueño.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                                this.dueño.enviarMensaje("Has abierto la puerta.", FontType.FONTTYPE_INFO);
                                return;
                            }
                            this.dueño.enviarMensaje("La llave no sirve.", FontType.FONTTYPE_INFO);
                            return;
                        }
                        if (targetInfo.Clave == info.Clave) {
                            mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                            this.dueño.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                            this.dueño.enviarMensaje("Has cerrado con llave la puerta.", FontType.FONTTYPE_INFO);
                            return;
                        } 
                        this.dueño.enviarMensaje("La llave no sirve.", FontType.FONTTYPE_INFO);
                        return;
                    }
                    this.dueño.enviarMensaje("No esta cerrada.", FontType.FONTTYPE_INFO);
                    return;
                }
                break;
            case OBJTYPE_BOTELLAVACIA:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                Pos lugar = new Pos(this.dueño.getFlags().TargetX, this.dueño.getFlags().TargetY);
                if (!lugar.isValid() || !mapa.hayAgua(this.dueño.getFlags().TargetX, this.dueño.getFlags().TargetY)) {
                    this.dueño.enviarMensaje("No hay agua allí.", FontType.FONTTYPE_INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexAbierta, 1) == 0) {
                    mapa.tirarItemAlPiso(this.dueño.pos().x, this.dueño.pos().y, new InventoryObject(info.IndexAbierta, 1));
                }
                break;
            case OBJTYPE_BOTELLALLENA:
                if (!this.dueño.isAlive()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.FONTTYPE_INFO);
                    return;
                }
                this.dueño.getEstads().aumentarSed(info.MinSed);
                this.dueño.getFlags().Sed = false;
                this.dueño.enviarEstadsHambreSed();
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexCerrada, 1) == 0) {
                    mapa.tirarItemAlPiso(this.dueño.pos().x, this.dueño.pos().y, new InventoryObject(info.IndexCerrada, 1));
                }
                break;
            case OBJTYPE_HERRAMIENTAS:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (this.dueño.getEstads().stamina <= 0) {
                    this.dueño.enviarMensaje("Estas muy cansado", FontType.FONTTYPE_INFO);
                    return;
                }
                if (!obj.equipado) {
                    this.dueño.enviarMensaje("Antes de usar la herramienta deberias equipartela.", FontType.FONTTYPE_INFO);
                    return;
                }
                this.dueño.getReputacion().incPlebe(vlProleta);
                switch (info.ObjIndex) {
                    case OBJTYPE_CAÑA:
                    	
                    	break;
                    case OBJTYPE_RED_PESCA:
                        this.dueño.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Pesca));
                        break;
                    case OBJTYPE_HACHA_LEÑADOR:
                        this.dueño.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Talar));
                        break;
                    case OBJTYPE_PIQUETE_MINERO:
                        this.dueño.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Mineria));
                        break;
                    case OBJTYPE_MARTILLO_HERRERO:
                        this.dueño.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Herreria));
                        break;
                    case OBJTYPE_SERRUCHO_CARPINTERO:
                        enviarObjConstruibles();
                      //  this.dueño.enviar(MSG_SFC);
                        break;
                }
                break;
            case OBJTYPE_PERGAMINOS:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.dueño.getFlags().Hambre && !this.dueño.getFlags().Sed) {
                    this.dueño.agregarHechizo(slot);
                    this.dueño.enviarInventario();
                } else {
                    this.dueño.enviarMensaje("Estas demasiado hambriento y sediento.", FontType.FONTTYPE_INFO);
                }
                break;
            case OBJTYPE_MINERALES:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
               //this.dueño.enviar(MSG_T01, SKILL_FundirMetal);
               break;
            case OBJTYPE_INSTRUMENTOS:
                if (!this.dueño.checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.dueño.enviarSonido(info.Snd1);
                break;
            case OBJTYPE_BARCOS:
                short m = this.dueño.pos().map;
                short x = this.dueño.pos().x;
                short y = this.dueño.pos().y;
                if (((mapa.isLegalPos(MapPos.mxy(m, (short) (x - 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y - 1)), true) || 
                mapa.isLegalPos(MapPos.mxy(m, (short) (x + 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y + 1)), true)) &&
                !this.dueño.getFlags().Navegando) || this.dueño.getFlags().Navegando) {
                    this.barcoSlot = slot;
                    this.dueño.doNavega();
                } else {
                    this.dueño.enviarMensaje("¡Debes aproximarte al agua para usar el barco!", FontType.FONTTYPE_INFO);
                }
                break;
            default:
                log.fatal("No se como usar este tipo de objeto: " + info.ObjType);
        }
        // Actualiza
        //this.dueño.refreshStatus();
    }
    
}
