/**
 * Constants.java
 *
 * Created on 14 de septiembre de 2003, 21:04
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

/**
 * @author gorlok
 */
public interface Constants {
	
	public final boolean DEBUG = false;
	
	final static int DISTANCE_CASHIER = 4; // CAJERO DE BANCO
	final static int DISTANCE_QUEST = 4;
	final static int DISTANCE_PET = 10; // MASCOTA
	final static int DISTANCE_MERCHANT = 3; // COMERCIANTE
	final static int DISTANCE_TRAINER = 10; // ENTRENADOR
	final static int DISTANCE_INFORMATION = 4;
	final static int DISTANCE_PRIEST = 10; // SACERDOTE
	final static int DISTANCE_FACTION = 10; // FACCIONARIO
    
    /*
    Public Type tAPuestas
        Ganancias As Long
        Perdidas As Long
        Jugadas As Long
    End Type
    Public Apuestas As tAPuestas

    Public EstadisticasWeb As New clsEstadisticasIPC

    Public IntervaloUserPuedeUsar As Long

    Public DeNoche As Boolean

    Public Type TCPESStats
        BytesEnviados As Long
        BytesRecibidos As Long
        BytesEnviadosXSEG As Long
        BytesRecibidosXSEG As Long
        BytesEnviadosXSEGMax As Long
        BytesRecibidosXSEGMax As Long
        BytesEnviadosXSEGCuando As Date
        BytesRecibidosXSEGCuando As Date
    End Type
    Public TCPESStats As TCPESStats
     */
	
	final static int max_border_y = 1;
	final static int min_border_y = 2;
	final static int max_border_x = 3;
	final static int min_border_x = 4;
	
	final static short heading_n = 1;
	final static short heading_e = 2;
	final static short heading_s = 3;
	final static short heading_w = 4;

    final static String VERSION = "0.12.3";
    final static int SERVER_PORT = 7666;
    
    final static int CRCKEY = 1234;

    final static int IntervaloSed = 6000;
    final static int IntervaloHambre = 6500;
    final static int IntervaloFrio = 15;
    final static int IntervaloVeneno = 500;
    final static int IntervaloInvisible = 500;
    final static int IntervaloParalizado = 500;
    final static int IntervaloInvocacion = 1001;
    
    final static int IntervaloUserPuedeAtacar = 200; // milisegs
    final static int IntervaloUserPuedeCastear = 200; // milisegs
    final static int IntervaloUserPuedeTrabajar = 200; // milisegs
    final static int IntervaloUserPuedeUsar = 200; // milisegs
    
    final static int SanaIntervaloSinDescansar = 1600;
    final static int SanaIntervaloDescansar = 100;
    final static int StaminaIntervaloSinDescansar = 30;
    final static int StaminaIntervaloDescansar = 5;
    final static int IntervaloParaConexion = 30001;
    final static int IntervaloCerrarConexion = 10; // 10 segundos.
    final static int IntervaloMinutosWs = 30; // Minutos cada WorldSave
    
    final static int IdleLimit = 5; // Minutos que un usuario puede estar ocioso.
    
    final static int TIEMPO_INICIO_MEDITAR = 3000; // milisegs.
    final static int TIEMPO_SACAR_TEXTO_ENCIMA = 1000; // milisegs.
    
    final static int MAXSPAWNATTEMPS = 60;
    
    final static int MAX_MENSAJE = 80;

    final static int TIMER_AI = 100;

    final static short FLAGORO = 21; // JAO: MAX_INVENTORY_SLOTS + 1
    
    final static int LimiteNewbie = 8;
    
    final static short POSINVALIDA = 3;
    
    final static String DATDIR = "dat";
    final static String GUILDDIR = "guilds";
    
    final static short MAX_TEXTO_HABLAR = 500;
    
    final static short HECHIZO_DARDO_MAGICO = 2;
    
    final static int APUESTA_MAXIMA = 5000;
    
