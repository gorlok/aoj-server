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
package org.argentumonline.server;

/**
 * Estadisticas básicas, de salud y golpe.
 * @author gorlok
 */
public abstract class AbstractCharStats implements Constants {
    
    /** Health Points (SALUD): Salud actual */
    public int MinHP = 0;
    /** Health Points (SALUD): Maximo de salud */
    public int MaxHP = 0;
    
    /** Hit Points (GOLPE): Golpe actual */
    public int MinHIT = 0;
    /** Hit Points (GOLPE): Máximo de golpe */
    public int MaxHIT = 0;

    /** Aumentar máximo de SALUD. */
    public void addMaxHP(int cant) {
        this.MaxHP += cant;
        if (this.MaxHP > STAT_MAXHP) {
			this.MaxHP = STAT_MAXHP;
		}
    }

    /** Aumentar SALUD actual */
    public void addHP(int cant) {
        this.MinHP += cant;
        if (this.MinHP > this.MaxHP) {
			this.MinHP = this.MaxHP;
		}
    }
    
    /** Aumentar SALUD actual */
    public void restoreFullHP() {
        this.MinHP = this.MaxHP;
    }
    
    /** Disminuir SALUD actual */
    public void removeHP(int cant) {
        this.MinHP -= cant;
        if (this.MinHP < 0) {
			this.MinHP = 0;
		}
    }

    /** Aumentar máximo de GOLPE */
    public void addMaxHIT(int cant, int level) {
        this.MaxHIT += cant;
        
        int max = (level < 36) ? Constants.STAT_MAXHIT_UNDER36 : Constants.STAT_MAXHIT_OVER36;

        if (this.MaxHIT > max) {
			this.MaxHIT = max;
		}
    }

    /** Aumentar GOLPE actual */
    public void addMinHIT(int cant, int level) {
        this.MinHIT += cant;
        
        int max = (level < 36) ? Constants.STAT_MAXHIT_UNDER36 : Constants.STAT_MAXHIT_OVER36;
        
        if (this.MinHIT > max) {
			this.MinHIT = max;
		}
    }
    
}
