package org.ArgentumOnline.server.areas;

import java.util.LinkedList;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.Npc;

/**
*
* @author: JAO (Juan Agustín Oliva)
* @userforos: Agushh, Thorkes
* Clase destinada a la gestión de datos de las áreas
* CLASE AÚN NO IMPLEMENTADA, EL OBJETIVO ES MANEJAR LAS ÁREAS EXCLUSIVAMENTE POR IDS Y NO POR TILES.
*/

public class newAreas implements Constants{
	
	int id = 0;
	
	public int[] adyacent = new int[SE + 1];
	
	public LinkedList <Client> user = new LinkedList();
	public LinkedList <Npc> npc = new LinkedList();
	public LinkedList <Short> obj = new LinkedList();
	
	public void addUsers(Client id) {this.user.add(id);}
	public void deleteUser(Client id) {this.user.remove(id);}
	public void addNpcs(Npc id) {this.npc.add(id);}
	public void deleteNpc(Npc id) {this.npc.remove(id);}
	
	public int userSize() {return this.user.size();}
	
	public short getUser(int value) {return this.user.get(value).getId();}
	
	public void setAdyacent(int id, int value) {this.adyacent[id] = value;}
	
	public void addObject(short obj, short x, short y) {
		this.obj.add(obj);
		this.obj.add(x);
		this.obj.add(y);
	}
	
	public void deleteObject(short obj) {
		int i = this.obj.indexOf(obj);
		
		if (i > -1) {
			this.obj.remove(i);
			this.obj.remove(i);
			this.obj.remove(i);
		}
		
	}
	
	public void setArea(int id) {this.id = id;}
	
	public boolean userInArray(Client user) {return this.user.contains(user);}
	public boolean npcInArray(Npc npc) {return this.npc.contains(npc);}
	
	public int getAdyacent(int id) {return this.adyacent[id];}
	
	public int lengthAdyacent() {return this.adyacent.length;}
	
}
