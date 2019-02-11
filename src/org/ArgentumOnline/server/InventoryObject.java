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
package org.ArgentumOnline.server;

/**
 * @author Pablo F. Lillia
 */
public class InventoryObject {

	public short objid = 0;

	public int cant = 0;

	public boolean equipado = false;

	/** Creates a new instance of InventoryObject */
	public InventoryObject() {
		//
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
		// Determina qué objetos son robables.
		AojServer server = AojServer.getInstance();
		ObjectInfo info = server.getInfoObjeto(this.objid);
		return !this.equipado && info.Real == 0 && info.Caos == 0
				&& info.ObjType != Constants.OBJTYPE_LLAVES
				&& info.ObjType != Constants.OBJTYPE_BARCOS;
	}

}
