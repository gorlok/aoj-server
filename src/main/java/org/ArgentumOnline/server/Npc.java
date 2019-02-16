/**
 * NPC.java
 *
 * Created on 14 de septiembre de 2003, 21:07
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
import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.aStar.Node;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.MapPos.Direction;
import org.ArgentumOnline.server.protocol.ServerPacketID;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class Npc extends AbstractCharacter implements Constants {
	private static Logger log = LogManager.getLogger();
    
	public int areaID = 0;
	
	public int areaPerteneceX = 0;
	public int areaPerteneceY = 0;
	
	public int areaRecibeX = 0;
	public int areaRecibeY = 0;
	
	public int minX = 0;
	public int minY = 0;
	
    public final static short MOV_ESTATICO = 1;
    public final static short MOV_MUEVE_AL_AZAR = 2;
    public final static short MOV_NPC_MALO_ATACA_USUARIOS_BUENOS = 3;
    public final static short MOV_NPCDEFENSA = 4;
    public final static short MOV_GUARDIAS_ATACAN_CRIMINALES = 5;
    public final static short MOV_SIGUE_AMO = 8;
    public final static short MOV_NPC_ATACA_NPC = 9;
    public final static short MOV_NPC_PATHFINDING = 10;
    public final static short MOV_GUARDIAS_ATACAN_CIUDADANOS = 11;
    
    public final static short NPCTYPE_COMUN = 0;
    public final static short NPCTYPE_SACERDOTE = 1;
    public final static short NPCTYPE_GUARDIAS = 2;
    public final static short NPCTYPE_ENTRENADOR = 3;
    public final static short NPCTYPE_BANQUERO = 4;
    public final static short NPCTYPE_NOBLE = 5;
    public final static short NPCTYPE_GUARDIA_REAL = 5; // Guardias Reales
    public final static short NPCTYPE_DRAGON = 6;
    public final static short NPCTYPE_TIMBERO = 7;
    public final static short NPCTYPE_GUARDIAS_CAOS = 8; // FIXME
    public final static short NPCTYPE_QUEST = 8; // FIXME
    public final static short NPCTYPE_AMIGOQUEST = 9;
    public final static short NPCTYPE_GUARDIA_CAOS = 11; // Guardias Caos
    
    public final static short GUARDIAS_PERSIGUEN_CIUDADANOS = 1;
    public final static short GUARDIAS_PERSIGUEN_CRIMINALES = 0;

    long m_lastMove = 0;
    
    public String m_name = "";
    public String m_desc = "";
    
    short m_NPCtype = 0;
    int   m_numero  = 0;
    short m_level   = 0;

    private final static int FLAG_INV_RESPAWN   = 0;
    private final static int FLAG_COMERCIA      = 1;
    private final static int FLAG_ENVENENA      = 2;
    private final static int FLAG_ATACABLE      = 3;
    private final static int FLAG_HOSTIL        = 4;
    private final static int FLAG_PUEDE_ATACAR  = 5;
    private final static int FLAG_AFECTA_PARALISIS = 6;
    private final static int FLAG_GOLPE_EXACTO  = 7;
    private final static int FLAG_NPC_ACTIVE    = 8; // ¿Esta vivo?
    private final static int FLAG_FOLLOW        = 9;
    private final static int FLAG_FACCION       = 10;
    private final static int FLAG_OLD_HOSTILE   = 11;
    private final static int FLAG_AGUA_VALIDA   = 12;
    private final static int FLAG_TIERRA_INVALIDA = 13;
    private final static int FLAG_USE_AI_NOW    = 14;
    private final static int FLAG_ATTACKING     = 15;
    private final static int FLAG_BACKUP        = 16;
    private final static int FLAG_RESPAWN_ORIG_POS= 17; // POS_ORIG
    private final static int FLAG_ENVENENADO    = 18;
    private final static int FLAG_PARALIZADO    = 19;
    private final static int FLAG_INVISIBLE     = 20;
    private final static int FLAG_MALDICION     = 21;
    private final static int FLAG_BENDICION     = 22;
    private final static int FLAG_RESPAWN       = 23;
    private final static int FLAG_LANZA_SPELLS  = 24;

    private final static int MAX_FLAGS          = 25;
    private BitSet m_flags = new BitSet(MAX_FLAGS);
    /*
    boolean m_invReSpawn  = false;
    boolean m_comercia    = false;
    boolean m_veneno      = false;
    boolean m_attackable  = false;
    boolean m_hostile     = false;
    boolean m_canAttack   = false;
    */
    
    int   m_target    = 0;
    Npc   m_targetNpc = null;
    short m_tipoItems = 0;
    
    short m_skillDomar = 0;
    short m_movement   = 0;
    
    int m_poderAtaque  = 0;
    int m_poderEvasion = 0;
    int m_inflacion    = 0; // TODO REVISAR
    int m_giveEXP      = 0;
    int m_giveGLD      = 0;
    
    //int m_expDada = 0;
    int m_expCount = 0;
    
    boolean m_deQuest = false;
    short m_guardiaPersigue = GUARDIAS_PERSIGUEN_CRIMINALES;
    
    public short   m_domable   = 0;
    public short   m_oldMovement   = 0;
    public String  m_attackedBy = "";
    public short   m_snd1 = 0; // Sonido ataque NPC
    public short   m_snd2 = 0; // Sonido ataque exitoso NPC
    public short   m_snd3 = 0; // Sonido muere NPC
    public short   m_snd4 = 0; // ???
    
    public NpcStats m_estads = new NpcStats();
    
    NpcCounters m_contadores = new NpcCounters();
    
    byte  m_nroExpresiones = 0;
    String m_expresiones[] = new String[MAX_EXPRESIONES];
    
    byte  m_nroSpells = 0;
    short m_spells[] = new short[MAX_NUM_SPELLS];  // le da vida ;)
    
    // <<<<Entrenadores>>>>>
    byte m_criaturas_entrenador_cant = 0;
    TrainerMascot m_criaturas_entrenador[] = new TrainerMascot[MAX_CRIATURAS_ENTRENADOR];
    
    // fixme - esto deberia ser el nombre del usuario, pero primero habria que
    // resolver "eficientemente" la recuperacion de un cliente por nombre.
    Client petUserOwner = null;
    short petNpcOwner  = 0;
    short m_mascotas    = 0;
    Npc m_mascota[] = new Npc[MAXMASCOTASENTRENADOR];
    
    public class PFINFO {
    	MapPos m_targetPos;
    	Client m_targetUser;
    	
    	PFINFO(MapPos pos, Client user) {
    		this.m_targetPos = pos;
    		this.m_targetUser = user;
    	}
    }
    PFINFO m_pfinfo; // FIXME
    List<Node> current_path = null;
    int current_step = 0;
    
    
    Inventory m_inv = new Inventory(20);
    
    MapPos m_pos  = MapPos.empty();
    MapPos m_orig  = MapPos.empty();
    
    AojServer server;
    
    public static Npc crearNPC(int nroNPC, MapPos orig, boolean bajoTecho, AojServer server) {
        // Crea un NPC del tipo NRONPC
        Npc npc = server.crearNPC(nroNPC, false);
        Map mapa = server.getMapa(orig.map);
        MapPos tmp;
        tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.esAguaValida(), npc.esTierraInvalida(), bajoTecho);
        
        if (tmp != null) {    
            npc.m_pos = tmp;
            mapa.entrar(npc, tmp.x, tmp.y);
            return npc;
        }
    	server.eliminarNPC(npc);
    	return null;
    }
    
    /** Creates a new instance of NPC */
    public Npc(int npc_numero, boolean loadBackup, AojServer server) {
    	this.server = server;
        this.setId(server.getNextId());
        this.m_numero = npc_numero;
        initCriaturasEntrenador();
        initExpresiones();
        initSpells();
        loadInfoNPC(this.m_numero, loadBackup);
    }
    
    public static Npc spawnNpc(int indiceNPC, MapPos orig, boolean conFX, boolean conRespawn) {
        // Crea un NPC del tipo indiceNPC
    	AojServer server = AojServer.instance();
        Npc npc = server.crearNPC(indiceNPC, false);
        if (!conRespawn) {
			npc.m_flags.set(FLAG_RESPAWN, false);
		}
        Map mapa = server.getMapa(orig.map);
        boolean hayPosValida = false;
        MapPos tmp;
        int i = 0;
        while (!hayPosValida && i < MAXSPAWNATTEMPS) {
            if (!orig.isValid()) {
                orig.x = (short) Util.Azar(1, MAPA_ANCHO);
                orig.y = (short) Util.Azar(1, MAPA_ALTO);
            }
            tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.esAguaValida(), npc.esTierraInvalida(), false);
            if (tmp != null) {
				orig = tmp;
			}
            if (mapa.testSpawnTriggerNpc(orig, false)) {
                // Necesita ser respawned en un lugar especifico
                npc.m_pos = orig.copy();
                
                mapa.entrar(npc, orig.x, orig.y);
                hayPosValida = true;
            } else {
                orig.x = 0;
                orig.y = 0;
            }
            i++;
        }
        if (!hayPosValida) {
        	server.eliminarNPC(npc);
        	System.out.println("OJO: NO HAY POSICION VALIDA !!!");
        	return null;
        } else if (conFX) {
            npc.enviarSonido(SND_WARP);
            npc.enviarCFX(FXWARP, 0);
        }
        npc.activar();
        
        return npc;
    }
    
    public void reSpawnNpc() {
        //////////// FIXME - HAY QUE USAR LA POSICION ORIGINAL (m_orig) REVISAR !!!
        if (puedeReSpawn()) {
        	if (respawnOrigPos()) {
        		spawnNpc(this.m_numero, this.m_orig, false, true); // TODO: PROBAR !!! 
        	} else {
        		spawnNpc(this.m_numero, MapPos.mxy(this.m_pos.map, (short)0, (short)0), false, true); // TODO: PROBAR !!!
        	}
        } else {
        	System.out.println("{{{{{{{ DEBUG }}}}}}} NO PUEDE RESPAWN !!!");
        }
    }
    
    public void resetFlags() {
        // Clear the npc's flags
        this.m_flags.set(FLAG_AFECTA_PARALISIS, false);
        this.m_flags.set(FLAG_AGUA_VALIDA, false);
        this.m_flags.set(FLAG_ATTACKING, false);
        this.m_flags.set(FLAG_BACKUP, false);
        this.m_flags.set(FLAG_BENDICION, false);
        this.m_flags.set(FLAG_ENVENENADO, false);
        this.m_flags.set(FLAG_FACCION, false);
        this.m_flags.set(FLAG_FOLLOW, false);
        this.m_flags.set(FLAG_GOLPE_EXACTO, false);
        this.m_flags.set(FLAG_INVISIBLE, false);
        this.m_flags.set(FLAG_MALDICION, false);
        this.m_flags.set(FLAG_OLD_HOSTILE, false);
        this.m_flags.set(FLAG_PARALIZADO, false);
        this.m_flags.set(FLAG_TIERRA_INVALIDA, false);
        this.m_flags.set(FLAG_USE_AI_NOW, false);
        this.m_flags.set(FLAG_RESPAWN_ORIG_POS, false); // ??? no se usa ???
        this.m_flags.set(FLAG_RESPAWN, true);
        this.m_flags.set(FLAG_LANZA_SPELLS, false);
        this.m_nroSpells = 0;
        this.m_attackedBy   = "";
        this.m_domable    = 0;
        this.m_oldMovement  = 0;
        this.m_snd1 = 0;
        this.m_snd2 = 0;
        this.m_snd3 = 0;
        this.m_snd4 = 0;
    }

    
	/**
	 * JAO: with this, we set the user area ID
	 */
	public void setArea(int id) {
		if (DEBUG)
			System.out.println("AREAID:" + id);
		this.areaID = id;}
	
	/**
	 * JAO: setter in X area
	 */
	public void setAreaPerteneceX(int value) {this.areaPerteneceX = value;}
	
	/**
	 * JAO: setter in Y area
	 */
	
	public void setAreaPerteneceY(int value) {this.areaPerteneceY = value;}
	
	/**
	 * JAO: adyacent X user area
	 */
	
	public void setAreaRecibeX(int value) {this.areaRecibeX = value;}
	
	/**
	 * JAO: adyacent Y user area
	 */
	
	public void setAreaRecibeY(int value) {this.areaRecibeY = value;}
	
	/**
	 * JAO: min x pos area
	 */
	
	public void setMinX(int value) {this.minX = value;}
	
	/**
	 * JAO: min y pos area
	 */
	
	public void setMinY(int value) {this.minY = value;}
	
	/**
	 * JAO: give the area in X
	 */
	
	public int getAreaPerteneceX() {return this.areaPerteneceX;}
	
	/**
	 * JAO: give the area in Y
	 */
	
	public int getAreaPerteneceY() {return this.areaPerteneceY;}
	
	/**
	 * JAO: give the adyacent area X 
	 */
	
	public int getAreaRecibeX() {return this.areaRecibeX;}
	
	/**
	 * JAO: give the adyacent area Y
	 */
	
	public int getAreaRecibeY() {return this.areaRecibeY;}
	
	/**
	 * JAO: return the lowest value in X
	 */
	
	public int getMinX() {return this.minX;}
	
	/**
	 * JAO: return the lowest value in Y 
	 */
	
	public int getMinY() {return this.minY;}
	
	public int getArea() {return this.areaID;}

    public boolean esDeQuest() {
        return this.m_deQuest;
    }

    public short getSonidoAtaqueNpc() {
        return this.m_snd1;
    }
    
    public short getSonidoAtaqueExitoso() {
        return this.m_snd2;
    }
    
    public short getSonidoMuereNpc() {
        return this.m_snd3;
    }
        
    public boolean lanzaSpells() {
        return this.m_flags.get(FLAG_LANZA_SPELLS);
    }
    
    public boolean getBackup() {
        return this.m_flags.get(FLAG_BACKUP);
    }
    
    public void volverMaldito() {
        this.m_flags.set(FLAG_MALDICION, true);
    }
    
    public void quitarMaldicion() {
        this.m_flags.set(FLAG_MALDICION, false);
    }
    
    public void volverBendito() {
        this.m_flags.set(FLAG_BENDICION, true);
    }
    
    public boolean esHostil() {
        return this.m_flags.get(FLAG_HOSTIL);
    }
    
    public boolean estaInvisible() {
        return this.m_flags.get(FLAG_INVISIBLE);
    }
    
    public boolean respawnOrigPos() {
        return this.m_flags.get(FLAG_RESPAWN_ORIG_POS);
    }
       
    public void hacerInvisible() {
        this.m_flags.set(FLAG_INVISIBLE, true);
    }
    
    public void hacerVisible() {
        this.m_flags.set(FLAG_INVISIBLE, false);
    }
    
    public boolean puedeAtacar() {
        return this.m_flags.get(FLAG_PUEDE_ATACAR);
    }
    
    public void setPuedeAtacar(boolean estado) {
        this.m_flags.set(FLAG_PUEDE_ATACAR, estado);
    }
    
    public boolean esFaccion() {
        return this.m_flags.get(FLAG_FACCION);
    }
    
    private boolean puedeEnvenenar() {
        return this.m_flags.get(FLAG_ENVENENA);
    }
    
    public void envenenar() {
        this.m_flags.set(FLAG_ENVENENA, true);
    }
    
    public void curarVeneno() {
        this.m_flags.set(FLAG_ENVENENA, false);
    }
    
    public boolean puedeReSpawn() {
        return this.m_flags.get(FLAG_RESPAWN);
    }
    
    private boolean invReSpawn() {
        return this.m_flags.get(FLAG_INV_RESPAWN);
    }
    
    private boolean isFollowing() {
        return this.m_flags.get(FLAG_FOLLOW);
    }
    
    public boolean afectaParalisis() {
        return this.m_flags.get(FLAG_AFECTA_PARALISIS);
    }
    
    public void paralizar() {
        this.m_flags.set(FLAG_PARALIZADO, true);
        this.m_contadores.Paralisis = IntervaloParalizado;
    }
    
    public void desparalizar() {
        this.m_flags.set(FLAG_PARALIZADO, false);
        this.m_contadores.Paralisis = 0;
    }
    
    @Override
	public String toString() {
        return this.m_name + " (id=" + this.getId() + ")";
    }
    
    public String getName() {
    	return this.m_name;
    }
    
    public Inventory getInv() {
        return this.m_inv;
    }
    
    public MapPos getPos() {
        return this.m_pos;
    }
    
    public MapPos getOrig() {
        return this.m_orig;
    }
    
    public short getCantMascotas() {
        return this.m_mascotas;
    }
    
    public boolean estaActivo() {
        return this.m_flags.get(FLAG_NPC_ACTIVE);
    }
    
	public void putAreas(int ax, int ay) {
		areaPerteneceX = ax;
		areaPerteneceY = ay;
	}
	
	public int getAreaX() {
		return areaPerteneceX;
	}
	
	public int getAreaY() {
		return areaPerteneceY;
	}
    
    public int getPoderAtaque() {
        return this.m_poderAtaque;
    }
    
    public int getPoderEvasion() {
        return this.m_poderEvasion;
    }
    
    public boolean getAttackable() {
        return this.m_flags.get(FLAG_ATACABLE);
    }
    
    public boolean esAguaValida() {
        return this.m_flags.get(FLAG_AGUA_VALIDA);
    }
    
    public boolean esTierraInvalida() {
        return this.m_flags.get(FLAG_TIERRA_INVALIDA);
    }
    
    public String getDesc() {
        return this.m_desc;
    }
    
    public int getNumero() {
        return this.m_numero;
    }
    
    public Client getPetUserOwner() {
		return petUserOwner;
	}
    
    public void setPetUserOwner(Client petUserOwner) {
		this.petUserOwner = petUserOwner;
	}
    
    public void releasePet() {
    	this.petUserOwner = null;
    	this.petNpcOwner = 0;
    }
    
    public boolean estaParalizado() {
        return this.m_flags.get(FLAG_PARALIZADO);
    }
    
    public boolean comercia() {
        return this.m_flags.get(FLAG_COMERCIA);
    }
    
    public boolean esBanquero() {
    	return getNPCtype() == Npc.NPCTYPE_BANQUERO;
    }
    
    public boolean esSacerdote() {
    	return getNPCtype() == Npc.NPCTYPE_SACERDOTE;
    }
    
    public boolean esNoble() {
    	return getNPCtype() == Npc.NPCTYPE_NOBLE;
    }
    
    public void activar() {
        this.m_flags.set(FLAG_NPC_ACTIVE, true);
    }
    
    public void setAttackedBy(String nick) {
        this.m_attackedBy = nick;
    }
    
    public boolean atacadoPorUsuario() {
        return this.m_attackedBy.length() > 0;
    }
    
    public void setGiveGLD(int val) {
        this.m_giveGLD = val;
    }
    
    public int getTarget() {
        return this.m_target;
    }
    
    public short getCantCriaturas() {
        return this.m_criaturas_entrenador_cant;
    }

    public void setTarget(int target) {
        this.m_target = target;
    }
    
    public short getNPCtype() {
        return this.m_NPCtype;
    }
    
    public NpcStats getEstads() {
        return this.m_estads;
    }
    
    public NpcCounters getContadores() {
        return this.m_contadores;
    }
    
    public void setPetNpcOwner(Npc petOwner) {
        this.petNpcOwner = petOwner.getId();
    }
    
    public short getCriaturaIndex(short slot) {
        if (slot > 0 && slot <= MAX_CRIATURAS_ENTRENADOR) {
			return this.m_criaturas_entrenador[slot-1].npc_index;
		} 
		return 0;
    }
    
    public short domable() {
        return this.m_domable;
    }
        
    public Object[] ccParams() {
    	Object[] params = {getInfoChar().getCuerpo(), getInfoChar().getCabeza(), 
    			getInfoChar().getDir(), getId(), getPos().x, getPos().y};
    	
    	return params;
    }
    
    private static IniFile iniNPC = null;
    private static IniFile iniHostiles = null;
    private static IniFile iniBackup = null;
    
    private void loadInfoNPC(int npc_ind, boolean loadBackup) {
        IniFile ini = getIniFile(npc_ind, loadBackup);
        leerNpc(ini, npc_ind);
    }
    
    private IniFile getIniFile(int npc_ind, boolean loadBackup) {
        if (loadBackup) {
            if (iniBackup == null) {
                iniBackup = new IniFile();
                try {
                    iniBackup.load("worldBackup" + java.io.File.separator + "backNPCs.dat");
                } catch (java.io.FileNotFoundException e) {
                    e.printStackTrace();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            // Si esta en el backup, lo usamos. Sino, procedemos normalmente.
            if (!iniBackup.getString("NPC" + npc_ind, "Name").equals("")) {
				return iniBackup;
			}
        }
        if (npc_ind < 500) {
            if (iniNPC == null) {
                iniNPC = new IniFile();
                try {
                    iniNPC.load(DATDIR + java.io.File.separator + "NPCs.dat");
                } catch (java.io.FileNotFoundException e) {
                    e.printStackTrace();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            return iniNPC;
        }
        if (iniHostiles == null) {
            iniHostiles = new IniFile();
            try {
                iniHostiles.load(DATDIR + java.io.File.separator + "NPCs-HOSTILES.dat");
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return iniHostiles;
    }
    
    public void resetNpcMainInfo() {
        this.m_flags.clear();
        this.m_giveEXP = 0;
        this.m_giveGLD = 0;
        this.m_inflacion = 0;
        this.m_level = 0;
        if (this.petUserOwner != null) {
            this.petUserOwner.quitarMascota(this);
            this.petUserOwner = null;
        }
        if (this.petNpcOwner > 0) {
            this.server.getNPC(this.petNpcOwner).quitarMascotaNpc(this);
            this.petNpcOwner = 0;
        }
        this.m_mascotas = 0;
        this.m_mascota = new Npc[MAXMASCOTASENTRENADOR];
        this.m_movement = 0;
        this.m_name = "NPC SIN INICIAR";
        this.m_NPCtype = 0;
        this.m_numero = 0;
        this.m_poderAtaque = 0;
        this.m_poderEvasion = 0;
        this.m_pos = MapPos.empty();
        this.m_skillDomar = 0;
        this.m_target = 0;
        this.m_targetNpc = null;
        this.m_tipoItems = 0;
        this.m_desc = "";
        initSpells();
        initCriaturasEntrenador();
        initExpresiones();
        this.m_infoChar.reset();
    }

    public void resetNpcCounters() {
        this.m_contadores.reset();
    }
    
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'                        Modulo NPC
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'Contiene todas las rutinas necesarias para cotrolar los
    //'NPCs menos la rutina de AI que se encuentra en el modulo
    //'AI_NPCs para su mejor comprension.
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    
    public void quitarMascotaNpc(Npc mascota) {
        for (int i = 0; i < this.m_mascota.length; i++) {
            if (this.m_mascota[i] == mascota) {
                this.m_mascota[i] = null;
                this.m_mascotas--;
                return;
            }
        }
    }
    
    public void agregarMascota(Npc mascota) {
    	for (int i = 0; i < this.m_mascota.length; i++) {
    		if (this.m_mascota[i] == null) {
    			this.m_mascota[i] = mascota;
                mascota.setPetNpcOwner(this);
                this.m_mascotas++;
                return;
    		}
    	}
    }
    
    public void calcularDarExp(Client cliente, int daño) {
        // [Alejo]
        // Modifique un poco el sistema de exp por golpe, ahora
        // son 2/3 de la exp mientras esta vivo, el resto se
        // obtiene al matarlo.
        int expSinMorir = (2 * this.m_giveEXP ) / 3;
        int totalNpcVida = getEstads().MaxHP;
        if (totalNpcVida <= 0) {
			return;
		}
        if (daño < 0) {
			daño = 0;
		}
        if (daño > getEstads().MinHP) {
			daño = getEstads().MinHP;
		}
        int expADar = (daño * expSinMorir) / totalNpcVida;
        
        if (expADar <= 0) {
			return;
		}
        if (expADar > this.m_expCount) {
            expADar = this.m_expCount;
            this.m_expCount = 0;
        } else {
            this.m_expCount -= expADar;
        }
        if (expADar > 0) {
        	expADar = expADar * 1000;
            cliente.getEstads().addExp(expADar);
            cliente.enviarMensaje("Has ganado " + expADar + " puntos de experiencia.", FontType.FIGHT);
            cliente.refreshStatus(5);
            cliente.checkUserLevel();
        }
    }
        
    public void muereNpc(Client cliente) {
        ////////////// FIXME
        // Lo mato un usuario?
        if (cliente != null) {
        	boolean eraCrimi = cliente.esCriminal();
        	
            MapPos pos = cliente.getPos();
            Map m = this.server.getMapa(pos.map);
            
            if (this.m_snd3 > 0) {
               // m.enviarAlArea(pos.x, pos.y, MSG_TW, this.m_snd3);
            	m.enviarAlArea(pos.x, pos.y, ServerPacketID.MSG_TW, (byte) this.m_snd3, cliente.getPos().x, cliente.getPos().y);
            }
            cliente.getFlags().TargetNpc = 0;
            cliente.getFlags().TargetNpcTipo = 0;
            // El user que lo mato tiene mascotas?
            if (cliente.m_cantMascotas > 0) {
                cliente.mascotasFollowAmo(this);
            }
            cliente.enviarMensaje("Has matado la criatura!", FontType.FIGHT);
            if (this.m_expCount > 0) {
                cliente.getEstads().addExp(this.m_expCount);
                cliente.enviarMensaje("Has ganado " + this.m_expCount + " puntos de experiencia.", FontType.FIGHT);
            } else {
                cliente.enviarMensaje("No has ganado experiencia al matar la criatura.", FontType.FIGHT);
            }
            cliente.getEstads().incNPCsMuertos();
            cliente.getQuest().checkNpcEnemigo(cliente, this);

            if (this.m_estads.Alineacion == 0) {
                if (this.m_numero == GUARDIAS) {
                    cliente.volverCriminal();
                }
                if (!cliente.esDios()) {
                    cliente.getReputacion().incAsesino(vlAsesino);
                }
            } else if (this.m_estads.Alineacion == 1) {
                cliente.getReputacion().incPlebe(vlCazador);
            } else if (this.m_estads.Alineacion == 2) {
                cliente.getReputacion().incNoble(vlAsesino / 2);
            } else if (this.m_estads.Alineacion == 4) {
                cliente.getReputacion().incPlebe(vlCazador);
            }
            // Controla el nivel del usuario
            cliente.checkUserLevel();

            //Agush: updateamos de ser necesario ;-)
            if (cliente.esCriminal() != eraCrimi && eraCrimi == true) cliente.refreshCiu();
            
        }
       
        	
        
        if (this.petUserOwner == null) {
            // Tiramos el oro
            tirarOro();
            // Tiramos el inventario
            tirarItems();
        }
        //short mapa = m_pos.mapa;
        // Quitamos el npc
        quitarNPC();
        // ReSpawn o no
        //reSpawnNpc(mapa);
        reSpawnNpc();
    }
    
    private void tirarItems() {
        // NPC_TIRAR_ITEMS
        // TIRA TODOS LOS ITEMS DEL NPC
        if (this.m_inv.getSize() > 0) {
            for (int i = 1; i <= this.m_inv.getSize(); i++) {
                if (this.m_inv.getObjeto(i) != null && this.m_inv.getObjeto(i).objid > 0) {
                    Map m = this.server.getMapa(this.m_pos.map);
                    m.tirarItemAlPiso(this.m_pos.x, this.m_pos.y, new InventoryObject(this.m_inv.getObjeto(i).objid, this.m_inv.getObjeto(i).cant));
                }
            }
        }
    }
    
    public void quitarNPC() {
    	
    	if (DEBUG)
    		System.out.println("NPC BORRADO");
    	    	
        this.m_flags.set(FLAG_NPC_ACTIVE, false);
        Map mapa = this.server.getMapa(this.m_pos.map);
        
        //JAO: Nuevo sistema de áreas!! ;-)
        //mapa.areasData.dieNpc(this);
        mapa.areasData.resetNpc(this);
        
        if (mapa != null && this.m_pos.isValid()) {
            mapa.salir(this);
        }
        
        mapa.enviarAlArea(this.m_pos.x,this.m_pos.y,ServerPacketID.MSG_BP, this.getId());
        
        // Nos aseguramos de que el inventario sea removido...
        // asi los lobos no volveran a tirar armaduras ;))
        this.m_inv.clear();
        if (this.petUserOwner != null) {
        	this.petUserOwner.quitarMascota(this);
        }
        Npc masterNpc = this.server.getNPC(this.petNpcOwner);
        if (masterNpc != null) {
        	masterNpc.quitarMascotaNpc(this);
        }
        this.server.eliminarNPC(this);
    }
    
    public void efectoParalisisNpc() {
        if (this.m_contadores.Paralisis > 0) {
            this.m_contadores.Paralisis -= 1;
        } else {
            this.m_flags.set(FLAG_PARALIZADO, false);
        }
    }
    
    public void enviarMP() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa != null) {
           // mapa.enviarATodos(serverPacketID.MSG_MP, this.m_id, this.m_pos.x, this.m_pos.y);
            //mapa.enviarAlArea(getPos().x, getPos().y, -1, serverPacketID.MSG_MP, this.m_id, this.m_pos.x, this.m_pos.y);
           // mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, serverPacketID.MSG_MP, this.m_id, this.m_pos.x, this.m_pos.y);
        }
    }
    
    public void mover(Direction dir) {
        long now = (new java.util.Date()).getTime();
        if ((now - this.m_lastMove) < 250) {
			return;
		}
        this.m_lastMove = now;
        MapPos newPos = this.m_pos.copy();
        newPos.moveToDir(dir);
        Map mapa = this.server.getMapa(newPos.map);
        if (mapa == null) {
			return;
		}
        
        // Es mascota ????
        if (this.petUserOwner != null) {
            // es una posicion legal ???
            if (mapa.isLegalPos(newPos, false)) {
                if (!esAguaValida() && mapa.hayAgua(newPos.x, newPos.y)) {
					return;
				}
                if (esTierraInvalida() && !mapa.hayAgua(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + esAguaValida());
                    log.debug("OJO, ya hay otro NPC!!! " + this + " " + 
                    newPos.x + " " + newPos.y + " encontro=" + mapa.getNPC(newPos.x, newPos.y));
                    return;
                }
                // Update map and user pos
                this.m_infoChar.setDir(dir);
                mapa.moverNpc(this, newPos.x, newPos.y);
                this.m_pos = newPos;
                //enviarMP();
            }
        } else { // No es mascota
            // Controlamos que la posicion sea legal, los npc que
            // no son mascotas tienen mas restricciones de movimiento.
            if (mapa.isLegalPosNPC(newPos, esAguaValida())) {
                if (!esAguaValida() && mapa.hayAgua(newPos.x, newPos.y)) {
					return;
				}
                if (esTierraInvalida() && !mapa.hayAgua(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + esAguaValida());
                    log.debug("OJO, ya hay otro NPC!!! " + this + " " + 
                    newPos.x + " " + newPos.y + " encontro=" + mapa.getNPC(newPos.x, newPos.y));
                    return;
                }
                // Update map and user pos
                this.m_infoChar.setDir(dir);
                mapa.moverNpc(this, newPos.x, newPos.y);
                this.m_pos = newPos;
                //enviarMP();
            } else {
                if (this.m_movement == MOV_NPC_PATHFINDING) {
                    // Someone has blocked the npc's way, we must to seek a new path!
                    //////////// FIXME
                    this.current_step = 0;
                    this.current_path = null;
                    moverAlAzar();
                }
            }
        }
    }
    
    public void npcEnvenenarUser(Client cliente) {
        int n = Util.Azar(1, 100);
        if (n < 30) {
            cliente.envenenar();
        }
    }
    
    public void enviarSonido(int sonido) {
        Map mapa = this.server.getMapa(this.m_pos.map);
        // Sonido
        if (mapa != null) {
			mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) sonido, this.m_pos.x,this.m_pos.y);
		}
    }
    
    public void enviarCFX(int fx, int val) {
        Map m = this.server.getMapa(this.m_pos.map);
        if (m == null) {
			return;
		}
        m.enviarCFX(this.m_pos.x, this.m_pos.y, getId(), (short) fx, (short) val);
    }
    
    public void tirarOro() {
        // NPCTirarOro
        // SI EL NPC TIENE ORO LO TIRAMOS
        if (this.m_giveGLD > 0) {
            Map m = this.server.getMapa(this.m_pos.map);
            m.tirarItemAlPiso(this.m_pos.x, this.m_pos.y, new InventoryObject(OBJ_ORO, this.m_giveGLD));
        }
    }
    
	/** Envia la lista de criaturas del entrenador. */
    public void enviarListaCriaturas(Client cliente) {
    	List<Object> criaturas = new LinkedList<Object>();
        criaturas.add(this.m_criaturas_entrenador_cant);
        for (int i = 0; i < this.m_criaturas_entrenador_cant; i++) {
            criaturas.add(this.m_criaturas_entrenador[i].npc_name);
        }
      //  cliente.enviar(MSG_LSTCRI, criaturas);
    }
    
    /** Seguir a un usuario / Follow user */
    public void seguirUsuario(String nombreUsuario) {
        // doFollow
        if (isFollowing()) {
            this.m_attackedBy = "";
            this.m_flags.set(FLAG_FOLLOW, false);
            this.m_movement  = this.m_oldMovement;
            this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
        } else {
            this.m_attackedBy = nombreUsuario;
            this.m_flags.set(FLAG_FOLLOW, true);
            this.m_movement = MOV_NPCDEFENSA; // follow
            this.m_flags.set(FLAG_HOSTIL, false);
        }
    }
    
    /** Seguir al amo / Follow master */
    public void followAmo() {
        this.m_flags.set(FLAG_FOLLOW, true);
        this.m_movement  = MOV_SIGUE_AMO; // follow npc's master.
        this.m_flags.set(FLAG_HOSTIL, false);
        this.m_target    = 0;
        this.m_targetNpc = null;
    }
    
    public void expresar() {
        // Public Sub Expresar(ByVal NpcIndex As Integer, ByVal UserIndex As Integer)
        if (this.petUserOwner == null) {
			return;
		}
        //WorldPos pos = m_maestroUser.getPos();
        if (this.m_nroExpresiones > 0) {
            int azar = Util.Azar(0, this.m_nroExpresiones - 1);
            Map mapa = this.server.getMapa(this.m_pos.map);
            if (mapa != null) {            
                hablarAlArea(COLOR_BLANCO, this.m_expresiones[azar]);
            }
        }
    }
    
    public void hablarAlArea(int color, String texto) {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa != null) {            
            //mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TALK, color, texto, this.m_id);
        	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.dialog, color, texto, this.getId());
        }
    }
    
    public void defenderse() {
        this.m_movement = MOV_NPCDEFENSA;
        this.m_flags.set(FLAG_HOSTIL, true);
    }
    
    public void cambiarDir(Direction dir) {
        // ChangeNPCChar
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa != null) {
            this.m_infoChar.setDir(dir);

            //mapa.enviarATodos(serverPacketID.MSG_CP, this.m_id, getInfoChar().getCuerpo(), getInfoChar().getCabeza(), getInfoChar().getDir(), getInfoChar().getArma(), getInfoChar().getEscudo(), getInfoChar().getCasco(), getInfoChar().m_fx, getInfoChar().m_loops);
            
            mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_CP, this.getId(), getInfoChar().getCuerpo(), getInfoChar().getCabeza(), getInfoChar().getDir(), getInfoChar().getArma(), getInfoChar().getEscudo(), getInfoChar().getCasco(), getInfoChar().m_fx, getInfoChar().m_loops);
            
        }
    }
    
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'                        Modulo AI_NPC
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'AI de los NPC
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    //'?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿
    
    private void guardiasAI() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (Direction dir : Direction.values()) {
        	if (dir == Direction.NONE)
        		continue;
        	
            MapPos pos = this.m_pos.copy();
            pos.moveToDir(dir);
            if (pos.isValid()) {
                if (mapa.hayCliente(pos.x, pos.y)) {
                    Client cliente = mapa.getCliente(pos.x, pos.y);
                    if (cliente.estaVivo() && !cliente.esGM()) {
                        // ¿ES CRIMINAL?
                    	if (getNPCtype() != NPCTYPE_GUARDIAS_CAOS) {
                    		if (cliente.esCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(cliente);
                    			return;
                    		} else if (this.m_attackedBy.equalsIgnoreCase(cliente.getNick()) && !this.m_flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(cliente);
                    			return;
                    		}
                    	} else {
                    		if (!cliente.esCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(cliente);
                    			return;
                    		} else if (this.m_attackedBy.equalsIgnoreCase(cliente.getNick()) && !this.m_flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(cliente);
                    			return;
                    		}
                    	}
                    	/*
                        if ((cliente.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CRIMINALES) || 
                            (!cliente.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CIUDADANOS)) {
                            cambiarDir(dir);
                            npcAtacaUser(cliente); // ok
                            return;
                        } else if (!isFollowing() && m_attackedBy.equalsIgnoreCase(cliente.getNick())) {
                            cambiarDir(dir);
                            npcAtacaUser(cliente); // ok
                            return;
                        }
                        */
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void hostilMalvadoAI() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (Direction dir : Direction.values()) {
        	if (dir == Direction.NONE)
        		continue;
            MapPos pos = this.m_pos.copy();
            pos.moveToDir(dir);
            if (pos.isValid()) {
                if (mapa.hayCliente(pos.x, pos.y)) {
                    Client cliente = mapa.getCliente(pos.x, pos.y);
                    if (cliente.estaVivo() && !cliente.esGM()) {
                        if (lanzaSpells()) {
                            npcLanzaUnSpell(cliente);
                        }
                        cambiarDir(dir);
                        npcAtacaUser(cliente); // ok
                        return;
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void hostilBuenoAI() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (Direction dir : Direction.values()) {
        	if (dir == Direction.NONE)
        		continue;
            MapPos pos = this.m_pos.copy();
            pos.moveToDir(dir);
            if (pos.isValid()) {
                if (mapa.hayCliente(pos.x, pos.y)) {
                    Client cliente = mapa.getCliente(pos.x, pos.y);
                    if (cliente.estaVivo() && !cliente.esGM() && this.m_attackedBy.equalsIgnoreCase(cliente.getNick())) {
                        if (lanzaSpells()) {
                            npcLanzaUnSpell(cliente);
                        }
                        cambiarDir(dir);
                        npcAtacaUser(cliente); // ok
                        return;
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void irUsuarioCercano() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        
                        if (cliente != null) {
                        
                            if (cliente.estaVivo() && !cliente.estaInvisible() && !cliente.esGM()) {
                                if (lanzaSpells()) {
                                   npcLanzaUnSpell(cliente);
                                }
                                Direction dir = this.m_pos.findDirection(cliente.getPos());
                                mover(dir);
                                return;
                            }
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void seguirAgresor() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        if (!cliente.esGM() && this.m_attackedBy.equalsIgnoreCase(cliente.getNick())) {
                            if (getPetUserOwner() != null) {
		                        if (	!getPetUserOwner().esCriminal() && !cliente.esCriminal() && 
		                        		(getPetUserOwner().tieneSeguro() || getPetUserOwner().getFaccion().ArmadaReal)) {
		                            getPetUserOwner().enviarMensaje("La mascota no atacará a ciudadanos si eres miembro de la Armada Real o tienes el seguro activado", FontType.INFO);
		                            this.m_attackedBy = "";
		                            followAmo();
		                            return;
		                        }
                            }
                            if (cliente.estaVivo() && !cliente.estaInvisible()) {
                                if (lanzaSpells()) {
                                    npcLanzaUnSpell(cliente);
                                }
                                Direction dir = this.m_pos.findDirection(cliente.getPos());
                                mover(dir);
                                return;
                            }
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    public void restoreOldMovement() {
        if (this.petUserOwner == null) {
            oldMovement();
        }
    }
    
    public void oldMovement() {
        this.m_movement  = this.m_oldMovement;
        this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
        this.m_attackedBy = "";
    }
    
    private void persigueCriminal() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        
                        if (cliente == null) break;
                        
                        if (cliente.esCriminal() && cliente.estaVivo() && !cliente.estaInvisible() && !cliente.esGM()) {
                            if (lanzaSpells()) {
                                npcLanzaUnSpell(cliente);
                            }
                            Direction dir = this.m_pos.findDirection(cliente.getPos());
                            mover(dir);
                            return;
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void persigueCiudadano() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        if (!cliente.esCriminal() && cliente.estaVivo() && !cliente.estaInvisible() && !cliente.esGM()) {
                            if (lanzaSpells()) {
                                npcLanzaUnSpell(cliente);
                            }
                            Direction dir = this.m_pos.findDirection(cliente.getPos());
                            mover(dir);
                            return;
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void seguirAmo() {
    	/*
        if (m_target > 0 || m_targetNpc != null) return;
        if (m_maestroUser == null) return;
        short dir = m_pos.findDirection(m_maestroUser.getPos());
        mover(dir);
        */
    	// FIXME: ESTO SE PUEDE OPTIMIZAR, YO SE QUIEN Y DONDE ESTA EL AMO !!!
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        if (cliente.estaVivo() && !cliente.estaInvisible() && getPetUserOwner() == cliente &&
                        		getPetUserOwner().getPos().distance(this.m_pos) > 3) {
                            Direction dir = this.m_pos.findDirection(cliente.getPos());
                            mover(dir);
                            return;
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void aiNpcAtacaNpc() {
    	// FIXME: ESTO SE PUEDE OPTIMIZAR TERRIBLEMENTE, 
    	// CONOCIENDO m_targetNpc no tengo que buscarlo en el mapa ;)
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    Npc npc = mapa.getNPC(x, y);
                    if (npc != null) {
                        if (this.m_targetNpc == npc) {
                            Direction dir = this.m_pos.findDirection(pos);
                            mover(dir);
                            npcAtacaNpc(npc);
                            return;
                        }
                    }
                }
            }
        }
        // No se encontró al NPC objetivo.
        if (this.petUserOwner != null) {
            followAmo();
        } else {
            this.m_movement  = this.m_oldMovement;
            this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
        }
    }
    
    public void moverAlAzar() {
        mover(Direction.value(Util.Azar(1, 4)));
    }
    
    public void doAI() {
        // NPCAI
        // <<<<<<<<<<< Ataques >>>>>>>>>>>>>>>>
        if (this.petUserOwner == null) {
            // Busca a alguien para atacar
            // ¿Es un guardia?
            if (this.m_NPCtype == NPCTYPE_GUARDIAS || this.m_NPCtype == NPCTYPE_GUARDIAS_CAOS) {
                guardiasAI();
            } else if (esHostil() && this.m_estads.Alineacion != 0) {
                hostilMalvadoAI();
            } else if (esHostil() && this.m_estads.Alineacion == 0) {
                hostilBuenoAI();
            }
        } else {
            // Evitamos que ataque a su amo, a menos
            // que el amo lo ataque.
            // 'Call HostilBuenoAI(NpcIndex)
        }
        // <<<<<<<<<<< Movimiento >>>>>>>>>>>>>>>>
        switch (this.m_movement) {
            case MOV_MUEVE_AL_AZAR:
                if (this.m_NPCtype == NPCTYPE_GUARDIAS) {
                    if (Util.Azar(1, 12) == 3) {
                        moverAlAzar();
                    }
                    persigueCriminal();
                } else if (this.m_NPCtype == NPCTYPE_GUARDIAS_CAOS) {
                    if (Util.Azar(1, 12) == 3) {
                        moverAlAzar();
                    }
                    persigueCiudadano();
                } else {
                    if (Util.Azar(1, 12) == 3) {
                        moverAlAzar();
                    }
                }
                break;
            case MOV_NPC_MALO_ATACA_USUARIOS_BUENOS:
                // Va hacia el usuario cercano
                irUsuarioCercano();
                break;
            case MOV_NPCDEFENSA:
                // Va hacia el usuario que lo ataco(FOLLOW)
                seguirAgresor();
                break;
            case MOV_GUARDIAS_ATACAN_CRIMINALES:
                // Persigue criminales
                persigueCriminal();
                break;
            case MOV_GUARDIAS_ATACAN_CIUDADANOS:
                // Persigue criminales
                persigueCiudadano();
                break;
            case MOV_SIGUE_AMO:
                seguirAmo();
                if (Util.Azar(1, 12) == 3) {
                    moverAlAzar();
                }
                break;
            case MOV_NPC_ATACA_NPC:
                aiNpcAtacaNpc();
                break;
            case MOV_NPC_PATHFINDING:
                ////irUsuarioCercano();
            	aiPathFinding();
                break;
                /********************** FIXME
                 * case NPC_PATHFINDING:
                 * if (reCalculatePath()) {
                 * pathFindingAI();
                 * // Existe el camino?
                 * if (m_PFINFO.NoPath) { // Si no existe nos movemos al azar
                 * // Move randomly
                 * moverAlAzar();
                 * }
                 * } else {
                 * if (!pathEnd()) {
                 * followPath();
                 * } else {
                 * m_PFINFO.PathLenght = 0;
                 * }
                 * }
                 * break;
                 */////////////////
        }
    }
    
    private void npcLanzaUnSpell(Client cliente) {
        if (cliente.estaInvisible()) {
			return;
		}
        npcLanzaSpellSobreUser(cliente, this.m_spells[Util.Azar(0, this.m_nroSpells-1)]);
    }
    
    private void npcLanzaSpellSobreUser(Client cliente, short spell) {
        if (!puedeAtacar()) {
			return;
		}
        if (estaInvisible()) {
			return;
		}
        if (cliente.esGM()) {
			return;
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        int daño = 0;
        Spell hechizo = this.server.getHechizo(spell);
        if (hechizo.SubeHP == 1) {
            daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            cliente.enviarSonido(hechizo.WAV);
            cliente.enviarCFX(hechizo.FXgrh, hechizo.loops);
            cliente.getEstads().addMinHP(daño);
            cliente.enviarMensaje(this.m_name + " te ha dado " + daño + " puntos de vida.", FontType.FIGHT);
            cliente.refreshStatus(2);
        } else if (hechizo.SubeHP == 2) {
            daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            cliente.enviarSonido(hechizo.WAV);            
            cliente.enviarCFX(hechizo.FXgrh, hechizo.loops);
            cliente.getEstads().quitarHP(daño);
            cliente.enviarMensaje(this.m_name + " te ha quitado " + daño + " puntos de vida.", FontType.FIGHT);
            cliente.refreshStatus(2);
            // Muere
            if (cliente.getEstads().MinHP < 1) {
                cliente.getEstads().MinHP = 0;
                cliente.userDie();
                if (getPetUserOwner() != null) {
                    getPetUserOwner().contarMuerte(cliente);
                    getPetUserOwner().actStats(cliente);
                }
            }
        }
        if (hechizo.Paraliza == 1) {
            cliente.paralizar(hechizo);
        }
    }
    
    private void npcAtacaUser(Client cliente) {
        Map mapa = this.server.getMapa(this.m_pos.map);
        // El npc puede atacar ???
        if (!puedeAtacar()) {
			return;
		}
        if (cliente.esGM()) {
			return;
		}
        cliente.checkPets(this);
        if (this.m_target == 0) {
			this.m_target = cliente.getId();
		}
        if (cliente.getFlags().AtacadoPorNpc == 0 && cliente.getFlags().AtacadoPorUser == 0) {
			cliente.getFlags().AtacadoPorNpc = this.getId();
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        if (this.m_snd1 > 0) {
			//mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TW, this.m_snd1);
        	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) this.m_snd1, this.m_pos.x,this.m_pos.y);
		}
        if (cliente.npcImpacto(this)) {
           // mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TW, SND_IMPACTO);
        	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) SND_IMPACTO, this.m_pos.x,this.m_pos.y);
            if (!cliente.estaNavegando()) {
				//mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_CFX, cliente.getId(), FXSANGRE, 0);
            	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_FX, cliente.getId(), FXSANGRE, (short) 0);
			}
            cliente.npcDaño(this);
            // ¿Puede envenenar?
            if (puedeEnvenenar()) {
				npcEnvenenarUser(cliente);
			}
        } else {
        	cliente.enviar(ServerPacketID.msgN1, (byte) 0);
        }
        // -----Tal vez suba los skills------
        cliente.subirSkill(Skill.SKILL_Tacticas);
        cliente.refreshStatus(5);
        // Controla el nivel del usuario
        cliente.checkUserLevel();
    }
    
    private boolean npcImpactoNpc(Npc victima) {
        long poderAtt = this.m_poderAtaque;
        long poderEva = victima.m_poderEvasion;
        double probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtt - poderEva) * 0.4)));
        return (Util.Azar(1, 100) <= probExito);
    }
    
    private void npcDañoNpc(Npc victima) {
        int daño = Util.Azar(this.m_estads.MinHIT, this.m_estads.MaxHIT);
        victima.m_estads.quitarHP(daño);
        if (victima.m_estads.MinHP < 1) {
            this.m_movement = this.m_oldMovement;
            if (this.m_attackedBy.length() > 0) {
                this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
            }
            followAmo();
            victima.muereNpc(this.petUserOwner);
        }
    }
    
    private void npcAtacaNpc(Npc victima) {
        Map mapa = this.server.getMapa(this.m_pos.map);
        
        // El npc puede atacar ???
        if (!puedeAtacar()) {
			return;
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        victima.m_targetNpc = this;
        victima.m_movement = MOV_NPC_ATACA_NPC;
        
        if (this.m_snd1 > 0) {
			//mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TW, this.m_snd1);
        	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) this.m_snd1, this.m_pos.x, this.m_pos.y);
		}
        if (npcImpactoNpc(victima)) {
            if (victima.m_snd2 > 0) {
			//	mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, MSG_TW, victima.m_snd2);
            	mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, ServerPacketID.MSG_TW, (byte) victima.m_snd2, victima.m_pos.x, victima.m_pos.y);
			} else {
			//	mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, MSG_TW, SND_IMPACTO2);
				mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, ServerPacketID.MSG_TW, (byte) SND_IMPACTO2, victima.m_pos.x, victima.m_pos.y);
			}
            if (this.petUserOwner != null) {
			//	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TW, SND_IMPACTO);
            	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) SND_IMPACTO, this.m_pos.x, this.m_pos.y);
			} else {
			//	mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, MSG_TW, SND_IMPACTO);
				mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, ServerPacketID.MSG_TW, (byte) SND_IMPACTO, victima.m_pos.x, victima.m_pos.y);
			}
            npcDañoNpc(victima);
        } else {
            if (this.petUserOwner != null) {
				//mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, MSG_TW, SOUND_SWING);
            	mapa.enviarAlArea(this.m_pos.x, this.m_pos.y, ServerPacketID.MSG_TW, (byte) SOUND_SWING, this.m_pos.x, this.m_pos.y);
			} else {
				//mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, MSG_TW, SOUND_SWING);
				mapa.enviarAlArea(victima.m_pos.x, victima.m_pos.y, ServerPacketID.MSG_TW, (byte) SOUND_SWING, victima.m_pos.x, victima.m_pos.y);
			}
        }
    }
    
    private boolean userNear() {
        //#################################################################
        //Returns True if there is an user adjacent to the npc position.
        //#################################################################
        return this.m_pos.distance(this.m_pfinfo.m_targetPos) <= 1;
    }
  
    private boolean isPathEnd() {
	    //#################################################################
	    //Returns if the npc has arrived to the end of its path
	    //#################################################################
	    return this.current_step >= this.current_path.size() - 1;
	}

	private boolean reCalculatePath() {
        //#################################################################
        //Returns true if we have to seek a new path
        //#################################################################
        if (this.current_path == null) {
        	return true;
        } else if (!userNear() && this.current_step == this.current_path.size()) {
        	return true;
        }
        return false;
    }
  
    private void followPath() {
        //#################################################################
        //Moves the npc.
        //#################################################################
        this.current_step++;
        org.ArgentumOnline.server.aStar.Node node = this.current_path.get(this.current_step);
        org.ArgentumOnline.server.aStar.Location loc = node.location;
        MapPos pos = MapPos.mxy(this.m_pos.map, (short)loc.x, (short)loc.y);
        Direction dir = this.m_pos.findDirection(pos);
        
        if (DEBUG)
        	System.out.println("[PF] " + this.current_step + "/" + this.current_path.size() + ": " + this.m_pos + " >> " + pos + " tg=" + this.m_pfinfo.m_targetPos);
        
        mover(dir);
    }
  
    private void aiPathFinding() {
        if (reCalculatePath()) {
            calculatePath();
            // Existe el camino?
            if (this.current_path == null) { // Si no existe nos movemos al azar
            	// Move randomly
            	moverAlAzar();
            }
         } else {
            if (!isPathEnd()) {
            	followPath();
            } else {
            	this.current_path = null;
            	this.current_step = 0;
            }
          }
    }
    
    private void calculatePath() {
        //private void pathFindingAI() {
        Map mapa = this.server.getMapa(this.m_pos.map);
        if (mapa == null) {
			return;
		}
        for (short x = (short) (this.m_pos.x-10); x <= (short) (this.m_pos.x+10); x++) {
            for (short y = (short) (this.m_pos.y-10); y <= (short) (this.m_pos.y+10); y++) {
                MapPos pos = MapPos.mxy(this.m_pos.map, x, y);
                if (pos.isValid()) {
                    if (mapa.hayCliente(x, y)) {
                        Client cliente = mapa.getCliente(x, y);
                        if (cliente.estaVivo() && !cliente.estaInvisible()) {
                        	this.m_pfinfo = new PFINFO(MapPos.mxy(this.m_pos.map, x, y), cliente);
                        	PathFinding pf = new PathFinding();
                        	this.current_path = pf.seekPath(this);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void initCriaturasEntrenador() {
        this.m_criaturas_entrenador_cant = 0;
        for (int i = 0; i < MAX_CRIATURAS_ENTRENADOR; i++) {
			this.m_criaturas_entrenador[i] = new TrainerMascot();
		}
    }
    
    public void initExpresiones() {
        this.m_nroExpresiones = 0;
        this.m_expresiones = new String[MAX_EXPRESIONES];
    }
    
    public void initSpells() {
        for (int j = 0; j < this.m_nroSpells; j++) {
            this.m_spells[j] = 0;
        }
    }

    public void backup(IniFile ini) {
        // Sub BackUPnPc(NpcIndex As Integer)
        // General
        String section = "NPC" + this.m_numero;
        ini.setValue(section, "NpcType", this.m_NPCtype);
        if (this.m_NPCtype == NPCTYPE_GUARDIAS) {
            ini.setValue(section, "GuardiaPersigue", this.m_guardiaPersigue);
        }
        ini.setValue(section, "Name", this.m_name);
        ini.setValue(section, "Desc", this.m_desc);
        ini.setValue(section, "Head", this.m_infoChar.m_cabeza);
        ini.setValue(section, "Body", this.m_infoChar.m_cuerpo);
        ini.setValue(section, "Heading", this.m_infoChar.m_dir);
        ini.setValue(section, "Movement", this.m_movement);
        ini.setValue(section, "TipoItems", this.m_tipoItems);
        ini.setValue(section, "GiveEXP", this.m_giveEXP);
        ini.setValue(section, "GiveGLD", this.m_giveGLD);
        ini.setValue(section, "Inflacion", this.m_inflacion);
        ini.setValue(section, "Attackable", this.m_flags.get(FLAG_ATACABLE));
        ini.setValue(section, "Comercia", this.m_flags.get(FLAG_COMERCIA));
        ini.setValue(section, "Hostil", this.m_flags.get(FLAG_HOSTIL));
        ini.setValue(section, "InvReSpawn", this.m_flags.get(FLAG_INV_RESPAWN));
        // Stats
        ini.setValue(section, "Alineacion", this.m_estads.Alineacion);
        ini.setValue(section, "DEF", this.m_estads.Def);
        ini.setValue(section, "MaxHit", this.m_estads.MaxHIT);
        ini.setValue(section, "MaxHp", this.m_estads.MaxHP);
        ini.setValue(section, "MinHit", this.m_estads.MinHIT);
        ini.setValue(section, "MinHp", this.m_estads.MinHP);
        ini.setValue(section, "UsuariosMatados", this.m_estads.usuariosMatados);
        // Flags
        ini.setValue(section, "ReSpawn", !this.m_flags.get(FLAG_RESPAWN));
        ini.setValue(section, "BackUp", this.m_flags.get(FLAG_BACKUP));
        ini.setValue(section, "Domable", this.m_domable);
        // Inventario
        ini.setValue(section, "NroItems", this.m_inv.getSize());
        for (int i = 1; i <= this.m_inv.getSize(); i++) {
            ini.setValue(section, "Obj" + i, this.m_inv.getObjeto(i).objid + "-" + this.m_inv.getObjeto(i).cant);
        }
    }    
    
    /** Cargar un NPC desde un ini. */
    private void leerNpc(IniFile ini, int npc_ind) {
        String section = "NPC" + npc_ind;
        
        this.m_name = ini.getString(section, "Name");
        this.m_desc = ini.getString(section, "Desc");
        
        this.m_movement = ini.getShort(section, "Movement");
        this.m_oldMovement = this.m_movement;
        this.m_flags.set(FLAG_AGUA_VALIDA, ini.getInt(section, "AguaValida") == 1);
        this.m_flags.set(FLAG_TIERRA_INVALIDA, ini.getInt(section, "TierraInvalida") == 1);
        this.m_flags.set(FLAG_FACCION, ini.getInt(section, "Faccion") == 1);
        
        this.m_NPCtype = ini.getShort(section, "NpcType");
        
        if (this.m_NPCtype == NPCTYPE_GUARDIAS) {
            this.m_guardiaPersigue = ini.getShort(section, "GuardiaPersigue");
        }
        this.m_deQuest = (ini.getShort(section, "DeQuest") == 1);
    
        this.m_infoChar.m_cuerpo   = ini.getShort(section, "Body");
        this.m_infoChar.m_cabeza   = ini.getShort(section, "Head");
        this.m_infoChar.m_dir      = ini.getShort(section, "Heading");
        
        this.m_flags.set(FLAG_ENVENENA, ini.getInt(section, "Veneno") == 1);
        this.m_flags.set(FLAG_ATACABLE, ini.getInt(section, "Attackable") == 1);
        this.m_flags.set(FLAG_COMERCIA, ini.getInt(section, "Comercia") == 1);
        this.m_flags.set(FLAG_HOSTIL,   ini.getInt(section, "Hostile") == 1);
        this.m_flags.set(FLAG_INV_RESPAWN, ini.getInt(section, "InvReSpawn") == 1);
        this.m_flags.set(FLAG_OLD_HOSTILE, this.m_flags.get(FLAG_HOSTIL));
        
        this.m_giveEXP   = ini.getInt(section, "GiveEXP");
        //m_expDada   = m_giveEXP;
        this.m_expCount  = this.m_giveEXP / 2;
        
        this.m_domable = ini.getShort(section, "Domable");
        this.m_flags.set(FLAG_RESPAWN, ini.getInt(section, "ReSpawn") != 1);
        
        this.m_giveGLD         = ini.getInt(section, "GiveGLD");
        this.m_poderAtaque	= ini.getInt(section, "PoderAtaque");
        this.m_poderEvasion	= ini.getInt(section, "PoderEvasion");
        
        this.m_estads.MaxHP	= ini.getInt(section, "MaxHP");
        this.m_estads.MinHP	= ini.getInt(section, "MinHP");
        this.m_estads.MaxHIT	= ini.getInt(section, "MaxHIT");
        this.m_estads.MinHIT	= ini.getInt(section, "MinHIT");
        
        this.m_estads.Alineacion    = ini.getShort(section, "Alineacion");
        this.m_estads.Def           = ini.getShort(section, "DEF");
        this.m_estads.ImpactRate    = ini.getShort(section, "ImpactRate");
        
        this.m_inflacion   = ini.getInt(section, "Inflacion");
        
        this.m_flags.set(FLAG_BACKUP, ini.getInt(section, "Backup") == 1);
        this.m_flags.set(FLAG_RESPAWN_ORIG_POS, ini.getInt(section, "OrigPos") == 1);
        this.m_flags.set(FLAG_AFECTA_PARALISIS, ini.getInt(section, "AfectaParalisis") == 1);
        this.m_flags.set(FLAG_GOLPE_EXACTO, ini.getInt(section, "GolpeExacto") == 1);
        
        this.m_snd1  = ini.getShort(section, "Snd1");
        this.m_snd2  = ini.getShort(section, "Snd2");
        this.m_snd3  = ini.getShort(section, "Snd3");
        this.m_snd4  = ini.getShort(section, "Snd4");
        
        //Inventario
        loadInventario(ini);
        
        //Spells
        this.m_nroSpells = (byte) ini.getInt(section, "LanzaSpells");
        this.m_flags.set(FLAG_LANZA_SPELLS, this.m_nroSpells > 0);
        if (this.m_nroSpells > 0) {
            for (int k = 0; k < (this.m_nroSpells); k++) {
                String spellName = "Sp" + (k+1);
                this.m_spells[k] = ini.getShort(section, spellName);
            }
        }
        
        //Entrenador
        this.m_criaturas_entrenador_cant = (byte) ini.getShort(section, "NroCriaturas");
        if (this.m_criaturas_entrenador_cant > 0) {
            for (int c = 0; c < this.m_criaturas_entrenador_cant; c++) {
                this.m_criaturas_entrenador[c].npc_index = ini.getShort(section, "CI" + (c+1));
                this.m_criaturas_entrenador[c].npc_name = ini.getString(section, "CN" + (c+1));
            }
        }
        
        // <<<<<<<<<<<<<< Expresiones >>>>>>>>>>>>>>>>
        short m_nroExpresiones = ini.getShort(section, "NROEXP");
        if (m_nroExpresiones > 0) {
            String m_expresiones[] = new String[m_nroExpresiones];
            for (int i = 0; i < m_nroExpresiones; i++) {
                m_expresiones[i] = ini.getString(section, "Exp" + (i+1));
            }
        }
        // Tipo de items con los que comercia
        this.m_tipoItems = ini.getShort(section, "TipoItems");
    }
    
    public short getHeading() {
    	return m_infoChar.m_dir;
    }
    
    private void loadInventario(IniFile ini) {
        String section = "NPC" + this.m_numero;
        int cant = ini.getInt(section, "NROITEMS");
        if (cant > 0) {
            if (cant > MAX_OBJS_X_SLOT) {
				cant = MAX_OBJS_X_SLOT;
			}
            for (int j = 0; j < cant; j++) {
                String objName = "Obj" + (j+1);
                String objTmp  = ini.getString(section, objName);
                int sep = objTmp.indexOf('-');
                if (sep != -1) {
					try {
                        //Separar las cadenas "ObjInd"-"Cant".
                        String objid = objTmp.substring(0, sep);
                        String objcnt = objTmp.substring(sep+1);
                        //System.out.println("DEBUG: m_numero=" + m_numero + " nombre=" + m_name);
                        //System.out.println("DEBUG: objTmp=" + objTmp);
                        //System.out.println("DEBUG: objid=" + objid);
                        //System.out.println("DEBUG: objcnt=" + objcnt);
                        this.m_inv.setObjeto(j+1, new InventoryObject(Short.parseShort(objid), Integer.parseInt(objcnt)));
                        //m_inv.getObjeto(j+1).objid = Short.parseShort(objid);
                        //m_inv.getObjeto(j+1).cant  = Integer.parseInt(objcnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
            }
        }
    }        
    
    public void enviarNpcInv(Client cliente) {
        // Sub EnviarNpcInv(ByVal UserIndex As Integer, ByVal NpcIndex As Integer)
        // Enviamos el inventario del npc con el cual el user va a comerciar...
        double dto = cliente.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        for (int i = 1; i <= this.m_inv.getSize(); i++) {
        	
            if (this.m_inv.getObjeto(i).objid > 0) {
            	
                // Calculamos el porc de inflacion del npc
                ObjectInfo info = this.server.getInfoObjeto(this.m_inv.getObjeto(i).objid);
                double infla = (this.m_inflacion *  info.Valor) / 100;
                double val = (info.Valor + infla) / dto;
                cliente.enviar(ServerPacketID.MSG_NPC_INV, (byte) i, info.Nombre, (short) this.m_inv.getObjeto(i).cant, ((int) val),
                		info.GrhIndex, this.m_inv.getObjeto(i).objid, (byte) info.ObjType,
               		info.MaxHIT, info.MinHIT, info.MaxDef);

            } else {
                cliente.enviar(ServerPacketID.MSG_NPC_INV, (byte) i, "Nada", (short) 0);
            }
        }
    }
    
    public void venderItem(Client cliente, short slot, int cant) {
        // Sub NPCVentaItem(ByVal UserIndex As Integer, ByVal i As Integer, ByVal Cantidad As Integer, ByVal NpcIndex As Integer)
        if (cant < 1) {
			return;
		}
        // NPC VENDE UN OBJ A UN USUARIO
        cliente.refreshStatus(1);
        // Calculamos el valor unitario
        if (this.m_inv.getObjeto(slot).objid == 0) {
			return;
		}
        // Calculamos el porc de inflacion del npc
        ObjectInfo info = this.server.getInfoObjeto(this.m_inv.getObjeto(slot).objid);
        double dto = cliente.descuento();
        if (dto == 0.0) {
			dto = 1.0; // evitamos dividir por 0!
		}
        double infla = (this.m_inflacion * info.Valor) / 100;
        double val = (info.Valor + infla) / dto;
        
        if (cliente.getEstads().getGold() < (val * cant)) {
            cliente.enviarMensaje("No tienes suficiente oro.", FontType.INFO);
            return;
        }
        
        if (this.m_inv.getObjeto(slot).cant > 0) {
            if (cant > this.m_inv.getObjeto(slot).cant) {
                cant = this.m_inv.getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            cliente.userCompraObj(this, slot, cant);
            // Actualizamos el inventario del usuario
            cliente.enviarInventario();
            // Actualizamos el oro
            cliente.refreshStatus(1);
            // Actualizamos la ventana de comercio
            enviarNpcInv(cliente);
            cliente.updateVentanaComercio(slot, (short) 0);
        }
    }

    public void comprarItem(Client cliente, short slot, int cant) {
        // Sub NPCCompraItem(ByVal UserIndex As Integer, ByVal Item As Integer, ByVal Cantidad As Integer)
        // NPC COMPRA UN OBJ A UN USUARIO
        cliente.refreshStatus(1);
        if (cliente.getInv().getObjeto(slot).cant > 0 && !cliente.getInv().getObjeto(slot).equipado) {
            if (cant > 0 && cant > cliente.getInv().getObjeto(slot).cant) {
                cant = cliente.getInv().getObjeto(slot).cant;
            }
            // Agregamos el obj que compro al inventario
            npcCompraObj(cliente, slot, cant);
            // Actualizamos el inventario del usuario
            cliente.enviarInventario();
            // Actualizamos el oro
            cliente.refreshStatus(1);
            enviarNpcInv(cliente);
            // Actualizamos la ventana de comercio
            cliente.updateVentanaComercio(slot, (short) 1);
        }
    }
    
    public void npcCompraObj(Client cliente, short slot, int cant) {
        // Sub NpcCompraObj(ByVal UserIndex As Integer, ByVal objIndex As Integer, ByVal Cantidad As Integer)
        if (cant < 1) {
			return;
		}
        short objid = cliente.getInv().getObjeto(slot).objid;
        ObjectInfo info = this.server.getInfoObjeto(objid);
        if (info.esNewbie()) {
            cliente.enviarMensaje("No comercio objetos para newbies.", FontType.INFO);
            return;
        }
        if (this.m_tipoItems != OBJTYPE_CUALQUIERA) {
            // ¿Son los items con los que comercia el npc?
            if (this.m_tipoItems != info.ObjType) {
                cliente.enviarMensaje("No me interesa comprar ese objeto.", FontType.WARNING);
                return;
            }
        }
        int slot_inv = 0;
        // ¿Ya tiene un objeto de este tipo?
        for (int i = 1; i <= this.m_inv.getSize(); i++) {
            if (this.m_inv.getObjeto(i).objid == objid && this.m_inv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
                slot_inv = i;
                break;
            }
        }
        // Sino se fija por un slot vacio antes del slot devuelto
        if (slot_inv == 0) {
            slot_inv = this.m_inv.getSlotLibre();
            //If Slot <= MAX_INVENTORY_SLOTS Then Npclist(NpcIndex).Invent.NroItems = Npclist(NpcIndex).Invent.NroItems + 1
        }
        if (slot_inv > 0) { // Slot valido
            // Mete el obj en el slot
            if (this.m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
                // Menor que MAX_INV_OBJS
                this.m_inv.getObjeto(slot_inv).objid = objid;
                this.m_inv.getObjeto(slot_inv).cant += cant;
                cliente.getInv().quitarUserInvItem(slot, cant);
                // Le sumamos al user el valor en oro del obj vendido
                //double monto = ((info.Valor / 3 + infla) * cant);
                double monto = ((info.Valor / 3) * cant);
                cliente.getEstads().addGold((int) monto);
                // tal vez suba el skill comerciar ;-)
                cliente.subirSkill(Skill.SKILL_Comerciar);
            } else {
                cliente.enviarMensaje("No puedo cargar tantos objetos.", FontType.INFO);
            }
        } else {
            cliente.getInv().quitarUserInvItem(slot, cant);
            // Le sumamos al user el valor en oro del obj vendido
            //double monto = ((info.Valor / 3 + infla) * cant);
            double monto = ((info.Valor / 3) * cant);
            cliente.getEstads().addGold((int) monto);
        }
    }

    public void quitarNpcInvItem(short slot, int cant) {
        // Sub QuitarNpcInvItem(ByVal NpcIndex As Integer, ByVal Slot As Byte, ByVal Cantidad As Integer)
        short objid = this.m_inv.getObjeto(slot).objid;
        // Quita un Obj
        ObjectInfo info = this.server.getInfoObjeto(objid);
        if (!info.esCrucial()) {
            this.m_inv.getObjeto(slot).cant -= cant;
            if (this.m_inv.getObjeto(slot).cant <= 0) {
                this.m_inv.getObjeto(slot).objid = 0;
                this.m_inv.getObjeto(slot).cant = 0;
                if (this.m_inv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        } else {
            this.m_inv.getObjeto(slot).cant -= cant;
            if (this.m_inv.getObjeto(slot).cant <= 0) {
                this.m_inv.getObjeto(slot).objid = 0;
                this.m_inv.getObjeto(slot).cant = 0;
                if (!quedanItems(objid)) {
                    this.m_inv.getObjeto(slot).objid = objid;
                    this.m_inv.getObjeto(slot).cant = encontrarCant(objid);
                }
                if (this.m_inv.isEmpty() && invReSpawn()) {
                    // Reponemos el inventario
                    cargarInvent(); 
                }
            }
        }
    }

    private void cargarInvent() {
        // Sub CargarInvent(ByVal NpcIndex As Integer)
        // Vuelve a cargar el inventario del npc NpcIndex
        IniFile ini = getIniFile(this.m_numero, false);
        loadInventario(ini);
    }

    private boolean quedanItems(short objid) {
        // Function QuedanItems(ByVal NpcIndex As Integer, ByVal objIndex As Integer) As Boolean
        for (int i = 1; i <= MAX_INVENTORY_SLOTS; i++) {
            if (this.m_inv.getObjeto(i).objid == objid) {
                return true;
            }
        }
        return false;
    }
     
    private int encontrarCant(short objid) {
    	if (DEBUG)
    		System.out.println("+++++DEBUG++++ encontrarCant() " + this.m_inv.isEmpty());
    	
        IniFile ini = getIniFile(this.m_numero, false);
        String section = "NPC" + this.m_numero;
        int cant = ini.getInt(section, "NROITEMS");
        if (cant > 0) {
            if (cant > MAX_OBJS_X_SLOT) {
                cant = MAX_OBJS_X_SLOT;
            }
            for (int j = 0; j < cant; j++) {
                String objName = "Obj" + (j+1);
                String objTmp  = ini.getString(section, objName);
                int sep = objTmp.indexOf('-');
                if (sep != -1) {
                    try {
                        // Separar las cadenas "ObjInd"-"Cant".
                        short objid_inv = Short.parseShort(objTmp.substring(0, sep));
                        int cant_inv = Integer.parseInt(objTmp.substring(sep+1));
                        if (objid_inv == objid) {
                            return cant_inv;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 0;
    }
    
    private String estadoVidaExacta() {
    	if (this.getEstads().MaxHP <= 0) {
			return "";
		}
    	return "(" + this.getEstads().MinHP + "/" + this.getEstads().MaxHP + ")";    	
    }
    
    public String estadoVida(Client cliente) {
    	if (this.getEstads().MaxHP <= 0) {
			return "";
		}
    	//agush: con esto los gms ahora ven la vida de los npcs
    	if (!cliente.esGM() == false) {
			return estadoVidaExacta();
		}
        short skSup = cliente.getEstads().getUserSkill(Skill.SKILL_Supervivencia);
        double vidaPct = this.getEstads().MinHP / this.getEstads().MaxHP;
        if (skSup < 11) {
        	return "(Dudoso)";
        } else if (skSup < 21) {
        	if (vidaPct < 0.5) {
				return "(Herido)";
			}
			return "(Sano)";
        } else if (skSup < 31) {
        	if (vidaPct < 0.5) {
				return "(Malherido)";
			} else if (vidaPct < 0.75) {
				return "(Herido)";
			}
			return "(Sano)";
        } else if (skSup < 41) {
        	if (vidaPct < 0.25) {
				return "(Muy malherido)";
			} else if (vidaPct < 0.5) {
				return "(Herido)";
			} else if (vidaPct < 0.75) {
				return "(Levemente herido)";
			}
			return "(Sano)";
        } else if (skSup < 60) {
        	if (vidaPct < 0.05) {
				return "(Agonizando)";
			}
        	if (vidaPct < 0.1) {
				return "(Casi muerto)";
			}            	
        	if (vidaPct < 0.25) {
				return "(Muy malherido)";
			} else if (vidaPct < 0.5) {
				return "(Herido)";
			} else if (vidaPct < 0.75) {
				return "(Levemente herido)";
			} else if (vidaPct < 1.0) {
				return "(Sano)";
			}
			return "(Intacto)";
        } else {
        	return estadoVidaExacta();
        }    	
    }
}
