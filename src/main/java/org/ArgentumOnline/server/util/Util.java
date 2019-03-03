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
package org.ArgentumOnline.server.util;

/**
 * @author gorlok
 */
public class Util {

	private Util() {}

    public static long millis() {
    	// 1 s  = 1.000 ms
    	// 1 ms = 1.000 us
    	// 1 us = 1.000 ns
    	// 1 ms = 1.000.000 ns
        return System.nanoTime() / 1000000;
    }
	
	public static boolean asciiValidos(String str) {
		byte[] bytes = str.toLowerCase().getBytes();
		for (byte b : bytes) {
			if ((b < 97 || b > 122) 
					&& b != (byte)255
					&& b != 32) {
						return false;
			}
		}
		return true;
	}

	public static short leShort(short n) {
		return (short) (((n & 0xff) << 8) | (((n & 0xff00) >> 8) & 0xff));
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		// Encuentra la distancia entre dos puntos
		return Math.sqrt(((y1 - y2) * (y1 - y2)) + ((x1 - x2) * (x1 - x2)));
	}

	public static int porcentaje(long total, long porc) {
		return (int) Math.round((total * porc) / 100.0);
	}

	public static int Azar(int min, int max) {
		int valor = (int) ((Math.random() * (max - min + 1)) + min);
		return (valor < min) ? min : valor;
	}

	public static short Min(short a, short b) {
		return a < b ? a : b;
	}

	public static int Min(int a, int b) {
		return a < b ? a : b;
	}

	public static long Min(long a, long b) {
		return a < b ? a : b;
	}

	public static double Min(double a, double b) {
		return a < b ? a : b;
	}

	public static short Max(short a, short b) {
		return a > b ? a : b;
	}

	public static int Max(int a, int b) {
		return a > b ? a : b;
	}

	public static long Max(long a, long b) {
		return a > b ? a : b;
	}

	public static double Max(double a, double b) {
		return a > b ? a : b;
	}

	public static boolean existeArchivo(String nombre) {
		java.io.File f = new java.io.File(nombre);
		return f.canRead();
	}
	
	public static String capitalize(String s) {
		if (s.length()<2)
			return s.toUpperCase();
		return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}

}
