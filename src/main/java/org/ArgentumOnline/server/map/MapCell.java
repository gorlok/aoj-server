/**
 * Bloque.java
 *
 * Created on 23 de febrero de 2004, 21:18
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
package org.ArgentumOnline.server.map;

import java.util.BitSet;

import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Npc;

/**
 * @author gorlok
 */
public class MapCell {
    // del archivo .map    
    
    final static int FLAG_BLOQUED = 0;
    final static int FLAG_MODIFIED = 1;
    final static int FLAG_WATER = 2;
    final static int FLAGS_NUMBER = 3;

    // TRIGGERS
    public final static int TRIGGER_NADA = 0;
    public final static int TRIGGER_BAJO_TECHO = 1;
    // Los NPCs no pueden hacer respawn y,
    // el layer cuatro desaparece cuando un jugador pisa el tile:
    public final static int TRIGGER_NO_RESPAWN = 2; 
    // Los npcs no pueden pisar tiles con este trigger:
    public final static int TRIGGER_POS_INVALIDA = 3;
    // No se puede robar o pelear desde este trigger:
    public final static int TRIGGER_ZONA_SEGURA = 4;
    // Te encarcelan si estas mucho tiempo sobre este trigger:
    public final static int TRIGGER_ANTI_PIQUETE = 5;
    // Arena para duelos (??? - revisar)
    // Al pelear en este trigger no se caen las cosas y no cambia el estado de ciuda o crimi
    public final static int TRIGGER_ARENA_DUELOS = 6; // ???
    // Para torneos con espectadores:
    public final static int TRIGGER_ARENA_TORNEO = 7;

    public final static int TRIGGER6_PERMITE = 1;
    public final static int TRIGGER6_PROHIBE = 2;
    public final static int TRIGGER6_AUSENTE = 3;
    
    
    
    private BitSet m_flags = new BitSet(FLAGS_NUMBER);
    
    int borderArea = 0; //Areas by jao
    
    byte m_trigger = 0;
    
    private byte m_x;
    private byte m_y;
    
    // del archivo .inf
    //private WorldPos m_dest = null;
    private short m_dest_mapa;
    private short m_dest_x;
    private short m_dest_y;
    
    // Npc que esta en este bloque.
    private short m_npc;
    
    // Indice del Objeto y cantidad que esta en este bloque.
    // Es nulo si no hay objeto.
    //private Objeto m_obj;
    private short m_obj_ind;
    private int m_obj_cant;
    
    // Cliente que esta en este bloque.
    private short m_clienteId = 0;
    
    private int grh[] = new int[4];
    
    public MapCell(short x, short y) {
    	this.m_x = (byte)x;
    	this.m_y = (byte)y;
    }
/*    
    public Objeto getObj() {
        return m_obj;
    }
    public void setObj(Objeto obj) {
        this.m_obj = obj;
    }
    */
    
    public int getGrh(int val) {return this.grh[val];}
    
    public short getX() {
    	return this.m_x;
    }
    
    public short getY() {
    	return this.m_y;
    }
    
    public short getObjInd() {
        return this.m_obj_ind;
    }
    
    public int getObjCant() {
        return this.m_obj_cant;
    }
    
    public boolean hayObjeto() {
    	return this.m_obj_ind > 0 && this.m_obj_cant > 0;
    }

    public void quitarObjeto() {
    	this.m_obj_ind = 0;
    	this.m_obj_cant = 0;
    }
    
    public void setGrh(int val, int value) {this.grh[val] = value;}
    
    public void setObj(short obj_ind, int cant) {
        this.m_obj_ind = obj_ind;
        this.m_obj_cant = cant;
    }
    
    public Npc getNpc() {
        return AojServer.instance().getNPC(this.m_npc);
    }
    
    public void setNpc(Npc npc) {
        this.m_npc = (npc == null) ? 0 : npc.getId();
    }
    
    public short getClienteId() {
        return this.m_clienteId;
    }
    
    public void setClienteId(short id) {
        this.m_clienteId = id;
    }
    
    public byte getTrigger() {
        return this.m_trigger;
    }

    public void setTrigger(byte value) {
        this.m_trigger = value;
    }
    
    public boolean estaModificado() {
        return this.m_flags.get(FLAG_MODIFIED);
    }
    
    public boolean estaBloqueado() {
        return this.m_flags.get(FLAG_BLOQUED);
    }
    
    public void setBloqueado(boolean estado) {
        this.m_flags.set(FLAG_BLOQUED, estado);
    }
    
    public void setModificado(boolean estado) {
        this.m_flags.set(FLAG_MODIFIED, estado);
    }
    
    public boolean hayTeleport() {
        //return (m_dest != null);
    	return (this.m_dest_mapa != 0 && this.m_dest_x != 0 && this.m_dest_y != 0);
    }
    
    public MapPos getTeleport() {
        //return m_dest;
    	return MapPos.mxy(this.m_dest_mapa, this.m_dest_x, this.m_dest_y);
    }
    
    public void setTeleport(MapPos dest) {
    	if (dest != null) {
    		this.m_dest_mapa = dest.map;
    		this.m_dest_x = dest.x;
    		this.m_dest_y = dest.y;
    	} else {
    		this.m_dest_mapa = 0;
    		this.m_dest_x = 0;
    		this.m_dest_y = 0;    		
    	}    	
        //this.m_dest = dest;
    }
    
}

