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
package org.argentumonline.server.user;

import static org.argentumonline.server.util.Color.COLOR_BLANCO;
import static org.argentumonline.server.util.Color.COLOR_CYAN;
import static org.argentumonline.server.util.FontType.FONTTYPE_FIGHT;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.AbstractCharacter;
import org.argentumonline.server.CharInfo;
import org.argentumonline.server.City;
import org.argentumonline.server.Clazz;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjType;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Pos;
import org.argentumonline.server.Security;
import org.argentumonline.server.Skill;
import org.argentumonline.server.anticheat.SpeedHackCheck;
import org.argentumonline.server.anticheat.SpeedHackException;
import org.argentumonline.server.areas.AreasAO;
import org.argentumonline.server.guilds.Guild;
import org.argentumonline.server.guilds.GuildUser;
import org.argentumonline.server.inventory.BankInventory;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.inventory.UserInventory;
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapObject;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.map.Terrain;
import org.argentumonline.server.map.Tile.Trigger;
import org.argentumonline.server.map.Zone;
import org.argentumonline.server.net.ServerPacket;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcCashier;
import org.argentumonline.server.npc.NpcGambler;
import org.argentumonline.server.npc.NpcTrainer;
import org.argentumonline.server.npc.NpcType;
import org.argentumonline.server.protocol.AttributesResponse;
import org.argentumonline.server.protocol.BlindNoMoreResponse;
import org.argentumonline.server.protocol.BlockPositionResponse;
import org.argentumonline.server.protocol.BlockedWithShieldOtherResponse;
import org.argentumonline.server.protocol.BlockedWithShieldUserResponse;
import org.argentumonline.server.protocol.ChangeInventorySlotResponse;
import org.argentumonline.server.protocol.ChangeMapResponse;
import org.argentumonline.server.protocol.CharacterChangeResponse;
import org.argentumonline.server.protocol.CharacterCreateResponse;
import org.argentumonline.server.protocol.ChatOverHeadResponse;
import org.argentumonline.server.protocol.ConsoleMsgResponse;
import org.argentumonline.server.protocol.DiceRollResponse;
import org.argentumonline.server.protocol.DisconnectResponse;
import org.argentumonline.server.protocol.DumbNoMoreResponse;
import org.argentumonline.server.protocol.DumbResponse;
import org.argentumonline.server.protocol.ErrorMsgResponse;
import org.argentumonline.server.protocol.LevelUpResponse;
import org.argentumonline.server.protocol.LoggedMessageResponse;
import org.argentumonline.server.protocol.MeditateToggleResponse;
import org.argentumonline.server.protocol.MiniStatsResponse;
import org.argentumonline.server.protocol.NPCHitUserResponse;
import org.argentumonline.server.protocol.NPCKillUserResponse;
import org.argentumonline.server.protocol.NavigateToggleResponse;
import org.argentumonline.server.protocol.ObjectCreateResponse;
import org.argentumonline.server.protocol.ParalizeOKResponse;
import org.argentumonline.server.protocol.PlayMidiResponse;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.protocol.PongResponse;
import org.argentumonline.server.protocol.PosUpdateResponse;
import org.argentumonline.server.protocol.RainToggleResponse;
import org.argentumonline.server.protocol.RemoveAllDialogsResponse;
import org.argentumonline.server.protocol.RemoveCharDialogResponse;
import org.argentumonline.server.protocol.RestOKResponse;
import org.argentumonline.server.protocol.ResuscitationSafeOffResponse;
import org.argentumonline.server.protocol.ResuscitationSafeOnResponse;
import org.argentumonline.server.protocol.SafeModeOffResponse;
import org.argentumonline.server.protocol.SafeModeOnResponse;
import org.argentumonline.server.protocol.SendSkillsResponse;
import org.argentumonline.server.protocol.SetInvisibleResponse;
import org.argentumonline.server.protocol.ShowBlacksmithFormResponse;
import org.argentumonline.server.protocol.UpdateHPResponse;
import org.argentumonline.server.protocol.UpdateHungerAndThirstResponse;
import org.argentumonline.server.protocol.UpdateTagAndStatusResponse;
import org.argentumonline.server.protocol.UpdateUserStatsResponse;
import org.argentumonline.server.protocol.UserAttackedSwingResponse;
import org.argentumonline.server.protocol.UserCharIndexInServerResponse;
import org.argentumonline.server.protocol.UserHitNPCResponse;
import org.argentumonline.server.protocol.UserHittedByUserResponse;
import org.argentumonline.server.protocol.UserHittedUserResponse;
import org.argentumonline.server.protocol.UserIndexInServerResponse;
import org.argentumonline.server.protocol.UserSwingResponse;
import org.argentumonline.server.protocol.WorkRequestTargetResponse;
import org.argentumonline.server.quest.UserQuest;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.util.Color;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;
import org.argentumonline.server.util.Util;

import com.google.gson.Gson;

import io.netty.channel.Channel;

/**
 * @author gorlok
 */
public class User extends AbstractCharacter {

	private static Logger log = LogManager.getLogger();
	
	private static final int EXPERIENCE_BY_LEVEL_UP = 50;

	private static final String TAG_USER_INVISIBLE = "[INVISIBLE]";

	public static MapPos WP_PRISION = MapPos.mxy(66, 75, 47);
    public static MapPos WP_LIBERTAD = MapPos.mxy(66, 75, 65);
	
	private Channel channel = null;

	String userName = "";
	String passwordHash = "";

	String description = ""; // Descripcion
	
	private int chatColor = Color.COLOR_BLANCO;

	Clazz clazz = Clazz.Hunter;

	UserRace race = UserRace.RAZA_HUMAN;
	UserGender gender = UserGender.GENERO_MAN;

	String email = "";
	City homeland = City.ULLATHORPE;

	private UserSpells spells;

	private UserPets userPets = new UserPets();

	private CharInfo mimetizadoChar = new CharInfo(); // FIXME

	// FIXME network
	long NumeroPaquetesPorMiliSec = 0;
	long BytesTransmitidosUser = 0;
	long BytesTransmitidosSvr = 0;

	GuildUser guildUser = new GuildUser(this);

	// FIXME security/anticheat
	int prevCRC = 0;
	long PacketNumber = 0;
	long RandKey = 0;

	boolean m_saliendo = false;

	// FIXME GM
	String ip = "";
	
    public boolean banned = false;
    public boolean AdministrativeBan = false;
	public String bannedBy = "";
	public String bannedReason = "";
	
	public boolean showName = true; // Permite que los GMs oculten su nick con el comando /SHOWNAME
	public String  descRM = ""; // TODO

	UserFlags flags = new UserFlags();

	UserStats stats = new UserStats();

	Reputation reputation = new Reputation();

	UserSkills skills = new UserSkills();

	UserFaction faction;

	private UserCounters counters = new UserCounters();

	public UserTrade userTrade;

	public UserStorage userStorage;

	private UserQuest quest;

	private UserInventory userInv;
	private BankInventory bankInv;
	
	// TODO
    public short partyIndex = 0;  // index a la party q es miembro
    public short partySolicitud = 0; // index a la party q solicito
    
	private SpeedHackCheck speedHackMover = new SpeedHackCheck("SpeedHack de mover");
	
	private GameServer server;
	