    final static short TERRENO_BOSQUE = 0;
    final static short TERRENO_DESIERTO = 1;
    final static short TERRENO_NIEVE = 2;
    final static String TERRENOS[] = { "BOSQUE", "DESIERTO", "NIEVE" };
    
    final static short ZONA_CAMPO = 0;
    final static short ZONA_CIUDAD = 1;
    final static short ZONA_DUNGEON = 2;
    final static String ZONAS[] = { "CAMPO", "CIUDAD", "DUNGEON" };
    
    final static int NUM_CLASES  = 17;
    
    String[] CIUDADES_NOMBRES = { "Nix", "Ullathorpe", "Banderbill", "Lindos" };
    final static short CIUDAD_NIX = 0;
    final static short CIUDAD_ULLA = 1;
    final static short CIUDAD_BANDER = 2;
    final static short CIUDAD_LINDOS = 3;
    
    final static int CANT_MAPAS  = 285;
    final static int XMinMapSize = 1;
    final static int YMinMapSize = 1;
    final static int XMaxMapSize = 100;
    final static int YMaxMapSize = 100;
    final static int MAPA_ALTO  = 100;
    final static int MAPA_ANCHO = 100;
    
    // Constantes de razas:
    final static int RAZA_HUMANO       = 0;
    final static int RAZA_ELFO         = 1;
    final static int RAZA_ELFO_OSCURO  = 2; // DROW
    final static int RAZA_ENANO        = 3;
    final static int RAZA_GNOMO        = 4;
    
    final static String nombreRaza[] = { "Humano", "Elfo", "Elfo Oscuro", "Enano", "Gnomo" };

    // Constantes de genero:
    final static int GENERO_HOMBRE  = 0;
    final static int GENERO_MUJER   = 1;

    // Modificadores de atributos por clase:
    byte modificadorFuerza[]        = { 2, 0, 1, 3, -5 };
    byte modificadorAgilidad[]      = { 1, 2, 2, 0, 3 };
    byte modificadorConstitucion[]  = { 2, 0, 0, 3, 0 };
    byte modificadorInteligencia[]  = { 1, 2, 2, -6, 3 };
    byte modificadorCarisma[]       = { 0, 2, 2, 0, 0 };

	public final String CHARFILES_FOLDER = "charfile";
    
    // Tama�o del tileset
    final static short TileSizeX = 32;
    final static short TileSizeY = 32;
    
    // Tama�o en Tiles de la pantalla de visualizacion
    final static short XWindow = 17;
    final static short YWindow = 13;
    
    // Bordes del mapa
    final static short MinXBorder = XMinMapSize + (XWindow / 2);  // 1 + (17/2) = 9
    final static short MaxXBorder = XMaxMapSize - (XWindow / 2);
    final static short MinYBorder = YMinMapSize + (YWindow / 2);  // 1 + (13/2) = 7 
    final static short MaxYBorder = YMaxMapSize - (YWindow / 2);
    
    final static int MAX_HECHIZOS = 35;
    
    final static int MAX_OBJS_X_SLOT = 10000;
    final static int MAX_INVENTORY_OBJS = 10000;
    final static short MAX_INVENTORY_SLOTS  = 20;
    
    final static short MAX_DISTANCIA_ARCO     = 12;
    final static short MAX_DISTANCIA_MAGIA    = 18;
    
    final static int MIN_APU�ALAR = 10;
   
    final static int MAXUSERMATADOS = 9000000;

    // FXs
    final static int FXWARP = 1;
    final static int FXCURAR = 2;
    final static int FXMEDITARCHICO = 4;
    final static int FXMEDITARMEDIANO = 5;
    final static int FXMEDITARGRANDE = 6;
    
    // <<<<<< Targets >>>>>>
    final static byte uUsuarios = 1;
    final static byte uNPC = 2;
    final static byte uUsuariosYnpc = 3;
    final static byte uTerreno = 4;

