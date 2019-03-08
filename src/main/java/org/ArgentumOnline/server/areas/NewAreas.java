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

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.user.Player;

/**
 *
 * @author: JAO (Juan Agustín Oliva)
 * @userforos: Agushh, Thorkes Clase destinada a la gestión de datos de las
 *             áreas CLASE AÚN NO IMPLEMENTADA, EL OBJETIVO ES MANEJAR LAS ÁREAS
 *             EXCLUSIVAMENTE POR IDS Y NO POR TILES.
 */
public class NewAreas implements Constants {
	
	final static int NORTH = 0;
	final static int SOUTH = 1;
	final static int EAST = 2;
	final static int WEST = 3;
	final static int SW = 4;
	final static int NE = 5;
	final static int NW = 6;
	final static int SE = 7;

	int id = 0;

	public int[] adyacent = new int[SE + 1];

	public List<Player> user = new LinkedList<Player>();
	public List<Npc> npc = new LinkedList<Npc>();
	public List<Short> obj = new LinkedList<Short>();

	public void addUsers(Player id) {
		this.user.add(id);
	}

	public void deleteUser(Player id) {
		this.user.remove(id);
	}

	public void addNpcs(Npc id) {
		this.npc.add(id);
	}

	public void deleteNpc(Npc id) {
		this.npc.remove(id);
	}

	public int userSize() {
		return this.user.size();
	}

	public short getUser(int value) {
		return this.user.get(value).getId();
	}

	public void setAdyacent(int id, int value) {
		this.adyacent[id] = value;
	}

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

	public void setArea(int id) {
		this.id = id;
	}

	public boolean userInArray(Player user) {
		return this.user.contains(user);
	}

	public boolean npcInArray(Npc npc) {
		return this.npc.contains(npc);
	}

	public int getAdyacent(int id) {
		return this.adyacent[id];
	}

	public int lengthAdyacent() {
		return this.adyacent.length;
	}

}
