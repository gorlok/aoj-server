package org.ArgentumOnline.server.areas;

import java.util.BitSet;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.net.ServerPacketID;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.Constants;


/** 
 * Areas class
 * Esta clase realiza el manejo de áreas para enviar datos a los clientes
 * Idea original: Juan Martín Sotuyo Dodero (Maraxus)
 * Implementado y adaptado a Java: Juan Agustín Oliva (JAO) -> juancho_isap14@hotmail.com
 */
public class AreasAO implements Constants {
	
	/**
	 * if a new user enter in the game, or change map,
	 * we defined your area using a head that does not exists
	 */
	public final static short USER_NUEVO = heading_w + 1;
	
	//JAO: aqu{i almacenamos una áreaID para cada POS del usuario
	public int[][] areasInfo = new int[101][101];
	
	//JAO: aquí almacenamos un array de 11 bits
	private int areasRecibe[] = new int[12];
	
	//Declaramos el AojServer, que se iniciará en el método initAreas
	GameServer server;
	
	//Declaramos el mapa, que será iniciado en el initAreas
	Map map;
	
	/**
	 * Devuelve un número que es igual a dos elevado a loopc, siendo loopc un integer que va de 0 a 11
	 * Esta función reemplaza el iif de VB6
	 */
	private int beginBucle(int loopc) {
		return (int) Math.pow(2, loopc);
	}
	
	/**
	 * Devuelve un número que es igual a dos elevado a loopc - 1 cuando loopc es distinto que cero, siendo loopc un integer que va de 0 a 11
	 * Esta función reemplaza el iif de VB6
	 */
	private int loopc1(int loopc) {
		if (loopc != 0) return (int) Math.pow(2,loopc - 1);
		return 0;
	}
	
	/**
	 * Devuelve un número que es igual a dos elevado a loopc + 1 cuando loopc es distinto que once, siendo loopc un integer que va de 0 a 11
	 * Esta función reemplaza el iif de VB6
	 */
	private int loopc2(int loopc) {
		if (loopc != 11) return (int) Math.pow(2,loopc + 1);
		return 0;
	}
	
	/**
	 * Cargamos las áreas por mapa
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
	 * Envío de datos cuando un usuario cambia de área, y updatea el área ID y adyacentes
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
		case heading_s:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			userArea.minX = minX;
			userArea.minY = minY - 18;
		case heading_w:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			userArea.minX = minX;
			userArea.minY = minY;
		case heading_e:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			userArea.minX = minX - 18;
			userArea.minY = minY;
		default: //user nuevo
			minY = ((user.pos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((user.pos().x / 9) - 1) * 9;
			maxX = minX + 26;
			
			userArea.minX = minX;
			userArea.minY = minY;
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
    	
    	user.enviar(ServerPacketID.AreaChanged, (byte)user.pos().x, (byte)user.pos().y);
    	
    	for(short x = (short) minX; x < maxX;x++) {
    		for(short y = (short) minY; y < maxY; y++) {
    			
    			if (map.hayCliente(x, y)) {
    				tempInt = map.getCliente(x, y).getId();
    			
    			
    			if (user.getId() != tempInt && tempInt > 0) {
    				Player other = this.server.getClientById(tempInt);
    				user.enviar(ServerPacketID.CharacterCreate, other.ccParams());
    				other.enviar(ServerPacketID.CharacterCreate, user.ccParams());
    				
    			} else if (dir == USER_NUEVO) {
    				user.enviar(ServerPacketID.CharacterCreate, user.ccParams());
    			}
    			
    			}
    			
    			if (map.hayNpc(x, y)) {
    				user.enviar(ServerPacketID.CharacterCreate, map.getNpc(x, y).ccParams());
    			}
    			
    			if (map.hayObjeto(x, y)) {
    				MapObject obj = map.getObjeto(x, y);
 
    				user.enviar(ServerPacketID.ObjectCreate, (byte)x, (byte)y, (short)obj.getInfo().GrhIndex);
    				
    				if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
    					user.enviarBQ(x, y, map.estaBloqueado(x, y));
    					short px = (short) (x - 1);
    					user.enviarBQ(x - 1, y, map.estaBloqueado(px, y));
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
	 * ídem que el método checkUpdateNeededUser, pero con Npcs
	 */
	public void checkUpdateNeededNpc(Npc npc, short dir) {
		
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
		case heading_s:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			npc.minX = minX;
			npc.minY = minY - 18;
		case heading_w:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			npc.minX = minX;
			npc.minY = minY;
		case heading_e:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			npc.minX = minX - 18;
			npc.minY = minY;
		default: //user nuevo
			minY = ((npc.pos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((npc.pos().x / 9) * 9);
			maxX = minX + 26;
			
			npc.minX = minX;
			npc.minY = minY;
			
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
    	
    	if (map.getCantUsuarios() > 0) {
    		
    		for(short x = (short) minX; x < maxX;x++) {
        		for(short y = (short) minY; y < maxY; y++) {
        			if (map.hayCliente(x, y)) {
        				Player jao = map.getCliente(x, y);
        				
        				jao.enviar(ServerPacketID.CharacterCreate, npc.ccParams());
        				
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
	 * JAO: Enviamos la área según la posición que se pase por el parámetro. Este método se utiliza frecuentemente
	 * para enviar al cliente los objetos en el piso
	 */
	public void sendToAreaByPos(Map map, int areaX, int areaY, ServerPacketID msg, Object... params) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(int loopc = 0; loopc < map.getCantUsuarios(); loopc++) {
			Player user = map.spUser(loopc);
			
			int tempInt = (user.getUserArea().areaRecibeX & areaX);
			
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (user.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					user.enviar(msg, params);
				}
			}
		}
	}
	
	/**
	 * Envío al área, y adyacentes, de los parámetros especificados, pero no al index 'id'
	 */
	public void sendToAreaButIndex(Map map, int areaX, int areaY, int id, ServerPacketID msg, Object... params) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(int loopc = 0; loopc < map.getCantUsuarios(); loopc++) {
			Player user = map.spUser(loopc);
			
			int tempInt = (user.getUserArea().areaRecibeX & areaX);
			
			BitSet gral = new BitSet();
			
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (user.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					if (user.getId() != id) user.enviar(msg, params);
				}
			}
		}
	}
	
	/**
	 * Envío de datos al área del user, y adyacentes
	 */
	public void sendToUserArea(Player user, ServerPacketID msg, Object... params) {
		var userArea = user.getUserArea();
		
		int areaX = userArea.areaPerteneceX;
		int areaY = userArea.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Player tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					tempIndex.enviar(msg, params);
				}
			}
		}
	}
	
	/**
	 * Envío al área del user, y adyacentes, excepto al parámetro 'user'
	 */
	public void sendToUserAreaButIndex(Player user, ServerPacketID msg, Object... params) {
		var userArea = user.getUserArea();

		int areaX = userArea.areaPerteneceX;
		int areaY = userArea.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Player tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					if (tempIndex.getId() != user.getId()) tempIndex.enviar(msg, params);
				}
			}
		}
	}
	
	/**
	 * Envío de datos al área del NPC, y adyacentes
	 */
	public void sendToNPCArea(Npc npc, ServerPacketID msg, Object... params) {

		int areaX = npc.areaPerteneceX;
		int areaY = npc.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Player tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.getUserArea().areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.getUserArea().areaRecibeY & areaY);
				gral.set(tempInt);
				if (gral.cardinality() > 0) {
					
					tempIndex.enviar(msg, params);
				}
			}
		}
	}

}
