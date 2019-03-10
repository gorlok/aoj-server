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
package org.ArgentumOnline.server.user;

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;
import static org.ArgentumOnline.server.util.Color.COLOR_CYAN;
import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.ArgentumOnline.server.AbstractCharacter;
import org.ArgentumOnline.server.CharInfo;
import org.ArgentumOnline.server.Clazz;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.anticheat.SpeedHackCheck;
import org.ArgentumOnline.server.anticheat.SpeedHackException;
import org.ArgentumOnline.server.guilds.Guild;
import org.ArgentumOnline.server.guilds.GuildUser;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.inventory.UserInventory;
import org.ArgentumOnline.server.map.Heading;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapCell.Trigger;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.Terrain;
import org.ArgentumOnline.server.map.Zone;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcCashier;
import org.ArgentumOnline.server.npc.NpcGambler;
import org.ArgentumOnline.server.npc.NpcMerchant;
import org.ArgentumOnline.server.npc.NpcTrainer;
import org.ArgentumOnline.server.npc.NpcType;
import org.ArgentumOnline.server.protocol.AttributesResponse;
import org.ArgentumOnline.server.protocol.BankEndResponse;
import org.ArgentumOnline.server.protocol.BankInitResponse;
import org.ArgentumOnline.server.protocol.BankOKResponse;
import org.ArgentumOnline.server.protocol.BlindNoMoreResponse;
import org.ArgentumOnline.server.protocol.BlockPositionResponse;
import org.ArgentumOnline.server.protocol.BlockedWithShieldOtherResponse;
import org.ArgentumOnline.server.protocol.BlockedWithShieldUserResponse;
import org.ArgentumOnline.server.protocol.ChangeBankSlotResponse;
import org.ArgentumOnline.server.protocol.ChangeInventorySlotResponse;
import org.ArgentumOnline.server.protocol.ChangeMapResponse;
import org.ArgentumOnline.server.protocol.ChangeUserTradeSlotResponse;
import org.ArgentumOnline.server.protocol.CharacterChangeResponse;
import org.ArgentumOnline.server.protocol.CharacterCreateResponse;
import org.ArgentumOnline.server.protocol.ChatOverHeadResponse;
import org.ArgentumOnline.server.protocol.CommerceEndResponse;
import org.ArgentumOnline.server.protocol.CommerceInitResponse;
import org.ArgentumOnline.server.protocol.ConsoleMsgResponse;
import org.ArgentumOnline.server.protocol.DiceRollResponse;
import org.ArgentumOnline.server.protocol.DisconnectResponse;
import org.ArgentumOnline.server.protocol.DumbNoMoreResponse;
import org.ArgentumOnline.server.protocol.DumbResponse;
import org.ArgentumOnline.server.protocol.ErrorMsgResponse;
import org.ArgentumOnline.server.protocol.LevelUpResponse;
import org.ArgentumOnline.server.protocol.LoggedMessageResponse;
import org.ArgentumOnline.server.protocol.MeditateToggleResponse;
import org.ArgentumOnline.server.protocol.MiniStatsResponse;
import org.ArgentumOnline.server.protocol.NPCHitUserResponse;
import org.ArgentumOnline.server.protocol.NPCKillUserResponse;
import org.ArgentumOnline.server.protocol.NavigateToggleResponse;
import org.ArgentumOnline.server.protocol.ObjectCreateResponse;
import org.ArgentumOnline.server.protocol.ParalizeOKResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.protocol.PosUpdateResponse;
import org.ArgentumOnline.server.protocol.RainToggleResponse;
import org.ArgentumOnline.server.protocol.RemoveCharDialogResponse;
import org.ArgentumOnline.server.protocol.RestOKResponse;
import org.ArgentumOnline.server.protocol.SafeModeOffResponse;
import org.ArgentumOnline.server.protocol.SafeModeOnResponse;
import org.ArgentumOnline.server.protocol.SendSkillsResponse;
import org.ArgentumOnline.server.protocol.SetInvisibleResponse;
import org.ArgentumOnline.server.protocol.ShowBlacksmithFormResponse;
import org.ArgentumOnline.server.protocol.UpdateHPResponse;
import org.ArgentumOnline.server.protocol.UpdateHungerAndThirstResponse;
import org.ArgentumOnline.server.protocol.UpdateTagAndStatusResponse;
import org.ArgentumOnline.server.protocol.UpdateUserStatsResponse;
import org.ArgentumOnline.server.protocol.UserAttackedSwingResponse;
import org.ArgentumOnline.server.protocol.UserCharIndexInServerResponse;
import org.ArgentumOnline.server.protocol.UserHitNPCResponse;
import org.ArgentumOnline.server.protocol.UserHittedByUserResponse;
import org.ArgentumOnline.server.protocol.UserHittedUserResponse;
import org.ArgentumOnline.server.protocol.UserIndexInServerResponse;
import org.ArgentumOnline.server.protocol.UserSwingResponse;
import org.ArgentumOnline.server.protocol.WorkRequestTargetResponse;
import org.ArgentumOnline.server.quest.UserQuest;
import org.ArgentumOnline.server.user.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.Color;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.netty.channel.Channel;

/**
 * @author gorlok
 */
public class Player extends AbstractCharacter {

	private static Logger log = LogManager.getLogger();
	
	private static final int EXPERIENCE_BY_LEVEL_UP = 50;
	
	private Channel channel = null;

	String userName = "";
	String password = ""; // FIXME SEGURIDAD

	String description = ""; // Descripcion

	Clazz clazz = Clazz.Hunter;

	UserRace race = UserRace.RAZA_HUMANO;
	UserGender gender = UserGender.GENERO_HOMBRE;

	String email = "";
	short homeland = CIUDAD_ULLA;

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
	String bannedBy = "";
	String bannedReason = "";
	
	boolean showName = true; // TODO

	UserFlags flags = new UserFlags();

	UserStats stats = new UserStats();

	Reputation reputation = new Reputation();

	UserSkills skills = new UserSkills();

	UserFaction faction;

	UserCounters counters = new UserCounters();

	public UserTrade userTrade;

	UserArea userArea = new UserArea();

	UserStorage userStorage;

	private UserQuest quest;

	UserInventory userInv;
	Inventory bankInv;
	
	// TODO
    public short partyIndex = 0;  // index a la party q es miembro
    public short partySolicitud = 0; // index a la party q solicito

	SpeedHackCheck speedHackMover = new SpeedHackCheck("SpeedHack de mover");

	GameServer server;

