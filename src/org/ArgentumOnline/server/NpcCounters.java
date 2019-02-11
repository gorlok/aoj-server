/**
 * NpcCounters.java
 *
 * Created on 23 de febrero de 2004, 21:27
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
public class NpcCounters {
    // NPC_CONTADOR contadores;
    public short Paralisis = 0;
    public int   TiempoExistencia = 0;
    
    public void reset() {
        this.Paralisis = 0;
        this.TiempoExistencia = 0;
    }
}

