/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.AreaChangedResponse;
import org.ArgentumOnline.server.protocol.ObjectCreateResponse;
import org.ArgentumOnline.server.user.Player;


/** 
 * Areas class
 * Esta clase realiza el manejo de �reas para limitar el env�o de datos a los usuarios
 * Idea original: Juan Mart�n Sotuyo Dodero (Maraxus)
 * Implementado y adaptado a Java: Juan Agust�n Oliva (JAO) -> juancho_isap14@hotmail.com
 */
public class AreasAO implements Constants {
	
	/**
	 * if a new user enter in the game, or change map,
	 * we defined your area using a head that does not exists
	 */
	public final static byte USER_NUEVO = heading_w + 1;
	
	//JAO: aqu{i almacenamos una �reaID para cada POS del usuario
	public int[][] areasInfo = new int[101][101];
	
	//JAO: aqu� almacenamos un array de 11 bits
	private int areasRecibe[] = new int[12];
	
	//Declaramos el AojServer, que se iniciar� en el m�todo initAreas
	GameServer server;
	
	//Declaramos el mapa, que ser� iniciado en el initAreas
	Map map;
	
	/**
	 * Devuelve un n�mero que es igual a dos elevado a loopc, siendo loopc un integer que va de 0 a 11
	 * Esta funci�n reemplaza el iif de VB6
	 */
	private int beginBucle(int loopc) {
		return (int) Math.pow(2, loopc);
	}
	
	/**
	 * Devuelve un n�mero que es igual a dos elevado a loopc - 1 cuando loopc es distinto que cero, siendo loopc un integer que va de 0 a 11
	 * Esta funci�n reemplaza el iif de VB6
	 */
	private int loopc1(int loopc) {
		if (loopc != 0) return (int) Math.pow(2,loopc - 1);
		return 0;
	}
	
	/**
	 * Devuelve un n�mero que es igual a dos elevado a loopc + 1 cuando loopc es distinto que once, siendo loopc un integer que va de 0 a 11
	 * Esta funci�n reemplaza el iif de VB6
	 */
	private int loopc2(int loopc) {
		if (loopc != 11) return (int) Math.pow(2,loopc + 1);
		return 0;
	}
	
	/**
	 * Cargamos las �reas por mapa
	 */
	public void initAreas(GameServer server, Map map) {
		
		this.server = server;
		this.map = map;

		for(int loopc = 0; loopc < 12; loopc++) {
			areasRecibe[loopc] = (short) (beginBucle(loopc) | loopc1(loopc) | loopc2(loopc));
		}
		
		for(int x = 0; x < 101; x++) {
			for(int y = 0; y < 101; y++) {
				areasInfo[x][y] = (x / 9 + 1) * (y / 9 + 1);
			}
		}
	}
	
