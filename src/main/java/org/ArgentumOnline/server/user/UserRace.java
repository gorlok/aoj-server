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
package org.ArgentumOnline.server.user;

/**
 * @author gorlok
 */
public enum UserRace {
	
	/*1*/ RAZA_HUMANO ("Humano", 2, 1, 2, 1, 0),
	/*2*/ RAZA_ELFO 	("Elfo", 0, 2, 0, 2, 2),
	/*3*/ RAZA_DROW   ("Elfo Oscuro", 1, 2, 0, 2, 2),
	/*4*/ RAZA_ENANO	("Enano", 3, 0, 3, -6, 0),
	/*5*/ RAZA_GNOMO	("Gnomo", -5, 3, 0, 3, 0);

	private String name;
    private int modificadorFuerza;
    private int modificadorAgilidad;
    private int modificadorConstitucion;
    private int modificadorInteligencia;
    private int modificadorCarisma;
	
	private UserRace(String name, 
			int modificadorFuerza, 
			int modificadorAgilidad, 
			int modificadorConstitucion,
			int modificadorInteligencia, 
			int modificadorCarisma) {
		this.name = name;
		this.modificadorFuerza = modificadorFuerza;
		this.modificadorAgilidad = modificadorAgilidad;
		this.modificadorConstitucion = modificadorConstitucion;
		this.modificadorInteligencia = modificadorInteligencia;
		this.modificadorCarisma = modificadorCarisma;
	}
	
	private static final UserRace[] VALUES = UserRace.values();
	public static UserRace value(int value) {
		return VALUES[value-1];
	}
	
	public String toString() {
		return this.name;
	}
	
	public byte value() {
		return (byte) (this.ordinal()+1);
	}

	public String getName() {
		return name;
	}

	public byte modificadorFuerza() {
		return (byte)modificadorFuerza;
	}

	public byte modificadorAgilidad() {
		return (byte)modificadorAgilidad;
	}

	public byte modificadorConstitucion() {
		return (byte)modificadorConstitucion;
	}

	public byte modificadorInteligencia() {
		return (byte)modificadorInteligencia;
	}

	public byte modificadorCarisma() {
		return (byte)modificadorCarisma;
	}
	
}

