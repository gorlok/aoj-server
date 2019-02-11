/**
 * PathFinding.java
 *
 * Created on 13 de octubre de 2003, 18:54
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

import java.util.Vector;

import misc.astar.AStar;
import misc.astar.Constants;
import misc.astar.Location;
import misc.astar.Node;

/**
 * @author Pablo F. Lillia
 */
public class PathFinding {
    
    static int current = Constants.NOTHING, startI = Constants.NOTHING, startJ = Constants.NOTHING, finishI = Constants.NOTHING,
	finishJ = Constants.NOTHING;

	int[][] grid = new int[100][100];
	static int[] costs = new int[Constants.NUMCOLORS];
	
	private void init() {
	    costs[Constants.EMPTY] = 1;
	    costs[Constants.TERRAIN1] = 5;
	    costs[Constants.TERRAIN2] = 10;
	    costs[Constants.TERRAIN3] = 15;
	    costs[Constants.SOLID] = Constants.NOTHING;
	    costs[Constants.START] = 1;
	    costs[Constants.FINISH] = 1;
	}


    /** Creates a new instance of PathFinding */
    public PathFinding() {
    	init();
    }
    
    public Vector<Node> seekPath(Npc npc) {
    	AojServer server = AojServer.getInstance();
    	short m = npc.getPos().mapa;
    	Map mapa = server.getMapa(m);
    	WorldPos start = npc.getPos();
    	WorldPos end = npc.m_pfinfo.m_targetPos;
    	for (short x = 1; x <= 100; x++) {
			for (short y = 1; y <= 100; y++) {
    			if (mapa.isLegalPosNPC(new WorldPos(m, x, y), npc.esAguaValida())) {
					this.grid[x-1][y-1] = Constants.EMPTY;
				} else {
					this.grid[x-1][y-1] = Constants.SOLID;
				}
    		}
		}
    	this.grid[start.x-1][start.y-1] = Constants.START;
    	this.grid[end.x-1][end.y-1] = Constants.FINISH;
    	Location loc_start = new Location(start.x, start.y);
    	Location loc_end = new Location(end.x, end.y);
    	AStar astar = new AStar(this.grid, costs, loc_start, loc_end);
    	return astar.AStarSearch(null);
    }
    
}
