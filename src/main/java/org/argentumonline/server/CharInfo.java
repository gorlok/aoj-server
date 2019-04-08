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
package org.argentumonline.server;

import static org.argentumonline.server.user.UserGender.GENERO_MAN;

import org.argentumonline.server.map.Heading;
import org.argentumonline.server.user.UserGender;
import org.argentumonline.server.user.UserRace;
import org.argentumonline.server.util.Util;

/**
 * @author gorlok
 */
public class CharInfo implements Constants {

	public short head;
	public short body;

	public short weapon;
	public short shield;
	public short helmet;

	public short fx;
	public short loops;

	public Heading heading;
	
	public CharInfo() {
	}

	public CharInfo(CharInfo charInfo) {
		this.head = charInfo.head;
		this.body = charInfo.body;
		this.weapon = charInfo.weapon;
		this.shield = charInfo.shield;
		this.helmet = charInfo.helmet;
		this.fx = charInfo.fx;
		this.loops = charInfo.loops;
		this.heading = charInfo.heading;
	}
	
	public void copyFrom(CharInfo other) {
		this.head = other.head;
		this.body = other.body;
		this.weapon = other.weapon;
		this.shield = other.shield;
		this.helmet = other.helmet;
	}

	public boolean validateChr() {
		return this.head != 0 && this.body != 0;
	}

	public void reset() {
		this.head = 0;
		this.body = 0;
		this.weapon = 0;
		this.shield = 0;
		this.helmet = 0;
		this.fx = 0;
		this.loops = 0;
		this.heading = Heading.NONE;
	}

	public short getHead() {
		return this.head;
	}

	public short getBody() {
		return this.body;
	}

	public short getWeapon() {
		return this.weapon;
	}

	public short getShield() {
		return this.shield;
	}

	public short getHelmet() {
		return this.helmet;
	}

	public short getFx() {
		return this.fx;
	}

	public short getLoops() {
		return this.loops;
	}

	public Heading getHeading() {
		return this.heading;
	}

	public void setHeading(Heading heading) {
		this.heading = heading;
	}

	public void undress(UserRace race, UserGender gender) {
		switch (race) {
		case RAZA_HUMAN:
			this.body = (gender == GENERO_MAN) ? (short) 21 : (short) 39;
			break;
		case RAZA_DROW:
			this.body = (gender == GENERO_MAN) ? (short) 32 : (short) 40;
			break;
		case RAZA_ELF:
			this.body = (gender == GENERO_MAN) ? (short) 210 : (short) 259;
			break;
		case RAZA_DWARF:
			this.body = (gender == GENERO_MAN) ? (short) 53 : (short) 60;
			break;
		case RAZA_GNOME:
			this.body = (gender == GENERO_MAN) ? (short) 222 : (short) 260;
			break;
		}
	}

	public void ramdonBodyAndHead(UserRace race, UserGender gender) {
		switch (gender) {
		case GENERO_MAN:
			switch (race) {
			case RAZA_HUMAN:
				this.head = (short) Util.random(1, 11);
				this.body = 1;
				break;
			case RAZA_ELF:
				this.head = (short) (Util.random(1, 4) + 100);
				this.body = 2;
				break;
			case RAZA_DROW:
				this.head = (short) (Util.random(1, 3) + 200);
				this.body = 3;
				break;
			case RAZA_DWARF:
				this.head = 301;
				this.body = 52;
				break;
			case RAZA_GNOME:
				this.head = 401;
				this.body = 52;
				break;
			default: // :-?
				this.head = 1;
				this.body = 1;
				break;
			}
			break;

		case GENERO_WOMAN:
			switch (race) {
			case RAZA_HUMAN:
				this.head = (short) (Util.random(1, 3) + 69);
				this.body = 1;
				break;
			case RAZA_ELF:
				this.head = (short) (Util.random(1, 3) + 169);
				this.body = 2;
				break;
			case RAZA_DROW:
				this.head = (short) (Util.random(1, 3) + 269);
				this.body = 3;
				break;
			case RAZA_GNOME:
				this.head = (short) (Util.random(1, 2) + 469);
				this.body = 52;
				break;
			case RAZA_DWARF:
				this.head = 370;
				this.body = 52;
				break;
			default: // :-?
				this.head = 70;
				this.body = 1;
				break;
			}
			break;
		}
	}

}
