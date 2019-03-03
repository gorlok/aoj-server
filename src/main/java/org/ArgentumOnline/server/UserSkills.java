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
