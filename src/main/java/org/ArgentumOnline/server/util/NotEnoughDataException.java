package org.ArgentumOnline.server.util;

/**
 * Excepci�n que se lanza al querer leer y no hay datos disponibles en un buffer
 * Extra�do de JF�nix13
 */
public class NotEnoughDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughDataException() {super("No hay suficientes datos en el buffer para leer");
    }
}
