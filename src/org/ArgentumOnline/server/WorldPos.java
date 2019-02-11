/**
 * WorldPos.java
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
package org.ArgentumOnline.server;

/**
 *
 * @author  pablo
 */
public class WorldPos extends MapPos {
    
    public short mapa = 0;
    
    /** Creates a new instance of WorldPos */
    public WorldPos() {
        super();
        this.mapa = 0;
    }
    
    public WorldPos(short mapa, short x, short y) {
        super(x, y);
        this.mapa = mapa;
    }
    
    public WorldPos(WorldPos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.mapa = pos.mapa;
    }
    
    @Override
	public String toString() {
        return "(mapa=" + this.mapa + ",x=" + this.x + ",y=" + this.y + ")";
    }
    
    public void mirarDir(int dir) {
        // HeadToPos
        // *****************************************************************
        // Toma una posicion y se mueve hacia donde esta perfilado
        // *****************************************************************
        switch (dir) {
            case DIR_NORTH:
                this.y--;
                break;
            case DIR_EAST:
                this.x++;
                break;
            case DIR_SOUTH:
                this.y++;
                break;
            case DIR_WEST:
                this.x--;
                break;
            default:
                return;
        }
    }

    public short findDirection(WorldPos target) {
        // *****************************************************************
        // Devuelve la direccion en la cual el target se encuentra
        // desde pos, 0 si la direc es igual
        // *****************************************************************
        int dx = this.x - target.x;
        int dy = this.y - target.y;

        // NE
        if (dx < 0 && dy > 0) {
			return DIR_NORTH;
		}

        // NW
        if (dx > 0 && dy > 0) {
			return DIR_WEST;
		}

        // SW
        if (dx > 0 && dy < 0) {
			return DIR_WEST;
		}

        // SE
        if (dx < 0 && dy < 0) {
			return DIR_SOUTH;
		}
        
        // Sur
        if (dx == 0 && dy < 0) {
			return DIR_SOUTH;
		}

        // norte
        if (dx == 0 && dy > 0) {
			return DIR_NORTH;
		}

        // oeste
        if (dx > 0 && dy == 0) {
			return DIR_WEST;
		}

        // este
        if (dx < 0 && dy == 0) {
			return DIR_EAST;
		}

        // misma
        if (dx == 0 && dy == 0) {
			return DIR_NONE;
		}
        
        return DIR_NONE;
    }    
    
    public int distancia(WorldPos pos) {
        // Encuentra la distancia entre dos WorldPos
        return Math.abs(this.x - pos.x) + Math.abs(this.y - pos.y) + (Math.abs(this.mapa - pos.mapa) * 100);
    }
    
}
