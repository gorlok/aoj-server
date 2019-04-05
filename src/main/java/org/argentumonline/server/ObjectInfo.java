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

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.util.IniFile;

public class ObjectInfo implements Constants {
	private static Logger log = LogManager.getLogger();
	
	public final static ObjectInfo EMPTY = new ObjectInfo(); 

    public short ObjIndex = 0;
    public String Nombre = "";
    public ObjType objType = ObjType.NONE; // Tipo enum que determina cuales son las caract del obj
    public short GrhIndex; // Indice del grafico que representa el obj
    public short GrhSecundario;
    
    //Solo contenedores (cofres)
    public short MaxItems; // FIXME
    
    public short HechizoIndex;
    
    public String ForoID;
    
    public short MinHP; // Minimo puntos de vida
    public short MaxHP; // Maximo puntos de vida
    
    public short MineralIndex;
    public short LingoteIndex;
    
    // Puntos de Stamina que da
    public short MinSta; // Minimo puntos de stamina
    
    //Pociones
    public short TipoPocion;
    public short MaxModificador;
    
    public short MinModificador;
    
    public short DuracionEfecto;
    public short MinSkill;
    
    public short MinHIT; // Minimo golpe
    public short MaxHIT; // Maximo golpe
    
    public short MinHam;
    public short MinSed;
    
    public short Def;
    public short MinDef; // Armaduras
    public short MaxDef; // Armaduras
    
    public short Ropaje; // Indice del grafico del ropaje
    
    public short WeaponAnim; // Apunta a una anim de armas
    public short ShieldAnim; // Apunta a una anim de escudo
    public short CascoAnim;
    
    public int 	 Valor;
    
    public short Llave;
    public short Clave; // Cuando clave=llave la puerta se abre o se cierra
    
    public short IndexAbierta;
    public short IndexCerrada;
    public short IndexCerradaLlave;
    
    public short LingH;
    public short LingO;
    
    public short LingP;
    public int Madera;
    
    public short SkHerreria;
    public short SkCarpinteria;
    
    public String Texto;
    
    // Clases que tienen prohibido usar este objeto.
    //public String ClasesProhibidas[] = new String[NUM_CLASES];
    private Set<String> clasesProhibidas = new HashSet<String>();
    
    public short Snd1;
    public short Snd2;
    public short Snd3;
    public short MinInt;
    
    public short StaffPower;
    public short StaffDamageBonus;
    public short DefensaMagicaMax;
    public short DefensaMagicaMin;
    byte Refuerzo;
    
    //Log As Byte 'es un objeto que queremos loguear? Pablo (ToxicWaste) 07/09/07
    //NoLog As Byte 'es un objeto que esta prohibido loguear?
    
    
    private final static int FLAG_APUÑALA = 0;
    private final static int FLAG_PROYECTIL = 1;
    private final static int FLAG_MUNICION = 2;
    private final static int FLAG_NEWBIE = 3;
    private final static int FLAG_CERRADA = 4;
    private final static int FLAG_HOMBRE = 5;
    private final static int FLAG_MUJER = 6;
    private final static int FLAG_ENVENENA = 7;
    private final static int FLAG_AGARRABLE = 8;
    private final static int FLAG_PARALIZA = 9;
    private final static int FLAG_NOSECAE = 10;
    private final static int FLAG_CRUCIAL = 11;
    private final static int FLAG_RAZA_ENANA = 12;
    private final static int FLAG_RAZA_DROW = 13;
    private final static int FLAG_RAZA_ELFA = 14;
    private final static int FLAG_RAZA_GNOMA = 15;
    private final static int FLAG_RAZA_HUMANA = 16;
    private final static int FLAG_REAL = 17;
    private final static int FLAG_CAOS = 18;
    
    
    private final static int MAX_FLAGS = 19;
    private BitSet flags = new BitSet(MAX_FLAGS);

    /** Creates a new instance of DefObjeto */
    public ObjectInfo() {
    	//
    }
    
    public boolean apuñala() {
        return this.flags.get(FLAG_APUÑALA);
    }
    
    public boolean esProyectil() {
        return this.flags.get(FLAG_PROYECTIL);
    }
    
    public boolean esMunicion() {
        return this.flags.get(FLAG_MUNICION);
    }
    
    public boolean esNewbie() {
        return this.flags.get(FLAG_NEWBIE);
    }
    
    public boolean esCrucial() {
        return this.flags.get(FLAG_CRUCIAL);
    }
    
    public boolean estaCerrada() {
        return this.flags.get(FLAG_CERRADA);
    }
    
    public boolean esParaRazaEnana() {
        return this.flags.get(FLAG_RAZA_ENANA);
    }

    public boolean esParaRazaDrow() {
        return this.flags.get(FLAG_RAZA_DROW);
    }
    
    public boolean esParaHombres() {
        return this.flags.get(FLAG_HOMBRE);
    }
    
