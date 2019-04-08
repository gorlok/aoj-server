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

import java.util.Arrays;

import org.argentumonline.server.user.FactionArmors;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserRace;
import org.argentumonline.server.user.UserStats;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Util;

public enum Clazz {

    /** id= 1 */ Mage("MAGO", true),
    /** id= 2 */ Cleric("CLERIGO", true),
    /** id= 3 */ Warrior("GUERRERO", false),
    /** id= 4 */ Assassin("ASESINO", true),
    /** id= 5 */ Thief("LADRON", false),
    /** id= 6 */ Bard("BARDO", true), // alto bardo
    /** id= 7 */ Druid("DRUIDA", true),
    /** id= 8 */ Bandit("BANDIDO", false),
    /** id= 9 */ Paladin("PALADIN", true),
    /** id=10 */ Hunter("CAZADOR", false),
    /** id=11 */ Fisher("PESCADOR", false),
    /** id=12 */ Blacksmith("HERRERO", false),
    /** id=13 */ Lumberjack("LEÑADOR", false),
    /** id=14 */ Miner("MINERO", false),
    /** id=15 */ Carpenter("CARPINTERO", false),
    /** id=16 */ Pirate("PIRATA", false);

	private String name;

	private boolean magic = false;

    // HP adicionales cuando sube de nivel
    final static byte AdicionalHPGuerrero = 2;
    final static byte AdicionalSTLadron   = 3;
    final static byte AdicionalSTLeñador  = 23;
    final static byte AdicionalSTPescador = 20;
    final static byte AdicionalSTMinero   = 25;

	private Clazz(String name, boolean magic) {
		this.name = name;
		this.magic = magic;
	}

	/** id starts with 1 */
	public byte id() {
		return (byte) (ordinal() + 1);
	}

	public static Clazz[] VALUES = Clazz.values();

	/** id starts with 1 */
	public static Clazz value(int index) {
		return VALUES[index-1];
	}

