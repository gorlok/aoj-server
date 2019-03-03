/**
 * Inventory.java
 *
 * Created on 15 de septiembre de 2003, 23:34
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

import java.util.Arrays;
import java.util.Iterator;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;

/**
 * @author gorlok
 */
public class Inventory implements Iterable<InventoryObject> {
    
    protected InventoryObject objs[] = null;
    
    GameServer server;
    
    /** Creates a new instance of Inventory */
    public Inventory(GameServer server, int size) {
        this.server = server;

        this.objs = new InventoryObject[size];
        reset();
    }
	
	protected ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
    
    public boolean isSlotValid(int slot) {
    	return slot >= 1 && slot <= size();
    }
    
    private void reset() {
        for (int i = 0; i < this.objs.length; i++) {
			this.objs[i] = new InventoryObject();
		}    	
    }
    
    public int size() {
        return this.objs.length;
    }
    
    /**
     * Devuelve un objeto del inventario, según slot (posición) dado
     * @param slot debe ser entre 1 y size()
     * @return objeto del inventario, o null si no existe
     */
    public InventoryObject getObjeto(int slot) {
        return (isSlotValid(slot)) ? this.objs[slot-1] : null;
    }
    
    public void setObjeto(int slot, InventoryObject objInv) {
        this.objs[slot-1] = objInv;
    }

    public void clear() {
        reset();
    }
    
    public int getSlotLibre() {
        for (int i = 0; i < this.objs.length; i++) {
            if (this.objs[i] == null) {
				return i+1;
			}
            if (this.objs[i].objid == 0) {
				return i+1;
			}
        }
        return 0;
    }
    
    public boolean isEmpty() {
        for (InventoryObject element : this.objs) {
            if (element != null && element.objid > 0) {
				return false;
			}
        }
        return true;
    }
    
    public int getCantObjs() {
        int cant = 0;
        for (InventoryObject element : this.objs) {
            if (element != null && element.objid > 0) {
				cant++;
			}
        }
        return cant;
    }

	@Override
	public Iterator<InventoryObject> iterator() {
		return Arrays.asList(this.objs).iterator();
	}
    
}
