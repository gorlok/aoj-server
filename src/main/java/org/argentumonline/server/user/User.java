/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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
import org.argentumonline.server.inventory.Inventory;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.inventory.UserInventory;
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapObject;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.map.Terrain;
import org.argentumonline.server.map.Zone;
import org.argentumonline.server.map.Tile.Trigger;
import org.argentumonline.server.net.ServerPacket;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcCashier;
import org.argentumonline.server.npc.NpcGambler;
import org.argentumonline.server.npc.NpcMerchant;
import org.argentumonline.server.npc.NpcTrainer;
import org.argentumonline.server.npc.NpcType;
import org.argentumonline.server.protocol.AttributesResponse;
import org.argentumonline.server.protocol.BankEndResponse;
import org.argentumonline.server.protocol.BankInitResponse;
import org.argentumonline.server.protocol.BankOKResponse;
import org.argentumonline.server.protocol.BlindNoMoreResponse;
import org.argentumonline.server.protocol.BlockPositionResponse;
import org.argentumonline.server.protocol.BlockedWithShieldOtherResponse;
import org.argentumonline.server.protocol.BlockedWithShieldUserResponse;
import org.argentumonline.server.protocol.ChangeBankSlotResponse;
import org.argentumonline.server.protocol.ChangeInventorySlotResponse;
import org.argentumonline.server.protocol.ChangeMapResponse;
import org.argentumonline.server.protocol.ChangeUserTradeSlotResponse;
import org.argentumonline.server.protocol.CharacterChangeResponse;
import org.argentumonline.server.protocol.CharacterCreateResponse;
import org.argentumonline.server.protocol.ChatOverHeadResponse;
import org.argentumonline.server.protocol.CommerceEndResponse;
import org.argentumonline.server.protocol.CommerceInitResponse;
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
	
	int chatColor = Color.COLOR_BLANCO;

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

	UserCounters counters = new UserCounters();

	public UserTrade userTrade;

	public UserStorage userStorage;

	private UserQuest quest;

	UserInventory userInv;
	Inventory bankInv;
	
	// TODO
    public short partyIndex = 0;  // index a la party q es miembro
    public short partySolicitud = 0; // index a la party q solicito
    
	SpeedHackCheck speedHackMover = new SpeedHackCheck("SpeedHack de mover");
	
	GameServer server;
	
	public User(GameServer aoserver) {
		init(aoserver);
		this.userTrade = new UserTrade(this);
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
		this.bankInv = new Inventory(this.server, MAX_BANCOINVENTORY_SLOTS);
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

	public Inventory getBankInventory() {
		return this.bankInv;
	}

	public UserPets getUserPets() {
		return this.userPets;
	}

	public GuildUser guildInfo() {
		return this.guildUser;
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

	public String getNick() {
		return this.userName;
	}

	public boolean hasNick() {
		return getNick() != null && !getNick().isEmpty();
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
		return flags().isGM();
	}
	
	public boolean isGod() {
		return flags().isGod();
	}
	
	public boolean isAdmin() {
		return flags().isAdmin();
	}
	
	public boolean isDemiGod() {
		return flags().isDemiGod();
	}
	
	public boolean isCounselor() {
		return flags().isCounselor();
	}
	
	public boolean isRoleMaster() {
		return flags().isRoleMaster();
	}
	
	public boolean isChaosCouncil() {
		return flags().isChaosCouncil(); 
	}

	public boolean isRoyalCouncil() {
		return flags().isRoyalCouncil(); 
	}
	
	
	public boolean isAlive() {
		return !flags().Muerto;
	}

	public boolean isInvisible() {
		return flags().Invisible;
	}

	public boolean isTrading() {
		return flags().Comerciando;
	}

	public boolean isHidden() {
		return flags().Oculto;
	}
	
	public boolean isAllowingChase() {
		return flags().AdminPerseguible;
	}

	public boolean isSailing() {
		return flags().Navegando;
	}

	public boolean isWorking() {
		return flags().Trabajando;
	}

	public UserStats stats() {
		return this.stats;
	}

	public Reputation reputation() {
		return this.reputation;
	}

	public UserFlags flags() {
		return this.flags;
	}

	public UserCounters counters() {
		return this.counters;
	}

	@Override
	public String toString() {
		return "User(id=" + getId() + ",nick=" + this.userName + ")";
	}

	public UserFaction userFaction() {
		return this.faction;
	}

	public UserInventory userInv() {
		return this.userInv;
	}

	public boolean hasSafeLock() {
		return flags().Seguro;
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
		if (flags().TargetObj == 0) {
			return;
		}
		ObjectInfo iobj = findObj(flags().TargetObj);
		if (iobj.esForo()) {
			this.server.getForumManager().postOnForum(iobj.ForoID, title, body, getNick());
		}
	}

	public void enlist() {
		// Comando /ENLISTAR
		if (!checkAlive("��Estas muerto!! Busca un sacerdote y no me hagas perder el tiempo.")) {
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
			talk(COLOR_BLANCO, "Jeje, ac�rcate o no podr� escucharte. �Est�s demasiado lejos!", npc.getId());
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
		if (!checkAlive("��Est�s muerto!! Busca un sacerdote y no me hagas perder el tiempo!")) {
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

	public void sendMiniStats() { // hazte fama y �chate a dormir...
		sendPacket(new MiniStatsResponse(
				(int) this.faction.citizensKilled,
				(int) this.faction.criminalsKilled,
				stats().usuariosMatados,
				(short) stats().NPCsMuertos,
				this.clazz.id(),
				(int) this.counters.Pena));

		sendUserAttributes();
		sendSkills();
		sendFame();
	}

	public boolean isLogged() {
		return flags().UserLogged;
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
			npc.expresar();
		}
	}

	public void petFollowMaster() {
		// Comando /ACOMPA�AR
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
			npc.expresar();
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
		// �Esta el user muerto? Si es asi no puede comerciar
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
		// Salir de la facci�n Armada/Caos
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
					talk(COLOR_BLANCO, "Ser�s bienvenido a las fuerzas imperiales si deseas regresar.", npc.getId());
				} else {
					talk(COLOR_BLANCO, "�Sal de aqu� buf�n!", npc.getId());
				}
			} else if (isDarkLegion()) {
				if (npc.isFaction()) {
					this.faction.darkLegionKick();
					talk(COLOR_BLANCO, "Ya volver�s arrastr�ndote.", npc.getId());
				} else {
					talk(COLOR_BLANCO, "�Sal de aqu� maldito criminal!", npc.getId());
				}
			} else {
				talk(COLOR_BLANCO, "�No perteneces a ninguna fuerza!", npc.getId());
			}
		}
	}

	public void bankStart() {
		// Comando /BOVEDA
		// Abrir b�veda del banco.
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			if (npc.isBankCashier()) {
				iniciarDeposito();
			} else {
				sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}


	public Npc getNearNpcSelected(int distance) {
		// Se asegura que el target es un npc
		if (flags().TargetNpc == 0) {
			sendMessage("Debes seleccionar un personaje cercano para poder interactuar.", FontType.FONTTYPE_INFO);
		} else {
			Npc npc = this.server.npcById(flags().TargetNpc);
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
		return checkAlive("��Est�s muerto!!");
	}

	public boolean checkAlive(String message) {
		if (isAlive()) {
			return true;
		} else {
			sendMessage(message, FontType.FONTTYPE_INFO);
			return false;
		}
	}

	public void bankDepositItem(short slot, int amount) {
		// Comando DEPO
		// Depositar un item en la b�veda del banco.
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			// �El Npc puede comerciar?
			if (npc.isBankCashier()) {
				// User deposita el item del slot rdata
				userDepositaItem(slot, amount);
			} else {
				sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}

	public void bankExtractItem(short slot, int cant) {
		// Comando RETI
		// Retirar un item de la b�veda del banco.
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			// �El Npc puede comerciar?
			if (npc.isBankCashier()) {
				// User retira el item del slot rdata
				userRetiraItem(slot, cant);
			} else {
				sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}

	private void userDepositaItem(short slot, int cant) {
		// El usuario deposita un item
		sendUpdateUserStats();
		if (this.userInv.getObject(slot).cant > 0 && !this.userInv.getObject(slot).equipado) {
			if (cant > 0 && cant > this.userInv.getObject(slot).cant) {
				cant = this.userInv.getObject(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userDejaObj(slot, cant);
			// Actualizamos el inventario del usuario
			sendInventoryToUser();
			// Actualizamos el inventario del banco
			updateBankUserInv();
			// Actualizamos la ventana del banco
			sendBankOk();
		}
	}

	private void userDejaObj(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		short objid = this.userInv.getObject(slot).objid;
		// �Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (int i = 1; i <= this.bankInv.getSize(); i++) {
			if (this.bankInv.getObject(i).objid == objid && this.bankInv.getObject(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio antes del slot devuelto
		if (slot_inv == 0) {
			slot_inv = this.bankInv.getEmptySlot();
		}
		if (slot_inv == 0) {
			sendMessage("No tienes mas espacio en el banco!!", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		this.bankInv.getObject(slot_inv).objid = objid;
		this.bankInv.getObject(slot_inv).cant += cant;
		this.userInv.quitarUserInvItem(slot, cant);
	}

	private void userRetiraItem(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		sendUpdateUserStats();
		if (this.bankInv.getObject(slot).cant > 0) {
			if (cant > this.bankInv.getObject(slot).cant) {
				cant = this.bankInv.getObject(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userReciveObj(slot, cant);
			// Actualizamos el inventario del usuario
			sendInventoryToUser();
			// Actualizamos el banco
			updateBankUserInv();
			// ventana update
			sendBankOk();
		}

	}

	private void userReciveObj(short slot, int cant) {
		if (this.bankInv.getObject(slot).cant <= 0) {
			return;
		}
		short objid = this.bankInv.getObject(slot).objid;
		// �Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= this.userInv.getSize(); i++) {
			if (this.userInv.getObject(i).objid == objid && this.userInv.getObject(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = this.userInv.getEmptySlot();
		}
		if (slot_inv == 0) {
			sendMessage("No pod�s tener mas objetos.", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		if (this.userInv.getObject(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			this.userInv.getObject(slot_inv).objid = objid;
			this.userInv.getObject(slot_inv).cant += cant;
			quitarBancoInvItem(slot, cant);
		} else {
			sendMessage("No pod�s tener mas objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private void quitarBancoInvItem(short slot, int cant) {
		// Quita un Obj
		this.bankInv.getObject(slot).cant -= cant;
		if (this.bankInv.getObject(slot).cant <= 0) {
			this.bankInv.getObject(slot).objid = 0;
			this.bankInv.getObject(slot).cant = 0;
		}
	}

	private void sendBankOk() {
		sendPacket(new BankOKResponse());
	}

	private void iniciarDeposito() {
		// Hacemos un Update del inventario del usuario
		updateBankUserInv();
		// Actualizamos el dinero
		sendUpdateUserStats();

		sendPacket(new BankInitResponse());

		flags().Comerciando = true;
	}

	private void updateBankUserInv(short slot) {
		// Actualiza un solo slot
		// Actualiza el inventario
		if (this.bankInv.getObject(slot).objid > 0) {
			sendBanObj(slot, this.bankInv.getObject(slot));
		} else {
			sendPacket(new ChangeBankSlotResponse((byte) slot, (short) 0, "", (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0, 0));
		}
	}

	private void updateBankUserInv() {
		// Actualiza todos los slots
		for (short i = 1; i <= MAX_BANCOINVENTORY_SLOTS; i++) {
			// Actualiza el inventario
			updateBankUserInv(i);
		}
	}

	private void sendBanObj(short slot, InventoryObject obj_inv) {
		if (obj_inv != null) {
			ObjectInfo info = findObj(obj_inv.objid);
			sendPacket(new ChangeBankSlotResponse(
					(byte) slot, info.ObjIndex, info.Nombre, (short)obj_inv.cant, info.GrhIndex,
					info.objType.value(), info.MaxHIT, info.MinHIT, info.MaxDef, info.Valor));
		}
	}

	public void bankEnd() {
		// Comando FINBAN
		// User sale del modo BANCO
		flags().Comerciando = false;
		sendPacket(new BankEndResponse());
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
			sendMessage("El password ha sido cambiado. �Cuidalo!", FontType.FONTTYPE_INFO);
		}
	}

	public void craftBlacksmith(short objid) {
		if (objid < 1) {
			return;
		}
		ObjectInfo info = findObj(objid);
		if (info.SkHerreria == 0) {
			return;
		}
		herreroConstruirItem(objid);
	}

	public void craftCarpenter(short objid) {
		if (objid < 1) {
			return;
		}
		ObjectInfo info = findObj(objid);
		if (info.SkCarpinteria == 0) {
			return;
		}
		carpinteroConstruirItem(objid);
	}

	public void userTrainWithPet(byte petIndex) {
		// Comando ENTR
		// Entrenar con una mascota.
		if (!checkAlive()) {
			return;
		}
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(flags().TargetNpc);
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

	public void commerceBuyFromMerchant(byte slotNpc, short amount) {
		// Comando COMP
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// �El target es un Npc valido?
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(flags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		// �El Npc puede comerciar?
		if (!npc.isTrade()) {
			talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}
		if (npc.npcInv().isValidSlot(slotNpc)) {
			((NpcMerchant)npc).sellItemToUser(this, slotNpc, amount);
		}
	}

	public void commerceSellToMerchant(byte slot, short amount) {
		// Comando VEND
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// �El target es un Npc valido?
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(flags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		// �El Npc puede comerciar?
		if (!npc.isTrade()) {
			talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}

		if (npc.npcInv().isValidSlot(slot)) {
			((NpcMerchant)npc).buyItemFromUser(this, slot, amount);
		}
	}

	public void updateVentanaComercio(short objIndex, int amount) {
		ObjectInfo objInfo = (objIndex == 0) ? ObjectInfo.EMPTY : findObj(objIndex);
		sendPacket(new ChangeUserTradeSlotResponse(
				objInfo.ObjIndex,
				objInfo.Nombre,
				amount,
				objInfo.GrhIndex,
				objInfo.objType.value(),
				objInfo.MaxHIT,
				objInfo.MinHIT,
				objInfo.Def,
				objInfo.Valor));
	}

	public void commerceEnd() {
		flags().Comerciando = false;
		sendPacket(new CommerceEndResponse());
	}

	public double descuento() {
		// Establece el descuento en funcion del skill comercio
		final double indicesDto[] = {
				1.0, // 0-5
				1.1, 1.1, // 6-10
				1.2, 1.2, // 11-20
				1.3, 1.3, // 21-30
				1.4, 1.4, // 31-40
				1.5, 1.5, // 41-50
				1.6, 1.6, // 51-60
				1.7, 1.7, // 61-70
				1.8, 1.8, // 71-80
				1.9, 1.9, // 81-90
				2.0, 2.0 // 91-100
		};
		int ptsComercio = skills().get(Skill.SKILL_Comerciar);
		flags().Descuento = indicesDto[(short) (ptsComercio / 5)];
		return flags().Descuento;
	}

	public void commerceStart() {
		// Comando /COMERCIAR
		if (!checkAlive()) {
			return;
		}
		if (flags().isCounselor()) {
			return;
		}

		if (isTrading()) {
			sendMessage("Ya est�s comerciando", FontType.FONTTYPE_INFO);
			return;
		}

		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_MERCHANT);
		if (npc != null) {
			// �El Npc puede comerciar?
			if (!npc.isTrade()) {
				if (npc.getDesc().length() > 0) {
					talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
				}
				return;
			}

			// Mandamos el Inventario
			((NpcMerchant)npc).sendNpcInventoryToUser(this);
			sendInventoryToUser();
			sendUpdateUserStats();

			// Iniciamos el comercio con el Npc
			sendPacket(new CommerceInitResponse());
			flags().Comerciando = true;
		}

		if (flags().TargetUser > 0) {
            //User commerce...
            //Can he commerce??
			if (flags().isCounselor()) {
				sendMessage("No puedes vender items.", FontType.FONTTYPE_WARNING);
                return;
			}

			User targetUser = this.server.userById(flags().TargetUser);
			if (targetUser == null) {
				return;
			}
            //Is the other one dead??
			if (!targetUser.isAlive()) {
				sendMessage("��No puedes comerciar con los muertos!!", FontType.FONTTYPE_INFO);
                return;
			}

            //Is it me??
			if (targetUser == this) {
				sendMessage("No puedes comerciar con vos mismo...", FontType.FONTTYPE_INFO);
                return;
			}

            //Check distance
			if (pos().distance(targetUser.pos()) > 3) {
				sendMessage("Est�s demasiado lejos del usuario.", FontType.FONTTYPE_INFO);
                return;
			}

            //Is he already trading?? is it with me or someone else??
			if (targetUser.isTrading() && targetUser.flags().TargetUser != this.getId()) {
				sendMessage("No puedes comerciar con el usuario en este momento.", FontType.FONTTYPE_INFO);
			}

            //Initialize some variables...
			this.userTrade.destUsu = flags().TargetUser;
			this.userTrade.cant = 0;
			this.userTrade.objectSlot = 0;
			this.userTrade.acepto = false;

            //Rutina para comerciar con otro usuario
            this.userTrade.iniciarComercioConUsuario(flags().TargetUser);
		}

		sendMessage("Primero haz click izquierdo sobre el personaje.", FontType.FONTTYPE_INFO);
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
		if (!checkAlive("��Estas muerto!! Solo podes usar items cuando estas vivo.")) {
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (mapa.hasObject(pos().x, pos().y) && mapa.getObject(pos().x, pos().y).obj_ind == FOGATA) {
			sendPacket(new RestOKResponse());
			if (!flags().Descansar) {
				sendMessage("Te acomodas junto a la fogata y comienzas a descansar.", FontType.FONTTYPE_INFO);
			} else {
				sendMessage("Te levantas.", FontType.FONTTYPE_INFO);
			}
			flags().Descansar = !flags().Descansar;
		} else {
			if (flags().Descansar) {
				sendMessage("Te levantas.", FontType.FONTTYPE_INFO);
				flags().Descansar = false;
				sendPacket(new RestOKResponse());
				return;
			}
			sendMessage("No hay ninguna fogata junto a la cual descansar.", FontType.FONTTYPE_INFO);
		}
	}

	public void sendFame() {
		sendPacket(reputation().createFameResponse());
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
		
		if (!flags().Navegando) {
			this.infoChar.head = 0;
			if (isAlive()) {
				this.infoChar.body = barco.Ropaje;
			} else {
				this.infoChar.body = OBJ_INDEX_FRAGATA_FANTASMAL;
			}
			this.infoChar.shield = NingunEscudo;
			this.infoChar.weapon = NingunArma;
			this.infoChar.helmet = NingunCasco;
			flags().Navegando = true;
		} else {
			flags().Navegando = false;
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
		MapPos targetPos = MapPos.mxy(flags().TargetObjMap, flags().TargetObjX, flags().TargetObjY);
		Map mapa = this.server.getMap(targetPos.map);
		if (!mapa.isLegalPos(targetPos, false, true)) {
			return;
		}

		if (!isAlive()) {
			sendMessage("No puedes hacer fogatas estando muerto.", FontType.FONTTYPE_INFO);
			return;
		}

		if (mapa.getObject(targetPos.x, targetPos.y).obj_ind != Constants.Le�a) {
			sendMessage("Necesitas clickear sobre Le�a para hacer ramitas", FontType.FONTTYPE_INFO);
		    return;
		}

		if (pos().distance(targetPos) > 2) {
			sendMessage("Est�s demasiado lejos para prender la fogata.", FontType.FONTTYPE_INFO);
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
			if (flags().UltimoMensaje != 10) {
				sendMessage("No has podido hacer la fogata.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 10;
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
		if (this.userInv.getObject(flags().TargetObjInvSlot).cant < 5) {
			sendMessage("No tienes suficientes minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		ObjectInfo info = findObj(flags().TargetObjInvIndex);
		if (info.objType != ObjType.Minerales) {
			sendMessage("Debes utilizar minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		this.userInv.quitarUserInvItem(flags().TargetObjInvSlot, 5);
		sendInventorySlot(flags().TargetObjInvSlot);
		if (Util.random(1, info.MinSkill) <= 10) {
			sendMessage("Has obtenido un lingote!!!", FontType.FONTTYPE_INFO);
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(info.LingoteIndex, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(info.LingoteIndex, 1));
			}
			sendMessage("�Has obtenido un lingote!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 7) {
				sendMessage("Los minerales no eran de buena calidad, no has logrado hacer un lingote.",
						FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 7;
			}
		}
		flags().Trabajando = true;
	}

	/**
	 * Fundir mineral
	 */
	private void meltOre() {
		if (flags().TargetObjInvIndex > 0) {
			ObjectInfo info = findObj(flags().TargetObjInvIndex);
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
			sendMessage("No puedes domar m�s de una criatura del mismo tipo.", FontType.FONTTYPE_INFO);
			return;
	    }

	    
	    int puntosDomar = stats().attr().get(Attribute.CHARISMA) * skills().get(Skill.SKILL_Domar);
	    int puntosRequeridos = npc.domable();
	    if (clazz() == Clazz.Druid && userInv().tieneAnilloEquipado() && userInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
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
        		sendMessage("No se permiten mascotas en zonas seguras. Te esperar� afuera.", FontType.FONTTYPE_INFO);
	        }
			
		} else {
			if (flags().UltimoMensaje != 5) {
				sendMessage("No has logrado domar la criatura.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 5;
			}
		}
	    
	    // Entreno domar. Es un 30% m�s dificil si no sos druida.
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
	 * Hacer miner�a
	 */
	private void mining() {
		stats().quitarStamina(this.clazz().getEsfuerzoExcavar());
		if (suerteMineria()) {
			if (flags().TargetObj == 0) {
				return;
			}
			short objid = findObj(flags().TargetObj).MineralIndex;
			int cant = this.clazz().getCantMinerales();
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				Map mapa = this.server.getMap(pos().map);
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("�Has extraido algunos minerales!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 9) {
				sendMessage("�No has conseguido nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 9;
			}
		}
		riseSkill(Skill.SKILL_Mineria);
		flags().Trabajando = true;
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
		stats().quitarStamina(this.clazz().getEsfuerzoTalar());
		if (suerteTalar()) {
			int cant = this.clazz().getCantLe�os();
			short objid = Le�a;
			Map mapa = this.server.getMap(pos().map);
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("�Has conseguido algo de le�a!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 8) {
				sendMessage("No has conseguido le�a. Intenta otra vez.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 8;
			}
		}
		riseSkill(Skill.SKILL_Talar);
		flags().Trabajando = true;
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
		if (victima.flags().isGM() || this.flags().isGM()) {
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
				if (victima.stats().getGold() > 0) {
					int cantidadRobada = Util.random(1, 100);
					victima.stats().addGold( -cantidadRobada );
					stats().addGold( cantidadRobada );
					sendMessage("Le has robado " + cantidadRobada + " monedas de oro a " + victima.userName, FontType.FONTTYPE_INFO);
				} else {
					sendMessage(this.userName + " no tiene oro.", FontType.FONTTYPE_INFO);
				}
			}
		} else {
			sendMessage("�No has logrado robar nada!", FontType.FONTTYPE_INFO);
			victima.sendMessage("�" + this.userName + " ha intentado robarte!", FontType.FONTTYPE_INFO);
			victima.sendMessage("�" + this.userName + " es un criminal!", FontType.FONTTYPE_INFO);
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
	 * Pescar con ca�a
	 */
	private void fishingWithRod() {
		stats().quitarStamina(this.clazz().getEsfuerzoPescar());
		if (fishingWithRodLucky()) {
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(OBJ_PESCADO, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(OBJ_PESCADO, 1));
			}
			sendMessage("�Has pescado un lindo pez!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 6) {
				sendMessage("�No has pescado nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 6;
			}
		}
		riseSkill(Skill.SKILL_Pesca);
		flags().Trabajando = true;
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
		stats().quitarStamina(this.clazz().getEsfuerzoPescar());
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
			sendMessage("�Has pescado algunos peces!", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("�No has pescado nada!", FontType.FONTTYPE_INFO);
		}
		riseSkill(Skill.SKILL_Pesca);
	}

	public void workLeftClick(byte x, byte y, byte skill) {
		Pos pos = new Pos(x, y);
		if (!isAlive() || flags().Descansar || flags().Meditando || !pos.isValid()) {
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
			if (flags().TargetObj > 0) {
				if (findObj(flags().TargetObj).objType == ObjType.Fragua) {
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
				if (stats().stamina >= 10) {
					stats().quitarStamina(Util.random(1, 10));
				} else {
					if (gender() == UserGender.GENERO_MAN) {
						sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
					} else {
						sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
					}					
					return;
				}

				mapa.lookAtTile(this, x, y);
				User targetUser = this.server.userById(flags().TargetUser);
				Npc npc = this.server.npcById(flags().TargetNpc);
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
					if (flags().Seguro) {
						if (!targetUser.isCriminal()) {
							sendMessage(
									"No puedes atacar a ciudadanos. Para hacerlo, antes debes desactivar el seguro.",
									FONTTYPE_FIGHT);
							return;
						}
					}
					usuarioAtacaUsuario(targetUser);
				}
				// Consumir munici�n.
				int slotMunicion = this.userInv.getMunicionSlot();
				this.userInv.quitarUserInvItem(this.userInv.getMunicionSlot(), 1);
				if (this.userInv.getObject(slotMunicion) != null && this.userInv.getObject(slotMunicion).cant > 0) {
					this.userInv.equipar(slotMunicion);
				}
				sendInventorySlot(slotMunicion);
				break;


			case SKILL_Magia:
				if (!counters().intervaloPermiteLanzarSpell()) {
					return;
				}
				if (flags().isCounselor())
					return;
				mapa.lookAtTile(this, x, y);
				if (flags().Hechizo > 0) {
					this.spells.hitSpell(this.server.getSpell(flags().Hechizo));
					flags().Hechizo = 0;
				} else {
					sendMessage("�Primero selecciona el hechizo que deseas lanzar!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Pesca:
				if (!counters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					sendMessage("Deber�as equiparte una ca�a o una red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (this.userInv.getArma().ObjIndex != OBJ_INDEX_CA�A
						&& this.userInv.getArma().ObjIndex != OBJ_INDEX_RED_PESCA) {
					sendMessage("Deber�as equiparte una ca�a o una red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isUnderRoof(pos().x,  pos().y)) {
					sendMessage("No puedes pescar desde d�nde te encuentras.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isWater(x, y)) {
					sendWave(SOUND_PESCAR);
					switch (this.userInv.getArma().ObjIndex) {
					case OBJ_INDEX_CA�A:
						fishingWithRod();
						break;
					case OBJ_INDEX_RED_PESCA:
						if (pos().distance(MapPos.mxy(pos().map, x, y)) > 2) {
							sendMessage("Est�s demasiado lejos para pescar.", FontType.FONTTYPE_INFO);
							return;
						}
						fishingWithNet();
						break;
					}
				} else {
					sendMessage("No hay agua donde pescar. Busca un lago, r�o o mar.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Robar:
				if (!mapa.isSafeMap()) {
					if (!counters().intervaloPermiteTrabajar()) {
						return;
					}
					mapa.lookAtTile(this, x, y);
					if (flags().TargetUser > 0 && flags().TargetUser != getId()) {
						targetUser = this.server.userById(flags().TargetUser);
						if (targetUser.isAlive()) {
							MapPos wpaux = MapPos.mxy(pos().map, x, y);
							if (wpaux.distance(pos()) > 2) {
								sendMessage("Est�s demasiado lejos.", FontType.FONTTYPE_INFO);
								return;
							}
							// Nos aseguramos que el trigger le permite robar
							if (mapa.isSafeZone(targetUser.pos().x, targetUser.pos().y)) {
								sendMessage("No puedes robar aqu�.", FontType.FONTTYPE_WARNING);
								return;
							}
							steal(targetUser);
						}
					} else {
						sendMessage("�No hay a qui�n robarle!", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("�No puedes robar en zonas seguras!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Talar:
				if (!counters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					sendMessage("Deber�as equiparte el hacha de le�ador.", FontType.FONTTYPE_INFO);
					return;
				}
				if (this.userInv.getArma().ObjIndex != HACHA_LE�ADOR) {
					sendMessage("Deber�as equiparte el hacha de le�ador.", FontType.FONTTYPE_INFO);
					return;
				}
				obj = mapa.getObject(x, y);
				if (obj != null) {
					MapPos wpaux = MapPos.mxy(pos().map, x, y);
					if (wpaux.distance(pos()) > 2) {
						sendMessage("Est�s demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// �Hay un arbol donde cliqueo?
					if (obj.objInfo().objType == ObjType.Arboles) {
						sendWave(SOUND_TALAR);
						cutWood();
					}
				} else {
					sendMessage("No hay ning�n �rbol ah�.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Mineria:
				if (!counters().intervaloPermiteTrabajar()) {
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
						sendMessage("Est�s demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// �Hay un yacimiento donde cliqueo?
					if (obj.objInfo().objType == ObjType.Yacimiento) {
						sendWave(SOUND_MINERO);
						mining();
					} else {
						sendMessage("Ah� no hay ning�n yacimiento.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ah� no hay ning�n yacimiento.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Domar:
				mapa.lookAtTile(this, x, y);
				if (flags().TargetNpc > 0) {
					npc = this.server.npcById(flags().TargetNpc);
					if (npc.domable() > 0) {
						MapPos wpaux = MapPos.mxy(pos().map, x, y);
						if (wpaux.distance(pos()) > 2) {
							sendMessage("Est�s demasiado lejos.", FontType.FONTTYPE_INFO);
							return;
						}
						if (npc.isAttackedByUser()) {
							sendMessage("No puedes domar una criatura que est� luchando con un jugador.", FontType.FONTTYPE_INFO);
							return;
						}
						tameCreature(npc);
					} else {
						sendMessage("No puedes domar a esa criatura.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("�No hay ninguna criatura alli!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Herreria:
				mapa.lookAtTile(this, x, y);
				if (flags().TargetObj > 0) {
					if (findObj(flags().TargetObj).objType == ObjType.Yunque) {
						this.userInv.sendBlacksmithWeapons();
						this.userInv.sendBlacksmithArmors();
						sendPacket(new ShowBlacksmithFormResponse());
					} else {
						sendMessage("Ah� no hay ning�n yunque.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ah� no hay ning�n yunque.", FontType.FONTTYPE_INFO);
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
			if (flags().Navegando) {
				if (flags().UltimoMensaje != 3) {
					sendMessage("No puedes ocultarte si estas navegando.", FontType.FONTTYPE_INFO);
					flags().UltimoMensaje = 3;
				}
				return;
			}
			if (flags().Oculto) {
				if (flags().UltimoMensaje != 2) {
					sendMessage("Ya estabas oculto.", FontType.FONTTYPE_INFO);
					flags().UltimoMensaje = 2;
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
		if (!checkAlive("��Estas muerto!! Solo los vivos pueden meditar.")) {
			return;
		}
		sendPacket(new MeditateToggleResponse());
		if (!flags().Meditando) {
			sendMessage("Comienzas a meditar.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Dejas de meditar.", FontType.FONTTYPE_INFO);
		}
		flags().Meditando = !flags().Meditando;
		if (flags().Meditando) {
			this.counters.tInicioMeditar = System.currentTimeMillis();
			int segs = (TIEMPO_INICIO_MEDITAR / 1000);
			sendMessage("Te est�s concentrando. En " + segs + " segundos comenzar�s a meditar.", FontType.FONTTYPE_INFO);
			this.infoChar.loops = LoopAdEternum;
			if (stats().ELV < 15) {
				sendCreateFX(FXMEDITARCHICO, LoopAdEternum);
				this.infoChar.fx = FXMEDITARCHICO;
			} else if (stats().ELV < 30) {
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
			sendMessage("Lo siento, s�lo puedo resucitar newbies.", FontType.FONTTYPE_INFO);
			return;
		}

		if (isAlive()) {
			sendMessage("�JA! Debes estar muerto para resucitarte.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!userExists()) {
			sendMessage("!!El personaje no existe, cree uno nuevo.", FontType.FONTTYPE_INFO);
			quitGame();
			return;
		}
		revive();
		sendMessage("��Has sido resucitado!!", FontType.FONTTYPE_INFO);
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
		if (!checkAlive("�Solo puedo curar a los vivos! �Resuc�tate primero!")) {
			return;
		}
		stats().MinHP = stats().MaxHP;
		sendUpdateUserStats();
		sendMessage("��Has sido curado!!", FontType.FONTTYPE_INFO);
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
        	User targetUser = server.userById(flags().TargetUser);
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
		
		if (flags().Inmovilizado) {
			return;
		}
		
		int posX = 0;
		int posY = 0;
		Heading heading = Heading.value(newHeading);
        if (flags().Paralizado && !flags().Inmovilizado) {
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
		log.info("saliendo: " + this.getNick());
		boolean wasLogged = flags().UserLogged;
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
			flags().UserLogged = false;
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
		log.info("Sali�: " + getNick());
		server.getWorkWatcher().userLogout(this);
	}

	public void throwDices() { // and get lucky!

		// FIXME dados f�ciles, hacerlo configurable
		stats().attr().set(Attribute.STRENGTH, Util.random(16, 18));
		stats().attr().set(Attribute.AGILITY, Util.random(16, 18));
		stats().attr().set(Attribute.INTELIGENCE, Util.random(16, 18));
		stats().attr().set(Attribute.CHARISMA, Util.random(16, 18));
		stats().attr().set(Attribute.CONSTITUTION, Util.random(16, 18));

		sendPacket(new DiceRollResponse(
			stats().attr().get(Attribute.STRENGTH),
			stats().attr().get(Attribute.AGILITY),
			stats().attr().get(Attribute.INTELIGENCE),
			stats().attr().get(Attribute.CHARISMA),
			stats().attr().get(Attribute.CONSTITUTION)));
	}

	/**
	 * Procesa el clic izquierdo del mouse sobre el mapa
	 *
	 * @param x es la posici�n x del clic
	 * @param y es la posici�n y del clic
	 */
	public void leftClickOnMap(byte x, byte y) {
		// Clic con el bot�n primario del mouse
		Map map = this.server.getMap(pos().map);
		if (map != null) {
			map.lookAtTile(this, x, y);
		}
	}

	/**
	 * Procesa el clic derecho del mouse sobre el mapa
	 *
	 * @param x es la posici�n x del clic
	 * @param y es la posici�n y del clic
	 */
	public void clicDerechoMapa(byte x, byte y) {
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		// �Posicion valida?
		if (Pos.isValid(x, y)) {
			// �Hay un objeto en el tile?
			if (mapa.hasObject(x, y)) {
				MapObject obj = mapa.getObject(x, y);
				flags().TargetObj = obj.objInfo().ObjIndex;
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
				case Le�a:
					if (flags().TargetObj == FOGATA_APAG) {
						mapa.accionParaRamita(x, y, this);
					}
					break;
				default:
					break;
				}
			} else {
				// �Hay un objeto que ocupa m�s de un tile?
				MapObject obj = mapa.lookForNearbyObject(x, y);
				if (obj != null) {
					flags().TargetObj = obj.objInfo().ObjIndex;
					if (obj.objInfo().objType == ObjType.Puertas) {
						mapa.accionParaPuerta(obj.x, obj.y, this);
						return;
					}
				} 
				
				// Hay un npc?
				Npc npc;
				if ((npc = mapa.getNPC(x, y)) != null) {
					flags().TargetNpc = npc.getId();
					if (npc.isTrade()) {
						// Doble clic sobre un comerciante, hace /COMERCIAR
						commerceStart();
					} else if (npc.isBankCashier()) {
						if (!checkAlive()) {
							return;
						}
						// Extensi�n de AOJ - 16/08/2004
						// Doble clic sobre el banquero hace /BOVEDA
						if (checkNpcNear(npc, DISTANCE_CASHIER)) {
							iniciarDeposito();
						}
					} else if (npc.isPriest() || npc.isPriestNewbies()) {
						// Extensi�n de AOJ - 01/02/2007
						// Doble clic sobre el sacerdote hace /RESUCITAR o /CURAR
						if (isAlive()) {
							heal();
						} else {
							resuscitate();
						}
					}
				} else {
					flags().TargetNpc = 0;
					flags().TargetNpcTipo = 0;
					flags().TargetUser = 0;
					flags().TargetObj = 0;
					sendMessage("No ves nada interesante.", FontType.FONTTYPE_INFO);
				}
			}
		}
	}

	/** Comando para hablar (;) */
	public void talk(String chat) {
		if (!checkAlive("��Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
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
			
	        if ( !flags().AdminInvisible ) {
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
		if (flags().isCounselor()) {
			Log.logGM(this.userName, "El consejero dijo: " + chat);
		}
	}

	/** Comando para gritar (-) */
	public void yell(String text) {
		if (!checkAlive("��Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
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
		if (flags().isCounselor()) {
			Log.logGM(this.userName, "El consejero grit�: " + text);
		}
	}

	/** Comando para susurrarle al oido a un usuario (\) */
	public void whisper(short targetIndex, String text) {
		if (!checkAlive("��Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
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
				sendMessage("Estas muy lejos de " + targetUser.getNick(), FontType.FONTTYPE_INFO);
			} else {
				if (flags().isCounselor()) {
					Log.logGM(this.userName, "El consejero le susurr� a " + targetUser.getNick() + ": " + text);
				}

				// send to target user
				targetUser.sendPacket(new ChatOverHeadResponse(text, getId(),
								Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));
				// send to source user
				sendPacket(new ChatOverHeadResponse(text, getId(),
						Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));

				if (!flags().isGM() || flags().isCounselor()) {
					// send to admins at area
					map.sendToAreaToAdminsButCounselor(pos().x, pos().y,
							new ChatOverHeadResponse("a " + targetUser.getNick() + "> " + text, this.getId(),
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

		if (this.counters.Saliendo) {
			sendMessage("/SALIR cancelado!.", FontType.FONTTYPE_INFO);
			this.counters.Saliendo = false;
		}

		if (flags().Paralizado) {
			if (flags().UltimoMensaje != 1) {
				sendMessage("No puedes moverte por estar paralizado.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 1;
			}
			return;
		}
		if (flags().Descansar) {
			flags().Descansar = false;
			sendPacket(new RestOKResponse());
			sendMessage("Has dejado de descansar.", FontType.FONTTYPE_INFO);
		}
		if (flags().Meditando) {
			flags().Meditando = false;
			sendPacket(new MeditateToggleResponse());
			sendMessage("Dejas de meditar.", FontType.FONTTYPE_INFO);
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
			sendCreateFX(0, 0);
		}
		if (flags().Oculto) {
			if (this.clazz != Clazz.Thief) {
				turnVisible();
			}
		}
		move(heading);
		flags().Trabajando = false;
	}

	private void turnVisible() {
		sendMessage("�Has vuelto a ser visible!", FontType.FONTTYPE_INFO);
		flags().Oculto = false;
		flags().Invisible = false;
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

		infoChar().heading(heading);
		MapPos newPos = pos().copy().moveToHeading(heading);
		if ( map.isLegalPos(newPos, isSailing(), !isSailing())) {
			casper = map.getUser(newPos.x, newPos.y);
			if (casper != null) {
				if (flags().AdminInvisible) {
					// los admins invisibles no pueden patear caspers
					// user can't move
					sendPositionUpdate();
					return;
				} 
				Heading casperHeading = heading.invertHeading();
            	casper.infoChar().heading(casperHeading);
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
					MapPos warpPos = map.closestLegalPosUser(oldPos.x, oldPos.y, flags().Navegando, flags().isGM());
					warpMe(warpPos.map, warpPos.x, warpPos.y, true);
				}
			}
		} else {
			// user can't move
			sendPositionUpdate();
		}
	}

	public void atack() {
		if (!checkAlive("��No puedes atacar a nadie por estar muerto!!")) {
			return;
		}
		if (flags().isCounselor()) {
			sendMessage("��No puedes atacar a nadie!!", FontType.FONTTYPE_INFO);
			return;
		} else {
			if (this.userInv.tieneArmaEquipada()) {
				if (this.userInv.getArma().esProyectil()) {
					sendMessage("No pod�s usar asi esta arma.", FontType.FONTTYPE_INFO);
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
		if (!checkAlive("��Estas muerto!! Los muertos no pueden recoger objetos.")) {
			return;
		}
		if (flags().isCounselor()) {
			sendMessage("No puedes recoger ning�n objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		Map mapa = this.server.getMap(pos().map);
		// �Hay algun obj?
		if (mapa.hasObject(pos().x, pos().y)) {
			// �Esta permitido agarrar este obj?
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
					if (flags().isGM()) {
						Log.logGM(this.userName, "Agarr�: " + obj.obj_ind + " objeto=" + obj.objInfo().Nombre);
					}
				}
			}
		} else {
			sendMessage("No hay nada aqui.", FontType.FONTTYPE_INFO);
		}
	}

	public void dropObject(byte slot, int cant) {
		if (!checkAlive("��Estas muerto!! Los muertos no pueden tirar objetos.")) {
			return;
		}
		if (isSailing()) {
			sendMessage("No puedes tirar objetos mientras navegas.", FontType.FONTTYPE_INFO);
			return;
		}
		if (flags().isCounselor()) {
			sendMessage("No puedes tirar ning�n objeto.", FontType.FONTTYPE_INFO);
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
		if (!checkAlive("��Estas muerto!! Solo puedes usar items cuando estas vivo.")) {
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
				(short) stats().MaxHP,
				(short) stats().MinHP,
				(short) stats().maxMana,
				(short) stats().mana,
				(short) stats().maxStamina,
				(short) stats().stamina,
				stats().getGold(),
				(byte)stats().ELV, // Current Level
				stats().ELU, // Experience to Level Up
				stats().Exp)); // Current Experience
	}
	
	public void sendUpdateHungerAndThirst() {
		sendPacket(new UpdateHungerAndThirstResponse(
				(byte) stats().maxDrinked,
				(byte) stats().drinked,
				(byte) stats().maxEaten,
				(byte) stats().eaten));
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
				log.fatal(this.getNick() + " no pudo salir del mapa actual");
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

		MapPos freePos = targetMap.closestLegalPosUser(x, y, flags().Navegando, flags().isGM());

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
			if (flags().UserLogged) { // No hacer sonido en el LOGIN.
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
		return flags().Paralizado;
	}

	public void revive() {
		if (this.counters.Saliendo) {
			return;
		}
		Map m = this.server.getMap(pos().map);
		if (m == null) {
			return;
		}
		flags().Muerto = false;
		stats().MinHP = stats().MaxHP;// 10;
		if (stats().drinked <= 0) {
			stats().drinked = 10;
		}
		if (stats().eaten <= 0) {
			stats().eaten = 10;
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
		
		MapPos freePos = newMap.closestLegalPosUser(x, y, flags().Navegando, flags().isGM());
		if (freePos == null) {
			log.warn("WARPUSER FALLO: no hay un lugar libre cerca de mapa=" + targetMap + " x=" + x + " y=" + y);
			return false;
		}
		
		newMap.enterMap(this, freePos.x, freePos.y);
		AreasAO.instance().loadUser(newMap, this);
		sendPacket(new UserCharIndexInServerResponse(getId()));
		
		// Seguis invisible al pasar de mapa
		if ((flags().Invisible || flags().Oculto) && !flags().AdminInvisible) {
			map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		}
		
		if (withFX && !flags().AdminInvisible) {
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
				// Es una mascota de invocaci�n. Se pierde al cambiar de mapa
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
			sendMessage("No se permiten mascotas en zonas seguras. Te esperar�n afuera.", FontType.FONTTYPE_INFO);
		}
	}

	public void sendToJail(int jailTimeMinutes, String adminName) {
		this.counters.Pena = jailTimeMinutes;
		if (warpMe(WP_PRISION.map, WP_PRISION.x, WP_PRISION.y, true)) {
			if (adminName == null) {
				sendMessage("Has sido encarcelado. Permanecer�s en la carcel " + jailTimeMinutes + " minutos.",
						FontType.FONTTYPE_INFO);
			} else {
				sendMessage(adminName + " te ha encarcelado. Permanecer�s en la carcel " + jailTimeMinutes + " minutos.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void releaseFromJail() {
		counters().Pena = 0;
		warpMe(WP_LIBERTAD.map, WP_LIBERTAD.x, WP_LIBERTAD.y, true);
		sendMessage("Has sido liberado!", FontType.FONTTYPE_INFO);
	}
	
	public void rainingEffect() {
		if (flags().UserLogged) {
			Map mapa = this.server.getMap(pos().map);
			if (this.server.isRaining() && mapa.isOutdoor(pos().x, pos().y) && mapa.getZone() != Zone.DUNGEON) {
				int modifi = Util.percentage(stats().maxStamina, 3);
				stats().quitarStamina(modifi);
				sendMessage("��Has perdido stamina, busca pronto refugio de la lluvia!!.", FontType.FONTTYPE_INFO);
				sendUpdateUserStats();
			}
		}
	}

	public void paralizedEffect() {
		if (this.counters.Paralisis > 0) {
			this.counters.Paralisis--;
		} else {
			flags().Paralizado = false;
			flags().Inmovilizado = false;
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
		flags().Oculto = true;
		flags().Invisible = true;
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		sendMessage("�Te has escondido entre las sombras!", FontType.FONTTYPE_INFO);
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
			if (flags().UltimoMensaje != 4) {
				sendMessage("�No has logrado esconderte!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 4;
			}
		}
		flags().Trabajando = true;
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
		if (!flags().isGM()) {
			return;
		}
		// Asegurarse haya un NPC seleccionado
		Npc targetNpc = this.server.npcById(flags().TargetNpc);
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
		npc.counters().TiempoExistencia = IntervaloInvocacion; // Duraci�n que tendr� la invocaci�n
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
				this.infoChar.body(),
				this.infoChar.head(),
				this.infoChar.heading().value(),

				pos().x, pos().y,
				this.infoChar.weapon(),
				this.infoChar.shield(),
				this.infoChar.helmet(),
				this.infoChar.fx(),
				this.infoChar.loops,
				userNameAndTagForCC(),
				(byte) (this.isCriminal() ? 1 : 0),
				(byte)flags().privileges);
	}

	private String userNameAndTagForCC() {
        if (this.showName) {
        	if (flags().isGM() && (flags().Invisible || flags().Oculto)) {
        		return getNick() + " " + TAG_USER_INVISIBLE;
        	} else {
    			if (getGuildInfo().esMiembroClan()) {
    				return getNick() + " <" + this.guildUser.getGuildName() + ">";
    			} else {
    				return getNick();
    			}
        	}
        } else {
            return "";
        }
	}

	public CharacterChangeResponse characterChange() {
		return new CharacterChangeResponse(
				getId(),
				this.infoChar.body(),
				this.infoChar.head(),
				this.infoChar.heading().value(),

				this.infoChar.weapon(),
				this.infoChar.shield(),
				this.infoChar.helmet(),
				this.infoChar.fx(),
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
		if (this.flags().Mimetizado) {
			this.mimetizadoChar.undress(this.race, this.gender);
		} else {
			this.infoChar.undress(this.race, this.gender);
		}
		flags().Desnudo = true;
	}

	public void removePet(Npc pet) {
		if ( !getUserPets().hasPets()) {
			return;
		}

		getUserPets().removePet(pet);
	}

	public void sendWave(int sound) {
	    if (flags().AdminInvisible) {
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
		if (flags().isGM()) {
			return;
		}
		Map map = this.server.getMap(pos().map);
		// Sonido
		sendWave(SOUND_USER_DIE);
		// Quitar el dialogo del usuario muerto
		map.sendToArea(pos().x, pos().y, new RemoveCharDialogResponse(getId()));
		
		stats().MinHP = 0;
		stats().stamina = 0;
		flags().AtacadoPorNpc = 0;
		flags().AtacadoPorUser = 0;
		flags().Envenenado = false;
		flags().Muerto = true;
		
        // SeguroResu: No se activa en arenas
		if (duelStatus(this) != DuelStatus.DUEL_ALLOWED) {
			flags().SeguroResu = true;
			sendPacket(new ResuscitationSafeOnResponse());
		} else {
			flags().SeguroResu = false;
			sendPacket(new ResuscitationSafeOffResponse());
		}
        
		if (flags().AtacadoPorNpc > 0) {
			Npc npc = this.server.npcById(flags().AtacadoPorNpc);
			if (npc != null) {
				npc.oldMovement();
			} else {
				flags().AtacadoPorNpc = 0;
			}
		}
		// <<<< Paralisis >>>>
		if (flags().Paralizado) {
			flags().Paralizado = false;
			sendPacket(new ParalizeOKResponse());
		}
		// <<<< Descansando >>>>
		if (flags().Descansar) {
			flags().Descansar = false;
			sendPacket(new RestOKResponse());
		}
		// <<<< Meditando >>>>
		if (flags().Meditando) {
			flags().Meditando = false;
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
		if ( !flags().Navegando ) {
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
		if (map.isArenaZone(pos().x, pos().y) || flags().isGM()) {
			return;
		}
		dropAllItems();
		dropGold(stats().getGold()); 
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
		if ((amount > 0) && (amount <= stats().getGold())) {
			while ((amount > 0) && (stats().getGold() > 0)) {
				InventoryObject oi = new InventoryObject(OBJ_ORO, amount);
				if ((amount > MAX_INVENTORY_OBJS) && (stats().getGold() > MAX_INVENTORY_OBJS)) {
					oi.cant = MAX_INVENTORY_OBJS;
					stats().addGold( -MAX_INVENTORY_OBJS);
					amount -= oi.cant;
				} else {
					oi.cant = amount;
					stats().addGold( -amount );
					amount -= oi.cant;
				}
				if (flags().isGM()) {
					Log.logGM(this.userName,
							"Tir� " + oi.cant + " unidades del objeto " + findObj(oi.objid).Nombre);
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
		if (!flags().isGM()) {
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
		if (!flags().Hambre && !flags().Sed) {
			
			// Alcanz� el m�ximo del skill para este nivel?
			if (skills().get(skill) >= Skill.levelSkill[stats().ELV]) {
				return;
			}
			
			// Alcanz� el m�ximo valor del skill? (100)
			if (skills().get(skill) >= Skill.MAX_SKILL_POINTS) {
				return;
			}
			
			int prob;
			if (stats().ELV <= 3) {
				prob = 25;
			} else if (stats().ELV < 6) {
				prob = 35;
			} else if (stats().ELV < 10) {
				prob = 40;
			} else if (stats().ELV < 20) {
				prob = 45;
			} else {
				prob = 50;
			}

			if (Util.random(1, prob) == 7) {
				skills().addSkillPoints(skill, (byte) 1);
				sendMessage("�Has mejorado tu skill " + skill + " en un punto!. Ahora tienes " 
							+ skills().get(skill) + " pts.", FontType.FONTTYPE_INFO);
				
				stats().addExp((byte) EXPERIENCE_BY_LEVEL_UP);
				sendMessage("�Has ganado " + EXPERIENCE_BY_LEVEL_UP +" puntos de experiencia!", FONTTYPE_FIGHT);
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
		return stats().ELV <= LimiteNewbie;
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
		flags().Envenenado = true;
		sendMessage("��La criatura te ha envenenado!!", FONTTYPE_FIGHT);
	}

	public void checkUserLevel() {
		while (stats().Exp >= stats().ELU) {
			boolean wasNewbie = isNewbie();

			// �Alcanzo el maximo nivel?
			if (stats().ELV == STAT_MAXELV) {
				stats().Exp = 0;
				stats().ELU = 0;
				return;
			}
			
			// Se acumul� la experiencia necesaria para subir de nivel
			sendWave(SOUND_NIVEL);
			sendMessage("�Has subido de nivel!", FontType.FONTTYPE_INFO);
			
			int skillPoints = (stats().ELV == 1) ? 10 : 5;
			skills().freeSkillPts += skillPoints;
			sendMessage("Has ganado " + skillPoints + " skillpoints.", FontType.FONTTYPE_INFO);
			
			stats().ELV++;
			stats().Exp -= stats().ELU;
			
            if (stats().ELV < 15) {
            	stats().ELU *= 1.4;
            } else if (stats().ELV < 21) {
            	stats().ELU *= 1.35;
            } else if (stats().ELV < 33) {
            	stats().ELU *= 1.3;
            } else if (stats().ELV < 41) {
            	stats().ELU *= 1.225;
            } else {
            	stats().ELU *= 1.175;
            }
			
			clazz().incStats(this);
			sendSkills();
			
			if (!isNewbie() && wasNewbie) {
				userInv().quitarObjsNewbie();
				quitDungeonNewbie();
			}
			
			sendUpdateUserStats();
		}
	}

	private void quitDungeonNewbie() {
		// Si el usuario dej� de ser Newbie, y estaba en el Dungeon Newbie
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
			tmp = (skills().get(Skill.SKILL_Tacticas) + stats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorEvasion();
		} else if (skills().get(Skill.SKILL_Tacticas) < 91) {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (2 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorEvasion();
		} else {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (3 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorEvasion();
		}
		return (tmp + (2.5 * Math.max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueArma() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Armas) < 31) {
			tmp = skills().get(Skill.SKILL_Armas) * this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Armas) < 61) {
			tmp = ((skills().get(Skill.SKILL_Armas) + stats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Armas) < 91) {
			tmp = ((skills().get(Skill.SKILL_Armas) + (2 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else {
			tmp = ((skills().get(Skill.SKILL_Armas) + (3 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas());
		}
		return tmp + (2.5 * Math.max(stats().ELV - 12, 0));
	}

	public double poderAtaqueProyectil() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Proyectiles) < 31) {
			tmp = (skills().get(Skill.SKILL_Proyectiles) * this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 61) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + stats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 91) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (2 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (3 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		}
		return (tmp + (2.5 * Math.max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueWresterling() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Wresterling) < 31) {
			tmp = (skills().get(Skill.SKILL_Wresterling) * this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Wresterling) < 61) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + stats().attr().get(Attribute.AGILITY))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Wresterling) < 91) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (2 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (3 * stats().attr().get(Attribute.AGILITY)))
					* this.clazz().modificadorPoderAtaqueArmas();
		}
		return tmp + (2.5 * Math.max(stats().ELV - 12, 0));
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
			// Peleando con pu�os
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
		// �El usuario esta usando un escudo ???
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

	public short calcularDa�o(Npc npc) {
		long da�oArma = 0;
		long da�oMaxArma = 0;
		long da�oUsuario = 0;
		double modifClase = 0;
		if (this.userInv.tieneArmaEquipada()) {
			ObjectInfo arma = this.userInv.getArma();
			// Ataca a un npc?
			if (npc != null) {
				// Usa la mata dragones?
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) { // Usa la
					// matadragones?
					modifClase = this.clazz().modicadorDa�oClaseArmas();
					if (npc.npcType() == NpcType.NPCTYPE_DRAGON) { 
						// Ataca a un drag�n?
						da�oArma = Util.random(arma.MinHIT, arma.MaxHIT);
						da�oMaxArma = arma.MaxHIT;
					} else { 
						// Si no ataca a un drag�n, el da�o es 1
						da�oArma = 1;
						da�oMaxArma = 1;
					}
				} else { 
					// da�o comun
					if (arma.esProyectil()) {
						modifClase = this.clazz().modicadorDa�oClaseProyectiles();
						da�oArma = Util.random(arma.MinHIT, arma.MaxHIT);
						da�oMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							da�oArma += Util.random(proyectil.MinHIT, proyectil.MaxHIT);
							da�oMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDa�oClaseArmas();
						da�oArma = Util.random(arma.MinHIT, arma.MaxHIT);
						da�oMaxArma = arma.MaxHIT;
					}
				}
			} else { 
				// Ataca usuario
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) {
					modifClase = this.clazz().modicadorDa�oClaseArmas();
					da�oArma = 1; // Si usa la espada matadragones da�o es 1
					da�oMaxArma = 1;
				} else {
					if (arma.esProyectil()) {
						modifClase = this.clazz().modicadorDa�oClaseProyectiles();
						da�oArma = Util.random(arma.MinHIT, arma.MaxHIT);
						da�oMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							da�oArma += Util.random(proyectil.MinHIT, proyectil.MaxHIT);
							da�oMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDa�oClaseArmas();
						da�oArma = Util.random(arma.MinHIT, arma.MaxHIT);
						da�oMaxArma = arma.MaxHIT;
					}
				}
			}
		}
		da�oUsuario = Util.random(stats().MinHIT, stats().MaxHIT);
		double da�o = (((3 * da�oArma) + ((da�oMaxArma / 5) * Math.max(0, (stats().attr.get(Attribute.STRENGTH) - 15)))
				+ da�oUsuario) * modifClase);
		return (short) da�o;
	}

	public void userDa�oNpc(Npc npc) {
		int da�o = calcularDa�o(npc);
		// esta navegando? si es asi le sumamos el da�o del barco
		if (flags().Navegando) {
			da�o += Util.random(this.userInv.getBarco().MinHIT, this.userInv.getBarco().MaxHIT);
		}
		da�o -= npc.stats().defensa;
		if (da�o < 0) {
			da�o = 0;
		}
		npc.stats().MinHP -= da�o;
		if (da�o > 0) {
			sendPacket(new UserHitNPCResponse(da�o));
			npc.calcularDarExp(this, da�o);
		} else {
			sendWave(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
		if (npc.stats().MinHP > 0) {
			// Trata de apu�alar por la espalda al enemigo
			if (puedeApu�alar()) {
				apu�alar(npc, da�o);
				riseSkill(Skill.SKILL_Apu�alar);
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

	public boolean puedeApu�alar() {
		if (this.userInv.tieneArmaEquipada()) {
			return ((skills().get(Skill.SKILL_Apu�alar) >= MIN_APU�ALAR) && this.userInv.getArma().apu�ala())
					|| ((this.clazz == Clazz.Assassin) && this.userInv.getArma().apu�ala());
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

	public void npcDa�o(Npc npc) {
		int da�o = Util.random(npc.stats().MinHIT, npc.stats().MaxHIT);
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
				da�o -= absorbido;
				if (da�o < 1) {
					da�o = 1;
				}
			}
			break;
		default:
			// Si tiene armadura absorbe el golpe
			if (this.userInv.tieneArmaduraEquipada()) {
				ObjectInfo armadura = this.userInv.getArmadura();
				int absorbido = Util.random(armadura.MinDef, armadura.MaxDef);
				absorbido += defbarco;
				da�o -= absorbido;
				if (da�o < 1) {
					da�o = 1;
				}
			}
			break;
		}
		sendPacket(new NPCHitUserResponse(lugar, (short)da�o));
		if (!flags().isGM()) {
			stats().MinHP -= da�o;
		}

		sendUpdateUserStats();

		// Muere el usuario
		if (stats().MinHP <= 0) {
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
			sendMessage("Est�s muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		if (this.faction.ArmadaReal && npc.getPetUserOwner() != null && !npc.getPetUserOwner().isCriminal()) {
			sendMessage("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.getPetUserOwner() != null && flags().Seguro && !npc.getPetUserOwner().isCriminal()) {
			sendMessage("Debes quitar el seguro para atacar a una mascota de un ciudadano.", FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.stats().alineacion == 0 && flags().Seguro) {
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
			userDa�oNpc(npc);
		} else {
			sendWave(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
	}

	public void safeToggle() {
		flags().Seguro = !flags().Seguro;
		if (flags().Seguro) {
			sendPacket(new SafeModeOnResponse());
		} else {
			sendPacket(new SafeModeOffResponse());
		}
	}
	
	public void resuscitationToggle() {
		// HandleResuscitationToggle
		flags().SeguroResu = !flags().SeguroResu;
		
		if (flags().SeguroResu) {
			sendPacket(new ResuscitationSafeOnResponse());
		} else {
			sendPacket(new ResuscitationSafeOffResponse());
		}
	}
	
	public boolean isInCombatMode() {
		return flags().ModoCombate;
	}

	public void toggleCombatMode() {
		// Entrar o salir modo combate
		if (isInCombatMode()) {
			sendMessage("Has salido del modo de combate.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Has pasado al modo de combate.", FontType.FONTTYPE_INFO);
		}
		flags().ModoCombate = !flags().ModoCombate;
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
		if (counters().intervaloPermiteAtacar()) {
			// Pierde stamina
			if (stats().stamina >= 10) {
				stats().quitarStamina(Util.random(1, 10));
			} else {
				if (gender() == UserGender.GENERO_MAN) {
					sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
				} else {
					sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
				}
				return;
			}
			MapPos attackPos = pos().copy();
			attackPos.moveToHeading(this.infoChar.heading());
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
						sendMessage("No pod�s atacar mascotas en zonas seguras", FONTTYPE_FIGHT);
						return;
					}
					usuarioAtacaNpc(attackedNpc);
				} else {
					sendMessage("No pod�s atacar a este Npc", FONTTYPE_FIGHT);
				}
				sendUpdateUserStats();
				return;
			}
			sendWave(SOUND_SWING);
			sendUpdateUserStats();
			flags().Trabajando = false;
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
			sendMessage("Est�s muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		usuarioAtacadoPorUsuario(victima);
		if (usuarioImpacto(victima)) {
			sendWave(SOUND_IMPACTO);
			if (!victima.isSailing()) {
				victima.sendCreateFX(FXSANGRE, 0);
			}
			userDa�oUser(victima);
		} else {
			sendWave(SOUND_SWING);
			this.sendPacket(new UserSwingResponse());
			victima.sendPacket(new UserAttackedSwingResponse(getId()));
		}
	}

	public void userDa�oUser(User victima) {
		short damage = calcularDa�o(null);
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
		victima.stats().removeHP(damage);
		if (!flags().Hambre && !flags().Sed) {
			if (this.userInv.tieneArmaEquipada()) {
				// Si usa un arma quizas suba "Combate con armas"
				riseSkill(Skill.SKILL_Armas);
			} else {
				// sino tal vez lucha libre
				riseSkill(Skill.SKILL_Wresterling);
			}
			riseSkill(Skill.SKILL_Tacticas);
			// Trata de apu�alar por la espalda al enemigo
			if (puedeApu�alar()) {
				apu�alar(victima, damage);
				riseSkill(Skill.SKILL_Apu�alar);
			}
		}
		if (victima.stats().MinHP <= 0) {
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
		int daExp = victima.stats().ELV * 2;
		stats().addExp(daExp);
		
		// Lo mata
		sendMessage("Has matado a " + victima.userName + "!", FONTTYPE_FIGHT);
		sendMessage("Has ganado " + daExp + " puntos de experiencia.", FONTTYPE_FIGHT);
		
		victima.sendMessage(this.userName + " te ha matado!", FONTTYPE_FIGHT);
		
		if (duelStatus(victima) != DuelStatus.DUEL_ALLOWED) {
			boolean eraCriminal = isCriminal();
			if (!victima.isCriminal()) {
				this.reputation().incAsesino(vlAsesino * 2);
				this.reputation().burguesRep = 0;
				this.reputation().nobleRep = 0;
				this.reputation().plebeRep = 0;
			} else {
				this.reputation().incNoble(vlNoble);
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
		stats().incUsuariosMatados();
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
		stats().incUsuariosMatados();
	}

	public void usuarioAtacadoPorUsuario(User victima) {
		if (duelStatus(victima) == DuelStatus.DUEL_ALLOWED) {
			return;
		}
		if (!getGuildInfo().esMiembroClan() || !victima.getGuildInfo().esMiembroClan()) {
			if (!isCriminal() && !victima.isCriminal()) {
				turnCriminal();
			}
		} else { // Ambos est�n en clan
			if (getGuild() != null && !getGuild().isEnemy(victima.getGuildInfo().getGuildName())) {
				// Est�n en clanes enemigos
				if (!isCriminal() && !victima.isCriminal()) {
					turnCriminal();
				}
			}
			// TODO Revisar: �puede un cuidadano atacar a otro ciudadano, cuando est�n en clanes enemigos?
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
			pet.attackedByUserName(objetivo.getNick());
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
			sendMessage("Los seguidores de las Fuerzas del Caos tienen prohibido atacarse entre s�.",
					FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!checkAlive("No puedes atacar porque estas muerto.")) {
			return false;
		}
		// Se asegura que la victima no es un GM
		if (victima.flags().isGM()) {
			sendMessage("��No puedes atacar a los administradores del juego!!", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isAlive()) {
			sendMessage("No puedes atacar a un esp�ritu.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (flags().Seguro) {
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
				sendMessage("Para atacar a ese usuario, �l se debe encontrar en tu misma zona.", FONTTYPE_FIGHT);
			} else if ( !map.isTournamentZone(pos().x, pos().y)	&& map.isTournamentZone(victima.pos().x, victima.pos().y) ) {
				sendMessage("Para atacar a ese usuario, debes encontrarte en la misma zona que �l.", FONTTYPE_FIGHT);
			}
			return false;
		}
		return true;
	}

	private boolean suerteApu�alar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Apu�alar) / 10)]);
		return (Util.random(1, rango) == 3);
	}

	public void apu�alar(Npc victimaNPC, int da�o) {
		// DoApu�alar
		if (suerteApu�alar()) {
			da�o *= 2;
			victimaNPC.stats().MinHP -= da�o;
			sendMessage("Has apu�alado a la criatura por " + da�o, FONTTYPE_FIGHT);
			riseSkill(Skill.SKILL_Apu�alar);
			victimaNPC.calcularDarExp(this, da�o);
		} else {
			sendMessage("�No has logrado apu�alar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void apu�alar(User victimaUsuario, int da�o) {
		// DoApu�alar
		if (suerteApu�alar()) {
			da�o *= 1.5;
			victimaUsuario.stats().MinHP -= da�o;
			sendMessage("Has apu�alado a " + victimaUsuario.userName + " por " + da�o, FONTTYPE_FIGHT);
			victimaUsuario.sendMessage("Te ha apu�alado " + this.userName + " por " + da�o, FONTTYPE_FIGHT);
		} else {
			sendMessage("�No has logrado apu�alar a tu enemigo!", FONTTYPE_FIGHT);
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
				npc.getPetUserOwner().sendMessage("��" + this.userName + " esta atacando a tu mascota!!", FONTTYPE_FIGHT);
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
			sendError("Nombre inv�lido.");
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
		this.flags().Muerto = false;
		this.flags().Escondido = false;
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
		this.infoChar.heading(Heading.SOUTH);
		this.infoChar.ramdonBodyAndHead(race(), gender());
		this.infoChar.weapon = NingunArma;
		this.infoChar.shield = NingunEscudo;
		this.infoChar.helmet = NingunCasco;
		this.origChar = new CharInfo(this.infoChar);

		this.stats().inicializarEstads(this.clazz);

		// Inicializar hechizos:
		if (this.clazz().isMagickal()) {
			this.spells.setSpell(1, HECHIZO_DARDO_MAGICO);
		}

		// ???????????????? INVENTARIO ��������������������
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

	static String getPjFile(String nick) {
		// FIXME
		return Constants.CHARFILES_FOLDER + java.io.File.separator + nick.toLowerCase() + ".chr";
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
		counters().IdleCount++;
		if (counters().IdleCount >= IdleLimit) {
		    sendError("Demasiado tiempo inactivo. Has sido desconectado.");
		    quitGame();
		}
	}
	
	public void checkPenalties() {
		if (counters().Pena > 0) {
		    counters().Pena--;
		    if (counters().Pena < 1) {
		        releaseFromJail();
		    }
		}
	}
	
	public void checkPiquete() {
		Map mapa = server.getMap(pos().map);
		if (mapa.isAntiPiquete(pos().x, pos().y)) {
			counters().piqueteSeconds++;
			sendMessage("Estas obstruyendo la via p�blica, mu�vete o ser�s encarcelado!!!", 
					FontType.FONTTYPE_INFO);
			if (counters().piqueteSeconds > 23) {
				counters().piqueteSeconds = 0;
				sendToJail(JAIL_TIME_PIQUETE_MINUTES, null);
			}
		} else {
			if (counters().piqueteSeconds > 0) {
				counters().piqueteSeconds = 0;
			}
		}
	}

	public void modifyAttributesByRace() {
		stats().attr().set(Attribute.STRENGTH, 		stats().attr().get(Attribute.STRENGTH) 		+ race().getStrengthModifier());
		stats().attr().set(Attribute.AGILITY, 		stats().attr().get(Attribute.AGILITY) 		+ race().getAgilityModifier());
		stats().attr().set(Attribute.INTELIGENCE, 	stats().attr().get(Attribute.INTELIGENCE) 	+ race().getInteligenceModifier());
		stats().attr().set(Attribute.CHARISMA, 		stats().attr().get(Attribute.CHARISMA) 		+ race().getCharismaModifier());
		stats().attr().set(Attribute.CONSTITUTION, 	stats().attr().get(Attribute.CONSTITUTION) 	+ race().getConstitutionModifier());
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
			// �Existe el personaje?
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
			flags().Escondido = false;
			flags().TargetNpc = 0;
			flags().TargetObj = 0;
			flags().TargetUser = 0;
			this.infoChar.fx = 0;

			// �El password es v�lido?
			this.passwordHash = this.userStorage.passwordHashFromStorage(this.userName);
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
				flags().setGod();
				chatColor = Color.rgb(250, 250, 150);
				Log.logGM(this.userName, "El GM-DIOS se conect� desde la ip=" + this.ip);
			} else if (this.server.manager().isDemiGod(this.userName)) {
				flags().setDemiGod();
				chatColor = Color.rgb(0, 255, 0);
				Log.logGM(this.userName, "El GM-SEMIDIOS se conect� desde la ip=" + this.ip);
			} else if (this.server.manager().isCounsellor(this.userName)) {
				flags().setCounselor();
				chatColor = Color.rgb(0, 255, 0);
				Log.logGM(this.userName, "El GM-CONSEJERO se conect� desde la ip=" + this.ip);
				// FIXME
//			} else if (this.server.manager().isRoyalCouncil(this.userName)) {
//				flags().setRoyalCouncil();
//				chatColor = Color.rgb(0, 255, 255);
//			} else if (this.server.manager().isChaosCouncil(this.userName)) {
//				flags().setChaosCouncil();
//				chatColor = Color.rgb(255, 128, 64);
			} else {
				// Usuario no privilegiado.
				flags().setOrdinaryUser();
				flags().AdminPerseguible = true;
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

			if (flags().Navegando) {
				this.infoChar.body = !isAlive() ? OBJ_INDEX_FRAGATA_FANTASMAL : this.userInv.getBarco().Ropaje;
				this.infoChar.head = 0;
				this.infoChar.weapon = NingunArma;
				this.infoChar.shield = NingunEscudo;
				this.infoChar.helmet = NingunCasco;
			}

			if (isAlive()) {
				flags().SeguroResu = false;
				sendPacket(new ResuscitationSafeOffResponse());
			} else {
				flags().SeguroResu = true;
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
				flags().Seguro = true;
				sendPacket(new SafeModeOnResponse());
			} else {
				sendPacket(new SafeModeOffResponse());
				flags().Seguro = false;
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
			flags().UserLogged = true;

			sendCreateFX(FXWARP, 0);

		} catch (Exception e) {
			log.fatal("ERROR EN connectUser(), nick=" + this.userName, e);
			sendError("Hubo un error conectando.");

		} finally {
			if (!flags().UserLogged) {
				// if anything went wrong...
				quitGame();
				this.userName = "";
			}
		}
	}

	public void efectoCegueEstu() {
		if (counters().Ceguera > 0) {
			counters().Ceguera--;
		} else {
			if (isBlind()) {
				makeNoBlind();
			} else {
				makeNoDumb();
			}
		}
	}

	private void makeNoBlind() {
		flags().Ceguera = false;
		sendPacket(new BlindNoMoreResponse());
	}

	public void makeNoDumb() {
		flags().Estupidez = false;
		sendPacket(new DumbNoMoreResponse());
	}
	
	public boolean isBlind() {
		return flags().Ceguera;
	}
	
	public boolean isDumb() {
		return flags().Estupidez;
	}
	
	public void makeDumb() {
		flags().Estupidez = true;
		counters().Ceguera = IntervaloInvisible;
		sendPacket(new DumbResponse());
	}

	public void efectoFrio() {
		if (this.counters.Frio < IntervaloFrio) {
			this.counters.Frio++;
		} else {
			Map mapa = this.server.getMap(pos().map);
			if (mapa.getTerrain() == Terrain.SNOW) {
				sendMessage("��Estas muriendo de frio, abr�gate o morir�s!!.", FontType.FONTTYPE_INFO);
				int modifi = Util.percentage(stats().MaxHP, 5);
				stats().MinHP -= modifi;

				if (stats().MinHP < 1) {
					sendMessage("��Has muerto de frio!!.", FontType.FONTTYPE_INFO);
					stats().MinHP = 0;
					userDie();
				}
			} else {
				if (!stats().isTooTired()) { // Verificaci�n agregada por gorlok
					int modifi = Util.percentage(stats().maxStamina, 5);
					stats().quitarStamina(modifi);
					sendMessage("��Has perdido stamina, si no te abrigas r�pido la perder�s toda!!.", FontType.FONTTYPE_INFO);
				}
			}
			this.counters.Frio = 0;
			sendUpdateUserStats();
		}
	}

	public boolean sanar(int intervalo) {
		Map map = this.server.getMap(pos().map);
		
		if (!map.isOutdoor(pos().x, pos().y)) {
			return false;
		}
		
		// Con el paso del tiempo se va sanando... pero muy lentamente ;-)
		if (stats().MinHP < stats().MaxHP) {
			if (this.counters.HPCounter < intervalo) {
				this.counters.HPCounter++;
			} else {
				int mashit = Util.random(2, Util.percentage(stats().maxStamina, 5));
				this.counters.HPCounter = 0;
				stats().MinHP += mashit;

				if (stats().MinHP > stats().MaxHP) {
					stats().MinHP = stats().MaxHP;
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
		if (tActual - this.counters.tInicioMeditar < TIEMPO_INICIO_MEDITAR) {
			return;
		}
		if (stats().stamina < stats().maxStamina) {
			return;
		}
		this.counters.IdleCount = 0;
		if (stats().mana >= stats().maxMana) {
			sendMessage("Has terminado de meditar.", FontType.FONTTYPE_INFO);
			sendPacket(new MeditateToggleResponse());
			flags().Meditando = false;
			this.infoChar.fx = 0;
			this.infoChar.loops = 0;
			sendCreateFX(0, 0);
			return;
		}
		if (suerteMeditar()) {
			int cant = Util.percentage(stats().maxMana, 3);
			stats().aumentarMana(cant);
			sendMessage("�Has recuperado " + cant + " puntos de mana!", FontType.FONTTYPE_INFO);
			sendUpdateUserStats();
			riseSkill(Skill.SKILL_Meditar);
		}
	}

	private boolean efectoVeneno() {
		if (this.counters.Veneno < IntervaloVeneno) {
			this.counters.Veneno++;
		} else {
			sendMessage("Estas envenenado, si no te curas moriras.", FontType.FONTTYPE_VENENO);
			this.counters.Veneno = 0;
			stats().MinHP -= Util.random(1, 5);
			sendUpdateUserStats();
			if (stats().MinHP < 1) {
				userDie();
			}
			return true;
		}
		return false;
	}

	private void efectoInvisibilidad() {
		if (this.counters.Invisibilidad < IntervaloInvisible) {
			this.counters.Invisibilidad++;
		} else {
			turnVisible();
		}
	}

	public void duracionPociones() {
		// Controla la duracion de las pociones
		if (flags().DuracionEfecto > 0) {
			flags().DuracionEfecto--;
		} else if (flags().DuracionEfecto == 0) {
			flags().TomoPocion = false;
			flags().TipoPocion = 0;
			// Volver los atributos al estado normal
			stats().attr().restoreAttributes();
		}
	}

	public void sendUserAttributes() {
		sendPacket(new AttributesResponse(
			stats().attr().get(Attribute.STRENGTH),
			stats().attr().get(Attribute.AGILITY),
			stats().attr().get(Attribute.INTELIGENCE),
			stats().attr().get(Attribute.CHARISMA),
			stats().attr().get(Attribute.CONSTITUTION)));
	}

	public boolean updateHungerAndThirst() {
		// Sed
		boolean wasUpdated = false;
		if (stats().drinked > 0) {
			if (this.counters.drinkCounter < IntervaloSed) {
				this.counters.drinkCounter++;
			} else {
				this.counters.drinkCounter = 0;
				stats().quitarSed(10);
				if (stats().drinked <= 0) {
					stats().drinked = 0;
					flags().Sed = true;
				}
				wasUpdated = true;
			}
		}
		// hambre
		if (stats().eaten > 0) {
			if (this.counters.foodCounter < IntervaloHambre) {
				this.counters.foodCounter++;
			} else {
				this.counters.foodCounter = 0;
				stats().quitarHambre(10);
				if (stats().eaten <= 0) {
					stats().eaten = 0;
					flags().Hambre = true;
				}
				wasUpdated = true;
			}
		}
		return wasUpdated;
	}

	public boolean recStamina(int intervalo) {
		if (stats().stamina < stats().maxStamina) {
			if (counters().STACounter < intervalo) {
				counters().STACounter++;
			} else {
				counters().STACounter = 0;
				if (flags().Desnudo) {
					// Desnudo no sube energ�a.
					return false;
				}
				int massta = Util.random(1, Util.percentage(stats().maxStamina, 5));
				stats().aumentarStamina(massta);
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
		if (flags().UserLogged) {
			boolean statsChanged = false;
			boolean hungerAndThristChanged = false;

			this.NumeroPaquetesPorMiliSec = 0;
			if (flags().Paralizado || flags().Inmovilizado) {
				paralizedEffect();
			}
			if (isBlind() || isDumb()) {
				efectoCegueEstu();
			}
			if (isAlive()) {
				if (flags().Desnudo && !flags().isGM()) {
					efectoFrio();
				}
				if (flags().Meditando) {
					meditar();
				}
				if (flags().Envenenado && !flags().isGM()) {
					statsChanged = efectoVeneno();
				}
				if (!flags().AdminInvisible && flags().Invisible) {
					efectoInvisibilidad();
				}
				duracionPociones();
				hungerAndThristChanged = updateHungerAndThirst();
				Map mapa = this.server.getMap(pos().map);
				if (!(this.server.isRaining() && mapa.isOutdoor(pos().x, pos().y))) {
					if (!flags().Descansar && !flags().Hambre && !flags().Sed) {
						// No esta descansando
						if (sanar(SanaIntervaloSinDescansar))
							statsChanged = true;
						if (recStamina(StaminaIntervaloSinDescansar))
							statsChanged = true;

					} else if (flags().Descansar) {
						// esta descansando

						if (sanar(SanaIntervaloDescansar))
							statsChanged = true;
						if (recStamina(StaminaIntervaloDescansar))
							statsChanged = true;

						// termina de descansar automaticamente
						if (stats().MaxHP == stats().MinHP && stats().maxStamina == stats().stamina) {
							sendPacket(new RestOKResponse());
							sendMessage("Has terminado de descansar.", FontType.FONTTYPE_INFO);
							flags().Descansar = false;
						}
					}
				}
				// Verificar muerte por hambre
				if (stats().eaten <= 0 && !flags().isGM()) {
					sendMessage("��Has muerto de hambre!!.", FontType.FONTTYPE_INFO);
					stats().eaten = 0;
					userDie();
				}
				// Verificar muerte de sed
				if (stats().drinked <= 0 && !flags().isGM()) {
					sendMessage("��Has muerto de sed!!.", FontType.FONTTYPE_INFO);
					stats().drinked = 0;
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
			this.counters.IdleCount++;
			if (this.counters.IdleCount > IntervaloParaConexion) {
				this.counters.IdleCount = 0;
				quitGame();
			}
		}
	}

	private String getTituloFaccion() {
		if (this.faction.ArmadaReal) {
			return " <Ejercito real> <" + this.faction.royalArmyTitle() + ">";
		} else if (this.faction.FuerzasCaos) {
			return " <Legi�n Oscura> <" + this.faction.darkLegionTitle() + ">";
		}
		return "";
	}

	public String userNameTagDesc() {
		var msg = new StringBuilder();
		if (showName) {
			msg.append(getNick());
			
			if (this.description.length() > 0) {
				msg.append(" - " + this.description);
			}
			
			if (!flags().isGM() && isNewbie()) {
				msg.append(" <NEWBIE>");
			}
			
			msg.append(getTituloFaccion());
			
			if (getGuildInfo().esMiembroClan()) {
				msg.append(" <" + this.guildUser.getGuildName() + ">");
			}
			
			if (flags().isRoyalCouncil()) {
				msg.append(" [CONSEJO DE BANDERBILL]");
			} else if (flags().isChaosCouncil()) {
				msg.append(" [CONCILIO DE LAS SOMBRAS]");
			}
			
			if (flags().isGod() || flags().isDemiGod() || flags().isCounselor()) {
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
		if (flags().isRoyalCouncil()) {
			return FontType.FONTTYPE_CONSEJOVesA;
		} else if (flags().isChaosCouncil()) {
			return FontType.FONTTYPE_CONSEJOCAOSVesA;
		}
		
		
		if (flags().isGod()) {
			return FontType.FONTTYPE_DIOS;
		}
		if (flags().isDemiGod()) {
			return FontType.FONTTYPE_GM;
		}
		if (flags().isCounselor()) {
			return FontType.FONTTYPE_CONSEJO;
		}
		if (flags().isRoleMaster()) {
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

		// La ropa para Drows, s�lo la pueden usar los Drows
		if (infoObj.esParaRazaDrow() && this.race != UserRace.RAZA_DROW) {
			canUse = false;
		}

		return canUse;
	}

	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ESTO PROVIENE DE TRABAJO.BAS

	private boolean tieneObjetos(short objid, int cant) {
		int total = 0;
		for (int i = 1; i <= this.userInv.getSize(); i++) {
			if (this.userInv.getObject(i).objid == objid) {
				total += this.userInv.getObject(i).cant;
			}
		}
		return (cant <= total);
	}

	private void herreroQuitarMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.LingH > 0) {
			quitarObjetos(LingoteHierro, info.LingH);
		}
		if (info.LingP > 0) {
			quitarObjetos(LingotePlata, info.LingP);
		}
		if (info.LingO > 0) {
			quitarObjetos(LingoteOro, info.LingO);
		}
	}

	public void carpinteroQuitarMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.Madera > 0) {
			quitarObjetos(Le�a, info.Madera);
		}
	}

	private boolean carpinteroTieneMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.Madera > 0) {
			if (!tieneObjetos(Le�a, info.Madera)) {
				sendMessage("No tenes suficientes madera.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		return true;
	}

	private boolean herreroTieneMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.LingH > 0) {
			if (!tieneObjetos(LingoteHierro, info.LingH)) {
				sendMessage("No tienes suficientes lingotes de hierro.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		if (info.LingP > 0) {
			if (!tieneObjetos(LingotePlata, info.LingP)) {
				sendMessage("No tienes suficientes lingotes de plata.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		if (info.LingO > 0) {
			if (!tieneObjetos(LingoteOro, info.LingO)) {
				sendMessage("No tienes suficientes lingotes de oro.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		return true;
	}

	private boolean puedeConstruir(short objid) {
		ObjectInfo info = findObj(objid);
		return herreroTieneMateriales(objid) && skills().get(Skill.SKILL_Herreria) >= info.SkHerreria;
	}

	private boolean puedeConstruirHerreria(short objid) {
		for (int i = 0; i < this.server.getArmasHerrero().length; i++) {
			if (this.server.getArmasHerrero()[i] == objid) {
				return true;
			}
		}
		for (int i = 0; i < this.server.getArmadurasHerrero().length; i++) {
			if (this.server.getArmadurasHerrero()[i] == objid) {
				return true;
			}
		}
		return false;
	}

	private void herreroConstruirItem(short objid) {
		if (puedeConstruir(objid) && puedeConstruirHerreria(objid)) {
			Map mapa = this.server.getMap(pos().map);
			if (mapa == null) {
				return;
			}
			ObjectInfo info = findObj(objid);
			herreroQuitarMateriales(objid);
			if (info.objType == ObjType.Weapon) {
				sendMessage("Has construido el arma!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.ESCUDO) {
				sendMessage("Has construido el escudo!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.CASCO) {
				sendMessage("Has construido el casco!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.Armadura) {
				sendMessage("Has construido la armadura!.", FontType.FONTTYPE_INFO);
			}
			if (this.userInv.agregarItem(objid, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			riseSkill(Skill.SKILL_Herreria);
			sendInventoryToUser();
			sendWave(SOUND_MARTILLO_HERRERO);
			flags().Trabajando = true;
		}
	}

	private boolean puedeConstruirCarpintero(short objid) {
		for (int i = 0; i < this.server.getObjCarpintero().length; i++) {
			if (this.server.getObjCarpintero()[i] == objid) {
				return true;
			}
		}
		return false;
	}

	private void carpinteroConstruirItem(short objid) {
		ObjectInfo info = findObj(objid);
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (carpinteroTieneMateriales(objid) && skills().get(Skill.SKILL_Carpinteria) >= info.SkCarpinteria
				&& puedeConstruirCarpintero(objid) && this.userInv.getArma().ObjIndex == SERRUCHO_CARPINTERO) {
			carpinteroQuitarMateriales(objid);
			sendMessage("�Has construido el objeto!", FontType.FONTTYPE_INFO);
			if (this.userInv.agregarItem(objid, 1) < 1) {
				mapa.dropItemOnFloor(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			riseSkill(Skill.SKILL_Carpinteria);
			sendInventoryToUser();
			sendWave(SOUND_LABURO_CARPINTERO);
			flags().Trabajando = true;
		}
	}

	// ################################# FIN TRABAJO ###################################

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

		System.out.println(this.getNick() + "ahora es ciuda");
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
        		ObjectInfo barco = userInv().getBarco();
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
		if (flags().UserLogged && !this.counters.Saliendo) {
			this.counters.Saliendo = true;
			Map mapa = this.server.getMap(pos().map);
			if (mapa != null && mapa.isSafeMap()) {
				this.counters.SalirCounter = 1; // 1 segundo.
			} else {
				this.counters.SalirCounter = IntervaloCerrarConexion; // 10 segundos
				sendMessage("Cerrando... Se cerrar� el juego en " + IntervaloCerrarConexion + " segundos...",
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

	public void moveBank(byte slot, byte dir) {
		if (dir != 1 && dir != -1) {
			return;
		}
		if (slot < 1 || slot > bankInv.getSize()) {
			return;
		}

		this.bankInv.move(slot, dir);
		updateBankUserInv();
		sendBankOk();
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
					victima.flags().Envenenado = true;
					victima.sendMessage(this.userName + " te ha envenenado!!", FONTTYPE_FIGHT);
					sendMessage("Has envenenado a " + victima.getNick() + "!!", FONTTYPE_FIGHT);
				}
			}
		}
	}

	public static void changeGuildLeaderChr(String nick, boolean esLider) {
		try {
			// �Existe el personaje?
			if (!Util.fileExists(getPjFile(nick))) {
				return;
			}
			IniFile ini = new IniFile(getPjFile(nick));
			ini.setValue("GUILD", "EsGuildLeader", esLider);
			ini.store(getPjFile(nick));
		} catch (Exception e) {
			log.fatal(nick + ": ERROR EN SAVEUSER()", e);
		}
	}

	public static void changeGuildPtsChr(String nick, int addedGuildPts) {
		try {
			// �Existe el personaje?
			if (!Util.fileExists(getPjFile(nick))) {
				return;
			}
			IniFile ini = new IniFile(getPjFile(nick));
			long guildPoints = ini.getLong("Guild", "GuildPts");
			ini.setValue("GUILD", "GuildPts", guildPoints + addedGuildPts);
			ini.store(getPjFile(nick));
		} catch (Exception e) {
			log.fatal(nick + ": ERROR EN SAVEUSER()", e);
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
		// �Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// Se asegura que el target es un npc
		if (flags().TargetNpc == 0) {
			sendMessage("Debes seleccionar un personaje.", FontType.FONTTYPE_INFO);
			return;
		}
		Npc npc = this.server.npcById(flags().TargetNpc);
		if (npc.pos().distance(pos()) > 3) {
			sendMessage("Estas demasiado lejos del vendedor.", FontType.FONTTYPE_INFO);
			return;
		}

		if (npc.isBankCashier() && (npc instanceof NpcCashier)) {
			((NpcCashier)npc).balance(this);
		} else if (npc.isGambler() && (npc instanceof NpcGambler)) {
			((NpcGambler)npc).balance(this);
		} else {
			sendTalk(Color.COLOR_BLANCO, "No entiendo de eso. Habla con alguien m�s.", getId());
		}
	}
	
	public void navigateToggleGM() {
		if (!isGM() || isCounselor()) {
			return;
		}
		
		flags().Navegando = !flags().Navegando;
		sendPacket(new NavigateToggleResponse());
	}

	public void ignoreToggleGM() {
        if (!isGM()) {
        	return;
        }
        
    	flags().AdminPerseguible = !flags().AdminPerseguible;
    	if (flags().AdminPerseguible) {
    		sendMessage("Los NPCs hostiles te perseguir�n.", FontType.FONTTYPE_INFO);
    	} else {
    		sendMessage("Los NPCs hostiles te ignorar�n.", FontType.FONTTYPE_INFO);
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
		return flags().Silenciado;
	}

	public void turnSilence() {
		flags().Silenciado = true;
	}

	public void undoSilence() {
		flags().Silenciado = false;
	}

	public void denounce(String text) {
		// Command /DENUNCIAR
		if (isSilenced()) {
			sendMessage("Est�s SILENCIADO y no puedes hacer Denuncias. " +
					"Puedes solicitar asistencia con /GM y ser�s atendido cuando alguien est� disponible.", 
					FontType.FONTTYPE_INFO);
		} else {
			server.sendToAdmins(new ConsoleMsgResponse(getNick() + " DENUNCIA: " + text, 
					FontType.FONTTYPE_GUILDMSG.id()));
			sendMessage("Denuncia enviada, espere..", FontType.FONTTYPE_INFO);
		}
	}

	public void ping() {
		sendPacket(new PongResponse());
	}

	public void useSpellMacro(User user) {
		server.sendToAdmins(new ConsoleMsgResponse(user.getNick() + 
				" fue expulsado por Anti-macro de hechizos", FontType.FONTTYPE_VENENO.id()));
        user.sendError("Has sido expulsado por usar macro de hechizos. Recomendamos leer el reglamento sobre el tema macros");
	}
	
}