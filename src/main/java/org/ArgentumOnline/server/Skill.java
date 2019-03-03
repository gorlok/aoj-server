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
public enum Skill {

	SKILL_Suerte("Suerte"),
	SKILL_Magia("Magia"),
	SKILL_Robar("Robar"),
	SKILL_Tacticas("Tacticas de combate"),
	SKILL_Armas("Combate con armas"),
	SKILL_Meditar("Meditar"),
	SKILL_Apuñalar("Apuñalar"),
	SKILL_Ocultarse("Ocultarse"),
	SKILL_Supervivencia("Supervivencia"),
	SKILL_Talar("Talar arboles"),
	SKILL_Comerciar("Comercio"),
	SKILL_Defensa("Defensa con escudos"),
	SKILL_Pesca("Pesca"),
	SKILL_Mineria("Mineria"),
	SKILL_Carpinteria("Carpinteria"),
	SKILL_Herreria("Herreria"),
	SKILL_Liderazgo("Liderazgo"),
	SKILL_Domar("Domar animales"),
	SKILL_Proyectiles("Armas de proyectiles"),
	SKILL_Wresterling("Wresterling"),
	SKILL_Navegacion("Navegacion");
    
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
    	return VALUES[index];
    }
    
    public byte value() {
    	return (byte) this.ordinal();
    }

}