	public User(GameServer server) {
		init(server);
		this.userTrade = new UserTrade(server, this);
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
		if (channel != null) {
			java.net.InetSocketAddress addr = (java.net.InetSocketAddress) channel.remoteAddress();
			if (addr != null) {
				this.ip = addr.getAddress().getHostAddress();
				log.info(this.userName + " conectado desde " + this.ip);
			}
		}
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void closeConnection() {
		if (this.channel != null) {
			this.channel.close();
		}
	}

	private void init(GameServer aoserver) {
		this.server = aoserver;
		this.userStorage = new UserStorage(this.server, this);

		this.setId(this.server.nextId());
		this.spells = new UserSpells(this.server, this);
		this.quest = new UserQuest(this.server);
		this.userInv = new UserInventory(this.server, this, MAX_USER_INVENTORY_SLOTS);
		this.bankInv = new BankInventory(this.server, this, MAX_BANCOINVENTORY_SLOTS);
		this.faction = new UserFaction(this.server, this);
	}
	
	public UserSpells spells() {
		return this.spells;
	}

	public UserSkills skills() {
		return this.skills;
	}

	public CharInfo mimetizeChar() {
		return this.mimetizadoChar;
	}

	public GuildUser getGuildInfo() {
		return this.guildUser;
	}

	public Guild getGuild() {
		return this.server.getGuildMngr().getGuild(this.guildUser.m_guildName);
	}

	private ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);
	}

	public BankInventory getBankInventory() {
		return this.bankInv;
	}

	public UserPets getUserPets() {
		return this.userPets;
	}

	public GuildUser guildInfo() {
		return this.guildUser;
	}
	
	public UserTrade getUserTrade() {
		return userTrade;
	}

	public int nextCRC() {
		// FIXME
		return (this.prevCRC = this.prevCRC % CRCKEY);
	}

	public UserRace race() {
		return this.race;
	}
	
	public void setRace(UserRace race) {
		this.race = race;
	}

	public UserGender gender() {
		return this.gender;
	}
	
	public void setGender(UserGender gender) {
		this.gender = gender;
	}

	public String getUserName() {
		return this.userName;
	}

	public boolean hasUserName() {
		return getUserName() != null && !getUserName().isEmpty();
	}

	public Clazz clazz() {
		return this.clazz;
	}
	
	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}

	public UserQuest quest() {
		return this.quest;
	}

	public void nextQuest() {
		this.quest().m_nroQuest++;
		this.quest().m_enQuest = true;
	}

	public boolean isCriminal() {
		return this.reputation.esCriminal();
	}
	
	public boolean isGM() {
		return getFlags().isGM();
	}
	
	public boolean isGod() {
		return getFlags().isGod();
	}
	
	public boolean isAdmin() {
		return getFlags().isAdmin();
	}
	
	public boolean isDemiGod() {
		return getFlags().isDemiGod();
	}
	
	public boolean isCounselor() {
		return getFlags().isCounselor();
	}
	
	public boolean isRoleMaster() {
		return getFlags().isRoleMaster();
	}
	
	public boolean isChaosCouncil() {
		return getFlags().isChaosCouncil(); 
	}

	public boolean isRoyalCouncil() {
		return getFlags().isRoyalCouncil(); 
	}
	
	
	public boolean isAlive() {
		return !getFlags().Muerto;
	}

	public boolean isInvisible() {
		return getFlags().Invisible;
	}

	public boolean isTrading() {
		return getFlags().Comerciando;
	}

	public boolean isHidden() {
		return getFlags().Oculto;
	}
	
	public boolean isAllowingChase() {
		return getFlags().AdminPerseguible;
	}

	public boolean isSailing() {
		return getFlags().Navegando;
	}

	public boolean isWorking() {
		return getFlags().Trabajando;
	}

	public UserStats getStats() {
		return this.stats;
	}

	public Reputation getReputation() {
		return this.reputation;
	}

	public UserFlags getFlags() {
		return this.flags;
	}

	public UserCounters getCounters() {
		return this.counters;
	}

	@Override
	public String toString() {
		return "User(id=" + getId() + ",name=" + this.userName + ")";
	}

	public UserFaction userFaction() {
		return this.faction;
	}

	public UserInventory getUserInv() {
		return this.userInv;
	}

	public boolean hasSafeLock() {
		return getFlags().Seguro;
	}

	/**
	 * Sends a network packet to the user.
	 *
	 * @param msg    is the message type to send.
	 * @param params is the parameters of the message (optional).
	 */
	public synchronized void sendPacket(ServerPacket packet) {
		if (this.m_saliendo) {
			return;
		}
		try {
			log.debug(">>" + this.userName + ">> " + packet.id());
			Gson gson = new Gson();
			System.out.println(">>" + this.userName + ">> " + packet.id() + " " + gson.toJson(packet)); // FIXME remove this
			this.channel.writeAndFlush(packet);
		} catch (Exception e) {
			e.printStackTrace();
			quitGame();
		}
	}

	/**
	 * Gives the user's status: citizen or criminal.
	 *
	 * @return the user's status.
	 */
	public String getStatusTag() {
		return isCriminal() ? "<CRIMINAL>" : "<CIUDADANO>";
	}

	public void gamble(int gold) {
		// Comando /APOSTAR
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_PET);
		if (npc == null) {
			return;
		}
		if (!npc.isGambler() || !(npc instanceof NpcGambler)) {
			talk(COLOR_BLANCO, "No tengo ningun interes en apostar.", npc.getId());
			return;
		}

		NpcGambler gambler = (NpcGambler)npc;
		gambler.bet(this, gold);
	}

	public void postOnForum(String title, String body) {
		// Comando DEMSG
		if (getFlags().TargetObj == 0) {
			return;
		}
		ObjectInfo iobj = findObj(getFlags().TargetObj);
		if (iobj.esForo()) {
			this.server.getForumManager().postOnForum(iobj.ForoID, title, body, getUserName());
		}
	}

	public void enlist() {
		// Comando /ENLISTAR
		if (!checkAlive("¡¡Estas muerto!! Busca un sacerdote y no me hagas perder el tiempo.")) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc == null) {
			return;
		}
		if (!npc.isNoble()) {
			talk(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (pos().distance(npc.pos()) > 4) {
			talk(COLOR_BLANCO, "Jeje, acércate o no podré escucharte. ¡Estás demasiado lejos!", npc.getId());
			return;
		}
		if (!npc.isFaction()) {
			userFaction().royalArmyEnlist(npc);
		} else {
			userFaction().darkLegionEnlist(npc);
		}
	}
	
	public void reward() {
		// Comando /RECOMPENSA
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc == null) {
			return;
		}
		if (!npc.isNoble()) {
			talk(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (!checkAlive("¡¡Estás muerto!! Busca un sacerdote y no me hagas perder el tiempo!")) {
			return;
		}
		if (!npc.isFaction()) {
			if (!userFaction().ArmadaReal) {
				talk(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			userFaction().royalArmyReward(npc);
		} else {
			if (!userFaction().FuerzasCaos) {
				talk(COLOR_BLANCO, "No perteneces a las fuerzas del caos!!!", npc.getId());
				return;
			}
			userFaction().darkLegionReward(npc);
		}
	}

	public void sendMiniStats() { // hazte fama y échate a dormir...
		sendPacket(new MiniStatsResponse(
				(int) this.faction.citizensKilled,
				(int) this.faction.criminalsKilled,
				getStats().usuariosMatados,
				(short) getStats().NPCsMuertos,
				this.clazz.id(),
				(int) this.getCounters().Pena));

		sendUserAttributes();
		sendSkills();
		sendFame();
	}

	public boolean isLogged() {
		return getFlags().UserLogged;
	}

	public String getIP() {
		return this.ip;
	}

	public void petStand() {
		// Comando /QUIETO
		// Comando a mascotas
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_PET);
		if (npc != null) {
			if (npc.getPetUserOwner() != this) {
				return;
			}
			npc.makeStatic();
			npc.talk();
		}
	}

	public void petFollowMaster() {
		// Comando /ACOMPAÑAR
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_PET);
		if (npc != null) {
			// es mi mascota?
			if (npc.getPetUserOwner() != this) {
				return;
			}
			npc.followMaster();
			npc.talk();
		}
	}

	public void showHelp() {
		// Comando /AYUDA
		String[] ayuda = this.server.readHelp();
		for (String element : ayuda) {
			sendMessage(element, FontType.FONTTYPE_INFO);
		}
	}

	public void bankDepositGold(int cant) {
		// Comando /DEPOSITAR
		// DEPOSITAR ORO EN EL BANCO
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			if (npc.isBankCashier()) {
				((NpcCashier)npc).depositarOroBanco(this, cant);
			}
		}
	}

	public void bankExtractGold(int amount) {
		// Comando /RETIRAR oro
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null && npc.isBankCashier()) {
			((NpcCashier)npc).retirarOroBanco(this, amount);
		}
	}

	public void leaveFaction() {
		// Comando /RETIRAR faccion
		// Salir de la facción Armada/Caos
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null && npc.isNoble()) {
			leaveUserFaction(npc);
		}
	}

	private void leaveUserFaction(Npc npc) {
		// Se quiere retirar de la armada
		if (checkNpcNear(npc, DISTANCE_FACTION)) {
			if (isRoyalArmy()) {
				if (!npc.isFaction()) {
					this.faction.royalArmyKick();
					talk(COLOR_BLANCO, "Serás bienvenido a las fuerzas imperiales si deseas regresar.", npc.getId());
				} else {
					talk(COLOR_BLANCO, "¡Sal de aquí bufón!", npc.getId());
				}
			} else if (isDarkLegion()) {
				if (npc.isFaction()) {
					this.faction.darkLegionKick();
					talk(COLOR_BLANCO, "Ya volverás arrastrándote.", npc.getId());
				} else {
					talk(COLOR_BLANCO, "¡Sal de aquí maldito criminal!", npc.getId());
				}
			} else {
				talk(COLOR_BLANCO, "¡No perteneces a ninguna fuerza!", npc.getId());
			}
		}
	}

	public Npc getNearNpcSelected(int distance) {
		// Se asegura que el target es un npc
		if (getFlags().TargetNpc == 0) {
			sendMessage("Debes seleccionar un personaje cercano para poder interactuar.", FontType.FONTTYPE_INFO);
		} else {
			Npc npc = this.server.npcById(getFlags().TargetNpc);
			if (checkNpcNear(npc, distance)) {
				return npc;
			}
		}
		return null;
	}

	private boolean checkNpcNear(Npc npc, int distance) {
		if (npc.pos().distance(pos()) > distance) {
			sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return false;
		}
		return true;
	}

	public boolean checkAlive() {
		return checkAlive("¡¡Estás muerto!!");
	}

	public boolean checkAlive(String message) {
		if (isAlive()) {
			return true;
		} else {
			sendMessage(message, FontType.FONTTYPE_INFO);
			return false;
		}
	}

	public void changePassword(String newPassword) {
		// Comando /PASSWD
		newPassword = newPassword.trim();
		if (newPassword.length() < 6) {
			sendMessage("El password debe tener al menos 6 caracteres.", FontType.FONTTYPE_INFO);
		} else if (newPassword.length() > 20) {
			sendMessage("El password puede tener hasta 20 caracteres.", FontType.FONTTYPE_INFO);
		} else {
			this.passwordHash = Security.hashPassword(userName, newPassword);
			// FIXME actualizar el passwordHash en storage
			sendMessage("El password ha sido cambiado. ¡Cuidalo!", FontType.FONTTYPE_INFO);
		}
	}

	public void userTrainWithPet(byte petIndex) {
		// Comando ENTR
		// Entrenar con una mascota.
		if (!checkAlive()) {
			return;
		}
		if (getFlags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(getFlags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (!npc.isTrainer()) {
			return;
		}

		NpcTrainer npcTrainer = (NpcTrainer) npc;
		if (npcTrainer.isTrainerIsFull()) {
			talk(COLOR_BLANCO, "No puedo traer mas criaturas, mata las existentes!", npc.getId());
		} else {
			npcTrainer.spawnTrainerPet(petIndex);
		}
	}

	public void trainList() {
		// Comando /ENTRENAR
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_TRAINER);
		if (npc == null) {
			return;
		}
		if (npc.isTrainer()) {
			((NpcTrainer)npc).sendTrainerCreatureList(this);
		}
	}

	public void rest() {
		// Comando /DESCANSAR
		if (!checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (mapa.hasObject(pos().x, pos().y) && mapa.getObject(pos().x, pos().y).obj_ind == FOGATA) {
			sendPacket(new RestOKResponse());
			if (!getFlags().Descansar) {
				sendMessage("Te acomodas junto a la fogata y comienzas a descansar.", FontType.FONTTYPE_INFO);
			} else {
				sendMessage("Te levantas.", FontType.FONTTYPE_INFO);
			}
			getFlags().Descansar = !getFlags().Descansar;
		} else {
			if (getFlags().Descansar) {
				sendMessage("Te levantas.", FontType.FONTTYPE_INFO);
				getFlags().Descansar = false;
				sendPacket(new RestOKResponse());
				return;
			}
			sendMessage("No hay ninguna fogata junto a la cual descansar.", FontType.FONTTYPE_INFO);
		}
	}

	public void sendFame() {
		sendPacket(getReputation().createFameResponse());
	}

	public void sailingToggle() {
		// DoNavega
		double modNave = this.clazz().modNavegacion();
		ObjectInfo barco = this.userInv.getBarco();
		if (skills().get(Skill.SKILL_Navegacion) / modNave < barco.MinSkill) {
			sendMessage("No tenes suficientes conocimientos para usar este barco.", FontType.FONTTYPE_INFO);
			sendMessage("Necesitas " + (int) (barco.MinSkill * modNave) + " puntos en navegacion.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (!getFlags().Navegando) {
			this.infoChar.head = 0;
			if (isAlive()) {
				this.infoChar.body = barco.Ropaje;
			} else {
				this.infoChar.body = OBJ_INDEX_FRAGATA_FANTASMAL;
			}
			this.infoChar.shield = NingunEscudo;
			this.infoChar.weapon = NingunArma;
			this.infoChar.helmet = NingunCasco;
			getFlags().Navegando = true;
		} else {
			getFlags().Navegando = false;
			if (isAlive()) {
				this.infoChar.head = this.origChar.head;
				if (this.userInv.tieneArmaduraEquipada()) {
					this.infoChar.body = this.userInv.getArmadura().Ropaje;
				} else {
					undress();
				}
				if (this.userInv.tieneEscudoEquipado()) {
					this.infoChar.shield = this.userInv.getEscudo().ShieldAnim;
				}
				if (this.userInv.tieneArmaEquipada()) {
					this.infoChar.weapon = this.userInv.getArma().WeaponAnim;
				}
				if (this.userInv.tieneCascoEquipado()) {
					this.infoChar.helmet = this.userInv.getCasco().CascoAnim;
				}
			} else {
				this.infoChar.body = OBJ_INDEX_CUERPO_MUERTO;
				this.infoChar.head = OBJ_INDEX_CABEZA_MUERTO;
				this.infoChar.shield = NingunEscudo;
				this.infoChar.weapon = NingunArma;
				this.infoChar.helmet = NingunCasco;
			}
		}
		sendCharacterChange();
		sendPacket(new NavigateToggleResponse());
	}

	public void tratarDeHacerFogata() {
		MapPos targetPos = MapPos.mxy(getFlags().TargetObjMap, getFlags().TargetObjX, getFlags().TargetObjY);
		Map mapa = this.server.getMap(targetPos.map);
		if (!mapa.isLegalPos(targetPos, false, true)) {
			return;
		}

		if (!isAlive()) {
			sendMessage("No puedes hacer fogatas estando muerto.", FontType.FONTTYPE_INFO);
			return;
		}

		if (mapa.getObject(targetPos.x, targetPos.y).obj_ind != Constants.Leña) {
			sendMessage("Necesitas clickear sobre Leña para hacer ramitas", FontType.FONTTYPE_INFO);
		    return;
		}

		if (pos().distance(targetPos) > 2) {
			sendMessage("Estás demasiado lejos para prender la fogata.", FontType.FONTTYPE_INFO);
		    return;
		}

		if (mapa.getObject(targetPos.x, targetPos.y).obj_cant < 3) {
			sendMessage("Necesitas por lo menos tres troncos para hacer una fogata.", FontType.FONTTYPE_INFO);
			return;
		}

		int suerte = 1;
		if (skills().get(Skill.SKILL_Supervivencia) < 6) {
			suerte = 3; // 33%
		} else if (skills().get(Skill.SKILL_Supervivencia) < 34) {
			suerte = 2; // 66%
		} else if (skills().get(Skill.SKILL_Supervivencia) > 34) {
			suerte = 1; // 100%
		}
		boolean exito = Util.random(1, suerte) == 1;

		if (exito) {
			short objid = FOGATA_APAG;
			int cant = mapa.getObject(targetPos.x, targetPos.y).obj_cant / 3;
			if (cant > 1) {
				sendMessage("Has hecho " + cant + " fogatas.", FontType.FONTTYPE_INFO);
			} else {
				sendMessage("Has hecho una fogata.", FontType.FONTTYPE_INFO);
			}
			mapa.removeObject(targetPos.x, targetPos.y);
			mapa.addObject(objid, cant, targetPos.x, targetPos.y);
		} else {
			if (getFlags().UltimoMensaje != 10) {
				sendMessage("No has podido hacer la fogata.", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 10;
			}
		}
		riseSkill(Skill.SKILL_Supervivencia);

		this.server.getTrashCollector().add(targetPos);
	}

	public void useItem(short slot) {
		if (this.userInv.getObject(slot) != null && this.userInv.getObject(slot).objid == 0) {
			return;
		}
		this.userInv.useInvItem(slot);
	}

	/**
	 * Fabricar lingotes
	 */
	private void makeIngots() {
		if (this.userInv.getObject(getFlags().TargetObjInvSlot).cant < 5) {
			sendMessage("No tienes suficientes minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		ObjectInfo info = findObj(getFlags().TargetObjInvIndex);
		if (info.objType != ObjType.Minerales) {
			sendMessage("Debes utilizar minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		this.userInv.quitarUserInvItem(getFlags().TargetObjInvSlot, 5);
		sendInventorySlot(getFlags().TargetObjInvSlot);
		if (Util.random(1, info.MinSkill) <= 10) {
			sendMessage("Has obtenido un lingote!!!", FontType.FONTTYPE_INFO);
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(info.LingoteIndex, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(info.LingoteIndex, 1));
			}
			sendMessage("¡Has obtenido un lingote!", FontType.FONTTYPE_INFO);
		} else {
			if (getFlags().UltimoMensaje != 7) {
				sendMessage("Los minerales no eran de buena calidad, no has logrado hacer un lingote.",
						FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 7;
			}
		}
		getFlags().Trabajando = true;
	}

	/**
	 * Fundir mineral
	 */
	private void meltOre() {
		if (getFlags().TargetObjInvIndex > 0) {
			ObjectInfo info = findObj(getFlags().TargetObjInvIndex);
			if (info.objType == ObjType.Minerales
					&& info.MinSkill <= skills().get(Skill.SKILL_Mineria) / this.clazz().modFundicion()) {
				makeIngots();
			} else {
				sendMessage("No tenes conocimientos de mineria suficientes para trabajar este mineral.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	private void tameCreature(Npc npc) {
		if (getUserPets().isFullPets()) {
			sendMessage("No podes controlar mas criaturas.", FontType.FONTTYPE_INFO);
			return;
		}

		if (npc.getPetUserOwner() == this) {
			sendMessage("La criatura ya te ha aceptado como su amo.", FontType.FONTTYPE_INFO);
			return;
		}

		if (npc.getPetUserOwner() != null) {
			sendMessage("La criatura ya tiene amo.", FontType.FONTTYPE_INFO);
			return;
		}
		
	    if (!getUserPets().canTame(npc.getNumber())) {
			sendMessage("No puedes domar más de una criatura del mismo tipo.", FontType.FONTTYPE_INFO);
			return;
	    }

	    
	    int puntosDomar = getStats().attr().get(Attribute.CHARISMA) * skills().get(Skill.SKILL_Domar);
	    int puntosRequeridos = npc.domable();
	    if (clazz() == Clazz.Druid && getUserInv().tieneAnilloEquipado() && getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
	    	puntosRequeridos = (int) (puntosRequeridos * 0.8);
	    }

	    if (puntosRequeridos <= puntosDomar && Util.random(1, 5) == 1) {
			addTamedPet(npc);
			sendMessage("La criatura te ha aceptado como su amo.", FontType.FONTTYPE_INFO);
			riseSkill(Skill.SKILL_Domar);
			// y hacemos respawn del npc original para reponerlo.
			Npc.spawnNpc(npc.getNumber(), npc.getOrig(), false, true);
			
	        // Es zona segura?
			Map map = server.getMap(pos().map);
	        
	        if (map.isSafeMap()) {
	        	map.exitNpc(npc);
	        	npc.pos().reset();
        		sendMessage("No se permiten mascotas en zonas seguras. Te esperará afuera.", FontType.FONTTYPE_INFO);
	        }
			
		} else {
			if (getFlags().UltimoMensaje != 5) {
				sendMessage("No has logrado domar la criatura.", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 5;
			}
		}
	    
	    // Entreno domar. Es un 30% más dificil si no sos druida.
	    if (clazz() == Clazz.Druid || Util.random(1,  3) < 3) {
	        riseSkill(Skill.SKILL_Domar);
	    }
	}

	public void addTamedPet(Npc npc) {
		getUserPets().addPet(npc);
		npc.setPetUserOwner(this);
		npc.followMaster();
		npc.setSpellSpawnedPet(false);
	}
	
	private boolean suerteMineria() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Mineria) / 10)]);
		return (Util.random(1, rango) < 6);
	}

	/**
	 * Hacer minería
	 */
	private void mining() {
		getStats().quitarStamina(this.clazz().getEsfuerzoExcavar());
		if (suerteMineria()) {
			if (getFlags().TargetObj == 0) {
				return;
			}
			short objid = findObj(getFlags().TargetObj).MineralIndex;
			int cant = this.clazz().getCantMinerales();
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				Map mapa = this.server.getMap(pos().map);
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has extraido algunos minerales!", FontType.FONTTYPE_INFO);
		} else {
			if (getFlags().UltimoMensaje != 9) {
				sendMessage("¡No has conseguido nada!", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 9;
			}
		}
		riseSkill(Skill.SKILL_Mineria);
		getFlags().Trabajando = true;
	}

	private boolean suerteTalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 13, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Talar) / 10)]);
		return (Util.random(1, rango) < 6);
	}

	/**
	 * Talar madera
	 */
	private void cutWood() {
		getStats().quitarStamina(this.clazz().getEsfuerzoTalar());
		if (suerteTalar()) {
			int cant = this.clazz().getCantLeños();
			short objid = Leña;
			Map mapa = this.server.getMap(pos().map);
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has conseguido algo de leña!", FontType.FONTTYPE_INFO);
		} else {
			if (getFlags().UltimoMensaje != 8) {
				sendMessage("No has conseguido leña. Intenta otra vez.", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 8;
			}
		}
		riseSkill(Skill.SKILL_Talar);
		getFlags().Trabajando = true;
	}

	/**
	 * Robar objeto
	 * @param victim
	 */
	private void stealObject(User victim) {
		boolean flag = false;
		short slot = 0;
		if (Util.random(1, 12) < 6) { // Comenzamos por el principio o el final?
			slot = 1;
			while (slot <= MAX_INVENTORY_SLOTS) {
				// Hay objeto en este slot?
				if (victim.userInv.getObject(slot).objid > 0) {
					if (victim.userInv.getObject(slot).esRobable()) {
						if (Util.random(1, 10) < 4) {
							flag = true;
							break;
						}
					}
				}
				slot++;
			}
		} else {
			slot = 20;
			while (slot > 0) {
				// Hay objeto en este slot?
				if (victim.userInv.getObject(slot).objid > 0) {
					if (victim.userInv.getObject(slot).esRobable()) {
						if (Util.random(1, 10) < 4) {
							flag = true;
							break;
						}
					}
				}
				slot--;
			}
		}
		if (flag) {
			// Cantidad al azar
			int cant = Util.random(1, 5);
			short objid = this.userInv.getObject(slot).objid;
			if (cant > this.userInv.getObject(slot).cant) {
				cant = this.userInv.getObject(slot).cant;
			}
			victim.userInv.quitarUserInvItem(slot, cant);
			victim.sendInventorySlot(slot);
			Map mapa = this.server.getMap(pos().map);
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendInventorySlot(slot);
			ObjectInfo info = findObj(objid);
			sendMessage("Has robado " + cant + " " + info.Nombre, FontType.FONTTYPE_INFO);
		} else {
			sendMessage("No has logrado robar un objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private boolean stealLucky() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Robar) / 10)]);
		return (Util.random(1, rango) < 3);
	}

	private void steal(User victima) {
		if (victima.getFlags().isGM() || this.getFlags().isGM()) {
			return;
		}
		
		Map mapa = this.server.getMap(pos().map);
		if (mapa.isSafeMap() || duelStatus(victima) != DuelStatus.DUEL_MISSING) {
			return;
		}
		
		if (stealLucky()) {
			// Exito robo
			if ((Util.random(1, 50) < 25) && (this.clazz == Clazz.Thief)) {
				if (victima.userInv.tieneObjetosRobables()) {
					stealObject(victima);
				} else {
					sendMessage(victima.userName + " no tiene objetos.", FontType.FONTTYPE_INFO);
				}
			} else { // Roba oro
				if (victima.getStats().getGold() > 0) {
					int cantidadRobada = Util.random(1, 100);
					victima.getStats().addGold( -cantidadRobada );
					getStats().addGold( cantidadRobada );
					sendMessage("Le has robado " + cantidadRobada + " monedas de oro a " + victima.userName, FontType.FONTTYPE_INFO);
				} else {
					sendMessage(this.userName + " no tiene oro.", FontType.FONTTYPE_INFO);
				}
			}
		} else {
			sendMessage("¡No has logrado robar nada!", FontType.FONTTYPE_INFO);
			victima.sendMessage("¡" + this.userName + " ha intentado robarte!", FontType.FONTTYPE_INFO);
			victima.sendMessage("¡" + this.userName + " es un criminal!", FontType.FONTTYPE_INFO);
		}
		if (!isCriminal()) {
			turnCriminal();
		}
		if (this.faction.ArmadaReal) {
			this.faction.royalArmyKick();
		}
		this.reputation.incLandron(vlLadron);
		riseSkill(Skill.SKILL_Robar);
	}

	private boolean fishingWithRodLucky() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Pesca) / 10)]);
		return (Util.random(1, rango) < 6);
	}

	/**
	 * Pescar con caña
	 */
	private void fishingWithRod() {
		getStats().quitarStamina(this.clazz().getEsfuerzoPescar());
		if (fishingWithRodLucky()) {
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(OBJ_PESCADO, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(OBJ_PESCADO, 1));
			}
			sendMessage("¡Has pescado un lindo pez!", FontType.FONTTYPE_INFO);
		} else {
			if (getFlags().UltimoMensaje != 6) {
				sendMessage("¡No has pescado nada!", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 6;
			}
		}
		riseSkill(Skill.SKILL_Pesca);
		getFlags().Trabajando = true;
	}

	private boolean fishingWithNetLucky() {
		final short[] suerte = { 60, 54, 49, 43, 38, 32, 27, 21, 16, 11, 11 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Pesca) / 10)]);
		return (Util.random(1, rango) < 6);
	}

	/**
	 * Pescar con red
	 */
	private void fishingWithNet() {
		getStats().quitarStamina(this.clazz().getEsfuerzoPescar());
		int skills = skills().get(Skill.SKILL_Pesca);
		if (skills == 0) {
			return;
		}
		if (fishingWithNetLucky()) {
			int cant = (this.clazz == Clazz.Fisher) ? Util.random(1, 5) : 1;
			short objid = (short) PESCADOS_RED[Util.random(1, PESCADOS_RED.length) - 1];
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				Map mapa = this.server.getMap(pos().map);
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has pescado algunos peces!", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("¡No has pescado nada!", FontType.FONTTYPE_INFO);
		}
		riseSkill(Skill.SKILL_Pesca);
	}

	public void workLeftClick(byte x, byte y, byte skill) {
		Pos pos = new Pos(x, y);
		if (!isAlive() || getFlags().Descansar || getFlags().Meditando || !pos.isValid()) {
			return;
		}
		if (!pos().inRangoVision(pos)) {
			sendPositionUpdate();
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		MapObject obj = null;

		if (skill == Skill.SKILL_FundirMetal) {
			// UGLY HACK!!! This is a constant, not a skill!!
			mapa.lookAtTile(this, x, y);
			if (getFlags().TargetObj > 0) {
				if (findObj(getFlags().TargetObj).objType == ObjType.Fragua) {
					meltOre();
				} else {
					sendMessage("Ahi no hay ninguna fragua.", FontType.FONTTYPE_INFO);
				}
			} else {
				sendMessage("Ahi no hay ninguna fragua.", FontType.FONTTYPE_INFO);
			}
		} else {
			switch (Skill.value(skill)) {
			case SKILL_Proyectiles:
				// Nos aseguramos que este usando un arma de proyectiles
				if (!this.userInv.tieneArmaEquipada()) {
					return;
				}
				if (!this.userInv.getArma().esProyectil()) {
					return;
				}
				if (!this.userInv.tieneMunicionEquipada()) {
					sendMessage("No tienes municiones.", FontType.FONTTYPE_INFO);
					return;
				}
				// Quitamos stamina
				if (getStats().stamina >= 10) {
					getStats().quitarStamina(Util.random(1, 10));
				} else {
					if (gender() == UserGender.GENERO_MAN) {
						sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
					} else {
						sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
					}					
					return;
				}

				mapa.lookAtTile(this, x, y);
				User targetUser = this.server.userById(getFlags().TargetUser);
				Npc npc = this.server.npcById(getFlags().TargetNpc);
				if (npc != null) {
					if (!npc.isAttackable()) {
						return;
					}
				} else {
					if (targetUser == null) {
						return;
					}
				}
				if (npc != null) {
					usuarioAtacaNpc(npc);
				}
				if (targetUser != null) {
					if (getFlags().Seguro) {
						if (!targetUser.isCriminal()) {
							sendMessage(
									"No puedes atacar a ciudadanos. Para hacerlo, antes debes desactivar el seguro.",
									FONTTYPE_FIGHT);
							return;
						}
					}
					usuarioAtacaUsuario(targetUser);
				}
				// Consumir munición.
				int slotMunicion = this.userInv.getMunicionSlot();
				this.userInv.quitarUserInvItem(this.userInv.getMunicionSlot(), 1);
				if (this.userInv.getObject(slotMunicion) != null && this.userInv.getObject(slotMunicion).cant > 0) {
					this.userInv.equipar(slotMunicion);
				}
				sendInventorySlot(slotMunicion);
				break;


			case SKILL_Magia:
				if (!getCounters().intervaloPermiteLanzarSpell()) {
					return;
				}
				if (getFlags().isCounselor())
					return;
				mapa.lookAtTile(this, x, y);
				if (getFlags().Hechizo > 0) {
					this.spells.hitSpell(this.server.getSpell(getFlags().Hechizo));
					getFlags().Hechizo = 0;
				} else {
					sendMessage("¡Primero selecciona el hechizo que deseas lanzar!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Pesca:
				if (!getCounters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					sendMessage("Deberías equiparte una caña o una red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (this.userInv.getArma().ObjIndex != OBJ_INDEX_CAÑA
						&& this.userInv.getArma().ObjIndex != OBJ_INDEX_RED_PESCA) {
					sendMessage("Deberías equiparte una caña o una red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isUnderRoof(pos().x,  pos().y)) {
					sendMessage("No puedes pescar desde dónde te encuentras.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isWater(x, y)) {
					sendWave(SOUND_PESCAR);
					switch (this.userInv.getArma().ObjIndex) {
					case OBJ_INDEX_CAÑA:
						fishingWithRod();
						break;
					case OBJ_INDEX_RED_PESCA:
						if (pos().distance(MapPos.mxy(pos().map, x, y)) > 2) {
							sendMessage("Estás demasiado lejos para pescar.", FontType.FONTTYPE_INFO);
							return;
						}
						fishingWithNet();
						break;
					}
				} else {
					sendMessage("No hay agua donde pescar. Busca un lago, río o mar.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Robar:
				if (!mapa.isSafeMap()) {
					if (!getCounters().intervaloPermiteTrabajar()) {
						return;
					}
					mapa.lookAtTile(this, x, y);
					if (getFlags().TargetUser > 0 && getFlags().TargetUser != getId()) {
						targetUser = this.server.userById(getFlags().TargetUser);
						if (targetUser.isAlive()) {
							MapPos wpaux = MapPos.mxy(pos().map, x, y);
							if (wpaux.distance(pos()) > 2) {
								sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
								return;
							}
							// Nos aseguramos que el trigger le permite robar
							if (mapa.isSafeZone(targetUser.pos().x, targetUser.pos().y)) {
								sendMessage("No puedes robar aquí.", FontType.FONTTYPE_WARNING);
								return;
							}
							steal(targetUser);
						}
					} else {
						sendMessage("¡No hay a quién robarle!", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("¡No puedes robar en zonas seguras!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Talar:
				if (!getCounters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					sendMessage("Deberías equiparte el hacha de leñador.", FontType.FONTTYPE_INFO);
					return;
				}
				if (this.userInv.getArma().ObjIndex != HACHA_LEÑADOR) {
					sendMessage("Deberías equiparte el hacha de leñador.", FontType.FONTTYPE_INFO);
					return;
				}
				obj = mapa.getObject(x, y);
				if (obj != null) {
					MapPos wpaux = MapPos.mxy(pos().map, x, y);
					if (wpaux.distance(pos()) > 2) {
						sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// ¿Hay un arbol donde cliqueo?
					if (obj.objInfo().objType == ObjType.Arboles) {
						sendWave(SOUND_TALAR);
						cutWood();
					}
				} else {
					sendMessage("No hay ningún árbol ahí.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Mineria:
				if (!getCounters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					return;
				}
				if (this.userInv.getArma().ObjIndex != PIQUETE_MINERO) {
					quitGame();
					return;
				}
				mapa.lookAtTile(this, x, y);
				obj = mapa.getObject(x, y);
				if (obj != null) {
					MapPos wpaux = MapPos.mxy(pos().map, x, y);
					if (wpaux.distance(pos()) > 2) {
						sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// ¿Hay un yacimiento donde cliqueo?
					if (obj.objInfo().objType == ObjType.Yacimiento) {
						sendWave(SOUND_MINERO);
						mining();
					} else {
						sendMessage("Ahí no hay ningún yacimiento.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ahí no hay ningún yacimiento.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Domar:
				mapa.lookAtTile(this, x, y);
				if (getFlags().TargetNpc > 0) {
					npc = this.server.npcById(getFlags().TargetNpc);
					if (npc.domable() > 0) {
						MapPos wpaux = MapPos.mxy(pos().map, x, y);
						if (wpaux.distance(pos()) > 2) {
							sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
							return;
						}
						if (npc.isAttackedByUser()) {
							sendMessage("No puedes domar una criatura que está luchando con un jugador.", FontType.FONTTYPE_INFO);
							return;
						}
						tameCreature(npc);
					} else {
						sendMessage("No puedes domar a esa criatura.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("¡No hay ninguna criatura alli!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Herreria:
				mapa.lookAtTile(this, x, y);
				if (getFlags().TargetObj > 0) {
					if (findObj(getFlags().TargetObj).objType == ObjType.Yunque) {
						server.getWork().sendBlacksmithWeapons(this);
						server.getWork().sendBlacksmithArmors(this);
						sendPacket(new ShowBlacksmithFormResponse());
					} else {
						sendMessage("Ahí no hay ningún yunque.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ahí no hay ningún yunque.", FontType.FONTTYPE_INFO);
				}
				break;
				
			default:
				break;
			}
		}

	}

	public void handleWork(byte skill) {
		// Comando UK
		if (!checkAlive()) {
			return;
		}

		switch (Skill.value(skill)) {
		case SKILL_Robar:
			sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Robar.value()));
			break;

		case SKILL_Magia:
			sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Magia.value()));
			break;

		case SKILL_Domar:
			sendPacket(new WorkRequestTargetResponse(Skill.SKILL_Domar.value()));
			break;

		case SKILL_Ocultarse:
			if (getFlags().Navegando) {
				if (getFlags().UltimoMensaje != 3) {
					sendMessage("No puedes ocultarte si estas navegando.", FontType.FONTTYPE_INFO);
					getFlags().UltimoMensaje = 3;
				}
				return;
			}
			if (getFlags().Oculto) {
				if (getFlags().UltimoMensaje != 2) {
					sendMessage("Ya estabas oculto.", FontType.FONTTYPE_INFO);
					getFlags().UltimoMensaje = 2;
				}
				return;
			}
			tryHiding();
			break;
			
		default:
			break;
		}
	}

	public void castSpell(short slot) {
		// Comando LH
		// Fixed by agush
		if (checkAlive()) {
			this.spells.castSpell(slot);
			handleWork(Skill.SKILL_Magia.value());
		}
	}

	public void showInformation() {
		// Comando /INFORMACION
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_INFORMATION);
		if (npc == null) {
			return;
		}
		if (!npc.isFaction()) {
			if (!this.faction.ArmadaReal) {
				talk(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			talk(COLOR_BLANCO,
					"Tu deber es combatir criminales, cada 100 criminales que derrotes te dare una recompensa.",
					npc.getId());
		} else {
			if (!this.faction.FuerzasCaos) {
				talk(COLOR_BLANCO, "No perteneces a las fuerzas del caos!!!", npc.getId());
				return;
			}
			talk(COLOR_BLANCO,
					"Tu deber es sembrar el caos y la desesperanza, cada 100 ciudadanos que derrotes te dare una recompensa.",
					npc.getId());
		}
	}

	public void meditate() { // oooommmmmmm
		// Comando /MEDITAR
		if (!checkAlive("¡¡Estas muerto!! Solo los vivos pueden meditar.")) {
			return;
		}
		sendPacket(new MeditateToggleResponse());
		if (!getFlags().Meditando) {
			sendMessage("Comienzas a meditar.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Dejas de meditar.", FontType.FONTTYPE_INFO);
		}
		getFlags().Meditando = !getFlags().Meditando;
		if (getFlags().Meditando) {
			this.getCounters().tInicioMeditar = System.currentTimeMillis();
			int segs = (TIEMPO_INICIO_MEDITAR / 1000);
			sendMessage("Te estás concentrando. En " + segs + " segundos comenzarás a meditar.", FontType.FONTTYPE_INFO);
			this.infoChar.loops = LoopAdEternum;
			if (getStats().ELV < 15) {
				sendCreateFX(FXMEDITARCHICO, LoopAdEternum);
				this.infoChar.fx = FXMEDITARCHICO;
			} else if (getStats().ELV < 30) {
				sendCreateFX(FXMEDITARMEDIANO, LoopAdEternum);
				this.infoChar.fx = FXMEDITARMEDIANO;
			} else {
				sendCreateFX(FXMEDITARGRANDE, LoopAdEternum);
				this.infoChar.fx = FXMEDITARGRANDE;
			}
		} else {
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
			sendCreateFX(0, 0);
		}
	}

	public void resuscitate() {
		// Comando /RESUCITAR
		Npc npc = getNearNpcSelected(DISTANCE_PRIEST);
		if (npc == null) {
			return;
		}
		if (!npc.isPriest() && !npc.isPriestNewbies()) {
			sendMessage("No poseo el poder de revivir a otros, mejor encuentra un sacerdote.", FontType.FONTTYPE_INFO);
			return;
		}
		if (npc.isPriestNewbies() && !isNewbie()) {
			sendMessage("Lo siento, sólo puedo resucitar newbies.", FontType.FONTTYPE_INFO);
			return;
		}

		if (isAlive()) {
			sendMessage("¡JA! Debes estar muerto para resucitarte.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!userExists()) {
			sendMessage("!!El personaje no existe, cree uno nuevo.", FontType.FONTTYPE_INFO);
			quitGame();
			return;
		}
		revive();
		sendMessage("¡¡Has sido resucitado!!", FontType.FONTTYPE_INFO);
	}

	public void heal() {
		// Comando /CURAR
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_PRIEST);
		if (npc == null) {
			return;
		}
		if (!npc.isPriest() && !npc.isPriestNewbies()) {
			sendMessage("No poseo el poder para curar a otros, mejor encuentra un sacerdote.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!checkAlive("¡Solo puedo curar a los vivos! ¡Resucítate primero!")) {
			return;
		}
		getStats().MinHP = getStats().MaxHP;
		sendUpdateUserStats();
		sendMessage("¡¡Has sido curado!!", FontType.FONTTYPE_INFO);
	}

	public void changeDescription(String s) {
		// Comando "/DESC "
		s = s.trim();
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		if (!Util.isValidAscii(s)) {
			sendMessage("La descripcion tiene caracteres invalidos.", FontType.FONTTYPE_INFO);
			return;
		}
		this.description = s;
		sendMessage("La descripcion a cambiado.", FontType.FONTTYPE_INFO);
	}
	
	public void changeCharDescription(String newDescRM) {
		// Comando "/SETDESC "
        if (isGod() || isAdmin() || isRoleMaster()) {
        	User targetUser = server.userById(getFlags().TargetUser);
        	if (targetUser != null) {
                targetUser.descRM = newDescRM;
        	} else {
        		sendMessage("Haz click sobre un personaje antes!", FontType.FONTTYPE_INFO);
        	}
        }
	}

	public void changeHeading(byte newHeading) {
		if (newHeading < 1 || newHeading > 4) {
	    	return;
	    }
		
		if (getFlags().Inmovilizado) {
			return;
		}
		
		int posX = 0;
		int posY = 0;
		Heading heading = Heading.value(newHeading);
        if (getFlags().Paralizado && !getFlags().Inmovilizado) {
        	switch (heading) {
	        case NORTH:
                posY = -1;
                break;
            case EAST:
                posX = 1;
                break;
            case SOUTH:
                posY = 1;
                break;
            case WEST:
                posX = -1;
                break;
			default:
				break;
        	}
        	Map map = server.getMap(pos().map);
        	MapPos pos = MapPos.mxy(pos().map, pos().x + posX, pos().y + posY);
            if (map.isLegalPos(pos, isSailing(), !isSailing())) {   
                return;
            }
        }
        
	    this.infoChar.heading = Heading.value(newHeading);
        sendCharacterChange();
	}

	public void sendCharacterChange() {
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		mapa.sendToArea(pos().x, pos().y, characterChange());
	}

	public void quitGame() {
		if (this.m_saliendo) {
			return;
		}
		this.m_saliendo = true;
		log.info("saliendo: " + this.getUserName());
		boolean wasLogged = getFlags().UserLogged;
		try {
			Map mapa = this.server.getMap(pos().map);
			if (mapa != null && mapa.hasUser(this)) {
				getUserPets().removeInvocationPets();				
				for (Npc pet: getUserPets().getPets()) {
					mapa.exitNpc(pet);
				}
				mapa.exitMap(this);
			}
			if (wasLogged) {
				if (this.server.isRaining()) {
					// Detener la lluvia.
					sendPacket(new RainToggleResponse());
				}
				sendPacket(new DisconnectResponse());
			}
			getFlags().UserLogged = false;
		} catch (Exception ex) {
			log.fatal("ERROR EN doSALIR(): ", ex);
		} finally {
			this.server.dropUser(this);
			if (wasLogged) {
				try {
					saveUser();
					// remove pets AFTER persit'em
					getUserPets().removeAll();
				} catch (Exception ex) {
					log.fatal("ERROR EN doSALIR() - saveUser(): ", ex);
				}
			}
		}
		log.info("Salió: " + getUserName());
		server.getWorkWatcher().userLogout(this);
	}

	public void throwDices() { // and get lucky!
		// FIXME dados fáciles, hacerlo configurable
		getStats().attr().set(Attribute.STRENGTH, Util.random(16, 18));
		getStats().attr().set(Attribute.AGILITY, Util.random(16, 18));
		getStats().attr().set(Attribute.INTELIGENCE, Util.random(16, 18));
		getStats().attr().set(Attribute.CHARISMA, Util.random(16, 18));
		getStats().attr().set(Attribute.CONSTITUTION, Util.random(16, 18));

		sendPacket(new DiceRollResponse(
			getStats().attr().get(Attribute.STRENGTH),
			getStats().attr().get(Attribute.AGILITY),
			getStats().attr().get(Attribute.INTELIGENCE),
			getStats().attr().get(Attribute.CHARISMA),
			getStats().attr().get(Attribute.CONSTITUTION)));
	}

	/**
	 * Procesa el clic izquierdo del mouse sobre el mapa
	 *
	 * @param x es la posición x del clic
	 * @param y es la posición y del clic
	 */
	public void leftClickOnMap(byte x, byte y) {
		// Clic con el botón primario del mouse
		Map map = this.server.getMap(pos().map);
		if (map != null) {
			map.lookAtTile(this, x, y);
		}
	}

	/**
	 * Procesa el clic derecho del mouse sobre el mapa
	 *
	 * @param x es la posición x del clic
	 * @param y es la posición y del clic
	 */
	public void doubleClickOnMap(byte x, byte y) {
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		// ¿Posicion valida?
		if (Pos.isValid(x, y)) {
			// ¿Hay un objeto en el tile?
			if (mapa.hasObject(x, y)) {
				MapObject obj = mapa.getObject(x, y);
				getFlags().TargetObj = obj.objInfo().ObjIndex;
				switch (obj.objInfo().objType) {
				case Puertas:
					mapa.accionParaPuerta(x, y, this);
					break;
				case Carteles:
					mapa.accionParaCartel(x, y, this);
					break;
				case Foros:
					mapa.accionParaForo(x, y, this);
					break;
				case Leña:
					if (getFlags().TargetObj == FOGATA_APAG) {
						mapa.accionParaRamita(x, y, this);
					}
					break;
				default:
					break;
				}
			} else {
				// ¿Hay un objeto que ocupa más de un tile?
				MapObject obj = mapa.lookForNearbyObject(x, y);
				if (obj != null) {
					getFlags().TargetObj = obj.objInfo().ObjIndex;
					if (obj.objInfo().objType == ObjType.Puertas) {
						mapa.accionParaPuerta(obj.x, obj.y, this);
						return;
					}
				} 
				
				// Hay un npc?
				Npc npc;
				if ((npc = mapa.getNPC(x, y)) != null) {
					getFlags().TargetNpc = npc.getId();
					if (npc.isTrade()) {
						// Doble clic sobre un comerciante, hace /COMERCIAR
						this.userTrade.commerceStart();
					} else if (npc.isBankCashier()) {
						if (!checkAlive()) {
							return;
						}
						// Extensión de AOJ - 16/08/2004
						// Doble clic sobre el banquero hace /BOVEDA
						if (checkNpcNear(npc, DISTANCE_CASHIER)) {
							getBankInventory().iniciarDeposito();
						}
					} else if (npc.isPriest() || npc.isPriestNewbies()) {
						// Extensión de AOJ - 01/02/2007
						// Doble clic sobre el sacerdote hace /RESUCITAR o /CURAR
						if (isAlive()) {
							heal();
						} else {
							resuscitate();
						}
					}
				} else {
					getFlags().TargetNpc = 0;
					getFlags().TargetNpcTipo = 0;
					getFlags().TargetUser = 0;
					getFlags().TargetObj = 0;
					sendMessage("No ves nada interesante.", FontType.FONTTYPE_INFO);
				}
			}
		}
	}

	/** Comando para hablar (;) */
	public void talk(String chat) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (chat.length() > MAX_MENSAJE) {
			chat = chat.substring(0, MAX_MENSAJE);
		}
		if (chat.trim().isEmpty()) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa != null) {
			
	        if ( !getFlags().AdminInvisible ) {
	        	if (isAlive()) {
	        		mapa.sendToArea(pos().x, pos().y,
	        				new ChatOverHeadResponse(chat, getId(),
	        						Color.r(chatColor), 
	        						Color.g(chatColor), 
	        						Color.b(chatColor)));
	        	} else {
	        		mapa.sendToArea(pos().x, pos().y,
	        				new ChatOverHeadResponse(chat, getId(),
	        						Color.r(Color.CHAT_COLOR_DEAD_CHAR), 
	        						Color.g(Color.CHAT_COLOR_DEAD_CHAR), 
	        						Color.b(Color.CHAT_COLOR_DEAD_CHAR)));
	        	}
	        } else {
        		mapa.sendToArea(pos().x, pos().y,
        				new ConsoleMsgResponse("GM> " + chat, FontType.FONTTYPE_GM.id()));
	        }
			
		}
		if (getFlags().isCounselor()) {
			Log.logGM(this.userName, "El consejero dijo: " + chat);
		}
	}

	/** Comando para gritar (-) */
	public void yell(String text) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (text.length() > MAX_MENSAJE) {
			text = text.substring(0, MAX_MENSAJE);
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa != null) {
			mapa.sendToArea(pos().x, pos().y,
				new ChatOverHeadResponse(text, getId(),
						Color.r(Color.COLOR_ROJO), Color.g(Color.COLOR_ROJO), Color.b(Color.COLOR_ROJO)));
		}
		if (getFlags().isCounselor()) {
			Log.logGM(this.userName, "El consejero gritó: " + text);
		}
	}

	/** Comando para susurrarle al oido a un usuario (\) */
	public void whisper(short targetIndex, String text) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (text.length() > MAX_MENSAJE) {
			text = text.substring(0, MAX_MENSAJE);
		}

		Map map = this.server.getMap(pos().map);
		if (map == null) {
			return;
		}
		User targetUser = this.server.userById(targetIndex);
		if (targetUser != null) {
			if (map.lookForUserAtArea(pos().x, pos().y, targetUser.getId()) == null) {
				sendMessage("Estas muy lejos de " + targetUser.getUserName(), FontType.FONTTYPE_INFO);
			} else {
				if (getFlags().isCounselor()) {
					Log.logGM(this.userName, "El consejero le susurró a " + targetUser.getUserName() + ": " + text);
				}

				// send to target user
				targetUser.sendPacket(new ChatOverHeadResponse(text, getId(),
								Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));
				// send to source user
				sendPacket(new ChatOverHeadResponse(text, getId(),
						Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));

				if (!getFlags().isGM() || getFlags().isCounselor()) {
					// send to admins at area
					map.sendToAreaToAdminsButCounselor(pos().x, pos().y,
							new ChatOverHeadResponse("a " + targetUser.getUserName() + "> " + text, this.getId(),
									Color.r(Color.COLOR_AMARILLO), Color.g(Color.COLOR_AMARILLO), Color.b(Color.COLOR_AMARILLO)));
				}
			}
		} else {
			sendMessage("Usuario inexistente.", FontType.FONTTYPE_INFO);
		}
	}

	public void walk(Heading heading) {
		try {
			this.speedHackMover.check();
		} catch (SpeedHackException e) {
			sendMessage(e.getMessage(), FontType.FONTTYPE_SERVER);
			quitGame();
			return;
		}

		if (this.getCounters().Saliendo) {
			sendMessage("/SALIR cancelado!.", FontType.FONTTYPE_INFO);
			this.getCounters().Saliendo = false;
		}

		if (getFlags().Paralizado) {
			if (getFlags().UltimoMensaje != 1) {
				sendMessage("No puedes moverte por estar paralizado.", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 1;
			}
			return;
		}
		if (getFlags().Descansar) {
			getFlags().Descansar = false;
			sendPacket(new RestOKResponse());
			sendMessage("Has dejado de descansar.", FontType.FONTTYPE_INFO);
		}
		if (getFlags().Meditando) {
			getFlags().Meditando = false;
			sendPacket(new MeditateToggleResponse());
			sendMessage("Dejas de meditar.", FontType.FONTTYPE_INFO);
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
			sendCreateFX(0, 0);
		}
		if (getFlags().Oculto) {
			if (this.clazz != Clazz.Thief) {
				turnVisible();
			}
		}
		move(heading);
		getFlags().Trabajando = false;
	}

	private void turnVisible() {
		sendMessage("¡Has vuelto a ser visible!", FontType.FONTTYPE_INFO);
		getFlags().Oculto = false;
		getFlags().Invisible = false;
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)0));
	}

	/**
	 * Move user in the heading direction.
	 * If newPos has a "casper" (ghost or died user), both positions are swapped.
	 * Invisible Admins can't move over caspers.
	 * @param heading
	 */
	private void move(Heading heading) {
		User casper;
		MapPos oldPos = pos().copy();
		Map map = this.server.getMap(pos().map);
		if (map == null) {
			return;
		}

		infoChar().setHeading(heading);
		MapPos newPos = pos().copy().moveToHeading(heading);
		if ( map.isLegalPos(newPos, isSailing(), !isSailing())) {
			casper = map.getUser(newPos.x, newPos.y);
			if (casper != null) {
				if (getFlags().AdminInvisible) {
					// los admins invisibles no pueden patear caspers
					// user can't move
					sendPositionUpdate();
					return;
				} 
				Heading casperHeading = heading.invertHeading();
            	casper.infoChar().setHeading(casperHeading);
				map.moveUserSwapping(this, newPos, casper);
			} else {
				map.moveUser(this, newPos);
			}

			// TELEPORT USER
			if (map.isTeleport(newPos.x, newPos.y)) {
				// Esto es similar al DoTileEvents original
				MapPos targetPos	= map.teleportTarget(newPos.x, newPos.y);
				boolean withFX 		= map.isTeleportObject(newPos.x, newPos.y);
				boolean sendingData = pos().map != targetPos.map;
				if (!enterIntoMap(targetPos.map, targetPos.x, targetPos.y, withFX, sendingData)) {
					// if fails, go back to the closest original position
					MapPos warpPos = map.closestLegalPosUser(oldPos.x, oldPos.y, getFlags().Navegando, getFlags().isGM());
					warpMe(warpPos.map, warpPos.x, warpPos.y, true);
				}
			}
		} else {
			// user can't move
			sendPositionUpdate();
		}
	}

	public void attack() {
		if (!checkAlive("¡¡No puedes atacar a nadie por estar muerto!!")) {
			return;
		}
		if (getFlags().isCounselor()) {
			sendMessage("¡¡No puedes atacar a nadie!!", FontType.FONTTYPE_INFO);
			return;
		} else {
			if (this.userInv.tieneArmaEquipada()) {
				if (this.userInv.getArma().esProyectil()) {
					sendMessage("No podés usar asi esta arma.", FontType.FONTTYPE_INFO);
					return;
				}
			}
			usuarioAtaca();
			// piedra libre para todos los compas!
			if (isHidden()) {
				turnVisible();
			}
		}
	}

	public void sendPositionUpdate() {
		sendPacket(new PosUpdateResponse(pos().x, pos().y));
	}

	public void pickUpObject() {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden recoger objetos.")) {
			return;
		}
		if (getFlags().isCounselor()) {
			sendMessage("No puedes recoger ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		// ¿Hay algun obj?
		if (mapa.hasObject(pos().x, pos().y)) {
			// ¿Esta permitido agarrar este obj?
			MapObject obj = mapa.getObject(pos().x, pos().y);
			if (!obj.objInfo().esAgarrable()) {
				sendMessage("El objeto no se puede agarrar.", FontType.FONTTYPE_INFO);
			} else {
				int agregados = this.userInv.agregarItem(obj.obj_ind, obj.obj_cant);
				if (agregados < obj.obj_cant) {
					mapa.removeObject(pos().x, pos().y);
					mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(obj.obj_ind, obj.obj_cant - agregados));
				} else {
					// Quitamos el objeto
					mapa.removeObject(pos().x, pos().y);
					if (getFlags().isGM()) {
						Log.logGM(this.userName, "Agarró: " + obj.obj_ind + " objeto=" + obj.objInfo().Nombre);
					}
				}
			}
		} else {
			sendMessage("No hay nada aqui.", FontType.FONTTYPE_INFO);
		}
	}

	public void dropObject(byte slot, int cant) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden tirar objetos.")) {
			return;
		}
		if (isSailing()) {
			sendMessage("No puedes tirar objetos mientras navegas.", FontType.FONTTYPE_INFO);
			return;
		}
		if (getFlags().isCounselor()) {
			sendMessage("No puedes tirar ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		if (slot == FLAGORO) {
			dropGold(cant);
			sendUpdateUserStats();
		} else {
			if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
				if (this.userInv.getObject(slot) != null && this.userInv.getObject(slot).objid > 0) {
					this.userInv.dropObj(slot, cant);
				}
			}
		}
	}

	public void equipItem(short slot) {
		// Comando EQUI
		if (!checkAlive("¡¡Estas muerto!! Solo puedes usar items cuando estas vivo.")) {
			return;
		}
		if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
			if (this.userInv.getObject(slot) != null && this.userInv.getObject(slot).objid > 0) {
				this.userInv.equipar(slot); // EquiparInvItem
			}
		}
	}

	public void sendInventoryToUser() {
		// updateUserInv
		for (int i = 1; i <= this.userInv.getSize(); i++) {
			sendInventorySlot(i);
		}
	}

	public void sendInventorySlot(int slot) {
		InventoryObject inv = this.userInv.getObject(slot);

		ObjectInfo objInfo;
		if (inv != null && inv.objid != 0) {
			objInfo = findObj(inv.objid);
		} else {
			objInfo = ObjectInfo.EMPTY;
		}
		sendPacket(new ChangeInventorySlotResponse(
				(byte) slot,
				objInfo.ObjIndex,
				objInfo.Nombre,
				(short) (inv != null ? inv.cant : 0),
				(byte) (inv != null && inv.equipado ? 1 : 0),
				objInfo.GrhIndex,
				objInfo.objType == null ? 0 : objInfo.objType.value(),
				objInfo.MaxHIT,
				objInfo.MinHIT,
				objInfo.MaxDef,
				Float.valueOf(objInfo.Valor)));
	}

	private void sendUserIndexInServer() {
		sendPacket(new UserIndexInServerResponse(getId()));
	}

	private void sendCharIndexInServer() {
		sendPacket(new UserCharIndexInServerResponse(getId()));
	}

	public void sendMessage(String msg, FontType fuente) {
		sendPacket(new ConsoleMsgResponse(msg, fuente.id()));
	}

	public void sendTalk(int color, String msg, short id) {
		sendPacket(new ChatOverHeadResponse(msg, id, Color.r(color), Color.g(color), Color.b(color)));
	}

	public void sendUpdateUserStats() {
		sendPacket(new UpdateUserStatsResponse(
				(short) getStats().MaxHP,
				(short) getStats().MinHP,
				(short) getStats().maxMana,
				(short) getStats().mana,
				(short) getStats().maxStamina,
				(short) getStats().stamina,
				getStats().getGold(),
				(byte)getStats().ELV, // Current Level
				getStats().ELU, // Experience to Level Up
				getStats().Exp)); // Current Experience
	}
	
	public void sendUpdateHungerAndThirst() {
		sendPacket(new UpdateHungerAndThirstResponse(
				(byte) getStats().maxDrinked,
				(byte) getStats().drinked,
				(byte) getStats().maxEaten,
				(byte) getStats().eaten));
	}
	
	public void sendUpdateHP(short minHP) {
		sendPacket(new UpdateHPResponse(minHP));
	}


	private boolean enterIntoMap(short mapNumber, byte x, byte y, boolean withFX, boolean sendingData) {
		if (this.m_saliendo) {
			return false;
		}
		MapPos originalPos = pos().copy();

		// exit from the old map
		if (pos().map != 0) {
			Map oldMap = this.server.getMap(pos().map);
			if (oldMap == null) {
				return false;
			}
			if (oldMap.hasUser(this) && !oldMap.exitMap(this)) {
				// it fails to exit map
				log.fatal(this.getUserName() + " no pudo salir del mapa actual");
			}
		}

		Map targetMap = this.server.getMap(mapNumber);
		if (targetMap == null) {
			log.warn("No existe el mapa " + mapNumber);
			return false;
		}

		if (targetMap.isForbbidenMap(this)) {
			return false;
		}

		MapPos freePos = targetMap.closestLegalPosUser(x, y, getFlags().Navegando, getFlags().isGM());

		// enter into the new map
		if (!targetMap.enterMap(this, freePos.x, freePos.y)) {
			sendMessage("No se pudo entrar en el mapa deseado", FontType.FONTTYPE_WARNING);
			return false;
		}

		pos().set(mapNumber, freePos.x, freePos.y);
		if (sendingData) {
			sendPacket(new ChangeMapResponse(mapNumber, targetMap.getVersion()));
		}
		Map map = server.getMap(pos().map);
		AreasAO.instance().loadUser(map, this);
		sendPositionUpdate();

		if (withFX) {
			sendCreateFX(1, 0);
			if (getFlags().UserLogged) { // No hacer sonido en el LOGIN.
				sendWave(SOUND_WARP);
			}
		}

		sendCharIndexInServer();

		if (originalPos.map != mapNumber) {
			warpPets();
		}

		return true;
	}

	public void sendCreateFX(int fx, int val) {
		Map m = this.server.getMap(pos().map);
		if (m == null) {
			return;
		}
		m.sendCreateFX(pos().x, pos().y, getId(), fx, val);
	}

	public boolean isParalized() {
		return getFlags().Paralizado;
	}

	public void revive() {
		if (this.getCounters().Saliendo) {
			return;
		}
		Map m = this.server.getMap(pos().map);
		if (m == null) {
			return;
		}
		getFlags().Muerto = false;
		getStats().MinHP = getStats().MaxHP;// 10;
		if (getStats().drinked <= 0) {
			getStats().drinked = 10;
		}
		if (getStats().eaten <= 0) {
			getStats().eaten = 10;
		}
		this.infoChar.head = this.origChar.head;
		undress();
		sendCharacterChange();
		sendUpdateUserStats();
		sendUpdateHungerAndThirst();
	}

	public boolean warpMe(short targetMap, byte x, byte y, boolean withFX) {
		// WarpUserChar
		// FIXME
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new RemoveCharDialogResponse(getId()));
		sendPacket(new RemoveAllDialogsResponse());
		
		// quitamos al char del mapa
		map.exitMap(this); // FIXME revisar
		
		boolean changingMap = pos().map != targetMap;
		
		Map newMap = this.server.getMap(targetMap);

		// Si el mapa destino es distinto al actual
		if (changingMap) {
			sendPacket(new ChangeMapResponse(targetMap, newMap.getVersion()));
			sendPacket(new PlayMidiResponse((byte) newMap.getMusic(), (short)45));
		}
		
		MapPos freePos = newMap.closestLegalPosUser(x, y, getFlags().Navegando, getFlags().isGM());
		if (freePos == null) {
			log.warn("WARPUSER FALLO: no hay un lugar libre cerca de mapa=" + targetMap + " x=" + x + " y=" + y);
			return false;
		}
		
		newMap.enterMap(this, freePos.x, freePos.y);
		AreasAO.instance().loadUser(newMap, this);
		sendPacket(new UserCharIndexInServerResponse(getId()));
		
		// Seguis invisible al pasar de mapa
		if ((getFlags().Invisible || getFlags().Oculto) && !getFlags().AdminInvisible) {
			map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		}
		
		if (withFX && !getFlags().AdminInvisible) {
			sendWave(SOUND_WARP);
			sendCreateFX(FXWARP, 0);
		}
		
		warpPets();
		
		// Automatic toggle navigate
//		if (!flags().isGM() && flags().isCounselor()) {
//			if (newMap.isWater(x, y)) {
//				if (!isSailing()) {
//					flags().Navegando = true;
//					sendPacket(new NavigateToggleResponse());
//				}
//			} else {
//				if (isSailing()) {
//					flags().Navegando = false;
//					sendPacket(new NavigateToggleResponse());
//				}
//			}
//		}

		return true;
	}

	private void warpPets() {
		// copy list first
		var pets = getUserPets().getPets().stream().collect(Collectors.toList());

		pets.forEach(pet -> {
			if (pet.counters().TiempoExistencia > 0) {
				// Es una mascota de invocación. Se pierde al cambiar de mapa
				getUserPets().removePet(pet);
			} else if (pet.canReSpawn()) {
				// Es una mascota domada que puede hacer respawn

				Map oldMapa = this.server.getMap(pet.pos().map);
				Map newMapa = this.server.getMap(pos().map);
				MapPos lugarLibre = newMapa.closestLegalPosNpc(pos().x, pos().y, pet.isWaterValid(), pet.isLandInvalid(), true);

				if (lugarLibre != null) {
					// La mascota lo sigue al nuevo mapa, y mantiene su control.
					if (oldMapa != null) {
						oldMapa.exitNpc(pet);
					}
					// No se permiten mascotas en zonas seguras, esperan afuera.
					if (!newMapa.isSafeMap()) {
						newMapa.enterNpc(pet, lugarLibre.x, lugarLibre.y);
					} else {
						pet.pos().reset();
					}
				} else {
					// La mascota no puede seguirlo al nuevo mapa, asi que pierde su control.
					getUserPets().removePet(pet);
				}
			} else {
				// La mascota no puede seguirlo al nuevo mapa, asi que pierde su control.
				getUserPets().removePet(pet);
			}
		});

		if (pets.size() < getUserPets().getPets().size()) {
			sendMessage("Pierdes el control de tus invocaciones.", FontType.FONTTYPE_INFO);
		}
		
		boolean petsWaitingOutside = false;
		for (Npc pet: getUserPets().getPets()) {
			if (pet.pos().isEmpty()) {
				petsWaitingOutside = true;
				break;
			}
		}
		if (petsWaitingOutside) {
			sendMessage("No se permiten mascotas en zonas seguras. Te esperarán afuera.", FontType.FONTTYPE_INFO);
		}
	}

	public void sendToJail(int jailTimeMinutes, String adminName) {
		this.getCounters().Pena = jailTimeMinutes;
		if (warpMe(WP_PRISION.map, WP_PRISION.x, WP_PRISION.y, true)) {
			if (adminName == null) {
				sendMessage("Has sido encarcelado. Permanecerás en la carcel " + jailTimeMinutes + " minutos.",
						FontType.FONTTYPE_INFO);
			} else {
				sendMessage(adminName + " te ha encarcelado. Permanecerás en la carcel " + jailTimeMinutes + " minutos.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void releaseFromJail() {
		getCounters().Pena = 0;
		warpMe(WP_LIBERTAD.map, WP_LIBERTAD.x, WP_LIBERTAD.y, true);
		sendMessage("Has sido liberado!", FontType.FONTTYPE_INFO);
	}
	
	public void rainingEffect() {
		if (getFlags().UserLogged) {
			Map mapa = this.server.getMap(pos().map);
			if (this.server.isRaining() && mapa.isOutdoor(pos().x, pos().y) && mapa.getZone() != Zone.DUNGEON) {
				int modifi = Util.percentage(getStats().maxStamina, 3);
				getStats().quitarStamina(modifi);
				sendMessage("¡¡Has perdido stamina, busca pronto refugio de la lluvia!!.", FontType.FONTTYPE_INFO);
				sendUpdateUserStats();
			}
		}
	}

	public void paralizedEffect() {
		if (this.getCounters().Paralisis > 0) {
			this.getCounters().Paralisis--;
		} else {
			getFlags().Paralizado = false;
			getFlags().Inmovilizado = false;
			sendPacket(new ParalizeOKResponse());
		}
	}

	private boolean hidingLucky() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Ocultarse) / 10)]);
		if (this.clazz != Clazz.Thief) {
			rango += 50;
		}
		return (Util.random(1, rango) > 9);
	}

	public void updateHiding() {
		if (hidingLucky()) {
			turnVisible();
		}
	}

	private void turnHiding() {
		getFlags().Oculto = true;
		getFlags().Invisible = true;
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		sendMessage("¡Te has escondido entre las sombras!", FontType.FONTTYPE_INFO);
	}

	public void tryHiding() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Ocultarse) / 10)]);
		if (this.clazz != Clazz.Thief) {
			rango += 50;
		}
		if (Util.random(1, rango) <= 5) {
			turnHiding();
			riseSkill(Skill.SKILL_Ocultarse);
		} else {
			if (getFlags().UltimoMensaje != 4) {
				sendMessage("¡No has logrado esconderte!", FontType.FONTTYPE_INFO);
				getFlags().UltimoMensaje = 4;
			}
		}
		getFlags().Trabajando = true;
	}

	public void sayMagicWords(String magicWords) {
		talk(COLOR_CYAN, magicWords, getId());
	}

	public void talk(int color, String chat, int whoId) {
		if (chat.length() > MAX_TEXTO_HABLAR) {
			chat = chat.substring(0, MAX_TEXTO_HABLAR);
		}

		Map map = this.server.getMap(pos().map);
		if (map != null) {
			map.sendToArea(pos().x, pos().y,
					new ChatOverHeadResponse(chat, (short)whoId,
							Color.r(color), Color.g(color), Color.b(color)));
		}
	}
	
	public void talkAsNpc(String chat) {
		// Solo dioses, admins y RMS
		if (!getFlags().isGM()) {
			return;
		}
		// Asegurarse haya un NPC seleccionado
		Npc targetNpc = this.server.npcById(getFlags().TargetNpc);
		if (targetNpc != null) {
			talk(Color.COLOR_BLANCO, chat, targetNpc.getId());
		} else {
			sendMessage("Debes seleccionar el NPC que quieres que hable", FontType.FONTTYPE_INFO);
		}
	}

	public Npc crateSummonedPet(int npcId, MapPos targetPos) {
		if (getUserPets().isFullPets()) {
			return null;
		}
		Npc npc = Npc.spawnPetNpc(npcId, targetPos, true, this.server);
		if (npc == null) {
			return null;
		}

		getUserPets().addPet(npc);
		npc.setPetUserOwner(this);
		npc.counters().TiempoExistencia = IntervaloInvocacion; // Duración que tendrá la invocación
		npc.setSpellSpawnedPet(true);
		npc.setGiveGLD(0);
		npc.followMaster();
		npc.sendPlayWave(SOUND_WARP);
		npc.sendCreateFX(FXWARP, 0);
		npc.activate();

		return npc;
	}

	public void sendCC() {
		sendPacket(characterCreate());
	}

	public CharacterCreateResponse characterCreate() {
		return new CharacterCreateResponse(
				getId(),
				this.infoChar.getBody(),
				this.infoChar.getHead(),
				this.infoChar.getHeading().value(),

				pos().x, pos().y,
				this.infoChar.getWeapon(),
				this.infoChar.getShield(),
				this.infoChar.getHelmet(),
				this.infoChar.getFx(),
				this.infoChar.loops,
				userNameAndTagForCC(),
				(byte) (this.isCriminal() ? 1 : 0),
				(byte)getFlags().privileges);
	}

	private String userNameAndTagForCC() {
        if (this.showName) {
        	if (getFlags().isGM() && (getFlags().Invisible || getFlags().Oculto)) {
        		return getUserName() + " " + TAG_USER_INVISIBLE;
        	} else {
    			if (getGuildInfo().esMiembroClan()) {
    				return getUserName() + " <" + this.guildUser.getGuildName() + ">";
    			} else {
    				return getUserName();
    			}
        	}
        } else {
            return "";
        }
	}

	public CharacterChangeResponse characterChange() {
		return new CharacterChangeResponse(
				getId(),
				this.infoChar.getBody(),
				this.infoChar.getHead(),
				this.infoChar.getHeading().value(),

				this.infoChar.getWeapon(),
				this.infoChar.getShield(),
				this.infoChar.getHelmet(),
				this.infoChar.getFx(),
				this.infoChar.loops);
	}
	
	private void sendLogged() {
		sendPacket(new LoggedMessageResponse());
	}

	public void sendObject(int objId, int x, int y) {
		short grhIndex = findObj(objId).GrhIndex;
		sendPacket(new ObjectCreateResponse((byte)x, (byte)y, grhIndex));
	}

	public void sendBlockedPosition(int x, int y, boolean blocked) {
		byte bq = 0;
		if (blocked)
			bq = 1;
		sendPacket(new BlockPositionResponse((byte)x, (byte)y, bq));
	}

	public void undress() {
		if (this.getFlags().Mimetizado) {
			this.mimetizadoChar.undress(this.race, this.gender);
		} else {
			this.infoChar.undress(this.race, this.gender);
		}
		getFlags().Desnudo = true;
	}

	public void removePet(Npc pet) {
		if ( !getUserPets().hasPets()) {
			return;
		}

		getUserPets().removePet(pet);
	}

	public void sendWave(int sound) {
	    if (getFlags().AdminInvisible) {
	    	// Los admin invisibles solo producen sonidos a si mismos
	    	sendPacket(new PlayWaveResponse((byte) sound, pos().x, pos().y));
	    
	    } else {
			Map map = this.server.getMap(pos().map);
			if (map != null) {
				map.sendToArea(pos().x, pos().y, new PlayWaveResponse((byte) sound, pos().x, pos().y));
			}
		}
	}

	public void userDie() {
		if (getFlags().isGM()) {
			return;
		}
		Map map = this.server.getMap(pos().map);
		// Sonido
		sendWave(SOUND_USER_DIE);
		// Quitar el dialogo del usuario muerto
		map.sendToArea(pos().x, pos().y, new RemoveCharDialogResponse(getId()));
		
		getStats().MinHP = 0;
		getStats().stamina = 0;
		getFlags().AtacadoPorNpc = 0;
		getFlags().AtacadoPorUser = 0;
		getFlags().Envenenado = false;
		getFlags().Muerto = true;
		
        // SeguroResu: No se activa en arenas
		if (duelStatus(this) != DuelStatus.DUEL_ALLOWED) {
			getFlags().SeguroResu = true;
			sendPacket(new ResuscitationSafeOnResponse());
		} else {
			getFlags().SeguroResu = false;
			sendPacket(new ResuscitationSafeOffResponse());
		}
        
		if (getFlags().AtacadoPorNpc > 0) {
			Npc npc = this.server.npcById(getFlags().AtacadoPorNpc);
			if (npc != null) {
				npc.oldMovement();
			} else {
				getFlags().AtacadoPorNpc = 0;
			}
		}
		// <<<< Paralisis >>>>
		if (getFlags().Paralizado) {
			getFlags().Paralizado = false;
			sendPacket(new ParalizeOKResponse());
		}
		// <<<< Descansando >>>>
		if (getFlags().Descansar) {
			getFlags().Descansar = false;
			sendPacket(new RestOKResponse());
		}
		// <<<< Meditando >>>>
		if (getFlags().Meditando) {
			getFlags().Meditando = false;
			sendPacket(new MeditateToggleResponse());
		}
		// desequipar armadura
		if (this.userInv.tieneArmaduraEquipada()) {
			this.userInv.desequiparArmadura();
		}
		// desequipar arma
		if (this.userInv.tieneArmaEquipada()) {
			this.userInv.desequiparArma();
		}
		// desequipar casco
		if (this.userInv.tieneCascoEquipado()) {
			this.userInv.desequiparCasco();
		}
		// desequipar municiones
		if (this.userInv.tieneMunicionEquipada()) {
			this.userInv.desequiparMunicion();
		}
		// << Si es zona de trigger 6, no pierde el inventario >>
		if ( !map.isArenaZone(pos().x, pos().y) ) {
			// << Si es newbie no pierde el inventario >>
			if ( !isNewbie() || isCriminal()) {
				dropAll();
			} else {
				tirarTodosLosItemsNoNewbies();
			}
		}
		// << Reseteamos los posibles FX sobre el personaje >>
		if (this.infoChar.loops == LoopAdEternum) {
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
		}
		// << Cambiamos la apariencia del char >>
		if ( !getFlags().Navegando ) {
			this.infoChar.body = OBJ_INDEX_CUERPO_MUERTO;
			this.infoChar.head = OBJ_INDEX_CABEZA_MUERTO;
			this.infoChar.shield = NingunEscudo;
			this.infoChar.weapon = NingunArma;
			this.infoChar.helmet = NingunCasco;
		} else {
			this.infoChar.body = OBJ_INDEX_FRAGATA_FANTASMAL;
		}

		getUserPets().removeAll();
		sendCharacterChange();
		sendUpdateUserStats();
	}

	public void dropAll() {
		Map map = this.server.getMap(pos().map);
		if (map.isArenaZone(pos().x, pos().y) || getFlags().isGM()) {
			return;
		}
		dropAllItems();
		dropGold(getStats().getGold()); 
	}

	private void dropAllItems() {
		Map m = this.server.getMap(pos().map);
		for (int i = 1; i <= this.userInv.getSize(); i++) {
			if (this.userInv.getObject(i) != null && this.userInv.getObject(i).objid > 0) {
				ObjectInfo info_obj = findObj(this.userInv.getObject(i).objid);
				if (info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(this.userInv.getObject(i).objid, this.userInv.getObject(i).cant);
					this.userInv.quitarUserInvItem(i, obj_inv.cant);
					sendInventorySlot(i);
					if (info_obj.itemSeCae()) {
						m.dropItemOnFloor(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarTodosLosItemsNoNewbies() {
		Map map = this.server.getMap(pos().map);
		for (int i = 1; i <= this.userInv.getSize(); i++) {
			if (this.userInv.getObject(i).objid > 0) {
				ObjectInfo info_obj = findObj(this.userInv.getObject(i).objid);
				if (!info_obj.esNewbie() && info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(this.userInv.getObject(i).objid, this.userInv.getObject(i).cant);
					this.userInv.quitarUserInvItem(i, obj_inv.cant);
					sendInventorySlot(i);
					if (info_obj.itemSeCae()) {
						map.dropItemOnFloor(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void dropGold(int amount) {
		// FIXME
		if (amount > MAX_INVENTORY_OBJS) {
			return;
		}
		Map m = this.server.getMap(pos().map);
		// SI EL Npc TIENE ORO LO TIRAMOS
		if ((amount > 0) && (amount <= getStats().getGold())) {
			while ((amount > 0) && (getStats().getGold() > 0)) {
				InventoryObject oi = new InventoryObject(OBJ_ORO, amount);
				if ((amount > MAX_INVENTORY_OBJS) && (getStats().getGold() > MAX_INVENTORY_OBJS)) {
					oi.cant = MAX_INVENTORY_OBJS;
					getStats().addGold( -MAX_INVENTORY_OBJS);
					amount -= oi.cant;
				} else {
					oi.cant = amount;
					getStats().addGold( -amount );
					amount -= oi.cant;
				}
				if (getFlags().isGM()) {
					Log.logGM(this.userName,
							"Tiró " + oi.cant + " unidades del objeto " + findObj(oi.objid).Nombre);
				}
				m.dropItemOnFloor(pos().x, pos().y, oi);
			}
		}
	}

	public void turnCriminal() {
		Map map = this.server.getMap(pos().map);
		if (map.isArenaZone(pos().x, pos().y)) {
			return;
		}
		if (!getFlags().isGM()) {
			if (!isCriminal()) {
				this.reputation.condenar();
				if (this.faction.ArmadaReal) {
					this.faction.royalArmyKick();
				}
				refreshCharStatus();
			}
		}
	}

	/**
	 * Subir skill
	 * @param skill
	 */
	public void riseSkill(Skill skill) {
		if (!getFlags().Hambre && !getFlags().Sed) {
			
			// Alcanzó el máximo del skill para este nivel?
			if (skills().get(skill) >= Skill.levelSkill[getStats().ELV]) {
				return;
			}
			
			// Alcanzó el máximo valor del skill? (100)
			if (skills().get(skill) >= Skill.MAX_SKILL_POINTS) {
				return;
			}
			
			int prob;
			if (getStats().ELV <= 3) {
				prob = 25;
			} else if (getStats().ELV < 6) {
				prob = 35;
			} else if (getStats().ELV < 10) {
				prob = 40;
			} else if (getStats().ELV < 20) {
				prob = 45;
			} else {
				prob = 50;
			}

			if (Util.random(1, prob) == 7) {
				skills().addSkillPoints(skill, (byte) 1);
				sendMessage("¡Has mejorado tu skill " + skill + " en un punto!. Ahora tienes " 
							+ skills().get(skill) + " pts.", FontType.FONTTYPE_INFO);
				
				getStats().addExp((byte) EXPERIENCE_BY_LEVEL_UP);
				sendMessage("¡Has ganado " + EXPERIENCE_BY_LEVEL_UP +" puntos de experiencia!", FONTTYPE_FIGHT);
				checkUserLevel();
			}
		}
	}

	public void userAsignaSkill(Skill skill, int amount) {
		if (skills().get(skill) >= Skill.MAX_SKILL_POINTS
				|| skills().get(skill) + (amount) > 100
				|| skills().freeSkillPts < amount) {
			return;
		}

		skills().addSkillPoints(skill, (byte) amount);
		skills().freeSkillPts -= amount;
	}

	public boolean isNewbie() {
		return getStats().ELV <= LimiteNewbie;
	}

	public boolean isRoyalArmy() {
		return this.faction.ArmadaReal;
	}

	public boolean isDarkLegion() {
		return this.faction.FuerzasCaos;
	}

	public void sendSkills() {
		// Comando ESKI
		sendPacket(new SendSkillsResponse(skills().skills()));
		if (skills().freeSkillPts > 0) {
			sendPacket(new LevelUpResponse((short) skills().freeSkillPts));
		}
	}

	/**
	 * Envenenar
	 */
	public void poison() {
		getFlags().Envenenado = true;
		sendMessage("¡¡La criatura te ha envenenado!!", FONTTYPE_FIGHT);
	}

	public void checkUserLevel() {
		while (getStats().Exp >= getStats().ELU) {
			boolean wasNewbie = isNewbie();

			// ¿Alcanzo el maximo nivel?
			if (getStats().ELV == STAT_MAXELV) {
				getStats().Exp = 0;
				getStats().ELU = 0;
				return;
			}
			
			// Se acumuló la experiencia necesaria para subir de nivel
			sendWave(SOUND_NIVEL);
			sendMessage("¡Has subido de nivel!", FontType.FONTTYPE_INFO);
			
			int skillPoints = (getStats().ELV == 1) ? 10 : 5;
			skills().freeSkillPts += skillPoints;
			sendMessage("Has ganado " + skillPoints + " skillpoints.", FontType.FONTTYPE_INFO);
			
			getStats().ELV++;
			getStats().Exp -= getStats().ELU;
			
            if (getStats().ELV < 15) {
            	getStats().ELU *= 1.4;
            } else if (getStats().ELV < 21) {
            	getStats().ELU *= 1.35;
            } else if (getStats().ELV < 33) {
            	getStats().ELU *= 1.3;
            } else if (getStats().ELV < 41) {
            	getStats().ELU *= 1.225;
            } else {
            	getStats().ELU *= 1.175;
            }
			
			clazz().incStats(this);
			sendSkills();
			
			if (!isNewbie() && wasNewbie) {
				getUserInv().quitarObjsNewbie();
				quitDungeonNewbie();
			}
			
			sendUpdateUserStats();
		}
	}

	private void quitDungeonNewbie() {
		// Si el usuario dejó de ser Newbie, y estaba en el Dungeon Newbie
		// es transportado a su hogar de origen.
		Map map = server.getMap(pos().map);
		if (map != null && map.isNewbieMap()) {
			MapPos ciudad = this.server.getCiudadPos(this.homeland);
			warpMe(ciudad.map, ciudad.x, ciudad.y, true);
		}
	}

	public long poderEvasionEscudo() {
		return (long) ((skills().get(Skill.SKILL_Defensa) * this.clazz().modEvasionDeEscudoClase()) / 2);
	}

	public double poderEvasion() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Tacticas) < 31) {
			tmp = skills().get(Skill.SKILL_Tacticas) * this.clazz().modificadorEvasion();
		} else if (skills().get(Skill.SKILL_Tacticas) < 61) {
			tmp = (skills().get(Skill.SKILL_Tacticas) + getStats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorEvasion();
		} else if (skills().get(Skill.SKILL_Tacticas) < 91) {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (2 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorEvasion();
		} else {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (3 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorEvasion();
		}
		return (tmp + (2.5 * Math.max(getStats().ELV - 12, 0)));
	}

	public double poderAtaqueArma() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Armas) < 31) {
			tmp = skills().get(Skill.SKILL_Armas) * this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Armas) < 61) {
			tmp = ((skills().get(Skill.SKILL_Armas) + getStats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Armas) < 91) {
			tmp = ((skills().get(Skill.SKILL_Armas) + (2 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else {
			tmp = ((skills().get(Skill.SKILL_Armas) + (3 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas());
		}
		return tmp + (2.5 * Math.max(getStats().ELV - 12, 0));
	}

	public double poderAtaqueProyectil() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Proyectiles) < 31) {
			tmp = (skills().get(Skill.SKILL_Proyectiles) * this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 61) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + getStats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 91) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (2 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (3 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		}
		return (tmp + (2.5 * Math.max(getStats().ELV - 12, 0)));
	}

	public double poderAtaqueWresterling() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Wresterling) < 31) {
			tmp = (skills().get(Skill.SKILL_Wresterling) * this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Wresterling) < 61) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + getStats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Wresterling) < 91) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (2 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (3 * getStats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas();
		}
		return tmp + (2.5 * Math.max(getStats().ELV - 12, 0));
	}

	public boolean userImpactoNpc(Npc npc) {
		double poderAtaque = 0;
		if (this.userInv.tieneArmaEquipada()) {
			// Usando un arma
			if (this.userInv.getArma().esProyectil()) {
				poderAtaque = poderAtaqueProyectil();
			} else {
				poderAtaque = poderAtaqueArma();
			}
		} else {
			// Peleando con puños
			poderAtaque = poderAtaqueWresterling();
		}
		double probExito = Math.max(10, Math.min(90, 50 + ((poderAtaque - npc.getPoderEvasion()) * 0.4)));
		boolean huboImpacto = (Util.random(1, 100) <= probExito);
		if (huboImpacto) {
			if (this.userInv.tieneArmaEquipada()) {
				if (this.userInv.getArma().esProyectil()) {
					riseSkill(Skill.SKILL_Proyectiles);
				} else {
					riseSkill(Skill.SKILL_Armas);
				}
			} else {
				riseSkill(Skill.SKILL_Wresterling);
			}
		}
		return huboImpacto;
	}

	public boolean npcImpacto(Npc npc) {
		double userEvasion = poderEvasion();
		long npcPoderAtaque = npc.getPoderAtaque();
		long skillTacticas = skills().get(Skill.SKILL_Tacticas);
		long skillDefensa = skills().get(Skill.SKILL_Defensa);
		// Esta usando un escudo ???
		if (this.userInv.tieneEscudoEquipado()) {
			userEvasion += poderEvasionEscudo();
		}
		double probExito = Math.max(10, Math.min(90, 50 + ((npcPoderAtaque - userEvasion) * 0.4)));
		boolean impacto = (Util.random(1, 100) <= probExito);
		// ¿El usuario esta usando un escudo ???
		if (!impacto && this.userInv.tieneEscudoEquipado()) {
			double probRechazo = Math.max(10, Math.min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
			boolean rechazo = (Util.random(1, 100) <= probRechazo);
			if (rechazo) {
				// Se rechazo el ataque con el escudo
				sendWave(SOUND_ESCUDO);
				sendPacket(new BlockedWithShieldUserResponse());
				riseSkill(Skill.SKILL_Defensa);
			}
		}
		return impacto;
	}

	public short calcularDaño(Npc npc) {
		long dañoArma = 0;
		long dañoMaxArma = 0;
		long dañoUsuario = 0;
		double modifClase = 0;
		if (this.userInv.tieneArmaEquipada()) {
			ObjectInfo arma = this.userInv.getArma();
			// Ataca a un npc?
			if (npc != null) {
				// Usa la mata dragones?
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) { // Usa la
					// matadragones?
					modifClase = this.clazz().modicadorDañoClaseArmas();
					if (npc.npcType() == NpcType.NPCTYPE_DRAGON) { 
						// Ataca a un dragón?
						dañoArma = Util.random(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					} else { 
						// Si no ataca a un dragón, el daño es 1
						dañoArma = 1;
						dañoMaxArma = 1;
					}
				} else { 
					// daño comun
					if (arma.esProyectil()) {
						modifClase = this.clazz().modicadorDañoClaseProyectiles();
						dañoArma = Util.random(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							dañoArma += Util.random(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.random(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			} else { 
				// Ataca usuario
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) {
					modifClase = this.clazz().modicadorDañoClaseArmas();
					dañoArma = 1; // Si usa la espada matadragones daño es 1
					dañoMaxArma = 1;
				} else {
					if (arma.esProyectil()) {
						modifClase = this.clazz().modicadorDañoClaseProyectiles();
						dañoArma = Util.random(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							dañoArma += Util.random(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.random(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			}
		}
		dañoUsuario = Util.random(getStats().MinHIT, getStats().MaxHIT);
		double daño = (((3 * dañoArma) + ((dañoMaxArma / 5) * Math.max(0, (getStats().attr.get(Attribute.STRENGTH) - 15)))
				+ dañoUsuario) * modifClase);
		return (short) daño;
	}

	public void userDañoNpc(Npc npc) {
		int daño = calcularDaño(npc);
		// esta navegando? si es asi le sumamos el daño del barco
		if (getFlags().Navegando) {
			daño += Util.random(this.userInv.getBarco().MinHIT, this.userInv.getBarco().MaxHIT);
		}
		daño -= npc.stats().defensa;
		if (daño < 0) {
			daño = 0;
		}
		npc.stats().MinHP -= daño;
		if (daño > 0) {
			sendPacket(new UserHitNPCResponse(daño));
			npc.calcularDarExp(this, daño);
		} else {
			sendWave(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
		if (npc.stats().MinHP > 0) {
			// Trata de apuñalar por la espalda al enemigo
			if (puedeApuñalar()) {
				apuñalar(npc, daño);
				riseSkill(Skill.SKILL_Apuñalar);
			}
		}
		if (npc.stats().MinHP <= 0) {
			// Si era un Dragon perdemos la espada matadragones
			if (npc.npcType() == NpcType.NPCTYPE_DRAGON) {
				quitarObjetos(OBJ_INDEX_ESPADA_MATA_DRAGONES, 1);
			}
			getUserPets().petsFollowMaster();

			npc.muereNpc(this);
		}
	}

	public boolean puedeApuñalar() {
		if (this.userInv.tieneArmaEquipada()) {
			return ((skills().get(Skill.SKILL_Apuñalar) >= MIN_APUÑALAR) && this.userInv.getArma().apuñala())
					|| ((this.clazz == Clazz.Assassin) && this.userInv.getArma().apuñala());
		}
		return false;
	}

	public void quitarObjetos(int objIdx, int cant) {
		if (cant <= 0) {
			return;
		}
		int cant_quitar;
		for (int i = 1; i <= this.userInv.getSize(); i++) {
			if (this.userInv.getObject(i).objid == objIdx) {
				cant_quitar = this.userInv.getObject(i).cant < cant ? this.userInv.getObject(i).cant : cant;
				cant -= cant_quitar;
				this.userInv.desequipar(i);
				this.userInv.quitarUserInvItem(i, cant_quitar);
				sendInventorySlot(i);
				if (cant <= 0) {
					break; // exit for.
				}
			}
		}
	}

	public void npcDaño(Npc npc) {
		int daño = Util.random(npc.stats().MinHIT, npc.stats().MaxHIT);
		int defbarco = 0;
		if (isSailing()) {
			ObjectInfo barco = this.userInv.getBarco();
			defbarco = Util.random(barco.MinDef, barco.MaxDef);
		}
		byte lugar = (byte)Util.random(1, 6);
		switch (lugar) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (this.userInv.tieneCascoEquipado()) {
				ObjectInfo casco = this.userInv.getCasco();
				int absorbido = Util.random(casco.MinDef, casco.MaxDef);
				absorbido += defbarco;
				daño -= absorbido;
				if (daño < 1) {
					daño = 1;
				}
			}
			break;
		default:
			// Si tiene armadura absorbe el golpe
			if (this.userInv.tieneArmaduraEquipada()) {
				ObjectInfo armadura = this.userInv.getArmadura();
				int absorbido = Util.random(armadura.MinDef, armadura.MaxDef);
				absorbido += defbarco;
				daño -= absorbido;
				if (daño < 1) {
					daño = 1;
				}
			}
			break;
		}
		sendPacket(new NPCHitUserResponse(lugar, (short)daño));
		if (!getFlags().isGM()) {
			getStats().MinHP -= daño;
		}

		sendUpdateUserStats();

		// Muere el usuario
		if (getStats().MinHP <= 0) {
			sendPacket(new NPCKillUserResponse()); // Le informamos que ha muerto ;)
			// Si lo mato un guardia
			if (isCriminal() && npc.isNpcGuard()) {
				this.reputation.decAsesino(vlAsesino / 4);
				this.reputation.decBandido(vlAsalto / 4);
				this.reputation.decLadron(vlCazador / 3);
			}
			if (npc.getPetUserOwner() != null) {
				npc.getPetUserOwner().getUserPets().petsFollowMaster();
			} else {
				// Al matarlo no lo sigue mas
				if (npc.stats().alineacion == 0) {
					npc.restoreOldMovement();
				}
			}
			userDie();
		}
	}

	public void usuarioAtacaNpc(Npc npc) {
		if (pos().distance(npc.pos()) > MAX_DISTANCIA_ARCO) {
			sendMessage("Estás muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		if (this.faction.ArmadaReal && npc.getPetUserOwner() != null && !npc.getPetUserOwner().isCriminal()) {
			sendMessage("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.getPetUserOwner() != null && getFlags().Seguro && !npc.getPetUserOwner().isCriminal()) {
			sendMessage("Debes quitar el seguro para atacar a una mascota de un ciudadano.", FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.stats().alineacion == 0 && getFlags().Seguro) {
			sendMessage("Debes quitar el seguro para atacar a una criatura no hostil.", FontType.FONTTYPE_WARNING);
			return;
		}
		npcAtacado(npc);
		if (userImpactoNpc(npc)) {
			if (npc.getSonidoAtaqueExitoso() > 0) {
				sendWave(npc.getSonidoAtaqueExitoso());
			} else {
				sendWave(SOUND_IMPACTO2);
			}
			userDañoNpc(npc);
		} else {
			sendWave(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
	}

	public void safeToggle() {
		getFlags().Seguro = !getFlags().Seguro;
		if (getFlags().Seguro) {
			sendPacket(new SafeModeOnResponse());
		} else {
			sendPacket(new SafeModeOffResponse());
		}
	}
	
	public void resuscitationToggle() {
		// HandleResuscitationToggle
		getFlags().SeguroResu = !getFlags().SeguroResu;
		
		if (getFlags().SeguroResu) {
			sendPacket(new ResuscitationSafeOnResponse());
		} else {
			sendPacket(new ResuscitationSafeOffResponse());
		}
	}
	
	public boolean isInCombatMode() {
		return getFlags().ModoCombate;
	}

	public void toggleCombatMode() {
		// Entrar o salir modo combate
		if (isInCombatMode()) {
			sendMessage("Has salido del modo de combate.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Has pasado al modo de combate.", FontType.FONTTYPE_INFO);
		}
		getFlags().ModoCombate = !getFlags().ModoCombate;
	}

	public void showUsersOnline() {
		// Comando /ONLINE
		// Muestra los usuarios conectados.
		StringBuffer msg = new StringBuffer();
		int cant = 0;
		for (Object element : this.server.getUsuariosConectados()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append((String) element);
		}
		sendMessage(msg.toString(), FontType.FONTTYPE_INFO);
		sendMessage("Cantidad de usuarios: " + cant, FontType.FONTTYPE_INFO);
	}

	public void usuarioAtaca() {
		if (getCounters().intervaloPermiteAtacar()) {
			// Pierde stamina
			if (getStats().stamina >= 10) {
				getStats().quitarStamina(Util.random(1, 10));
			} else {
				if (gender() == UserGender.GENERO_MAN) {
					sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
				} else {
					sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
				}
				return;
			}
			MapPos attackPos = pos().copy();
			attackPos.moveToHeading(this.infoChar.getHeading());
			// Exit if not legal
			if (!attackPos.isValid()) {
				sendWave(SOUND_SWING);
				return;
			}
			Map mapa = this.server.getMap(pos().map);
			User attackedUser = mapa.getUser(attackPos.x, attackPos.y);
			// Look for user
			if (attackedUser != null) {
				usuarioAtacaUsuario(attackedUser);
				sendUpdateUserStats();
				attackedUser.sendUpdateUserStats();
				return;
			}
			// Look for Npc
			Npc attackedNpc = mapa.getNPC(attackPos.x, attackPos.y);
			if (attackedNpc != null) {
				if (attackedNpc.isAttackable()) {
					if (attackedNpc.getPetUserOwner() != null && mapa.isSafeMap()) {
						sendMessage("No podés atacar mascotas en zonas seguras", FONTTYPE_FIGHT);
						return;
					}
					usuarioAtacaNpc(attackedNpc);
				} else {
					sendMessage("No podés atacar a este Npc", FONTTYPE_FIGHT);
				}
				sendUpdateUserStats();
				return;
			}
			sendWave(SOUND_SWING);
			sendUpdateUserStats();
			getFlags().Trabajando = false;
		} else {
			log.info("NO PUEDE ATACAR");
		}
	}

	public boolean usuarioImpacto(User victima) {
		double probExito = 0;
		long skillTacticas = victima.skills().get(Skill.SKILL_Tacticas);
		long skillDefensa = victima.skills().get(Skill.SKILL_Defensa);
		ObjectInfo arma = this.userInv.getArma();
		boolean proyectil = (arma != null) && arma.esProyectil();
		// Calculamos el poder de evasion...
		double userPoderEvasion = victima.poderEvasion();
		double userPoderEvasionEscudo = 0;
		if (victima.userInv.tieneEscudoEquipado()) {
			userPoderEvasionEscudo = victima.poderEvasionEscudo();
			userPoderEvasion += userPoderEvasionEscudo;
		}
		// El atacante esta usando un arma ???
		double poderAtaque = 0;
		if (this.userInv.tieneArmaEquipada()) {
			if (proyectil) {
				poderAtaque = poderAtaqueProyectil();
			} else {
				poderAtaque = poderAtaqueArma();
			}
			probExito = Math.max(10, Math.min(90, 50 + ((poderAtaque - userPoderEvasion) * 0.4)));
		} else {
			poderAtaque = poderAtaqueWresterling();
			probExito = Math.max(10, Math.min(90, 50 + ((poderAtaque - userPoderEvasion) * 0.4)));
		}
		boolean huboImpacto = (Util.random(1, 100) <= probExito);
		// el usuario esta usando un escudo ???
		if (victima.userInv.tieneEscudoEquipado()) {
			// Fallo ???
			if (!huboImpacto) {
				double probRechazo = Math.max(10, Math.min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
				boolean huboRechazo = (Util.random(1, 100) <= probRechazo);
				if (huboRechazo) {
					// Se rechazo el ataque con el escudo!
					sendWave(SOUND_ESCUDO);
					sendPacket(new BlockedWithShieldOtherResponse());
					victima.sendPacket(new BlockedWithShieldUserResponse());
					victima.riseSkill(Skill.SKILL_Defensa);
				}
			}
		}
		if (huboImpacto) {
			if (arma != null) {
				if (!proyectil) {
					riseSkill(Skill.SKILL_Armas);
				} else {
					riseSkill(Skill.SKILL_Proyectiles);
				}
			} else {
				riseSkill(Skill.SKILL_Wresterling);
			}
		}
		return huboImpacto;
	}

	public void usuarioAtacaUsuario(User victima) {
		if (!puedeAtacar(victima)) {
			return;
		}
		if (pos().distance(victima.pos()) > MAX_DISTANCIA_ARCO) {
			sendMessage("Estás muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		usuarioAtacadoPorUsuario(victima);
		if (usuarioImpacto(victima)) {
			sendWave(SOUND_IMPACTO);
			if (!victima.isSailing()) {
				victima.sendCreateFX(FXSANGRE, 0);
			}
			userDañoUser(victima);
		} else {
			sendWave(SOUND_SWING);
			this.sendPacket(new UserSwingResponse());
			victima.sendPacket(new UserAttackedSwingResponse(getId()));
		}
	}

	public void userDañoUser(User victima) {
		short damage = calcularDaño(null);
		short shipDefense = 0;
		envenenarUsuario(victima); // revisar... FIXME
		if (isSailing()) {
			ObjectInfo barco = this.userInv.getBarco();
			damage += Util.random(barco.MinHIT, barco.MaxHIT);
		}
		if (victima.isSailing()) {
			ObjectInfo barco = victima.userInv.getBarco();
			shipDefense = (short) Util.random(barco.MinDef, barco.MaxDef);
		}
		byte target = (byte) Util.random(1, 6);
		switch (target) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (victima.userInv.tieneCascoEquipado()) {
				ObjectInfo casco = victima.userInv.getCasco();
				int absorbido = Util.random(casco.MinDef, casco.MaxDef);
				absorbido += shipDefense;
				damage -= absorbido;
				if (damage < 0) {
					damage = 1;
				}
			}
			break;
		default:
			// Si tiene armadura absorbe el golpe
			if (victima.userInv.tieneEscudoEquipado()) {
				ObjectInfo escudo = victima.userInv.getEscudo();
				int absorbido = Util.random(escudo.MinDef, escudo.MaxDef);
				absorbido += shipDefense;
				damage -= absorbido;
				if (damage < 0) {
					damage = 1;
				}
			}
		}
		sendPacket(new UserHittedUserResponse(victima.getId(), target, damage));
		victima.sendPacket(new UserHittedByUserResponse(getId(), target, damage));
		victima.getStats().removeHP(damage);
		if (!getFlags().Hambre && !getFlags().Sed) {
			if (this.userInv.tieneArmaEquipada()) {
				// Si usa un arma quizas suba "Combate con armas"
				riseSkill(Skill.SKILL_Armas);
			} else {
				// sino tal vez lucha libre
				riseSkill(Skill.SKILL_Wresterling);
			}
			riseSkill(Skill.SKILL_Tacticas);
			// Trata de apuñalar por la espalda al enemigo
			if (puedeApuñalar()) {
				apuñalar(victima, damage);
				riseSkill(Skill.SKILL_Apuñalar);
			}
		}
		if (victima.getStats().MinHP <= 0) {
			contarMuerte(victima);
			getUserPets().petsFollowMaster(victima.getId());
			actStats(victima);
			victima.userDie();
		}
		// Controla el nivel del usuario
		checkUserLevel();
	}

	public void actStats(User victima) {
		// ActStats
		int daExp = victima.getStats().ELV * 2;
		getStats().addExp(daExp);
		
		// Lo mata
		sendMessage("Has matado a " + victima.userName + "!", FONTTYPE_FIGHT);
		sendMessage("Has ganado " + daExp + " puntos de experiencia.", FONTTYPE_FIGHT);
		
		victima.sendMessage(this.userName + " te ha matado!", FONTTYPE_FIGHT);
		
		if (duelStatus(victima) != DuelStatus.DUEL_ALLOWED) {
			boolean eraCriminal = isCriminal();
			if (!victima.isCriminal()) {
				this.getReputation().incAsesino(vlAsesino * 2);
				this.getReputation().burguesRep = 0;
				this.getReputation().nobleRep = 0;
				this.getReputation().plebeRep = 0;
			} else {
				this.getReputation().incNoble(vlNoble);
			}
			if (isCriminal()) {
				if (!eraCriminal) {
					refreshCharStatus();
				}
			} else {
				if (eraCriminal) {
					refreshCharStatus();
				}
			}
		}
		getStats().incUsuariosMatados();
		log.info("ASESINATO: " + this.userName + " asesino a " + victima.userName);
	}
	
	public void contarMuerte(User killedUser) {
		// ContarMuerte
		if (killedUser.isNewbie()) {
			return;
		}
		if (duelStatus(killedUser) == DuelStatus.DUEL_ALLOWED) {
			return;
		}

		userFaction().countKill(killedUser);
		getStats().incUsuariosMatados();
	}

	public void usuarioAtacadoPorUsuario(User victima) {
		if (duelStatus(victima) == DuelStatus.DUEL_ALLOWED) {
			return;
		}
		if (!getGuildInfo().esMiembroClan() || !victima.getGuildInfo().esMiembroClan()) {
			if (!isCriminal() && !victima.isCriminal()) {
				turnCriminal();
			}
		} else { // Ambos están en clan
			if (getGuild() != null && !getGuild().isEnemy(victima.getGuildInfo().getGuildName())) {
				// Están en clanes enemigos
				if (!isCriminal() && !victima.isCriminal()) {
					turnCriminal();
				}
			}
			// TODO Revisar: ¿puede un cuidadano atacar a otro ciudadano, cuando están en clanes enemigos?
		}

		if (victima.isCriminal()) {
			this.reputation.incNoble(vlNoble);
		} else {
			this.reputation.incBandido(vlAsalto);
		}

		allPetsAttackUser(victima);
		victima.allPetsAttackUser(this);
	}

	public void allPetsAttackUser(User objetivo) {
		getUserPets().getPets().forEach(pet -> {
			pet.attackedByUserName(objetivo.getUserName());
			pet.defenderse();
		});
	}
	
	public boolean isAtDuelArena() {
        Map map = server.getMap(pos().map);
        return map.getTrigger(pos().x, pos().y) == Trigger.TRIGGER_ARENA_DUELOS;
	}

	public boolean puedeAtacar(User victima) {
		Map map = this.server.getMap(victima.pos().map);

		DuelStatus t = duelStatus(victima);
		if (t == DuelStatus.DUEL_ALLOWED) {
			return true;
		} else if (t == DuelStatus.DUEL_FORBIDDEN) {
			return false;
		}
		
		if (map.isSafeMap()) {
			sendMessage("Esta es una zona segura, aqui no puedes atacar usuarios.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (map.isSafeZone(victima.pos().x, victima.pos().y)) {
			sendMessage("No puedes pelear aqui.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isCriminal() && this.faction.ArmadaReal) {
			sendMessage("Los soldados del Ejercito Real tienen prohibido atacar ciudadanos.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (victima.faction.FuerzasCaos && this.faction.FuerzasCaos) {
			sendMessage("Los seguidores de las Fuerzas del Caos tienen prohibido atacarse entre sí.",
					FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!checkAlive("No puedes atacar porque estas muerto.")) {
			return false;
		}
		// Se asegura que la victima no es un GM
		if (victima.getFlags().isGM()) {
			sendMessage("¡¡No puedes atacar a los administradores del juego!!", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isAlive()) {
			sendMessage("No puedes atacar a un espíritu.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (getFlags().Seguro) {
			if (!victima.isCriminal()) {
				sendMessage(
						"No puedes atacar ciudadanos, para hacerlo debes desactivar el seguro apretando la tecla S",
						FONTTYPE_FIGHT);
				return false;
			}
		}
		// Implementacion de trigger 7 - Para torneos con espectadores
		if (map.isTournamentZone(pos().x, pos().y)) {
			if ( map.isTournamentZone(pos().x, pos().y) && !map.isTournamentZone(victima.pos().x, victima.pos().y) ) {
				sendMessage("Para atacar a ese usuario, él se debe encontrar en tu misma zona.", FONTTYPE_FIGHT);
			} else if ( !map.isTournamentZone(pos().x, pos().y)	&& map.isTournamentZone(victima.pos().x, victima.pos().y) ) {
				sendMessage("Para atacar a ese usuario, debes encontrarte en la misma zona que él.", FONTTYPE_FIGHT);
			}
			return false;
		}
		return true;
	}

	private boolean suerteApuñalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Apuñalar) / 10)]);
		return (Util.random(1, rango) == 3);
	}

	public void apuñalar(Npc victimaNPC, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 2;
			victimaNPC.stats().MinHP -= daño;
			sendMessage("Has apuñalado a la criatura por " + daño, FONTTYPE_FIGHT);
			riseSkill(Skill.SKILL_Apuñalar);
			victimaNPC.calcularDarExp(this, daño);
		} else {
			sendMessage("¡No has logrado apuñalar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void apuñalar(User victimaUsuario, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 1.5;
			victimaUsuario.getStats().MinHP -= daño;
			sendMessage("Has apuñalado a " + victimaUsuario.userName + " por " + daño, FONTTYPE_FIGHT);
			victimaUsuario.sendMessage("Te ha apuñalado " + this.userName + " por " + daño, FONTTYPE_FIGHT);
		} else {
			sendMessage("¡No has logrado apuñalar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void npcAtacado(Npc npc) {
		// Guardamos el usuario que ataco el npc
		npc.attackedByUserName(this.userName);
		if (npc.getPetUserOwner() != null) {
			npc.getPetUserOwner().allPetsAttackUser(this);
		}
		if (esMascotaCiudadano(npc)) {
			turnCriminal();
			npc.defenderse();
		} else {
			// Reputacion
			if (npc.stats().alineacion == 0 && npc.getPetUserOwner() == null) {
				if (npc.npcType() == NpcType.NPCTYPE_ROYAL_GUARD) {
					turnCriminal();
				} else {
					this.reputation.incBandido(vlAsalto);
				}
			} else if (npc.stats().alineacion == 1) {
				this.reputation.incPlebe(vlCazador / 2);
			}
			// hacemos que el npc se defienda
			npc.defenderse();
			getUserPets().petsAttackNpc(npc);
		}
	}

	public boolean esMascotaCiudadano(Npc npc) {
		if (npc.getPetUserOwner() != null) {
			if (!npc.getPetUserOwner().isCriminal()) {
				npc.getPetUserOwner().sendMessage("¡¡" + this.userName + " esta atacando a tu mascota!!", FONTTYPE_FIGHT);
				return true;
			}
		}
		return false;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void connectNewUser(String userName, String password, byte race, byte gender, byte clazz, String email, byte homeland) {
		// Validar los datos recibidos :-)
		if (!Util.isValidAscii(userName)) {
			sendError("Nombre inválido.");
			return;
		}
		if (!this.server.manager().isValidUserName(userName)) {
			sendError("Los nombres de los personajes deben pertencer a la fantasia, el nombre indicado es invalido.");
			return;
		}
		if (userExists(userName)) {
			sendError("Ya existe el personaje.");
			return;
		}
		
		this.userName = userName;
		this.getFlags().Muerto = false;
		this.getFlags().Escondido = false;
		this.reputation.asesinoRep = 0;
		this.reputation.bandidoRep = 0;
		this.reputation.burguesRep = 0;
		this.reputation.ladronRep = 0;
		this.reputation.nobleRep = 1000;
		this.reputation.plebeRep = 30;
		this.clazz = Clazz.value(clazz);
		this.race = UserRace.value(race);
		this.gender = UserGender.value(gender);
		this.email = email;
		this.homeland = City.value(homeland);
		// FIXME
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		// if (!atributosValidos()) {
		// sendError("Atributos invalidos.");
		// return;
		// }
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		modifyAttributesByRace();
		this.skills().freeSkillPts = 10;

		this.passwordHash = Security.hashPassword(userName, password);
		this.infoChar.setHeading(Heading.SOUTH);
		this.infoChar.ramdonBodyAndHead(race(), gender());
		this.infoChar.weapon = NingunArma;
		this.infoChar.shield = NingunEscudo;
		this.infoChar.helmet = NingunCasco;
		this.origChar = new CharInfo(this.infoChar);

		this.getStats().inicializarEstads(this.clazz);

		// Inicializar hechizos:
		if (this.clazz().isMagickal()) {
			this.spells.setSpell(1, HECHIZO_DARDO_MAGICO);
		}

		// ???????????????? INVENTARIO ¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿
		this.userInv.clear();
		this.userInv.setObject(1, new InventoryObject(MANZANA_ROJA_NEWBIES, 100, false));
		this.userInv.setObject(2, new InventoryObject(BOTELLA_AGUA_NEWBIES, 100, false));
		this.userInv.setArma(3, new InventoryObject(DAGA_NEWBIES, 1, true));

		switch (race()) {
		case RAZA_HUMAN:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_1, 1, true));
			break;
		case RAZA_ELF:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_2, 1, true));
			break;
		case RAZA_DROW:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_3, 1, true));
			break;
		case RAZA_DWARF:
			this.userInv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		case RAZA_GNOME:
			this.userInv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		}

		saveUser();
		connectUser(userName, password);
	}

	public void saveUser() {
		this.userStorage.saveUserToStorage();
	}

	static String getPjFile(String userName) {
		// FIXME
		return Constants.CHARFILES_FOLDER + java.io.File.separator + userName.toLowerCase() + ".chr";
	}

	public boolean userExists() {
		return userExists(this.userName);
	}
	
	public static boolean userExists(String userName) {
		if (userName == null || userName.isBlank())
			return false;
		return Util.fileExists(getPjFile(userName));
	}
	
	public void checkIdle() {
		getCounters().IdleCount++;
		if (getCounters().IdleCount >= IdleLimit) {
		    sendError("Demasiado tiempo inactivo. Has sido desconectado.");
		    quitGame();
		}
	}
	
	public void checkPenalties() {
		if (getCounters().Pena > 0) {
		    getCounters().Pena--;
		    if (getCounters().Pena < 1) {
		        releaseFromJail();
		    }
		}
	}
	
	public void checkPiquete() {
		Map mapa = server.getMap(pos().map);
		if (mapa.isAntiPiquete(pos().x, pos().y)) {
			getCounters().piqueteSeconds++;
			sendMessage("Estas obstruyendo la via pública, muévete o serás encarcelado!!!", 
					FontType.FONTTYPE_INFO);
			if (getCounters().piqueteSeconds > 23) {
				getCounters().piqueteSeconds = 0;
				sendToJail(JAIL_TIME_PIQUETE_MINUTES, null);
			}
		} else {
			if (getCounters().piqueteSeconds > 0) {
				getCounters().piqueteSeconds = 0;
			}
		}
	}

	public void modifyAttributesByRace() {
		getStats().attr().set(Attribute.STRENGTH, 		getStats().attr().get(Attribute.STRENGTH) 		+ race().getStrengthModifier());
		getStats().attr().set(Attribute.AGILITY, 		getStats().attr().get(Attribute.AGILITY) 		+ race().getAgilityModifier());
		getStats().attr().set(Attribute.INTELIGENCE, 	getStats().attr().get(Attribute.INTELIGENCE) 	+ race().getInteligenceModifier());
		getStats().attr().set(Attribute.CHARISMA, 		getStats().attr().get(Attribute.CHARISMA) 		+ race().getCharismaModifier());
		getStats().attr().set(Attribute.CONSTITUTION, 	getStats().attr().get(Attribute.CONSTITUTION) 	+ race().getConstitutionModifier());
	}

	public void sendError(String msg) {
		log.warn("ERROR: " + msg);
		sendPacket(new ErrorMsgResponse(msg));
	}
	
	public void banned(String adminName, String reason) {
		this.banned = true;
		this.bannedBy = adminName;
		this.bannedReason = reason;
	}
	
	public void connectUser(String userName, String password) {
		try {
			// ¿Existe el personaje?
			if (!userExists(userName)) {
				sendError("El personaje no existe. Compruebe el nombre de usuario.");
				return;
			}
			if (this.server.isUserAlreadyConnected(userName)) {
				sendError("Perdon, pero ya esta conectado.");
				return;
			}
			
			this.userName = userName;
			
			// Reseteamos los FLAGS
			getFlags().Escondido = false;
			getFlags().TargetNpc = 0;
			getFlags().TargetObj = 0;
			getFlags().TargetUser = 0;
			this.infoChar.fx = 0;

			// ¿El password es válido?
			this.passwordHash = this.userStorage.passwordHashFromStorage();
			if (!Security.validatePassword(userName, password, passwordHash)) {
				sendError("Clave incorrecta.");
				return;
			}
			this.userStorage.loadUserFromStorage();

			if (this.banned) {
				sendError("Su personaje ha sido expulsado permanentemente.");
				return;				
			}
			
			if (this.server.manager().isGod(this.userName)) {
				getFlags().setGod();
				chatColor = Color.rgb(250, 250, 150);
				Log.logGM(this.userName, "El GM-DIOS se conectó desde la ip=" + this.ip);
			} else if (this.server.manager().isDemiGod(this.userName)) {
				getFlags().setDemiGod();
				chatColor = Color.rgb(0, 255, 0);
				Log.logGM(this.userName, "El GM-SEMIDIOS se conectó desde la ip=" + this.ip);
			} else if (this.server.manager().isCounsellor(this.userName)) {
				getFlags().setCounselor();
				chatColor = Color.rgb(0, 255, 0);
				Log.logGM(this.userName, "El GM-CONSEJERO se conectó desde la ip=" + this.ip);
				// FIXME
//			} else if (this.server.manager().isRoyalCouncil(this.userName)) {
//				flags().setRoyalCouncil();
//				chatColor = Color.rgb(0, 255, 255);
//			} else if (this.server.manager().isChaosCouncil(this.userName)) {
//				flags().setChaosCouncil();
//				chatColor = Color.rgb(255, 128, 64);
			} else {
				// Usuario no privilegiado.
				getFlags().setOrdinaryUser();
				getFlags().AdminPerseguible = true;
				chatColor = Color.COLOR_BLANCO;
			}
			
			if (server.isServerRestrictedToGMs()) {
				if (!isGM()) {
					sendError("Servidor restringido a administradores. Por favor reintente en unos momentos.");
			        return;
				}
			}

			// FIXME maximo de usuarios alcanzado?


			if (this.userInv.getEscudoSlot() == 0) {
				this.infoChar.shield = NingunEscudo;
			}
			if (this.userInv.getCascoSlot() == 0) {
				this.infoChar.helmet = NingunCasco;
			}
			if (this.userInv.getArmaSlot() == 0) {
				this.infoChar.weapon = NingunArma;
			}

			if (getFlags().Navegando) {
				this.infoChar.body = !isAlive() ? OBJ_INDEX_FRAGATA_FANTASMAL : this.userInv.getBarco().Ropaje;
				this.infoChar.head = 0;
				this.infoChar.weapon = NingunArma;
				this.infoChar.shield = NingunEscudo;
				this.infoChar.helmet = NingunCasco;
			}

			if (isAlive()) {
				getFlags().SeguroResu = false;
				sendPacket(new ResuscitationSafeOffResponse());
			} else {
				getFlags().SeguroResu = true;
				sendPacket(new ResuscitationSafeOnResponse());
			}
			
			sendInventoryToUser();
			spells().sendSpells();
			
			if (isParalized()) {
				sendPacket(new ParalizeOKResponse());
			}
			
			if (isDumb()) {
				sendPacket(new DumbResponse());
			}
			
			if (pos().map == 0) {
				setPos(this.server.getCiudadPos(this.homeland));
			}
			if (pos().map == 0) {
				setPos(this.server.getCiudadPos(City.ULLATHORPE));
			}
			
			sendUserIndexInServer();
			
			if (!enterIntoMap(pos().map, pos().x, pos().y, true, true)) {
				sendError("Se encuenta en un mapa invalido.");
				return;
			}
			
			if (this.server.isRaining()) {
				sendPacket(new RainToggleResponse());
			}
			if (!isCriminal()) {
				getFlags().Seguro = true;
				sendPacket(new SafeModeOnResponse());
			} else {
				sendPacket(new SafeModeOffResponse());
				getFlags().Seguro = false;
			}
			
			sendCharIndexInServer();
			
			checkUserLevel();
			sendUpdateUserStats();
			sendUpdateHungerAndThirst();
			
			server.motd().showMOTD(this);
			

			warpPets();
			// FIXME conectar clan
			
			sendLogged();
			
			// FIXME modGuilds.SendGuildNews(UserIndex)
			
			if (skills().freeSkillPts > 0) {
				sendSkills();
			}			
			
			sendPositionUpdate();
			getFlags().UserLogged = true;

			sendCreateFX(FXWARP, 0);

		} catch (Exception e) {
			log.fatal("ERROR EN connectUser(), nick=" + this.userName, e);
			sendError("Hubo un error conectando.");

		} finally {
			if (!getFlags().UserLogged) {
				// if anything went wrong...
				quitGame();
				this.userName = "";
			}
		}
	}

	public void efectoCegueEstu() {
		if (getCounters().Ceguera > 0) {
			getCounters().Ceguera--;
		} else {
			if (isBlind()) {
				makeNoBlind();
			} else {
				makeNoDumb();
			}
		}
	}

	private void makeNoBlind() {
		getFlags().Ceguera = false;
		sendPacket(new BlindNoMoreResponse());
	}

	public void makeNoDumb() {
		getFlags().Estupidez = false;
		sendPacket(new DumbNoMoreResponse());
	}
	
	public boolean isBlind() {
		return getFlags().Ceguera;
	}
	
	public boolean isDumb() {
		return getFlags().Estupidez;
	}
	
	public void makeDumb() {
		getFlags().Estupidez = true;
		getCounters().Ceguera = IntervaloInvisible;
		sendPacket(new DumbResponse());
	}

	public void efectoFrio() {
		if (this.getCounters().Frio < IntervaloFrio) {
			this.getCounters().Frio++;
		} else {
			Map mapa = this.server.getMap(pos().map);
			if (mapa.getTerrain() == Terrain.SNOW) {
				sendMessage("¡¡Estas muriendo de frio, abrígate o morirás!!.", FontType.FONTTYPE_INFO);
				int modifi = Util.percentage(getStats().MaxHP, 5);
				getStats().MinHP -= modifi;

				if (getStats().MinHP < 1) {
					sendMessage("¡¡Has muerto de frio!!.", FontType.FONTTYPE_INFO);
					getStats().MinHP = 0;
					userDie();
				}
			} else {
				if (!getStats().isTooTired()) { // Verificación agregada por gorlok
					int modifi = Util.percentage(getStats().maxStamina, 5);
					getStats().quitarStamina(modifi);
					sendMessage("¡¡Has perdido stamina, si no te abrigas rápido la perderás toda!!.", FontType.FONTTYPE_INFO);
				}
			}
			this.getCounters().Frio = 0;
			sendUpdateUserStats();
		}
	}

	public boolean sanar(int intervalo) {
		Map map = this.server.getMap(pos().map);
		
		if (!map.isOutdoor(pos().x, pos().y)) {
			return false;
		}
		
		// Con el paso del tiempo se va sanando... pero muy lentamente ;-)
		if (getStats().MinHP < getStats().MaxHP) {
			if (this.getCounters().HPCounter < intervalo) {
				this.getCounters().HPCounter++;
			} else {
				int mashit = Util.random(2, Util.percentage(getStats().maxStamina, 5));
				this.getCounters().HPCounter = 0;
				getStats().MinHP += mashit;

				if (getStats().MinHP > getStats().MaxHP) {
					getStats().MinHP = getStats().MaxHP;
				}
				sendMessage("Has sanado.", FontType.FONTTYPE_INFO);

				sendUpdateUserStats();
				return true;
			}
		}
		return false;
	}

	private boolean suerteMeditar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Meditar) / 10)]);
		return (Util.random(1, rango) == 1);
	}

	public void meditar() {
		long tActual = System.currentTimeMillis();
		if (tActual - this.getCounters().tInicioMeditar < TIEMPO_INICIO_MEDITAR) {
			return;
		}
		if (getStats().stamina < getStats().maxStamina) {
			return;
		}
		this.getCounters().IdleCount = 0;
		if (getStats().mana >= getStats().maxMana) {
			sendMessage("Has terminado de meditar.", FontType.FONTTYPE_INFO);
			sendPacket(new MeditateToggleResponse());
			getFlags().Meditando = false;
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
			sendCreateFX(0, 0);
			return;
		}
		if (suerteMeditar()) {
			int cant = Util.percentage(getStats().maxMana, 3);
			getStats().aumentarMana(cant);
			sendMessage("¡Has recuperado " + cant + " puntos de mana!", FontType.FONTTYPE_INFO);
			sendUpdateUserStats();
			riseSkill(Skill.SKILL_Meditar);
		}
	}

	private boolean efectoVeneno() {
		if (this.getCounters().Veneno < IntervaloVeneno) {
			this.getCounters().Veneno++;
		} else {
			sendMessage("Estas envenenado, si no te curas moriras.", FontType.FONTTYPE_VENENO);
			this.getCounters().Veneno = 0;
			getStats().MinHP -= Util.random(1, 5);
			sendUpdateUserStats();
			if (getStats().MinHP < 1) {
				userDie();
			}
			return true;
		}
		return false;
	}

	private void efectoInvisibilidad() {
		if (this.getCounters().Invisibilidad < IntervaloInvisible) {
			this.getCounters().Invisibilidad++;
		} else {
			turnVisible();
		}
	}

	public void duracionPociones() {
		// Controla la duracion de las pociones
		if (getFlags().DuracionEfecto > 0) {
			getFlags().DuracionEfecto--;
		} else if (getFlags().DuracionEfecto == 0) {
			getFlags().TomoPocion = false;
			getFlags().TipoPocion = 0;
			// Volver los atributos al estado normal
			getStats().attr().restoreAttributes();
		}
	}

	public void sendUserAttributes() {
		sendPacket(new AttributesResponse(
			getStats().attr().get(Attribute.STRENGTH),
			getStats().attr().get(Attribute.AGILITY),
			getStats().attr().get(Attribute.INTELIGENCE),
			getStats().attr().get(Attribute.CHARISMA),
			getStats().attr().get(Attribute.CONSTITUTION)));
	}

	public boolean updateHungerAndThirst() {
		// Sed
		boolean wasUpdated = false;
		if (getStats().drinked > 0) {
			if (this.getCounters().drinkCounter < IntervaloSed) {
				this.getCounters().drinkCounter++;
			} else {
				this.getCounters().drinkCounter = 0;
				getStats().quitarSed(10);
				if (getStats().drinked <= 0) {
					getStats().drinked = 0;
					getFlags().Sed = true;
				}
				wasUpdated = true;
			}
		}
		// hambre
		if (getStats().eaten > 0) {
			if (this.getCounters().foodCounter < IntervaloHambre) {
				this.getCounters().foodCounter++;
			} else {
				this.getCounters().foodCounter = 0;
				getStats().quitarHambre(10);
				if (getStats().eaten <= 0) {
					getStats().eaten = 0;
					getFlags().Hambre = true;
				}
				wasUpdated = true;
			}
		}
		return wasUpdated;
	}

	public boolean recStamina(int intervalo) {
		if (getStats().stamina < getStats().maxStamina) {
			if (getCounters().STACounter < intervalo) {
				getCounters().STACounter++;
			} else {
				getCounters().STACounter = 0;
				if (getFlags().Desnudo) {
					// Desnudo no sube energía.
					return false;
				}
				int massta = Util.random(1, Util.percentage(getStats().maxStamina, 5));
				getStats().aumentarStamina(massta);
				sendUpdateUserStats();
				return true;
			}
		}
		return false;
	}

	private void checkSummonTimeout() {
		// copy list of pets, to avoid concurrent issues
		var pets = List.copyOf(getUserPets().getPets());
		pets.forEach(pet -> {
			if (pet.counters().TiempoExistencia > 0) {
				pet.counters().TiempoExistencia--;
				if (pet.counters().TiempoExistencia == 0) {
					removePet(pet);
					pet.muereNpc(null);
				}
			}
		});
	}

	long lTirarBasura;

	/** Procesa eventos de los usuarios */
	public void procesarEventos() {
		// This is like GameTimer_Timer
		// Is user logged?
		if (getFlags().UserLogged) {
			boolean statsChanged = false;
			boolean hungerAndThristChanged = false;

			this.NumeroPaquetesPorMiliSec = 0;
			if (getFlags().Paralizado || getFlags().Inmovilizado) {
				paralizedEffect();
			}
			if (isBlind() || isDumb()) {
				efectoCegueEstu();
			}
			if (isAlive()) {
				if (getFlags().Desnudo && !getFlags().isGM()) {
					efectoFrio();
				}
				if (getFlags().Meditando) {
					meditar();
				}
				if (getFlags().Envenenado && !getFlags().isGM()) {
					statsChanged = efectoVeneno();
				}
				if (!getFlags().AdminInvisible && getFlags().Invisible) {
					efectoInvisibilidad();
				}
				duracionPociones();
				hungerAndThristChanged = updateHungerAndThirst();
				Map mapa = this.server.getMap(pos().map);
				if (!(this.server.isRaining() && mapa.isOutdoor(pos().x, pos().y))) {
					if (!getFlags().Descansar && !getFlags().Hambre && !getFlags().Sed) {
						// No esta descansando
						if (sanar(SanaIntervaloSinDescansar))
							statsChanged = true;
						if (recStamina(StaminaIntervaloSinDescansar))
							statsChanged = true;

					} else if (getFlags().Descansar) {
						// esta descansando

						if (sanar(SanaIntervaloDescansar))
							statsChanged = true;
						if (recStamina(StaminaIntervaloDescansar))
							statsChanged = true;

						// termina de descansar automaticamente
						if (getStats().MaxHP == getStats().MinHP && getStats().maxStamina == getStats().stamina) {
							sendPacket(new RestOKResponse());
							sendMessage("Has terminado de descansar.", FontType.FONTTYPE_INFO);
							getFlags().Descansar = false;
						}
					}
				}
				// Verificar muerte por hambre
				if (getStats().eaten <= 0 && !getFlags().isGM()) {
					sendMessage("¡¡Has muerto de hambre!!.", FontType.FONTTYPE_INFO);
					getStats().eaten = 0;
					userDie();
				}
				// Verificar muerte de sed
				if (getStats().drinked <= 0 && !getFlags().isGM()) {
					sendMessage("¡¡Has muerto de sed!!.", FontType.FONTTYPE_INFO);
					getStats().drinked = 0;
					userDie();
				}
				if (statsChanged) {
					sendUpdateUserStats();
				}
				if (hungerAndThristChanged) {
					sendUpdateHungerAndThirst();
				}
				if (getUserPets().hasPets()) {
					checkSummonTimeout();
				}
			}
		} else {
			this.getCounters().IdleCount++;
			if (this.getCounters().IdleCount > IntervaloParaConexion) {
				this.getCounters().IdleCount = 0;
				quitGame();
			}
		}
	}

	private String getTituloFaccion() {
		if (this.faction.ArmadaReal) {
			return " <Ejercito real> <" + this.faction.royalArmyTitle() + ">";
		} else if (this.faction.FuerzasCaos) {
			return " <Legión Oscura> <" + this.faction.darkLegionTitle() + ">";
		}
		return "";
	}

	public String userNameTagDesc() {
		var msg = new StringBuilder();
		if (showName) {
			msg.append(getUserName());
			
			if (this.description.length() > 0) {
				msg.append(" - " + this.description);
			}
			
			if (!getFlags().isGM() && isNewbie()) {
				msg.append(" <NEWBIE>");
			}
			
			msg.append(getTituloFaccion());
			
			if (getGuildInfo().esMiembroClan()) {
				msg.append(" <" + this.guildUser.getGuildName() + ">");
			}
			
			if (getFlags().isRoyalCouncil()) {
				msg.append(" [CONSEJO DE BANDERBILL]");
			} else if (getFlags().isChaosCouncil()) {
				msg.append(" [CONCILIO DE LAS SOMBRAS]");
			}
			
			if (getFlags().isGod() || getFlags().isDemiGod() || getFlags().isCounselor()) {
				msg.append(" <GAME MASTER>");
			} else if (isCriminal()) {
				msg.append(" <CRIMINAL>");
			} else {
				msg.append(" <CIUDADANO>");
			}
		}
		return msg.toString();
	}

	public FontType getTagColor() {
		if (getFlags().isRoyalCouncil()) {
			return FontType.FONTTYPE_CONSEJOVesA;
		} else if (getFlags().isChaosCouncil()) {
			return FontType.FONTTYPE_CONSEJOCAOSVesA;
		}
		
		
		if (getFlags().isGod()) {
			return FontType.FONTTYPE_DIOS;
		}
		if (getFlags().isDemiGod()) {
			return FontType.FONTTYPE_GM;
		}
		if (getFlags().isCounselor()) {
			return FontType.FONTTYPE_CONSEJO;
		}
		if (getFlags().isRoleMaster()) {
			return FontType.FONTTYPE_EJECUCION;
		}
		
		if (isCriminal()) {
			return FontType.FONTTYPE_FIGHT;
		}
		return FontType.FONTTYPE_CITIZEN;
	}

	public boolean genderCanUseItem(short objid) {
		ObjectInfo infoObj = findObj(objid);
		if (infoObj.esParaMujeres()) {
			return (this.gender == UserGender.GENERO_WOMAN);
		} else if (infoObj.esParaHombres()) {
			return (this.gender == UserGender.GENERO_MAN);
		} else {
			return true;
		}
	}

	public boolean checkRazaUsaRopa(short objid) {
		// Verifica si la raza puede usar esta ropa
		boolean canUse = false;

		var infoObj = findObj(objid);
		switch (this.race) {
			case RAZA_HUMAN:
			case RAZA_ELF:
			case RAZA_DROW:
				canUse = !infoObj.esParaRazaEnana();
				break;

			case RAZA_DWARF:
			case RAZA_GNOME:
				canUse = infoObj.esParaRazaEnana();
				break;
		}

		// La ropa para Drows, sólo la pueden usar los Drows
		if (infoObj.esParaRazaDrow() && this.race != UserRace.RAZA_DROW) {
			canUse = false;
		}

		return canUse;
	}

	public void volverCiudadano() {
		Map mapa = this.server.getMap(pos().map);
		if (mapa.isTournamentZone(pos().x, pos().y)) {
			return;
		}
		boolean eraCrimi = isCriminal();
		this.reputation.perdonar();
		if (eraCrimi) {
			refreshCharStatus();
		}

		System.out.println(this.getUserName() + "ahora es ciuda");
	}

	public void refreshCharStatus() {
		// RefreshCharStatus
		// Refreshes the status and tag of UserIndex.
		
		Map mapa = this.server.getMap(pos().map);
		mapa.sendToArea(pos().x, pos().y,
				new UpdateTagAndStatusResponse(getId(), isCriminal() ? (byte)1 : (byte)0, userNameTagDesc()));
        
        // Si esta navengando, se cambia la barca.
        if (isSailing()) {
        	if (!isAlive()) {
                infoChar().body = iFragataFantasmal;
        	} else {
        		ObjectInfo barco = getUserInv().getBarco();
                if (isRoyalArmy()) {
                    infoChar().body = iFragataReal;
                } else if (isDarkLegion()) {
                    infoChar().body = iFragataCaos;
                } else {
                    if (isCriminal()) {
                    	switch (barco.Ropaje) {
                            case iBarca:
                            	infoChar().body = iBarcaPk;
                                break;
                            case iGalera:
                            	infoChar().body = iGaleraPk;
                                break;
                            case iGaleon:
                            	infoChar().body = iGaleonPk;
                                break;
                    	}
                    } else {
                    	switch (barco.Ropaje) {
                        case iBarca:
                        	infoChar().body = iBarcaCiuda;
                            break;
                        case iGalera:
                        	infoChar().body = iGaleraCiuda;
                            break;
                        case iGaleon:
                        	infoChar().body = iGaleonCiuda;
                            break;
                    	}
                	}
                }
        	}
            sendCharacterChange();
        }
	}

	public void startQuitGame() {
		if (getFlags().UserLogged && !this.getCounters().Saliendo) {
			this.getCounters().Saliendo = true;
			Map mapa = this.server.getMap(pos().map);
			if (mapa != null && mapa.isSafeMap()) {
				this.getCounters().SalirCounter = 1; // 1 segundo.
			} else {
				this.getCounters().SalirCounter = IntervaloCerrarConexion; // 10 segundos
				sendMessage("Cerrando... Se cerrará el juego en " + IntervaloCerrarConexion + " segundos...",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void moveSpell(byte slot, byte dir) {
		if (dir != 1 && dir != -1) {
			return;
		}
		if (slot < 1 || slot > MAX_SPELLS) {
			return;
		}

		this.spells.moveSpell(slot, dir);
	}

    enum DuelStatus {
	    DUEL_ALLOWED 	/* TRIGGER6_PERMITE  1 */,
	    DUEL_FORBIDDEN 	/* TRIGGER6_PROHIBE  2 */,
	    DUEL_MISSING 	/* TRIGGER6_AUSENTE  3 */;
    }

	public DuelStatus duelStatus(User victima) {
		// triggerZonaPelea
		
		if (victima == null) {
			// there is no opponent
			return DuelStatus.DUEL_MISSING;
		}
		
		Trigger t1 = this.server.getMap(pos().map).getTrigger(pos().x, pos().y);
		Trigger t2 = this.server.getMap(victima.pos().map).getTrigger(victima.pos().x, victima.pos().y);
		
		if (t1 == Trigger.TRIGGER_ARENA_DUELOS || t2 == Trigger.TRIGGER_ARENA_DUELOS) {
			if (t1 == t2) {
				return DuelStatus.DUEL_ALLOWED;
			} else {
				return DuelStatus.DUEL_FORBIDDEN;
			}
		}
		return DuelStatus.DUEL_MISSING;
	}

	private void envenenarUsuario(User victima) {
		// UserEnvenenar
		ObjectInfo arma = this.userInv.getArma();
		if (arma != null) {
			if (arma.esProyectil()) {
				arma = this.userInv.getMunicion();
			}
			if (arma == null) {
				return;
			}
			if (arma.envenena()) {
				if (Util.random(1, 100) < 60) {
					victima.getFlags().Envenenado = true;
					victima.sendMessage(this.userName + " te ha envenenado!!", FONTTYPE_FIGHT);
					sendMessage("Has envenenado a " + victima.getUserName() + "!!", FONTTYPE_FIGHT);
				}
			}
		}
	}

	public static void changeGuildLeaderChr(String userName, boolean isLeader) {
		try {
			// ¿Existe el personaje?
			if (!Util.fileExists(getPjFile(userName))) {
				return;
			}
			IniFile ini = new IniFile(getPjFile(userName));
			ini.setValue("GUILD", "EsGuildLeader", isLeader);
			ini.store(getPjFile(userName));
		} catch (Exception e) {
			log.fatal(userName + ": ERROR EN SAVEUSER()", e);
		}
	}

	public static void changeGuildPtsChr(String userName, int addedGuildPts) {
		try {
			// ¿Existe el personaje?
			if (!Util.fileExists(getPjFile(userName))) {
				return;
			}
			IniFile ini = new IniFile(getPjFile(userName));
			long guildPoints = ini.getLong("Guild", "GuildPts");
			ini.setValue("GUILD", "GuildPts", guildPoints + addedGuildPts);
			ini.store(getPjFile(userName));
		} catch (Exception e) {
			log.fatal(userName + ": ERROR EN SAVEUSER()", e);
		}
	}

	/**
	 * Devuelve el skill de Herraria efectivo, aplicando su modificador de clase.
	 * @return valor del skill efectivo
	 */
	public double skillHerreriaEfectivo() {
		return this.skills().get(Skill.SKILL_Herreria) / this.clazz().modHerreria();
	}

	/**
	 * Devuelve el skill de Carpinteria efectivo, aplicando su modificador de clase.
	 * @return valor del skill efectivo
	 */
	public double skillCarpinteriaEfectivo() {
		return this.skills().get(Skill.SKILL_Carpinteria) / this.clazz().modCarpinteria();
	}

	public void agregarHechizo(int slot) {
		this.spells.addSpell(slot);
	}

	public void requestAccountState() {
		// Comando /BALANCE
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// Se asegura que el target es un npc
		if (getFlags().TargetNpc == 0) {
			sendMessage("Debes seleccionar un personaje.", FontType.FONTTYPE_INFO);
			return;
		}
		Npc npc = this.server.npcById(getFlags().TargetNpc);
		if (npc.pos().distance(pos()) > 3) {
			sendMessage("Estas demasiado lejos del vendedor.", FontType.FONTTYPE_INFO);
			return;
		}

		if (npc.isBankCashier() && (npc instanceof NpcCashier)) {
			((NpcCashier)npc).balance(this);
		} else if (npc.isGambler() && (npc instanceof NpcGambler)) {
			((NpcGambler)npc).balance(this);
		} else {
			sendTalk(Color.COLOR_BLANCO, "No entiendo de eso. Habla con alguien más.", getId());
		}
	}
	
	public void navigateToggleGM() {
		if (!isGM() || isCounselor()) {
			return;
		}
		
		getFlags().Navegando = !getFlags().Navegando;
		sendPacket(new NavigateToggleResponse());
	}

	public void ignoreToggleGM() {
        if (!isGM()) {
        	return;
        }
        
    	getFlags().AdminPerseguible = !getFlags().AdminPerseguible;
    	if (getFlags().AdminPerseguible) {
    		sendMessage("Los NPCs hostiles te perseguirán.", FontType.FONTTYPE_INFO);
    	} else {
    		sendMessage("Los NPCs hostiles te ignorarán.", FontType.FONTTYPE_INFO);
    	}
	}

	public void showNameToggleGM() {
        if (isGod() || isAdmin() || isRoleMaster()) {
        	showName = !showName; // Show/Hide the name
        	refreshCharStatus();
        }
    }

	public void changeChatColor(int red, int green, int blue) { 
		// HandleChatColor
		// /CHATCOLOR Change the user`s chat color
		if (!isGod() && !isAdmin() && !isRoleMaster()) {
			return;
		}
		this.chatColor = Color.rgb(red, green, blue);
	}

	public boolean isSilenced() {
		return getFlags().Silenciado;
	}

	public void turnSilence() {
		getFlags().Silenciado = true;
	}

	public void undoSilence() {
		getFlags().Silenciado = false;
	}

	public void denounce(String text) {
		// Command /DENUNCIAR
		if (isSilenced()) {
			sendMessage("Estás SILENCIADO y no puedes hacer Denuncias. " +
					"Puedes solicitar asistencia con /GM y serás atendido cuando alguien esté disponible.", 
					FontType.FONTTYPE_INFO);
		} else {
			server.sendToAdmins(new ConsoleMsgResponse(getUserName() + " DENUNCIA: " + text, 
					FontType.FONTTYPE_GUILDMSG.id()));
			sendMessage("Denuncia enviada, espere..", FontType.FONTTYPE_INFO);
		}
	}

	public void ping() {
		sendPacket(new PongResponse());
	}

	public void useSpellMacro(User user) {
		server.sendToAdmins(new ConsoleMsgResponse(user.getUserName() + 
				" fue expulsado por Anti-macro de hechizos", FontType.FONTTYPE_VENENO.id()));
        user.sendError("Has sido expulsado por usar macro de hechizos. Recomendamos leer el reglamento sobre el tema macros");
	}

}