	/**
	 * Env�o de datos cuando un usuario cambia de �rea, y updatea el �rea ID y adyacentes
	 */
	public void checkUpdateNeededUser(Player user, short dir) {
		
		var userArea = user.getUserArea();
		
		if (userArea.getArea() == areasInfo[user.pos().x][user.pos().y]) {
			return;
		}
		
		int minX = 0; int maxX = 0; int minY = 0; int maxY = 0;
		short tempInt = 0;
			
		minX = userArea.minX;
		minY = userArea.minY;
		
		switch (dir) {
		case heading_n:
			maxY = minY - 1;
			minY = minY - 9;
			maxX = minX + 26;
			userArea.minX = minX;
			userArea.minY = minY;
			break;
			
		case heading_s:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			userArea.minX = minX;
			userArea.minY = minY - 18;
			break;
			
		case heading_w:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			userArea.minX = minX;
			userArea.minY = minY;
			break;
			
		case heading_e:
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
    	
    	for(byte x = (byte) minX; x < maxX;x++) {
    		for(byte y = (byte) minY; y < maxY; y++) {
    			
    			if (map.hasPlayer(x, y)) {
    				tempInt = map.getPlayer(x, y).getId();
    			
    			
    			if (user.getId() != tempInt && tempInt > 0) {
    				Player other = this.server.playerById(tempInt);
    				user.sendPacket(other.createCC());
    				other.sendPacket(user.createCC());
    				
    			} else if (dir == USER_NUEVO) {
    				user.sendPacket(user.createCC());
    			}
    			
    			}
    			
    			if (map.hasNpc(x, y)) {
    				user.sendPacket(map.getNpc(x, y).createCC());
    			}
    			
    			if (map.hasObject(x, y)) {
    				MapObject obj = map.getObject(x, y);
 
    				user.sendPacket(new ObjectCreateResponse((byte)x, (byte)y, obj.getInfo().GrhIndex));
    				
    				if (obj.getInfo().objType == ObjType.Puertas) {
    					user.sendBlockedPosition(x, y, map.isBlocked(x, y));
    					byte px = (byte) (x - 1);
    					user.sendBlockedPosition(x - 1, y, map.isBlocked(px, y));
    				}
    			}
    		}
    	}
    	
    	tempInt = (short) (user.pos().x / 9);
    	userArea.setAreaRecibeX(areasRecibe[tempInt]);
    	userArea.setAreaPerteneceX((int) Math.pow(2, tempInt));
    	
    	tempInt = (short) (user.pos().y / 9);
    	userArea.setAreaRecibeY(areasRecibe[tempInt]);
    	userArea.setAreaPerteneceY( (int) Math.pow(2, tempInt));
    	
    	userArea.setArea(areasInfo[user.pos().x][user.pos().y]);
	}
	
	/**
	 * �dem que el m�todo checkUpdateNeededUser, pero con Npcs
	 */
	public void checkUpdateNeededNpc(Npc npc, byte dir) {
		
		if (npc.getArea() == areasInfo[npc.pos().x][npc.pos().y]) return;
		
		int minX = 0; int maxX = 0; int minY = 0; int maxY = 0;
		short tempInt = 0;
			
		minX = npc.minX;
		minY = npc.minY;
		
		switch (dir) {
		case heading_n:
			maxY = minY - 1;
			minY = minY - 9;
			maxX = minX + 26;
			npc.minX = minX;
			npc.minY = minY;
			break;
			
		case heading_s:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			npc.minX = minX;
			npc.minY = minY - 18;
			break;
			
		case heading_w:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			npc.minX = minX;
			npc.minY = minY;
			break;
			
		case heading_e:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			npc.minX = minX - 18;
			npc.minY = minY;
			break;
			
		default: //user nuevo
			minY = ((npc.pos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((npc.pos().x / 9) * 9);
			maxX = minX + 26;
			
			npc.minX = minX;
			npc.minY = minY;
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
    		
    		for(byte x = (byte) minX; x < maxX;x++) {
        		for(byte y = (byte) minY; y < maxY; y++) {
        			if (map.hasPlayer(x, y)) {
        				Player jao = map.getPlayer(x, y);
        				jao.sendPacket(npc.createCC());
        			}
        		}
    		}
    	}
    	
    	tempInt = (short) (npc.pos().x / 9);
    	npc.setAreaRecibeX(areasRecibe[tempInt]);
    	npc.setAreaPerteneceX((int) Math.pow(2, tempInt));
    	
    	tempInt = (short) (npc.pos().y / 9);
    	npc.setAreaRecibeY(areasRecibe[tempInt]);
    	npc.setAreaPerteneceY( (int) Math.pow(2, tempInt));
    	
    	npc.setArea(areasInfo[npc.pos().x][npc.pos().y]);
	}
	
	public void resetNpc(Npc npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
	}
	
	public void resetUser(Player user) {
		var userArea = user.getUserArea();
		userArea.setArea(0);
		userArea.setAreaPerteneceX(0);
		userArea.setAreaPerteneceY(0);
		userArea.setAreaRecibeX(0);
		userArea.setAreaRecibeY(0);
	}
	
	public void loadNpc(Npc npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
		checkUpdateNeededNpc(npc, USER_NUEVO);
	}
	
	public void loadUser(Player user) {
		resetUser(user);
		checkUpdateNeededUser(user, USER_NUEVO);
	}
	
	/**
	 * JAO: Enviamos la �rea seg�n la posici�n que se pase por el par�metro. Este m�todo se utiliza frecuentemente
	 * para enviar al usuarios los objetos en el piso
	 */
	public void sendToAreaByPos(Map map, int areaX, int areaY, ServerPacket packet) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(Player player : map.getPlayers()) {
			int tempInt = (player.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (player.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					player.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * Env�o al �rea, y adyacentes, de los par�metros especificados, pero no al index 'id'
	 */
	public void sendToAreaButIndex(Map map, int areaX, int areaY, int id, ServerPacket packet) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(Player player : map.getPlayers()) {
			int tempInt = (player.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);

			if (gral.cardinality() > 0) {
				tempInt = (player.getUserArea().areaRecibeY & areaY);
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
	 * Env�o de datos al �rea del user, y adyacentes
	 */
	public void sendToUserArea(Player user, ServerPacket packet) {
		var userArea = user.getUserArea();
		
		int areaX = userArea.areaPerteneceX;
		int areaY = userArea.areaPerteneceY;
		
		for(Player player : map.getPlayers()) {
			int tempInt = (player.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				tempInt = (player.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					player.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * Env�o al �rea del user, y adyacentes, excepto al par�metro 'user'
	 */
	public void sendToUserAreaButIndex(Player user, ServerPacket packet) {
		var userArea = user.getUserArea();

		int areaX = userArea.areaPerteneceX;
		int areaY = userArea.areaPerteneceY;
		
		for(Player player : map.getPlayers()) {
			int tempInt = (player.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (player.getUserArea().areaRecibeY & areaY);
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
	 * Env�o de datos al �rea del NPC, y adyacentes
	 */
	public void sendToNPCArea(Npc npc, ServerPacket packet) {

		int areaX = npc.areaPerteneceX;
		int areaY = npc.areaPerteneceY;
		
		for(Player player : map.getPlayers()) {
			int tempInt = (player.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (player.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				if (gral.cardinality() > 0) {
					player.sendPacket(packet);
				}
			}
		}
	}

}
