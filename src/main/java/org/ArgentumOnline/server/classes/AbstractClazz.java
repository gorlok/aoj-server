/**
 * CharClass.java
 *
 * Created on 6 de octubre de 2003, 23:41
 * 
    AOJava Server
    Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
    Web site: http://www.aojava.com.ar
    
    This file is part of AOJava.

    AOJava is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AOJava is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 
 */
package org.ArgentumOnline.server.classes;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserFaction.FactionArmors;
import org.ArgentumOnline.server.UserRace;
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Util;

/**
 * Abstract class base of character classes.
 *
 * @author gorlok
 */
public abstract class AbstractClazz implements Constants {

	protected String name = null;
	protected boolean magic = false;

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	public String getName() {
		return this.name;
	}

	public boolean esMagica() {
		return this.magic;
	}

	public double modificadorEvasion() {
		return 0.8;
	}

	public double modificadorPoderAtaqueArmas() {
		return 0.5;
	}

	public double modificadorPoderAtaqueProyectiles() {
		return 0.5;
	}

	public double modicadorDañoClaseArmas() {
		return 0.5;
	}

	public double modicadorDañoClaseProyectiles() {
		return 0.5;
	}

	public double modEvasionDeEscudoClase() {
		return 0.6;
	}

	public double modDomar() {
		return 10;
	}

	public double modNavegacion() {
		return 2.0;
	}

	public double modFundicion() {
		return 3.0;
	}

	public double modCarpinteria() {
		return 3.0;
	}

	public double modHerreria() {
		return 4.0;
	}

	public short getEsfuerzoExcavar() {
		return 5;
	}

	public short getEsfuerzoPescar() {
		return 3;
	}

	public short getEsfuerzoTalar() {
		return 4;
	}

	public int getCantMinerales() {
		return 1;
	}

	public int getCantLeños() {
		return 1;
	}

	public int getManaInicial(int atribInteligencia) {
		return 0;
	}

	/** Incremento de salud al subir de nivel */
	protected int getMejoraSalud(UserStats estads) {
		return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
	}

	/** Incremento de mana al subir de nivel */
	protected int getMejoraMana(UserStats estads) {
		return 0;
	}

	/** Incremento de stamina al subir de nivel */
	protected int getMejoraStamina() {
		return 15;
	}

	/** Incremento de golpe al subir de nivel */
	protected int getMejoraGolpe() {
		return 2;
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
			estads.addMaxHIT(aumentoGolpe);
			estads.addMinHIT(aumentoGolpe);
			player.sendMessage("Tu golpe maximo aumento en " + aumentoGolpe + " puntos.", FontType.FONTTYPE_INFO);
		}
	}

	public short getArmaduraImperial(Player player) {
		if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
			return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
		}
		return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_2);
	}

	public short getArmaduraCaos(Player player) {
		if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
			return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
		}
		return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_2);
	}

}
