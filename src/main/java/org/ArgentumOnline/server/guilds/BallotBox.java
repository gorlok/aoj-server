/**
 * BallotBox.java
 *
 * Created on 25/may/2007
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

package org.ArgentumOnline.server.guilds;

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
