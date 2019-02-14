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
package org.ArgentumOnline.server;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.protocol.ServerPacketID;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Pablo F. Lillia
 */
public class UserInventory extends Inventory implements Constants {
	
	private static Logger log = LogManager.getLogger();
    
    Client dueño;
    AojServer server;
    
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
    public UserInventory(AojServer server, Client dueño, int slots) {
        super(slots);
        this.server = server;
        this.dueño = dueño;
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
    	if (this.armaSlot > 0) return this.server.getInfoObjeto(this.objs[this.armaSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getEscudo() {
    	if (this.escudoSlot > 0) return this.server.getInfoObjeto(this.objs[this.escudoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getMunicion() {
    	if (this.municionSlot > 0) return this.server.getInfoObjeto(this.objs[this.municionSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getBarco() {
    	if (this.barcoSlot > 0) return this.server.getInfoObjeto(this.objs[this.barcoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getCasco() {
    	if (this.cascoSlot > 0) return this.server.getInfoObjeto(this.objs[this.cascoSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getArmadura() {
    	if (this.armaduraSlot > 0) return this.server.getInfoObjeto(this.objs[this.armaduraSlot-1].objid);
    	return null;
    }
    
    public ObjectInfo getHerramienta() {
    	if (this.herramientaSlot > 0) return this.server.getInfoObjeto(this.objs[this.herramientaSlot-1].objid);
    	return null;
    }
    
    public void quitarObjsNewbie() {
        for (int j = 0; j < this.objs.length; j++) {
            if (this.objs[j].objid > 0) {
                ObjectInfo infoObj = this.server.getInfoObjeto(this.objs[j].objid);
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
        // ¿Quedan mas?
        if (this.objs[slot-1].cant <= 0) {
            this.objs[slot-1].cant = 0;
            this.objs[slot-1].objid = 0;
            this.dueño.setNullObject(slot);
            //cantItems--;
        } else {
        this.dueño.enviarObjetoInventario(slot);
        }
    }

    public void dropObj(short slot, int cant) {
        // Sub DropObj(ByVal UserIndex As Integer, ByVal Slot As Byte, ByVal num As Integer, ByVal Map As Integer, ByVal x As Integer, ByVal y As Integer)
        if (cant > 0) {
            if (cant > this.objs[slot-1].cant) {
                cant = this.objs[slot-1].cant;
            }
            // Check objeto en el suelo
            Map mapa = this.server.getMapa(this.dueño.getPos().map);
            short x = this.dueño.getPos().x;
            short y = this.dueño.getPos().y;
            short objid = this.objs[slot-1].objid;
            if (!mapa.hayObjeto(x, y)) {
                if (this.objs[slot-1].equipado) {
					desequipar(slot);
				}
                mapa.agregarObjeto(objid, cant, x, y);
                quitarUserInvItem(slot, cant);
                this.dueño.enviarObjetoInventario(slot);
                ObjectInfo iobj = this.server.getInfoObjeto(objid);
                if (this.dueño.esGM()) {
					Log.logGM(this.dueño.getNick(), "Tiró la cantidad de " + cant + " unidades del objeto " + iobj.Nombre);
				}
            } else {
                this.dueño.enviarMensaje("No hay espacio en el piso.", FontType.INFO);
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
        
        ObjectInfo infoObj = this.server.getInfoObjeto(this.objs[slot-1].objid);
        switch (infoObj.ObjType) {
            case OBJTYPE_WEAPON:
                this.objs[slot-1].equipado = false;
                this.armaSlot = 0;
                this.armaEquipada = false;
                this.dueño.getInfoChar().m_arma = NingunArma;
                this.dueño.enviarCP();
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
                        this.dueño.enviarCP();
                        break;
                    case SUBTYPE_CASCO:
                        this.objs[slot-1].equipado = false;
                        this.cascoSlot = 0;
                        this.cascoEquipado = false;
                        this.dueño.getInfoChar().m_casco = NingunCasco;
                        this.dueño.enviarCP();
                        break;
                    case SUBTYPE_ESCUDO:
                        this.objs[slot-1].equipado = false;
                        this.escudoSlot = 0;
                        this.escudoEquipado = false;
                        this.dueño.getInfoChar().m_escudo = NingunEscudo;
                        this.dueño.enviarCP();
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
        ObjectInfo infoObj = this.server.getInfoObjeto(this.objs[slot-1].objid);
        short objid = this.objs[slot-1].objid;
        log.debug("equipar slot " + slot);
        if (infoObj.esNewbie() && !this.dueño.esNewbie()) {
            this.dueño.enviarMensaje("Solo los newbies pueden usar este objeto.", FontType.INFO);
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
                if (infoObj.clasePuedeUsarItem(this.dueño.getClase()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarCP();
                } else {
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.INFO);
                }
                break;
                
            case OBJTYPE_HERRAMIENTAS:
                log.debug("es una herramienta");
                if (infoObj.clasePuedeUsarItem(this.dueño.getClase()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.INFO);
                }
                break;
                
            case OBJTYPE_FLECHAS:
                log.debug("son flechas");
                if (infoObj.clasePuedeUsarItem(this.dueño.getClase()) && this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid)) {
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
                    this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.INFO);
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
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClase()) && 
                            this.dueño.getFaccion().faccionPuedeUsarItem(this.dueño, objid) &&
                            this.dueño.sexoPuedeUsarItem(objid) &&
                            this.dueño.checkRazaUsaRopa(objid)) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.cuerpoDesnudo();
                                this.dueño.enviarCP();
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
                            this.dueño.enviarCP();
                        } else {
                            this.dueño.enviarMensaje("Tu clase, genero o raza no puede usar este objeto.", FontType.INFO);
                        }
                        break;
                        
                    case SUBTYPE_CASCO:
                        log.debug("es un casco");
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClase())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.getInfoChar().m_casco = NingunCasco;
                                this.dueño.enviarCP();
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
                            this.dueño.enviarCP();
                        } else {
                            this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.INFO);
                        }
                        break;
                        
                    case SUBTYPE_ESCUDO:
                        log.debug("es un escudo");
                        if (infoObj.clasePuedeUsarItem(this.dueño.getClase())) {
                            // Si esta equipado lo quita
                            if (obj_inv.equipado) {
                                // Quitamos del inv el item
                                desequipar(slot);
                                this.dueño.getInfoChar().m_escudo = NingunEscudo;
                                this.dueño.enviarCP();
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
                            this.dueño.enviarCP();
                        } else {
                            this.dueño.enviarMensaje("Tu clase no puede usar este objeto.", FontType.INFO);
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
        // Function MeterItemEnInventario(ByVal UserIndex As Integer, ByRef MiObj As Obj) As Boolean
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
            this.dueño.enviarMensaje("No podes cargar mas objetos.", FontType.INFO);
            return 0; // Devuelvo cuantos items se agregaron, que es ninguno.
    	}
    	// Se pudo agregar algo, pero no había suficiente lugar en el inventario para todo.
        this.dueño.enviarMensaje("Solo puedes cargar parte de los objetos.", FontType.INFO);
        return cant - agregar; // Devuelvo cuantos items se agregaron, que no son todos.
    }
    
    public boolean tieneObjetosRobables() {
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo infoObj = this.server.getInfoObjeto(element.objid);
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
            ObjectInfo info = this.server.getInfoObjeto(this.server.getArmasHerrero()[i]);
            if (info.SkHerreria <= this.dueño.m_estads.userSkills[Skill.SKILL_Herreria] / this.dueño.m_clase.modHerreria()) {
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
            ObjectInfo info = this.server.getInfoObjeto(this.server.getObjCarpintero()[i]);
            if (info.SkCarpinteria <= this.dueño.m_estads.userSkills[Skill.SKILL_Carpinteria] / this.dueño.m_clase.modCarpinteria()) {
            	params.add(info.Nombre + " (" + info.Madera + ")");
            	params.add(this.server.getObjCarpintero()[i]);
            }
        }
       // this.dueño.enviar(MSG_OBR, params.toArray());
    }

    public void enviarArmadurasConstruibles() {
        List<Object> params = new LinkedList<Object>();
        for (int i = 0; i < this.server.getArmadurasHerrero().length; i++) {
            ObjectInfo info = this.server.getInfoObjeto(this.server.getArmadurasHerrero()[i]);
            if (info.SkHerreria <= this.dueño.m_estads.userSkills[Skill.SKILL_Herreria] / this.dueño.m_clase.modHerreria()) {
            	params.add(info.Nombre + " (" + info.MinDef + "/" + info.MaxDef + ")");
            	params.add(this.server.getArmadurasHerrero()[i]);
            }
        }
      //  this.dueño.enviar(MSG_LAR, params.toArray());
    }

    public void tirarTodosLosItemsNoNewbies() {
        Map mapa = this.server.getMapa(this.dueño.m_pos.map);
        for (InventoryObject element : this.objs) {
            if (element.objid > 0) {
                ObjectInfo obj = this.server.getInfoObjeto(element.objid);
                if (obj.itemSeCae() && !obj.esNewbie()) {
                    mapa.tirarItemAlPiso(this.dueño.m_pos.x, this.dueño.m_pos.y, element);
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
        ObjectInfo info = this.server.getInfoObjeto(obj.objid);
        if (info.esNewbie() && !this.dueño.esNewbie()) {
            this.dueño.enviarMensaje("Solo los newbies pueden usar estos objetos.", FontType.INFO);
            return;
        }
        if (!this.dueño.intervaloPermiteUsar()) {
            return;
        }
        this.dueño.getFlags().TargetObjInvIndex = obj.objid;
        this.dueño.getFlags().TargetObjInvSlot = slot;
        Map mapa = this.server.getMapa(this.dueño.getPos().map);
        switch (info.ObjType) {
            case OBJTYPE_USEONCE:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
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
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                this.dueño.getEstads().oro += obj.cant;
                this.dueño.refreshStatus(1);
                quitarUserInvItem(slot, obj.cant);
                break;
            case OBJTYPE_WEAPON:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                if (info.esProyectil()) {
               //     this.dueño.enviar(MSG_T01, SKILL_Proyectiles); // FIXME: REVISAR MEJOR, NO DEBERIA ATACAR ???
                } else {
                    if (this.dueño.getFlags().TargetObj == 0) {
						return;
					}
                    ObjectInfo targeInfo = this.server.getInfoObjeto(this.dueño.getFlags().TargetObj);
                    // ¿El target-objeto es leña?
                    if (targeInfo.ObjType == OBJTYPE_LEÑA) {
                        if (info.ObjIndex == DAGA) {
                            this.dueño.tratarDeHacerFogata();
                        }
                    }
                }
                break;
            case OBJTYPE_POCIONES:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                if (!this.dueño.intervaloPermiteAtacar()) {
                    this.dueño.enviarMensaje("¡¡Debes esperar unos momentos para tomar otra poción!!", FontType.INFO);
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
                        this.dueño.refreshStatus(2);
                        break;
                    case 4: // Pocion azul, restaura MANA
                        // Usa el item
                        this.dueño.getEstads().aumentarMana(Util.porcentaje(this.dueño.getEstads().MaxMAN, 5));
                        this.dueño.refreshStatus(3);
                        break;
                    case 5: // Pocion violeta
                        if (this.dueño.getFlags().Envenenado) {
                            this.dueño.getFlags().Envenenado = false;
                            this.dueño.enviarMensaje("Te has curado del envenenamiento.", FontType.INFO);
                        }
                        break;
                }
                // Quitamos del inv el item
                quitarUserInvItem(slot, 1);
                this.dueño.enviarSonido(SND_BEBER);
                //this.dueño.enviarEstadsUsuario();
                break;
            case OBJTYPE_BEBIDA:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
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
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                if (this.dueño.getFlags().TargetObj == 0) {
					return;
				}
                ObjectInfo targetInfo = this.server.getInfoObjeto(this.dueño.getFlags().TargetObj);
                // ¿El objeto clickeado es una puerta?
                if (targetInfo.ObjType == OBJTYPE_PUERTAS) {
                    // ¿Esta cerrada?
                    if (targetInfo.estaCerrada()) {
                        // ¿Cerrada con llave?
                        short targetX = this.dueño.getFlags().TargetObjX;
                        short targetY = this.dueño.getFlags().TargetObjY;
                        if (targetInfo.Llave > 0) {
                            if (targetInfo.Clave == info.Clave) {
                                mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                                this.dueño.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                                this.dueño.enviarMensaje("Has abierto la puerta.", FontType.INFO);
                                return;
                            }
                            this.dueño.enviarMensaje("La llave no sirve.", FontType.INFO);
                            return;
                        }
                        if (targetInfo.Clave == info.Clave) {
                            mapa.abrirCerrarPuerta(mapa.getObjeto(targetX, targetY));
                            this.dueño.getFlags().TargetObj = mapa.getObjeto(targetX, targetY).obj_ind;
                            this.dueño.enviarMensaje("Has cerrado con llave la puerta.", FontType.INFO);
                            return;
                        } 
                        this.dueño.enviarMensaje("La llave no sirve.", FontType.INFO);
                        return;
                    }
                    this.dueño.enviarMensaje("No esta cerrada.", FontType.INFO);
                    return;
                }
                break;
            case OBJTYPE_BOTELLAVACIA:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                MapPos lugar = new MapPos(this.dueño.getFlags().TargetX, this.dueño.getFlags().TargetY);
                if (!lugar.isValid() || !mapa.hayAgua(this.dueño.getFlags().TargetX, this.dueño.getFlags().TargetY)) {
                    this.dueño.enviarMensaje("No hay agua allí.", FontType.INFO);
                    return;
                }
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexAbierta, 1) == 0) {
                    mapa.tirarItemAlPiso(this.dueño.m_pos.x, this.dueño.m_pos.y, new InventoryObject(info.IndexAbierta, 1));
                }
                break;
            case OBJTYPE_BOTELLALLENA:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                this.dueño.getEstads().aumentarSed(info.MinSed);
                this.dueño.getFlags().Sed = false;
                this.dueño.enviarEstadsHambreSed();
                quitarUserInvItem(slot, 1);
                if (agregarItem(info.IndexCerrada, 1) == 0) {
                    mapa.tirarItemAlPiso(this.dueño.m_pos.x, this.dueño.m_pos.y, new InventoryObject(info.IndexCerrada, 1));
                }
                break;
            case OBJTYPE_HERRAMIENTAS:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                if (this.dueño.getEstads().MinSta <= 0) {
                    this.dueño.enviarMensaje("Estas muy cansado", FontType.INFO);
                    return;
                }
                if (!obj.equipado) {
                    this.dueño.enviarMensaje("Antes de usar la herramienta deberias equipartela.", FontType.INFO);
                    return;
                }
                this.dueño.getReputacion().incPlebe(vlProleta);
                switch (info.ObjIndex) {
                    case OBJTYPE_CAÑA:
                    	
                    	break;
                    case OBJTYPE_RED_PESCA:
                        this.dueño.enviar(ServerPacketID.userWork, Skill.SKILL_Pesca);
                        break;
                    case OBJTYPE_HACHA_LEÑADOR:
                        this.dueño.enviar(ServerPacketID.userWork, Skill.SKILL_Talar);
                        break;
                    case OBJTYPE_PIQUETE_MINERO:
                        this.dueño.enviar(ServerPacketID.userWork, Skill.SKILL_Mineria);
                        break;
                    case OBJTYPE_MARTILLO_HERRERO:
                        this.dueño.enviar(ServerPacketID.userWork, Skill.SKILL_Herreria);
                        break;
                    case OBJTYPE_SERRUCHO_CARPINTERO:
                        enviarObjConstruibles();
                      //  this.dueño.enviar(MSG_SFC);
                        break;
                }
                break;
            case OBJTYPE_PERGAMINOS:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                if (!this.dueño.getFlags().Hambre && !this.dueño.getFlags().Sed) {
                    this.dueño.m_spells.agregarHechizo(slot);
                    this.dueño.enviarInventario();
                } else {
                    this.dueño.enviarMensaje("Estas demasiado hambriento y sediento.", FontType.INFO);
                }
                break;
            case OBJTYPE_MINERALES:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
               //this.dueño.enviar(MSG_T01, SKILL_FundirMetal);
               break;
            case OBJTYPE_INSTRUMENTOS:
                if (!this.dueño.estaVivo()) {
                    this.dueño.enviarMensaje("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.", FontType.INFO);
                    return;
                }
                this.dueño.enviarSonido(info.Snd1);
                break;
            case OBJTYPE_BARCOS:
                short m = this.dueño.getPos().map;
                short x = this.dueño.getPos().x;
                short y = this.dueño.getPos().y;
                if (((mapa.isLegalPos(WorldPos.mxy(m, (short) (x - 1), y), true) || 
                mapa.isLegalPos(WorldPos.mxy(m, x, (short) (y - 1)), true) || 
                mapa.isLegalPos(WorldPos.mxy(m, (short) (x + 1), y), true) || 
                mapa.isLegalPos(WorldPos.mxy(m, x, (short) (y + 1)), true)) &&
                !this.dueño.getFlags().Navegando) || this.dueño.getFlags().Navegando) {
                    this.barcoSlot = slot;
                    this.dueño.doNavega();
                } else {
                    this.dueño.enviarMensaje("¡Debes aproximarte al agua para usar el barco!", FontType.INFO);
                }
                break;
            default:
                log.fatal("No se como usar este tipo de objeto: " + info.ObjType);
        }
        // Actualiza
        //this.dueño.enviarEstadsUsuario();
    }
    
}
