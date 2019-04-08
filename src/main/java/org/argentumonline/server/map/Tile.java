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
package org.argentumonline.server.map;

import java.util.BitSet;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.user.User;

/**
 * @author gorlok
 */
public class Tile {
    // del archivo .map    
    
    private final static int FLAG_BLOQUED = 0;
    private final static int FLAG_MODIFIED = 1;
    private final static int FLAG_WATER = 2;
    private final static int FLAGS_NUMBER = 3;

    private BitSet flags = new BitSet(FLAGS_NUMBER);
    
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
    
    private Trigger trigger = Trigger.TRIGGER_NADA;
    
    private byte x;
    private byte y;
    
    // teleport (del archivo .inf) 
    private short dest_map;
    private short dest_x;
    private short dest_y;
    
    private short objIndex;
    private int objCount;
    
    private short userId = 0;
    private short npcId;
    
    private int grh[] = new int[4];
    
    public Tile(short x, short y) {
    	this.x = (byte)x;
    	this.y = (byte)y;
    }
    
    public int getGrh(int index) {
    	return this.grh[index];
    }
    
    public short x() {
    	return this.x;
    }
    
    public short y() {
    	return this.y;
    }
    
    public short objIndex() {
        return this.objIndex;
    }
    
    public int objCount() {
        return this.objCount;
    }
    
    public boolean hasObject() {
    	return this.objIndex > 0 && this.objCount > 0;
    }

    public void removeObject() {
    	this.objIndex = 0;
    	this.objCount = 0;
    }
    
    public void setGrh(int index, int value) {
    	this.grh[index] = value;
    }
    
    public void setObj(short obj_ind, int cant) {
        this.objIndex = obj_ind;
        this.objCount = cant;
    }
    
    public User getUser() {
    	return GameServer.instance().userById(this.userId);
    }
    
    public Npc npc() {
        return GameServer.instance().npcById(this.npcId);
    }
    
    public boolean hasNpc() {
    	return this.npcId > 0 && this.npc() != null;
    }
    
    public void npc(Npc npc) {
        this.npcId = (npc == null) ? 0 : npc.getId();
    }
    
    public short userId() {
        return this.userId;
    }
    
    public void userId(short id) {
        this.userId = id;
    }
    
    public Trigger trigger() {
        return this.trigger;
    }

    public void trigger(Trigger value) {
        this.trigger = value;
    }
    
    public boolean isModified() {
        return this.flags.get(FLAG_MODIFIED);
    }
    
    public boolean isBlocked() {
        return this.flags.get(FLAG_BLOQUED);
    }
    
    public void blocked(boolean estado) {
        this.flags.set(FLAG_BLOQUED, estado);
    }
    
    public void modified(boolean estado) {
        this.flags.set(FLAG_MODIFIED, estado);
    }
    
    public boolean isTeleport() {
    	return (this.dest_map != 0 && this.dest_x != 0 && this.dest_y != 0);
    }
    
    public MapPos teleport() {
    	return MapPos.mxy(this.dest_map, this.dest_x, this.dest_y);
    }
    
    public void teleport(MapPos dest) {
    	if (dest != null) {
    		this.dest_map = dest.map;
    		this.dest_x = dest.x;
    		this.dest_y = dest.y;
    	} else {
    		this.dest_map = 0;
    		this.dest_x = 0;
    		this.dest_y = 0;    		
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
	        		&& userId() == 0 
	        		&& npc() == null 
	        		&& trigger() != Trigger.TRIGGER_POS_INVALIDA 
	        		&& !isWater();
	    }
        return !isBlocked() 
        		&& userId() == 0 
        		&& npc() == null 
        		&& trigger() != Trigger.TRIGGER_POS_INVALIDA;
    }
    
    public boolean isLegalPos(boolean canWater) {
    	return isLegalPos(canWater, true);
    }
    
    public boolean isLegalPos(boolean canWater, boolean canLand) {
    	boolean isDeadChar = false;
    	boolean isAdminInvisible = false;
    	
	    if (userId() > 0) {
	        isDeadChar = !getUser().isAlive();
	        isAdminInvisible = getUser().getFlags().AdminInvisible;
	    }
    	
        if (canWater && canLand) {
            return !isBlocked() 
            	&& (userId() == 0 || isAdminInvisible) 
            	&& npc() == null; 
        } else if (canLand && !canWater) {
            return !isBlocked() 
                	&& (userId() == 0 || isDeadChar || isAdminInvisible) 
                	&& npc() == null 
                	&& !isWater();
        } else if (canWater && !canLand) {
            return !isBlocked() 
                	&& (userId() == 0 || isDeadChar || isAdminInvisible) 
                	&& npc() == null 
                	&& isWater();
        }
        return false;
    }
    
    /** es intemperie */
    public boolean isOutdoor() {
    	return trigger() != Trigger.TRIGGER_BAJO_TECHO
    			&& trigger() != Trigger.TRIGGER_NO_RESPAWN
    			&& trigger() != Trigger.TRIGGER_ZONA_SEGURA;
    }
    
    public boolean isAntiPiquete() {
    	return trigger() == Trigger.TRIGGER_ANTI_PIQUETE;
    }

    public boolean isUnderRoof() {
    	return trigger() == Trigger.TRIGGER_BAJO_TECHO;
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

    public boolean isFreePosWithWater() {
        return !isBlocked() 
        		&& userId() == 0 
        		&& npc() == null 
        		&& isWater();
    }
    
    public boolean isFreePosWithoutWater() {
        return !isBlocked() 
        		&& userId() == 0 
        		&& npc() == null 
        		&& !isWater();
    }
            
    public boolean isFreePosForAdmin() {
        // Los Admins no respetan las leyes de la física :P
        return userId() == 0 
        		&& npc() == null;
    }

}    
    
