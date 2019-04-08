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
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.user.User;

public class PathFinding {
	
	private int[][] grid = new int[100][100];
	
	private static int[] costs = new int[Constants.NUMCOLORS];
	static {
	    costs[Constants.EMPTY] = 1;
	    costs[Constants.TERRAIN1] = 5;
	    costs[Constants.TERRAIN2] = 10;
	    costs[Constants.TERRAIN3] = 15;
	    costs[Constants.SOLID] = Constants.NOTHING;
	    costs[Constants.START] = 1;
	    costs[Constants.FINISH] = 1;
	}
	
	private Npc npc;
	
	private List<Node> current_path = null;
	private int current_step = 0;

	private MapPos targetPos;
	private User targetUser;
	
	public PathFinding(Npc npc, MapPos targetPos, User targetUser) {
		this.npc = npc;
		this.targetPos = targetPos;
		this.targetUser = targetUser;
	}
	
    public List<Node> seekPath(Npc npc) {
    	GameServer server = GameServer.instance();
    	short m = npc.pos().map;
    	Map mapa = server.getMap(m);
    	MapPos start = npc.pos();
    	MapPos end = this.targetPos;
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
	
	public MapPos getTargetPos() {
		return targetPos;
	}
	
	public User getTargetUser() {
		return targetUser;
	}
	
	public void reset() {
        this.current_step = 0;
        this.current_path = null;
	}
	
	/** Returns if the npc has arrived to the end of its path */
    private boolean isPathEnd() {
	    return this.current_step >= this.current_path.size() - 1;
	}
    
    /** Returns True if there is an user adjacent to the npc position. */
    private boolean userNear() {
        return this.npc.pos().distance(this.targetPos) <= 1;
    }

    /** Returns true if we have to seek a new path */
	private boolean reCalculatePath() {
        if (this.current_path == null) {
        	return true;
        } else if (!userNear() && this.current_step == this.current_path.size()) {
        	return true;
        }
        return false;
    }

	/** Moves the npc. */
    private void followPath() {
        this.current_step++;
        org.argentumonline.server.aStar.Node node = this.current_path.get(this.current_step);
        org.argentumonline.server.aStar.Location loc = node.location;
        MapPos pos = MapPos.mxy(this.npc.pos().map, (short)loc.x, (short)loc.y);
        Heading dir = this.npc.pos().findDirection(pos);

        this.npc.move(dir);
    }

    public void aiPathFinding() {
        if (reCalculatePath()) {
            this.npc.calculatePath();
            // Existe el camino?
            if (this.current_path == null) { // Si no existe nos movemos al azar
            	// Move randomly
            	this.npc.moverAlAzar();
            }
         } else {
            if (!isPathEnd()) {
            	followPath();
            } else {
            	this.current_path = null;
            	this.current_step = 0;
            }
          }
    }

	public void init() {
    	this.current_path = seekPath(npc);
	}

}