    public boolean esParaMujeres() {
        return this.flags.get(FLAG_MUJER);
    }
    
    public boolean envenena() {
        return this.flags.get(FLAG_ENVENENA);
    }
    
    public boolean esAgarrable() {
        return this.flags.get(FLAG_AGARRABLE);
    }
    
    public boolean esForo() {
        return (this.objType == ObjType.Foros);
    }
    
    public void load(IniFile ini, short index) {
        this.ObjIndex = index;
        final String section = "OBJ" + index;
        
        this.Nombre = ini.getString(section, "Name");
        this.GrhIndex = ini.getShort(section, "GrhIndex");
        this.objType  = ObjType.value(ini.getShort(section, "ObjType"));
        this.flags.set(FLAG_NEWBIE, (ini.getInt(section, "Newbie") == 1));
        
        if (this.GrhIndex == 0) {
            log.warn("Obj.DAT => " + section + 
            		" Name=" + this.Nombre + 
            		" ObjType=" + this.objType + 
            		" GrhIndex=" + this.GrhIndex);
        }

        switch (this.objType) {
        case Armadura:
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria  = ini.getShort(section, "SkHerreria");
            this.flags.set(FLAG_REAL, (ini.getInt(section, "Real") == 1));
            this.flags.set(FLAG_CAOS, (ini.getInt(section, "Caos") == 1));
            break;

        case ESCUDO:
        case CASCO:
            this.ShieldAnim  = ini.getShort(section, "Anim");
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria  = ini.getShort(section, "SkHerreria");
            this.flags.set(FLAG_REAL, (ini.getInt(section, "Real") == 1));
            this.flags.set(FLAG_CAOS, (ini.getInt(section, "Caos") == 1));
            break;
	        
        case Weapon:
            this.WeaponAnim  = ini.getShort(section, "Anim");
            this.flags.set(FLAG_APUÑALA, (ini.getInt(section, "Apuñala") == 1));
            this.flags.set(FLAG_ENVENENA, (ini.getInt(section, "Envenena") == 1));
            this.MaxHIT  	 = ini.getShort(section, "MaxHIT");
            this.MinHIT  	 = ini.getShort(section, "MinHIT");
            this.flags.set(FLAG_PROYECTIL, (ini.getInt(section, "Proyectil") == 1));
            this.flags.set(FLAG_MUNICION, (ini.getInt(section, "Municiones") == 1));
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria  = ini.getShort(section, "SkHerreria");
            this.flags.set(FLAG_REAL, (ini.getInt(section, "Real") == 1));
            this.flags.set(FLAG_CAOS, (ini.getInt(section, "Caos") == 1));
            this.StaffPower  = ini.getShort(section, "StaffPower");
            this.StaffDamageBonus = ini.getShort(section, "StaffDamageBonus");
            this.Refuerzo 	 = (byte) ini.getShort(section, "Refuerzo");
	        break;
	
        case Instrumentos:
            this.Snd1    = ini.getShort(section, "SND1");
            this.Snd2    = ini.getShort(section, "SND2");
            this.Snd3    = ini.getShort(section, "SND3");
            this.flags.set(FLAG_REAL, (ini.getInt(section, "Real") == 1));
            this.flags.set(FLAG_CAOS, (ini.getInt(section, "Caos") == 1));
	        break;
	        
        case Minerales:
	        this.MinSkill = ini.getShort(section, "MinSkill");
	        break;
	       
        case Puertas:
        case BotellaVacia:
        case BotellaLlena:
            this.IndexAbierta      = ini.getShort(section, "IndexAbierta");
            this.IndexCerrada      = ini.getShort(section, "IndexCerrada");
            this.IndexCerradaLlave = ini.getShort(section, "IndexCerradaLlave");
	        break;
	        
        case Pociones:
            this.TipoPocion     = ini.getShort(section, "TipoPocion");
            this.MaxModificador = ini.getShort(section, "MaxModificador");
            this.MinModificador = ini.getShort(section, "MinModificador");
            this.DuracionEfecto = ini.getShort(section, "DuracionEfecto");
            break;

        case Barcos:
        	this.MinSkill = ini.getShort(section, "MinSkill");
            this.MaxHIT   = ini.getShort(section, "MaxHIT");
            this.MinHIT   = ini.getShort(section, "MinHIT");
	        break;
	
        case Flechas:
            this.MaxHIT  = ini.getShort(section, "MaxHIT");
            this.MinHIT  = ini.getShort(section, "MinHIT");
            this.flags.set(FLAG_ENVENENA, (ini.getInt(section, "Envenena") == 1));
            this.flags.set(FLAG_PARALIZA, (ini.getInt(section, "Paraliza") == 1));
            break;
            
        case Anillo:
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria  = ini.getShort(section, "SkHerreria");
            break;
            
		case Arboles:
			break;
		case Bebidas:
			break;
		case Carteles:
			break;
		case Contenedores:
			break;
		case Cualquiera:
			break;
		case Fogata:
			break;
		case Foros:
			break;
		case Fragua:
			break;
		case Guita:
			break;
		case Leña:
			break;
		case Llaves:
			break;
		case Manchas:
			break;
		case Pergaminos:
			break;
		case Teleport:
			break;
		case UseOnce:
			break;
		case Yacimiento:
			break;
		case Yunque:
			break;
        }

        this.Ropaje       = ini.getShort(section, "NumRopaje");
        this.HechizoIndex = ini.getShort(section, "HechizoIndex");
        this.LingoteIndex = ini.getShort(section, "LingoteIndex");
        this.MineralIndex = ini.getShort(section, "MineralIndex");

        this.MaxHP = ini.getShort(section, "MaxHP");
        this.MinHP = ini.getShort(section, "MinHP");

        this.flags.set(FLAG_HOMBRE, (ini.getInt(section, "Hombre") == 1));
        this.flags.set(FLAG_MUJER, (ini.getInt(section, "Mujer") == 1));
        
        this.MinHam = ini.getShort(section, "MinHam");
        this.MinSed = ini.getShort(section, "MinAgu");

        this.MinDef = ini.getShort(section, "MINDEF");
        this.MaxDef = ini.getShort(section, "MAXDEF");
        this.Def 	= (short) ((this.MinDef + this.MaxDef) / 2); 

        
        this.flags.set(FLAG_RAZA_ENANA, (ini.getInt(section, "RazaEnana") == 1));
        this.flags.set(FLAG_RAZA_DROW, (ini.getInt(section, "RazaDrow") == 1));
        this.flags.set(FLAG_RAZA_ELFA, (ini.getInt(section, "RazaElfa") == 1));
        this.flags.set(FLAG_RAZA_GNOMA, (ini.getInt(section, "RazaGnoma") == 1));
        this.flags.set(FLAG_RAZA_HUMANA, (ini.getInt(section, "RazaHumana") == 1));

        this.Valor   = ini.getInt(section, "Valor");
        this.flags.set(FLAG_CRUCIAL, (ini.getInt(section, "Crucial") == 1));
        
        this.flags.set(FLAG_CERRADA, (ini.getInt(section, "abierta") == 1));
        if (this.estaCerrada()) {
            this.Llave   = ini.getShort(section, "Llave");
        }
        this.Clave   = ini.getShort(section, "Clave");

        this.Texto  = ini.getString(section, "Texto");            
        this.GrhSecundario = ini.getShort(section, "VGrande");
        this.flags.set(FLAG_AGARRABLE, (ini.getInt(section, "Agarrable") != 1));

        this.ForoID = ini.getString(section, "ID");

        for (int j = 0; j < NUM_CLASES; j++) {
            this.clasesProhibidas.add(ini.getString(section, "CP" + (j+1)).toUpperCase());
        }

        this.DefensaMagicaMax = ini.getShort(section, "DefensaMagicaMax");
        this.DefensaMagicaMin = ini.getShort(section, "DefensaMagicaMin");
        
        this.SkCarpinteria = ini.getShort(section, "SkCarpinteria");
        if (this.SkCarpinteria > 0) {
            this.Madera  = ini.getInt(section, "Madera");
        }

        // Bebidas
        this.MinSta      = ini.getShort(section, "MinST");

        // Item no se cae
        this.flags.set(FLAG_NOSECAE, (ini.getInt(section, "NoSeCae") == 1));
    }
    
