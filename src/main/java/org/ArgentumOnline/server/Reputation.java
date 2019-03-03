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
package org.ArgentumOnline.server;

/**
 * @author gorlok
 */
public class Reputation {
    // Fama del usuario
    
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
    }
    
    public void decAsesino(double val) {
        if (this.asesinoRep > 0) {
            this.asesinoRep -= val;
            if (this.asesinoRep < 0) {
				this.asesinoRep = 0;
			}
        }
    }
}

