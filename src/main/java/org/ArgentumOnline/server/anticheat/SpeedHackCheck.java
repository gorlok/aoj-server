/**
 * SpeedHackCheck.java
 *
 * Created on 23 de febrero de 2004, 21:32
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
package org.ArgentumOnline.server.anticheat;

/**
 * @author gorlok
 */
public class SpeedHackCheck {
    String name = "";
    long ultimo = 0;
    int tics = 0;
    
    public SpeedHackCheck(String name) {
        this.name = name;
        this.ultimo = getTimeMilsegs();
    }
    
    private long getTimeMilsegs() {
        return (new java.util.Date()).getTime();
    }
    
    public void check()
    throws SpeedHackException {
        this.tics++;
        long ahora = getTimeMilsegs();
        long tiempo = ahora - this.ultimo;
        if (tiempo > 5000.0) { // 5000 miliseg = 5 segundos.
            // Hay SH cuando hay más de 3 pasos/seg.
            if (this.tics > 30.0) {
				throw new SpeedHackException(" ¡¡¡ SPEED HACK !!! tics=" + this.tics + " tipo=" + this.name);
			}
            this.ultimo = ahora;
            this.tics = 0;
        }
    }
}

