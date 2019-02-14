/**
 * CharStats.java
 *
 * Created on 13 de octubre de 2003, 11:05
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
 * Estadisticas básicas, de salud, golpe y usuarios matados.
 * @author Pablo F. Lillia
 */
public class CharStats implements Constants {
    
    protected CharStats() {
    	// Clase base - abstracta!
    }
    
    int usuariosMatados = 0;
    
    /** Health Points (SALUD): Maximo de salud */
    int MaxHP = 0;
    
    /** Health Points (SALUD): Salud actual */
    int MinHP = 0;
    
    /** Hit Points (GOLPE): Máximo de golpe */
    int MaxHIT = 0;

    /** Hit Points (GOLPE): Golpe actual */
    int MinHIT = 0;

    /** Aumentar máximo de SALUD. */
    public void addMaxHP(int cant) {
        this.MaxHP += cant;
        if (this.MaxHP > STAT_MAXHP) {
			this.MaxHP = STAT_MAXHP;
		}
    }

    /** Aumentar SALUD actual */
    public void addMinHP(int cant) {
        this.MinHP += cant;
        if (this.MinHP > this.MaxHP) {
			this.MinHP = this.MaxHP;
		}
    }
    
    /** Aumentar SALUD actual */
    public void fullHP() {
        this.MinHP = this.MaxHP;
    }
    
    /** Disminuir SALUD actual */
    public void quitarHP(int cant) {
        this.MinHP -= cant;
        if (this.MinHP < 0) {
			this.MinHP = 0;
		}
    }

    /** Aumentar máximo de GOLPE */
    public void addMaxHIT(int cant) {
        this.MaxHIT += cant;
        if (this.MaxHIT > STAT_MAXHIT) {
			this.MaxHIT = STAT_MAXHIT;
		}
    }

    /** Aumentar GOLPE actual */
    public void addMinHIT(int cant) {
        this.MinHIT += cant;
        if (this.MinHIT > STAT_MAXHIT) {
			this.MinHIT = STAT_MAXHIT;
		}
    }
    
    /** Incrementar estadística de usuarios matados */
    public void incUsuariosMatados() {
        this.usuariosMatados++;
    }
    
}
