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
package org.ArgentumOnline.server.user;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapCell.Trigger;
import org.ArgentumOnline.server.protocol.FameResponse;
import org.ArgentumOnline.server.protocol.NobilityLostResponse;

/**
 * @author gorlok
 */
public class Reputation {
    // Fama del usuario

	final static int MAX_REP = 6000000;
	
    // Puntos positivos:
    double burguesRep    = 0.0;
    double nobleRep      = 0.0;
    double plebeRep      = 0.0;
    
    // Puntos negativos:
    double ladronRep     = 0.0;
    double bandidoRep    = 0.0;
    double asesinoRep    = 0.0;
    
    public double getPromedio() {
        return ((this.burguesRep + this.nobleRep + this.plebeRep) - (this.ladronRep + this.asesinoRep + this.bandidoRep)) / 6;
    }
    
    public boolean esCriminal() {
        return getPromedio() < 0;
    }
    
    public boolean esIntachable() {
    	return this.ladronRep == 0 && this.bandidoRep == 0 && this.asesinoRep == 0; 
    }
    
    public void perdonar() {
        this.ladronRep = 0;
        this.bandidoRep = 0;
        this.asesinoRep = 0;
        incPlebe(Constants.vlAsalto);    
    }
    
    public void condenar() {
        this.burguesRep = 0;
        this.nobleRep   = 0;
        this.plebeRep   = 0;
        incBandido(Constants.vlAsalto);    
    }
    
    public void incNoble(double val) {
        this.nobleRep += val;
        if (this.nobleRep > MAX_REP) {
        	this.nobleRep = MAX_REP;
        }
    }
    
    public void decNoble(double val) {
        if (this.nobleRep > 0) {
            this.nobleRep -= val;
            if (this.nobleRep < 0) {
				this.nobleRep = 0;
			}
        }
    }
    
    public void incBurgues(double val) {
        this.burguesRep += val;
        if (this.burguesRep > MAX_REP) {
        	this.burguesRep = MAX_REP;
        }
    }
    
    public void decBurgues(double val) {
        if (this.burguesRep > 0) {
            this.burguesRep -= val;
            if (this.burguesRep < 0) {
				this.burguesRep = 0;
			}
        }
    }
    
    public void incPlebe(double val) {
        this.plebeRep += val;
        if (this.plebeRep > MAX_REP) {
        	this.plebeRep = MAX_REP;
        }
    }
    
    public void decPlebe(double val) {
        if (this.plebeRep > 0) {
            this.plebeRep -= val;
            if (this.plebeRep < 0) {
				this.plebeRep = 0;
			}
        }
    }
    
    public void incLandron(double val) {
        this.ladronRep += val;
        if (this.ladronRep > MAX_REP) {
        	this.ladronRep = MAX_REP;
        }
    }
    
    public void decLadron(double val) {
        if (this.ladronRep > 0) {
            this.ladronRep -= val;
            if (this.ladronRep < 0) {
				this.ladronRep = 0;
			}
        }
    }
    
    public void incBandido(double val) {
        this.bandidoRep += val;
        if (this.bandidoRep > MAX_REP) {
        	this.bandidoRep = MAX_REP;
        }
    }
    
    public void decBandido(double val) {
        if (this.bandidoRep > 0) {
            this.bandidoRep -= val;
            if (this.bandidoRep < 0) {
				this.bandidoRep = 0;
			}
        }
    }
    
    public void incAsesino(double val) {
        this.asesinoRep += val;
        if (this.asesinoRep > MAX_REP) {
        	this.asesinoRep = MAX_REP;
        }
    }
    
    public void decAsesino(double val) {
        if (this.asesinoRep > 0) {
            this.asesinoRep -= val;
            if (this.asesinoRep < 0) {
				this.asesinoRep = 0;
			}
        }
    }
    
    public void disminuyeNoblezaAumentaBandido(Player player, double noblePts, double bandidoPts) {
    	// DisNobAuBan
    	// disminuye la nobleza NoblePts puntos y aumenta el bandido BandidoPts puntos
        boolean eraCriminal = player.isCriminal();
        
        // Si estamos en la arena no hacemos nada
        if (player.isAtDuelArena()) {
        	return;
        }
        
        if (!player.isGM() || player.isCounselor()) {
        	// pierdo nobleza...
        	decNoble(noblePts);
			// gano bandido...
        	incBandido(bandidoPts);

        	player.sendPacket(new NobilityLostResponse());
        	
        	if (player.isCriminal() && player.isRoyalArmy()) {
        		player.userFaction().expulsarFaccionReal();
        	}
        }
        
        if ( !eraCriminal && player.isCriminal()) {
        	player.refreshUpdateTagAndStatus();
        }
    }
    
    public FameResponse createFameResponse() {
    	return new FameResponse(
				(int) this.asesinoRep, 
				(int) this.bandidoRep,
				(int) this.burguesRep, 
				(int) this.ladronRep, 
				(int) this.nobleRep,
				(int) this.plebeRep, 
				(int) this.getPromedio());    	
    }
}

