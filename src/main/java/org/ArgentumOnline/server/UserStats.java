/**
 * UserStats.java
 *
 * Created on 13 de octubre de 2003, 11:06
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
package org.ArgentumOnline.server;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.classes.CharClass;
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

	int Exp = 0; // Experiencia
	int ELV = 0; // Nivel
	int ELU = 0; // Exp. Max del nivel

	public byte userSkills[] = new byte[Skill.MAX_SKILLS + 1];
	public byte userAtributos[] = new byte[NUMATRIBUTOS];
	byte userAtributosBackup[] = new byte[NUMATRIBUTOS];

	public int NPCsMuertos = 0;
	public int SkillPts = 0; // Puntos de skills sin asignar.

	public void setSkillPoints(int val) {
		this.SkillPts = val;
	}

	public int getSkillPoints() {
		return this.SkillPts;
	}

	public void setSkills(byte skills[]) {
		for (int i = 0; i < skills.length; i++) {
			this.userSkills[i] = skills[i];
		}
	}

	public boolean atributosValidos() {
		for (byte element : this.userAtributos) {
			if (element > 18 || element < 1) {
				return false;
			}
		}
		return true;
	}

	public void saveAtributos() {
		for (int i = 0; i < this.userAtributos.length; i++) {
			this.userAtributosBackup[i] = this.userAtributos[i];
		}
	}

	public void restoreAtributos() {
		for (int i = 0; i < this.userAtributos.length; i++) {
			this.userAtributos[i] = this.userAtributosBackup[i];
		}
	}

	public boolean skillsValidos() {
		int totalskpts = 0;
		// Abs PREVINENE EL HACKEO DE LOS SKILLS %%%%%%%%%%%%%
		for (int i = 1; i < this.userSkills.length; i++) {
			totalskpts += Math.abs(this.userSkills[i]);
		}
		return totalskpts == 10;
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS SKILLS %%%%%%%%%%%%%
	}

	public byte getUserSkill(int skill) {
		return this.userSkills[skill];
	}

	public void setUserSkill(int skill, byte value) {
		this.userSkills[skill] = value;
	}

	public void addSkillPoints(int skill, byte cant) {
		this.userSkills[skill] += cant;
		if (this.userSkills[skill] > Skill.MAX_SKILL_POINTS) {
			this.userSkills[skill] = Skill.MAX_SKILL_POINTS;
		}
	}

	public void subirSkills(byte[] incSkills) {
		for (int i = 1; i <= Skill.MAX_SKILLS; i++) {
			this.SkillPts -= incSkills[i];
			this.userSkills[i] += incSkills[i];
			if (this.userSkills[i] > 100) {
				this.userSkills[i] = 100;
			}
		}
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

	public void aumentarAtributo(int atributo, int cant) {
		int tmp = this.userAtributos[atributo] + cant;
		if (tmp > MAXATRIBUTOS) {
			tmp = MAXATRIBUTOS;
		}
		this.userAtributos[atributo] = (byte) tmp;
	}

	public void disminuirAtributo(int atributo, int cant) {
		int tmp = this.userAtributos[atributo] - cant;
		if (tmp < MINATRIBUTOS) {
			tmp = MINATRIBUTOS;
		}
		this.userAtributos[atributo] = (byte) tmp;
	}

	public Object[] getAtribs() {
		List<Object> atribs = new LinkedList<Object>();
		for (byte b : this.userAtributos) {
			atribs.add(b);
		}
		return atribs.toArray();
	}

	public void inicializarEstads(CharClass clase) {
		// Salud
		this.MaxHP = 15 + Util.Azar(1, this.userAtributos[ATRIB_CONSTITUCION] / 3);
		this.MinHP = this.MaxHP;
		// Stamina
		int agil = Util.Azar(1, this.userAtributos[ATRIB_AGILIDAD] / 6);
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
		this.maxMana = clase.getManaInicial(this.userAtributos[ATRIB_INTELIGENCIA]);
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
