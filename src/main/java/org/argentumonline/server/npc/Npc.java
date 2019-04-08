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
package org.argentumonline.server.npc;

import static org.argentumonline.server.npc.NpcFlags.FLAG_AFECTA_PARALISIS;
import static org.argentumonline.server.npc.NpcFlags.FLAG_AGUA_VALIDA;
import static org.argentumonline.server.npc.NpcFlags.FLAG_ATACABLE;
import static org.argentumonline.server.npc.NpcFlags.FLAG_BACKUP;
import static org.argentumonline.server.npc.NpcFlags.FLAG_BENDICION;
import static org.argentumonline.server.npc.NpcFlags.FLAG_COMERCIA;
import static org.argentumonline.server.npc.NpcFlags.FLAG_ENVENENA;
import static org.argentumonline.server.npc.NpcFlags.FLAG_FACCION;
import static org.argentumonline.server.npc.NpcFlags.FLAG_FOLLOW;
import static org.argentumonline.server.npc.NpcFlags.FLAG_GOLPE_EXACTO;
import static org.argentumonline.server.npc.NpcFlags.FLAG_HOSTIL;
import static org.argentumonline.server.npc.NpcFlags.FLAG_INMOVILIZADO;
import static org.argentumonline.server.npc.NpcFlags.FLAG_INVISIBLE;
import static org.argentumonline.server.npc.NpcFlags.FLAG_INV_RESPAWN;
import static org.argentumonline.server.npc.NpcFlags.FLAG_LANZA_SPELLS;
import static org.argentumonline.server.npc.NpcFlags.FLAG_MALDICION;
import static org.argentumonline.server.npc.NpcFlags.FLAG_NPC_ACTIVE;
import static org.argentumonline.server.npc.NpcFlags.FLAG_OLD_HOSTILE;
import static org.argentumonline.server.npc.NpcFlags.FLAG_PARALIZADO;
import static org.argentumonline.server.npc.NpcFlags.FLAG_PUEDE_ATACAR;
import static org.argentumonline.server.npc.NpcFlags.FLAG_RESPAWN;
import static org.argentumonline.server.npc.NpcFlags.FLAG_RESPAWN_ORIG_POS;
import static org.argentumonline.server.npc.NpcFlags.FLAG_TIERRA_INVALIDA;
import static org.argentumonline.server.util.FontType.FONTTYPE_FIGHT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.AbstractCharacter;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Skill;
import org.argentumonline.server.inventory.Inventory;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.protocol.CharacterCreateResponse;
import org.argentumonline.server.protocol.CharacterRemoveResponse;
import org.argentumonline.server.protocol.ChatOverHeadResponse;
import org.argentumonline.server.protocol.CreateFXResponse;
import org.argentumonline.server.protocol.NPCSwingResponse;
import org.argentumonline.server.protocol.ParalizeOKResponse;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.user.Party;
import org.argentumonline.server.user.Spell;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.Color;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

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
		
		/* 1*/ STATIC,
		/* 2*/ RANDOM_MOVE,
		/* 3*/ BAD_NPC_ATTACK_GOOD_USER,
		/* 4*/ DEFENSE_NPC,
		/* 5*/ GUARDS_ATTACK_CRIMINALS,
		/* 6*/ NPC_OBJECT,
		/* 7*/ UNUSED,
		/* 8*/ FOLLOW_MASTER,
		/* 9*/ NPC_ATTACK_NPC,
		/*10*/ NPC_PATHFINDING,
		/*11*/ GUARDS_ATTACK_CITIZENS; // DEPRECATED? Only used by [NPC102] "Guardia Armada WACHO"
		
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
	
    public final static short GUARDS_FOLLOW_CITIZENS = 1;
    public final static short GUARDS_FOLLOW_CRIMINALS = 0;

    protected MapPos origPos  = MapPos.empty();
    
    private long lastMove = 0;

    public String name = "";
    public String description = "";

    private NpcType npcType = NpcType.NPCTYPE_COMMON;
    protected int   npcNumber = 0;

    private NpcFlags flags = new NpcFlags();

    private AiType movement    = AiType.NONE;
    private AiType oldMovement = AiType.NONE;
    
    public String  attackedBy = "";
    public String  attackedFirstBy = ""; // FIXME

    private int   targetUser    = 0;
    private Npc   targetNpc = null;
    protected short objType = 0;

    private int attackPower  = 0;
    private int dodgePower   = 0;
    protected int inflation    = 0; // TODO REVISAR
    private int giveEXP      = 0;
    private int giveGLD      = 0;

    private int expCount = 0;

    /** domable */
    public short  tamable = 0;
    
    private boolean isQuest = false;
    
    private short guardiaPersigue = GUARDS_FOLLOW_CRIMINALS; // TODO
    
    private byte snd1 = 0; // Sonido ataque NPC
    private byte snd2 = 0; // Sonido ataque exitoso NPC
    private byte snd3 = 0; // Sonido muere NPC
    private byte snd4 = 0; // ???

    public NpcStats stats = new NpcStats();

    private NpcCounters counters = new NpcCounters();

    private byte  expressionsCount = 0;
    private String expressions[] = new String[MAX_EXPRESIONES];

    private byte  spellsCount = 0;
    private short spells[] = new short[MAX_NUM_SPELLS];  // le da vida ;)

    private User petUserOwner = null;
    private short petNpcOwnerId  = 0;

    /**
     * El inventario tiene doble función:
     * - en npc comerciante, contiene los items en venta
     * - en npc hostil, contiene los items dropeados
     */
    protected Inventory npcInv;

    private PathFinding pfInfo; // FIXME
    
    protected GameServer server;

    /** Creates a new instance of NPC */
    protected Npc(int npcNumber, GameServer server) {
    	this.server = server;

        this.npcNumber = npcNumber;
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
                orig.x = (byte) Util.random(1, MAP_WIDTH);
                orig.y = (byte) Util.random(1, MAP_HEIGHT);
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
    	return this.npcType == NpcType.NPCTYPE_ROYAL_GUARD;
    }

    public boolean isGambler() {
    	return this.npcType == NpcType.NPCTYPE_GAMBLER;
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
    	return this.movement == AiType.STATIC;
    }

    public Npc makeStatic() {
    	this.movement = AiType.STATIC;
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

    public boolean isBackup() {
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
    
    public void startAttacking() {
    	this.setCanAttack(true);
    }
    
    public void stopAttacking() {
    	this.setCanAttack(false);
    }

    private void setCanAttack(boolean estado) {
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

    public void paralize() {
        this.flags().set(FLAG_PARALIZADO, true);
        this.counters.Paralisis = IntervaloParalizado;
    }

    public void unparalize() {
        this.flags().set(FLAG_PARALIZADO, false);
        this.counters.Paralisis = 0;
    }

    public void immobilize() {
        this.flags().set(FLAG_INMOVILIZADO, true);
    }

    public void unimmobilize() {
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
        return this.attackPower;
    }

    public int getPoderEvasion() {
        return this.dodgePower;
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

    public User getPetUserOwner() {
		return this.petUserOwner;
	}

    public void setPetUserOwner(User petUserOwner) {
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
    	return npcType() == NpcType.NPCTYPE_CASHIER;
    }

    public boolean isPriest() {
    	return npcType() == NpcType.NPCTYPE_PRIEST;
    }

    public boolean isPriestNewbies() {
    	return npcType() == NpcType.NPCTYPE_PRIEST_NEWBIES;
    }

    public boolean isNoble() {
    	return npcType() == NpcType.NPCTYPE_NOBLE;
    }

    public void activate() {
        this.flags().set(FLAG_NPC_ACTIVE, true);
    }

    public void attackedByUserName(String userName) {
        this.attackedBy = userName;
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
        return this.tamable;
    }

    public CharacterCreateResponse characterCreate() {
    	return new CharacterCreateResponse(
			getId(),
			this.infoChar.getBody(),
			this.infoChar.getHead(),

			this.infoChar.getHeading().value(),
			pos().x,
			pos().y,

			this.infoChar.getWeapon(),
			this.infoChar.getShield(),
			this.infoChar.getHelmet(),

			this.infoChar.getFx(),
			this.infoChar.getLoops(),

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

    public void calcularDarExp(User user, int daño) {
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
            if (user.partyIndex > 0) {
            	Party.obtenerExito(user, exp, pos());
            } else {
            	user.getStats().addExp(exp);
                user.sendMessage("Has ganado " + exp + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            }
            user.checkUserLevel();
        }
    }

    public void muereNpc(User user) {
        ////////////// FIXME
        // Lo mato un usuario?
        if (user != null) {
        	boolean eraCrimi = user.isCriminal();

            MapPos pos = user.pos();
            Map m = this.server.getMap(pos.map);

            if (this.snd3 > 0) {
            	m.sendToArea(pos.x, pos.y, new PlayWaveResponse(this.snd3, user.pos().x, user.pos().y));
            }
            user.getFlags().TargetNpc = 0;
            user.getFlags().TargetNpcTipo = 0;
            // El user que lo mato tiene mascotas?
            if (user.getUserPets().hasPets()) {
                user.getUserPets().petsFollowMaster(this);
            }
            user.sendMessage("Has matado la criatura!", FONTTYPE_FIGHT);
            if (this.expCount > 0) {
                user.getStats().addExp(this.expCount * 1000); // FIXME experiencia FACIL
                user.sendMessage("Has ganado " + this.expCount + " puntos de experiencia.", FONTTYPE_FIGHT);
            } else {
                user.sendMessage("No has ganado experiencia al matar la criatura.", FONTTYPE_FIGHT);
            }
            user.getStats().incNPCsMuertos();
            user.quest().checkNpcEnemigo(user, this);

            if (this.stats.alineacion == 0) {
            	// TODO: ¿No debería compararse con NpcType==2? Hay otros guardias aparte del npcNumber=6
                if (this.npcNumber == GUARDIAS) {
                    user.turnCriminal();
                }
                if (!user.getFlags().isGod()) {
                    user.getReputation().incAsesino(vlAsesino);
                }
            } else if (this.stats.alineacion == 1) {
                user.getReputation().incPlebe(vlCazador);
            } else if (this.stats.alineacion == 2) {
                user.getReputation().incNoble(vlAsesino / 2);
            } else if (this.stats.alineacion == 4) {
                user.getReputation().incPlebe(vlCazador);
            }
            // Controla el nivel del usuario
            user.checkUserLevel();

            //Agush: updateamos de ser necesario ;-)
            if (user.isCriminal() != eraCrimi && eraCrimi == true) {
            	user.refreshCharStatus();
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
        if (this.npcInv().getSize() > 0) {
            for (int i = 1; i <= this.npcInv().getSize(); i++) {
                if (this.npcInv().getObject(i) != null && this.npcInv().getObject(i).objid > 0) {
                    Map m = this.server.getMap(pos().map);
                    m.dropItemOnFloor(pos().x, pos().y, 
                    		new InventoryObject(
                    				this.npcInv().getObject(i).objid,
                    				this.npcInv().getObject(i).cant));
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

    public void move(Heading heading) {
        long now = (new java.util.Date()).getTime();
        if ((now - this.lastMove) < 250) {
			return;
		}
        this.lastMove = now;
        MapPos newPos = pos().copy();
        newPos.moveToHeading(heading);
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
                this.infoChar.setHeading(heading);
                mapa.moveNpc(this, newPos.x, newPos.y);
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
                this.infoChar.setHeading(heading);
                mapa.moveNpc(this, newPos.x, newPos.y);
                this.setPos(newPos);
            } else {
                if (this.movement == AiType.NPC_PATHFINDING) {
                    // Someone has blocked the npc's way, we must to seek a new path!
                    //////////// FIXME
                    this.pfInfo.reset();
                    moverAlAzar();
                }
            }
        }
    }

    public void npcEnvenenarUser(User user) {
        int n = Util.random(1, 100);
        if (n < 30) {
            user.poison();
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
            this.movement = AiType.DEFENSE_NPC; // follow
            this.flags().set(FLAG_HOSTIL, false);
        }
    }

    /** Seguir al amo / Follow master */
    public void followMaster() {
        this.flags().set(FLAG_FOLLOW, true);
        this.movement  = AiType.FOLLOW_MASTER; // follow npc's master.
        this.flags().set(FLAG_HOSTIL, false);
        this.targetUser = 0;
        this.targetNpc  = null;
    }

    public Npc talk() {
        if (this.petUserOwner == null) {
			return this;
		}
        if (this.expressionsCount > 0) {
            int azar = Util.random(0, this.expressionsCount - 1);
            Map mapa = this.server.getMap(pos().map);
            if (mapa != null) {
                talkToArea(this.expressions[azar], Color.COLOR_BLANCO);
            }
        }
        return this;
    }

    public void talkToArea(String chat, int color) {
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
        	mapa.sendToArea(pos().x, pos().y,
        			new ChatOverHeadResponse(chat, this.getId(),
        					Color.r(color), Color.g(color), Color.b(color)));
        }
    }

    public void talkToUser(User user, String chat, int color) {
    	user.sendPacket(
    			new ChatOverHeadResponse(chat, this.getId(),
        					Color.r(color), Color.g(color), Color.b(color)));
    }

    public void defenderse() {
        this.movement = AiType.DEFENSE_NPC;
        this.flags().set(FLAG_HOSTIL, true);
    }

    public void changeHeading(Heading heading) {
        // ChangeNPCChar
        Map mapa = this.server.getMap(pos().map);
        if (mapa != null) {
            this.infoChar.setHeading(heading);
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
                if (mapa.hasUser(pos.x, pos.y)) {
                    User user = mapa.getUser(pos.x, pos.y);
                    if (user.isAlive() && user.isAllowingChase()) {
                        // ¿ES CRIMINAL?
                    	if (npcType() != NpcType.NPCTYPE_GUARDAS_CHAOS) {
                    		if (user.isCriminal()) {
                    			changeHeading(dir);
                    			npcAtacaUser(user);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(user.getUserName()) && !this.flags().get(FLAG_FOLLOW)) {
                    			changeHeading(dir);
                    			npcAtacaUser(user);
                    			return;
                    		}
                    	} else {
                    		if (!user.isCriminal()) {
                    			changeHeading(dir);
                    			npcAtacaUser(user);
                    			return;
                    		} else if (this.attackedBy.equalsIgnoreCase(user.getUserName()) && !this.flags().get(FLAG_FOLLOW)) {
                    			changeHeading(dir);
                    			npcAtacaUser(user);
                    			return;
                    		}
                    	}
                    	/* FIXME
                        if ((user.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CRIMINALES) ||
                            (!user.esCriminal() && this.m_guardiaPersigue == GUARDIAS_PERSIGUEN_CIUDADANOS)) {
                            cambiarDir(dir);
                            npcAtacaUser(user); // ok
                            return;
                        } else if (!isFollowing() && m_attackedBy.equalsIgnoreCase(user.getUserName())) {
                            cambiarDir(dir);
                            npcAtacaUser(user); // ok
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
                if (mapa.hasUser(pos.x, pos.y)) {
                    User user = mapa.getUser(pos.x, pos.y);
                    if (user.isAlive() && user.isAllowingChase()) {
                        if (isMagical()) {
                            npcCastSpell(user);
                        }
                        changeHeading(dir);
                        npcAtacaUser(user); // ok
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
                if (mapa.hasUser(pos.x, pos.y)) {
                    User user = mapa.getUser(pos.x, pos.y);
                    if (user.isAlive() && user.isAllowingChase() && this.attackedBy.equalsIgnoreCase(user.getUserName())) {
                        if (isMagical()) {
                            npcCastSpell(user);
                        }
                        changeHeading(dir);
                        npcAtacaUser(user); // ok
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
                    if (mapa.hasUser(x, y)) {
                        User user = mapa.getUser(x, y);
                        if (user != null) {
                            if (user.isAlive() && !user.isInvisible() && user.isAllowingChase()) {
                                if (isMagical()) {
                                   npcCastSpell(user);
                                }
                                Heading dir = pos().findDirection(user.pos());
                                move(dir);
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
                    if (mapa.hasUser(x, y)) {
                        User user = mapa.getUser(x, y);
                        if (!user.getFlags().isGM() && this.attackedBy.equalsIgnoreCase(user.getUserName())) {
                            if (getPetUserOwner() != null) {
		                        if (	!getPetUserOwner().isCriminal() && !user.isCriminal() &&
		                        		(getPetUserOwner().hasSafeLock() || getPetUserOwner().userFaction().ArmadaReal)) {
		                            getPetUserOwner().sendMessage("La mascota no atacará a ciudadanos si eres miembro de la Armada Real o tienes el seguro activado", FontType.FONTTYPE_INFO);
		                            this.attackedBy = "";
		                            followMaster();
		                            return;
		                        }
                            }
                            if (user.isAlive() && !user.isInvisible()) {
                                if (isMagical()) {
                                    npcCastSpell(user);
                                }
                                Heading dir = pos().findDirection(user.pos());
                                move(dir);
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
                    if (mapa.hasUser(x, y)) {
                        User user = mapa.getUser(x, y);

                        if (user == null) break;

                        if (user.isCriminal() && user.isAlive() && !user.isInvisible() && user.isAllowingChase()) {
                            if (isMagical()) {
                                npcCastSpell(user);
                            }
                            Heading dir = pos().findDirection(user.pos());
                            move(dir);
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
                    if (mapa.hasUser(x, y)) {
                        User user = mapa.getUser(x, y);
                        if (!user.isCriminal() && user.isAlive() && !user.isInvisible() && !user.getFlags().isGM()) {
                            if (isMagical()) {
                                npcCastSpell(user);
                            }
                            Heading dir = pos().findDirection(user.pos());
                            move(dir);
                            return;
                        }
                    }
                }
            }
        }
        restoreOldMovement();
    }

    private void seguirAmo() {
        User master = getPetUserOwner();
        if (master == null) {
        	return;
        }
        // FIXME debe ser por rango de visión
        if (master.isAlive() 
    		&& !master.isInvisible() 
    		&& master.pos().distance(pos()) > 3) {
		            Heading dir = pos().findDirection(master.pos());
		            move(dir);
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
        						move(heading);
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
    				if (map.hasUser(x, y)) {
    					User user = map.getUser(x, y);
    					if (user.isAlive() && !user.isInvisible() && !user.isHidden() && user.isAllowingChase()) {
    						// No quiero que ataque siempre al primero
    						if (Util.random(1,  3) < 3) {
    							if (flags().get(FLAG_LANZA_SPELLS)) {
    								npcCastSpell(user);
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
    		int spellIndex = Util.random(0, spellsCount-1);
    		npcLanzaSpellSobreNpc(targetNpc, spells[spellIndex]);
    	}
    	
    }
    

    private void npcLanzaSpellSobreNpc(Npc targetNpc, short spellId) {
    	// NpcLanzaSpellSobreNpc
    	// solo hechizos ofensivos!
    	if (!canAttack()) {
    		return;
    	}
    	stopAttacking();
    	
    	Spell spell = server.getSpell(spellId);
    	if (spell.SubeHP == 2) {
	        int daño = Util.random(spell.MinHP, spell.MaxHP);
	        
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
        move(Heading.value(Util.random(1, 4)));
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
            if (this.npcType == NpcType.NPCTYPE_ROYAL_GUARD || this.npcType == NpcType.NPCTYPE_GUARDAS_CHAOS) {
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
    	case STATIC:
    		// do nothing.
    		break;
    		
        case RANDOM_MOVE:
            if (this.npcType == NpcType.NPCTYPE_ROYAL_GUARD) {
                if (Util.random(1, 12) == 3) {
                    moverAlAzar();
                }
                persigueCriminal();
            } else if (this.npcType == NpcType.NPCTYPE_GUARDAS_CHAOS) {
                if (Util.random(1, 12) == 3) {
                    moverAlAzar();
                }
                persigueCiudadano();
            } else {
                if (Util.random(1, 12) == 3) {
                    moverAlAzar();
                }
            }
            break;
            
        case BAD_NPC_ATTACK_GOOD_USER:
            // Va hacia el usuario cercano
            irUsuarioCercano();
            break;
            
        case DEFENSE_NPC:
            // Va hacia el usuario que lo ataco(FOLLOW)
            seguirAgresor();
            break;
            
        case GUARDS_ATTACK_CRIMINALS:
            // Persigue criminales
            persigueCriminal();
            break;
            
        case GUARDS_ATTACK_CITIZENS:
            // Persigue criminales
            persigueCiudadano();
            break;
            
        case FOLLOW_MASTER:
            seguirAmo();
            if (Util.random(1, 12) == 3) {
                moverAlAzar();
            }
            break;
            
        case NPC_ATTACK_NPC:
            aiNpcAtacaNpc();
            break;
            
        case NPC_OBJECT:
        	aiNpcObjeto();
        	break;
            
        case NPC_PATHFINDING:
            ////irUsuarioCercano();
        	pfInfo.aiPathFinding();
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

    private void npcCastSpell(User user) {
    	// NpcLanzaUnSpell
    	
    	if (!canAttack()) {
    		return;
    	}
        if (user.isInvisible() || user.isHidden()) {
			return;
		}
        // Si no se peude usar magia en el mapa, no le deja hacerlo.
        Map map = this.server.getMap(pos().map);
        if (map.isMagiaSinEfecto()) {
        	return;
        }        
        this.flags().set(FLAG_PUEDE_ATACAR, false);
        int daño = 0;
        
        short spellIndex = this.spells[Util.random(0, this.spellsCount-1)];
        Spell spell = this.server.getSpell(spellIndex);
        if (spell.SubeHP == 1) {
            daño = Util.random(spell.MinHP, spell.MaxHP);
            user.sendWave(spell.WAV);
            user.sendCreateFX(spell.FXgrh, spell.loops);
            user.getStats().addHP(daño);
            user.sendMessage(this.name + " te ha dado " + daño + " puntos de vida.", FONTTYPE_FIGHT);
            user.sendUpdateUserStats();
            
        }

        if (spell.SubeHP == 2) {
            if (user.getFlags().isGM()) {
    			return;
    		}
            daño = Util.random(spell.MinHP, spell.MaxHP);
            
            // Si el usuario tiene un sombrero mágico de defensa, se reduce el daño
            if (user.getUserInv().tieneCascoEquipado()) {
            	daño = daño - Util.random(
            			user.getUserInv().getCasco().DefensaMagicaMin,
            			user.getUserInv().getCasco().DefensaMagicaMax);
            }
            
            // Si el usuario tiene un anillo mágico de defensa, se reduce el daño
            if (user.getUserInv().tieneAnilloEquipado()) {
            	daño = daño - Util.random(
            			user.getUserInv().getAnillo().DefensaMagicaMin,
            			user.getUserInv().getAnillo().DefensaMagicaMax);
            }
            
            if (daño < 0) {
            	daño = 0;
            }
            
            user.sendWave(spell.WAV);
            user.sendCreateFX(spell.FXgrh, spell.loops);
            user.getStats().removeHP(daño);
            user.sendMessage(this.name + " te ha quitado " + daño + " puntos de vida.", FONTTYPE_FIGHT);
            user.sendUpdateUserStats();
            // Muere
            if (user.getStats().MinHP < 1) {
                user.getStats().MinHP = 0;
                user.userDie();
                if (getPetUserOwner() != null) {
                    getPetUserOwner().contarMuerte(user);
                    getPetUserOwner().actStats(user);
                }
            }
        }
        
        if (spell.isParaliza() || spell.isInmoviliza()) {
    		if (!user.getFlags().Paralizado) {
    			user.sendWave(spell.WAV);
    			user.sendCreateFX(spell.FXgrh, spell.loops);
    			
    			if (user.getUserInv().tieneAnilloEquipado() && user.getUserInv().getAnillo().ObjIndex == SUPERANILLO) {
    	            user.sendMessage("Tu anillo rechaza los efectos del hechizo.", FONTTYPE_FIGHT);
    	            return;
    			}
    			
    			if (spell.isInmoviliza()) {
    				user.getFlags().Inmovilizado = true;
    			}
    			
    			user.getFlags().Paralizado = true;
    			user.getCounters().Paralisis = IntervaloParalizado;
    			user.sendPacket(new ParalizeOKResponse());
    		}
        }
        
        if (spell.isEstupidez()) { // turbación
        	if (!user.isDumb()) {
    			user.sendWave(spell.WAV);
    			user.sendCreateFX(spell.FXgrh, spell.loops);

    			if (user.getUserInv().tieneAnilloEquipado() && user.getUserInv().getAnillo().ObjIndex == SUPERANILLO) {
    	            user.sendMessage("Tu anillo rechaza los efectos del hechizo.", FONTTYPE_FIGHT);
    	            return;
    			}
    			user.makeDumb();
        	}
        }
    }

    private void npcAtacaUser(User user) {
    	if (user.getFlags().AdminInvisible || !user.isAllowingChase()) {
    		return;
    	}
    	
        Map mapa = this.server.getMap(pos().map);
        // El npc puede atacar ???
        if (!canAttack()) {
			return;
		}
        if (user.getFlags().isGM()) {
			return;
		}
        user.getUserPets().petsAttackNpc(this);
		targetUser(user.getId());
        if (user.getFlags().AtacadoPorNpc == 0 && user.getFlags().AtacadoPorUser == 0) {
			user.getFlags().AtacadoPorNpc = this.getId();
		}
        this.flags().set(FLAG_PUEDE_ATACAR, false);
        if (this.snd1 > 0) {
        	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(this.snd1, pos().x,pos().y));
		}
        if (user.npcImpacto(this)) {
        	mapa.sendToArea(pos().x, pos().y, new PlayWaveResponse(SOUND_IMPACTO, pos().x,pos().y));
            if (!user.isSailing()) {
            	mapa.sendToArea(pos().x, pos().y, new CreateFXResponse(user.getId(), FXSANGRE, (short) 0));
			}
            user.npcDaño(this);
            // ¿Puede envenenar?
            if (puedeEnvenenar()) {
				npcEnvenenarUser(user);
			}
        } else {
        	user.sendPacket(new NPCSwingResponse());
        }
        // -----Tal vez suba los skills------
        user.riseSkill(Skill.SKILL_Tacticas);
        user.sendUpdateUserStats();
        // Controla el nivel del usuario
        user.checkUserLevel();
    }

    private boolean npcImpactoNpc(Npc victim) {
        long poderAtt = this.attackPower;
        long poderEva = victim.dodgePower;
        double probExito = Math.max(10, Math.min(90, 50 + ((poderAtt - poderEva) * 0.4)));
        return (Util.random(1, 100) <= probExito);
    }

    private void npcDañoNpc(Npc victima) {
        int daño = Util.random(this.stats.MinHIT, this.stats.MaxHIT);
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
        victim.movement = AiType.NPC_ATTACK_NPC;

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

    public void backupNpc(IniFile ini) {
        // Sub BackUPnPc(NpcIndex As Integer)
        // General
        String section = "NPC" + this.npcNumber;
        ini.setValue(section, "NpcType", this.npcType.value());
        if (this.npcType == NpcType.NPCTYPE_ROYAL_GUARD) {
            ini.setValue(section, "GuardiaPersigue", this.guardiaPersigue);
        }
        ini.setValue(section, "Name", this.name);
        ini.setValue(section, "Desc", this.description);
        ini.setValue(section, "Head", this.infoChar.head);
        ini.setValue(section, "Body", this.infoChar.body);
        ini.setValue(section, "Heading", this.infoChar.heading.value());
        ini.setValue(section, "Movement", this.movement.ordinal());
        ini.setValue(section, "TipoItems", this.objType);
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
        
        ini.setValue(section, "Domable", this.tamable);
        ini.setValue(section, "PoderAtaque", this.attackPower);
        ini.setValue(section, "PoderEvasion", this.dodgePower);

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
        if (this.npcType == NpcType.NPCTYPE_ROYAL_GUARD) {
        	this.guardiaPersigue = ini.getShort(section, "GuardiaPersigue");
        }
        this.name = ini.getString(section, "Name");
        this.description = ini.getString(section, "Desc");
        this.infoChar.head   = ini.getShort(section, "Head");
        this.infoChar.body   = ini.getShort(section, "Body");
        this.infoChar.heading      = Heading.value(ini.getShort(section, "Heading"));
        this.movement 	 = AiType.value(ini.getShort(section, "Movement"));
        this.oldMovement = this.movement;
        this.objType = ini.getShort(section, "TipoItems"); // Tipo de items con los que comercia
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

        this.tamable = ini.getShort(section, "Domable");
        this.attackPower	= ini.getInt(section, "PoderAtaque");
        this.dodgePower	= ini.getInt(section, "PoderEvasion");

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

    public String healthDescription(User user) {
    	if (this.stats().MaxHP <= 0) {
			return "";
		}

    	if (user.getFlags().isGM()) {
			return estadoVidaExacta();
		}
    	
    	if (!user.isAlive()) {
    		return "";
    	}
    	
        short skSup = user.skills().get(Skill.SKILL_Supervivencia);
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
    	return npcType() == NpcType.NPCTYPE_TRAINER;
    }

    public void setPetTargetNpc(Npc targetNpc) {
    	if (targetNpc == this) {
    		// can't attack himself
    		return;
    	}
		this.targetNpc = targetNpc;
		this.movement = AiType.NPC_ATTACK_NPC;
    }


	protected ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);
	}

	private NpcFlags flags() {
		return flags;
	}

    public void calculatePath() {
        //private void pathFindingAI() {
        Map mapa = this.server.getMap(pos().map);
        if (mapa == null) {
			return;
		}
        for (byte x = (byte) (pos().x-10); x <= (byte) (pos().x+10); x++) {
            for (byte y = (byte) (pos().y-10); y <= (byte) (pos().y+10); y++) {
                MapPos pos = MapPos.mxy(pos().map, x, y);
                if (pos.isValid()) {
                    if (mapa.hasUser(x, y)) {
                        User user = mapa.getUser(x, y);
                        if (user.isAlive() && !user.isInvisible() && !user.isHidden() && user.isAllowingChase()) {
                        	this.pfInfo = new PathFinding(this, MapPos.mxy(pos().map, x, y), user);
                        	this.pfInfo.init();
                            return;
                        }
                    }
                }
            }
        }
    }

}