    public boolean esCaos() {
    	return this.flags.get(FLAG_CAOS);
    }
    
    public boolean esReal() {
    	return this.flags.get(FLAG_REAL);
    }
    
    public boolean noSeCae() {
    	return this.flags.get(FLAG_NOSECAE);
    }
    
    public boolean itemSeCae() {
        return
        		!esReal() &&
        		!esCaos() &&
	            !noSeCae() &&
	            this.objType != ObjType.Llaves && 
	            this.objType != ObjType.Barcos;
    }

    public boolean clasePuedeUsarItem(Clazz clazz) {
        return !this.clasesProhibidas.contains(clazz.toString().toUpperCase());
    }
    

    public boolean itemNoEsDeMapa() {
        return 
        	this.objType != ObjType.Puertas &&
            this.objType != ObjType.Foros &&
            this.objType != ObjType.Carteles &&
            this.objType != ObjType.Arboles &&
            this.objType != ObjType.Yacimiento &&
            this.objType != ObjType.Teleport;
    }

    public boolean mostrarCantidad() {
    	// MostrarCantidad
    	return objType != ObjType.Puertas
    			&& objType != ObjType.Foros
    			&& objType != ObjType.Carteles
    			&& objType != ObjType.Arboles
    			&& objType != ObjType.Yacimiento
    			&& objType != ObjType.Teleport;
    }


}
