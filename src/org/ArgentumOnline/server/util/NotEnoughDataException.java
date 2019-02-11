package org.ArgentumOnline.server.util;

/**
 * Excepción que se lanza al querer leer y no hay datos disponibles en un buffer
 * Extraído de JFénix13
 */
public class NotEnoughDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughDataException() {super("No hay suficientes datos en el buffer para leer");
    }
}
