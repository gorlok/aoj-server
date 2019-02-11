/**
 * Objeto.java
 *
 * Created on 28 de septiembre de 2003, 23:19
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
package org.ArgentumOnline.server;

/**
 * @author Pablo F. Lillia
 */
public class MapObject {
	short obj_ind;

	int obj_cant;

	short x;

	short y;

	/** Creates a new instance of Objeto */
	public MapObject(short objid, int cant, short x, short y) {
		this.obj_ind = objid;
		this.obj_cant = cant;
		this.x = x;
		this.y = y;
	}

	public ObjectInfo getInfo() {
		return AojServer.getInstance().getInfoObjeto(this.obj_ind);
	}

	@Override
	public String toString() {
		return "obj(id=" + this.obj_ind + ",cnt=" + this.obj_cant + ",x=" + this.x + ",y=" + this.y
				+ ")";
	}
}
