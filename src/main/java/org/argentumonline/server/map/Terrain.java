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
package org.argentumonline.server.map;

import java.util.Arrays;
import java.util.Optional;

public enum Terrain {
	/* 0 */ FOREST("BOSQUE"),
	/* 2 */ DESERT("DESIERTO"),
	/* 3 */ SNOW("NIEVE");

	// 'CAMPO', 'DUNGEON'", FontTypeNames.FONTTYPE_INFO)
	private String name;

	private Terrain(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static Optional<Terrain> fromName(String name) {
		return Arrays.stream(Terrain.values())
				.filter(t -> name.equalsIgnoreCase(t.name))
				.findFirst();
	}

}