	public static Clazz byName(String value) {
		return Arrays.stream(VALUES)
				.filter( c -> value.equalsIgnoreCase(c.name()))
				.findFirst().orElse(null);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public boolean isMagickal() {
		return this.magic;
	}

	public double modificadorEvasion() {
		switch (this) {
		case Assassin:
			return 1.1;
		case Bandit:
			return 0.9;
		case Bard:
			return 1.1;
		case Hunter:
			return 0.9;
		case Paladin:
			return 0.9;
		case Pirate:
			return 0.9;
		case Thief:
			return 1.1;
		case Warrior:
			return 1.0;
		default:
			return 0.8;
		}
	}

	public double modificadorPoderAtaqueArmas() {
		switch (this) {
		case Assassin:
			return 0.85;
		case Bandit:
			return 0.75;
		case Bard:
			return 0.7;
		case Blacksmith:
			return 0.6;
		case Carpenter:
			return 0.6;
		case Cleric:
			return 0.7;
		case Druid:
			return 0.7;
		case Fisher:
			return 0.6;
		case Hunter:
			return 0.8;
		case Lumberjack:
			return 0.6;
		case Miner:
			return 0.6;
		case Paladin:
			return 0.85;
		case Pirate:
			return 0.8;
		case Thief:
			return 0.75;
		case Warrior:
			return 1.0;
		default:
			return 0.5;
		}
	}

	public double modificadorPoderAtaqueProyectiles() {
		switch (this) {
		case Assassin:
			return 0.75;
		case Bandit:
			return 0.8;
		case Bard:
			return 0.7;
		case Blacksmith:
			return 0.65;
		case Carpenter:
			return 0.7;
		case Cleric:
			return 0.7;
		case Druid:
			return 0.75;
		case Fisher:
			return 0.65;
		case Hunter:
			return 1.0;
		case Lumberjack:
			return 0.7;
		case Miner:
			return 0.65;
		case Paladin:
			return 0.75;
		case Pirate:
			return 0.75;
		case Thief:
			return 0.8;
		case Warrior:
			return 0.8;
		default:
			return 0.5;
		}
	}

	public double modicadorDañoClaseArmas() {
		switch (this) {
		case Assassin:
			return 0.9;
		case Bandit:
			return 0.8;
		case Bard:
			return 0.75;
		case Blacksmith:
			return 0.75;
		case Carpenter:
			return 0.7;
		case Cleric:
			return 0.8;
		case Druid:
			return 0.75;
		case Fisher:
			return 0.6;
		case Hunter:
			return 0.9;
		case Lumberjack:
			return 0.7;
		case Miner:
			return 0.75;
		case Paladin:
			return 0.9;
		case Pirate:
			return 0.8;
		case Thief:
			return 0.8;
		case Warrior:
			return 1.1;
		default:
			return 0.5;
		}
	}

	public double modicadorDañoClaseProyectiles() {
		switch (this) {
		case Assassin:
			return 0.8;
		case Bandit:
			return 0.75;
		case Bard:
			return 0.7;
		case Blacksmith:
			return 0.6;
		case Carpenter:
			return 0.7;
		case Cleric:
			return 0.7;
		case Druid:
			return 0.75;
		case Fisher:
			return 0.6;
		case Hunter:
			return 1.1;
		case Lumberjack:
			return 0.7;
		case Miner:
			return 0.6;
		case Paladin:
			return 0.8;
		case Pirate:
			return 0.75;
		case Thief:
			return 0.75;
		case Warrior:
	        return 1.0;
	    default:
	    	return 0.5;
		}
	}

	public double modEvasionDeEscudoClase() {
		switch (this) {
		case Assassin:
			return 0.8;
		case Bandit:
			return 0.8;
		case Bard:
			return 0.75;
		case Blacksmith:
			return 0.7;
		case Carpenter:
			return 0.7;
		case Cleric:
			return 0.9;
		case Druid:
			return 0.75;
		case Fisher:
			return 0.7;
		case Hunter:
			return 0.8;
		case Lumberjack:
			return 0.7;
		case Miner:
			return 0.7;
		case Paladin:
			return 1.0;
		case Pirate:
			return 0.75;
		case Thief:
			return 0.7;
		case Warrior:
	        return 1.0;
	    default:
	    	return 0.6;
		}
	}

	public double modDomar() {
		switch (this) {
		case Cleric:
			return 7;
		case Druid:
			return 6;
		case Hunter:
			return 6;
		default:
			return 10;
		}
	}

	public double modNavegacion() {
		switch (this) {
		case Pirate:
			return 1.0;
		default:
			return 2.0;
		}
	}

	public double modFundicion() {
		switch (this) {
		case Blacksmith:
			return 1.2;
		case Fisher:
			return 1.2;
		case Miner:
			return 1.0;
		default:
			return 3.0;
		}
	}

	public double modCarpinteria() {
		switch (this) {
		case Carpenter:
			return 1.0;
		default:
			return 3.0;
		}
	}

	public double modHerreria() {
		switch (this) {
		case Blacksmith:
			return 1.0;
		case Miner:
			return 1.2;
		default:
			return 4.0;
		}
	}

	public short getEsfuerzoExcavar() {
		switch (this) {
		case Miner:
			return 2;
		default:
			return 5;
		}
	}

	public short getEsfuerzoPescar() {
		switch (this) {
		case Fisher:
			return 1;
		default:
			return 3;
		}
	}

	public short getEsfuerzoTalar() {
		switch (this) {
		case Lumberjack:
			return 2;
		default:
			return 4;
		}
	}

	public int getCantMinerales() {
		switch (this) {
		case Miner:
			return Util.random(1, 6);
		default:
			return 1;
		}
	}

	public int getCantLeños() {
		switch (this) {
		case Lumberjack:
			return Util.random(1, 5);
		default:
			return 1;
		}
	}

	public int getManaInicial(int atribInteligencia) {
		switch (this) {
		case Assassin:
			return 50;
		case Bard:
			return 50;
		case Cleric:
			return 50;
		case Druid:
			return 50;
		case Mage:
			return 100 + Util.random(1, atribInteligencia / 3);
		default:
			return 0;
		}
	}

	/** Incremento de salud al subir de nivel */
	private int getMejoraSalud(UserStats estads) {
		switch (this) {
		case Assassin:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Bard:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Cleric:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Druid:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Fisher:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Hunter:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2) + AdicionalHPGuerrero;
		case Lumberjack:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Mage:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2) + AdicionalHPGuerrero / 2;
		case Miner:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Paladin:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2) + AdicionalHPGuerrero;
		case Pirate:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2) + AdicionalHPGuerrero;
		case Thief:
			return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		case Warrior:
	        return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2) + AdicionalHPGuerrero;
	    default:
	    	return Util.random(4, estads.attr().get(Attribute.CONSTITUTION) / 2);
		}
	}

	/** Incremento de mana al subir de nivel */
	private int getMejoraMana(UserStats estads) {
		switch (this) {
		case Assassin:
			return estads.attr().get(Attribute.INTELIGENCE);
		case Bard:
			return 2 * estads.attr().get(Attribute.INTELIGENCE);
		case Cleric:
			return 2 * estads.attr().get(Attribute.INTELIGENCE);
		case Druid:
			return 2 * estads.attr().get(Attribute.INTELIGENCE);
		case Mage:
			return 3 * estads.attr().get(Attribute.INTELIGENCE);
		case Paladin:
			return estads.attr().get(Attribute.INTELIGENCE);
		default:
			return 0;
		}
	}

	/** Incremento de stamina al subir de nivel */
	private int getMejoraStamina() {
		switch (this) {
		case Assassin:
			return 15;
		case Bard:
			return 15;
		case Cleric:
			return 15;
		case Druid:
			return 15;
		case Fisher:
			return 15 + AdicionalSTPescador;
		case Hunter:
			return 15;
		case Lumberjack:
			return 15 + AdicionalSTLeñador;
		case Mage:
	        int valor = 15 - AdicionalSTLadron / 2;
	        return (valor < 1) ? 5 : valor;
		case Miner:
			return 15 + AdicionalSTMinero;
		case Paladin:
			return 15;
		case Pirate:
			return 15;
		case Thief:
			return 15 + AdicionalSTLadron;
		case Warrior:
	        return 15;
	    default:
	    	return 15;
		}
	}

	/** Incremento de golpe al subir de nivel */
	private int getMejoraGolpe() {
		switch (this) {
		case Assassin:
			return 3;
		case Bard:
			return 2;
		case Cleric:
			return 2;
		case Druid:
			return 2;
		case Fisher:
			return 1;
		case Hunter:
			return 3;
		case Lumberjack:
			return 2;
		case Mage:
			return 1;
		case Miner:
			return 2;
		case Paladin:
			return 3;
		case Pirate:
			return 3;
		case Thief:
			return 1;
		case Warrior:
	        return 3;
	    default:
	    	return 2;
		}
	}

	/** Subir las estadísticas segun la clase */
	public void incStats(User user) {
		UserStats stats = user.getStats();

		// Las mejoras varian según las características de cada clase.
		int aumentoSalud = getMejoraSalud(stats);
		int aumentoMana = getMejoraMana(stats);
		int aumentoStamina = getMejoraStamina();
		int aumentoGolpe = getMejoraGolpe();

		if (aumentoSalud > 0) {
			stats.addMaxHP(aumentoSalud);
			stats.restoreFullHP(); // Recupera la salud al 100%.
			user.sendMessage("Has ganado " + aumentoSalud + " puntos de vida.", FontType.FONTTYPE_INFO);
		}
		if (aumentoStamina > 0) {
			stats.addMaxSTA(aumentoStamina);
			user.sendMessage("Has ganado " + aumentoStamina + " puntos de energia.", FontType.FONTTYPE_INFO);
		}
		if (aumentoMana > 0) {
			stats.addMaxMANA(aumentoMana);
			user.sendMessage("Has ganado " + aumentoMana + " puntos de magia.", FontType.FONTTYPE_INFO);
		}
		if (aumentoGolpe > 0) {
			stats.addMaxHIT(aumentoGolpe, user.getStats().ELV);
			stats.addMinHIT(aumentoGolpe, user.getStats().ELV);
			user.sendMessage("Tu golpe maximo aumento en " + aumentoGolpe + " puntos.", FontType.FONTTYPE_INFO);
		}
	}

	public short getRoyalArmyArmor(User user) {
		switch (this) {
		case Assassin:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);

		case Bandit:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);

		case Hunter:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
		case Mage:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL);
		case Paladin:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
		case Warrior:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	        	return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
	    default:
			if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
				return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
			}
			return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_2);
		}
	}

	public short getDarkLegionArmor(User user) {
		switch (this) {
		case Assassin:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Bandit:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Hunter:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Mage:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS_ENANOS);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS);
		case Paladin:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Warrior:
	        if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
	            return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
	    default:
			if (user.race() == UserRace.RAZA_DWARF || user.race() == UserRace.RAZA_GNOME) {
				return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
			}
			return FactionArmors.getFactionArmor(FactionArmors.ARMADURA_CAOS_2);
		}
	}

}
