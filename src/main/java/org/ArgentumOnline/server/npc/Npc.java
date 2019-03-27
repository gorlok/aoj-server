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
import static org.ArgentumOnline.server.npc.NpcFlags.FLAG_INMOVILIZADO;
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
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.aStar.Node;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Heading;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.protocol.CharacterCreateResponse;
import org.ArgentumOnline.server.protocol.CharacterRemoveResponse;
import org.ArgentumOnline.server.protocol.ChatOverHeadResponse;
import org.ArgentumOnline.server.protocol.CreateFXResponse;
import org.ArgentumOnline.server.protocol.NPCSwingResponse;
import org.ArgentumOnline.server.protocol.ParalizeOKResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.user.Party;
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
	
	// Damos a los NPCs el mismo rango de visión que un PJ
	final static byte RANGO_VISION_X = 8;
	final static byte RANGO_VISION_Y = 6;

	public enum AiType {
		NONE,
		
		/* 1*/ ESTATICO,
		/* 2*/ MUEVE_AL_AZAR,
		/* 3*/ NPC_MALO_ATACA_USUARIOS_BUENOS,
		/* 4*/ NPC_DEFENSA,
		/* 5*/ GUARDIAS_ATACAN_CRIMINALES,
		/* 6*/ NPC_OBJETO,
		/* 7*/ UNUSED,
		/* 8*/ SIGUE_AMO,
		/* 9*/ NPC_ATACA_NPC,
		/*10*/ NPC_PATHFINDING,
		/*11*/ GUARDIAS_ATACAN_CIUDADANOS; // DEPRECATED? Usado por [NPC102] "Guardia Armada WACHO"
		
		private static AiType[] VALUES = AiType.values();
		
		public static AiType value(int index) {
			return VALUES[index];
		}
	}
    
    /* FIXME
'Pretorianos
    SacerdotePretorianoAi = 20
    GuerreroPretorianoAi = 21
    MagoPretorianoAi = 22
    CazadorPretorianoAi = 23
    ReyPretoriano = 24
End Enum
     */
	
    public final static short GUARDIAS_PERSIGUEN_CIUDADANOS = 1;
    public final static short GUARDIAS_PERSIGUEN_CRIMINALES = 0;

    protected MapPos origPos  = MapPos.empty();
    
    long lastMove = 0;

    public String name = "";
    public String description = "";

    NpcType npcType = NpcType.NPCTYPE_COMUN;
    int   npcNumber = 0;
    short level     = 0;

    private NpcFlags flags = new NpcFlags();

    AiType movement    = AiType.NONE;
    AiType oldMovement = AiType.NONE;
    
    public String  attackedBy = "";
    public String  attackedFirstBy = ""; // FIXME

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

        MapPos tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.isWaterValid(), npc.isLandInvalid(), bajoTecho);

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
			npc.flags().set(FLAG_RESPAWN, false);
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
            tmp = mapa.closestLegalPosNpc(orig.x, orig.y, npc.isWaterValid(), npc.isLandInvalid(), false);
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
        if (canReSpawn()) {
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
    	return this.movement == AiType.ESTATICO;
    }

    public Npc makeStatic() {
    	this.movement = AiType.ESTATICO;
    	return this;
    }

    public boolean isQuest() {
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

    public boolean isMagical() {
        return this.flags().get(FLAG_LANZA_SPELLS);
    }

    public boolean getBackup() {
        return this.flags().get(FLAG_BACKUP);
    }

    /* volverMaldito */
    public void turnDamned() {
        this.flags().set(FLAG_MALDICION, true);
    }

    /* quitarMaldito */
    public void removeDamned() {
        this.flags().set(FLAG_MALDICION, false);
    }

    /* volverBendito */
    public void turnBlessed() {
        this.flags().set(FLAG_BENDICION, true);
    }

    public boolean isHostile() {
        return this.flags().get(FLAG_HOSTIL);
    }

    public boolean isInvisible() {
        return this.flags().get(FLAG_INVISIBLE);
    }

    public boolean respawnOrigPos() {
        return this.flags().get(FLAG_RESPAWN_ORIG_POS);
    }

    public void makeInvisible() {
        this.flags().set(FLAG_INVISIBLE, true);
    }

    public void makeVisible() {
        this.flags().set(FLAG_INVISIBLE, false);
    }

    public boolean canAttack() {
        return this.flags().get(FLAG_PUEDE_ATACAR);
    }

    public void setCanAttack(boolean estado) {
        this.flags().set(FLAG_PUEDE_ATACAR, estado);
    }

    public boolean isFaction() {
        return this.flags().get(FLAG_FACCION);
    }

    private boolean puedeEnvenenar() {
        return this.flags().get(FLAG_ENVENENA);
    }

    public void envenenar() {
        this.flags().set(FLAG_ENVENENA, true);
    }

    public void curarVeneno() {
        this.flags().set(FLAG_ENVENENA, false);
    }

    public boolean canReSpawn() {
        return this.flags().get(FLAG_RESPAWN);
    }

    protected boolean invReSpawn() {
        return this.flags().get(FLAG_INV_RESPAWN);
    }

    private boolean isFollowing() {
        return this.flags().get(FLAG_FOLLOW);
    }

    public boolean afectaParalisis() {
        return this.flags().get(FLAG_AFECTA_PARALISIS);
    }

    public void paralizar() {
        this.flags().set(FLAG_PARALIZADO, true);
        this.counters.Paralisis = IntervaloParalizado;
    }

    public void desparalizar() {
        this.flags().set(FLAG_PARALIZADO, false);
        this.counters.Paralisis = 0;
    }

    public void inmovilizar() {
        this.flags().set(FLAG_INMOVILIZADO, true);
    }

    public void desinmovilizar() {
        this.flags().set(FLAG_INMOVILIZADO, false);
    }

    @Override
	public String toString() {
        return "npc(id=" + this.getId() + ",x=" + pos().x + ",y=" + pos().y + " name=" + this.name + ")";
    }

    public String getName() {
    	return this.name;
    }

    public MapPos getOrig() {
        return this.origPos;
    }

    public boolean isNpcActive() {
        return this.flags().get(FLAG_NPC_ACTIVE);
    }

    public int getPoderAtaque() {
        return this.poderAtaque;
    }

    public int getPoderEvasion() {
        return this.poderEvasion;
    }

    public boolean isAttackable() {
        return this.flags().get(FLAG_ATACABLE);
    }

    public boolean isWaterValid() {
        return this.flags().get(FLAG_AGUA_VALIDA);
    }

    public boolean isLandInvalid() {
        return this.flags().get(FLAG_TIERRA_INVALIDA);
    }

    public String getDesc() {
        return this.description;
    }

    public int getNumber() {
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

    public boolean isParalized() {
        return this.flags().get(FLAG_PARALIZADO);
    }

    public boolean isInmovilized() {
        return this.flags().get(FLAG_INMOVILIZADO);
    }

    public boolean isTrade() {
        return this.flags().get(FLAG_COMERCIA);
    }

    public boolean isBankCashier() {
    	return npcType() == NpcType.NPCTYPE_BANQUERO;
    }

    public boolean isPriest() {
    	return npcType() == NpcType.NPCTYPE_SACERDOTE;
    }

    public boolean isPriestNewbies() {
    	return npcType() == NpcType.NPCTYPE_SACERDOTE_NEWBIES;
    }

    public boolean isNoble() {
    	return npcType() == NpcType.NPCTYPE_NOBLE;
    }

    public void activate() {
        this.flags().set(FLAG_NPC_ACTIVE, true);
    }

    public void attackedByUserName(String nick) {
        this.attackedBy = nick;
    }

    public boolean isAttackedByUser() {
        return this.attackedBy.length() > 0;
    }

    public void setGiveGLD(int val) {
        this.giveGLD = val;
    }

    public int targetUser() {
        return this.targetUser;
    }

    public Npc targetNpc() {
    	return this.targetNpc;
    }

    public void targetUser(int target) {
        this.targetUser = target;
    }

    public NpcType npcType() {
        return this.npcType;
    }

    public NpcStats stats() {
        return this.stats;
    }

    public NpcCounters counters() {
        return this.counters;
    }

    public void petNpcOwner(Npc petOwner) {
        this.petNpcOwnerId = petOwner.getId();
    }

    public short domable() {
        return this.domable;
    }

    public CharacterCreateResponse characterCreate() {
    	return new CharacterCreateResponse(
			getId(),
			this.infoChar.body(),
			this.infoChar.head(),

			this.infoChar.heading().value(),
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
        loadNpc(ini, npc_ind);
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
    	int exp = 0;
    	// Chekeamos que las variables sean validas para las operaciones
    	if (daño < 0) {
    		daño = 0;
    	}
    	if (stats().MaxHP <= 0) {
    		return;
    	}
    	if (daño > stats().MinHP) {
    		daño = stats().MinHP;
    	}
    	
    	// La experiencia a dar es la porcion de vida quitada * toda la experiencia
    	exp = (int) (daño * (this.giveEXP / stats().MaxHP));
    	if (exp <= 0) {
    		return;
    	}
    	
    	// Vamos contando cuanta experiencia sacamos, porque se da toda la que no se dio al user que mata al NPC
    	// Esto es porque cuando un elemental ataca, no se da exp, y tambien porque la cuenta que hicimos antes
    	// podria dar un numero fraccionario. Esas fracciones se acumulan hasta formar enteros ;P
    	if (exp > this.expCount) {
			exp = this.expCount;
			this.expCount = 0;
    	} else {
			this.expCount = this.expCount - exp;
    	}
    	
    	exp = exp * 1000; // FIXME EXPERIENCIA FACIL
    	// Le damos la exp al user
        if (exp > 0) {
            if (player.partyIndex > 0) {
            	Party.obtenerExito(player, exp, pos());
            } else {
            	player.stats().addExp(exp);
                player.sendMessage("Has ganado " + exp + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            }
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
                player.stats().addExp(this.expCount * 1000); // FIXME experiencia FACIL
                player.sendMessage("Has ganado " + this.expCount + " puntos de experiencia.", FONTTYPE_FIGHT);
            } else {
                player.sendMessage("No has ganado experiencia al matar la criatura.", FONTTYPE_FIGHT);
            }
            player.stats().incNPCsMuertos();
            player.quest().checkNpcEnemigo(player, this);

            if (this.stats.alineacion == 0) {
            	// TODO: ¿No debería compararse con NpcType==2? Hay otros guardias aparte del npcNumber=6
                if (this.npcNumber == GUARDIAS) {
                    player.turnCriminal();
                }
                if (!player.flags().isGod()) {
                    player.reputation().incAsesino(vlAsesino);
                }
            } else if (this.stats.alineacion == 1) {
                player.reputation().incPlebe(vlCazador);
            } else if (this.stats.alineacion == 2) {
                player.reputation().incNoble(vlAsesino / 2);
            } else if (this.stats.alineacion == 4) {
                player.reputation().incPlebe(vlCazador);
            }
            // Controla el nivel del usuario
            player.checkUserLevel();

            //Agush: updateamos de ser necesario ;-)
            if (player.isCriminal() != eraCrimi && eraCrimi == true) {
            	player.refreshCharStatus();
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
        if (this.npcInv().size() > 0) {
            for (int i = 1; i <= this.npcInv().size(); i++) {
                if (this.npcInv().getObjeto(i) != null && this.npcInv().getObjeto(i).objid > 0) {
                    Map m = this.server.getMap(pos().map);
                    m.dropItemOnFloor(pos().x, pos().y, 
                    		new InventoryObject(
                    				this.npcInv().getObjeto(i).objid,
                    				this.npcInv().getObjeto(i).cant));
                }
            }
        }
    }

    public void quitarNPC() {
    	if (DEBUG) {
    		log.debug("NPC BORRADO");
    	}

        this.flags().set(FLAG_NPC_ACTIVE, false);
        Map map = this.server.getMap(pos().map);
        if (map == null) {
        	return;
        }

        if (map != null && pos().isValid()) {
            map.exitNpc(this);
        }

        map.sendToArea(pos().x, pos().y, new CharacterRemoveResponse(this.getId()));

        // Nos aseguramos de que el inventario sea removido...
        // asi los lobos no volveran a tirar armaduras ;))
        this.npcInv().clear();

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
            this.flags().set(FLAG_PARALIZADO, false);
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
            if (mapa.isLegalPos(newPos, false, true)) {
                if (!isWaterValid() && mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (isLandInvalid() && !mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + isWaterValid());
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
            if (mapa.isLegalPosNPC(newPos, isWaterValid())) {
                if (!isWaterValid() && mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (isLandInvalid() && !mapa.isWater(newPos.x, newPos.y)) {
					return;
				}
                if (mapa.getNPC(newPos.x, newPos.y) != null) {
                    log.debug("m_flags.AguaValida=" + isWaterValid());
                    log.debug("OJO, ya hay otro NPC!!! " + this + " " +
                    newPos.x + " " + newPos.y + " encontro=" + mapa.getNPC(newPos.x, newPos.y));
                    return;
                }
                // Update map and user pos
                this.infoChar.heading(dir);
                mapa.moverNpc(this, newPos.x, newPos.y);
                this.setPos(newPos);
            } else {
                if (this.movement == AiType.NPC_PATHFINDING) {
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
            player.poison();
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
            m.dropItemOnFloor(pos().x, pos().y, new InventoryObject(OBJ_ORO, this.giveGLD));
        }
    }

    /** Seguir a un usuario / Follow user */
    public void followUser(String nombreUsuario) {
        // doFollow
        if (isFollowing()) {
            this.attackedBy = "";
            this.flags().set(FLAG_FOLLOW, false);
            this.movement  = this.oldMovement;
            this.flags().set(FLAG_HOSTIL, this.flags().get(FLAG_OLD_HOSTILE));
        } else {
            this.attackedBy = nombreUsuario;
            this.flags().set(FLAG_FOLLOW, true);
            this.movement = AiType.NPC_DEFENSA; // follow
            this.flags().set(FLAG_HOSTIL, false);
        }
    }

    /** Seguir al amo / Follow master */
    public void followMaster() {
        this.flags().set(FLAG_FOLLOW, true);
        this.movement  = AiType.SIGUE_AMO; // follow npc's master.
        this.flags().set(FLAG_HOSTIL, false);
        this.targetUser = 0;
        this.targetNpc  = null;
    }

    public Npc expresar() {
        if (this.petUserOwner == null) {
			return this;
		}
        if (this.expressionsCount > 0) {
            int azar = Util.Azar(0, this.expressionsCount - 1);
            Map mapa = this.server.getMap(pos().map);
            if (mapa != null) {
                hablarAlArea(Color.COLOR_BLANCO, this.expressions[azar]);
            }
        }
        return this;
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
        this.movement = AiType.NPC_DEFENSA;
        this.flags().set(FLAG_HOSTIL, true);
    }

    public void cambiarDir(Heading dir) {
        // ChangeNPCChar
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
            this.infoChar.heading(dir);
            mapa.sendToArea(pos().x, pos().y, characterCreate());
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
                    if (player.isAlive() && player.isAllowingChase()) {
                        // ¿ES CRIMINAL?
                    	if (npcType() != NpcType.NPCTYPE_GUARDIAS_CAOS) {
                    		if (player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(player.getNick()) && !this.flags().get(FLAG_FOLLOW)) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		}
                    	} else {
                    		if (!player.isCriminal()) {
                    			cambiarDir(dir);
                    			npcAtacaUser(player);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(player.getNick()) && !this.flags().get(FLAG_FOLLOW)) {
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
                    if (player.isAlive() && player.isAllowingChase()) {
                        if (isMagical()) {
                            npcCastSpell(player);
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
                    if (player.isAlive() && player.isAllowingChase() && this.attackedBy.equalsIgnoreCase(player.getNick())) {
                        if (isMagical()) {
                            npcCastSpell(player);
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
                            if (player.isAlive() && !player.isInvisible() && player.isAllowingChase()) {
                                if (isMagical()) {
                                   npcCastSpell(player);
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
                        if (!player.flags().isGM() && this.attackedBy.equalsIgnoreCase(player.getNick())) {
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
                                if (isMagical()) {
                                    npcCastSpell(player);
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
        this.flags().set(FLAG_HOSTIL, this.flags().get(FLAG_OLD_HOSTILE));
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

                        if (player.isCriminal() && player.isAlive() && !player.isInvisible() && player.isAllowingChase()) {
                            if (isMagical()) {
                                npcCastSpell(player);
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
                        if (!player.isCriminal() && player.isAlive() && !player.isInvisible() && !player.flags().isGM()) {
                            if (isMagical()) {
                                npcCastSpell(player);
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
    	// AiNpcAtacaNpc
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}

        if (isInmovilized()) {
        	byte signNS = 0;
        	byte signEO = 0;
        	switch (heading()) {
	        case NORTH:
	            signNS = -1;
	            signEO = 0;
	            break;
	        case EAST:
	            signNS = 0;
	            signEO = 1;
	            break;
	        case SOUTH:
	            signNS = 1;
	            signEO = 0;
	            break;
	        case WEST:
	        	signNS = 0;
	            signEO = -1;
	            break;
	        default:
	        	break;
			}
	        for (byte y = pos().y; y <= pos().y + signNS * RANGO_VISION_Y; y += (signNS == 0 ? 1 : signNS)) {
	        	for (byte x = pos().x; x <= pos().x + signEO * RANGO_VISION_X; x += (signEO == 0 ? 1 : signEO)) {
	                MapPos pos = MapPos.mxy(pos().map, x, y);
	                if (pos.isValid()) {
	                    Npc npc = mapa.getNPC(x, y);
	                    if (npc != null) {
	                        if (this.targetNpc == npc) {
	                        	if (getNumber() == ELEMENTAL_FUEGO) {
	                        		npcLanzaUnSpellSobreNpc(npc);
	                        		if (npc.npcType() == NpcType.NPCTYPE_DRAGON) {
	                        			// contraataque
	                        			npc.canAttack();
	                        			npc.npcLanzaUnSpellSobreNpc(this);
	                        		}
	                        	} else {
                                    // aca verificamosss la distancia de ataque
                                    if (pos().distance(npc.pos()) <= 1) {
                                        npcAtacaNpc(npc);
                                    }
	                        	}
	                        	return;
	                        }
	                    }
	                }
	            }
	        }
        } else {
        	// not inmovilized
        	for (byte y = (byte) (pos().y-RANGO_VISION_Y); y <= (byte) (pos().y+RANGO_VISION_Y); y++) {
        		for (byte x = (byte) (pos().x-RANGO_VISION_X); x <= (byte) (pos().x+RANGO_VISION_X); x++) {
        			MapPos pos = MapPos.mxy(pos().map, x, y);
        			if (pos.isValid()) {
        				Npc npc = mapa.getNPC(x, y);
        				if (npc != null) {
        					if (this.targetNpc == npc) {
	                        	if (getNumber() == ELEMENTAL_FUEGO) {
	                        		npcLanzaUnSpellSobreNpc(npc);
	                        		if (npc.npcType() == NpcType.NPCTYPE_DRAGON) {
	                        			// contraataque
	                        			npc.canAttack();
	                        			npc.npcLanzaUnSpellSobreNpc(this);
	                        		}
	                        	} else {
                                    // aca verificamosss la distancia de ataque
                                    if (pos().distance(npc.pos()) <= 1) {
                                        npcAtacaNpc(npc);
                                    }
	                        	}
        						
	                        	if (isInmovilized()) {
	                        		return;
	                        	}
	                        	if (targetNpc() == null) {
	                        		return;
	                        	}
        						Heading heading = pos().findDirection(npc.pos());
        						mover(heading);
        						return;
        					}
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
    }
    
    private void aiNpcObjeto() {
    	//AiNpcObjeto
        Map map = this.server.getMap(pos().map);
        if (map == null) {
			return;
		}
    	for (byte y = (byte) (pos().y-RANGO_VISION_Y); y <= (byte) (pos().y+RANGO_VISION_Y); y++) {
    		for (byte x = (byte) (pos().x-RANGO_VISION_X); x <= (byte) (pos().x+RANGO_VISION_X); x++) {
    			if (pos.isValid()) {
    				if (map.hasPlayer(x, y)) {
    					Player player = map.getPlayer(x, y);
    					if (player.isAlive() && !player.isInvisible() && !player.isHidden() && player.isAllowingChase()) {
    						// No quiero que ataque siempre al primero
    						if (Util.Azar(1,  3) < 3) {
    							if (flags().get(FLAG_LANZA_SPELLS)) {
    								npcCastSpell(player);
    								return;
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }

    private void npcLanzaUnSpellSobreNpc(Npc targetNpc) {
    	if (isMagical()) {
    		int spellIndex = Util.Azar(0, spellsCount-1);
    		npcLanzaSpellSobreNpc(targetNpc, spells[spellIndex]);
    	}
    	
    }
    

    private void npcLanzaSpellSobreNpc(Npc targetNpc, short spellId) {
    	// NpcLanzaSpellSobreNpc
    	// solo hechizos ofensivos!
    	if (!canAttack()) {
    		return;
    	}
    	setCanAttack(false);
    	
    	Spell spell = server.getSpell(spellId);
    	if (spell.SubeHP == 2) {
	        int daño = Util.Azar(spell.MinHP, spell.MaxHP);
	        
            sendPlayWave(spell.WAV);
            sendCreateFX(spell.FXgrh, spell.loops);
            
            targetNpc.stats().removeHP(daño);
	        // Muere?
            if (targetNpc.stats().MinHP < 1) {
            	targetNpc.muereNpc(getPetUserOwner());
            }
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
            // ¿Es un guardia?
            if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL || this.npcType == NpcType.NPCTYPE_GUARDIAS_CAOS) {
                guardiasAI();
            } else if (isHostile() && this.stats.alineacion != 0) {
                hostilMalvadoAI();
            } else if (isHostile() && this.stats.alineacion == 0) {
                hostilBuenoAI();
            }
        }
        
        switch (this.movement) {
    	case NONE: 
    	case UNUSED:
    	case ESTATICO:
    		// do nothing.
    		break;
    		
        case MUEVE_AL_AZAR:
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
            
        case NPC_MALO_ATACA_USUARIOS_BUENOS:
            // Va hacia el usuario cercano
            irUsuarioCercano();
            break;
            
        case NPC_DEFENSA:
            // Va hacia el usuario que lo ataco(FOLLOW)
            seguirAgresor();
            break;
            
        case GUARDIAS_ATACAN_CRIMINALES:
            // Persigue criminales
            persigueCriminal();
            break;
            
        case GUARDIAS_ATACAN_CIUDADANOS:
            // Persigue criminales
            persigueCiudadano();
            break;
            
        case SIGUE_AMO:
            seguirAmo();
            if (Util.Azar(1, 12) == 3) {
                moverAlAzar();
            }
            break;
            
        case NPC_ATACA_NPC:
            aiNpcAtacaNpc();
            break;
            
        case NPC_OBJETO:
        	aiNpcObjeto();
        	break;
            
        case NPC_PATHFINDING:
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

    private void npcCastSpell(Player player) {
    	// NpcLanzaUnSpell
    	
    	if (!canAttack()) {
    		return;
    	}
        if (player.isInvisible() || player.isHidden()) {
			return;
		}
        // Si no se peude usar magia en el mapa, no le deja hacerlo.
        Map map = this.server.getMap(pos().map);
        if (map.MagiaSinEfecto) {
        	return;
        }        
        this.flags().set(FLAG_PUEDE_ATACAR, false);
        int daño = 0;
        
        short spellIndex = this.spells[Util.Azar(0, this.spellsCount-1)];
        Spell spell = this.server.getSpell(spellIndex);
        if (spell.SubeHP == 1) {
            daño = Util.Azar(spell.MinHP, spell.MaxHP);
            player.sendWave(spell.WAV);
            player.sendCreateFX(spell.FXgrh, spell.loops);
            player.stats().addHP(daño);
            player.sendMessage(this.name + " te ha dado " + daño + " puntos de vida.", FONTTYPE_FIGHT);
            player.sendUpdateUserStats();
            
        }

        if (spell.SubeHP == 2) {
            if (player.flags().isGM()) {
    			return;
    		}
            daño = Util.Azar(spell.MinHP, spell.MaxHP);
            
            // Si el usuario tiene un sombrero mágico de defensa, se reduce el daño
            if (player.userInv().tieneCascoEquipado()) {
            	daño = daño - Util.Azar(
            			player.userInv().getCasco().DefensaMagicaMin,
            			player.userInv().getCasco().DefensaMagicaMax);
            }
            
            // Si el usuario tiene un anillo mágico de defensa, se reduce el daño
            if (player.userInv().tieneAnilloEquipado()) {
            	daño = daño - Util.Azar(
            			player.userInv().getAnillo().DefensaMagicaMin,
            			player.userInv().getAnillo().DefensaMagicaMax);
            }
            
            if (daño < 0) {
            	daño = 0;
            }
            
            player.sendWave(spell.WAV);
            player.sendCreateFX(spell.FXgrh, spell.loops);
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
        
        if (spell.isParaliza() || spell.isInmoviliza()) {
    		if (!player.flags().Paralizado) {
    			player.sendWave(spell.WAV);
    			player.sendCreateFX(spell.FXgrh, spell.loops);
    			
    			if (player.userInv().tieneAnilloEquipado() && player.userInv().getAnillo().ObjIndex == SUPERANILLO) {
    	            player.sendMessage("Tu anillo rechaza los efectos del hechizo.", FONTTYPE_FIGHT);
    	            return;
    			}
    			
    			if (spell.isInmoviliza()) {
    				player.flags().Inmovilizado = true;
    			}
    			
    			player.flags().Paralizado = true;
    			player.counters().Paralisis = IntervaloParalizado;
    			player.sendPacket(new ParalizeOKResponse());
    		}
        }
        
        if (spell.isEstupidez()) { // turbación
        	if (!player.isDumb()) {
    			player.sendWave(spell.WAV);
    			player.sendCreateFX(spell.FXgrh, spell.loops);

    			if (player.userInv().tieneAnilloEquipado() && player.userInv().getAnillo().ObjIndex == SUPERANILLO) {
    	            player.sendMessage("Tu anillo rechaza los efectos del hechizo.", FONTTYPE_FIGHT);
    	            return;
    			}
    			player.makeDumb();
        	}
        }
    }

    private void npcAtacaUser(Player player) {
    	if (player.flags().AdminInvisible || !player.isAllowingChase()) {
    		return;
    	}
    	
        Map mapa = this.server.getMap(pos().map);
        // El npc puede atacar ???
        if (!canAttack()) {
			return;
		}
        if (player.flags().isGM()) {
			return;
		}
        player.getUserPets().petsAttackNpc(this);
		targetUser(player.getId());
        if (player.flags().AtacadoPorNpc == 0 && player.flags().AtacadoPorUser == 0) {
			player.flags().AtacadoPorNpc = this.getId();
		}
        this.flags().set(FLAG_PUEDE_ATACAR, false);
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
        player.riseSkill(Skill.SKILL_Tacticas);
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
                this.flags().set(FLAG_HOSTIL, this.flags().get(FLAG_OLD_HOSTILE));
            }
            followMaster();
            victima.muereNpc(this.petUserOwner);
        }
    }

    private void npcAtacaNpc(Npc victim) {
        Map mapa = this.server.getMap(pos().map);

        // El npc puede atacar ???
        if (!canAttack()) {
			return;
		}
        this.flags().set(FLAG_PUEDE_ATACAR, false);
        victim.targetNpc = this;
        victim.movement = AiType.NPC_ATACA_NPC;

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
                        if (player.isAlive() && !player.isInvisible() && !player.isHidden() && player.isAllowingChase()) {
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

    public void backupNpc(IniFile ini) {
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
        ini.setValue(section, "Heading", this.infoChar.heading.value());
        ini.setValue(section, "Movement", this.movement.ordinal());
        ini.setValue(section, "TipoItems", this.tipoItems);
        ini.setValue(section, "GiveEXP", this.giveEXP);
        ini.setValue(section, "GiveGLD", this.giveGLD);
        ini.setValue(section, "Inflacion", this.inflation);
        // Stats
        ini.setValue(section, "Alineacion", this.stats.alineacion);
        ini.setValue(section, "DEF", this.stats.defensa);
        ini.setValue(section, "DEFm", this.stats.defensaMagica);
        ini.setValue(section, "MaxHit", this.stats.MaxHIT);
        ini.setValue(section, "MaxHp", this.stats.MaxHP);
        ini.setValue(section, "MinHit", this.stats.MinHIT);
        ini.setValue(section, "MinHp", this.stats.MinHP);
        // Flags
        ini.setValue(section, "AguaValida", this.flags().get(FLAG_AGUA_VALIDA));
        ini.setValue(section, "TierraInvalida", this.flags().get(FLAG_TIERRA_INVALIDA));
        ini.setValue(section, "Faccion", this.flags().get(FLAG_FACCION));
        ini.setValue(section, "Veneno", this.flags().get(FLAG_ENVENENA));
        ini.setValue(section, "Attackable", this.flags().get(FLAG_ATACABLE));
        ini.setValue(section, "Comercia", this.flags().get(FLAG_COMERCIA));
        ini.setValue(section, "Hostile", this.flags().get(FLAG_HOSTIL));
        ini.setValue(section, "InvReSpawn", this.flags().get(FLAG_INV_RESPAWN));
        ini.setValue(section, "ReSpawn", this.flags().get(FLAG_RESPAWN));
        ini.setValue(section, "Backup", this.flags().get(FLAG_BACKUP));
        ini.setValue(section, "OrigPos", this.flags().get(FLAG_RESPAWN_ORIG_POS));
        ini.setValue(section, "AfectaParalisis", this.flags().get(FLAG_AFECTA_PARALISIS));
        ini.setValue(section, "GolpeExacto", this.flags().get(FLAG_GOLPE_EXACTO));
        
        ini.setValue(section, "Domable", this.domable);
        ini.setValue(section, "PoderAtaque", this.poderAtaque);
        ini.setValue(section, "PoderEvasion", this.poderEvasion);

        ini.setValue(section, "Snd1", this.snd1);
        ini.setValue(section, "Snd2", this.snd2);
        ini.setValue(section, "Snd3", this.snd3);
        ini.setValue(section, "Snd4", this.snd4);
        
        //Spells
        ini.setValue(section, "LanzaSpells", this.spellsCount); 
        if (this.spellsCount > 0) {
            for (int k = 0; k < (this.spellsCount); k++) {
                ini.setValue(section, "Sp" + (k+1), this.spells[k]);
            }
        }

        ini.setValue(section, "DeQuest", this.isQuest);
    }


    /** Cargar un NPC desde un ini. */
    protected void loadNpc(IniFile ini, int npc_ind) {
        String section = "NPC" + npc_ind;

        this.npcType = NpcType.value(ini.getShort(section, "NpcType"));
        if (this.npcType == NpcType.NPCTYPE_GUARDIAS_REAL) {
        	this.guardiaPersigue = ini.getShort(section, "GuardiaPersigue");
        }
        this.name = ini.getString(section, "Name");
        this.description = ini.getString(section, "Desc");
        this.infoChar.head   = ini.getShort(section, "Head");
        this.infoChar.body   = ini.getShort(section, "Body");
        this.infoChar.heading      = Heading.value(ini.getShort(section, "Heading"));
        this.movement 	 = AiType.value(ini.getShort(section, "Movement"));
        this.oldMovement = this.movement;
        this.tipoItems = ini.getShort(section, "TipoItems"); // Tipo de items con los que comercia
        this.giveEXP   = ini.getInt(section, "GiveEXP");
        this.giveGLD        = ini.getInt(section, "GiveGLD");
        this.expCount  = this.giveEXP / 2;
        this.inflation   = ini.getInt(section, "Inflacion");
        
        // stats
        this.stats.alineacion    = ini.getShort(section, "Alineacion");
        this.stats.defensa       = ini.getShort(section, "DEF");
        this.stats.defensaMagica = ini.getShort(section, "DEFm");
        this.stats.MaxHP	= ini.getInt(section, "MaxHP");
        this.stats.MinHP	= ini.getInt(section, "MinHP");
        this.stats.MaxHIT	= ini.getInt(section, "MaxHIT");
        this.stats.MinHIT	= ini.getInt(section, "MinHIT");
        
        // flags
        this.flags().set(FLAG_AGUA_VALIDA, ini.getInt(section, "AguaValida") == 1);
        this.flags().set(FLAG_TIERRA_INVALIDA, ini.getInt(section, "TierraInvalida") == 1);
        this.flags().set(FLAG_FACCION, ini.getInt(section, "Faccion") == 1);
        this.flags().set(FLAG_ENVENENA, ini.getInt(section, "Veneno") == 1);
        this.flags().set(FLAG_ATACABLE, ini.getInt(section, "Attackable") == 1);
        this.flags().set(FLAG_COMERCIA, ini.getInt(section, "Comercia") == 1);
        this.flags().set(FLAG_HOSTIL,   ini.getInt(section, "Hostile") == 1);
        this.flags().set(FLAG_INV_RESPAWN, ini.getInt(section, "InvReSpawn") == 1);
        this.flags().set(FLAG_OLD_HOSTILE, this.flags().get(FLAG_HOSTIL));
        this.flags().set(FLAG_RESPAWN, ini.getInt(section, "ReSpawn") != 1);
        this.flags().set(FLAG_BACKUP, ini.getInt(section, "Backup") == 1);
        this.flags().set(FLAG_RESPAWN_ORIG_POS, ini.getInt(section, "OrigPos") == 1);
        this.flags().set(FLAG_AFECTA_PARALISIS, ini.getInt(section, "AfectaParalisis") == 1);
        this.flags().set(FLAG_GOLPE_EXACTO, ini.getInt(section, "GolpeExacto") == 1);

        this.domable = ini.getShort(section, "Domable");
        this.poderAtaque	= ini.getInt(section, "PoderAtaque");
        this.poderEvasion	= ini.getInt(section, "PoderEvasion");

        this.snd1  = (byte) ini.getShort(section, "Snd1");
        this.snd2  = (byte) ini.getShort(section, "Snd2");
        this.snd3  = (byte) ini.getShort(section, "Snd3");
        this.snd4  = (byte) ini.getShort(section, "Snd4");

        //Spells
        this.spellsCount = (byte) ini.getInt(section, "LanzaSpells");
        this.flags().set(FLAG_LANZA_SPELLS, this.spellsCount > 0);
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
        
        this.isQuest = (ini.getShort(section, "DeQuest") == 1);
    }

    public Heading heading() {
    	return this.infoChar.heading;
    }

    private String estadoVidaExacta() {
    	if (this.stats().MaxHP <= 0) {
			return "";
		}
    	return "(" + this.stats().MinHP + "/" + this.stats().MaxHP + ")";
    }

    public String healthDescription(Player player) {
    	if (this.stats().MaxHP <= 0) {
			return "";
		}

    	if (player.flags().isGM()) {
			return estadoVidaExacta();
		}
    	
    	if (!player.isAlive()) {
    		return "";
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
		this.movement = AiType.NPC_ATACA_NPC;
    }


	protected ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);
	}

	private NpcFlags flags() {
		return flags;
	}

}
