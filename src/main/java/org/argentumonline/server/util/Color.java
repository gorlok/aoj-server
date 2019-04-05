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
package org.argentumonline.server.util;

public class Color {
	
    public final static int COLOR_NEGRO    = 0x000000;
    public final static int COLOR_BLANCO   = 0xffffff;
    public final static int COLOR_ROJO     = 0x0000ff;
    public final static int COLOR_VERDE    = 0x00ff00;
    public final static int COLOR_AZUL     = 0xff0000;
    public final static int COLOR_AZUL2    = 0xff0a0a;
    public final static int COLOR_MAGENTA  = 0xff00ff;
    public final static int COLOR_CYAN     = 0xffff00;
    public final static int COLOR_AMARILLO = 0x00ffff;
    
    // The color of chats over head of dead characters.
    public final static int CHAT_COLOR_DEAD_CHAR = 0xc0c0c0;

    // The color of yells made by any kind of game administrator.
    public final static int CHAT_COLOR_GM_YELL = 0x0f82ff;    
    
    public static byte r(int color) {
    	return (byte) (color & COLOR_ROJO);
    }
    public static byte g(int color) {
    	return (byte) ((color & COLOR_VERDE) >> 8);
    }
    public static byte b(int color) {
    	return (byte) ((color & COLOR_AZUL) >> 16);
    }
    
    public static int rgb(int r, int g, int b) {
    	return r + (g << 8) + (b << 16);
    }

}
