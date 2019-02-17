/**
 * ObjectInfo.java
 *
 * Created on 15 de septiembre de 2003, 23:19
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

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectInfo implements Constants {
	private static Logger log = LogManager.getLogger();

    public short ObjIndex = 0;
    public String Nombre;
    public short ObjType; // Tipo enum que determina cuales son las caract del obj
    public short SubTipo; // Tipo enum que determina cuales son las caract del obj
    public short GrhIndex; // Indice del grafico que representa el obj
    public short GrhSecundario;
    public short Respawn;
    
    //Solo contenedores
    public short MaxItems;
    
    public Inventory m_contenido;
    
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
    
    public int Valor; // Precio
    
    public short Llave;
    public short Clave; // Cuando clave=llave la puerta se abre o se cierra
    
    public short IndexAbierta;
    public short IndexCerrada;
    public short IndexCerradaLlave;
    
    public int Resistencia;
    
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
    
    public short Real;
    public short Caos;
    
    private final static int FLAG_APUÑALA = 0;
    private final static int FLAG_PROYECTIL = 1;
    private final static int FLAG_MUNICION = 2;
    private final static int FLAG_NEWBIE = 3;
    private final static int FLAG_CERRADA = 4;
    private final static int FLAG_RAZAENANA = 5;
    private final static int FLAG_HOMBRE = 6;
    private final static int FLAG_MUJER = 7;
    private final static int FLAG_ENVENENA = 8;
    private final static int FLAG_AGARRABLE = 9;
    private final static int FLAG_PARALIZA = 10;
    private final static int FLAG_NOSECAE = 11;
    private final static int FLAG_CRUCIAL = 12;    
    
    private final static int MAX_FLAGS = 13;
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
        return this.flags.get(FLAG_RAZAENANA);
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
        return this.ObjType == OBJTYPE_FOROS;
    }
    
    public void load(IniFile ini, short index) {
        this.ObjIndex = index;
        String section = "OBJ" + index;
        
        this.Nombre = ini.getString(section, "Name");
        this.GrhIndex = ini.getShort(section, "GrhIndex");
        this.ObjType  = ini.getShort(section, "ObjType");
        this.SubTipo  = ini.getShort(section, "Subtipo");
        this.flags.set(FLAG_NEWBIE, (ini.getInt(section, "Newbie") == 1));
        this.Texto  = ini.getString(section, "Texto");            
        this.GrhSecundario = ini.getShort(section, "VGrande");
        this.flags.set(FLAG_AGARRABLE, (ini.getInt(section, "Agarrable") != 1));
        
        if (this.GrhIndex == 0) {
            log.warn("<<<<< ADVERTENCIA EN this.DAT >>>>");
            log.warn("i=" + index + " seccion=" + section);
            log.warn("Obj nombre=" + this.Nombre);
            log.warn("Obj m_body=" + this.Texto);
            log.warn("Obj grhIndex=" + this.GrhIndex);
        }
        
        this.Ropaje       = ini.getShort(section, "NumRopaje");
        this.HechizoIndex = ini.getShort(section, "HechizoIndex");
        
        if (this.SubTipo == SUBTYPE_ESCUDO) {
            this.ShieldAnim    = ini.getShort(section, "Anim");
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria    = ini.getShort(section, "SkHerreria");
        }

        if (this.SubTipo == SUBTYPE_CASCO) {
            this.CascoAnim    = ini.getShort(section, "Anim");
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria    = ini.getShort(section, "SkHerreria");
        }
        
        if (this.ObjType == OBJTYPE_WEAPON) {
            this.WeaponAnim  = ini.getShort(section, "Anim");
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria    = ini.getShort(section, "SkHerreria");
            this.flags.set(FLAG_APUÑALA, (ini.getInt(section, "Apuñala") == 1));
            this.flags.set(FLAG_ENVENENA, (ini.getInt(section, "Envenena") == 1));
            this.MaxHIT  = ini.getShort(section, "MaxHIT");
            this.MinHIT  = ini.getShort(section, "MinHIT");
            this.Real        = ini.getShort(section, "Real");
            this.Caos        = ini.getShort(section, "Caos");
            this.flags.set(FLAG_PROYECTIL, (ini.getInt(section, "Proyectil") == 1));
            this.flags.set(FLAG_MUNICION, (ini.getInt(section, "Municiones") == 1));
        }

        if (this.ObjType == OBJTYPE_ARMOUR) {
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria    = ini.getShort(section, "SkHerreria");
            this.Real        = ini.getShort(section, "Real");
            this.Caos        = ini.getShort(section, "Caos");
        }

        if (this.ObjType == OBJTYPE_HERRAMIENTAS) {
            this.LingH       = ini.getShort(section, "LingH");
            this.LingP       = ini.getShort(section, "LingP");
            this.LingO       = ini.getShort(section, "LingO");
            this.SkHerreria    = ini.getShort(section, "SkHerreria");
        }

        if (this.ObjType == OBJTYPE_INSTRUMENTOS) {
            this.Snd1    = ini.getShort(section, "SND1");
            this.Snd2    = ini.getShort(section, "SND2");
            this.Snd3    = ini.getShort(section, "SND3");
            this.MinInt  = ini.getShort(section, "MinInt");
        }

        this.LingoteIndex = ini.getShort(section, "LingoteIndex");
        this.MineralIndex = ini.getShort(section, "MineralIndex");

        if (this.ObjType == 31 || this.ObjType == 23) {
            this.MinSkill    = ini.getShort(section, "MinSkill");
        }

        this.MaxHP = ini.getShort(section, "MaxHP");
        this.MinHP = ini.getShort(section, "MinHP");
        
        this.flags.set(FLAG_HOMBRE, (ini.getInt(section, "Hombre") == 1));
        this.flags.set(FLAG_MUJER, (ini.getInt(section, "Mujer") == 1));
        
        this.MinHam = ini.getShort(section, "MinHam");
        this.MinSed = ini.getShort(section, "MinAgu");
        
        this.MinDef = ini.getShort(section, "MINDEF");
        this.MaxDef = ini.getShort(section, "MAXDEF");
        
        this.Respawn   = ini.getShort(section, "Respawn");
        this.flags.set(FLAG_RAZAENANA, (ini.getInt(section, "RazaEnana") == 1));
        
        this.Valor   = ini.getInt(section, "Valor");
        this.flags.set(FLAG_CRUCIAL, (ini.getInt(section, "Crucial") == 1));

        this.flags.set(FLAG_CERRADA, (ini.getInt(section, "abierta") == 1));
        if (this.estaCerrada()) {
            this.Llave   = ini.getShort(section, "llave");
            this.Clave   = ini.getShort(section, "Clave");
        }

        if (this.ObjType == OBJTYPE_PUERTAS || 
        this.ObjType == OBJTYPE_BOTELLAVACIA || 
        this.ObjType == OBJTYPE_BOTELLALLENA) {
            this.IndexAbierta    = ini.getShort(section, "IndexAbierta");
            this.IndexCerrada    = ini.getShort(section, "IndexCerrada");
            this.IndexCerradaLlave = ini.getShort(section, "IndexCerradaLlave");
        }

        // Puertas y llaves
        this.Clave = ini.getShort(section, "Clave");

        // Foros
        this.ForoID = ini.getString(section, "ID");

        for (int j = 0; j < NUM_CLASES; j++) {
            this.clasesProhibidas.add(ini.getString(section, "CP" + (j+1)).toUpperCase());
        }
        
        this.Resistencia = ini.getInt(section, "Resistencia");

        // Pociones
        if (this.ObjType == 11) {
            this.TipoPocion     = ini.getShort(section, "TipoPocion");
            this.MaxModificador = ini.getShort(section, "MaxModificador");
            this.MinModificador = ini.getShort(section, "MinModificador");
            this.DuracionEfecto = ini.getShort(section, "DuracionEfecto");
        }

        this.SkCarpinteria = ini.getShort(section, "SkCarpinteria");
        if (this.SkCarpinteria > 0) {
            this.Madera  = ini.getInt(section, "Madera");
        }

        if (this.ObjType == OBJTYPE_BARCOS) {
            this.MaxHIT  = ini.getShort(section, "MaxHIT");
            this.MinHIT  = ini.getShort(section, "MinHIT");
        }

        if (this.ObjType == OBJTYPE_FLECHAS) {
            this.MaxHIT  = ini.getShort(section, "MaxHIT");
            this.MinHIT  = ini.getShort(section, "MinHIT");
            this.flags.set(FLAG_ENVENENA, (ini.getInt(section, "Envenena") == 1));
            this.flags.set(FLAG_PARALIZA, (ini.getInt(section, "Paraliza") == 1));
        }

        // Bebidas
        this.MinSta      = ini.getShort(section, "MinST");

        // Item no se cae
        this.flags.set(FLAG_NOSECAE, (ini.getInt(section, "NoSeCae") == 1));
    }
    
    public boolean itemSeCae() {
        return this.Real != 1 && this.Caos != 1 && 
            this.ObjType != OBJTYPE_LLAVES && 
            this.ObjType != OBJTYPE_BARCOS &&
            !this.flags.get(FLAG_NOSECAE);
    }

    public boolean clasePuedeUsarItem(CharClass clase) {
        // Function ClasePuedeUsarItem(ByVal UserIndex As Integer, ByVal ObjIndex As Integer) As Boolean
        /*
        if (ClasesProhibidas[0] != null) {
            for (int j = 0; j < NUM_CLASES; j++) {
                if (ClasesProhibidas[j] != null && ClasesProhibidas[j].equals(Clases[clase])) {
                    return false;
                }
            }
        }
         */        
    	
        return !this.clasesProhibidas.contains(clase.getName().toUpperCase());
    }
    

    public boolean itemNoEsDeMapa() {
        return this.ObjType != OBJTYPE_PUERTAS &&
            this.ObjType != OBJTYPE_FOROS &&
            this.ObjType != OBJTYPE_CARTELES &&
            this.ObjType != OBJTYPE_ARBOLES &&
            this.ObjType != OBJTYPE_YACIMIENTO &&
            this.ObjType != OBJTYPE_TELEPORT;
    }

}
