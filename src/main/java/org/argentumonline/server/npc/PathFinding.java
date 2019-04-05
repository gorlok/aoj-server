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
package org.argentumonline.server.npc;

import java.util.List;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.aStar.AStar;
import org.argentumonline.server.aStar.Constants;
import org.argentumonline.server.aStar.Location;
import org.argentumonline.server.aStar.Node;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;

/**
 * @author gorlok
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
    
    public List<Node> seekPath(Npc npc) {
    	GameServer server = GameServer.instance();
    	short m = npc.pos().map;
    	Map mapa = server.getMap(m);
    	MapPos start = npc.pos();
    	MapPos end = npc.m_pfinfo.m_targetPos;
    	for (short x = 1; x <= 100; x++) {
			for (short y = 1; y <= 100; y++) {
    			if (mapa.isLegalPosNPC(MapPos.mxy(m, x, y), npc.isWaterValid())) {
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
