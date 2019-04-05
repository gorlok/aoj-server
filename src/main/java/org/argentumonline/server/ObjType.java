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

import java.util.stream.Stream;

public enum ObjType {

	NONE(0),
	UseOnce(1),
	Weapon(2),
	Armadura(3),
	Arboles(4),
	Guita(5),
	Puertas(6),
	Contenedores(7),
	Carteles(8),
	Llaves(9),
	Foros(10),
	Pociones(11),
	Libros(12),
	Bebidas(13),
	Leña(14),
	Fogata(15),
	ESCUDO(16),
	CASCO(17),
	Anillo(18),
	Teleport(19),
	Mueble(20),
	Joya(21),
	Yacimiento(22),
	Minerales(23),
	Pergaminos(24),
	Aura(25),
	Instrumentos(26),
	Yunque(27),
	Fragua(28),
	Gema(29),
	Flor(30),
	Barcos(31),
	Flechas(32),
	BotellaVacia(33),
	BotellaLlena(34),
	Manchas(35), // No se usa
	Cualquiera(255);

	private byte value;

	private ObjType(int value) {
		this.value = (byte)value;
	}

	public byte value() {
		return this.value;
	}
	
	private static ObjType[] values = ObjType.values();
	public static ObjType value(int value) {
		return Stream.of(values).filter(ot -> ot.value() == value).findFirst().get();
	}

}