    // <<<<<< Acciona sobre >>>>>>
    final static byte uPropiedades = 1;
    final static byte uEstado = 2;
    final static byte uMaterializa = 3;
    final static byte uInvocacion = 4;
    
    // HP adicionales cuando sube de nivel
    final static byte AdicionalHPGuerrero = 2 ;
    final static byte AdicionalSTLadron   = 3;
    final static byte AdicionalSTLe�ador  = 23;
    final static byte AdicionalSTPescador = 20;
    final static byte AdicionalSTMinero   = 25;
    
    //// ATRIBUTOS
    final static int NUMATRIBUTOS = 5;
    final static int ATRIB_FUERZA  = 0;
    final static int ATRIB_AGILIDAD = 1;
    final static int ATRIB_INTELIGENCIA = 2;
    final static int ATRIB_CARISMA = 3;
    final static int ATRIB_CONSTITUCION = 4;
    
    
    // Estadisticas
    final static int STAT_MAXELV = 99;
    final static int STAT_MAXHP  = 999;
    final static int STAT_MAXSTA = 999;
    final static int STAT_MAXMAN = 2000;
    final static int STAT_MAXHIT = 99;
    final static int STAT_MAXDEF = 99;
    
    //// NPCs
    final static int MAX_EXPRESIONES    = 10;
    final static int MAX_NUM_SPELLS     = 10;
    final static int MAX_NPC_NAME       = 100;
    final static int MAX_NPC_DESC       = 300;
    final static int MAX_CRIATURAS_ENTRENADOR = 20;

    final static int MAX_MASCOTAS_ENTRENADOR = 7;
    
    final static int MAX_MASCOTAS_USER = 3;
    
    final static int MAX_USER_INVENTORY_SLOTS = 20;
    final static int MAX_BANCOINVENTORY_SLOTS = 40;
    
    final static int MAXREP = 6000000;
    final static int MAX_GOLD = 90000000;
    final static int MAXEXP = 99999999;
    
    final static int MAXATRIBUTOS = 35;
    final static int MINATRIBUTOS = 6;
    
    
    final static short OBJ_INDEX_HACHA_LE�ADOR = 127;
    final static short OBJ_INDEX_CA�A = 138;
    final static short OBJ_INDEX_PIQUETE_MINERO = 187;
    final static short OBJ_INDEX_SERRUCHO_CARPINTERO = 198;
    final static short OBJ_INDEX_MARTILLO_HERRERO = 389;    
    final static short OBJ_INDEX_CUALQUIERA = 1000;
    final static short OBJ_INDEX_RED_PESCA = 543;
    final static short OBJ_INDEX_ESPADA_MATA_DRAGONES = 402;
    final static short OBJ_INDEX_FRAGATA_FANTASMAL = 87;
    final static short OBJ_INDEX_CUERPO_MUERTO = 8;
    final static short OBJ_INDEX_CABEZA_MUERTO = 500;
    //final static short ObjArboles = 4;
    
    /*
Public Const EspadaMataDragonesIndex As Integer = 402
Public Const LAUDMAGICO As Integer = 696
Public Const FLAUTAMAGICA As Integer = 208

Public Const iFragataFantasmal = 87
Public Const iFragataReal = 190
Public Const iFragataCaos = 189
Public Const iBarca = 84
Public Const iGalera = 85
Public Const iGaleon = 86
Public Const iBarcaCiuda = 395
Public Const iBarcaPk = 396
Public Const iGaleraCiuda = 397
Public Const iGaleraPk = 398
Public Const iGaleonCiuda = 399
Public Const iGaleonPk = 400

Public Enum iMinerales
    HierroCrudo = 192
    PlataCruda = 193
    OroCrudo = 194
    LingoteDeHierro = 386
    LingoteDePlata = 387
    LingoteDeOro = 388
End Enum
     */
    
    
    final static short NingunEscudo = 2;
    final static short NingunCasco = 2;
    final static short NingunArma = 2;
    
