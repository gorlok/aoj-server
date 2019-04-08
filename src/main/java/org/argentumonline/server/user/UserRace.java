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
package org.argentumonline.server.user;

import java.util.Arrays;

/**
 * @author gorlok
 */
public enum UserRace {
	
	/*1*/ RAZA_HUMAN 	("Humano", 2, 1, 2, 1, 0),
	/*2*/ RAZA_ELF 		("Elfo", 0, 2, 0, 2, 2),
	/*3*/ RAZA_DROW   	("Elfo Oscuro", 1, 2, 0, 2, 2),
	/*4*/ RAZA_DWARF	("Enano", 3, 0, 3, -6, 0),
	/*5*/ RAZA_GNOME	("Gnomo", -5, 3, 0, 3, 0);

	private String name;
	
    private int strengthModifier;
    private int agilityModifier;
    private int constitutionModifier;
    private int inteligenceModifier;
    private int charismaModifier;
	
	
	private UserRace(String name, int strengthModifier, int agilityModifier, int constitutionModifier,
			int inteligenceModifier, int charismaModifier) {
		this.name = name;
		this.strengthModifier = strengthModifier;
		this.agilityModifier = agilityModifier;
		this.constitutionModifier = constitutionModifier;
		this.inteligenceModifier = inteligenceModifier;
		this.charismaModifier = charismaModifier;
	}

	private static final UserRace[] VALUES = UserRace.values();
	public static UserRace value(int value) {
		return VALUES[value-1];
	}
	
	public static UserRace byName(String value) {
		return Arrays.stream(VALUES)
				.filter( r -> value.equalsIgnoreCase(r.name()))
				.findFirst().orElse(null);
	}
	
	public String toString() {
		return this.name;
	}
	
	public byte id() {
		return (byte) (this.ordinal()+1);
	}

	public String getName() {
		return name;
	}

	public byte getStrengthModifier() {
		return (byte)strengthModifier;
	}

	public byte getAgilityModifier() {
		return (byte)agilityModifier;
	}

	public byte getConstitutionModifier() {
		return (byte)constitutionModifier;
	}

	public byte getInteligenceModifier() {
		return (byte)inteligenceModifier;
	}

	public byte getCharismaModifier() {
		return (byte)charismaModifier;
	}
	
}

