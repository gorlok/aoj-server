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
package org.ArgentumOnline.server.areas;

import java.util.BitSet;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.map.Heading;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.AreaChangedResponse;
import org.ArgentumOnline.server.protocol.ObjectCreateResponse;
import org.ArgentumOnline.server.user.Player;


/** 
 * Areas class
 * Esta clase realiza el manejo de áreas para limitar el envío de datos a los usuarios
 * Idea original: Juan Martín Sotuyo Dodero (Maraxus)
 * Implementado y adaptado a Java: Juan Agustín Oliva (JAO) -> juancho_isap14@hotmail.com
 */
public class AreasAO implements Constants {
	
	/**
	 * if a new user enter in the game, or change map,
	 * we defined your area using a head that does not exists
	 */
	public final static Heading USER_NUEVO = Heading.NONE;
	
	/**
	 * Divide a MAP in 144 areas of 9x9. 
	 * AreaID from 1 to 144.
	 */
	private static int[][] MAP_TO_AREA = new int[101][101];
	
	/**
	 * bitwise of areas to send
	 * 
	 * bit0 => send area-1
	 * bit1 => send area-2
	 * ...
	 * bit11 => send area-12 
	 * 
	 * AREA_TO_SEND[00] = 000000000011
	 * AREA_TO_SEND[01] = 000000000111
	 * AREA_TO_SEND[02] = 000000001110
	 * AREA_TO_SEND[03] = 000000011100
	 * AREA_TO_SEND[04] = 000000111000
	 * AREA_TO_SEND[05] = 000001110000
	 * AREA_TO_SEND[06] = 000011100000
	 * AREA_TO_SEND[07] = 000111000000
	 * AREA_TO_SEND[08] = 001110000000
	 * AREA_TO_SEND[09] = 011100000000
	 * AREA_TO_SEND[10] = 111000000000
	 * AREA_TO_SEND[11] = 110000000000
	 */
	private static int[] AREAS_TO_SEND = new int[12];
	
