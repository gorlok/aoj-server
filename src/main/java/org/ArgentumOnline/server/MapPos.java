/**
 * MapPos.java
 *
 * Created on 14 de septiembre de 2003, 20:42
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
 * @author gorlok
 */
public class MapPos implements Constants {

	public short x;
	public short y;
	
	public static MapPos xy(int x, int y) {
		return new MapPos((short)x, (short)y);
	}

	public MapPos(short x, short y) {
		this.x = x;
		this.y = y;
	}

	public boolean isValid() {
		return (this.x > 0) && (this.y > 0) && (this.x <= MAPA_ANCHO) && (this.y <= MAPA_ALTO);
	}

	public static boolean isValid(int x, int y) {
		return (x > 0) && (y > 0) && (x <= MAPA_ANCHO) && (y <= MAPA_ALTO);
	}

	public boolean inRangoVision(MapPos pos) {
		return inRangoVision(pos.x, pos.y);
	}

	public boolean inRangoVision(int xx, int yy) {
		return Math.abs(this.x - xx) < MinXBorder &&
				Math.abs(this.y - yy) < MinYBorder;
	}

}
