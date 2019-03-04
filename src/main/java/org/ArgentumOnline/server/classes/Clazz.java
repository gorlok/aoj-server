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
package org.ArgentumOnline.server.classes;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserFaction.FactionArmors;
import org.ArgentumOnline.server.UserRace;
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Util;

public enum Clazz {

    Mage("MAGO", true),
    Cleric("CLERIGO", true),
    Warrior("GUERRERO", false),
    Assassin("ASESINO", true),
    Thief("LADRON", false),
    Bard("BARDO", true), // alto bardo
    Druid("DRUIDA", true),
    Bandit("BANDIDO", false),
    Paladin("PALADIN", true),
    Hunter("CAZADOR", false),
    Fisher("PESCADOR", false),
    Blacksmith("HERRERO", false),
    Lumberjack("LEÑADOR", false),
    Miner("MINERO", false),
    Carpenter("CARPINTERO", false),
    Pirate("PIRATA", false);

	private String name;

	private boolean magic = false;

	private Clazz(String name, boolean magic) {
		this.name = name;
		this.magic = magic;
	}

	/** id starts with 1 */
	public byte id() {
		return (byte) (ordinal() + 1);
	}

	public static Clazz[] values = Clazz.values();
	
	/** id starts with 1 */
	public static Clazz value(int i) {
		return values[i-1];
	}

	public String getName() {
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
			return Util.Azar(1, 6);
		default:
			return 1;
		}
	}

	public int getCantLeños() {
		switch (this) {
		case Lumberjack:
			return Util.Azar(1, 5);
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
			return 100 + Util.Azar(1, atribInteligencia / 3);
		default:
			return 0;
		}
	}

	/** Incremento de salud al subir de nivel */
	protected int getMejoraSalud(UserStats estads) {
		switch (this) {
		case Assassin:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Bard:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Cleric:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Druid:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Fisher:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Hunter:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + Constants.AdicionalHPGuerrero;
		case Lumberjack:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Mage:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + Constants.AdicionalHPGuerrero / 2;
		case Miner:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Paladin:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + Constants.AdicionalHPGuerrero;
		case Pirate:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + Constants.AdicionalHPGuerrero;
		case Thief:
			return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		case Warrior:
	        return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + Constants.AdicionalHPGuerrero;
	    default:
	    	return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
		}
	}

	/** Incremento de mana al subir de nivel */
	protected int getMejoraMana(UserStats estads) {
		switch (this) {
		case Assassin:
			return estads.attr().get(Attribute.INTELIGENCIA);
		case Bard:
			return 2 * estads.attr().get(Attribute.INTELIGENCIA);
		case Cleric:
			return 2 * estads.attr().get(Attribute.INTELIGENCIA);
		case Druid:
			return 2 * estads.attr().get(Attribute.INTELIGENCIA);
		case Mage:
			return 3 * estads.attr().get(Attribute.INTELIGENCIA);
		case Paladin:
			return estads.attr().get(Attribute.INTELIGENCIA);
		default:
			return 0;
		}
	}

	/** Incremento de stamina al subir de nivel */
	protected int getMejoraStamina() {
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
			return 15 + Constants.AdicionalSTPescador;
		case Hunter:
			return 15;
		case Lumberjack:
			return 15 + Constants.AdicionalSTLeñador;
		case Mage:
	        int valor = 15 - Constants.AdicionalSTLadron / 2;
	        return (valor < 1) ? 5 : valor;
		case Miner:
			return 15 + Constants.AdicionalSTMinero;
		case Paladin:
			return 15;
		case Pirate:
			return 15;
		case Thief:
			return 15 + Constants.AdicionalSTLadron;
		case Warrior:
	        return 15;
	    default:
	    	return 15;
		}
	}

	/** Incremento de golpe al subir de nivel */
	protected int getMejoraGolpe() {
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
	public void subirEstads(Player player) {
		UserStats estads = player.stats();

		// Las mejoras varian según las características de cada clase.
		int aumentoSalud = getMejoraSalud(estads);
		int aumentoMana = getMejoraMana(estads);
		int aumentoStamina = getMejoraStamina();
		int aumentoGolpe = getMejoraGolpe();

		if (aumentoSalud > 0) {
			estads.addMaxHP(aumentoSalud);
			estads.fullHP(); // Recupera la salud al 100%.
			player.sendMessage("Has ganado " + aumentoSalud + " puntos de vida.", FontType.FONTTYPE_INFO);
		}
		if (aumentoStamina > 0) {
			estads.addMaxSTA(aumentoStamina);
			player.sendMessage("Has ganado " + aumentoStamina + " puntos de energia.", FontType.FONTTYPE_INFO);
		}
		if (aumentoMana > 0) {
			estads.addMaxMANA(aumentoMana);
			player.sendMessage("Has ganado " + aumentoMana + " puntos de magia.", FontType.FONTTYPE_INFO);
		}
		if (aumentoGolpe > 0) {
			estads.addMaxHIT(aumentoGolpe, player.stats().ELV);
			estads.addMinHIT(aumentoGolpe, player.stats().ELV);
			player.sendMessage("Tu golpe maximo aumento en " + aumentoGolpe + " puntos.", FontType.FONTTYPE_INFO);
		}
	}

	public short getArmaduraImperial(Player player) {
		switch (this) {
		case Assassin:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);

		case Bandit:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);

		case Hunter:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
		case Mage:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL);
		case Paladin:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
		case Warrior:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	        	return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
	    default:
			if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
				return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
			}
			return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_2);
		}
	}

	public short getArmaduraCaos(Player player) {
		switch (this) {
		case Assassin:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Bandit:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Hunter:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Mage:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS_ENANOS);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS);
		case Paladin:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
		case Warrior:
	        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
	            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
	        }
	        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
	    default:
			if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
				return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
			}
			return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_2);
		}
	}

}
