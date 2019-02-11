/**
 * UserCounters.java
 *
 * Created on 23 de febrero de 2004, 21:35
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
public class UserCounters {
    long IdleCount = 0;
    int AttackCounter = 0;
    int HPCounter = 0;
    int STACounter = 0;
    int Frio = 0;
    int COMCounter = 0;
    int AGUACounter = 0;
    int Veneno = 0;
    int Paralisis = 0;
    int Ceguera = 0;
    int Estupidez = 0;
    int Invisibilidad = 0;
    long PiqueteC = 0;
    long Pena = 0;
    WorldPos SendMapCounter = new WorldPos();
    int Pasos = 0;
    boolean Saliendo = false;
    short SalirCounter = 0; // segundos para salir.
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
}