    final static short bCabeza = 1;
    final static short bPiernaIzquierda = 2;
    final static short bPiernaDerecha = 3;
    final static short bBrazoDerecho = 4;
    final static short bBrazoIzquierdo = 5;
    final static short bTorso = 6;
    
    final static short GUARDIAS = 6;
    
    final static short vlAsalto  = 100;
    final static short vlAsesino = 1000;
    final static short vlCazador = 5;
    final static short vlNoble   = 5;
    final static short vlLadron  = 25;
    final static short vlProleta = 2;
    
    final static short OBJ_ORO = 12;
    final static short OBJ_PESCADO = 139;
    final static short OBJ_TELEPORT = 378;
    
    final static short LingoteHierro = 386;
    final static short LingotePlata = 387;
    final static short LingoteOro = 388;
    final static short Le�a = 58;
    
    final static short HACHA_LE�ADOR = 127;
    final static short PIQUETE_MINERO = 187;
    final static short DAGA = 15;
    final static short FOGATA_APAG = 136;
    final static short FOGATA = 63;
    final static short ORO_MINA = 194;
    final static short PLATA_MINA = 193;
    final static short HIERRO_MINA = 192;
    final static short MARTILLO_HERRERO = 389;
    final static short SERRUCHO_CARPINTERO = 198;
    final static short DAGA_NEWBIES = 460;
    final static short MANZANA_ROJA_NEWBIES = 467;
    final static short BOTELLA_AGUA_NEWBIES = 468;
    final static short POCION_ROJA = 38;
    final static short VESTIMENTAS_COMUNES_NEWBIES_1 = 463;
    final static short VESTIMENTAS_COMUNES_NEWBIES_2 = 464;
    final static short VESTIMENTAS_COMUNES_NEWBIES_3 = 465;
    final static short ROPA_ENANO_NEWBIES = 466;

    final static short LoopAdEternum = 999;
    final static short FXSANGRE = 14;
    
    // Sonidos
    final static byte SOUND_BUMP = 1;
    final static byte SOUND_SWING = 2;
    final static byte SOUND_TALAR = 13;
    final static byte SOUND_PESCAR = 14;
    final static byte SOUND_MINERO = 15;
    final static byte SND_WARP = 3;
    final static byte SND_PUERTA = 5;
    final static byte SOUND_NIVEL = 6;
    final static byte SOUND_COMIDA = 7;
    final static byte SND_USERMUERTE = 11;
    final static byte SND_IMPACTO = 10;
    final static byte SND_IMPACTO2 = 12;
    final static byte SND_LE�ADOR = 13;
    final static byte SND_FOGATA = 14;
    final static byte SND_AVE = 21;
    final static byte SND_AVE2 = 22;
    final static byte SND_AVE3 = 34;
    final static byte SND_GRILLO = 28;
    final static byte SND_GRILLO2 = 29;
    final static byte SOUND_SACARARMA = 25;
    final static byte SND_ESCUDO = 37;
    final static byte MARTILLOHERRERO = 41;
    final static byte LABUROCARPINTERO = 42;
    final static byte SND_ACEPTADOCLAN = 43;
    final static byte SND_CREACIONCLAN = 44;
    final static byte SND_DECLAREWAR = 45;
    final static byte SND_BEBER = 46;
    
    // PECES_POSIBLES
    final static int PESCADOS_RED[] = { 139, 544, 545, 546 };
    
    // <------------------SUB-CATEGORIAS----------------->
    /*
    final static int SUBTYPE_ARMADURA = 0;
    final static int SUBTYPE_CASCO = 1;
    final static int SUBTYPE_ESCUDO = 2;
    final static int SUBTYPE_CA�A = 138;
    final static int SUBTYPE_MATADRAGONES = 1;
    */
    

}