	static {
		for(int i = 0; i < AREAS_TO_SEND.length; i++) {
			AREAS_TO_SEND[i] = 
					(int)(Math.pow(2, i)) 
					| ((i != 0)  ? (int)Math.pow(2, i - 1) : 0) 
					| ((i != 11) ? (int)Math.pow(2, i + 1) : 0);
		}
		
//		for(int x = 0; x < 101; x++) {
//			for(int y = 0; y < 101; y++) {
//				POS_TO_AREA[x][y] = (x / 9 + 1) * (y / 9 + 1);
//			}
//		}
		for(int x = 0; x < 101; x++) {
			for(int y = 0; y < 101; y++) {
				MAP_TO_AREA[x][y] = (x / 9)*12 + (y / 9) + 1;
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		for(int i = 0; i < AREAS_TO_SEND.length; i++)
			System.out.format("AREA_TO_SEND[%02d] = %s\n", i, Integer.toBinaryString(AREAS_TO_SEND[i]));
		
		
		for(int x = 0; x < 101; x++) {
			for(int y = 0; y < 101; y++) {
				System.out.format("[%d][%d]=%d ", x, y, MAP_TO_AREA[x][y]);
			}
			System.out.println();
		}
		
	}
	
	
	public void checkUpdateNeededUser(Map map, Player user, Heading heading) {
		
		var userArea = user.charArea();
		if (userArea.areaID == MAP_TO_AREA[user.pos().x][user.pos().y]) {
			return;
		}
		
		int minX = 0; int maxX = 0; int minY = 0; int maxY = 0;
			
		minX = userArea.minX;
		minY = userArea.minY;
		
		switch (heading) {
		case NORTH:
			maxY = minY - 1;
			minY = minY - 9;
			maxX = minX + 26;
			userArea.minX = minX;
			userArea.minY = minY;
			break;
			
		case SOUTH:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			userArea.minX = minX;
			userArea.minY = minY - 18;
			break;
			
		case WEST:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			userArea.minX = minX;
			userArea.minY = minY;
			break;
			
		case EAST:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			userArea.minX = minX - 18;
			userArea.minY = minY;
			break;
			
		default: //user nuevo
			minY = ((user.pos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((user.pos().x / 9) - 1) * 9;
			maxX = minX + 26;
			
			userArea.minX = minX;
			userArea.minY = minY;
			break;
		}
		
    	if (maxY > YMaxMapSize - 1) {
    		maxY = YMaxMapSize - 1;
    	}
    	
    	if (minY < YMinMapSize) {
    		minY = YMinMapSize;
    	}

    	if (maxX > XMaxMapSize - 1) {
    		maxX = XMaxMapSize - 1;
    	}
    	if (minX < XMinMapSize) {
    		minX = XMinMapSize;
		
	    }
    	
    	user.sendPacket(new AreaChangedResponse(user.pos().x, user.pos().y));
    	
    	for (byte x = (byte) minX; x < maxX;x++) {
    		for (byte y = (byte) minY; y < maxY; y++) {
    			
    			if (map.hasPlayer(x, y)) {
    				Player other = map.getPlayer(x, y);
    				if (user == other && heading == USER_NUEVO) {
    					user.sendPacket(user.characterCreate());
    				}
	    			if (user != other) {
	    				user.sendPacket(other.characterCreate());
	    				other.sendPacket(user.characterCreate());
	    			}	
    			}
    			
    			if (map.hasNpc(x, y)) {
    				user.sendPacket(map.getNpc(x, y).characterCreate());
    			}
    			
    			if (map.hasObject(x, y)) {
    				MapObject obj = map.getObject(x, y);
 
    				user.sendPacket(new ObjectCreateResponse((byte)x, (byte)y, obj.getInfo().GrhIndex));
    				
    				if (obj.getInfo().objType == ObjType.Puertas) {
    					user.sendBlockedPosition(x, y, map.isBlocked(x, y));
    					//user.sendBlockedPosition(x-1, y, map.isBlocked((byte)(x-1), y)); FIXME ?
    				}
    			}
    		}
    	}
    	
    	int areaX = user.pos().x / 9;
    	userArea.areasToSendX = AREAS_TO_SEND[areaX];
    	userArea.currentAreaX = (int) Math.pow(2, areaX);
    	
    	int areaY = user.pos().y / 9;
    	userArea.areasToSendY = AREAS_TO_SEND[areaY];
    	userArea.currentAreaY = (int) Math.pow(2, areaY);
    	
    	userArea.areaID = MAP_TO_AREA[user.pos().x][user.pos().y];
	}
	
	public void checkUpdateNeededNpc(Map map, Npc npc, Heading heading) {

		var npcArea = npc.charArea();
		if (npcArea.areaID == MAP_TO_AREA[npc.pos().x][npc.pos().y]) {
			return;
		}
		
		int minX = 0; int maxX = 0; int minY = 0; int maxY = 0;
			
		minX = npcArea.minX;
		minY = npcArea.minY;
		
		switch (heading) {
		case NORTH:
			maxY = minY - 1;
			minY = minY - 9;
			maxX = minX + 26;
			npcArea.minX = minX;
			npcArea.minY = minY;
			break;
			
		case SOUTH:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			npcArea.minX = minX;
			npcArea.minY = minY - 18;
			break;
			
		case WEST:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			npcArea.minX = minX;
			npcArea.minY = minY;
			break;
			
		case EAST:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			npcArea.minX = minX - 18;
			npcArea.minY = minY;
			break;
			
		default: //user nuevo
			minY = ((npc.pos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((npc.pos().x / 9) * 9);
			maxX = minX + 26;
			
			npcArea.minX = minX;
			npcArea.minY = minY;
			break;
		}
		
    	if (maxY > YMaxMapSize - 1) {
    		maxY = YMaxMapSize - 1;
    	}
    	if (minY < YMinMapSize) {
    		minY = YMinMapSize;
    	}
    	if (maxX > XMaxMapSize - 1) {
    		maxX = XMaxMapSize - 1;
    	}
    	if (minX < XMinMapSize) {
    		minX = XMinMapSize;
		
	    }
    	
    	if (map.getPlayersCount() > 0) {
    		for (byte x = (byte) minX; x < maxX; x++) {
        		for (byte y = (byte) minY; y < maxY; y++) {
        			if (map.hasPlayer(x, y)) {
        				Player player = map.getPlayer(x, y);
        				player.sendPacket(npc.characterCreate());
        			}
        		}
    		}
    	}
    	
    	int areaX = npc.pos().x / 9;
    	npcArea.areasToSendX = AREAS_TO_SEND[areaX];
    	npcArea.currentAreaX = (int) Math.pow(2, areaX);
    	
    	int areaY = npc.pos().y / 9;
    	npcArea.areasToSendY = AREAS_TO_SEND[areaY];
    	npcArea.currentAreaY = (int) Math.pow(2, areaY);
    	
    	npcArea.areaID = MAP_TO_AREA[npc.pos().x][npc.pos().y];
	}
	
	public void loadNpc(Map map, Npc npc) {
		npc.charArea().reset();
		checkUpdateNeededNpc(map, npc, USER_NUEVO);
	}
	
	public void loadUser(Map map, Player user) {
		user.charArea().reset();
		checkUpdateNeededUser(map, user, USER_NUEVO);
	}
	
	/**
	 * JAO: Enviamos la área según la posición que se pase por el parámetro. Este método se utiliza frecuentemente
	 * para enviar al usuarios los objetos en el piso
	 */
	public void sendToAreaByPos(Map map, byte x, byte y, ServerPacket packet) {
		int areaX = (int) Math.pow(2, x / 9);
		int areaY = (int) Math.pow(2, y / 9);

		for (Player player : map.getPlayers()) {
			if ( ((player.charArea().areasToSendX & areaX) > 0) &&
				 ((player.charArea().areasToSendY & areaY) > 0) ) {
					player.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Envío al área, y adyacentes, de los parámetros especificados, pero no al index 'id'
	 */
	public void sendToAreaButIndex(Map map, int areaX, int areaY, int id, ServerPacket packet) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for (Player player : map.getPlayers()) {
			int tempInt = (player.charArea().areasToSendX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);

			if (gral.cardinality() > 0) {
				tempInt = (player.charArea().areasToSendY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					if (player.getId() != id) {
						player.sendPacket(packet);
					}
				}
			}
		}
	}
	
	/**
	 * Envío de datos al área del user, y adyacentes
	 */
	public void sendToUserArea(Map map, Player user, ServerPacket packet) {
		var userArea = user.charArea();
		
		int areaX = userArea.currentAreaX;
		int areaY = userArea.currentAreaY;
		
		for (Player player : map.getPlayers()) {
			int tempInt = (player.charArea().areasToSendX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				tempInt = (player.charArea().areasToSendY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					player.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * Envío al área del user, y adyacentes, excepto al parámetro 'user'
	 */
	public void sendToUserAreaButIndex(Map map, Player user, ServerPacket packet) {
		var userArea = user.charArea();

		int areaX = userArea.currentAreaX;
		int areaY = userArea.currentAreaY;
		
		for (Player player : map.getPlayers()) {
			int tempInt = (player.charArea().areasToSendX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (player.charArea().areasToSendY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					if (player.getId() != user.getId()) {
						player.sendPacket(packet);
					}
				}
			}
		}
	}
	
	/**
	 * Envío de datos al área del NPC, y adyacentes
	 */
	public void sendToNPCArea(Map map, Npc npc, ServerPacket packet) {

		int areaX = npc.charArea().currentAreaX;
		int areaY = npc.charArea().currentAreaY;
		
		for (Player player : map.getPlayers()) {
			int tempInt = (player.charArea().areasToSendX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (player.charArea().areasToSendY & areaY);
				gral.set(tempInt);
				if (gral.cardinality() > 0) {
					player.sendPacket(packet);
				}
			}
		}
	}

	private static AreasAO instance = null;
	public static AreasAO instance() {
		if (instance == null)  {
			instance = new AreasAO();
		}
		return instance;
	}

}
