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

package org.argentumonline.server.guilds;

import java.util.HashMap;

/**
 * BallotBox is used for guild leader votation.
 * @author Gorlok
 *
 */
public class BallotBox {
	
	public BallotBox() {
		//
	}
	
	private HashMap<String,Integer> votes = new HashMap<String,Integer>();
	
	/**
	 * Empty the box.
	 */
	public void clear() {
		this.votes.clear();		
	}
	
	public void addVote(String vote) {
		vote = vote.toUpperCase();
        if (this.votes.containsKey(vote)) {
        	this.votes.put(vote, this.votes.get(vote) + 1);
        } else {
        	this.votes.put(vote, 1);
        }
	}
	
	public String getWinner() {
        long winnerVotes = 0;
        String winner = "";
        for (String member: this.votes.keySet()) {
        	if (this.votes.get(member) > winnerVotes) {
        		winnerVotes = this.votes.get(member);
        		winner = member;
        	} else {
            	if (this.votes.get(member) == winnerVotes) {
            		winner = "";
            	}
        	}
        }
		return winner;
	}
}
