package org.ArgentumOnline.server.util;

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
    
    public static byte r(int color) {
    	return (byte) (color & COLOR_ROJO);
    }
    public static byte g(int color) {
    	return (byte) ((color & COLOR_VERDE) >>2);
    }
    public static byte b(int color) {
    	return (byte) ((color & COLOR_AZUL) >> 4);
    }
    
    public static int rgb(int r, int g, int b) {
    	return r & (g << 2) & (b << 4);
    }

}
