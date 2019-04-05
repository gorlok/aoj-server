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

import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;

/**
 * @author gorlok
 */
public class MapObject {
	
	public short obj_ind;
	public int obj_cant;
	public byte x;
	public byte y;

	/** Creates a new instance of Objeto */
	public MapObject(short objid, int cant, byte x, byte y) {
		this.obj_ind = objid;
		this.obj_cant = cant;
		this.x = x;
		this.y = y;
	}

	public ObjectInfo objInfo() {
		return GameServer.instance().getObjectInfoStorage().getInfoObjeto(this.obj_ind); // FIXME
	}

	@Override
	public String toString() {
		return "obj(id=" + this.obj_ind + 
				",cnt=" + this.obj_cant + 
				",x=" + this.x + ",y=" + this.y	+ ")";
	}
}
