package org.ArgentumOnline.server;

/**
 * @author gorlok
 */
public class Skill {
	
	//// SKILLS
	public final static int MAX_SKILLS = 21;
	public final static int MAX_SKILL_POINTS = 100;
    
	public final static int SKILL_ZERO = 0; // En la posición cero del array de skills, se guardaban los skills points libres
	public final static int SKILL_Suerte = 1;
	public final static int SKILL_Magia = 2;
	public final static int SKILL_Robar = 3;
	public final static int SKILL_Tacticas = 4;
	public final static int SKILL_Armas = 5;
	public final static int SKILL_Meditar = 6;
	public final static int SKILL_Apuñalar = 7;
	public final static int SKILL_Ocultarse = 8;
	public final static int SKILL_Supervivencia = 9;
	public final static int SKILL_Talar = 10;
	public final static int SKILL_Comerciar = 11;
	public final static int SKILL_Defensa = 12;
	public final static int SKILL_Pesca = 13;
	public final static int SKILL_Mineria = 14;
	public final static int SKILL_Carpinteria = 15;
	public final static int SKILL_Herreria = 16;
	public final static int SKILL_Liderazgo = 17;
	public final static int SKILL_Domar = 18;
	public final static int SKILL_Proyectiles = 19;
	public final static int SKILL_Wresterling = 20;
	public final static int SKILL_Navegacion = 21;
    
    
    final static int SKILL_FundirMetal = 88;
	

	public final static String skillsNames[] = {
	    "",
	    "Suerte",
	    "Magia",
	    "Robar",
	    "Tacticas de combate",
	    "Combate con armas",
	    "Meditar",
	    "Apuñalar",
	    "Ocultarse",
	    "Supervivencia",
	    "Talar arboles",
	    "Comercio",
	    "Defensa con escudos",
	    "Pesca",
	    "Mineria",
	    "Carpinteria",
	    "Herreria",
	    "Liderazgo",
	    "Domar animales",
	    "Armas de proyectiles",
	    "Wresterling",
	    "Navegacion"
	};
	
	public final static byte levelSkill[] = {
	    0, 3, 5, 7, 10, 13, 15, 17, 20, 23, 25,
	    27, 30, 33, 35, 37, 40, 43, 45, 47, 50,
	    53, 55, 57, 60, 63, 65, 67, 70, 73, 75,
	    77, 80, 83, 85, 87, 90, 93, 95, 97, 100,
	    100, 100, 100, 100, 100, 100, 100, 100, 100, 100
	};

}