	public Player(Channel channel, GameServer aoserver) {
		init(aoserver);

		this.userTrade = new UserTrade(this);

		this.channel = channel;
		java.net.InetSocketAddress addr = (java.net.InetSocketAddress) channel.remoteAddress();
		if (addr != null) {
			this.ip = addr.getAddress().getHostAddress();
			log.info(this.userName + " conectado desde " + this.ip);
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
		this.userStorage = new UserStorage(this.server, this);
		this.server = aoserver;

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

	public UserArea getUserArea() {
		return this.userArea;
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

	public UserGender gender() {
		return this.gender;
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

	public boolean isSailing() {
		return flags().Navegando;
	}

	public boolean isGod() {
		return flags().Privilegios == 3;
	}

	public boolean isDemiGod() {
		return flags().Privilegios == 2;
	}

	public boolean isCounselor() {
		return flags().Privilegios == 1;
	}

	public boolean isGM() {
		return flags().Privilegios > 0;
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
		return "Player(id=" + getId() + ",nick=" + this.userName + ")";
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
	 * Sends a network packet to the player.
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

	public void doComandoNave() {
		// Comando /NAVE
		// Comando para depurar la navegacion
		sendMessage("Comando deshabilitado o sin efecto en AOJ.", FontType.FONTTYPE_INFO);
	}

	public void doApostar(int gold) {
		// Comando /APOSTAR
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_PET);
		if (npc == null) {
			return;
		}
		if (!npc.isGambler() || !(npc instanceof NpcGambler)) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en apostar.", npc.getId());
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

	public void doEnlistar() {
		// Comando /ENLISTAR
		if (!checkAlive("¡¡Estas muerto!! Busca un sacerdote y no me hagas perder el tiempo.")) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc == null) {
			return;
		}
		if (!npc.isNoble()) {
			hablar(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (pos().distance(npc.pos()) > 4) {
			hablar(COLOR_BLANCO, "Jeje, acércate o no podré escucharte. ¡Estás demasiado lejos!", npc.getId());
			return;
		}
		if (!npc.isFaction()) {
			userFaction().enlistarArmadaReal(npc);
		} else {
			userFaction().enlistarCaos(npc);
		}
	}

	public void doRecompensa() {
		// Comando /RECOMPENSA
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc == null) {
			return;
		}
		if (!npc.isNoble()) {
			hablar(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (!checkAlive("¡¡Estás muerto!! Busca un sacerdote y no me hagas perder el tiempo!")) {
			return;
		}
		if (!npc.isFaction()) {
			if (!userFaction().ArmadaReal) {
				hablar(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			userFaction().recompensaArmadaReal(npc);
		} else {
			if (!userFaction().FuerzasCaos) {
				hablar(COLOR_BLANCO, "No perteneces a las fuerzas del caos!!!", npc.getId());
				return;
			}
			userFaction().recompensaCaos(npc);
		}
	}

	public void sendMiniStats() { // hazte fama y échate a dormir...
		sendPacket(new MiniStatsResponse(
				(int) this.faction.CiudadanosMatados,
				(int) this.faction.CriminalesMatados,
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

	public void doSeguir() {
		// Comando /SEGUIR
		if (flags().TargetNpc > 0) {
			Npc npc = this.server.npcById(flags().TargetNpc);
			npc.seguirUsuario(this.userName);
		}
	}

	public void doInfoUsuario(String s) {
		// INFO DE USER
		// Comando /INFO usuario
		Player usuario = this.server.playerByUserName(s);
		if (usuario == null) {
			sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(this.userName, "/INFO " + s);
		sendUserStatsTxt(usuario);
	}

	private void sendUserStatsTxt(Player usuario) {
		// Comando /EST
		sendMessage("Estadisticas de: " + usuario.userName, FontType.FONTTYPE_INFOBOLD);
		sendMessage("Nivel: " + usuario.stats().ELV + "  EXP: " + usuario.stats().Exp + "/" + usuario.stats().ELU,
				FontType.FONTTYPE_INFO);
		sendMessage("Salud: " + usuario.stats().MinHP + "/" + usuario.stats().MaxHP + "  Mana: "
				+ usuario.stats().mana + "/" + usuario.stats().maxMana + "  Energia: " + usuario.stats().stamina
				+ "/" + usuario.stats().maxStamina, FontType.FONTTYPE_INFO);
		if (usuario.userInv.tieneArmaEquipada()) {
			sendMessage("Menor Golpe/Mayor Golpe: " + usuario.stats().MinHIT + "/" + usuario.stats().MaxHIT + " ("
					+ this.userInv.getArma().MinHIT + "/" + this.userInv.getArma().MaxHIT + ")", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Menor Golpe/Mayor Golpe: " + usuario.stats().MinHIT + "/" + usuario.stats().MaxHIT,
					FontType.FONTTYPE_INFO);
		}
		if (usuario.userInv.tieneArmaduraEquipada()) {
			sendMessage("(CUERPO) Min Def/Max Def: " + usuario.userInv.getArmadura().MinDef + "/"
					+ usuario.userInv.getArmadura().MaxDef, FontType.FONTTYPE_INFO);
		} else {
			sendMessage("(CUERPO) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		if (usuario.userInv.tieneCascoEquipado()) {
			sendMessage("(CABEZA) Min Def/Max Def: " + this.userInv.getCasco().MinDef + "/" + this.userInv.getCasco().MaxDef,
					FontType.FONTTYPE_INFO);
		} else {
			sendMessage("(CABEZA) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		if (getGuildInfo().esMiembroClan()) {
			sendMessage("Clan: " + usuario.guildUser.m_guildName, FontType.FONTTYPE_INFO);
			if (usuario.guildUser.m_esGuildLeader) {
				if (usuario.guildUser.m_clanFundado.equals(usuario.guildUser.m_guildName)) {
					sendMessage("Status: Fundador/Lider", FontType.FONTTYPE_INFO);
				} else {
					sendMessage("Status: Lider", FontType.FONTTYPE_INFO);
				}
			} else {
				sendMessage("Status: " + usuario.guildUser.m_guildPoints, FontType.FONTTYPE_INFO);
			}
			sendMessage("User GuildPoints: " + usuario.guildUser.m_guildPoints, FontType.FONTTYPE_INFO);
		}
		sendMessage("Oro: " + usuario.stats().getGold() + "  Posicion: " + usuario.pos().x + "," + usuario.pos().y
				+ " en mapa " + usuario.pos().map, FontType.FONTTYPE_INFO);
	}

	public void doQuieto() {
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

	public void doAcompañar() {
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
			npc.expresar();
		}
	}

	public void doAyuda() {
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

	public void doRetirarFaccion() {
		// Comando /RETIRAR faccion
		// Salir de la facción Armada/Caos
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null && npc.isNoble()) {
			retirarUsuarioFaccion(npc);
	}
	}

	private void retirarUsuarioFaccion(Npc npc) {
		// Se quiere retirar de la armada
		if (checkNpcNear(npc, DISTANCE_FACTION)) {
			if (this.faction.ArmadaReal) {
				if (!npc.isFaction()) {
					this.faction.expulsarFaccionReal();
					hablar(COLOR_BLANCO, "Serás bienvenido a las fuerzas imperiales si deseas regresar.", npc.getId());
				} else {
					hablar(COLOR_BLANCO, "¡¡¡Sal de aquí bufón!!!", npc.getId());
				}
			} else if (this.faction.FuerzasCaos) {
				if (npc.isFaction()) {
					this.faction.expulsarFaccionCaos();
					hablar(COLOR_BLANCO, "Ya volverás arrastrándote.", npc.getId());
				} else {
					hablar(COLOR_BLANCO, "Sal de aquí maldito criminal", npc.getId());
				}
			} else {
				hablar(COLOR_BLANCO, "¡No perteneces a ninguna fuerza!", npc.getId());
			}
		}
	}

	public void bankStart() {
		// Comando /BOVEDA
		// Abrir bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
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

	public void bankDepositItem(short slot, int amount) {
		// Comando DEPO
		// Depositar un item en la bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			// ¿El Npc puede comerciar?
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
		// Retirar un item de la bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			// ¿El Npc puede comerciar?
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
		if (this.userInv.getObjeto(slot).cant > 0 && !this.userInv.getObjeto(slot).equipado) {
			if (cant > 0 && cant > this.userInv.getObjeto(slot).cant) {
				cant = this.userInv.getObjeto(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userDejaObj(slot, cant);
			// Actualizamos el inventario del usuario
			sendInventoryToUser();
			// Actualizamos el inventario del banco
			updateBankUserInv();
			// Actualizamos la ventana del banco
			updateVentanaBanco(slot, 1);
		}
	}

	private void userDejaObj(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		short objid = this.userInv.getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (int i = 1; i <= this.bankInv.size(); i++) {
			if (this.bankInv.getObjeto(i).objid == objid && this.bankInv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio antes del slot devuelto
		if (slot_inv == 0) {
			slot_inv = this.bankInv.getSlotLibre();
		}
		if (slot_inv == 0) {
			sendMessage("No tienes mas espacio en el banco!!", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		this.bankInv.getObjeto(slot_inv).objid = objid;
		this.bankInv.getObjeto(slot_inv).cant += cant;
		this.userInv.quitarUserInvItem(slot, cant);
	}

	private void userRetiraItem(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		sendUpdateUserStats();
		if (this.bankInv.getObjeto(slot).cant > 0) {
			if (cant > this.bankInv.getObjeto(slot).cant) {
				cant = this.bankInv.getObjeto(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userReciveObj(slot, cant);
			// Actualizamos el inventario del usuario
			sendInventoryToUser();
			// Actualizamos el banco
			updateBankUserInv();
			// ventana update
			updateVentanaBanco(slot, 0);
		}

	}

	private void userReciveObj(short slot, int cant) {
		if (this.bankInv.getObjeto(slot).cant <= 0) {
			return;
		}
		short objid = this.bankInv.getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= this.userInv.size(); i++) {
			if (this.userInv.getObjeto(i).objid == objid && this.userInv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = this.userInv.getSlotLibre();
		}
		if (slot_inv == 0) {
			sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		if (this.userInv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			this.userInv.getObjeto(slot_inv).objid = objid;
			this.userInv.getObjeto(slot_inv).cant += cant;
			quitarBancoInvItem(slot, cant);
		} else {
			sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private void quitarBancoInvItem(short slot, int cant) {
		// Quita un Obj
		this.bankInv.getObjeto(slot).cant -= cant;
		if (this.bankInv.getObjeto(slot).cant <= 0) {
			this.bankInv.getObjeto(slot).objid = 0;
			this.bankInv.getObjeto(slot).cant = 0;
		}
	}

	private void updateVentanaBanco(short slot, int npc_inv) {
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
		if (this.bankInv.getObjeto(slot).objid > 0) {
			sendBanObj(slot, this.bankInv.getObjeto(slot));
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

	public void cambiarPasswd(String newPassword) {
		// Comando /PASSWD
		newPassword = newPassword.trim();
		if (newPassword.length() < 6) {
			sendMessage("El password debe tener al menos 6 caracteres.", FontType.FONTTYPE_INFO);
		} else if (newPassword.length() > 20) {
			sendMessage("El password puede tener hasta 20 caracteres.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("El password ha sido cambiado. ¡Cuídalo!", FontType.FONTTYPE_INFO);
			this.password = newPassword;
		}
	}

	public void doConstruyeHerreria(short objid) {
		if (objid < 1) {
			return;
		}
		ObjectInfo info = findObj(objid);
		if (info.SkHerreria == 0) {
			return;
		}
		herreroConstruirItem(objid);
	}

	public void doConstruyeCarpinteria(short objid) {
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
			hablar(COLOR_BLANCO, "No puedo traer mas criaturas, mata las existentes!", npc.getId());
		} else {
			npcTrainer.spawnTrainerPet(petIndex);
		}
	}

	public void commerceBuyFromMerchant(byte slotNpc, short amount) {
		// Comando COMP
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
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
		// ¿El Npc puede comerciar?
		if (!npc.isTrade()) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}
		if (npc.npcInv().isSlotValid(slotNpc)) {
			((NpcMerchant)npc).sellItemToUser(this, slotNpc, amount);
		}
	}

	public void commerceSellToMerchant(byte slot, short amount) {
		// Comando VEND
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
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
		// ¿El Npc puede comerciar?
		if (!npc.isTrade()) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}

		if (npc.npcInv().isSlotValid(slot)) {
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
		if (isCounselor()) {
			return;
		}

		if (isTrading()) {
			sendMessage("Ya estás comerciando", FontType.FONTTYPE_INFO);
			return;
		}

		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_MERCHANT);
		if (npc != null) {
			// ¿El Npc puede comerciar?
			if (!npc.isTrade()) {
				if (npc.getDesc().length() > 0) {
					hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
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
			if (isCounselor()) {
				sendMessage("No puedes vender items.", FontType.FONTTYPE_WARNING);
                return;
			}

			Player targetPlayer = this.server.playerById(flags().TargetUser);
			if (targetPlayer == null) {
				return;
			}
            //Is the other one dead??
			if (!targetPlayer.isAlive()) {
				sendMessage("¡¡No puedes comerciar con los muertos!!", FontType.FONTTYPE_INFO);
                return;
			}

            //Is it me??
			if (targetPlayer == this) {
				sendMessage("No puedes comerciar con vos mismo...", FontType.FONTTYPE_INFO);
                return;
			}

            //Check distance
			if (pos().distance(targetPlayer.pos()) > 3) {
				sendMessage("Estás demasiado lejos del usuario.", FontType.FONTTYPE_INFO);
                return;
			}

            //Is he already trading?? is it with me or someone else??
			if (targetPlayer.isTrading() && targetPlayer.flags().TargetUser != this.getId()) {
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

	public void doEntrenar() {
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

	public void doDescansar() {
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

	public void doNavega() {
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
		if (!mapa.isLegalPos(targetPos, false)) {
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
		boolean exito = Util.Azar(1, suerte) == 1;

		if (exito) {
			short objid = FOGATA_APAG;
			int cant = mapa.getObject(targetPos.x, targetPos.y).obj_cant / 3;
			if (cant > 1) {
				sendMessage("Has hecho " + cant + " fogatas.", FontType.FONTTYPE_INFO);
			} else {
				sendMessage("Has hecho una fogata.", FontType.FONTTYPE_INFO);
			}
			mapa.quitarObjeto(targetPos.x, targetPos.y);
			mapa.agregarObjeto(objid, cant, targetPos.x, targetPos.y);
		} else {
			if (flags().UltimoMensaje != 10) {
				sendMessage("No has podido hacer la fogata.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 10;
			}
		}
		subirSkill(Skill.SKILL_Supervivencia);

		this.server.getTrashCollector().add(targetPos);
	}

	public void useItem(short slot) {
		if (this.userInv.getObjeto(slot) != null && this.userInv.getObjeto(slot).objid == 0) {
			return;
		}
		this.userInv.useInvItem(slot);
	}

	private void doLingotes() {
		if (this.userInv.getObjeto(flags().TargetObjInvSlot).cant < 5) {
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
		if (Util.Azar(1, info.MinSkill) <= 10) {
			sendMessage("Has obtenido un lingote!!!", FontType.FONTTYPE_INFO);
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(info.LingoteIndex, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(info.LingoteIndex, 1));
			}
			sendMessage("¡Has obtenido un lingote!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 7) {
				sendMessage("Los minerales no eran de buena calidad, no has logrado hacer un lingote.",
						FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 7;
			}
		}
		flags().Trabajando = true;
	}

	private void fundirMineral() {
		if (flags().TargetObjInvIndex > 0) {
			ObjectInfo info = findObj(flags().TargetObjInvIndex);
			if (info.objType == ObjType.Minerales
					&& info.MinSkill <= skills().get(Skill.SKILL_Mineria) / this.clazz().modFundicion()) {
				doLingotes();
			} else {
				sendMessage("No tenes conocimientos de mineria suficientes para trabajar este mineral.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	private double calcularPoderDomador() {
		return stats().attr().get(Attribute.CARISMA) *
				(skills().get(Skill.SKILL_Domar) / this.clazz().modDomar())
					+ Util.Azar(1, stats().attr().get(Attribute.CARISMA) / 3)
					+ Util.Azar(1, stats().attr().get(Attribute.CARISMA) / 3)
					+ Util.Azar(1, stats().attr().get(Attribute.CARISMA) / 3);
	}

	private void doDomar(Npc npc) {
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

		double suerteDoma = calcularPoderDomador();
		if (npc.domable() <= suerteDoma) {
			getUserPets().addPet(npc);
			npc.setPetUserOwner(this);
			npc.followMaster();
			sendMessage("La criatura te ha aceptado como su amo.", FontType.FONTTYPE_INFO);
			subirSkill(Skill.SKILL_Domar);
			// y hacemos respawn del npc original para reponerlo.
			Npc.spawnNpc(npc.getNumero(), MapPos.mxy(npc.pos().map, (short) 0, (short) 0), false, true);
		} else {
			if (flags().UltimoMensaje != 5) {
				sendMessage("No has logrado domar la criatura.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 5;
			}
		}
	}

	private boolean suerteMineria() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Mineria) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doMineria() {
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
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has extraido algunos minerales!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 9) {
				sendMessage("¡No has conseguido nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 9;
			}
		}
		subirSkill(Skill.SKILL_Mineria);
		flags().Trabajando = true;
	}

	private boolean suerteTalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 13, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Talar) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doTalar() {
		stats().quitarStamina(this.clazz().getEsfuerzoTalar());
		if (suerteTalar()) {
			int cant = this.clazz().getCantLeños();
			short objid = Leña;
			Map mapa = this.server.getMap(pos().map);
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has conseguido algo de leña!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 8) {
				sendMessage("No has conseguido leña. Intenta otra vez.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 8;
			}
		}
		subirSkill(Skill.SKILL_Talar);
		flags().Trabajando = true;
	}

	private void robarObjeto(Player victima) {
		boolean flag = false;
		short slot = 0;
		if (Util.Azar(1, 12) < 6) { // Comenzamos por el principio o el final?
			slot = 1;
			while (slot <= MAX_INVENTORY_SLOTS) {
				// Hay objeto en este slot?
				if (victima.userInv.getObjeto(slot).objid > 0) {
					if (victima.userInv.getObjeto(slot).esRobable()) {
						if (Util.Azar(1, 10) < 4) {
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
				if (victima.userInv.getObjeto(slot).objid > 0) {
					if (victima.userInv.getObjeto(slot).esRobable()) {
						if (Util.Azar(1, 10) < 4) {
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
			int cant = Util.Azar(1, 5);
			short objid = this.userInv.getObjeto(slot).objid;
			if (cant > this.userInv.getObjeto(slot).cant) {
				cant = this.userInv.getObjeto(slot).cant;
			}
			victima.userInv.quitarUserInvItem(slot, cant);
			victima.sendInventorySlot(slot);
			Map mapa = this.server.getMap(pos().map);
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendInventorySlot(slot);
			ObjectInfo info = findObj(objid);
			sendMessage("Has robado " + cant + " " + info.Nombre, FontType.FONTTYPE_INFO);
		} else {
			sendMessage("No has logrado robar un objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private boolean suerteRobar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Robar) / 10)]);
		return (Util.Azar(1, rango) < 3);
	}

	private void doRobar(Player victima) {
		Map mapa = this.server.getMap(pos().map);
		if (mapa.isSafeMap() || duelStatus(victima) != DuelStatus.DUEL_MISSING) {
			return;
		}
		if (flags().Privilegios < 2) {
			if (suerteRobar()) {
				// Exito robo
				if ((Util.Azar(1, 50) < 25) && (this.clazz == Clazz.Thief)) {
					if (victima.userInv.tieneObjetosRobables()) {
						robarObjeto(victima);
					} else {
						sendMessage(victima.userName + " no tiene objetos.", FontType.FONTTYPE_INFO);
					}
				} else { // Roba oro
					if (victima.stats().getGold() > 0) {
						int cantidadRobada = Util.Azar(1, 100);
						victima.stats().addGold( -cantidadRobada );
						stats().addGold( cantidadRobada );
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
				volverCriminal();
			}
			if (this.faction.ArmadaReal) {
				this.faction.expulsarFaccionReal();
			}
			this.reputation.incLandron(vlLadron);
			subirSkill(Skill.SKILL_Robar);
		}
	}

	private boolean suertePescarCaña() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Pesca) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarCaña() {
		stats().quitarStamina(this.clazz().getEsfuerzoPescar());
		if (suertePescarCaña()) {
			Map mapa = this.server.getMap(pos().map);
			if (this.userInv.agregarItem(OBJ_PESCADO, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(OBJ_PESCADO, 1));
			}
			sendMessage("¡Has pescado un lindo pez!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 6) {
				sendMessage("¡No has pescado nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 6;
			}
		}
		subirSkill(Skill.SKILL_Pesca);
		flags().Trabajando = true;
	}

	private boolean suertePescarRed() {
		final short[] suerte = { 60, 54, 49, 43, 38, 32, 27, 21, 16, 11, 11 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Pesca) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarRed() {
		stats().quitarStamina(this.clazz().getEsfuerzoPescar());
		int skills = skills().get(Skill.SKILL_Pesca);
		if (skills == 0) {
			return;
		}
		if (suertePescarRed()) {
			int cant = (this.clazz == Clazz.Fisher) ? Util.Azar(1, 5) : 1;
			short objid = (short) PESCADOS_RED[Util.Azar(1, PESCADOS_RED.length) - 1];
			int agregados = this.userInv.agregarItem(objid, cant);
			if (agregados < cant) {
				Map mapa = this.server.getMap(pos().map);
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			sendMessage("¡Has pescado algunos peces!", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("¡No has pescado nada!", FontType.FONTTYPE_INFO);
		}
		subirSkill(Skill.SKILL_Pesca);
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
		Player tu = null;
		MapObject obj = null;

		if (skill == Skill.SKILL_FundirMetal) {
			// UGLY HACK!!! This is a constant, not a skill!!
			mapa.lookAtTile(this, x, y);
			if (flags().TargetObj > 0) {
				if (findObj(flags().TargetObj).objType == ObjType.Fragua) {
					fundirMineral();
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
					stats().quitarStamina(Util.Azar(1, 10));
				} else {
					if (gender() == UserGender.GENERO_HOMBRE) {
						sendMessage("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
					} else {
						sendMessage("Estas muy cansada para luchar.", FontType.FONTTYPE_INFO);
					}					
					return;
				}

				mapa.lookAtTile(this, x, y);
				tu = this.server.playerById(flags().TargetUser);
				Npc npc = this.server.npcById(flags().TargetNpc);
				if (npc != null) {
					if (!npc.isAttackable()) {
						return;
					}
				} else {
					if (tu == null) {
						return;
					}
				}
				if (npc != null) {
					usuarioAtacaNpc(npc);
				}
				if (tu != null) {
					if (flags().Seguro) {
						if (!tu.isCriminal()) {
							sendMessage(
									"No puedes atacar a ciudadanos, para hacerlo, antes debes desactivar el seguro con la tecla S",
									FONTTYPE_FIGHT);
							return;
						}
					}
					usuarioAtacaUsuario(tu);
				}
				// Consumir munición.
				int slotMunicion = this.userInv.getMunicionSlot();
				this.userInv.quitarUserInvItem(this.userInv.getMunicionSlot(), 1);
				if (this.userInv.getObjeto(slotMunicion) != null && this.userInv.getObjeto(slotMunicion).cant > 0) {
					this.userInv.equipar(slotMunicion);
				}
				sendInventorySlot(slotMunicion);
				break;


			case SKILL_Magia:
				if (!counters().intervaloPermiteLanzarSpell()) {
					return;
				}
				if (isCounselor())
					return;
				mapa.lookAtTile(this, x, y);
				if (flags().Hechizo > 0) {
					this.spells.hitSpell(this.server.getSpell(flags().Hechizo));
					flags().Hechizo = 0;
				} else {
					sendMessage("¡Primero selecciona el hechizo que deseas lanzar!", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Pesca:
				if (!counters().intervaloPermiteTrabajar()) {
					return;
				}
				if (!this.userInv.tieneArmaEquipada()) {
					sendMessage("Deberías equiparte la caña o la red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (this.userInv.getArma().ObjIndex != OBJ_INDEX_CAÑA
						&& this.userInv.getArma().ObjIndex != OBJ_INDEX_RED_PESCA) {
					sendMessage("Deberías equiparte la caña o la red.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isUnderRoof(pos().x,  pos().y)) {
					sendMessage("No puedes pescar desde donde te encuentras.", FontType.FONTTYPE_INFO);
					return;
				}
				if (mapa.isWater(x, y)) {
					sendWave(SOUND_PESCAR);
					switch (this.userInv.getArma().ObjIndex) {
					case OBJ_INDEX_CAÑA:
						doPescarCaña();
						break;
					case OBJ_INDEX_RED_PESCA:
						if (pos().distance(MapPos.mxy(pos().map, x, y)) > 2) {
							sendMessage("Estás demasiado lejos para pescar.", FontType.FONTTYPE_INFO);
							return;
						}
						doPescarRed();
						break;
					}
				} else {
					sendMessage("No hay agua donde pescar. Busca un lago, rio o mar.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Robar:
				if (!mapa.isSafeMap()) {
					if (!counters().intervaloPermiteTrabajar()) {
						return;
					}
					mapa.lookAtTile(this, x, y);
					if (flags().TargetUser > 0 && flags().TargetUser != getId()) {
						tu = this.server.playerById(flags().TargetUser);
						if (tu.isAlive()) {
							MapPos wpaux = MapPos.mxy(pos().map, x, y);
							if (wpaux.distance(pos()) > 2) {
								sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
								return;
							}
							// Nos aseguramos que el trigger le permite robar
							if (mapa.isSafeZone(tu.pos().x, tu.pos().y)) {
								sendMessage("No podes robar aquí.", FontType.FONTTYPE_WARNING);
								return;
							}
							doRobar(tu);
						}
					} else {
						sendMessage("No hay a quien robarle!.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("¡No podes robarle en zonas seguras!.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Talar:
				if (!counters().intervaloPermiteTrabajar()) {
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
						sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// ¿Hay un arbol donde cliqueo?
					if (obj.getInfo().objType == ObjType.Arboles) {
						sendWave(SOUND_TALAR);
						doTalar();
					}
				} else {
					sendMessage("No hay ningun arbol ahi.", FontType.FONTTYPE_INFO);
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
						sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					// ¿Hay un yacimiento donde cliqueo?
					if (obj.getInfo().objType == ObjType.Yacimiento) {
						sendWave(SOUND_MINERO);
						doMineria();
					} else {
						sendMessage("Ahi no hay ningun yacimiento.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ahi no hay ningun yacimiento.", FontType.FONTTYPE_INFO);
				}
				break;


			case SKILL_Domar:
				// Modificado 25/11/02
				// Optimizado y solucionado el bug de la doma de
				// criaturas hostiles.
				mapa.lookAtTile(this, x, y);
				if (flags().TargetNpc > 0) {
					npc = this.server.npcById(flags().TargetNpc);
					if (npc.domable() > 0) {
						MapPos wpaux = MapPos.mxy(pos().map, x, y);
						if (wpaux.distance(pos()) > 2) {
							sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
							return;
						}
						if (npc.isAttackedByUser()) {
							sendMessage("No puedes domar una criatura que está luchando con un jugador.", FontType.FONTTYPE_INFO);
							return;
						}
						doDomar(npc);
					} else {
						sendMessage("No puedes domar a esa criatura.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("No hay ninguna criatura alli!.", FontType.FONTTYPE_INFO);
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
						sendMessage("Ahi no hay ningun yunque.", FontType.FONTTYPE_INFO);
					}
				} else {
					sendMessage("Ahi no hay ningun yunque.", FontType.FONTTYPE_INFO);
				}
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
			doOcultarse();
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

	public void doInformacion() {
		// Comando /INFORMACION
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_INFORMATION);
		if (npc == null) {
			return;
		}
		if (!npc.isFaction()) {
			if (!this.faction.ArmadaReal) {
				hablar(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			hablar(COLOR_BLANCO,
					"Tu deber es combatir criminales, cada 100 criminales que derrotes te dare una recompensa.",
					npc.getId());
		} else {
			if (!this.faction.FuerzasCaos) {
				hablar(COLOR_BLANCO, "No perteneces a las fuerzas del caos!!!", npc.getId());
				return;
			}
			hablar(COLOR_BLANCO,
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
		if (!flags().Meditando) {
			sendMessage("Comienzas a meditar.", FontType.FONTTYPE_INFO);
		} else {
			sendMessage("Dejas de meditar.", FontType.FONTTYPE_INFO);
		}
		flags().Meditando = !flags().Meditando;
		if (flags().Meditando) {
			this.counters.tInicioMeditar = Util.millis();
			int segs = (TIEMPO_INICIO_MEDITAR / 1000);
			sendMessage("Te estás concentrando. En " + segs + " segundos comenzarás a meditar.", FontType.FONTTYPE_INFO);
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

	public void doResucitar() {
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

	public void doCurar() {
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
		stats().MinHP = stats().MaxHP;
		sendUpdateUserStats();
		sendMessage("¡¡Has sido curado!!", FontType.FONTTYPE_INFO);
	}

	public void cambiarDescripcion(String s) {
		// Comando "/DESC "
		s = s.trim();
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		if (!Util.asciiValidos(s)) {
			sendMessage("La descripcion tiene caracteres invalidos.", FontType.FONTTYPE_INFO);
			return;
		}
		this.description = s;
		sendMessage("La descripcion a cambiado.", FontType.FONTTYPE_INFO);
	}

	public void changeHeading(byte newHeading) {
		if (newHeading < 1 || newHeading > 4) {
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
			if (mapa != null && mapa.hasPlayer(this)) {
				getUserPets().removeAll();
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
			this.server.dropPlayer(this);
			if (wasLogged) {
				try {
					saveUser();
				} catch (Exception ex) {
					log.fatal("ERROR EN doSALIR() - saveUser(): ", ex);
				}
			}
		}
		log.info("Salió: " + getNick());
	}

	public void throwDices() { // and get lucky!

		// FIXME dados fáciles, hacerlo configurable
		stats().attr().set(Attribute.FUERZA, Util.Azar(16, 18));
		stats().attr().set(Attribute.AGILIDAD, Util.Azar(16, 18));
		stats().attr().set(Attribute.INTELIGENCIA, Util.Azar(16, 18));
		stats().attr().set(Attribute.CARISMA, Util.Azar(16, 18));
		stats().attr().set(Attribute.CONSTITUCION, Util.Azar(16, 18));

		sendPacket(new DiceRollResponse(
			stats().attr().get(Attribute.FUERZA),
			stats().attr().get(Attribute.AGILIDAD),
			stats().attr().get(Attribute.INTELIGENCIA),
			stats().attr().get(Attribute.CARISMA),
			stats().attr().get(Attribute.CONSTITUCION)));
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
	public void clicDerechoMapa(byte x, byte y) {
		Map mapa = this.server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		// ¿Posicion valida?
		if (Pos.isValid(x, y)) {
			// ¿Hay un objeto en el tile?
			if (mapa.hasObject(x, y)) {
				MapObject obj = mapa.getObject(x, y);
				flags().TargetObj = obj.getInfo().ObjIndex;
				switch (obj.getInfo().objType) {
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
					if (flags().TargetObj == FOGATA_APAG) {
						mapa.accionParaRamita(x, y, this);
					}
					break;
				}
			} else {
				// ¿Hay un objeto que ocupa más de un tile?
				MapObject obj = mapa.queryObject(x, y);
				Npc npc;
				if (obj != null) {
					flags().TargetObj = obj.getInfo().ObjIndex;
					if (obj.getInfo().objType == ObjType.Puertas) {
						mapa.accionParaPuerta(obj.x, obj.y, this);
					}
				} else if ((npc = mapa.getNPC(x, y)) != null) {
					flags().TargetNpc = npc.getId();
					if (npc.isTrade()) {
						// Doble clic sobre un comerciante, hace /COMERCIAR
						commerceStart();
					} else if (npc.isBankCashier()) {
						if (!checkAlive()) {
							return;
						}
						// Extensión de AOJ - 16/08/2004
						// Doble clic sobre el banquero hace /BOVEDA
						if (checkNpcNear(npc, DISTANCE_CASHIER)) {
							iniciarDeposito();
						}
					} else if (npc.isPriest() || npc.isPriestNewbies()) {
						// Extensión de AOJ - 01/02/2007
						// Doble clic sobre el sacerdote hace /RESUCITAR o /CURAR
						if (isAlive()) {
							doCurar();
						} else {
							doResucitar();
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
	public void talk(String text) {
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
							Color.r(COLOR_BLANCO), Color.g(COLOR_BLANCO), Color.b(COLOR_BLANCO)));
		}
		if (isCounselor()) {
			Log.logGM(this.userName, "El consejero dijo: " + text);
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
		if (isCounselor()) {
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
		Player targetUser = this.server.playerById(targetIndex);
		if (targetUser != null) {
			if (map.buscarEnElArea(pos().x, pos().y, targetUser.getId()) == null) {
				sendMessage("Estas muy lejos de " + targetUser.getNick(), FontType.FONTTYPE_INFO);
			} else {
				if (isCounselor()) {
					Log.logGM(this.userName, "El consejero le susurró a " + targetUser.getNick() + ": " + text);
				}

				// send to target user
				targetUser.sendPacket(new ChatOverHeadResponse(text, getId(),
								Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));
				// send to source user
				sendPacket(new ChatOverHeadResponse(text, getId(),
						Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));

				if (!isGM() || isCounselor()) {
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
				volverseVisible();
			}
		}
		move(heading);
		flags().Trabajando = false;
	}

	private void volverseVisible() {
		sendMessage("¡Has vuelto a ser visible!", FontType.FONTTYPE_INFO);
		flags().Oculto = false;
		flags().Invisible = false;
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)0));
	}

	private void move(Heading heading) {
		MapPos oldPos = pos().copy();
		Map map = this.server.getMap(pos().map);
		if (map == null) {
			return;
		}

		infoChar().heading(heading);
		MapPos newPos = pos().copy().moveToHeading(heading);
		if (newPos.isValid()
				&& map.isFree(newPos.x, newPos.y)
				&& !map.isBlocked(newPos.x, newPos.y)) {

			map.movePlayer(this, newPos.x, newPos.y);

			// TELEPORT PLAYER
			if (map.isTeleport(newPos.x, newPos.y)) {
				// Esto es similar al DoTileEvents original
				MapPos targetPos	= map.teleportTarget(newPos.x, newPos.y);
				boolean withFX 		= map.isTeleportObject(newPos.x, newPos.y);
				boolean sendingData = pos().map != targetPos.map;
				if (!enterIntoMap(targetPos.map, targetPos.x, targetPos.y, withFX, sendingData)) {
					// if fails, go back to the closest original position
					MapPos warpPos = map.closestLegalPosPlayer(oldPos.x, oldPos.y, flags().Navegando, isGM());
					warpMe(warpPos.map, warpPos.x, warpPos.y, true);
				}
			}
		} else {
			// player can not move
			sendPositionUpdate();
		}
	}

	public void atack() {
		if (!checkAlive("¡¡No puedes atacar a nadie por estar muerto!!")) {
			return;
		}
		if (isCounselor()) {
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
				volverseVisible();
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
		if (isCounselor()) {
			sendMessage("No puedes recoger ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		getObj();
	}

	public void dropObject(byte slot, int cant) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden tirar objetos.")) {
			return;
		}
		if (isSailing()) {
			sendMessage("No puedes tirar objetos mientras navegas.", FontType.FONTTYPE_INFO);
			return;
		}
		if (isCounselor()) {
			sendMessage("No puedes tirar ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		if (slot == FLAGORO) {
			tirarOro(cant);
			sendUpdateUserStats();
		} else {
			if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
				if (this.userInv.getObjeto(slot) != null && this.userInv.getObjeto(slot).objid > 0) {
					this.userInv.dropObj(slot, cant);
				}
			}
		}
	}

	public void getObj() {
		Map mapa = this.server.getMap(pos().map);
		// ¿Hay algun obj?
		if (mapa.hasObject(pos().x, pos().y)) {
			// ¿Esta permitido agarrar este obj?
			MapObject obj = mapa.getObject(pos().x, pos().y);
			if (!obj.getInfo().esAgarrable()) {
				sendMessage("El objeto no se puede agarrar.", FontType.FONTTYPE_INFO);
			} else {
				int agregados = this.userInv.agregarItem(obj.obj_ind, obj.obj_cant);
				if (agregados < obj.obj_cant) {
					mapa.quitarObjeto(pos().x, pos().y);
					mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(obj.obj_ind, obj.obj_cant - agregados));
				} else {
					// Quitamos el objeto
					mapa.quitarObjeto(pos().x, pos().y);
					if (isGM()) {
						Log.logGM(this.userName, "Agarró: " + obj.obj_ind + " objeto=" + obj.getInfo().Nombre);
					}
				}
			}
		} else {
			sendMessage("No hay nada aqui.", FontType.FONTTYPE_INFO);
		}
	}

	public void equipItem(short slot) {
		// Comando EQUI
		if (!checkAlive("¡¡Estas muerto!! Solo puedes usar items cuando estas vivo.")) {
			return;
		}
		if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
			if (this.userInv.getObjeto(slot) != null && this.userInv.getObjeto(slot).objid > 0) {
				this.userInv.equipar(slot); // EquiparInvItem
			}
		}
	}

	public void sendInventoryToUser() {
		// updateUserInv
		for (int i = 1; i <= this.userInv.size(); i++) {
			sendInventorySlot(i);
		}
	}

	public void sendInventorySlot(int slot) {
		InventoryObject inv = this.userInv.getObjeto(slot);

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
			if (oldMap.hasPlayer(this) && !oldMap.exitMap(this)) {
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

		MapPos freePos = targetMap.closestLegalPosPlayer(x, y, flags().Navegando, isGM());

		// enter into the new map
		if (!targetMap.enterMap(this, freePos.x, freePos.y)) {
			sendMessage("No se pudo entrar en el mapa deseado", FontType.FONTTYPE_WARNING);
			return false;
		}

		pos().set(mapNumber, freePos.x, freePos.y);
		if (sendingData) {
			sendPacket(new ChangeMapResponse(mapNumber, targetMap.getVersion()));
		}
		sendPacket(characterCreate());

		targetMap.areasData.loadUser(this);
		sendPositionUpdate();

		if (withFX) {
			sendCreateFX(1, 0);
			if (flags().UserLogged) { // No hacer sonido en el LOGIN.
				sendWave(SOUND_WARP);
			}
		}

		sendCharIndexInServer();
		if (sendingData) {
			targetMap.sendPlayers(this);
			targetMap.sendObjects(this);
			targetMap.sendBlockedPositions(this);
		}

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
		MapPos oldPos = pos().copy();
		// Quitar el dialogo
		Map map = this.server.getMap(pos().map);
		if (map != null) {
			map.sendToArea(x, y, new RemoveCharDialogResponse(getId()));
		}

		boolean changingMap = pos().map != targetMap;

		// Si el destino es distinto a la posición actual
		if (changingMap || pos().x != x || pos().y != y) {
			Map newMap = this.server.getMap(targetMap);
			MapPos pos_libre = newMap.closestLegalPosPlayer(x, y, flags().Navegando, isGM());
			if (pos_libre == null) {
				log.warn("WARPUSER FALLO: no hay un lugar libre cerca de mapa=" + targetMap + " x=" + x + " y=" + y);
				return false;
			}
			x = pos_libre.x;
			y = pos_libre.y;
			if (!enterIntoMap(targetMap, x, y, false, changingMap)) {
				// try to go back to the original pos
				enterIntoMap(oldPos.map, x, y, false, false);
			}
		}
		sendPositionUpdate();

		// Seguis invisible al pasar de mapa
		if ((flags().Invisible || flags().Oculto) && !flags().AdminInvisible) {
			map.sendToArea(x, y, new SetInvisibleResponse(getId(), (byte)1));
		}
		if (withFX && !flags().AdminInvisible) {
			sendWave(SOUND_WARP);
			sendCreateFX(FXWARP, 0);
		}
		warpPets();

		return true;
	}

	private void warpPets() {
		// copy list first
		var pets = getUserPets().getPets().stream().collect(Collectors.toList());

		pets.forEach(pet -> {
			if (pet.counters().TiempoExistencia > 0) {
				// Es una mascota de invocación. Se pierde al cambiar de mapa
				getUserPets().removePet(pet);
			} else if (pet.puedeReSpawn()) {
				// Es una mascota domada que puede hacer respawn

				Map oldMapa = this.server.getMap(pet.pos().map);
				Map newMapa = this.server.getMap(pos().map);
				MapPos lugarLibre = newMapa.closestLegalPosNpc(pos().x, pos().y, pet.isWaterValid(), pet.isLandInvalid(), true);

				if (lugarLibre != null) {
					// La mascota lo sigue al nuevo mapa, y mantiene su control.
					oldMapa.exitNpc(pet);
					if (!newMapa.enterNpc(pet, lugarLibre.x, lugarLibre.y)) {
						// FIXME !!!
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
			sendMessage("Pierdes el control de tus mascotas.", FontType.FONTTYPE_INFO);
		}
	}

	public void sendToJail(int minutos, String gm_name) {
		this.counters.Pena = minutos;
		if (warpMe(GameServer.WP_PRISION.map, GameServer.WP_PRISION.x, GameServer.WP_PRISION.y, true)) {
			if (gm_name == null) {
				sendMessage("Has sido encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.FONTTYPE_INFO);
			} else {
				sendMessage(gm_name + " te ha encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void efectoLluvia() {
		if (flags().UserLogged) {
			Map mapa = this.server.getMap(pos().map);
			if (this.server.isRaining() && mapa.isOutdoor(pos().x, pos().y) && mapa.getZone() != Zone.DUNGEON) {
				int modifi = Util.porcentaje(stats().maxStamina, 3);
				stats().quitarStamina(modifi);
				sendMessage("¡¡Has perdido stamina, busca pronto refugio de la lluvia!!.", FontType.FONTTYPE_INFO);
				sendUpdateUserStats();
			}
		}
	}

	public void efectoParalisisUser() {
		if (this.counters.Paralisis > 0) {
			this.counters.Paralisis--;
		} else {
			flags().Paralizado = false;
			flags().Inmovilizado = false;
			sendPacket(new ParalizeOKResponse());
		}
	}

	private boolean suerteMostrarse() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Ocultarse) / 10)]);
		if (this.clazz != Clazz.Thief) {
			rango += 50;
		}
		return (Util.Azar(1, rango) > 9);
	}

	public void doPermanecerOculto() {
		if (suerteMostrarse()) {
			volverseVisible();
		}
	}

	private boolean suerteOcultarse() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (skills().get(Skill.SKILL_Ocultarse) / 10)]);
		if (this.clazz != Clazz.Thief) {
			rango += 50;
		}
		return (Util.Azar(1, rango) <= 5);
	}

	private void volverseOculto() {
		flags().Oculto = true;
		flags().Invisible = true;
		Map map = this.server.getMap(pos().map);
		map.sendToArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		sendMessage("¡Te has escondido entre las sombras!", FontType.FONTTYPE_INFO);
	}

	public void doOcultarse() {
		if (suerteOcultarse()) {
			volverseOculto();
			subirSkill(Skill.SKILL_Ocultarse);
		} else {
			if (flags().UltimoMensaje != 4) {
				sendMessage("¡No has logrado esconderte!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 4;
			}
		}
		flags().Trabajando = true;
	}

	public void decirPalabrasMagicas(String palabrasMagicas) {
		hablar(COLOR_CYAN, palabrasMagicas, getId());
	}

	public void hablar(int color, String texto, int quienId) {
		if (texto.length() > MAX_TEXTO_HABLAR) {
			texto = texto.substring(0, MAX_TEXTO_HABLAR);
		}

		Map map = this.server.getMap(pos().map);
		if (map != null) {
			map.sendToArea(pos().x, pos().y,
					new ChatOverHeadResponse(texto, (short)quienId,
							Color.r(color), Color.g(color), Color.b(color)));
		}
	}

	public Npc crearMascotaInvocacion(int npcId, MapPos targetPos) {
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

	public Object[] ccParams() {
		byte crimi = (byte) (isCriminal() ? 1 : 0);

		Object[] params = {
				(short)getId(),
				(short)this.infoChar.body(),
				(short)this.infoChar.head(),

				(byte)this.infoChar.heading().value(),
				(byte)pos().x,
				(byte)pos().y,

				(short)this.infoChar.weapon(),
				(short)this.infoChar.shield(),
				(short)this.infoChar.helmet(),

				(short)this.infoChar.fx(),
				(short)this.infoChar.loops(),

				this.userName + tag(),
				(byte)crimi,
				(byte)flags().Privilegios };

		return params;
	}

	private String tag() {
		if (isGM()) {
			if (isGod()) {
				return " <GAME MASTER>";
			}
			if (isDemiGod()) {
				return " <SEMIDIOS>";
			}
			return " <CONSEJERO>";
		}
		if (!getGuildInfo().esMiembroClan()) {
			return "";
		}
		return " <" + this.guildUser.getGuildName() + ">";
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
				this.userName + tag(),
				(byte) (this.isCriminal() ? 1 : 0),
				(byte)flags().Privilegios);
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
		if (isGM()) {
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
				tirarTodo();
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

	public void tirarTodo() {
		Map map = this.server.getMap(pos().map);
		if (map.isArenaZone(pos().x, pos().y) || isGM()) {
			return;
		}
		tirarTodosLosItems();
		tirarOro(stats().getGold());
	}

	private void tirarTodosLosItems() {
		Map m = this.server.getMap(pos().map);
		for (int i = 1; i <= this.userInv.size(); i++) {
			if (this.userInv.getObjeto(i) != null && this.userInv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = findObj(this.userInv.getObjeto(i).objid);
				if (info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(this.userInv.getObjeto(i).objid, this.userInv.getObjeto(i).cant);
					this.userInv.quitarUserInvItem(i, obj_inv.cant);
					sendInventorySlot(i);
					if (info_obj.itemSeCae()) {
						m.tirarItemAlPiso(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarTodosLosItemsNoNewbies() {
		Map map = this.server.getMap(pos().map);
		for (int i = 1; i <= this.userInv.size(); i++) {
			if (this.userInv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = findObj(this.userInv.getObjeto(i).objid);
				if (!info_obj.esNewbie() && info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(this.userInv.getObjeto(i).objid, this.userInv.getObjeto(i).cant);
					this.userInv.quitarUserInvItem(i, obj_inv.cant);
					sendInventorySlot(i);
					if (info_obj.itemSeCae()) {
						map.tirarItemAlPiso(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarOro(int cantidad) {
		if (cantidad > MAX_INVENTORY_OBJS) {
			return;
		}
		Map m = this.server.getMap(pos().map);
		// SI EL Npc TIENE ORO LO TIRAMOS
		if ((cantidad > 0) && (cantidad <= stats().getGold())) {
			while ((cantidad > 0) && (stats().getGold() > 0)) {
				InventoryObject oi = new InventoryObject(OBJ_ORO, cantidad);
				if ((cantidad > MAX_INVENTORY_OBJS) && (stats().getGold() > MAX_INVENTORY_OBJS)) {
					oi.cant = MAX_INVENTORY_OBJS;
					stats().addGold( -MAX_INVENTORY_OBJS);
					cantidad -= oi.cant;
				} else {
					oi.cant = cantidad;
					stats().addGold( -cantidad );
					cantidad -= oi.cant;
				}
				if (isGM()) {
					Log.logGM(this.userName,
							"Tiró " + oi.cant + " unidades del objeto " + findObj(oi.objid).Nombre);
				}
				m.tirarItemAlPiso(pos().x, pos().y, oi);
			}
		}
	}

	public void volverCriminal() {
		Map map = this.server.getMap(pos().map);
		if (map.isArenaZone(pos().x, pos().y)) {
			return;
		}
		if (flags().Privilegios < 2) {
			if (!isCriminal()) {
				this.reputation.condenar();
				if (this.faction.ArmadaReal) {
					this.faction.expulsarFaccionReal();
				}
				refreshCharStatus();
				System.out.println(this.getNick() + " ahora es criminal");
			}
		}
	}

	public void subirSkill(Skill skill) {
		if (!flags().Hambre && !flags().Sed) {
			
			// Alcanzó el máximo del skill para este nivel?
			if (skills().get(skill) >= Skill.levelSkill[stats().ELV]) {
				return;
			}
			
			// Alcanzó el máximo valor del skill? (100)
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

			if (Util.Azar(1, prob) == 7) {
				skills().addSkillPoints(skill, (byte) 1);
				sendMessage("¡Has mejorado tu skill " + skill + " en un punto!. Ahora tienes " 
							+ skills().get(skill) + " pts.", FontType.FONTTYPE_INFO);
				
				stats().addExp((byte) EXPERIENCE_BY_LEVEL_UP);
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

	public void envenenar() {
		flags().Envenenado = true;
		sendMessage("¡¡La criatura te ha envenenado!!", FONTTYPE_FIGHT);
	}

	public void modificarSkills(String s) {
		// TODO
		// Comando "SKSE" Modificar skills
		/*
		StringTokenizer st = new StringTokenizer(s, ",");
		byte skills[] = new byte[Skill.values().length];
		for (int i = 1; i <= Skill.MAX_SKILLS; i++) {
			skills[i] = Byte.parseByte(st.nextToken());
		}
		// Prevenir el hackeo de los skills
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		int total = 0;
		for (int i = 1; i <= Skill.MAX_SKILLS; i++) {
			if (skills[i] < 0) {
				Log.logHack(m_nick + " IP:" + m_ip + " trató de hackear los skills (skill negativo).");
				stats().setSkillPoints(0); // Pierde los skills points
				// acumulados por tramposo!
				doSALIR(); // Cerrar la conexion como castigo.
				return;
			}
			total += skills[i];
		}
		if (total > stats().SkillPts) {
			Log.logHack(m_nick + " IP:" + m_ip + " trató de hackear los skills (skills > SkillPts).");
			stats().setSkillPoints(0); // Pierde los skills points
			// acumulados
			// por tramposo!
			doSALIR(); // Cerrar la conexion como castigo.
			return;
		}
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		stats().subirSkills(skills);
		*/
	}

	public void checkUserLevel() {
		while (stats().Exp >= stats().ELU) {
			boolean wasNewbie = isNewbie();

			// ¿Alcanzo el maximo nivel?
			if (stats().ELV == STAT_MAXELV) {
				stats().Exp = 0;
				stats().ELU = 0;
				return;
			}
			
			// Se acumuló la experiencia necesaria para subir de nivel
			sendWave(SOUND_NIVEL);
			sendMessage("¡Has subido de nivel!", FontType.FONTTYPE_INFO);
			
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
			
			clazz().subirEstads(this);
			sendSkills();
			
			if (!isNewbie() && wasNewbie) {
				userInv().quitarObjsNewbie();
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
			tmp = (skills().get(Skill.SKILL_Tacticas) + stats().attr().get(Attribute.AGILIDAD))
					* this.clazz().modificadorEvasion();
		} else if (skills().get(Skill.SKILL_Tacticas) < 91) {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (2 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorEvasion();
		} else {
			tmp = (skills().get(Skill.SKILL_Tacticas) + (3 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorEvasion();
		}
		return (tmp + (2.5 * Util.Max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueArma() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Armas) < 31) {
			tmp = skills().get(Skill.SKILL_Armas) * this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Armas) < 61) {
			tmp = ((skills().get(Skill.SKILL_Armas) + stats().attr().get(Attribute.AGILIDAD))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Armas) < 91) {
			tmp = ((skills().get(Skill.SKILL_Armas) + (2 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueArmas());
		} else {
			tmp = ((skills().get(Skill.SKILL_Armas) + (3 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueArmas());
		}
		return tmp + (2.5 * Util.Max(stats().ELV - 12, 0));
	}

	public double poderAtaqueProyectil() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Proyectiles) < 31) {
			tmp = (skills().get(Skill.SKILL_Proyectiles) * this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 61) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + stats().attr().get(Attribute.AGILIDAD))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else if (skills().get(Skill.SKILL_Proyectiles) < 91) {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (2 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		} else {
			tmp = ((skills().get(Skill.SKILL_Proyectiles) + (3 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueProyectiles());
		}
		return (tmp + (2.5 * Util.Max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueWresterling() {
		double tmp = 0;
		if (skills().get(Skill.SKILL_Wresterling) < 31) {
			tmp = (skills().get(Skill.SKILL_Wresterling) * this.clazz().modificadorPoderAtaqueArmas());
		} else if (skills().get(Skill.SKILL_Wresterling) < 61) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + stats().attr().get(Attribute.AGILIDAD))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else if (skills().get(Skill.SKILL_Wresterling) < 91) {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (2 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueArmas();
		} else {
			tmp = (skills().get(Skill.SKILL_Wresterling) + (3 * stats().attr().get(Attribute.AGILIDAD)))
					* this.clazz().modificadorPoderAtaqueArmas();
		}
		return tmp + (2.5 * Util.Max(stats().ELV - 12, 0));
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
		double probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtaque - npc.getPoderEvasion()) * 0.4)));
		boolean huboImpacto = (Util.Azar(1, 100) <= probExito);
		if (huboImpacto) {
			if (this.userInv.tieneArmaEquipada()) {
				if (this.userInv.getArma().esProyectil()) {
					subirSkill(Skill.SKILL_Proyectiles);
				} else {
					subirSkill(Skill.SKILL_Armas);
				}
			} else {
				subirSkill(Skill.SKILL_Wresterling);
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
		double probExito = Util.Max(10, Util.Min(90, 50 + ((npcPoderAtaque - userEvasion) * 0.4)));
		boolean impacto = (Util.Azar(1, 100) <= probExito);
		// ¿El usuario esta usando un escudo ???
		if (!impacto && this.userInv.tieneEscudoEquipado()) {
			double probRechazo = Util.Max(10, Util.Min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
			boolean rechazo = (Util.Azar(1, 100) <= probRechazo);
			if (rechazo) {
				// Se rechazo el ataque con el escudo
				sendWave(SOUND_ESCUDO);
				sendPacket(new BlockedWithShieldUserResponse());
				subirSkill(Skill.SKILL_Defensa);
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
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
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
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
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
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = this.userInv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = this.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			}
		}
		dañoUsuario = Util.Azar(stats().MinHIT, stats().MaxHIT);
		double daño = (((3 * dañoArma) + ((dañoMaxArma / 5) * Util.Max(0, (stats().attr.get(Attribute.FUERZA) - 15)))
				+ dañoUsuario) * modifClase);
		return (short) daño;
	}

	public void userDañoNpc(Npc npc) {
		int daño = calcularDaño(npc);
		// esta navegando? si es asi le sumamos el daño del barco
		if (flags().Navegando) {
			daño += Util.Azar(this.userInv.getBarco().MinHIT, this.userInv.getBarco().MaxHIT);
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
				subirSkill(Skill.SKILL_Apuñalar);
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
		for (int i = 1; i <= this.userInv.size(); i++) {
			if (this.userInv.getObjeto(i).objid == objIdx) {
				cant_quitar = this.userInv.getObjeto(i).cant < cant ? this.userInv.getObjeto(i).cant : cant;
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
		int daño = Util.Azar(npc.stats().MinHIT, npc.stats().MaxHIT);
		int defbarco = 0;
		if (isSailing()) {
			ObjectInfo barco = this.userInv.getBarco();
			defbarco = Util.Azar(barco.MinDef, barco.MaxDef);
		}
		byte lugar = (byte)Util.Azar(1, 6);
		switch (lugar) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (this.userInv.tieneCascoEquipado()) {
				ObjectInfo casco = this.userInv.getCasco();
				int absorbido = Util.Azar(casco.MinDef, casco.MaxDef);
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
				int absorbido = Util.Azar(armadura.MinDef, armadura.MaxDef);
				absorbido += defbarco;
				daño -= absorbido;
				if (daño < 1) {
					daño = 1;
				}
			}
			break;
		}
		sendPacket(new NPCHitUserResponse(lugar, (short)daño));
		if (flags().Privilegios == 0) {
			stats().MinHP -= daño;
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
			sendMessage("Estás muy lejos para disparar.", FONTTYPE_FIGHT);
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
			userDañoNpc(npc);
		} else {
			sendWave(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
	}

	public void safeToggle() {
		flags().Seguro = !flags().Seguro;
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

	public void doONLINE() {
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
				stats().quitarStamina(Util.Azar(1, 10));
			} else {
				if (gender() == UserGender.GENERO_HOMBRE) {
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
			Player attackedPlayer = mapa.getPlayer(attackPos.x, attackPos.y);
			// Look for user
			if (attackedPlayer != null) {
				usuarioAtacaUsuario(attackedPlayer);
				sendUpdateUserStats();
				attackedPlayer.sendUpdateUserStats();
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
			flags().Trabajando = false;
		} else {
			log.info("NO PUEDE ATACAR");
		}
	}

	public boolean usuarioImpacto(Player victima) {
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
			probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtaque - userPoderEvasion) * 0.4)));
		} else {
			poderAtaque = poderAtaqueWresterling();
			probExito = Util.Max(10, Util.Min(90, 50 + ((poderAtaque - userPoderEvasion) * 0.4)));
		}
		boolean huboImpacto = (Util.Azar(1, 100) <= probExito);
		// el usuario esta usando un escudo ???
		if (victima.userInv.tieneEscudoEquipado()) {
			// Fallo ???
			if (!huboImpacto) {
				double probRechazo = Util.Max(10, Util.Min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
				boolean huboRechazo = (Util.Azar(1, 100) <= probRechazo);
				if (huboRechazo) {
					// Se rechazo el ataque con el escudo!
					sendWave(SOUND_ESCUDO);
					sendPacket(new BlockedWithShieldOtherResponse());
					victima.sendPacket(new BlockedWithShieldUserResponse());
					victima.subirSkill(Skill.SKILL_Defensa);
				}
			}
		}
		if (huboImpacto) {
			if (arma != null) {
				if (!proyectil) {
					subirSkill(Skill.SKILL_Armas);
				} else {
					subirSkill(Skill.SKILL_Proyectiles);
				}
			} else {
				subirSkill(Skill.SKILL_Wresterling);
			}
		}
		return huboImpacto;
	}

	public void usuarioAtacaUsuario(Player victima) {
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

	public void userDañoUser(Player victima) {
		short damage = calcularDaño(null);
		short shipDefense = 0;
		envenenarUsuario(victima); // revisar... FIXME
		if (isSailing()) {
			ObjectInfo barco = this.userInv.getBarco();
			damage += Util.Azar(barco.MinHIT, barco.MaxHIT);
		}
		if (victima.isSailing()) {
			ObjectInfo barco = victima.userInv.getBarco();
			shipDefense = (short) Util.Azar(barco.MinDef, barco.MaxDef);
		}
		byte target = (byte) Util.Azar(1, 6);
		switch (target) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (victima.userInv.tieneCascoEquipado()) {
				ObjectInfo casco = victima.userInv.getCasco();
				int absorbido = Util.Azar(casco.MinDef, casco.MaxDef);
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
				int absorbido = Util.Azar(escudo.MinDef, escudo.MaxDef);
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
				subirSkill(Skill.SKILL_Armas);
			} else {
				// sino tal vez lucha libre
				subirSkill(Skill.SKILL_Wresterling);
			}
			subirSkill(Skill.SKILL_Tacticas);
			// Trata de apuñalar por la espalda al enemigo
			if (puedeApuñalar()) {
				apuñalar(victima, damage);
				subirSkill(Skill.SKILL_Apuñalar);
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

	public void actStats(Player victima) {
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
/*

 */
	
	public void contarMuerte(Player usuarioMuerto) {
		if (usuarioMuerto.isNewbie()) {
			return;
		}
		if (duelStatus(usuarioMuerto) == DuelStatus.DUEL_ALLOWED) {
			return;
		}

		// TODO move this to UserFaction
		if (usuarioMuerto.isCriminal()) {
			if (!flags().LastCrimMatado.equalsIgnoreCase(usuarioMuerto.userName)) {
				flags().LastCrimMatado = usuarioMuerto.userName;
				this.faction.CriminalesMatados++;
			}
			if (this.faction.CriminalesMatados > MAXUSERMATADOS) {
				this.faction.CriminalesMatados = 0;
				this.faction.RecompensasReal = 0;
			}

		} else {
			if (!flags().LastCiudMatado.equalsIgnoreCase(usuarioMuerto.userName)) {
				flags().LastCiudMatado = usuarioMuerto.userName;
				this.faction.CiudadanosMatados++;
			}
			if (this.faction.CiudadanosMatados > MAXUSERMATADOS) {
				this.faction.CiudadanosMatados = 0;
				this.faction.RecompensasCaos = 0;
			}
		}
	}

	public void usuarioAtacadoPorUsuario(Player victima) {
		if (duelStatus(victima) == DuelStatus.DUEL_ALLOWED) {
			return;
		}
		if (!getGuildInfo().esMiembroClan() || !victima.getGuildInfo().esMiembroClan()) {
			if (!isCriminal() && !victima.isCriminal()) {
				volverCriminal();
			}
		} else { // Ambos están en clan
			if (getGuild() != null && !getGuild().isEnemy(victima.getGuildInfo().getGuildName())) {
				// Están en clanes enemigos
				if (!isCriminal() && !victima.isCriminal()) {
					volverCriminal();
				}
			}
			// TODO Revisar: ¿puede un cuidadano atacar a otro ciudadano, cuandoestán en clanes enemigos?
		}

		if (victima.isCriminal()) {
			this.reputation.incNoble(vlNoble);
		} else {
			this.reputation.incBandido(vlAsalto);
		}

		allPetsAttackUser(victima);
		victima.allPetsAttackUser(this);
	}

	public void allPetsAttackUser(Player objetivo) {
		getUserPets().getPets().forEach(pet -> {
			pet.attackedByUserName(objetivo.getNick());
			pet.defenderse();
		});
	}
	
	public boolean isAtDuelArena() {
        Map map = server.getMap(pos().map);
        return map.getTrigger(pos().x, pos().y) == Trigger.TRIGGER_ARENA_DUELOS;
	}

	public boolean puedeAtacar(Player victima) {
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
		if (victima.isGM()) {
			sendMessage("¡¡No puedes atacar a los administradores del juego!!", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isAlive()) {
			sendMessage("No puedes atacar a un espíritu.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (flags().Seguro) {
			if (!victima.isCriminal()) {
				sendMessage(
						"No puedes atacar ciudadanos, para hacerlo debes desactivar el seguro apretando la tecla <S>.",
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
		return (Util.Azar(1, rango) == 3);
	}

	public void apuñalar(Npc victimaNPC, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 2;
			victimaNPC.stats().MinHP -= daño;
			sendMessage("Has apuñalado a la criatura por " + daño, FONTTYPE_FIGHT);
			subirSkill(Skill.SKILL_Apuñalar);
			victimaNPC.calcularDarExp(this, daño);
		} else {
			sendMessage("¡No has logrado apuñalar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void apuñalar(Player victimaUsuario, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 1.5;
			victimaUsuario.stats().MinHP -= daño;
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
			volverCriminal();
			npc.defenderse();
		} else {
			// Reputacion
			if (npc.stats().alineacion == 0 && npc.getPetUserOwner() == null) {
				if (npc.npcType() == NpcType.NPCTYPE_GUARDIAS_REAL) {
					volverCriminal();
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

	public void connectNewUser(String userName, String password, byte race, byte gender, byte clazz, String email, byte homeland) {
		// Validar los datos recibidos :-)
		this.userName = userName;
		if (!this.server.manager().isValidUserName(this.userName)) {
			sendError("Los nombres de los personajes deben pertencer a la fantasia, el nombre indicado es invalido.");
			return;
		}
		if (!Util.asciiValidos(this.userName)) {
			sendError("Nombre invalido.");
			return;
		}
		if (userExists()) {
			sendError("Ya existe el personaje.");
			return;
		}
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
		this.homeland = homeland;
		// FIXME
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		// if (!atributosValidos()) {
		// sendError("Atributos invalidos.");
		// return;
		// }
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		modifyAttributesByRace();
		this.skills().freeSkillPts = 10;

		this.password = password;
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

		// ???????????????? INVENTARIO ¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿
		this.userInv.clear();
		this.userInv.setObjeto(1, new InventoryObject(MANZANA_ROJA_NEWBIES, 100, false));
		this.userInv.setObjeto(2, new InventoryObject(BOTELLA_AGUA_NEWBIES, 100, false));
		this.userInv.setArma(3, new InventoryObject(DAGA_NEWBIES, 1, true));

		switch (race()) {
		case RAZA_HUMANO:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_1, 1, true));
			break;
		case RAZA_ELFO:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_2, 1, true));
			break;
		case RAZA_DROW:
			this.userInv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_3, 1, true));
			break;
		case RAZA_ENANO:
			this.userInv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		case RAZA_GNOMO:
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
		// TODO revisar para qué y cuándo se usa esto... está raro como chequeo de consistencia -gorlok
		return Util.existeArchivo(getPjFile(this.userName));
	}

	public short indiceCiudad(String ciudad) {
		// TODO esto no tiene porque estar en esta clase...
		for (short i = 0; i < Constants.CIUDADES_NOMBRES.length; i++) {
			if (Constants.CIUDADES_NOMBRES[i].equalsIgnoreCase(ciudad)) {
				return i;
			}
		}
		return 0;
	}

	public void modifyAttributesByRace() {
		stats().attr().set(Attribute.FUERZA, 		stats().attr().get(Attribute.FUERZA) 		+ race().modificadorFuerza());
		stats().attr().set(Attribute.AGILIDAD, 		stats().attr().get(Attribute.AGILIDAD) 		+ race().modificadorAgilidad());
		stats().attr().set(Attribute.INTELIGENCIA, 	stats().attr().get(Attribute.INTELIGENCIA) 	+ race().modificadorInteligencia());
		stats().attr().set(Attribute.CARISMA, 		stats().attr().get(Attribute.CARISMA) 		+ race().modificadorCarisma());
		stats().attr().set(Attribute.CONSTITUCION, 	stats().attr().get(Attribute.CONSTITUCION) 	+ race().modificadorConstitucion());
	}

	public void sendError(String msg) {
		log.warn("ERROR: " + msg);
		sendPacket(new ErrorMsgResponse(msg));
	}

	public void connectUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
		try {
			// ¿Existe el personaje?
			if (!userExists()) {
				sendError("El personaje no existe. Compruebe el nombre de usuario.");
				return;
			}

			// Reseteamos los FLAGS
			flags().Escondido = false;
			flags().TargetNpc = 0;
			flags().TargetObj = 0;
			flags().TargetUser = 0;
			this.infoChar.fx = 0;

			if (this.server.isPlayerAlreadyConnected(this)) {
				sendError("Perdon, pero ya esta conectado.");
				return;
			}

			// ¿Es el password valido?
			if (!this.password.equals(this.userStorage.loadPasswordFromStorage(this.userName))) {
				sendError("Clave incorrecta.");
				return;
			}
			this.userStorage.loadUserFromStorage();

			if (this.server.manager().isGod(this.userName)) {
				flags().Privilegios = 3;
				Log.logGM(this.userName, "El GM-DIOS se conectó desde la ip=" + this.ip);
			} else if (this.server.manager().isDemiGod(this.userName)) {
				flags().Privilegios = 2;
				Log.logGM(this.userName, "El GM-SEMIDIOS se conectó desde la ip=" + this.ip);
			} else if (this.server.manager().isCounsellor(this.userName)) {
				flags().Privilegios = 1;
				Log.logGM(this.userName, "El GM-CONSEJERO se conectó desde la ip=" + this.ip);
			} else {
				// Usuario no privilegiado.
				flags().Privilegios = 0;
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

			sendInventoryToUser();
			spells().sendSpells();
			
			if (isParalized() || flags().Inmovilizado) {
				sendPacket(new ParalizeOKResponse());
			}
			
			if (flags().Estupidez) {
				sendPacket(new DumbResponse());
			}
			
			if (pos().map == 0) {
				// Posicion de comienzo
				if (this.homeland == CIUDAD_NIX) {
					setPos(this.server.getCiudadPos(CIUDAD_NIX).copy());
				} else if (this.homeland == CIUDAD_ULLA) {
					setPos(this.server.getCiudadPos(CIUDAD_ULLA).copy());
				} else if (this.homeland == CIUDAD_BANDER) {
					setPos(this.server.getCiudadPos(CIUDAD_BANDER).copy());
				} else if (this.homeland == CIUDAD_LINDOS) {
					setPos(this.server.getCiudadPos(CIUDAD_LINDOS).copy());
				} else {
					this.homeland = CIUDAD_ULLA;
					setPos(this.server.getCiudadPos(CIUDAD_ULLA).copy());
				}
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
			
			server.getMotd().sendMOTD(this);
			

			// FIXME respawn pets
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

	public void doServerVersion() {
		sendMessage("-=<>[ Bienvenido a Argentun Online ]<>=-", FontType.FONTTYPE_SERVER);
		sendMessage("-=<>[    Powered by Gorlok's AO    ]<>=-", FontType.FONTTYPE_SERVER);
		sendMessage("Server version: " + VERSION, FontType.FONTTYPE_SERVER);
	}

	public void doDebug(String s) {
		if ("".equals(s)) {
			sendMessage("DEBUG: " + (this.server.isShowDebug() ? "ON" : "OFF"), FontType.FONTTYPE_INFO);
		} else if (s.equalsIgnoreCase("ON")) {
			this.server.setShowDebug(true);
			sendMessage("DEBUG: activado", FontType.FONTTYPE_INFO);
		} else if (s.equalsIgnoreCase("OFF")) {
			this.server.setShowDebug(false);
			sendMessage("DEBUG: desactivado", FontType.FONTTYPE_INFO);
		}
	}

	public void efectoCegueEstu() {
		if (this.counters.Ceguera > 0) {
			this.counters.Ceguera--;
		} else {
			if (flags().Ceguera) {
				flags().Ceguera = false;
				sendPacket(new BlindNoMoreResponse());
			} else {
				flags().Estupidez = false;
				sendPacket(new DumbNoMoreResponse());
			}
		}
	}

	public void efectoFrio() {
		if (this.counters.Frio < IntervaloFrio) {
			this.counters.Frio++;
		} else {
			Map mapa = this.server.getMap(pos().map);
			if (mapa.getTerrain() == Terrain.SNOW) {
				sendMessage("¡¡Estas muriendo de frio, abrígate o morirás!!.", FontType.FONTTYPE_INFO);
				int modifi = Util.porcentaje(stats().MaxHP, 5);
				stats().MinHP -= modifi;

				if (stats().MinHP < 1) {
					sendMessage("¡¡Has muerto de frio!!.", FontType.FONTTYPE_INFO);
					stats().MinHP = 0;
					userDie();
				}
			} else {
				if (!stats().isTooTired()) { // Verificación agregada por gorlok
					int modifi = Util.porcentaje(stats().maxStamina, 5);
					stats().quitarStamina(modifi);
					sendMessage("¡¡Has perdido stamina, si no te abrigas rápido la perderás toda!!.", FontType.FONTTYPE_INFO);
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
				int mashit = Util.Azar(2, Util.porcentaje(stats().maxStamina, 5));
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
		return (Util.Azar(1, rango) == 1);
	}

	public void meditar() {
		long tActual = Util.millis();
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
			int cant = Util.porcentaje(stats().maxMana, 3);
			stats().aumentarMana(cant);
			sendMessage("¡Has recuperado " + cant + " puntos de mana!", FontType.FONTTYPE_INFO);
			sendUpdateUserStats();
			subirSkill(Skill.SKILL_Meditar);
		}
	}

	private boolean efectoVeneno() {
		if (this.counters.Veneno < IntervaloVeneno) {
			this.counters.Veneno++;
		} else {
			sendMessage("Estas envenenado, si no te curas moriras.", FontType.FONTTYPE_VENENO);
			this.counters.Veneno = 0;
			stats().MinHP -= Util.Azar(1, 5);
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
			volverseVisible();
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
			stats().attr().get(Attribute.FUERZA),
			stats().attr().get(Attribute.AGILIDAD),
			stats().attr().get(Attribute.INTELIGENCIA),
			stats().attr().get(Attribute.CARISMA),
			stats().attr().get(Attribute.CONSTITUCION)));
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
					// Desnudo no sube energía.
					return false;
				}
				int massta = Util.Azar(1, Util.porcentaje(stats().maxStamina, 5));
				stats().aumentarStamina(massta);
				sendUpdateUserStats();
				return true;
			}
		}
		return false;
	}

	private void checkSummonTimeout() {
		getUserPets().getPets().forEach(pet -> {
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
				efectoParalisisUser();
			}
			if (flags().Ceguera || flags().Estupidez) {
				efectoCegueEstu();
			}
			if (isAlive()) {
				if (flags().Desnudo && flags().Privilegios == 0) {
					efectoFrio();
				}
				if (flags().Meditando) {
					meditar();
				}
				if (flags().Envenenado && flags().Privilegios == 0) {
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
				if (stats().eaten <= 0 && !isGM()) {
					sendMessage("¡¡Has muerto de hambre!!.", FontType.FONTTYPE_INFO);
					stats().eaten = 0;
					userDie();
				}
				// Verificar muerte de sed
				if (stats().drinked <= 0 && !isGM()) {
					sendMessage("¡¡Has muerto de sed!!.", FontType.FONTTYPE_INFO);
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
			return " <Ejercito real> <" + this.faction.tituloReal() + ">";
		} else if (this.faction.FuerzasCaos) {
			return " <Fuerzas del caos> <" + this.faction.tituloCaos() + ">";
		}
		return "";
	}

	public String getTagsDesc() {
		var msg = new StringBuilder();
		if (showName) {
			msg.append(getNick());
			if (isNewbie()) {
				msg.append(" <NEWBIE>");
			}
			msg.append(getTituloFaccion());
			if (getGuildInfo().esMiembroClan()) {
				msg.append(" <" + this.guildUser.getGuildName() + ">");
			}
			if (this.description.length() > 0) {
				msg.append(" - " + this.description);
			}
			if (isGod()) {
				msg.append(" <GAME MASTER>");
			} else if (isDemiGod()) {
				msg.append(" <SEMIDIOS>");
			} else if (isCounselor()) {
				msg.append(" <CONSEJERO>");
			} else if (isCriminal()) {
				msg.append(" <CRIMINAL>");
			} else {
				msg.append(" <CIUDADANO>");
			}
		}
		return msg.toString();
	}

	public FontType getTagColor() {
		if (isGod()) {
			return FontType.FONTTYPE_DIOS;
		}
		if (isDemiGod()) {
			return FontType.FONTTYPE_GM;
		}
		if (isCounselor()) {
			return FontType.FONTTYPE_CONSEJO;
		}
		if (isCriminal()) {
			return FontType.FONTTYPE_CITIZEN;
		}
		return FontType.FONTTYPE_CITIZEN;
	}

	public boolean genderCanUseItem(short objid) {
		ObjectInfo infoObj = findObj(objid);
		if (infoObj.esParaMujeres()) {
			return (this.gender == UserGender.GENERO_MUJER);
		} else if (infoObj.esParaHombres()) {
			return (this.gender == UserGender.GENERO_HOMBRE);
		} else {
			return true;
		}
	}

	public boolean checkRazaUsaRopa(short objid) {
		// Verifica si la raza puede usar esta ropa
		boolean canUse = false;

		var infoObj = findObj(objid);
		switch (this.race) {
			case RAZA_HUMANO:
			case RAZA_ELFO:
			case RAZA_DROW:
				canUse = !infoObj.esParaRazaEnana();
				break;

			case RAZA_ENANO:
			case RAZA_GNOMO:
				canUse = infoObj.esParaRazaEnana();
				break;
		}

		// La ropa para Drows, sólo la pueden usar los Drows
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
		for (int i = 1; i <= this.userInv.size(); i++) {
			if (this.userInv.getObjeto(i).objid == objid) {
				total += this.userInv.getObjeto(i).cant;
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
			quitarObjetos(Leña, info.Madera);
		}
	}

	private boolean carpinteroTieneMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.Madera > 0) {
			if (!tieneObjetos(Leña, info.Madera)) {
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
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Herreria);
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
			sendMessage("¡Has construido el objeto!", FontType.FONTTYPE_INFO);
			if (this.userInv.agregarItem(objid, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Carpinteria);
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
				new UpdateTagAndStatusResponse(getId(), isCriminal() ? (byte)1 : (byte)0, getTagsDesc()));
        
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
				sendMessage("Cerrando... Se cerrará el juego en " + IntervaloCerrarConexion + " segundos...",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	private void sendUserBovedaTxt(Player usuario) {
		sendMessage("El usuario " + usuario.getNick(), FontType.FONTTYPE_INFOBOLD);
		sendMessage("Tiene " + usuario.bankInv.getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		InventoryObject obj;
		ObjectInfo iobj;
		for (int i = 1; i <= usuario.bankInv.size(); i++) {
			if ((obj = usuario.bankInv.getObjeto(i)) != null) {
				iobj = findObj(obj.objid);
				sendMessage(" Objeto " + i + ": " + iobj.Nombre + " Cantidad:" + obj.cant, FontType.FONTTYPE_INFO);
			}
		}
	}

	private void sendUserBovedaTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			sendMessage("Pj: " + pj, FontType.FONTTYPE_INFOBOLD);
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				int cantObjs = ini.getInt("BancoInventory", "CantidadItems");
				sendMessage("Tiene " + cantObjs + " objetos.", FontType.FONTTYPE_INFO);
				// Lista de objetos en el banco.
				ObjectInfo iobj;
				for (int i = 0; i < MAX_BANCOINVENTORY_SLOTS; i++) {
					String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = findObj(objid);
						sendMessage(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.FONTTYPE_INFO);
					}
				}
			} catch (java.io.IOException e) {
				log.fatal("Error sendUserBovedaTxtFromChar", e);
			}
		} else {
			sendMessage("Usuario inexistente: " + pj, FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(short dir, short slot) {
		if (dir < 1 || dir > 2) {
			return;
		}
		if (slot < 1 || slot > MAX_SPELLS) {
			return;
		}

		this.spells.moveSpell(slot, dir);
	}

	private void sendUserMiniStatsTxt(Player usuario) {
		if (usuario == null) {
			return;
		}
		sendMessage("Pj: " + usuario.getNick() + " Clase: " + usuario.clazz().toString(), FontType.FONTTYPE_INFOBOLD);
		sendMessage(
				"CiudadanosMatados: " + usuario.userFaction().CiudadanosMatados +
				" CriminalesMatados: "+ usuario.userFaction().CriminalesMatados +
				" UsuariosMatados: " + usuario.stats().usuariosMatados,
				FontType.FONTTYPE_INFO);
		sendMessage("NPCsMuertos: " + usuario.stats().NPCsMuertos + " Pena: " + usuario.counters.Pena,
				FontType.FONTTYPE_INFO);
	}

	private void sendUserMiniStatsTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				sendMessage("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.FONTTYPE_INFOBOLD);
				sendMessage("CiudadanosMatados: " + ini.getLong("FACCIONES", "CiudMatados") + " CriminalesMatados: "
						+ ini.getLong("FACCIONES", "CrimMatados") + " UsuariosMatados: "
						+ ini.getInt("MUERTES", "UserMuertes"), FontType.FONTTYPE_INFO);
				sendMessage("NPCsMuertos: " + ini.getInt("MUERTES", "NpcsMuertes") + " Pena: "
						+ ini.getLong("COUNTERS", "PENA"), FontType.FONTTYPE_INFO);
				boolean ban = ini.getShort("FLAGS", "Ban") == 1;
				sendMessage("Ban: " + (ban ? "si" : "no"), FontType.FONTTYPE_INFO);
				if (ban) {
					sendMessage("Ban por: " + ini.getString("BAN", "BannedBy") + " Motivo: "
							+ ini.getString("BAN", "Reason"), FONTTYPE_FIGHT);
				}
			} catch (java.io.IOException e) {
				log.fatal("ERROR sendUserMiniStatsTxtFromChar", e);
			}
		} else {
			sendMessage("El pj no existe: " + pj, FontType.FONTTYPE_INFO);
		}
	}

	private void sendUserInvTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				sendMessage("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.FONTTYPE_INFOBOLD);
				sendMessage("Tiene " + ini.getShort("Inventory", "CantidadItems") + " objetos.", FontType.FONTTYPE_INFO);
				ObjectInfo iobj;
				for (int i = 0; i < MAX_INVENTORY_SLOTS; i++) {
					String tmp = ini.getString("Inventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = findObj(objid);
						sendMessage(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.FONTTYPE_INFO);
					}
				}
			} catch (java.io.IOException e) {
				log.fatal("ERROR sendUserInvTxtFromChar", e);
			}
		} else {
			sendMessage("El pj no existe: " + pj, FontType.FONTTYPE_INFO);
		}
	}

    enum DuelStatus {
	    DUEL_ALLOWED 	/* TRIGGER6_PERMITE  1 */,
	    DUEL_FORBIDDEN 	/* TRIGGER6_PROHIBE  2 */,
	    DUEL_MISSING 	/* TRIGGER6_AUSENTE  3 */;
    }

	public DuelStatus duelStatus(Player victima) {
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

	private void envenenarUsuario(Player victima) {
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
				if (Util.Azar(1, 100) < 60) {
					victima.flags().Envenenado = true;
					victima.sendMessage(this.userName + " te ha envenenado!!", FONTTYPE_FIGHT);
					sendMessage("Has envenenado a " + victima.getNick() + "!!", FONTTYPE_FIGHT);
				}
			}
		}
	}

	public static void changeGuildLeaderChr(String nick, boolean esLider) {
		try {
			// ¿Existe el personaje?
			if (!Util.existeArchivo(getPjFile(nick))) {
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
			// ¿Existe el personaje?
			if (!Util.existeArchivo(getPjFile(nick))) {
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

	public void doBalance() {
		// Comando /BALANCE
		// ¿Esta el user muerto? Si es asi no puede comerciar
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
			sendTalk(Color.COLOR_BLANCO, "No entiendo de eso. Habla con alguien más.", getId());
		}
	}
	
}