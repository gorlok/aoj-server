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

import org.argentumonline.server.Skill;
import org.argentumonline.server.util.IniFile;

/**
 * @author gorlok
 */
public class UserSkills {
	
	// Puntos de skills sin asignar.
	public int freeSkillPts = 0; 

	private byte userSkills[] = new byte[Skill.values().length];

	public byte[] skills() {
		return this.userSkills;
	}
	
	public byte get(Skill skill) {
		return this.userSkills[skill.value()-1];
	}
	
	public void add(Skill skill, byte value) {
		this.userSkills[skill.value()-1] += value;
	}
	
	public void set(Skill skill, int value) {
		this.userSkills[skill.value()-1] = (byte) value;
	}
	
	public boolean validateSkills() {
		for (Skill skill : Skill.values()) {
			if (get(skill) < 0) {
				return false;
			}
			if (get(skill) > 100) {
				set(skill, 100);
			}
		}
		return true;
	}

	public void setSkillPoints(int val) {
		this.freeSkillPts = val;
	}

	public int getSkillPoints() {
		return this.freeSkillPts;
	}

	public boolean skillsValidos() {
		int totalskpts = 0;
		for (Skill skill : Skill.values()) {
			totalskpts += Math.abs(get(skill));
		}
		return totalskpts == 10;
	}

	public void addSkillPoints(Skill skill, byte cant) {
		add(skill, cant);
		if (get(skill) > Skill.MAX_SKILL_POINTS) {
			set(skill, Skill.MAX_SKILL_POINTS);
		}
	}

	public void subirSkills(byte[] incSkills) {
		// FIXME validar que Sum(skills) <= freeSkillPts
		int i = 0;
		for (Skill skill : Skill.values()) {
			byte points = incSkills[i++];
			
			this.freeSkillPts -= points;
			
			add(skill, points);
			if (get(skill) > Skill.MAX_SKILL_POINTS) {
				// devuelvo los sobrantes
				this.freeSkillPts += get(skill) - Skill.MAX_SKILL_POINTS; 
				set(skill, Skill.MAX_SKILL_POINTS);
			}
		}
	}

	public void loadUserSkills(IniFile ini) {
		int i = 1;
		for (Skill skill : Skill.values()) {
			set(skill, ini.getShort("SKILLS", "SK" + (i++)));
		}
		freeSkillPts = ini.getInt("STATS", "SkillPtsLibres");
	}
	
}
