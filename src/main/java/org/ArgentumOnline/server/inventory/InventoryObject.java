/**
 * InventoryObject.java
 *
 * Created on 29 de septiembre de 2003, 21:31
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
package org.ArgentumOnline.server.inventory;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.ObjectInfo;

/**
 * @author gorlok
 */
public class InventoryObject {

	public short objid = 0;

	public int cant = 0;

	public boolean equipado = false;

	public InventoryObject() {
	}

	public InventoryObject(short objid, int cant) {
		this.objid = objid;
		this.cant = cant;
		this.equipado = false;
	}

	public InventoryObject(short objid, int cant, boolean equipado) {
		this.objid = objid;
		this.cant = cant;
		this.equipado = equipado;
	}

	public boolean estaVacio() {
		return (this.objid == 0);
	}

	public boolean estaLleno() {
		return (this.cant == Constants.MAX_INVENTORY_OBJS);
	}

	public int espacioLibre() {
		return Constants.MAX_INVENTORY_OBJS - this.cant;
	}

	public boolean esRobable() {
		var storage = GameServer.instance().getObjectInfoStorage();
		ObjectInfo info = storage.getInfoObjeto(this.objid);

		return !this.equipado &&
				!info.esReal() &&
				!info.esCaos() &&
				info.objType != ObjType.Llaves &&
				info.objType != ObjType.Barcos;
	}

}
