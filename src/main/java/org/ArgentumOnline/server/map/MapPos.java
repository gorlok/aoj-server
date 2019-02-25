/**
 * MapPos.java
 *
 * Created on 14 de septiembre de 2003, 21:18
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
package org.ArgentumOnline.server.map;

import org.ArgentumOnline.server.Pos;

/**
 * World location at a map
 * @author gorlok
 */
public class MapPos extends Pos {
	
	public enum Heading {
	    NONE,
	    NORTH,
	    EAST,
	    SOUTH,
	    WEST;
	    
		// cache values() because performance
		private static final Heading[] values = Heading.values();
		
	    public static Heading value(int heading) {
	    	return values[heading];
	    }
	}

	public short map = 0;
	
	public static MapPos empty() {
		return mxy(0, 0, 0);
	}
	
	public static MapPos mxy(int map, int x, int y) {
		return new MapPos((short) map, (short) x, (short) y);
	}
	
	public MapPos copy() {
		return MapPos.mxy(this.map, this.x, this.y);
	}
	
	public void set(int map, int x, int y) {
		this.map = (short) map;
		this.x = (byte)x;
		this.y = (byte)y;
	}

	private MapPos(short mapa, short x, short y) {
		super(x, y);
		this.map = mapa;
	}

	@Override
	public String toString() {
		return "(map=" + this.map + ",x=" + this.x + ",y=" + this.y + ")";
	}

	/**
	 * Traslada la posición una unidad en la dirección indicada.
	 * VB: HeadToPos
	 * @param dir dirección del movimiento
	 * @return the same instance (fluent api)
	 */
	public MapPos moveToDir(Heading dir) {
		switch (dir) {
		case NONE:
			// don't move
			break;
		case NORTH:
			this.y--;
			break;
		case EAST:
			this.x++;
			break;
		case SOUTH:
			this.y++;
			break;
		case WEST:
			this.x--;
			break;
		}
		return this;
	}

	/**
	 * Calcula la dirección en la que se encuentra el objetivo.
	 * Devuelve 0 si las posiciones son iguales.
	 * @param target es la posición del objetivo
	 * @return dirección del objetivo
	 */
	public Heading findDirection(MapPos target) {
		int dx = this.x - target.x;
		int dy = this.y - target.y;

		// NE
		if (dx < 0 && dy > 0) {
			return Heading.NORTH;
		}

		// NW
		if (dx > 0 && dy > 0) {
			return Heading.WEST;
		}

		// SW
		if (dx > 0 && dy < 0) {
			return Heading.WEST;
		}

		// SE
		if (dx < 0 && dy < 0) {
			return Heading.SOUTH;
		}

		// Sur
		if (dx == 0 && dy < 0) {
			return Heading.SOUTH;
		}

		// norte
		if (dx == 0 && dy > 0) {
			return Heading.NORTH;
		}

		// oeste
		if (dx > 0 && dy == 0) {
			return Heading.WEST;
		}

		// este
		if (dx < 0 && dy == 0) {
			return Heading.EAST;
		}

		// misma
		if (dx == 0 && dy == 0) {
			return Heading.NONE;
		}

		return Heading.NONE;
	}

	/**
	 * Calcula la distancia entre dos posiciones
	 * @param wp otra WorldPos
	 * @return distancia en unidades
	 */
	public int distance(MapPos wp) {
		return Math.abs(this.x - wp.x) + Math.abs(this.y - wp.y) + (Math.abs(this.map - wp.map) * 100);
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o)
	        return true;
	    if (o == null)
	        return false;
	    if ( !(o instanceof MapPos) )
	        return false;
	    MapPos wp = (MapPos)o;
	    return this.x == wp.x &&
	    		this.y == wp.y &&
	    		this.map == wp.map;
	}

}
