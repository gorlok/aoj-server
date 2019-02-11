package org.ArgentumOnline.server.areas;

import java.util.BitSet;

import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Map;
import org.ArgentumOnline.server.MapObject;
import org.ArgentumOnline.server.Npc;
import org.ArgentumOnline.server.protocol.serverPacketID;
import org.ArgentumOnline.server.Constants;


/** 
 * Areas class
 * Esta clase realiza el manejo de áreas para enviar datos a los clientes
 * Idea original: Juan Martín Sotuyo Dodero (Maraxus)
 * Implementado y adaptado a Java: Juan Agustín Oliva (JAO) -> juancho_isap14@hotmail.com
 */

public class areasAO implements Constants{
	
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
	AojServer server;
	
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
	
	public void initAreas(AojServer server, Map map) {
		
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
	
	public void checkUpdateNeededUser(Client user, short dir) {
		
		if (user.getArea() == areasInfo[user.getPos().x][user.getPos().y]) return;
		
		int minX = 0; int maxX = 0; int minY = 0; int maxY = 0;
		short tempInt = 0;
			
		minX = user.minX;
		minY = user.minY;
		
		switch (dir) {
		case heading_n:
			maxY = minY - 1;
			minY = minY - 9;
			maxX = minX + 26;
			user.minX = minX;
			user.minY = minY;
		case heading_s:
			maxY = minY + 35;
			minY = minY + 27;
			maxX = minX + 26;
			user.minX = minX;
			user.minY = minY - 18;
		case heading_w:
			maxX = minX - 1;
			minX = minX - 9;
			maxY = minY + 26;
			user.minX = minX;
			user.minY = minY;
		case heading_e:
			maxX = minX + 35;
			minX = minX + 27;
			maxY = minY + 26;
			user.minX = minX - 18;
			user.minY = minY;
		default: //user nuevo
			minY = ((user.getPos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((user.getPos().x / 9) - 1) * 9;
			maxX = minX + 26;
			
			user.minX = minX;
			user.minY = minY;
			
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
    	
    	user.enviar(serverPacketID.areasChange, user.getPos().x, user.getPos().y);
    	
    	for(short x = (short) minX; x < maxX;x++) {
    		for(short y = (short) minY; y < maxY; y++) {
    			
    			if (map.hayCliente(x, y)) {
    				tempInt = map.getCliente(x, y).getId();
    			
    			
    			if (user.getId() != tempInt && tempInt > 0) {
    				Client jao = this.server.getCliente(tempInt);
    				
    				user.enviar(serverPacketID.CC, jao.ccParams());
    				jao.enviar(serverPacketID.CC, user.ccParams());
    				
    			} else if (dir == this.USER_NUEVO) {
    				user.enviar(serverPacketID.CC, user.ccParams());
    			}
    			
    			}
    			
    			if (map.hayNpc(x, y)) {
    				user.enviar(serverPacketID.MSG_CCNPC, map.getNpc(x, y).ccParams());
    			}
    			
    			if (map.hayObjeto(x, y)) {
    				MapObject obj = map.getObjeto(x, y);
 
    				user.enviar(serverPacketID.MSG_HO, obj.getInfo().GrhIndex, x, y);
    				
    				if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
    					user.enviarBQ(x, y, map.estaBloqueado(x, y));
    					short px = (short) (x - 1);
    					user.enviarBQ(x - 1, y, map.estaBloqueado(px, y));
    				}
    				
    			}
    			
    		}
    	}
    	
    	tempInt = (short) (user.getPos().x / 9);
    	user.setAreaRecibeX(areasRecibe[tempInt]);
    	user.setAreaPerteneceX((int) Math.pow(2, tempInt));
    	
    	tempInt = (short) (user.getPos().y / 9);
    	user.setAreaRecibeY(areasRecibe[tempInt]);
    	user.setAreaPerteneceY( (int) Math.pow(2, tempInt));
    	
    	user.setArea(areasInfo[user.getPos().x][user.getPos().y]);
    	
    	
	}
	
	/**
	 * ídem que el método checkUpdateNeededUser, pero con Npcs
	 */
	
	public void checkUpdateNeededNpc(Npc npc, short dir) {
		
		if (npc.getArea() == areasInfo[npc.getPos().x][npc.getPos().y]) return;
		
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
			minY = ((npc.getPos().y / 9) - 1) * 9;
			maxY = minY + 26;
			
			minX = ((npc.getPos().x / 9) * 9);
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
        				Client jao = map.getCliente(x, y);
        				
        				jao.enviar(serverPacketID.MSG_CCNPC, npc.ccParams());
        				
        			}
        		}
    		}
    	}
    	
