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
package org.argentumonline.server.inventory;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjType;
import org.argentumonline.server.ObjectInfo;

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
		ObjectInfo info = objInfo();

		return !this.equipado &&
				!info.esReal() &&
				!info.esCaos() &&
				info.objType != ObjType.Llaves &&
				info.objType != ObjType.Barcos;
	}

	public ObjectInfo objInfo() {
		var storage = GameServer.instance().getObjectInfoStorage();
		ObjectInfo info = storage.getInfoObjeto(this.objid);
		return info;
	}

}
