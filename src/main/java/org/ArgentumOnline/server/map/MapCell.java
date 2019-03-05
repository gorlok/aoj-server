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
package org.ArgentumOnline.server.map;

import java.util.BitSet;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.npc.Npc;

/**
 * @author gorlok
 */
public class MapCell {
    // del archivo .map    
    
	// FIXME
	
    final static int FLAG_BLOQUED = 0;
    final static int FLAG_MODIFIED = 1;
    final static int FLAG_WATER = 2;
    final static int FLAGS_NUMBER = 3;

    public enum Trigger {
	    /* 0 */ TRIGGER_NADA,
	    /* 1 */ TRIGGER_BAJO_TECHO,
	    /* 2 */ TRIGGER_NO_RESPAWN, // Los NPCs no pueden hacer respawn y, el layer cuatro desaparece cuando un jugador pisa el tile: 
	    /* 3 */ TRIGGER_POS_INVALIDA, // Los npcs no pueden pisar este trigger
	    /* 4 */ TRIGGER_ZONA_SEGURA, // No se puede robar o pelear desde este trigger
	    /* 5 */ TRIGGER_ANTI_PIQUETE, // Te encarcelan si estas mucho tiempo sobre este trigger
	    /* 6 */ TRIGGER_ARENA_DUELOS, // ZONAPELEA Arena para duelos. Al pelear en este trigger no se caen las cosas y no cambia el estado de ciuda o crimi
	    /* 7 */ TRIGGER_ARENA_TORNEO; // Para torneos con espectadores
    }
    
    private BitSet m_flags = new BitSet(FLAGS_NUMBER);
    
    int borderArea = 0; //Areas by jao
    
    private Trigger trigger = Trigger.TRIGGER_NADA;
    
    private byte m_x;
    private byte m_y;
    
    // teleport (del archivo .inf) 
    private short m_dest_mapa;
    private short m_dest_x;
    private short m_dest_y;
    
    private short m_npc;
    
    private short m_obj_ind;
    private int m_obj_cant;
    
    private short playerId = 0;
    
    private int grh[] = new int[4];
    
    public MapCell(short x, short y) {
    	this.m_x = (byte)x;
    	this.m_y = (byte)y;
    }
    
    public int getGrh(int index) {
    	return this.grh[index];
    }
    
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
    
    public boolean hasObject() {
    	return this.m_obj_ind > 0 && this.m_obj_cant > 0;
    }

    public void quitarObjeto() {
    	this.m_obj_ind = 0;
    	this.m_obj_cant = 0;
    }
    
    public void setGrh(int index, int value) {
    	this.grh[index] = value;
    }
    
    public void setObj(short obj_ind, int cant) {
        this.m_obj_ind = obj_ind;
        this.m_obj_cant = cant;
    }
    
    public Npc getNpc() {
        return GameServer.instance().npcById(this.m_npc);
    }
    
    public void setNpc(Npc npc) {
        this.m_npc = (npc == null) ? 0 : npc.getId();
    }
    
    public short playerId() {
        return this.playerId;
    }
    
    public void playerId(short id) {
        this.playerId = id;
    }
    
    public Trigger getTrigger() {
        return this.trigger;
    }

    public void setTrigger(Trigger value) {
        this.trigger = value;
    }
    
    public boolean isModified() {
        return this.m_flags.get(FLAG_MODIFIED);
    }
    
    public boolean isBlocked() {
        return this.m_flags.get(FLAG_BLOQUED);
    }
    
    public void blocked(boolean estado) {
        this.m_flags.set(FLAG_BLOQUED, estado);
    }
    
    public void modified(boolean estado) {
        this.m_flags.set(FLAG_MODIFIED, estado);
    }
    
    public boolean isTeleport() {
    	return (this.m_dest_mapa != 0 && this.m_dest_x != 0 && this.m_dest_y != 0);
    }
    
    public MapPos teleport() {
    	return MapPos.mxy(this.m_dest_mapa, this.m_dest_x, this.m_dest_y);
    }
    
    public void teleport(MapPos dest) {
    	if (dest != null) {
    		this.m_dest_mapa = dest.map;
    		this.m_dest_x = dest.x;
    		this.m_dest_y = dest.y;
    	} else {
    		this.m_dest_mapa = 0;
    		this.m_dest_x = 0;
    		this.m_dest_y = 0;    		
    	}    	
    }
    
    public boolean testSpawnTriggerNpc(boolean underRoof) {
        if (underRoof) {
			return this.trigger != Trigger.TRIGGER_POS_INVALIDA 
					&& this.trigger != Trigger.TRIGGER_NO_RESPAWN;
		}
		return this.trigger != Trigger.TRIGGER_POS_INVALIDA 
				&& this.trigger != Trigger.TRIGGER_NO_RESPAWN 
				&& this.trigger != Trigger.TRIGGER_BAJO_TECHO;
    }

    public boolean isFreeForObject() {
        return !isBlocked() &&
        		!isWater() &&
        		!hasObject() && 
        		!isTeleport();
    }
    
    public boolean isWater() {
    	return getGrh(0) >= 1505 && getGrh(0) <= 1520 && getGrh(1) == 0;
    }

    public boolean isFreeForNpc(boolean canWater) {
	    if (!canWater) {
	        return !isBlocked() 
	        		&& playerId() == 0 
	        		&& getNpc() == null 
	        		&& getTrigger() != Trigger.TRIGGER_POS_INVALIDA 
	        		&& !isWater();
	    }
        return !isBlocked() 
        		&& playerId() == 0 
        		&& getNpc() == null 
        		&& getTrigger() != Trigger.TRIGGER_POS_INVALIDA;
    }
    
    public boolean isLegalPos(boolean canWater) {
        if (!canWater) {
            return !isBlocked() 
            	&& playerId() == 0 
            	&& getNpc() == null 
            	&& !isWater();
        }
        return !isBlocked() 
        		&& playerId() == 0
        		&& getNpc() == null
        		&& isWater();
    }
    
    /** es intemperie */
    public boolean isOutdoor() {
    	return getTrigger() != Trigger.TRIGGER_BAJO_TECHO
    			&& getTrigger() != Trigger.TRIGGER_NO_RESPAWN
    			&& getTrigger() != Trigger.TRIGGER_ZONA_SEGURA;
    }
    
    public boolean isAntiPiquete() {
    	return getTrigger() == Trigger.TRIGGER_ANTI_PIQUETE;
    }

    public boolean isUnderRoof() {
    	return getTrigger() == Trigger.TRIGGER_BAJO_TECHO;
    }
    
    public boolean isSafeZone() {
    	return this.trigger == Trigger.TRIGGER_ZONA_SEGURA; 
    }
    
    public boolean isArenaZone() {
    	return this.trigger == Trigger.TRIGGER_ARENA_DUELOS;
    }
    
    public boolean isTournamentZone() {
    	return this.trigger == Trigger.TRIGGER_ARENA_TORNEO;
    }
    
}
