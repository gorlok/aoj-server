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
    
    Player due�o;
    
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
    public UserInventory(GameServer server, Player due�o, int slots) {
        super(server, slots);
        this.due�o = due�o;
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
                if (this.due�o.esGM()) {
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
        switch (infoObj.ObjType) {
            case OBJTYPE_WEAPON:
                this.objs[slot-1].equipado = false;
                this.armaSlot = 0;
                this.armaEquipada = false;
                this.due�o.getInfoChar().m_arma = NingunArma;
                this.due�o.sendCharacterChange();
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
                        this.due�o.cuerpoDesnudo();
                        this.due�o.sendCharacterChange();
                        break;
                    case SUBTYPE_CASCO:
                        this.objs[slot-1].equipado = false;
                        this.cascoSlot = 0;
                        this.cascoEquipado = false;
                        this.due�o.getInfoChar().m_casco = NingunCasco;
                        this.due�o.sendCharacterChange();
                        break;
                    case SUBTYPE_ESCUDO:
                        this.objs[slot-1].equipado = false;
                        this.escudoSlot = 0;
                        this.escudoEquipado = false;
                        this.due�o.getInfoChar().m_escudo = NingunEscudo;
                        this.due�o.sendCharacterChange();
                        break;
                }
        }
        //due�o.enviarInventario();
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
        log.debug("objeto: " + infoObj.Nombre + " objtype=" + infoObj.ObjType + " subtipo=" + infoObj.SubTipo);
        log.debug("WeaponAnim: " + infoObj.WeaponAnim);
        log.debug("CascoAnim: " + infoObj.CascoAnim);
        log.debug("ShieldAnim: " + infoObj.ShieldAnim);
        switch (infoObj.ObjType) {
            case OBJTYPE_WEAPON:
                log.debug("es un arma");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.getFaccion().faccionPuedeUsarItem(this.due�o, objid)) {
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
                    this.due�o.enviarSonido(SOUND_SACARARMA);
                    this.due�o.getInfoChar().m_arma = infoObj.WeaponAnim;
                    this.due�o.sendCharacterChange();
                } else {
                    this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                }
                break;
                
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
                
            case OBJTYPE_FLECHAS:
                log.debug("son flechas");
                if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && this.due�o.getFaccion().faccionPuedeUsarItem(this.due�o, objid)) {
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
                
            case OBJTYPE_ARMOUR:
                log.debug("es un armour");
                if (this.due�o.estaNavegando()) {
					return;
				}
                switch (infoObj.SubTipo) {
                    case SUBTYPE_ARMADURA: // ARMADURA
                        // Nos aseguramos que puede usarla
                        if (infoObj.clasePuedeUsarItem(this.due�o.getClazz()) && 
                            this.due�o.getFaccion().faccionPuedeUsarItem(this.due�o, objid) &&
                            this.due�o.sexoPuedeUsarItem(objid) &&
                            this.due�o.checkRazaUsaRopa(objid)) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.due�o.cuerpoDesnudo();
                                this.due�o.sendCharacterChange();
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
                            this.due�o.getInfoChar().m_cuerpo = infoObj.Ropaje;
                            this.due�o.getFlags().Desnudo = false;
                            this.due�o.sendCharacterChange();
                        } else {
                            this.due�o.enviarMensaje("Tu clase, genero o raza no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                        
                    case SUBTYPE_CASCO:
                        log.debug("es un casco");
                        if (infoObj.clasePuedeUsarItem(this.due�o.getClazz())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.due�o.getInfoChar().m_casco = NingunCasco;
                                this.due�o.sendCharacterChange();
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
                            this.due�o.getInfoChar().m_casco = infoObj.CascoAnim;
                            this.due�o.sendCharacterChange();
                        } else {
                            this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                        
                    case SUBTYPE_ESCUDO:
                        log.debug("es un escudo");
                        if (infoObj.clasePuedeUsarItem(this.due�o.getClazz())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.due�o.getInfoChar().m_escudo = NingunEscudo;
                                this.due�o.sendCharacterChange();
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
                            this.due�o.getInfoChar().m_escudo = infoObj.ShieldAnim;
                            this.due�o.sendCharacterChange();
                        } else {
                            this.due�o.enviarMensaje("Tu clase no puede usar este objeto.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                break;
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
            if (info.SkHerreria <= this.due�o.skillHerreriaEfectivo()) {
                if (info.ObjType == OBJTYPE_WEAPON) {
                	params.add(info.Nombre + " (" + info.MinHIT + "/" + info.MaxHIT + ")");
                	params.add(this.server.getArmasHerrero()[i]);
                } else {
                	params.add(info.Nombre);
                	params.add(this.server.getArmasHerrero()[i]);
                }
            }
        }
      //  this.due�o.enviar(MSG_LAH, params.toArray());
    }
 
    public void enviarObjConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getObjCarpintero().length; i++) {
            ObjectInfo info = findObj(this.server.getObjCarpintero()[i]);
            if (info.SkCarpinteria <= this.due�o.skillCarpinteriaEfectivo()) {
            	params.add(info.Nombre + " (" + info.Madera + ")");
            	params.add(this.server.getObjCarpintero()[i]);
            }
        }
       // this.due�o.enviar(MSG_OBR, params.toArray());
    }

    public void enviarArmadurasConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getArmadurasHerrero().length; i++) {
            ObjectInfo info = findObj(this.server.getArmadurasHerrero()[i]);
            if (info.SkHerreria <= this.due�o.skillHerreriaEfectivo()) {
            	params.add(info.Nombre + " (" + info.MinDef + "/" + info.MaxDef + ")");
            	params.add(this.server.getArmadurasHerrero()[i]);
            }
        }
      //  this.due�o.enviar(MSG_LAR, params.toArray());
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
        if (!this.due�o.intervaloPermiteUsar()) {
            return;
        }
        this.due�o.getFlags().TargetObjInvIndex = obj.objid;
        this.due�o.getFlags().TargetObjInvSlot = slot;
        Map mapa = this.server.getMap(this.due�o.pos().map);
        switch (info.ObjType) {
            case OBJTYPE_USEONCE:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                // Usa el item
                this.due�o.getEstads().aumentarHambre(info.MinHam);
                this.due�o.getFlags().Hambre = false;
                this.due�o.enviarEstadsHambreSed();
                // Sonido
                this.due�o.enviarSonido(SOUND_COMIDA);
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                break;
            case OBJTYPE_GUITA:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.getEstads().addGold(obj.cant);
                this.due�o.sendUpdateUserStats();
                quitarUserInvItem(slot, obj.cant);
                break;
            case OBJTYPE_WEAPON:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (info.esProyectil()) {
               //     this.due�o.enviar(MSG_T01, SKILL_Proyectiles); // FIXME: REVISAR MEJOR, NO DEBERIA ATACAR ???
                } else {
                    if (this.due�o.getFlags().TargetObj == 0) {
						return;
					}
                    ObjectInfo targeInfo = findObj(this.due�o.getFlags().TargetObj);
                    // �El target-objeto es le�a?
                    if (targeInfo.ObjType == OBJTYPE_LE�A) {
                        if (info.ObjIndex == DAGA) {
                            this.due�o.tratarDeHacerFogata();
                        }
                    }
                }
                break;
            case OBJTYPE_POCIONES:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.due�o.intervaloPermiteAtacar()) {
                    this.due�o.enviarMensaje("��Debes esperar unos momentos para tomar otra poci�n!!", FontType.FONTTYPE_INFO);
                    return;
                }
                this.due�o.getFlags().TomoPocion = true;
                this.due�o.getFlags().TipoPocion = info.TipoPocion;
                switch (this.due�o.getFlags().TipoPocion) {
                    case 1: // Modif la agilidad
                        this.due�o.getFlags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.due�o.getEstads().aumentarAtributo(ATRIB_AGILIDAD, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 2: // Modif la fuerza
                        this.due�o.getFlags().DuracionEfecto = info.DuracionEfecto;
                        // Usa el item
                        this.due�o.getEstads().aumentarAtributo(ATRIB_FUERZA, Util.Azar(info.MinModificador, info.MaxModificador));
                        break;
                    case 3: // Pocion roja, restaura HP
                        // Usa el item
                        this.due�o.getEstads().addMinHP(Util.Azar(info.MinModificador, info.MaxModificador));
                        this.due�o.sendUpdateUserStats();
                        break;
                    case 4: // Pocion azul, restaura MANA
                        // Usa el item
                        this.due�o.getEstads().aumentarMana(Util.porcentaje(this.due�o.getEstads().maxMana, 5));
                        this.due�o.sendUpdateUserStats();
                        break;
                    case 5: // Pocion violeta
                        if (this.due�o.getFlags().Envenenado) {
                            this.due�o.getFlags().Envenenado = false;
                            this.due�o.enviarMensaje("Te has curado del envenenamiento.", FontType.FONTTYPE_INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.due�o.enviarSonido(SND_BEBER);
                this.due�o.sendUpdateUserStats();
                break;
            case OBJTYPE_BEBIDA:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.getEstads().aumentarSed(info.MinSed);
                this.due�o.getFlags().Sed = false;
                this.due�o.enviarEstadsHambreSed();
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.due�o.enviarSonido(SND_BEBER);
                break;
            case OBJTYPE_LLAVES:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (this.due�o.getFlags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = findObj(this.due�o.getFlags().TargetObj);
                // �El objeto clickeado es una puerta?
                if (targetInfo.ObjType == OBJTYPE_PUERTAS) {
                    // �Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // �Cerrada con llave?
                        byte targetX = this.due�o.getFlags().TargetObjX;
                        byte targetY = this.due�o.getFlags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == info.Clave) {
                                mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                                this.due�o.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                                this.due�o.enviarMensaje("Has abierto la puerta.", FontType.FONTTYPE_INFO);
                                return;
                            }
                            this.due�o.enviarMensaje("La llave no sirve.", FontType.FONTTYPE_INFO);
                            return;
                        }
                        if (targetInfo.Clave == info.Clave) {
                            mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                            this.due�o.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
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
            case OBJTYPE_BOTELLAVACIA:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                Pos lugar = new Pos(this.due�o.getFlags().TargetX, this.due�o.getFlags().TargetY);
                if (!lugar.isValid() || !mapa.hayAgua(this.due�o.getFlags().TargetX, this.due�o.getFlags().TargetY)) {
                    this.due�o.enviarMensaje("No hay agua all�.", FontType.FONTTYPE_INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexAbierta, 1) == 0) {
                    mapa.tirarItemAlPiso(this.due�o.pos().x, this.due�o.pos().y, new InventoryObject(info.IndexAbierta, 1));
                }
                break;
            case OBJTYPE_BOTELLALLENA:
                if (!this.due�o.isAlive()) {
                    this.due�o.enviarMensaje("��Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.FONTTYPE_INFO);
                    return;
                }
                this.due�o.getEstads().aumentarSed(info.MinSed);
                this.due�o.getFlags().Sed = false;
                this.due�o.enviarEstadsHambreSed();
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexCerrada, 1) == 0) {
                    mapa.tirarItemAlPiso(this.due�o.pos().x, this.due�o.pos().y, new InventoryObject(info.IndexCerrada, 1));
                }
                break;
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
                    case OBJTYPE_CA�A:
                    	
                    	break;
                    case OBJTYPE_RED_PESCA:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Pesca));
                        break;
                    case OBJTYPE_HACHA_LE�ADOR:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Talar));
                        break;
                    case OBJTYPE_PIQUETE_MINERO:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Mineria));
                        break;
                    case OBJTYPE_MARTILLO_HERRERO:
                        this.due�o.sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Herreria));
                        break;
                    case OBJTYPE_SERRUCHO_CARPINTERO:
                        enviarObjConstruibles();
                      //  this.due�o.enviar(MSG_SFC);
                        break;
                }
                break;
            case OBJTYPE_PERGAMINOS:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                if (!this.due�o.getFlags().Hambre && !this.due�o.getFlags().Sed) {
                    this.due�o.agregarHechizo(slot);
                    this.due�o.enviarInventario();
                } else {
                    this.due�o.enviarMensaje("Estas demasiado hambriento y sediento.", FontType.FONTTYPE_INFO);
                }
                break;
            case OBJTYPE_MINERALES:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
               //this.due�o.enviar(MSG_T01, SKILL_FundirMetal);
               break;
            case OBJTYPE_INSTRUMENTOS:
                if (!this.due�o.checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
                    return;
                }
                this.due�o.enviarSonido(info.Snd1);
                break;
            case OBJTYPE_BARCOS:
                short m = this.due�o.pos().map;
                short x = this.due�o.pos().x;
                short y = this.due�o.pos().y;
                if (((mapa.isLegalPos(MapPos.mxy(m, (short) (x - 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y - 1)), true) || 
                mapa.isLegalPos(MapPos.mxy(m, (short) (x + 1), y), true) || 
                mapa.isLegalPos(MapPos.mxy(m, x, (short) (y + 1)), true)) &&
                !this.due�o.getFlags().Navegando) || this.due�o.getFlags().Navegando) {
                    this.barcoSlot = slot;
                    this.due�o.doNavega();
                } else {
                    this.due�o.enviarMensaje("�Debes aproximarte al agua para usar el barco!", FontType.FONTTYPE_INFO);
                }
                break;
            default:
                log.fatal("No se como usar este tipo de objeto: " + info.ObjType);
        }
        // Actualiza
        //this.due�o.refreshStatus();
    }
    
}
