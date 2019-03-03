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
package org.ArgentumOnline.server.npc;

import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_AFECTA_PARALISIS;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_AGUA_VALIDA;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_ATACABLE;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_BACKUP;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_BENDICION;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_COMERCIA;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_ENVENENA;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_FACCION;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_FOLLOW;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_GOLPE_EXACTO;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_HOSTIL;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_INVISIBLE;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_INV_RESPAWN;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_LANZA_SPELLS;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_MALDICION;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_NPC_ACTIVE;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_OLD_HOSTILE;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_PARALIZADO;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_PUEDE_ATACAR;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_RESPAWN;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_RESPAWN_ORIG_POS;
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_TIERRA_INVALIDA;
import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import java.util.List;

import org.ArgentumOnline.server.AbstractCharacter;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.Spell;
import org.ArgentumOnline.server.aStar.Node;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.protocol.CharacterCreateResponse;
import org.ArgentumOnline.server.protocol.CharacterRemoveResponse;
import org.ArgentumOnline.server.protocol.ChatOverHeadResponse;
import org.ArgentumOnline.server.protocol.CreateFXResponse;
import org.ArgentumOnline.server.protocol.NPCSwingResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.util.Color;
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
    
    public final static short GUARDIAS_PERSIGUEN_CIUDADANOS = 1;
    public final static short GUARDIAS_PERSIGUEN_CRIMINALES = 0;

    long m_lastMove = 0;
    
    public String m_name = "";
    public String m_desc = "";
    
    NpcType m_NPCtype = NpcType.NPCTYPE_COMUN;
    int   m_numero  = 0;
    short m_level   = 0;

    private NpcFlags m_flags = new NpcFlags();
    
    int   m_targetUser    = 0;
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
    public byte   m_snd1 = 0; // Sonido ataque NPC
    public byte   m_snd2 = 0; // Sonido ataque exitoso NPC
    public byte   m_snd3 = 0; // Sonido muere NPC
    public byte   m_snd4 = 0; // ???
    
    public NpcStats m_estads = new NpcStats();
    
    NpcCounters m_contadores = new NpcCounters();
    
    byte  m_nroExpresiones = 0;
    String m_expresiones[] = new String[MAX_EXPRESIONES];
    
    byte  m_nroSpells = 0;
    short m_spells[] = new short[MAX_NUM_SPELLS];  // le da vida ;)
    
    Player petUserOwner = null;
    short petNpcOwnerId  = 0;

    /**
     * El inventario de los npcs tiene dos funciones:
     * - en los comerciantes, son los items en venta
     * - en los hostiles, son los items dropeados
     */
    protected Inventory m_inv;

    public Inventory getInv() {
        return this.m_inv;
    }
    
    public int getInflacion() {
    	return m_inflacion;
    }
    
    public class PFINFO {
    	public MapPos m_targetPos;
    	public Player m_targetUser;
    	
    	public PFINFO(MapPos pos, Player user) {
    		this.m_targetPos = pos;
    		this.m_targetUser = user;
    	}
    }
    
    public PFINFO m_pfinfo; // FIXME
    List<Node> current_path = null;
    int current_step = 0;
    
    MapPos m_orig  = MapPos.empty();
    
    protected GameServer server;
    
    /** Creates a new instance of NPC */
    protected Npc(int npc_numero, GameServer server) {
    	this.server = server;
    	
        this.m_numero = npc_numero;
        this.m_inv = new Inventory(server, 20);
        this.setId(server.nextId());
        loadInfoNPC(this.m_numero, server.isLoadBackup());
    }
    
    /**
     * Crea una mascota del tipo nroNPC, cerca de la posici�n orig.
     * @param nroNPC
     * @param orig
     * @param bajoTecho
     * @param server
     * @return a new pet npc
     */
    public static Npc spawnPetNpc(int nroNPC, MapPos orig, boolean bajoTecho, GameServer server) {
        // Crea un NPC del tipo NRONPC
        Npc npc = server.createNpc(nroNPC);
        Map mapa = server.getMap(orig.map);
        
        MapPos tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.esAguaValida(), npc.esTierraInvalida(), bajoTecho);
        
        if (tmp == null) {
        	server.deleteNpc(npc);
        	return null;
        }

        npc.pos().set(tmp.map, tmp.x, tmp.y);
        mapa.enterNpc(npc, tmp.x, tmp.y);
        return npc;
    }
    
    public static Npc spawnNpc(int npcNumber, MapPos orig, boolean conFX, boolean conRespawn) {
        // Crea un NPC del tipo indiceNPC
    	GameServer server = GameServer.instance();
        Npc npc = server.createNpc(npcNumber);
        if (!conRespawn) {
			npc.m_flags.set(FLAG_RESPAWN, false);
		}
        Map mapa = server.getMap(orig.map);
        boolean hayPosValida = false;
        MapPos tmp;
        int i = 0;
        while (!hayPosValida && i < MAXSPAWNATTEMPS) {
            if (!orig.isValid()) {
                orig.x = (byte) Util.Azar(1, MAPA_ANCHO);
                orig.y = (byte) Util.Azar(1, MAPA_ALTO);
            }
            tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.esAguaValida(), npc.esTierraInvalida(), false);
            if (tmp != null) {
				orig = tmp;
			}
            if (mapa.testSpawnTriggerNpc(orig, false)) {
                // Necesita ser respawned en un lugar especifico
                npc.setPos(orig.copy());
                
                mapa.enterNpc(npc, orig.x, orig.y);
                hayPosValida = true;
            } else {
                orig.x = 0;
                orig.y = 0;
            }
            i++;
        }
        if (!hayPosValida) {
        	server.deleteNpc(npc);
        	System.out.println("OJO: NO HAY POSICION VALIDA !!!");
        	return null;
        } else if (conFX) {
            npc.sendPlayWave(SND_WARP);
            npc.sendCreateFX(FXWARP, 0);
        }
        npc.activate();
        
        return npc;
    }
    
    public void reSpawnNpc() {
        //////////// FIXME - HAY QUE USAR LA POSICION ORIGINAL (m_orig) REVISAR !!!
        if (puedeReSpawn()) {
        	if (respawnOrigPos()) {
        		spawnNpc(this.m_numero, this.m_orig, false, true); // TODO: PROBAR !!! 
        	} else {
        		spawnNpc(this.m_numero, MapPos.mxy(pos().map, (short)0, (short)0), false, true); // TODO: PROBAR !!!
        	}
        } else {
        	log.debug("{{{{{{{ DEBUG }}}}}}} NO PUEDE RESPAWN !!!");
        }
    }
    
    
    public boolean isNpcGuard() {
    	return this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS;
    }
    
    public boolean isGambler() {
    	return this.m_NPCtype == NpcType.NPCTYPE_TIMBERO;
    }
    
    // added by gorlok
    boolean spellSpawnedPet = false;
    public boolean isSpellSpawnedPet() {
		return spellSpawnedPet;
	}
    public void setSpellSpawnedPet(boolean spellSpawnedPet) {
		this.spellSpawnedPet = spellSpawnedPet;
	}
    
    
    public boolean isStatic() {
    	return this.m_movement == Npc.MOV_ESTATICO;
    }
    
    public void makeStatic() {
    	this.m_movement = Npc.MOV_ESTATICO;
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
    
    protected boolean invReSpawn() {
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
    
    public MapPos getOrig() {
        return this.m_orig;
    }
    
    public boolean isNpcActive() {
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
    
    public Player getPetUserOwner() {
		return petUserOwner;
	}
    
    public void setPetUserOwner(Player petUserOwner) {
		this.petUserOwner = petUserOwner;
	}
    
    public void releasePet() {
    	this.petUserOwner = null;
    	this.petNpcOwnerId = 0;
    }
    
    public boolean estaParalizado() {
        return this.m_flags.get(FLAG_PARALIZADO);
    }
    
    public boolean comercia() {
        return this.m_flags.get(FLAG_COMERCIA);
    }
    
    public boolean isBankCashier() {
    	return npcType() == NpcType.NPCTYPE_BANQUERO;
    }
    
    public boolean esSacerdote() {
    	return npcType() == NpcType.NPCTYPE_SACERDOTE;
    }
    
    public boolean esSacerdoteNewbies() {
    	return npcType() == NpcType.NPCTYPE_SACERDOTE_NEWBIES;
    }
    
    public boolean esNoble() {
    	return npcType() == NpcType.NPCTYPE_NOBLE;
    }
    
    public void activate() {
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
    
    public int getTargetUser() {
        return this.m_targetUser;
    }
    
    public Npc getTargetNpc() {
    	return m_targetNpc;
    }
    
    public void setTargetUser(int target) {
        this.m_targetUser = target;
    }
    
    public NpcType npcType() {
        return this.m_NPCtype;
    }
    
    public NpcStats stats() {
        return this.m_estads;
    }
    
    public NpcCounters getContadores() {
        return this.m_contadores;
    }
    
    public void setPetNpcOwner(Npc petOwner) {
        this.petNpcOwnerId = petOwner.getId();
    }
    
    public short domable() {
        return this.m_domable;
    }
    
    public CharacterCreateResponse createCC() {
    	return new CharacterCreateResponse(
			(short)getId(), 
			(short)m_infoChar.getCuerpo(), 
			(short)m_infoChar.getCabeza(),
			
			(byte)m_infoChar.getDir(), 
			(byte)pos().x,
			(byte)pos().y,
			
			(short)m_infoChar.getArma(), 
			(short)m_infoChar.getEscudo(), 
			(short)m_infoChar.getCasco(),
			
			(short)m_infoChar.getFX(),
			(short)m_infoChar.getLoops(),
			
			(String)"", 
			(byte)0, 
			(byte)0);
    }

    private void loadInfoNPC(int npc_ind, boolean loadBackup) {
        IniFile ini = this.server.getNpcLoader().getIniFile(npc_ind, loadBackup);
        leerNpc(ini, npc_ind);
    }
    
    
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'                        Modulo NPC
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'Contiene todas las rutinas necesarias para cotrolar los
    //'NPCs menos la rutina de AI que se encuentra en el modulo
    //'AI_NPCs para su mejor comprension.
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    
    public void calcularDarExp(Player player, int da�o) {
        // [Alejo]
        // Modifique un poco el sistema de exp por golpe, ahora
        // son 2/3 de la exp mientras esta vivo, el resto se
        // obtiene al matarlo.
        int expSinMorir = (2 * this.m_giveEXP ) / 3;
        int totalNpcVida = stats().MaxHP;
        if (totalNpcVida <= 0) {
			return;
		}
        if (da�o < 0) {
			da�o = 0;
		}
        if (da�o > stats().MinHP) {
			da�o = stats().MinHP;
		}
        int expADar = (da�o * expSinMorir) / totalNpcVida;
        
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
            player.stats().addExp(expADar);
            player.enviarMensaje("Has ganado " + expADar + " puntos de experiencia.", FONTTYPE_FIGHT);
            player.sendUpdateUserStats();
            player.checkUserLevel();
        }
    }
        
    public void muereNpc(Player player) {
        ////////////// FIXME
        // Lo mato un usuario?
        if (player != null) {
        	boolean eraCrimi = player.isCriminal();
        	
            MapPos pos = player.pos();
            Map m = this.server.getMap(pos.map);
            
            if (this.m_snd3 > 0) {
            	m.enviarAlArea(pos.x, pos.y, new PlayWaveResponse((byte) this.m_snd3, player.pos().x, player.pos().y));
            }
            player.flags().TargetNpc = 0;
            player.flags().TargetNpcTipo = 0;
            // El user que lo mato tiene mascotas?
            if (player.getUserPets().hasPets()) {
                player.getUserPets().petsFollowMaster(this);
            }
            player.enviarMensaje("Has matado la criatura!", FONTTYPE_FIGHT);
            if (this.m_expCount > 0) {
                player.stats().addExp(this.m_expCount);
                player.enviarMensaje("Has ganado " + this.m_expCount + " puntos de experiencia.", FONTTYPE_FIGHT);
            } else {
                player.enviarMensaje("No has ganado experiencia al matar la criatura.", FONTTYPE_FIGHT);
            }
            player.stats().incNPCsMuertos();
            player.getQuest().checkNpcEnemigo(player, this);

            if (this.m_estads.Alineacion == 0) {
                if (this.m_numero == GUARDIAS) {
                    player.volverCriminal();
                }
                if (!player.isGod()) {
                    player.reputation().incAsesino(vlAsesino);
                }
            } else if (this.m_estads.Alineacion == 1) {
                player.reputation().incPlebe(vlCazador);
            } else if (this.m_estads.Alineacion == 2) {
                player.reputation().incNoble(vlAsesino / 2);
            } else if (this.m_estads.Alineacion == 4) {
                player.reputation().incPlebe(vlCazador);
            }
            // Controla el nivel del usuario
            player.checkUserLevel();

            //Agush: updateamos de ser necesario ;-)
            if (player.isCriminal() != eraCrimi && eraCrimi == true) {
            	player.refreshUpdateTagAndStatus();
            }
        }
        
        if (this.petUserOwner == null) {
            // Tiramos el oro
            tirarOro();
            // Tiramos el inventario
            tirarItems();
        }
        //short mapa = pos().mapa;
        // Quitamos el npc
        quitarNPC();
        // ReSpawn o no
        //reSpawnNpc(mapa);
        reSpawnNpc();
    }
    
    private void tirarItems() {
        // NPC_TIRAR_ITEMS
        // TIRA TODOS LOS ITEMS DEL NPC
        if (this.m_inv.size() > 0) {
            for (int i = 1; i <= this.m_inv.size(); i++) {
                if (this.m_inv.getObjeto(i) != null && this.m_inv.getObjeto(i).objid > 0) {
                    Map m = this.server.getMap(pos().map);
                    m.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(this.m_inv.getObjeto(i).objid, this.m_inv.getObjeto(i).cant));
                }
            }
        }
    }
    
    public void quitarNPC() {
    	if (DEBUG) {
    		log.debug("NPC BORRADO");
    	}
    	    	
        this.m_flags.set(FLAG_NPC_ACTIVE, false);
        Map map = this.server.getMap(pos().map);
        if (map == null) {
        	return;
        }
        
        //JAO: Nuevo sistema de �reas!! ;-)
        // FIXME
        //mapa.areasData.dieNpc(this);
        map.areasData.resetNpc(this);
        
        if (map != null && pos().isValid()) {
            map.exitNpc(this);
        }
        
        map.enviarAlArea(pos().x, pos().y, new CharacterRemoveResponse(this.getId()));
        
        // Nos aseguramos de que el inventario sea removido...
        // asi los lobos no volveran a tirar armaduras ;))
        this.m_inv.clear();
        
        if (this.petUserOwner != null) {
        	this.petUserOwner.quitarMascota(this);
        }
        Npc ownerNpc = this.server.npcById(this.petNpcOwnerId);
        if (ownerNpc != null) {
        	if (ownerNpc.isTrainer()) {
        		((NpcTrainer)ownerNpc).removePet(this);
        	} else {
        		log.debug("ERROR, el NPC propietario de la mascota no es un entrenador!!! npc_type=" + ownerNpc.npcType());
        	}
        }
        this.server.deleteNpc(this);
    }
    
    public void efectoParalisisNpc() {
        if (this.m_contadores.Paralisis > 0) {
            this.m_contadores.Paralisis -= 1;
        } else {
            this.m_flags.set(FLAG_PARALIZADO, false);
        }
    }
    
    public void enviarMP() {
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
        	// FIXME
           // mapa.enviarATodos(serverPacketID.MSG_MP, this.m_id, pos().x, pos().y);
           // mapa.enviarAlArea(getPos().x, getPos().y, -1, serverPacketID.MSG_MP, this.m_id, pos().x, pos().y);
           // mapa.enviarAlArea(pos().x, pos().y, serverPacketID.MSG_MP, this.m_id, pos().x, pos().y);
        }
    }
    
    public void mover(Heading dir) {
        long now = (new java.util.Date()).getTime();
        if ((now - this.m_lastMove) < 250) {
			return;
		}
        this.m_lastMove = now;
        MapPos newPos = pos().copy();
        newPos.moveToHeading(dir);
        Map mapa = this.server.getMap(newPos.map);
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
                this.setPos(newPos);
                //enviarMP(); FIXME
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
                this.setPos(newPos);
                //enviarMP(); // FIXME
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
    
    public void npcEnvenenarUser(Player player) {
        int n = Util.Azar(1, 100);
        if (n < 30) {
            player.envenenar();
        }
    }
    
    public void sendPlayWave(byte sonido) {
        Map mapa = this.server.getMap(pos().map);
        // Sonido
        if (mapa != null) {
			mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(sonido, pos().x, pos().y));
		}
    }
    
    public void sendCreateFX(int fx, int val) {
        Map m = this.server.getMap(pos().map);
        if (m == null) {
			return;
		}
        m.sendCreateFX(pos().x, pos().y, getId(), (short) fx, (short) val);
    }
    
    public void tirarOro() {
        // NPCTirarOro
        // SI EL NPC TIENE ORO LO TIRAMOS
        if (this.m_giveGLD > 0) {
            Map m = this.server.getMap(pos().map);
            m.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(OBJ_ORO, this.m_giveGLD));
        }
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
    public void followMaster() {
        this.m_flags.set(FLAG_FOLLOW, true);
        this.m_movement  = MOV_SIGUE_AMO; // follow npc's master.
        this.m_flags.set(FLAG_HOSTIL, false);
        this.m_targetUser = 0;
        this.m_targetNpc  = null;
    }
    
    public void expresar() {
        // Public Sub Expresar(ByVal NpcIndex As Integer, ByVal UserIndex As Integer)
        if (this.petUserOwner == null) {
			return;
		}
        //WorldPos pos = m_maestroUser.getPos();
        if (this.m_nroExpresiones > 0) {
            int azar = Util.Azar(0, this.m_nroExpresiones - 1);
            Map mapa = this.server.getMap(pos().map);
            if (mapa != null) {            
                hablarAlArea(Color.COLOR_BLANCO, this.m_expresiones[azar]);
            }
        }
    }
    
    public void hablarAlArea(int color, String texto) {
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {            
        	mapa.enviarAlArea(pos().x, pos().y,
        			new ChatOverHeadResponse(texto, this.getId(), 
        					Color.r(color), Color.g(color), Color.b(color)));
        }
    }
    
    public void defenderse() {
        this.m_movement = MOV_NPCDEFENSA;
        this.m_flags.set(FLAG_HOSTIL, true);
    }
    
    public void cambiarDir(Heading dir) {
        // ChangeNPCChar
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
            this.m_infoChar.setDir(dir);
            mapa.enviarAlArea(pos().x, pos().y, createCC());
        }
    }
    
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'                        Modulo AI_NPC
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'AI de los NPC
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    //'?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�?�
    
    private void guardiasAI() {
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (Heading dir : Heading.values()) {
        	if (dir == Heading.NONE)
        		continue;
        	
            MapPos pos = pos().copy();
            pos.moveToHeading(dir);
            if (pos.isValid()) {
                if (mapa.hasPlayer(pos.x, pos.y)) {
                    Player player = mapa.getPlayer(pos.x, pos.y);
                    if (player.isAlive() && !player.isGM()) {
                        // �ES CRIMINAL?
                    	if (npcType() != NpcType.NPCTYPE_GUARDIAS_CAOS) {
                    		if (player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.m_attackedBy.equalsIgnoreCase(player.getNick()) && !this.m_flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		}
                    	} else {
                    		if (!player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.m_attackedBy.equalsIgnoreCase(player.getNick()) && !this.m_flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		}
                    	}
                    	/*
                        if ((player.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CRIMINALES) || 
                            (!player.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CIUDADANOS)) {
                            cambiarDir(dir);
                            npcAtacaUser(player); // ok
                            return;
                        } else if (!isFollowing() && m_attackedBy.equalsIgnoreCase(player.getNick())) {
                            cambiarDir(dir);
                            npcAtacaUser(player); // ok
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (Heading dir : Heading.values()) {
        	if (dir == Heading.NONE)
        		continue;
            MapPos pos = pos().copy();
            pos.moveToHeading(dir);
            if (pos.isValid()) {
                if (mapa.hasPlayer(pos.x, pos.y)) {
                    Player player = mapa.getPlayer(pos.x, pos.y);
                    if (player.isAlive() && !player.isGM()) {
                        if (lanzaSpells()) {
                            npcLanzaUnSpell(player);
                        }
                        cambiarDir(dir);
                        npcAtacaUser(player); // ok
                        return;
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void hostilBuenoAI() {
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (Heading dir : Heading.values()) {
        	if (dir == Heading.NONE)
        		continue;
            MapPos pos = pos().copy();
            pos.moveToHeading(dir);
            if (pos.isValid()) {
                if (mapa.hasPlayer(pos.x, pos.y)) {
                    Player player = mapa.getPlayer(pos.x, pos.y);
                    if (player.isAlive() && !player.isGM() && this.m_attackedBy.equalsIgnoreCase(player.getNick())) {
                        if (lanzaSpells()) {
                            npcLanzaUnSpell(player);
                        }
                        cambiarDir(dir);
                        npcAtacaUser(player); // ok
                        return;
                    }
                }
            }
        }
        restoreOldMovement();
    }
    
    private void irUsuarioCercano() {
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        
                        if (player != null) {
                        
                            if (player.isAlive() && !player.isInvisible() && !player.isGM()) {
                                if (lanzaSpells()) {
                                   npcLanzaUnSpell(player);
                                }
                                Heading dir = pos().findDirection(player.pos());
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        if (!player.isGM() && this.m_attackedBy.equalsIgnoreCase(player.getNick())) {
                            if (getPetUserOwner() != null) {
		                        if (	!getPetUserOwner().isCriminal() && !player.isCriminal() && 
		                        		(getPetUserOwner().hasSafeLock() || getPetUserOwner().userFaction().ArmadaReal)) {
		                            getPetUserOwner().enviarMensaje("La mascota no atacar� a ciudadanos si eres miembro de la Armada Real o tienes el seguro activado", FontType.FONTTYPE_INFO);
		                            this.m_attackedBy = "";
		                            followMaster();
		                            return;
		                        }
                            }
                            if (player.isAlive() && !player.isInvisible()) {
                                if (lanzaSpells()) {
                                    npcLanzaUnSpell(player);
                                }
                                Heading dir = pos().findDirection(player.pos());
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        
                        if (player == null) break;
                        
                        if (player.isCriminal() && player.isAlive() && !player.isInvisible() && !player.isGM()) {
                            if (lanzaSpells()) {
                                npcLanzaUnSpell(player);
                            }
                            Heading dir = pos().findDirection(player.pos());
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        if (!player.isCriminal() && player.isAlive() && !player.isInvisible() && !player.isGM()) {
                            if (lanzaSpells()) {
                                npcLanzaUnSpell(player);
                            }
                            Heading dir = pos().findDirection(player.pos());
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
        short dir = pos().findDirection(m_maestroUser.getPos());
        mover(dir);
        */
    	// FIXME: ESTO SE PUEDE OPTIMIZAR, YO SE QUIEN Y DONDE ESTA EL AMO !!!
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        if (player.isAlive() && !player.isInvisible() && getPetUserOwner() == player &&
                        		getPetUserOwner().pos().distance(pos()) > 3) {
                            Heading dir = pos().findDirection(player.pos());
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    Npc npc = mapa.getNPC(x, y);
                    if (npc != null) {
                        if (this.m_targetNpc == npc) {
                            Heading dir = pos().findDirection(pos);
                            mover(dir);
                            npcAtacaNpc(npc);
                            return;
                        }
                    }
                }
            }
        }
        // No se encontr� al NPC objetivo.
        if (this.petUserOwner != null) {
            followMaster();
        } else {
            this.m_movement  = this.m_oldMovement;
            this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
        }
    }
    
    public void moverAlAzar() {
        mover(Heading.value(Util.Azar(1, 4)));
    }
    
    public void doAI() {
    	if (this.isStatic()) {
    		// no movement
    		return;
    	}
    		
        // NPCAI
        // <<<<<<<<<<< Ataques >>>>>>>>>>>>>>>>
        if (this.petUserOwner == null) {
            // Busca a alguien para atacar
            // �Es un guardia?
            if (this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS || this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS_CAOS) {
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
                if (this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS) {
                    if (Util.Azar(1, 12) == 3) {
                        moverAlAzar();
                    }
                    persigueCriminal();
                } else if (this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS_CAOS) {
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
    
    private void npcLanzaUnSpell(Player player) {
        if (player.isInvisible()) {
			return;
		}
        npcLanzaSpellSobreUser(player, this.m_spells[Util.Azar(0, this.m_nroSpells-1)]);
    }
    
    private void npcLanzaSpellSobreUser(Player player, short spell) {
        if (!puedeAtacar()) {
			return;
		}
        if (estaInvisible()) {
			return;
		}
        if (player.isGM()) {
			return;
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        int da�o = 0;
        Spell hechizo = this.server.getHechizo(spell);
        if (hechizo.SubeHP == 1) {
            da�o = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            player.enviarSonido(hechizo.WAV);
            player.sendCreateFX(hechizo.FXgrh, hechizo.loops);
            player.stats().addMinHP(da�o);
            player.enviarMensaje(this.m_name + " te ha dado " + da�o + " puntos de vida.", FONTTYPE_FIGHT);
            player.sendUpdateUserStats();
        } else if (hechizo.SubeHP == 2) {
            da�o = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            player.enviarSonido(hechizo.WAV);            
            player.sendCreateFX(hechizo.FXgrh, hechizo.loops);
            player.stats().quitarHP(da�o);
            player.enviarMensaje(this.m_name + " te ha quitado " + da�o + " puntos de vida.", FONTTYPE_FIGHT);
            player.sendUpdateUserStats();
            // Muere
            if (player.stats().MinHP < 1) {
                player.stats().MinHP = 0;
                player.userDie();
                if (getPetUserOwner() != null) {
                    getPetUserOwner().contarMuerte(player);
                    getPetUserOwner().actStats(player);
                }
            }
        }
        if (hechizo.isParaliza()) {
            player.paralizar(hechizo);
        }
    }
    
    private void npcAtacaUser(Player player) {
        Map mapa = this.server.getMap(pos().map);
        // El npc puede atacar ???
        if (!puedeAtacar()) {
			return;
		}
        if (player.isGM()) {
			return;
		}
        player.getUserPets().petsAttackNpc(this);
		setTargetUser(player.getId());
        if (player.flags().AtacadoPorNpc == 0 && player.flags().AtacadoPorUser == 0) {
			player.flags().AtacadoPorNpc = this.getId();
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        if (this.m_snd1 > 0) {
        	mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(this.m_snd1, pos().x,pos().y));
		}
        if (player.npcImpacto(this)) {
        	mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(SND_IMPACTO, pos().x,pos().y));
            if (!player.isSailing()) {
            	mapa.enviarAlArea(pos().x, pos().y, new CreateFXResponse(player.getId(), FXSANGRE, (short) 0));
			}
            player.npcDa�o(this);
            // �Puede envenenar?
            if (puedeEnvenenar()) {
				npcEnvenenarUser(player);
			}
        } else {
        	player.sendPacket(new NPCSwingResponse());
        }
        // -----Tal vez suba los skills------
        player.subirSkill(Skill.SKILL_Tacticas);
        player.sendUpdateUserStats();
        // Controla el nivel del usuario
        player.checkUserLevel();
    }
    
    private boolean npcImpactoNpc(Npc victim) {
        long poderAtt = this.m_poderAtaque;
        long poderEva = victim.m_poderEvasion;
        double probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtt - poderEva) * 0.4)));
        return (Util.Azar(1, 100) <= probExito);
    }
    
    private void npcDa�oNpc(Npc victima) {
        int da�o = Util.Azar(this.m_estads.MinHIT, this.m_estads.MaxHIT);
        victima.m_estads.quitarHP(da�o);
        if (victima.m_estads.MinHP < 1) {
            this.m_movement = this.m_oldMovement;
            if (this.m_attackedBy.length() > 0) {
                this.m_flags.set(FLAG_HOSTIL, this.m_flags.get(FLAG_OLD_HOSTILE));
            }
            followMaster();
            victima.muereNpc(this.petUserOwner);
        }
    }
    
    private void npcAtacaNpc(Npc victim) {
        Map mapa = this.server.getMap(pos().map);
        
        // El npc puede atacar ???
        if (!puedeAtacar()) {
			return;
		}
        this.m_flags.set(FLAG_PUEDE_ATACAR, false);
        victim.m_targetNpc = this;
        victim.m_movement = MOV_NPC_ATACA_NPC;
        
        if (this.m_snd1 > 0) {
        	mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(this.m_snd1, pos().x, pos().y));
		}
        if (npcImpactoNpc(victim)) {
            if (victim.m_snd2 > 0) {
            	mapa.enviarAlArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(victim.m_snd2, victim.pos().x, victim.pos().y));
			} else {
				mapa.enviarAlArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SND_IMPACTO2, victim.pos().x, victim.pos().y));
			}
            if (this.petUserOwner != null) {
            	mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(SND_IMPACTO, pos().x, pos().y));
			} else {
				mapa.enviarAlArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SND_IMPACTO, victim.pos().x, victim.pos().y));
			}
            npcDa�oNpc(victim);
        } else {
            if (this.petUserOwner != null) {
            	mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse(SOUND_SWING, pos().x, pos().y));
			} else {
				mapa.enviarAlArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SOUND_SWING, victim.pos().x, victim.pos().y));
			}
        }
    }
    
    private boolean userNear() {
        //#################################################################
        //Returns True if there is an user adjacent to the npc position.
        //#################################################################
        return pos().distance(this.m_pfinfo.m_targetPos) <= 1;
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
        MapPos pos = MapPos.mxy(pos().map, (short)loc.x, (short)loc.y);
        Heading dir = pos().findDirection(pos);
        
        if (DEBUG)
        	System.out.println("[PF] " + this.current_step + "/" + this.current_path.size() + ": " + pos() + " >> " + pos + " tg=" + this.m_pfinfo.m_targetPos);
        
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
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasPlayer(x, y)) {
                        Player player = mapa.getPlayer(x, y);
                        if (player.isAlive() && !player.isInvisible()) {
                        	this.m_pfinfo = new PFINFO(MapPos.mxy(pos().map, x, y), player);
                        	PathFinding pf = new PathFinding();
                        	this.current_path = pf.seekPath(this);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void backup(IniFile ini) {
        // Sub BackUPnPc(NpcIndex As Integer)
        // General
        String section = "NPC" + this.m_numero;
        ini.setValue(section, "NpcType", this.m_NPCtype.value());
        if (this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS) {
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
        // Flags
        ini.setValue(section, "ReSpawn", !this.m_flags.get(FLAG_RESPAWN));
        ini.setValue(section, "BackUp", this.m_flags.get(FLAG_BACKUP));
        ini.setValue(section, "Domable", this.m_domable);
        // Inventario
        ini.setValue(section, "NroItems", this.m_inv.size());
        for (int i = 1; i <= this.m_inv.size(); i++) {
            ini.setValue(section, "Obj" + i, this.m_inv.getObjeto(i).objid + "-" + this.m_inv.getObjeto(i).cant);
        }
    }    
    
    
    /** Cargar un NPC desde un ini. */
    protected void leerNpc(IniFile ini, int npc_ind) {
        String section = "NPC" + npc_ind;
        
        this.m_name = ini.getString(section, "Name");
        this.m_desc = ini.getString(section, "Desc");
        
        this.m_movement = ini.getShort(section, "Movement");
        this.m_oldMovement = this.m_movement;
        this.m_flags.set(FLAG_AGUA_VALIDA, ini.getInt(section, "AguaValida") == 1);
        this.m_flags.set(FLAG_TIERRA_INVALIDA, ini.getInt(section, "TierraInvalida") == 1);
        this.m_flags.set(FLAG_FACCION, ini.getInt(section, "Faccion") == 1);
        
        this.m_NPCtype = NpcType.value(ini.getShort(section, "NpcType"));
        
        if (this.m_NPCtype == NpcType.NPCTYPE_GUARDIAS) {
            this.m_guardiaPersigue = ini.getShort(section, "GuardiaPersigue");
        }
        this.m_deQuest = (ini.getShort(section, "DeQuest") == 1);
    
        this.m_infoChar.m_cuerpo   = ini.getShort(section, "Body");
        this.m_infoChar.m_cabeza   = ini.getShort(section, "Head");
        this.m_infoChar.m_dir      = (byte)ini.getShort(section, "Heading");
        
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
        
        this.m_snd1  = (byte) ini.getShort(section, "Snd1");
        this.m_snd2  = (byte) ini.getShort(section, "Snd2");
        this.m_snd3  = (byte) ini.getShort(section, "Snd3");
        this.m_snd4  = (byte) ini.getShort(section, "Snd4");
        
        //Spells
        this.m_nroSpells = (byte) ini.getInt(section, "LanzaSpells");
        this.m_flags.set(FLAG_LANZA_SPELLS, this.m_nroSpells > 0);
        if (this.m_nroSpells > 0) {
            for (int k = 0; k < (this.m_nroSpells); k++) {
                String spellName = "Sp" + (k+1);
                this.m_spells[k] = ini.getShort(section, spellName);
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
    
    private String estadoVidaExacta() {
    	if (this.stats().MaxHP <= 0) {
			return "";
		}
    	return "(" + this.stats().MinHP + "/" + this.stats().MaxHP + ")";    	
    }
    
    public String estadoVida(Player player) {
    	if (this.stats().MaxHP <= 0) {
			return "";
		}
    	//agush: con esto los gms ahora ven la vida de los npcs
    	if (!player.isGM() == false) {
			return estadoVidaExacta();
		}
        short skSup = player.skills().get(Skill.SKILL_Supervivencia);
        double vidaPct = this.stats().MinHP / this.stats().MaxHP;
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
    
    public boolean isTrainer() {
    	return npcType() == NpcType.NPCTYPE_ENTRENADOR;    	
    }
    
    public void setPetTargetNpc(Npc targetNpc) {
    	if (targetNpc == this) {
    		// can't attack himself
    		return;
    	}
		this.m_targetNpc = targetNpc;
		this.m_movement = Npc.MOV_NPC_ATACA_NPC;
    }
    
	
	protected ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
}
