/**
 * Reputation.java
 *
 * Created on 23 de febrero de 2004, 21:33
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

