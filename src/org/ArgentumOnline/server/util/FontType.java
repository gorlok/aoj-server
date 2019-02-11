/**
 * FontType.java
 *
 * Created on 21 de septiembre de 2003, 20:23
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
package org.ArgentumOnline.server.util;

/**
 * @author Pablo F. Lillia
 */
public class FontType {
    
	public int ind;
    public int r;
    public int g;
    public int b;
    public int bold;
    public int italic;
    
    /** Creates a new instance of FontType */
    public FontType(int r, int g, int b, int bold, int italic) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.bold = bold;
        this.italic = italic;
    }
    
    public final static FontType TALK       = new FontType(255, 255, 255, 0, 0);
    public final static FontType FIGHT      = new FontType(255, 0, 0, 1, 0);
    public final static FontType WARNING    = new FontType(32, 51, 223, 1, 1);
    public final static FontType INFO       = new FontType(65, 190, 156, 0, 0);
    public final static FontType INFO_B     = new FontType(65, 190, 156, 1, 0);
    public final static FontType VENENO     = new FontType(0, 255, 0, 0, 0);
    public final static FontType GUILD      = new FontType(255, 255, 255, 1, 0);
    public final static FontType WELLCOME   = new FontType(89, 43, 213, 1, 0);
    public final static FontType USUARIO    = new FontType(0, 0, 255, 1, 0);
    public final static FontType DEBUG      = new FontType(240, 255, 0, 0, 0);    
    public final static FontType TALKGM     = new FontType(255, 255, 255, 0, 1);
    //public final static FontType SERVERINFO = new FontType(255, 255, 255, 1, 0);
    // Mejoramos los mensajes del servidor
    public final static FontType SERVER = new FontType(0, 185, 0, 0, 0);
    public final static FontType GUILDMSG = new FontType(228, 199, 27, 0, 0);
    
    public final static FontType TAG_GOD = new FontType(255, 255, 255, 1, 0);
    public final static FontType TAG_SEMIGOD = new FontType(0, 185, 0, 1, 0);
    public final static FontType TAG_CONSEJERO = new FontType(0, 185, 0, 1, 0);
    public final static FontType TAG_CRIMINAL = new FontType(255, 0, 0, 1, 0);
    public final static FontType TAG_CIUDADANO = new FontType(0, 0, 200, 1, 0);
    
    @Override
	public String toString() {
        return "~" + this.r + "~" + this.g + "~" + this.b + "~" + this.bold + "~" + this.italic;
    }
}
