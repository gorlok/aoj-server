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

/**
 * @author gorlok
 */
public enum Skill {
	
	/* 1*/ SKILL_Suerte("Suerte"),
	/* 2*/ SKILL_Magia("Magia"),
	/* 3*/ SKILL_Robar("Robar"),
	/* 4*/ SKILL_Tacticas("Tacticas de combate"),
	/* 5*/ SKILL_Armas("Combate con armas"),
	/* 6*/ SKILL_Meditar("Meditar"),
	/* 7*/ SKILL_Apuñalar("Apuñalar"),
	/* 8*/ SKILL_Ocultarse("Ocultarse"),
	/* 9*/ SKILL_Supervivencia("Supervivencia"),
	/*10*/ SKILL_Talar("Talar arboles"),
	/*11*/ SKILL_Comerciar("Comercio"),
	/*12*/ SKILL_Defensa("Defensa con escudos"),
	/*13*/ SKILL_Pesca("Pesca"),
	/*14*/ SKILL_Mineria("Mineria"),
	/*15*/ SKILL_Carpinteria("Carpinteria"),
	/*16*/ SKILL_Herreria("Herreria"),
	/*17*/ SKILL_Liderazgo("Liderazgo"),
	/*18*/ SKILL_Domar("Domar animales"),
	/*19*/ SKILL_Proyectiles("Armas de proyectiles"),
	/*20*/ SKILL_Wresterling("Wresterling"),
	/*21*/ SKILL_Navegacion("Navegacion");
    
	public final static byte SKILL_FundirMetal = 88;
	
	public final static byte MAX_SKILL_POINTS = 100;
    
	public final static byte levelSkill[] = {
		    0, 3, 5, 7, 10, 13, 15, 17, 20, 23, 25,
		    27, 30, 33, 35, 37, 40, 43, 45, 47, 50,
		    53, 55, 57, 60, 63, 65, 67, 70, 73, 75,
		    77, 80, 83, 85, 87, 90, 93, 95, 97, 100,
		    100, 100, 100, 100, 100, 100, 100, 100, 100, 100
		};
	
    private String name;
    
    private Skill(String name) {
    	this.name = name;
	}
    
    @Override
    public String toString() {
    	return this.name;
    }
    
    private static final Skill[] VALUES = Skill.values();
    public static Skill value(int index) {
    	return VALUES[index-1];
    }
    
    public static Skill byName(String skillName) {
    	return Arrays.stream(VALUES)
    			.filter( s -> skillName.equalsIgnoreCase(s.name))
    			.findFirst().orElse(null);
    }
    
    public byte value() {
    	return (byte) (this.ordinal() + 1);
    }

}
