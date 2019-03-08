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
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_INMOVILIZADO;
import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import java.util.List;

import org.ArgentumOnline.server.AbstractCharacter;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Skill;
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
import org.ArgentumOnline.server.protocol.ParalizeOKResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.user.Player;
import org.ArgentumOnline.server.user.Spell;
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

	/* FIXME
Public Enum TipoAI
    ESTATICO = 1
    MueveAlAzar = 2
    NpcMaloAtacaUsersBuenos = 3
    NPCDEFENSA = 4
    GuardiasAtacanCriminales = 5
    NpcObjeto = 6
    SigueAmo = 8
    NpcAtacaNpc = 9
    NpcPathfinding = 10
    
    'Pretorianos
    SacerdotePretorianoAi = 20
    GuerreroPretorianoAi = 21
    MagoPretorianoAi = 22
    CazadorPretorianoAi = 23
    ReyPretoriano = 24
End Enum
	 */
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


	public int areaID = 0;

	public int areaPerteneceX = 0;
	public int areaPerteneceY = 0;

	public int areaRecibeX = 0;
	public int areaRecibeY = 0;

	public int minX = 0;
	public int minY = 0;

    protected MapPos origPos  = MapPos.empty();
    
    long lastMove = 0;

    public String name = "";
    public String description = "";

    NpcType npcType = NpcType.NPCTYPE_COMUN;
    int   npcNumber = 0;
    short level     = 0;

    private NpcFlags flags = new NpcFlags();

    short movement   = 0;
    public short   oldMovement   = 0;
    
    public String  attackedBy = "";

    int   targetUser    = 0;
    Npc   targetNpc = null;
    short tipoItems = 0;

    short skillDomar = 0;

    int poderAtaque  = 0;
    int poderEvasion = 0;
    int inflation    = 0; // TODO REVISAR
    int giveEXP      = 0;
    int giveGLD      = 0;

    //int expDada = 0;
    int expCount = 0;

    public short   domable   = 0;
    
    boolean isQuest = false;
    short guardiaPersigue = GUARDIAS_PERSIGUEN_CRIMINALES; // TODO
    
    public byte   snd1 = 0; // Sonido ataque NPC
    public byte   snd2 = 0; // Sonido ataque exitoso NPC
    public byte   snd3 = 0; // Sonido muere NPC
    public byte   snd4 = 0; // ???

    public NpcStats stats = new NpcStats();

    NpcCounters counters = new NpcCounters();

    byte  expressionsCount = 0;
    String expressions[] = new String[MAX_EXPRESIONES];

    byte  spellsCount = 0;
    short spells[] = new short[MAX_NUM_SPELLS];  // le da vida ;)

    Player petUserOwner = null;
    short petNpcOwnerId  = 0;

    /**
     * El inventario tiene doble función:
     * - en npc comerciante, contiene los items en venta
     * - en npc hostil, contiene los items dropeados
     */
    protected Inventory npcInv;

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

    protected GameServer server;

    /** Creates a new instance of NPC */
    protected Npc(int npc_numero, GameServer server) {
    	this.server = server;

        this.npcNumber = npc_numero;
        this.npcInv = new Inventory(server, 20);
        this.setId(server.nextId());
        loadInfoNPC(this.npcNumber, server.isLoadBackup());
    }

    /**
     * Crea una mascota del tipo nroNPC, cerca de la posición orig.
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
			npc.flags.set(FLAG_RESPAWN, false);
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
            npc.sendPlayWave(SOUND_WARP);
            npc.sendCreateFX(FXWARP, 0);
        }
        npc.activate();

        return npc;
    }

    public void reSpawnNpc() {
        //////////// FIXME - HAY QUE USAR LA POSICION ORIGINAL (m_orig) REVISAR !!!
        if (puedeReSpawn()) {
        	if (respawnOrigPos()) {
        		spawnNpc(this.npcNumber, this.origPos, false, true); // TODO: PROBAR !!!
        	} else {
        		spawnNpc(this.npcNumber, MapPos.mxy(pos().map, (short)0, (short)0), false, true); // TODO: PROBAR !!!
        	}
        } else {
        	log.debug("{{{{{{{ DEBUG }}}}}}} NO PUEDE RESPAWN !!!");
        }
    }

    public Inventory npcInv() {
        return this.npcInv;
    }

    public int inflation() {
    	return this.inflation;
    }

    public boolean isNpcGuard() {
    	return this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL;
    }

    public boolean isGambler() {
    	return this.npcType == NpcType.NPCTYPE_TIMBERO;
    }

    // added by gorlok
    boolean spellSpawnedPet = false;
    public boolean isSpellSpawnedPet() {
		return this.spellSpawnedPet;
	}
    public void setSpellSpawnedPet(boolean spellSpawnedPet) {
		this.spellSpawnedPet = spellSpawnedPet;
	}


    public boolean isStatic() {
    	return this.movement == Npc.MOV_ESTATICO;
    }

    public void makeStatic() {
    	this.movement = Npc.MOV_ESTATICO;
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
        return this.isQuest;
    }

    public short getSonidoAtaqueNpc() {
        return this.snd1;
    }

    public short getSonidoAtaqueExitoso() {
        return this.snd2;
    }

    public short getSonidoMuereNpc() {
        return this.snd3;
    }

    public boolean lanzaSpells() {
        return this.flags.get(FLAG_LANZA_SPELLS);
    }

    public boolean getBackup() {
        return this.flags.get(FLAG_BACKUP);
    }

    public void volverMaldito() {
        this.flags.set(FLAG_MALDICION, true);
    }

    public void quitarMaldicion() {
        this.flags.set(FLAG_MALDICION, false);
    }

    public void volverBendito() {
        this.flags.set(FLAG_BENDICION, true);
    }

    public boolean esHostil() {
        return this.flags.get(FLAG_HOSTIL);
    }

    public boolean estaInvisible() {
        return this.flags.get(FLAG_INVISIBLE);
    }

    public boolean respawnOrigPos() {
        return this.flags.get(FLAG_RESPAWN_ORIG_POS);
    }

    public void hacerInvisible() {
        this.flags.set(FLAG_INVISIBLE, true);
    }

    public void hacerVisible() {
        this.flags.set(FLAG_INVISIBLE, false);
    }

    public boolean puedeAtacar() {
        return this.flags.get(FLAG_PUEDE_ATACAR);
    }

    public void setPuedeAtacar(boolean estado) {
        this.flags.set(FLAG_PUEDE_ATACAR, estado);
    }

    public boolean esFaccion() {
        return this.flags.get(FLAG_FACCION);
    }

    private boolean puedeEnvenenar() {
        return this.flags.get(FLAG_ENVENENA);
    }

    public void envenenar() {
        this.flags.set(FLAG_ENVENENA, true);
    }

    public void curarVeneno() {
        this.flags.set(FLAG_ENVENENA, false);
    }

    public boolean puedeReSpawn() {
        return this.flags.get(FLAG_RESPAWN);
    }

    protected boolean invReSpawn() {
        return this.flags.get(FLAG_INV_RESPAWN);
    }

    private boolean isFollowing() {
        return this.flags.get(FLAG_FOLLOW);
    }

    public boolean afectaParalisis() {
        return this.flags.get(FLAG_AFECTA_PARALISIS);
    }

    public void paralizar() {
        this.flags.set(FLAG_PARALIZADO, true);
        this.counters.Paralisis = IntervaloParalizado;
    }

    public void desparalizar() {
        this.flags.set(FLAG_PARALIZADO, false);
        this.counters.Paralisis = 0;
    }

    public void inmovilizar() {
        this.flags.set(FLAG_INMOVILIZADO, true);
    }

    public void desinmovilizar() {
        this.flags.set(FLAG_INMOVILIZADO, false);
    }

    @Override
	public String toString() {
        return this.name + " (id=" + this.getId() + ")";
    }

    public String getName() {
    	return this.name;
    }

    public MapPos getOrig() {
        return this.origPos;
    }

    public boolean isNpcActive() {
        return this.flags.get(FLAG_NPC_ACTIVE);
    }

	public void putAreas(int ax, int ay) {
		this.areaPerteneceX = ax;
		this.areaPerteneceY = ay;
	}

	public int getAreaX() {
		return this.areaPerteneceX;
	}

	public int getAreaY() {
		return this.areaPerteneceY;
	}

    public int getPoderAtaque() {
        return this.poderAtaque;
    }

    public int getPoderEvasion() {
        return this.poderEvasion;
    }

    public boolean getAttackable() {
        return this.flags.get(FLAG_ATACABLE);
    }

    public boolean esAguaValida() {
        return this.flags.get(FLAG_AGUA_VALIDA);
    }

    public boolean esTierraInvalida() {
        return this.flags.get(FLAG_TIERRA_INVALIDA);
    }

    public String getDesc() {
        return this.description;
    }

    public int getNumero() {
        return this.npcNumber;
    }

    public Player getPetUserOwner() {
		return this.petUserOwner;
	}

    public void setPetUserOwner(Player petUserOwner) {
		this.petUserOwner = petUserOwner;
	}

    public void releasePet() {
    	this.petUserOwner = null;
    	this.petNpcOwnerId = 0;
    }

    public boolean estaParalizado() {
        return this.flags.get(FLAG_PARALIZADO);
    }

    public boolean estaInmovilizado() {
        return this.flags.get(FLAG_INMOVILIZADO);
    }

    public boolean comercia() {
        return this.flags.get(FLAG_COMERCIA);
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
        this.flags.set(FLAG_NPC_ACTIVE, true);
    }

    public void setAttackedBy(String nick) {
        this.attackedBy = nick;
    }

    public boolean atacadoPorUsuario() {
        return this.attackedBy.length() > 0;
    }

    public void setGiveGLD(int val) {
        this.giveGLD = val;
    }

    public int getTargetUser() {
        return this.targetUser;
    }

    public Npc getTargetNpc() {
    	return this.targetNpc;
    }

    public void setTargetUser(int target) {
        this.targetUser = target;
    }

    public NpcType npcType() {
        return this.npcType;
    }

    public NpcStats stats() {
        return this.stats;
    }

    public NpcCounters getContadores() {
        return this.counters;
    }

    public void setPetNpcOwner(Npc petOwner) {
        this.petNpcOwnerId = petOwner.getId();
    }

    public short domable() {
        return this.domable;
    }

    public CharacterCreateResponse createCC() {
    	return new CharacterCreateResponse(
			getId(),
			this.infoChar.body(),
			this.infoChar.head(),

			this.infoChar.heading(),
			pos().x,
			pos().y,

			this.infoChar.weapon(),
			this.infoChar.shield(),
			this.infoChar.helmet(),

			this.infoChar.fx(),
			this.infoChar.loops(),

			"",
			(byte)0,
			(byte)0);
    }

    private void loadInfoNPC(int npc_ind, boolean loadBackup) {
        IniFile ini = this.server.getNpcLoader().getIniFile(npc_ind, loadBackup);
        leerNpc(ini, npc_ind);
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

    public void calcularDarExp(Player player, int daño) {
        // [Alejo]
        // Modifique un poco el sistema de exp por golpe, ahora
        // son 2/3 de la exp mientras esta vivo, el resto se
        // obtiene al matarlo.
        int expSinMorir = (2 * this.giveEXP ) / 3;
        int totalNpcVida = stats().MaxHP;
        if (totalNpcVida <= 0) {
			return;
		}
        if (daño < 0) {
			daño = 0;
		}
        if (daño > stats().MinHP) {
			daño = stats().MinHP;
		}
        int expADar = (daño * expSinMorir) / totalNpcVida;

        if (expADar <= 0) {
			return;
		}
        if (expADar > this.expCount) {
            expADar = this.expCount;
            this.expCount = 0;
        } else {
            this.expCount -= expADar;
        }
        if (expADar > 0) {
        	expADar = expADar * 1000;
            player.stats().addExp(expADar);
            player.sendMessage("Has ganado " + expADar + " puntos de experiencia.", FONTTYPE_FIGHT);
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

            if (this.snd3 > 0) {
            	m.sendToArea(pos.x, pos.y, new PlayWaveResponse(this.snd3, player.pos().x, player.pos().y));
            }
            player.flags().TargetNpc = 0;
            player.flags().TargetNpcTipo = 0;
            // El user que lo mato tiene mascotas?
            if (player.getUserPets().hasPets()) {
                player.getUserPets().petsFollowMaster(this);
            }
            player.sendMessage("Has matado la criatura!", FONTTYPE_FIGHT);
            if (this.expCount > 0) {
                player.stats().addExp(this.expCount);
                player.sendMessage("Has ganado " + this.expCount + " puntos de experiencia.", FONTTYPE_FIGHT);
            } else {
                player.sendMessage("No has ganado experiencia al matar la criatura.", FONTTYPE_FIGHT);
            }
            player.stats().incNPCsMuertos();
            player.quest().checkNpcEnemigo(player, this);

            if (this.stats.Alineacion == 0) {
            	// TODO: ¿No debería compararse con NpcType==2? Hay otros guardias aparte del npcNumber=6
                if (this.npcNumber == GUARDIAS) {
                    player.volverCriminal();
                }
                if (!player.isGod()) {
                    player.reputation().incAsesino(vlAsesino);
                }
            } else if (this.stats.Alineacion == 1) {
                player.reputation().incPlebe(vlCazador);
            } else if (this.stats.Alineacion == 2) {
                player.reputation().incNoble(vlAsesino / 2);
            } else if (this.stats.Alineacion == 4) {
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
        // Quitamos el npc
        quitarNPC();
        // ReSpawn o no
        reSpawnNpc();
    }

    private void tirarItems() {
        // NPC_TIRAR_ITEMS
        // TIRA TODOS LOS ITEMS DEL NPC
        if (this.npcInv.size() > 0) {
            for (int i = 1; i <= this.npcInv.size(); i++) {
                if (this.npcInv.getObjeto(i) != null && this.npcInv.getObjeto(i).objid > 0) {
                    Map m = this.server.getMap(pos().map);
                    m.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(this.npcInv.getObjeto(i).objid, this.npcInv.getObjeto(i).cant));
                }
            }
        }
    }

    public void quitarNPC() {
    	if (DEBUG) {
    		log.debug("NPC BORRADO");
    	}

        this.flags.set(FLAG_NPC_ACTIVE, false);
        Map map = this.server.getMap(pos().map);
        if (map == null) {
        	return;
        }

        map.areasData.resetNpc(this);

        if (map != null && pos().isValid()) {
            map.exitNpc(this);
        }

        map.sendToArea(pos().x, pos().y, new CharacterRemoveResponse(this.getId()));

        // Nos aseguramos de que el inventario sea removido...
        // asi los lobos no volveran a tirar armaduras ;))
        this.npcInv.clear();

        if (this.petUserOwner != null) {
        	this.petUserOwner.removePet(this);
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
        if (this.counters.Paralisis > 0) {
            this.counters.Paralisis -= 1;
        } else {
            this.flags.set(FLAG_PARALIZADO, false);
        }
    }

    public void mover(Heading dir) {
        long now = (new java.util.Date()).getTime();
        if ((now - this.lastMove) < 250) {
			return;
		}
        this.lastMove = now;
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
                if (!esAguaValida() && mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (esTierraInvalida() && !mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + esAguaValida());
                    log.debug("OJO, ya hay otro NPC!!! " + this + " " +
                    newPos.x + " " + newPos.y + " encontro=" + mapa.getNPC(newPos.x, newPos.y));
                    return;
                }
                // Update map and user pos
                this.infoChar.heading(dir);
                mapa.moverNpc(this, newPos.x, newPos.y);
                this.setPos(newPos);
            }
        } else { // No es mascota
            // Controlamos que la posicion sea legal, los npc que
            // no son mascotas tienen mas restricciones de movimiento.
            if (mapa.isLegalPosNPC(newPos, esAguaValida())) {
                if (!esAguaValida() && mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (esTierraInvalida() && !mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + esAguaValida());
                    log.debug("OJO, ya hay otro NPC!!! " + this + " " +
                    newPos.x + " " + newPos.y + " encontro=" + mapa.getNPC(newPos.x, newPos.y));
                    return;
                }
                // Update map and user pos
                this.infoChar.heading(dir);
                mapa.moverNpc(this, newPos.x, newPos.y);
                this.setPos(newPos);
            } else {
                if (this.movement == MOV_NPC_PATHFINDING) {
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
			mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(sonido, pos().x, pos().y));
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
        if (this.giveGLD > 0) {
            Map m = this.server.getMap(pos().map);
            m.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(OBJ_ORO, this.giveGLD));
        }
    }

    /** Seguir a un usuario / Follow user */
    public void seguirUsuario(String nombreUsuario) {
        // doFollow
        if (isFollowing()) {
            this.attackedBy = "";
            this.flags.set(FLAG_FOLLOW, false);
            this.movement  = this.oldMovement;
            this.flags.set(FLAG_HOSTIL, this.flags.get(FLAG_OLD_HOSTILE));
        } else {
            this.attackedBy = nombreUsuario;
            this.flags.set(FLAG_FOLLOW, true);
            this.movement = MOV_NPCDEFENSA; // follow
            this.flags.set(FLAG_HOSTIL, false);
        }
    }

    /** Seguir al amo / Follow master */
    public void followMaster() {
        this.flags.set(FLAG_FOLLOW, true);
        this.movement  = MOV_SIGUE_AMO; // follow npc's master.
        this.flags.set(FLAG_HOSTIL, false);
        this.targetUser = 0;
        this.targetNpc  = null;
    }

    public void expresar() {
        if (this.petUserOwner == null) {
			return;
		}
        if (this.expressionsCount > 0) {
            int azar = Util.Azar(0, this.expressionsCount - 1);
            Map mapa = this.server.getMap(pos().map);
            if (mapa != null) {
                hablarAlArea(Color.COLOR_BLANCO, this.expressions[azar]);
            }
        }
    }

    public void hablarAlArea(int color, String texto) {
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
        	mapa.sendToArea(pos().x, pos().y,
        			new ChatOverHeadResponse(texto, this.getId(),
        					Color.r(color), Color.g(color), Color.b(color)));
        }
    }

    public void defenderse() {
        this.movement = MOV_NPCDEFENSA;
        this.flags.set(FLAG_HOSTIL, true);
    }

    public void cambiarDir(Heading dir) {
        // ChangeNPCChar
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
            this.infoChar.heading(dir);
            mapa.sendToArea(pos().x, pos().y, createCC());
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
                        // ¿ES CRIMINAL?
                    	if (npcType() != NpcType.NPCTYPE_GUARDIAS_CAOS) {
                    		if (player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(player.getNick()) && !this.flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		}
                    	} else {
                    		if (!player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(player.getNick()) && !this.flags.get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		}
                    	}
                    	/* FIXME
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
                    if (player.isAlive() && !player.isGM() && this.attackedBy.equalsIgnoreCase(player.getNick())) {
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
                        if (!player.isGM() && this.attackedBy.equalsIgnoreCase(player.getNick())) {
                            if (getPetUserOwner() != null) {
		                        if (	!getPetUserOwner().isCriminal() && !player.isCriminal() &&
		                        		(getPetUserOwner().hasSafeLock() || getPetUserOwner().userFaction().ArmadaReal)) {
		                            getPetUserOwner().sendMessage("La mascota no atacará a ciudadanos si eres miembro de la Armada Real o tienes el seguro activado", FontType.FONTTYPE_INFO);
		                            this.attackedBy = "";
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
        this.movement  = this.oldMovement;
        this.flags.set(FLAG_HOSTIL, this.flags.get(FLAG_OLD_HOSTILE));
        this.attackedBy = "";
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
        Player master = getPetUserOwner();
        if (master == null) {
        	return;
        }
        // FIXME debe ser por rango de visión
        if (master.isAlive() 
    		&& !master.isInvisible() 
    		&& master.pos().distance(pos()) > 3) {
		            Heading dir = pos().findDirection(master.pos());
		            mover(dir);
		            return;
        }
        restoreOldMovement();
    }

    private void aiNpcAtacaNpc() {
    	// FIXME BROKEN
    	/*
    	if (targetNpc != null && targetNpc.pos().map == pos().map) {
            Map map = this.server.getMap(pos().map);
            if (map == null) {
    			return;
    		}
    		
    	}
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
                        if (this.targetNpc == npc) {
                            Heading dir = pos().findDirection(pos);
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
            followMaster();
        } else {
            this.movement  = this.oldMovement;
            this.flags.set(FLAG_HOSTIL, this.flags.get(FLAG_OLD_HOSTILE));
        }
        */
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
            // ¿Es un guardia?
            if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL || this.npcType == NpcType.NPCTYPE_GUARDIAS_CAOS) {
                guardiasAI();
            } else if (esHostil() && this.stats.Alineacion != 0) {
                hostilMalvadoAI();
            } else if (esHostil() && this.stats.Alineacion == 0) {
                hostilBuenoAI();
            }
        } else {
            // Evitamos que ataque a su amo, a menos
            // que el amo lo ataque.
            // 'Call HostilBuenoAI(NpcIndex)
        }
        // <<<<<<<<<<< Movimiento >>>>>>>>>>>>>>>>
        switch (this.movement) {
            case MOV_MUEVE_AL_AZAR:
                if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL) {
                    if (Util.Azar(1, 12) == 3) {
                        moverAlAzar();
                    }
                    persigueCriminal();
                } else if (this.npcType == NpcType.NPCTYPE_GUARDIAS_CAOS) {
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
        npcLanzaSpellSobreUser(player, this.spells[Util.Azar(0, this.spellsCount-1)]);
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
        this.flags.set(FLAG_PUEDE_ATACAR, false);
        int daño = 0;
        
        Spell hechizo = this.server.getSpell(spell);
        if (hechizo.SubeHP == 1) {
            daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            player.sendWave(hechizo.WAV);
            player.sendCreateFX(hechizo.FXgrh, hechizo.loops);
            player.stats().addHP(daño);
            player.sendMessage(this.name + " te ha dado " + daño + " puntos de vida.", FONTTYPE_FIGHT);
            player.sendUpdateUserStats();
            
        } else if (hechizo.SubeHP == 2) {
            daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
            player.sendWave(hechizo.WAV);
            player.sendCreateFX(hechizo.FXgrh, hechizo.loops);
            player.stats().removeHP(daño);
            player.sendMessage(this.name + " te ha quitado " + daño + " puntos de vida.", FONTTYPE_FIGHT);
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
        
        if (hechizo.isParaliza() || hechizo.isInmoviliza()) {
    		if (!player.flags().Paralizado) {
    			player.sendWave(hechizo.WAV);
    			player.sendCreateFX(hechizo.FXgrh, hechizo.loops);
    			
    			if (hechizo.isInmoviliza()) {
    				player.flags().Inmovilizado = true;
    			}
    			
    			player.flags().Paralizado = true;
    			player.counters().Paralisis = IntervaloParalizado;
    			player.sendPacket(new ParalizeOKResponse());
    			/*
        If UserList(UserIndex).Invent.AnilloEqpObjIndex = SUPERANILLO Then
            Call WriteConsoleMsg(UserIndex, " Tu anillo rechaza los efectos del hechizo.", FontTypeNames.FONTTYPE_FIGHT)
            Exit Sub
        End If
        
        If Hechizos(Spell).Inmoviliza = 1 Then
            UserList(UserIndex).flags.Inmovilizado = 1
        End If
          
        UserList(UserIndex).flags.Paralizado = 1
        UserList(UserIndex).Counters.Paralisis = IntervaloParalizado
          
        Call WriteParalizeOK(UserIndex)

    			 */
    		}
    		
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
        this.flags.set(FLAG_PUEDE_ATACAR, false);
        if (this.snd1 > 0) {
        	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(this.snd1, pos().x,pos().y));
		}
        if (player.npcImpacto(this)) {
        	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(SOUND_IMPACTO, pos().x,pos().y));
            if (!player.isSailing()) {
            	mapa.sendToArea(pos().x, pos().y, new CreateFXResponse(player.getId(), FXSANGRE, (short) 0));
			}
            player.npcDaño(this);
            // ¿Puede envenenar?
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
        long poderAtt = this.poderAtaque;
        long poderEva = victim.poderEvasion;
        double probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtt - poderEva) * 0.4)));
        return (Util.Azar(1, 100) <= probExito);
    }

    private void npcDañoNpc(Npc victima) {
        int daño = Util.Azar(this.stats.MinHIT, this.stats.MaxHIT);
        victima.stats.removeHP(daño);
        if (victima.stats.MinHP < 1) {
            this.movement = this.oldMovement;
            if (this.attackedBy.length() > 0) {
                this.flags.set(FLAG_HOSTIL, this.flags.get(FLAG_OLD_HOSTILE));
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
        this.flags.set(FLAG_PUEDE_ATACAR, false);
        victim.targetNpc = this;
        victim.movement = MOV_NPC_ATACA_NPC;

        if (this.snd1 > 0) {
        	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(this.snd1, pos().x, pos().y));
		}
        if (npcImpactoNpc(victim)) {
            if (victim.snd2 > 0) {
            	mapa.sendToArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(victim.snd2, victim.pos().x, victim.pos().y));
			} else {
				mapa.sendToArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SOUND_IMPACTO2, victim.pos().x, victim.pos().y));
			}
            if (this.petUserOwner != null) {
            	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(SOUND_IMPACTO, pos().x, pos().y));
			} else {
				mapa.sendToArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SOUND_IMPACTO, victim.pos().x, victim.pos().y));
			}
            npcDañoNpc(victim);
        } else {
            if (this.petUserOwner != null) {
            	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(SOUND_SWING, pos().x, pos().y));
			} else {
				mapa.sendToArea(victim.pos().x, victim.pos().y, new PlayWaveResponse(SOUND_SWING, victim.pos().x, victim.pos().y));
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
        String section = "NPC" + this.npcNumber;
        ini.setValue(section, "NpcType", this.npcType.value());
        if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL) {
            ini.setValue(section, "GuardiaPersigue", this.guardiaPersigue);
        }
        ini.setValue(section, "Name", this.name);
        ini.setValue(section, "Desc", this.description);
        ini.setValue(section, "Head", this.infoChar.head);
        ini.setValue(section, "Body", this.infoChar.body);
        ini.setValue(section, "Heading", this.infoChar.heading);
        ini.setValue(section, "Movement", this.movement);
        ini.setValue(section, "TipoItems", this.tipoItems);
        ini.setValue(section, "GiveEXP", this.giveEXP);
        ini.setValue(section, "GiveGLD", this.giveGLD);
        ini.setValue(section, "Inflacion", this.inflation);
        ini.setValue(section, "Attackable", this.flags.get(FLAG_ATACABLE));
        ini.setValue(section, "Comercia", this.flags.get(FLAG_COMERCIA));
        ini.setValue(section, "Hostil", this.flags.get(FLAG_HOSTIL));
        ini.setValue(section, "InvReSpawn", this.flags.get(FLAG_INV_RESPAWN));
        // Stats
        ini.setValue(section, "Alineacion", this.stats.Alineacion);
        ini.setValue(section, "DEF", this.stats.Def);
        ini.setValue(section, "MaxHit", this.stats.MaxHIT);
        ini.setValue(section, "MaxHp", this.stats.MaxHP);
        ini.setValue(section, "MinHit", this.stats.MinHIT);
        ini.setValue(section, "MinHp", this.stats.MinHP);
        // Flags
        ini.setValue(section, "ReSpawn", !this.flags.get(FLAG_RESPAWN));
        ini.setValue(section, "BackUp", this.flags.get(FLAG_BACKUP));
        ini.setValue(section, "Domable", this.domable);
        // Inventario
        ini.setValue(section, "NroItems", this.npcInv.size());
        for (int i = 1; i <= this.npcInv.size(); i++) {
            ini.setValue(section, "Obj" + i, this.npcInv.getObjeto(i).objid + "-" + this.npcInv.getObjeto(i).cant);
        }
    }


    /** Cargar un NPC desde un ini. */
    protected void leerNpc(IniFile ini, int npc_ind) {
        String section = "NPC" + npc_ind;

        this.name = ini.getString(section, "Name");
        this.description = ini.getString(section, "Desc");

        this.movement = ini.getShort(section, "Movement");
        this.oldMovement = this.movement;
        this.flags.set(FLAG_AGUA_VALIDA, ini.getInt(section, "AguaValida") == 1);
        this.flags.set(FLAG_TIERRA_INVALIDA, ini.getInt(section, "TierraInvalida") == 1);
        this.flags.set(FLAG_FACCION, ini.getInt(section, "Faccion") == 1);

        this.npcType = NpcType.value(ini.getShort(section, "NpcType"));

        if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL) {
            this.guardiaPersigue = ini.getShort(section, "GuardiaPersigue");
        }
        this.isQuest = (ini.getShort(section, "DeQuest") == 1);

        this.infoChar.body   = ini.getShort(section, "Body");
        this.infoChar.head   = ini.getShort(section, "Head");
        this.infoChar.heading      = (byte)ini.getShort(section, "Heading");

        this.flags.set(FLAG_ENVENENA, ini.getInt(section, "Veneno") == 1);
        this.flags.set(FLAG_ATACABLE, ini.getInt(section, "Attackable") == 1);
        this.flags.set(FLAG_COMERCIA, ini.getInt(section, "Comercia") == 1);
        this.flags.set(FLAG_HOSTIL,   ini.getInt(section, "Hostile") == 1);
        this.flags.set(FLAG_INV_RESPAWN, ini.getInt(section, "InvReSpawn") == 1);
        this.flags.set(FLAG_OLD_HOSTILE, this.flags.get(FLAG_HOSTIL));

        this.giveEXP   = ini.getInt(section, "GiveEXP");
        //m_expDada   = m_giveEXP;
        this.expCount  = this.giveEXP / 2;

        this.domable = ini.getShort(section, "Domable");
        this.flags.set(FLAG_RESPAWN, ini.getInt(section, "ReSpawn") != 1);

        this.giveGLD         = ini.getInt(section, "GiveGLD");
        this.poderAtaque	= ini.getInt(section, "PoderAtaque");
        this.poderEvasion	= ini.getInt(section, "PoderEvasion");

        this.stats.MaxHP	= ini.getInt(section, "MaxHP");
        this.stats.MinHP	= ini.getInt(section, "MinHP");
        this.stats.MaxHIT	= ini.getInt(section, "MaxHIT");
        this.stats.MinHIT	= ini.getInt(section, "MinHIT");

        this.stats.Alineacion    = ini.getShort(section, "Alineacion");
        this.stats.Def           = ini.getShort(section, "DEF");
        this.stats.ImpactRate    = ini.getShort(section, "ImpactRate");

        this.inflation   = ini.getInt(section, "Inflacion");

        this.flags.set(FLAG_BACKUP, ini.getInt(section, "Backup") == 1);
        this.flags.set(FLAG_RESPAWN_ORIG_POS, ini.getInt(section, "OrigPos") == 1);
        this.flags.set(FLAG_AFECTA_PARALISIS, ini.getInt(section, "AfectaParalisis") == 1);
        this.flags.set(FLAG_GOLPE_EXACTO, ini.getInt(section, "GolpeExacto") == 1);

        this.snd1  = (byte) ini.getShort(section, "Snd1");
        this.snd2  = (byte) ini.getShort(section, "Snd2");
        this.snd3  = (byte) ini.getShort(section, "Snd3");
        this.snd4  = (byte) ini.getShort(section, "Snd4");

        //Spells
        this.spellsCount = (byte) ini.getInt(section, "LanzaSpells");
        this.flags.set(FLAG_LANZA_SPELLS, this.spellsCount > 0);
        if (this.spellsCount > 0) {
            for (int k = 0; k < (this.spellsCount); k++) {
                String spellName = "Sp" + (k+1);
                this.spells[k] = ini.getShort(section, spellName);
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
        this.tipoItems = ini.getShort(section, "TipoItems");
    }

    public short getHeading() {
    	return this.infoChar.heading;
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
		this.targetNpc = targetNpc;
		this.movement = Npc.MOV_NPC_ATACA_NPC;
    }


	protected ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);
	}
}
