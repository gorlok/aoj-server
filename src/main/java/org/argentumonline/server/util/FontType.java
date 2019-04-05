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

/**
 * @author gorlok
 */
public enum FontType {
	FONTTYPE_TALK(255, 255, 255),
	FONTTYPE_FIGHT(255, 0, 0, true, false),
	FONTTYPE_WARNING(32, 51, 223, true, true),
	FONTTYPE_INFO(65, 190, 156),
	FONTTYPE_INFOBOLD(65, 190, 156, true, false),
	FONTTYPE_EJECUCION(130, 130, 130, true, false),
	FONTTYPE_PARTY(255, 180, 250),
	FONTTYPE_VENENO(0, 255, 0),
	FONTTYPE_GUILD(255, 255, 255, true, false),
	FONTTYPE_SERVER(0, 185, 0),
	FONTTYPE_GUILDMSG(228, 199, 27),
	FONTTYPE_CONSEJO(130, 130, 255, true, false),
	FONTTYPE_CONSEJOCAOS(255, 60, 0, true, false),
	FONTTYPE_CONSEJOVesA(0, 200, 255, true, false),
	FONTTYPE_CONSEJOCAOSVesA(255, 50, 0, true, false),
	FONTTYPE_CENTINELA(0, 255, 0, true, false),
	FONTTYPE_GMMSG(255, 255, 255, false, true),
	FONTTYPE_GM(30, 255, 30, true, false),
	FONTTYPE_CITIZEN(0, 0, 200, true, false),
	FONTTYPE_CONSE(30, 150, 30, true, false),
	FONTTYPE_DIOS(250, 250, 150, true, false);

	public byte r;
	public byte g;
	public byte b;
	public boolean bold;
	public boolean italic;

	private FontType(int r, int g, int b) {
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
	}

	private FontType(int r, int g, int b, boolean bold, boolean italic) {
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		this.bold = bold;
		this.italic = italic;
	}

	@Override
	public String toString() {
		return "~" + this.r + "~" + this.g + "~" + this.b + "~" + this.bold + "~" + this.italic;
	}

	private static FontType[] values = FontType.values();

	public static FontType value(int index) {
		return values[index];
	}

	public byte id() {
		return (byte) this.ordinal();
	}
}
