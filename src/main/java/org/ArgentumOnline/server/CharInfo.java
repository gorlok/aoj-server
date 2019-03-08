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
package org.ArgentumOnline.server;

import static org.ArgentumOnline.server.user.UserGender.GENERO_HOMBRE;

import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.user.UserGender;
import org.ArgentumOnline.server.user.UserRace;
import org.ArgentumOnline.server.util.Util;

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

	public byte heading; // FIXME cambiar por Heading

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
		this.heading = 0;
	}

	public short head() {
		return this.head;
	}

	public short body() {
		return this.body;
	}

	public short weapon() {
		return this.weapon;
	}

	public short shield() {
		return this.shield;
	}

	public short helmet() {
		return this.helmet;
	}

	public short fx() {
		return this.fx;
	}

	public short loops() {
		return this.loops;
	}

	public byte heading() {
		return this.heading;
	}

	public void heading(Heading dir) {
		this.heading = (byte) dir.ordinal();
	}

	public void undress(UserRace raza, UserGender gender) {
		switch (raza) {
		case RAZA_HUMANO:
			this.body = (gender == GENERO_HOMBRE) ? (short) 21 : (short) 39;
			break;
		case RAZA_DROW:
			this.body = (gender == GENERO_HOMBRE) ? (short) 32 : (short) 40;
			break;
		case RAZA_ELFO:
			this.body = (gender == GENERO_HOMBRE) ? (short) 210 : (short) 259;
			break;
		case RAZA_ENANO:
			this.body = (gender == GENERO_HOMBRE) ? (short) 53 : (short) 60;
			break;
		case RAZA_GNOMO:
			this.body = (gender == GENERO_HOMBRE) ? (short) 222 : (short) 260;
			break;
		}
	}

	public void ramdonBodyAndHead(UserRace raza, UserGender gender) {
		switch (gender) {
		case GENERO_HOMBRE:
			switch (raza) {
			case RAZA_HUMANO:
				this.head = (short) Util.Azar(1, 11);
				this.body = 1;
				break;
			case RAZA_ELFO:
				this.head = (short) (Util.Azar(1, 4) + 100);
				this.body = 2;
				break;
			case RAZA_DROW:
				this.head = (short) (Util.Azar(1, 3) + 200);
				this.body = 3;
				break;
			case RAZA_ENANO:
				this.head = 301;
				this.body = 52;
				break;
			case RAZA_GNOMO:
				this.head = 401;
				this.body = 52;
				break;
			default: // :-?
				this.head = 1;
				this.body = 1;
				break;
			}
			break;

		case GENERO_MUJER:
			switch (raza) {
			case RAZA_HUMANO:
				this.head = (short) (Util.Azar(1, 3) + 69);
				this.body = 1;
				break;
			case RAZA_ELFO:
				this.head = (short) (Util.Azar(1, 3) + 169);
				this.body = 2;
				break;
			case RAZA_DROW:
				this.head = (short) (Util.Azar(1, 3) + 269);
				this.body = 3;
				break;
			case RAZA_GNOMO:
				this.head = (short) (Util.Azar(1, 2) + 469);
				this.body = 52;
				break;
			case RAZA_ENANO:
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
