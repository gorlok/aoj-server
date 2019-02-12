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

/**
 * Estadísticas de usuarios: Stamina, Mana, Hambre y Sed, Oro, etc.
 * @author Pablo F. Lillia
 */
public class UserStats extends CharStats {
    
    public UserStats() { 
        super();
    }
    
    int oro = 0;
    int banco = 0;
    
    int MaxSta = 0; // Stamina
    int MinSta = 0; // Stamina
    
    int MaxMAN = 0; // Mana
    int MinMAN = 0; // Mana
    
    int MaxHam = 0; // Hambre
    int MinHam = 0; // Hambre
    
    int MaxAGU = 0; // Sed
    int MinAGU = 0; // Sed
    
    int Exp = 0; // Experiencia
    int ELV = 0; // Nivel
    int ELU = 0; // Exp. Max del nivel
    
    public byte userSkills[] = new byte[Skill.MAX_SKILLS+1];
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
    
    public void agregarOro(int cant) {
        this.oro += cant;
        if (this.oro > MAXORO) {
			this.oro = MAXORO;
		}
    }

    public void quitarOro(int cant) {
        this.oro -= cant;
        if (this.oro < 0) {
			this.oro = 0;
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
        this.MaxSta += cant;
        if (this.MaxSta > STAT_MAXSTA) {
			this.MaxSta = STAT_MAXSTA;
		}
    }

    // Mana
    public void addMaxMANA(int cant) {
        this.MaxMAN += cant;
        if (this.MaxMAN > STAT_MAXMAN) {
			this.MaxMAN = STAT_MAXMAN;
		}
    }

    public void incNPCsMuertos() {
        this.NPCsMuertos++;
    }

    public void aumentarStamina(int cant) {
        this.MinSta += cant;
        if (this.MinSta > this.MaxSta) {
			this.MinSta = this.MaxSta;
		}
    }

    public void quitarStamina(int cant) {
        this.MinSta -= cant;
        if (this.MinSta < 0) {
			this.MinSta = 0;
		}
    }

    public void quitarMana(int cant) {
        this.MinMAN -= cant;
        if (this.MinMAN < 0) {
			this.MinMAN = 0;
		}
    }
    
    public void aumentarMana(int cant) {
        this.MinMAN += cant;
        if (this.MinMAN > this.MaxMAN) {
			this.MinMAN = this.MaxMAN;
		}
    }
    
    public void aumentarHambre(int cant) {
        this.MinHam += cant;
        if (this.MinHam > this.MaxHam) {
			this.MinHam = this.MaxHam;
		}
    }

    public void quitarHambre(int cant) {
        this.MinHam -= cant;
        if (this.MinHam < 0) {
			this.MinHam = 0;
		}
    }
    
    public void aumentarSed(int cant) {
        this.MinAGU += cant;
        if (this.MinAGU > this.MaxAGU) {
			this.MinAGU = this.MaxAGU;
		}
    }

    public void quitarSed(int cant) {
        this.MinAGU -= cant;
        if (this.MinAGU < 0) {
			this.MinAGU = 0;
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
    	for (byte b: this.userAtributos) {
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
        this.MaxSta = 20 * agil;
        this.MinSta = this.MaxSta;
        // Agua/Sed: si llega a cero produce la muerte.
        this.MaxAGU = 100;
        this.MinAGU = this.MaxAGU;
        // Hambre: si llega a cero produce la muerte.
        this.MaxHam = 100;
        this.MinHam = this.MaxHam;
        // Mana (magia y meditacion de clases mágicas)
        this.MaxMAN = clase.getManaInicial(this.userAtributos[ATRIB_INTELIGENCIA]);
        this.MinMAN = this.MaxMAN;
        // Golpe al atacar.
        this.MaxHIT = 2;
        this.MinHIT = 1;
        // Varios
        this.oro = 0; // Oro en la billetera.
        this.Exp = 0; // Puntos de experiencia ganados.
        this.ELU = 300; // Puntos necesarios para subir de nivel.
        this.ELV = 1; // Nivel inicial.
    }
    
}