    	tempInt = (short) (npc.getPos().x / 9);
    	npc.setAreaRecibeX(areasRecibe[tempInt]);
    	npc.setAreaPerteneceX((int) Math.pow(2, tempInt));
    	
    	tempInt = (short) (npc.getPos().y / 9);
    	npc.setAreaRecibeY(areasRecibe[tempInt]);
    	npc.setAreaPerteneceY( (int) Math.pow(2, tempInt));
    	
    	npc.setArea(areasInfo[npc.getPos().x][npc.getPos().y]);
		
	}
	
	public void resetNpc(Npc npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
	}
	
	public void resetUser(Client npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
	}
	
	public void loadNpc(Npc npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
		this.checkUpdateNeededNpc(npc, this.USER_NUEVO);
	}
	
	public void loadUser(Client npc) {
		npc.setArea(0);
		npc.setAreaPerteneceX(0);
		npc.setAreaPerteneceY(0);
		npc.setAreaRecibeX(0);
		npc.setAreaRecibeY(0);
		this.checkUpdateNeededUser(npc, this.USER_NUEVO);
	}
	
	/**
	 * JAO: Enviamos la área según la posición que se pase por el parámetro. Este método se utiliza frecuentemente
	 * para enviar al cliente los objetos en el piso
	 */
	
	public void sendToAreaByPos(Map map, int areaX, int areaY, serverPacketID msg, Object... params) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(int loopc = 0; loopc < map.getCantUsuarios(); loopc++) {
			Client tempIndex = map.spUser(loopc);
			
			int tempInt = (tempIndex.areaRecibeX & areaX);
			
			BitSet gral = new BitSet();
			
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					tempIndex.enviar(msg, params);
				}
				
			}
			
		}
		
	}
	
	/**
	 * Envío al área, y adyacentes, de los parámetros especificados, pero no al index 'id'
	 */
	
	public void sendToAreaButIndex(Map map, int areaX, int areaY, int id, serverPacketID msg, Object... params) {
		areaX = (int) Math.pow(2, areaX / 9);
		areaY = (int) Math.pow(2, areaY / 9);

		for(int loopc = 0; loopc < map.getCantUsuarios(); loopc++) {
			Client tempIndex = map.spUser(loopc);
			
			int tempInt = (tempIndex.areaRecibeX & areaX);
			
			BitSet gral = new BitSet();
			
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.areaRecibeY & areaY);
				gral.set(tempInt);
				
				if (gral.cardinality() > 0) {
					if (tempIndex.getId() != id) tempIndex.enviar(msg, params);
				}
				
			}
			
		}
	}
	
	/**
	 * Envío de datos al área del user, y adyacentes
	 */
	
	public void sendToUserArea(Client user, serverPacketID msg, Object... params) {
		int areaX = user.areaPerteneceX;
		int areaY = user.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Client tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.areaRecibeY & areaY);
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
	
	public void sendToUserAreaButIndex(Client user, serverPacketID msg, Object... params) {
		int areaX = user.areaPerteneceX;
		int areaY = user.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Client tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.areaRecibeY & areaY);
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
	
	public void sendToNPCArea(Npc npc, serverPacketID msg, Object... params) {
		int areaX = npc.areaPerteneceX;
		int areaY = npc.areaPerteneceY;
		
		for(int i = 0; i < map.getCantUsuarios();i++) {
			Client tempIndex = map.spUser(i);
			
			int tempInt = (tempIndex.areaRecibeX & areaX);
			BitSet gral = new BitSet();
			gral.set(tempInt);
			
			if (gral.cardinality() > 0) {
				
				tempInt = (tempIndex.areaRecibeY & areaY);
				gral.set(tempInt);
				if (gral.cardinality() > 0) {
					
					tempIndex.enviar(msg, params);
				}
				
			}
			
		}
		
		
	}
	


}
