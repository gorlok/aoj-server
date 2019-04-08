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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.AbstractCharStats;
import org.argentumonline.server.Clazz;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

/**
 * Estadísticas de usuarios: Stamina, Mana, Hambre y Sed, Oro, etc.
 * 
 * @author gorlok
 */
public class UserStats extends AbstractCharStats {
	private static Logger log = LogManager.getLogger();

	public UserStats() {
		super();
	}

	private int gold = 0;
	private int bankGold = 0;

	public int stamina = 0; // aguante actual
	public int maxStamina = 0; // máximo de aguante

	public int mana = 0; // maná actual
	public int maxMana = 0; // máximo de maná

	int eaten = 0; // comido, cuando llega a cero, muere de hambre
	int maxEaten = 0; // máximo de comida, lleno y sin hambre.

	int drinked = 0; // bebida (cuando llega a cero, muere)
	int maxDrinked = 0; // máximo de bebida, totalmente hidratado y sin sed

	/** Current Experience */
	public int Exp = 0; // Experiencia
	/** Level */
	public int ELV = 0; // Nivel
	/** Experience to Level Up */
	public int ELU = 0; // Exp. Max del nivel

	public int usuariosMatados = 0;
	public int NPCsMuertos = 0;
	
	UserAttributes attr = new UserAttributes();

	public UserAttributes attr() {
		return this.attr;
	}
	
	public int getBankGold() {
		return this.bankGold;
	}
	
	public void setBankGold(int bankGold) {
		this.bankGold = bankGold;
	}
	
	/**
	 * Incrementa o decrementa la cantidad de oro guardado en el banco.
	 * La cantidad a sumar puede ser positiva o negativa.
	 * Si el banco queda en negativo, queda en cero.
	 * @param amount
	 */
	public void addBankGold(int amount) {
		this.bankGold += amount;
		if (this.bankGold < 0) {
			this.bankGold = 0;
		}
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	/**
	 * Incrementa o decrementa la cantidad de oro. La cantidad a sumar puede ser positiva o negativa.
	 * Si se supera el máximo posible de oro, se descartar el resto. Si se vuelve negativo, queda en cero.
	 * @param cantidad de oro a sumar o restar 
	 */
	public void addGold(int amount) {
		this.gold += amount;
		if (this.gold > MAX_GOLD) {
			log.warn("Se superó el máximo de oro " + this.gold);
			this.gold = MAX_GOLD;
		}
		if (this.gold < 0) {
			log.warn("Se superó el mínimo de oro " + this.gold);
			this.gold = 0;
		}
	}

	public void addExp(int cant) {
		this.Exp += cant;
		if (this.Exp > MAXEXP) {
			this.Exp = MAXEXP;
		}
	}

	// ¿?¿?¿?¿?¿?¿?¿ Stamina ¿?¿?¿?¿?¿?¿?¿
	public void addMaxSTA(int cant) {
		this.maxStamina += cant;
		if (this.maxStamina > STAT_MAXSTA) {
			this.maxStamina = STAT_MAXSTA;
		}
	}

	// Mana
	public void addMaxMANA(int cant) {
		this.maxMana += cant;
		if (this.maxMana > STAT_MAXMAN) {
			this.maxMana = STAT_MAXMAN;
		}
	}
	
	public void incUsuariosMatados() {
		if (this.usuariosMatados < MAX_USER_KILLED) {
			this.usuariosMatados++;
		}
	}

	public void incNPCsMuertos() {
		this.NPCsMuertos++;
	}

	public void aumentarStamina(int cant) {
		this.stamina += cant;
		if (this.stamina > this.maxStamina) {
			this.stamina = this.maxStamina;
		}
	}

	public void quitarStamina(int cant) {
		this.stamina -= cant;
		if (this.stamina < 0) {
			this.stamina = 0;
		}
	}

	public void quitarMana(int cant) {
		this.mana -= cant;
		if (this.mana < 0) {
			this.mana = 0;
		}
	}

	public void aumentarMana(int cant) {
		this.mana += cant;
		if (this.mana > this.maxMana) {
			this.mana = this.maxMana;
		}
	}

	public void aumentarHambre(int cant) {
		this.eaten += cant;
		if (this.eaten > this.maxEaten) {
			this.eaten = this.maxEaten;
		}
	}

	public void quitarHambre(int cant) {
		this.eaten -= cant;
		if (this.eaten < 0) {
			this.eaten = 0;
		}
	}

	public void aumentarSed(int cant) {
		this.drinked += cant;
		if (this.drinked > this.maxDrinked) {
			this.drinked = this.maxDrinked;
		}
	}

	public void quitarSed(int cant) {
		this.drinked -= cant;
		if (this.drinked < 0) {
			this.drinked = 0;
		}
	}

	public void inicializarEstads(Clazz clazz) {
		// Salud
		this.MaxHP = 15 + Util.random(1, attr.get(Attribute.CONSTITUTION) / 3);
		this.MinHP = this.MaxHP;
		// Stamina
		int agil = Util.random(1, attr.get(Attribute.AGILITY) / 6);
		if (agil < 2) {
			agil = 2;
		}
		this.maxStamina = 20 * agil;
		this.stamina = this.maxStamina;
		// Agua/Sed: si llega a cero produce la muerte.
		this.maxDrinked = 100;
		this.drinked = this.maxDrinked;
		// Hambre: si llega a cero produce la muerte.
		this.maxEaten = 100;
		this.eaten = this.maxEaten;
		// Mana (magia y meditacion de clases mágicas)
		this.maxMana = clazz.getManaInicial(attr.get(Attribute.INTELIGENCE));
		this.mana = this.maxMana;
		// Golpe al atacar.
		this.MaxHIT = 2;
		this.MinHIT = 1;
		// Varios
		this.gold = 0; // Oro en la billetera.
		this.Exp = 0; // Puntos de experiencia ganados.
		this.ELU = 300; // Puntos necesarios para subir de nivel.
		this.ELV = 1; // Nivel inicial.
	}
	
	public boolean isTooTired() {
		return this.stamina <= 0;
	}
	
	public boolean isFullStamina() {
		return this.stamina > 0 && this.stamina == this.maxStamina;
	}

	public void loadUserStats(IniFile ini) {
		int i = 1;
		for (Attribute attr : Attribute.values()) {
			attr().set(attr, ini.getShort("ATRIBUTOS", "AT" + (i++)));
		}
		attr().backupAttributes();

		Exp = ini.getInt("STATS", "EXP");
		ELU = ini.getInt("STATS", "ELU");
		ELV = ini.getInt("STATS", "ELV");

		setGold(ini.getInt("STATS", "GLD"));
		setBankGold(ini.getInt("STATS", "BANCO"));

		MaxHP = ini.getInt("STATS", "MaxHP");
		MinHP = ini.getInt("STATS", "MinHP");

		stamina = ini.getInt("STATS", "MinSTA");
		maxStamina = ini.getInt("STATS", "MaxSTA");

		maxMana = ini.getInt("STATS", "MaxMAN");
		mana = ini.getInt("STATS", "MinMAN");

		MaxHIT = ini.getInt("STATS", "MaxHIT");
		MinHIT = ini.getInt("STATS", "MinHIT");

		maxDrinked = ini.getInt("STATS", "MaxAGU");
		drinked = ini.getInt("STATS", "MinAGU");

		maxEaten = ini.getInt("STATS", "MaxHAM");
		eaten = ini.getInt("STATS", "MinHAM");

		usuariosMatados = ini.getInt("MUERTES", "UserMuertes");
		NPCsMuertos = ini.getInt("MUERTES", "NpcsMuertes");
	}
	
}
