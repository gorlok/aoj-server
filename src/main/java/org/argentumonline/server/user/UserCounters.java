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
package org.argentumonline.server.user;

import static org.argentumonline.server.Constants.IntervaloUserPuedeAtacar;
import static org.argentumonline.server.Constants.IntervaloUserPuedeCastear;
import static org.argentumonline.server.Constants.IntervaloUserPuedeTrabajar;
import static org.argentumonline.server.Constants.IntervaloUserPuedeUsar;

import org.argentumonline.server.map.MapPos;

/**
 * @author gorlok
 */
public class UserCounters {
    public long IdleCount = 0;
    int AttackCounter = 0;
    int HPCounter = 0;
    int STACounter = 0;
    int Frio = 0;
    int foodCounter = 0; // contador del intervalo de comida
    int drinkCounter = 0; // contador del inveralo de bebida
    int Veneno = 0;
    public int Paralisis = 0;
    public int Ceguera = 0;
    public int Estupidez = 0;
    public int Invisibilidad = 0;
    public long piqueteSeconds = 0;
	public long Pena = 0;
    public boolean Saliendo = false;
    public short SalirCounter = 0; // segundos para salir.
    MapPos SendMapCounter = MapPos.empty();
    int Pingeo = 0;
    long tInicioMeditar = 0;
    long tUltimoHechizo = 0;
    
    // Timers 
    long TimerLanzarSpell = 0;
    long TimerPuedeAtacar = 0;
    long TimerPuedeTrabajar = 0;
    long TimerUsar = 0;
    
    public void resetIdleCount() {
    	this.IdleCount = 0;
    }
    
	// Las siguientes funciones devuelven TRUE o FALSE si el intervalo
	// permite hacerlo. Si devuelve TRUE, setean automaticamente el
	// timer para que no se pueda hacer la accion hasta el nuevo ciclo.

	/** INTERVALO DE CASTING DE HECHIZOS */
	public boolean intervaloPermiteLanzarSpell() {
		long time = System.currentTimeMillis();
		if ((time - TimerLanzarSpell) >= IntervaloUserPuedeCastear) {
			TimerLanzarSpell = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE ATAQUE CUERPO A CUERPO */
	public boolean intervaloPermiteAtacar() {
		long time = System.currentTimeMillis();
		if ((time - TimerPuedeAtacar) >= IntervaloUserPuedeAtacar) {
			TimerPuedeAtacar = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE TRABAJO */
	public boolean intervaloPermiteTrabajar() {
		long time = System.currentTimeMillis();
		if ((time - TimerPuedeTrabajar) >= IntervaloUserPuedeTrabajar) {
			TimerPuedeTrabajar = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE USAR OBJETOS */
	public boolean intervaloPermiteUsar() {
		long time = System.currentTimeMillis();
		if ((time - TimerUsar) >= IntervaloUserPuedeUsar) {
			TimerUsar = time;
			return true;
		}
		return false;
	}

    
}

