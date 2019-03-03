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

import java.util.stream.Stream;

public enum ObjType {
	/*
    // Tipos de objetos:
    final static int OBJTYPE_USEONCE = 1;
    final static int OBJTYPE_WEAPON = 2;
    final static int OBJTYPE_ARMOUR = 3;
    final static int OBJTYPE_ARBOLES = 4;
    final static int OBJTYPE_GUITA = 5;
    final static int OBJTYPE_PUERTAS = 6;
    final static int OBJTYPE_CONTENEDORES = 7;
    final static int OBJTYPE_CARTELES = 8;
    final static int OBJTYPE_LLAVES = 9;
    final static int OBJTYPE_FOROS = 10;
    final static int OBJTYPE_POCIONES = 11;
    final static int OBJTYPE_LIBROS = 12;
    final static int OBJTYPE_BEBIDA = 13;
    final static int OBJTYPE_LEÑA = 14;
    final static int OBJTYPE_FOGATA = 15;
    final static int OBJTYPE_ESCUDO = 16;
    final static int OBJTYPE_CASCO = 17;
    final static int OBJTYPE_HERRAMIENTAS = 18;
    final static int OBJTYPE_TELEPORT = 19;
    final static int OBJTYPE_MUEBLE = 20;
    final static int OBJTYPE_JOYA = 21;
    final static int OBJTYPE_YACIMIENTO = 22;
    final static int OBJTYPE_MINERALES = 23;
    final static int OBJTYPE_PERGAMINOS = 24;
    final static int OBJTYPE_AURA = 25;
    final static int OBJTYPE_INSTRUMENTOS = 26;
    final static int OBJTYPE_YUNQUE = 27;
    final static int OBJTYPE_FRAGUA = 28;
    final static int OBJTYPE_GEMA = 29;
    final static int OBJTYPE_FLOR = 30;
    final static int OBJTYPE_BARCOS = 31;
    final static int OBJTYPE_FLECHAS = 32;
    final static int OBJTYPE_BOTELLAVACIA = 33;
    final static int OBJTYPE_BOTELLALLENA = 34;
    final static int OBJTYPE_MANCHAS = 35;
        
    final static int OBJTYPE_HACHA_LEÑADOR = 127;
    final static int OBJTYPE_CAÑA = 138;
    final static int OBJTYPE_PIQUETE_MINERO = 187;
    final static int OBJTYPE_SERRUCHO_CARPINTERO = 198;
    final static int OBJTYPE_MARTILLO_HERRERO = 389;    
    final static int OBJTYPE_CUALQUIERA = 1000;
    final static int OBJTYPE_RED_PESCA = 543;
	 */
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
