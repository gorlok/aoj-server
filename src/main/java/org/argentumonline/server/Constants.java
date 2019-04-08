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

/**
 * @author gorlok
 */
public interface Constants {
	
	public final boolean DEBUG = false;
	
	public final static char NULL_CHAR = '\0';
	
    final static String VERSION = "0.12.3";
    final static int SERVER_PORT = 7666;
    final static int CRCKEY = 1234;
	
	final static int DISTANCE_CASHIER = 4; // CAJERO DE BANCO
	final static int DISTANCE_QUEST = 4;
	final static int DISTANCE_PET = 10; // MASCOTA
	final static int DISTANCE_MERCHANT = 3; // COMERCIANTE
	final static int DISTANCE_TRAINER = 10; // ENTRENADOR
	final static int DISTANCE_INFORMATION = 4;
	final static int DISTANCE_PRIEST = 10; // SACERDOTE
	final static int DISTANCE_FACTION = 10; // FACCIONARIO
	
	final static int MAPA_PRETORIANO = -1;
	
	final static int JAIL_TIME_PIQUETE_MINUTES = 5;
	
	public final static int MAX_ORO_EDIT = 5000000;
	
	public final static String STANDARD_BOUNTY_HUNTER_MESSAGE = "Se te ha otorgado un premio por ayudar al proyecto reportando bugs, el mismo está disponible en tu bóveda.";
    
