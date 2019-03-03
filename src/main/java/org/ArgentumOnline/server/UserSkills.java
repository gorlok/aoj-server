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

/**
 * @author gorlok
 */
public class UserSkills {
	
	// Puntos de skills sin asignar.
	public int SkillPts = 0; 

	private byte userSkills[] = new byte[Skill.values().length];

	public byte[] skills() {
		return this.userSkills;
	}
	
	public byte get(Skill skill) {
		return this.userSkills[skill.value()];
	}
	
	public void set(Skill skill, int value) {
		this.userSkills[skill.value()] = (byte) value;
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
		this.SkillPts = val;
	}

	public int getSkillPoints() {
		return this.SkillPts;
	}

	public boolean skillsValidos() {
		int totalskpts = 0;
		for (Skill skill : Skill.values()) {
			totalskpts += Math.abs(get(skill));
		}
		return totalskpts == 10;
	}

	public void addSkillPoints(Skill skill, byte cant) {
		skills()[skill.value()] += cant;
		if (skills()[skill.value()] > Skill.MAX_SKILL_POINTS) {
			skills()[skill.value()] = Skill.MAX_SKILL_POINTS;
		}
	}

	public void subirSkills(byte[] incSkills) {
		for (Skill skill : Skill.values()) {
			byte points = incSkills[skill.value()];
			
			this.SkillPts -= points;
			
			skills()[skill.value()] += points;
			if (skills()[skill.value()] > 100) {
				this.SkillPts += (skills()[skill.value()] - 100); // devuelvo los sobrantes
				skills()[skill.value()] = 100;
			}
		}
	}
	
}
