/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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

import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.classes.Clazz;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Estad�sticas de usuarios: Stamina, Mana, Hambre y Sed, Oro, etc.
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
	int maxStamina = 0; // m�ximo de aguante

	int mana = 0; // man� actual
	public int maxMana = 0; // m�ximo de man�

	int eaten = 0; // comido, cuando llega a cero, muere de hambre
	int maxEaten = 0; // m�ximo de comida, lleno y sin hambre.

	int drinked = 0; // bebida (cuando llega a cero, muere)
	int maxDrinked = 0; // m�ximo de bebida, totalmente hidratado y sin sed

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
	 * Si se supera el m�ximo posible de oro, se descartar el resto. Si se vuelve negativo, queda en cero.
	 * @param cantidad de oro a sumar o restar 
	 */
	public void addGold(int amount) {
		this.gold += amount;
		if (this.gold > MAX_GOLD) {
			log.warn("Se super� el m�ximo de oro " + this.gold);
			this.gold = MAX_GOLD;
		}
		if (this.gold < 0) {
			log.warn("Se super� el m�nimo de oro " + this.gold);
			this.gold = 0;
		}
	}

	public void addExp(int cant) {
		this.Exp += cant;
		if (this.Exp > MAXEXP) {
			this.Exp = MAXEXP;
		}
	}

	// �?�?�?�?�?�?� Stamina �?�?�?�?�?�?�
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
		this.usuariosMatados++;
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
		this.MaxHP = 15 + Util.Azar(1, attr.get(Attribute.CONSTITUCION) / 3);
		this.MinHP = this.MaxHP;
		// Stamina
		int agil = Util.Azar(1, attr.get(Attribute.AGILIDAD) / 6);
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
		// Mana (magia y meditacion de clases m�gicas)
		this.maxMana = clazz.getManaInicial(attr.get(Attribute.INTELIGENCIA));
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

}