    /*
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
	
    final static int IntervaloSed = 6000;
    final static int IntervaloHambre = 6500;
    final static int IntervaloFrio = 15;
    final static int IntervaloVeneno = 500;
    final static int IntervaloInvisible = 500;
    final static int IntervaloParalizado = 500;
    final static int IntervaloInvocacion = 1001;
    
    final static int IntervaloUserPuedeAtacar = 200; // ms
    final static int IntervaloUserPuedeCastear = 200; // ms
    final static int IntervaloUserPuedeTrabajar = 200; // ms
    final static int IntervaloUserPuedeUsar = 200; // ms
    
    final static int SanaIntervaloSinDescansar = 1600;
    final static int SanaIntervaloDescansar = 100;
    final static int StaminaIntervaloSinDescansar = 30;
    final static int StaminaIntervaloDescansar = 5;
    final static int IntervaloParaConexion = 30001;
    final static int IntervaloCerrarConexion = 10; // 10 segundos.
    final static int IntervaloMinutosWs = 30; // Minutos cada WorldSave
    
    final static int IdleLimit = 30; // FIXME 5 Minutos que un usuario puede estar ocioso.
    
    final static int TIEMPO_INICIO_MEDITAR = 3000; // ms.
    final static int TIEMPO_SACAR_TEXTO_ENCIMA = 1000; // ms.
    
    final static int MAXSPAWNATTEMPS = 60;
    
    final static int MAX_MENSAJE = 80;

    final static int TIMER_AI = 100;

    final static short FLAGORO = 21; // JAO: MAX_INVENTORY_SLOTS + 1
    
    final static int LimiteNewbie = 12;
    
    final static String DAT_DIR = "dat";
    final static String LOG_DIR = "log";
    final static String GUILD_DIR = "guilds";
    final static String FORUM_DIR = "forum";
    
    final static short MAX_TEXTO_HABLAR = 500;
    
    final static short HECHIZO_DARDO_MAGICO = 2;
    
    final static int APUESTA_MAXIMA = 5000;
    
    final static int NUM_CLASES  = 17;
    
	final static int CANT_MAPAS  = 285;
    final static int XMinMapSize = 1;
    final static int YMinMapSize = 1;
    final static int XMaxMapSize = 100;
    final static int YMaxMapSize = 100;
    final static int MAP_HEIGHT  = 100;
    final static int MAP_WIDTH = 100;
    
	public final String CHARFILES_FOLDER = "charfile";
    
    // Tamaño del tileset
    final static short TileSizeX = 32;
    final static short TileSizeY = 32;
    
    // Tamaño en Tiles de la pantalla de visualizacion
    final static short XWindow = 17;
    final static short YWindow = 13;
    
    // Bordes del mapa
    final static short MinXBorder = XMinMapSize + (XWindow / 2);  // 1 + (17/2) = 9
    final static short MaxXBorder = XMaxMapSize - (XWindow / 2);
    final static short MinYBorder = YMinMapSize + (YWindow / 2);  // 1 + (13/2) = 7 
    final static short MaxYBorder = YMaxMapSize - (YWindow / 2);
    
    final static int MAX_SPELLS = 35;
    
    final static int MAX_OBJS_X_SLOT = 10000;
    final static int MAX_INVENTORY_OBJS = 10000;
    final static short MAX_INVENTORY_SLOTS  = 20;
    
    final static short MAX_DISTANCIA_ARCO     = 12;
    final static short MAX_DISTANCIA_MAGIA    = 18;
    
    final static int MIN_APUÑALAR = 10;
   
    final static int MAX_USER_KILLED = 9000000;

    // FXs
    final static int FXWARP = 1;
    final static int FXCURAR = 2;
    final static int FXMEDITARCHICO = 4;
    final static int FXMEDITARMEDIANO = 5;
    final static int FXMEDITARGRANDE = 6;
    
    // Estadisticas
    final static int STAT_MAXELV = 255;
    final static int STAT_MAXHP  = 999;
    final static int STAT_MAXSTA = 999;
    final static int STAT_MAXMAN = 2000;
    final static int STAT_MAXHIT_UNDER36 = 99;
    final static int STAT_MAXHIT_OVER36 = 999;
    final static int STAT_MAXDEF = 99;
    
    //// NPCs
    final static int MAX_EXPRESIONES    = 10;
    final static int MAX_NUM_SPELLS     = 10;
    final static int MAX_NPC_NAME       = 100;
    final static int MAX_NPC_DESC       = 300;
    
    final static int MAX_TRAINER_CREATURES = 20;
    final static int MAX_TRAINER_PETS = 7;

    final static int MAX_USER_PETS = 3;
    
    final static int MAX_USER_INVENTORY_SLOTS = 20;
    final static int MAX_BANCOINVENTORY_SLOTS = 40;
    
    final static int MAXREP = 6000000;
    final static int MAX_GOLD = 90000000;
    final static int MAXEXP = 99999999;
    
    final static short OBJ_INDEX_HACHA_LEÑADOR = 127;
    final static short OBJ_INDEX_CAÑA = 138;
    final static short OBJ_INDEX_PIQUETE_MINERO = 187;
    final static short OBJ_INDEX_SERRUCHO_CARPINTERO = 198;
    final static short OBJ_INDEX_MARTILLO_HERRERO = 389;    
    final static short OBJ_INDEX_CUALQUIERA = 1000;
    final static short OBJ_INDEX_RED_PESCA = 543;
    final static short OBJ_INDEX_ESPADA_MATA_DRAGONES = 402;
    final static short OBJ_INDEX_FRAGATA_FANTASMAL = 87;
    final static short OBJ_INDEX_CUERPO_MUERTO = 8;
    final static short OBJ_INDEX_CABEZA_MUERTO = 500;
    
	public final static short EspadaMataDragonesIndex = 402;
	public final static short LAUDMAGICO = 696;
	public final static short FLAUTAMAGICA = 208;
	public final static short SUPERANILLO = 700;	
	public final static short ELEMENTAL_FUEGO = 26;
	public final static short ELEMENTAL_TIERRA = 28;

	public final static short iFragataFantasmal = 87;
	public final static short iFragataReal = 190;
	public final static short iFragataCaos = 189;
	public final static short iBarca = 84;
	public final static short iGalera = 85;
	public final static short iGaleon = 86;
	public final static short iBarcaCiuda = 395;
	public final static short iBarcaPk = 396;
	public final static short iGaleraCiuda = 397;
	public final static short iGaleraPk = 398;
	public final static short iGaleonCiuda = 399;
	public final static short iGaleonPk = 400;

/*
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
    final static short Leña = 58;
    
    final static short HACHA_LEÑADOR = 127;
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
    final static byte SOUND_WARP = 3;
    final static byte SOUND_PUERTA = 5;
    final static byte SOUND_NIVEL = 6;
    final static byte SOUND_COMIDA = 7;
    final static byte SOUND_IMPACTO = 10;
    final static byte SOUND_USER_DIE = 11;
    final static byte SOUND_IMPACTO2 = 12;
    final static byte SOUND_TALAR = 13;
    final static byte SOUND_PESCAR = 14;
    final static byte SOUND_MINERO = 15;
    final static byte SOUND_AVE = 21;
    final static byte SOUND_AVE2 = 22;
    final static byte SOUND_SACAR_ARMA = 25;
    final static byte SOUND_GRILLO = 28;
    final static byte SOUND_GRILLO2 = 29;
    final static byte SOUND_AVE3 = 34;
    final static byte SOUND_ESCUDO = 37;
    final static byte SOUND_MARTILLO_HERRERO = 41;
    final static byte SOUND_LABURO_CARPINTERO = 42;
    final static byte SOUND_ACEPTADO_CLAN = 43;
    final static byte SOUND_CREACION_CLAN = 44;
    final static byte SOUND_DECLARE_WAR = 45;
    final static byte SOUND_BEBER = 46;
    final static byte SOUND_CONVERSION_BARCO = 55;
    final static byte SOUND_FLECHA_IMPACTO = 65;
    final static byte SOUND_MUERTE_MUJER = 74;
    final static byte SOUND_MORFAR_MANZANA = 82;
    
    
    // PECES_POSIBLES
    final static int PESCADOS_RED[] = { 139, 544, 545, 546 };
    
}

