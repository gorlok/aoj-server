/**
 * Player.java
 * 
 * Created 14/sep/2003.
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

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;
import static org.ArgentumOnline.server.util.Color.COLOR_CYAN;
import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.ArgentumOnline.server.anticheat.SpeedHackCheck;
import org.ArgentumOnline.server.anticheat.SpeedHackException;
import org.ArgentumOnline.server.classes.Clazz;
import org.ArgentumOnline.server.guilds.Guild;
import org.ArgentumOnline.server.guilds.GuildUser;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.inventory.UserInventory;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapCell;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcCashier;
import org.ArgentumOnline.server.npc.NpcGambler;
import org.ArgentumOnline.server.npc.NpcMerchant;
import org.ArgentumOnline.server.npc.NpcTrainer;
import org.ArgentumOnline.server.protocol.AttributesResponse;
import org.ArgentumOnline.server.protocol.BankEndResponse;
import org.ArgentumOnline.server.protocol.BankInitResponse;
import org.ArgentumOnline.server.protocol.BankOKResponse;
import org.ArgentumOnline.server.protocol.BlindNoMoreResponse;
import org.ArgentumOnline.server.protocol.BlockPositionResponse;
import org.ArgentumOnline.server.protocol.ChangeBankSlotResponse;
import org.ArgentumOnline.server.protocol.ChangeInventorySlotResponse;
import org.ArgentumOnline.server.protocol.ChangeMapResponse;
import org.ArgentumOnline.server.protocol.ChangeUserTradeSlotResponse;
import org.ArgentumOnline.server.protocol.CharacterCreateResponse;
import org.ArgentumOnline.server.protocol.ChatOverHeadResponse;
import org.ArgentumOnline.server.protocol.CommerceEndResponse;
import org.ArgentumOnline.server.protocol.CommerceInitResponse;
import org.ArgentumOnline.server.protocol.ConsoleMsgResponse;
import org.ArgentumOnline.server.protocol.DiceRollResponse;
import org.ArgentumOnline.server.protocol.DisconnectResponse;
import org.ArgentumOnline.server.protocol.DumbNoMoreResponse;
import org.ArgentumOnline.server.protocol.ErrorMsgResponse;
import org.ArgentumOnline.server.protocol.FameResponse;
import org.ArgentumOnline.server.protocol.LevelUpResponse;
import org.ArgentumOnline.server.protocol.LoggedMessageResponse;
import org.ArgentumOnline.server.protocol.MeditateToggleResponse;
import org.ArgentumOnline.server.protocol.MiniStatsResponse;
import org.ArgentumOnline.server.protocol.NPCHitUserResponse;
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
import org.ArgentumOnline.server.protocol.UpdateHungerAndThirstResponse;
import org.ArgentumOnline.server.protocol.UpdateTagAndStatusResponse;
import org.ArgentumOnline.server.protocol.UpdateUserStatsResponse;
import org.ArgentumOnline.server.protocol.UserCharIndexInServerResponse;
import org.ArgentumOnline.server.protocol.UserHitNPCResponse;
import org.ArgentumOnline.server.protocol.UserIndexInServerResponse;
import org.ArgentumOnline.server.protocol.UserSwingResponse;
import org.ArgentumOnline.server.protocol.WorkRequestTargetResponse;
import org.ArgentumOnline.server.quest.UserQuest;
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

	Channel channel = null;

	String m_nick = "";
	String m_password = ""; // FIXME SEGURIDAD

	String m_desc = ""; // Descripcion

	Clazz clazz = Clazz.Hunter;

	UserRace race = UserRace.RAZA_HUMANO;
	UserGender gender = UserGender.GENERO_HOMBRE;
	
	String m_email = "";
	short m_hogar = CIUDAD_ULLA;

	int m_cantHechizos = 0;
	UserSpells m_spells;
	
	private UserPets userPets = new UserPets();
	
	private CharInfo mimetizadoChar = new CharInfo(); // FIXME

	// FIXME network
	long NumeroPaquetesPorMiliSec = 0;
	long BytesTransmitidosUser = 0;
	long BytesTransmitidosSvr = 0;

	GuildUser guildUser = new GuildUser(this);

	// FIXME security/anticheat
	int m_prevCRC = 0;
	long PacketNumber = 0;
	long RandKey = 0;

	boolean m_saliendo = false;

	// FIXME GM 
	String m_ip = "";
	String m_banned_by = "";
	String m_banned_reason = "";

	UserFlags m_flags = new UserFlags();

	UserStats m_estads = new UserStats();

	Reputation m_reputacion = new Reputation();

	UserFaction m_faccion;

	UserCounters m_counters = new UserCounters();

	public UserTrade m_comUsu;
	
	UserArea userArea = new UserArea();
	
	UserStorage userStorage;

	public UserQuest m_quest;

	UserInventory m_inv;
	Inventory m_bancoInv; 

	SpeedHackCheck speedHackMover = new SpeedHackCheck("SpeedHack de mover");

	GameServer server;
	
	public Player(Channel channel, GameServer aoserver) {
		init(aoserver);
		
		this.m_comUsu = new UserTrade(this);

		this.channel = channel;
		java.net.InetSocketAddress addr = (java.net.InetSocketAddress) channel.remoteAddress();
		if (addr != null) {
			this.m_ip = addr.getAddress().getHostAddress();
			log.info(this.m_nick + " conectado desde " + this.m_ip);
		}
	}
	
	public void closeConnection() {
		if (this.channel != null) {
			this.channel.close();
		}
	}

	private void init(GameServer aoserver) {
		this.userStorage = new UserStorage(server, this);
		this.server = aoserver;
		
		this.setId(server.nextId());
		this.m_spells = new UserSpells(server, this);
		this.m_quest = new UserQuest(this.server);
		this.m_inv = new UserInventory(this.server, this, MAX_USER_INVENTORY_SLOTS);
		this.m_bancoInv = new Inventory(this.server, MAX_BANCOINVENTORY_SLOTS);
		this.m_faccion = new UserFaction(this.server, this);
	}
	
	public CharInfo mimetizeChar() {
		return mimetizadoChar;
	}
	
	public GuildUser getGuildInfo() {
		return this.guildUser;
	}
	
	public Guild getGuild() {
		return server.getGuildMngr().getGuild(this.guildUser.m_guildName);
	}
	
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
	
	public Inventory getBankInventory() {
		return m_bancoInv;
	}
	
	public UserArea getUserArea() {
		return userArea;
	}
	
	public UserPets getUserPets() {
		return this.userPets;
	}
	
	public GuildUser guildInfo() {
		return guildUser;
	}

	public int nextCRC() {
		// FIXME
		return (m_prevCRC = m_prevCRC % CRCKEY);
	}

	public UserRace race() {
		return this.race;
	}
	
	public UserGender gender() {
		return this.gender;
	}

	public String getNick() {
		return m_nick;
	}
	
	public boolean hasNick() {
		return getNick() != null && !getNick().isEmpty();
	}

	public Clazz getClazz() {
		return clazz;
	}

	public UserQuest getQuest() {
		return m_quest;
	}

	public void nextQuest() {
		m_quest.m_nroQuest++;
		m_quest.m_enQuest = true;
	}

	public boolean isCriminal() {
		return m_reputacion.esCriminal();
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
		return m_estads;
	}

	public Reputation reputation() {
		return m_reputacion;
	}

	public UserFlags flags() {
		return m_flags;
	}

	public UserCounters counters() {
		return m_counters;
	}

	@Override
	public String toString() {
		return "Player(id=" + getId() + ",nick=" + m_nick + ")";
	}

	public UserFaction userFaction() {
		return m_faccion;
	}

	public UserInventory userInv() {
		return m_inv;
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
		if (m_saliendo) {
			return;
		}
		try {
			log.debug(">>" + m_nick + ">> " + packet.id());
			Gson gson = new Gson();
			System.out.println(">>" + m_nick + ">> " + packet.id() + " " + gson.toJson(packet)); // FIXME remove this
			this.channel.writeAndFlush(packet);
		} catch (Exception e) {
			e.printStackTrace();
			doSALIR();
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
		enviarMensaje("Comando deshabilitado o sin efecto en AOJ.", FontType.FONTTYPE_INFO);
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

	public void doPonerMensajeForo(String titulo, String texto) {
		// Comando DEMSG
		if (flags().TargetObj == 0) {
			return;
		}
		ObjectInfo iobj = findObj(flags().TargetObj);
		if (iobj.esForo()) {
			server.getForumManager().ponerMensajeForo(iobj.ForoID, titulo, texto);
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
		if (!npc.esNoble()) {
			hablar(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (pos().distance(npc.pos()) > 4) {
			hablar(COLOR_BLANCO, "Jeje, acércate o no podré escucharte. ¡Estás demasiado lejos!", npc.getId());
			return;
		}
		if (!npc.esFaccion()) {
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
		if (!npc.esNoble()) {
			hablar(COLOR_BLANCO, "Lo siento, no puedo ayudarte. Debes buscar a alguien de la armada Real o del Caos.",
					npc.getId());
			return;
		}
		if (!checkAlive("¡¡Estás muerto!! Busca un sacerdote y no me hagas perder el tiempo!")) {
			return;
		}
		if (!npc.esFaccion()) {
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
				(int) m_faccion.CiudadanosMatados, 
				(int) m_faccion.CriminalesMatados, 
				(int) stats().usuariosMatados, 
				(short) stats().NPCsMuertos, 
				clazz.id(), 
				(int) m_counters.Pena));

		sendUserAttributes();
		sendSkills();
		sendFame();
	}

	public boolean isLogged() {
		return flags().UserLogged;
	}

	public String getIP() {
		return m_ip;
	}

	public void doSeguir() {
		// Comando /SEGUIR
		if (flags().TargetNpc > 0) {
			Npc npc = server.npcById(flags().TargetNpc);
			npc.seguirUsuario(m_nick);
		}
	}

	public void doInfoUsuario(String s) {
		// INFO DE USER
		// Comando /INFO usuario
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(m_nick, "/INFO " + s);
		sendUserStatsTxt(usuario);
	}

	private void sendUserStatsTxt(Player usuario) {
		enviarMensaje("Estadisticas de: " + usuario.m_nick, FontType.FONTTYPE_INFOBOLD);
		enviarMensaje("Nivel: " + usuario.stats().ELV + "  EXP: " + usuario.stats().Exp + "/" + usuario.stats().ELU,
				FontType.FONTTYPE_INFO);
		enviarMensaje("Salud: " + usuario.stats().MinHP + "/" + usuario.stats().MaxHP + "  Mana: "
				+ usuario.stats().mana + "/" + usuario.stats().maxMana + "  Energia: " + usuario.stats().stamina
				+ "/" + usuario.stats().maxStamina, FontType.FONTTYPE_INFO);
		if (usuario.m_inv.tieneArmaEquipada()) {
			enviarMensaje("Menor Golpe/Mayor Golpe: " + usuario.stats().MinHIT + "/" + usuario.stats().MaxHIT + " ("
					+ m_inv.getArma().MinHIT + "/" + m_inv.getArma().MaxHIT + ")", FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("Menor Golpe/Mayor Golpe: " + usuario.stats().MinHIT + "/" + usuario.stats().MaxHIT,
					FontType.FONTTYPE_INFO);
		}
		if (usuario.m_inv.tieneArmaduraEquipada()) {
			enviarMensaje("(CUERPO) Min Def/Max Def: " + usuario.m_inv.getArmadura().MinDef + "/"
					+ usuario.m_inv.getArmadura().MaxDef, FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("(CUERPO) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		if (usuario.m_inv.tieneCascoEquipado()) {
			enviarMensaje("(CABEZA) Min Def/Max Def: " + m_inv.getCasco().MinDef + "/" + m_inv.getCasco().MaxDef,
					FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("(CABEZA) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		if (getGuildInfo().esMiembroClan()) {
			enviarMensaje("Clan: " + usuario.guildUser.m_guildName, FontType.FONTTYPE_INFO);
			if (usuario.guildUser.m_esGuildLeader) {
				if (usuario.guildUser.m_clanFundado.equals(usuario.guildUser.m_guildName)) {
					enviarMensaje("Status: Fundador/Lider", FontType.FONTTYPE_INFO);
				} else {
					enviarMensaje("Status: Lider", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("Status: " + usuario.guildUser.m_guildPoints, FontType.FONTTYPE_INFO);
			}
			enviarMensaje("User GuildPoints: " + usuario.guildUser.m_guildPoints, FontType.FONTTYPE_INFO);
		}
		enviarMensaje("Oro: " + usuario.stats().getGold() + "  Posicion: " + usuario.pos().x + "," + usuario.pos().y
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
		String[] ayuda = server.readHelp();
		for (String element : ayuda) {
			enviarMensaje(element, FontType.FONTTYPE_INFO);
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
		if (npc != null && npc.esNoble()) {
			retirarUsuarioFaccion(npc);
	}
	}

	private void retirarUsuarioFaccion(Npc npc) {
		// Se quiere retirar de la armada
		if (checkNpcNear(npc, DISTANCE_FACTION)) {
			if (m_faccion.ArmadaReal) {
				if (!npc.esFaccion()) {
					m_faccion.expulsarFaccionReal();
					hablar(COLOR_BLANCO, "Serás bienvenido a las fuerzas imperiales si deseas regresar.", npc.getId());
				} else {
					hablar(COLOR_BLANCO, "¡¡¡Sal de aquí bufón!!!", npc.getId());
				}
			} else if (m_faccion.FuerzasCaos) {
				if (npc.esFaccion()) {
					m_faccion.expulsarFaccionCaos();
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
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}

	
	public Npc getNearNpcSelected(int distance) {
		// Se asegura que el target es un npc
		if (flags().TargetNpc == 0) {
			enviarMensaje("Debes seleccionar un personaje cercano para poder interactuar.", FontType.FONTTYPE_INFO);
		} else {
			Npc npc = server.npcById(flags().TargetNpc);
			if (checkNpcNear(npc, distance)) {
				return npc;
			}
		}
		return null;
	}
	
	private boolean checkNpcNear(Npc npc, int distance) {
		if (npc.pos().distance(pos()) > distance) {
			enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
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
			enviarMensaje(message, FontType.FONTTYPE_INFO);
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
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
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
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}

	private void userDepositaItem(short slot, int cant) {
		// El usuario deposita un item
		sendUpdateUserStats();
		if (m_inv.getObjeto(slot).cant > 0 && !m_inv.getObjeto(slot).equipado) {
			if (cant > 0 && cant > m_inv.getObjeto(slot).cant) {
				cant = m_inv.getObjeto(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userDejaObj(slot, cant);
			// Actualizamos el inventario del usuario
			enviarInventario();
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
		short objid = m_inv.getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (int i = 1; i <= m_bancoInv.size(); i++) {
			if (m_bancoInv.getObjeto(i).objid == objid && m_bancoInv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio antes del slot devuelto
		if (slot_inv == 0) {
			slot_inv = m_bancoInv.getSlotLibre();
		}
		if (slot_inv == 0) {
			enviarMensaje("No tienes mas espacio en el banco!!", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		m_bancoInv.getObjeto(slot_inv).objid = objid;
		m_bancoInv.getObjeto(slot_inv).cant += cant;
		m_inv.quitarUserInvItem(slot, cant);
	}

	private void userRetiraItem(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		sendUpdateUserStats();
		if (m_bancoInv.getObjeto(slot).cant > 0) {
			if (cant > m_bancoInv.getObjeto(slot).cant) {
				cant = m_bancoInv.getObjeto(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userReciveObj(slot, cant);
			// Actualizamos el inventario del usuario
			enviarInventario();
			// Actualizamos el banco
			updateBankUserInv();
			// ventana update
			updateVentanaBanco(slot, 0);
		}

	}

	private void userReciveObj(short slot, int cant) {
		if (m_bancoInv.getObjeto(slot).cant <= 0) {
			return;
		}
		short objid = m_bancoInv.getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i).objid == objid && m_inv.getObjeto(i).cant + cant <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = m_inv.getSlotLibre();
		}
		if (slot_inv == 0) {
			enviarMensaje("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		if (m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			m_inv.getObjeto(slot_inv).objid = objid;
			m_inv.getObjeto(slot_inv).cant += cant;
			quitarBancoInvItem(slot, cant);
		} else {
			enviarMensaje("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private void quitarBancoInvItem(short slot, int cant) {
		// Quita un Obj
		m_bancoInv.getObjeto(slot).cant -= cant;
		if (m_bancoInv.getObjeto(slot).cant <= 0) {
			m_bancoInv.getObjeto(slot).objid = 0;
			m_bancoInv.getObjeto(slot).cant = 0;
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
		if (m_bancoInv.getObjeto(slot).objid > 0) {
			sendBanObj(slot, m_bancoInv.getObjeto(slot));
		} else {
			sendPacket(new ChangeBankSlotResponse((byte) slot, (short) 0, "", (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0, (int)0));
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

	public void cambiarPasswd(String s) {
		// Comando /PASSWD
		s = s.trim();
		if (s.length() < 6) {
			enviarMensaje("El password debe tener al menos 6 caracteres.", FontType.FONTTYPE_INFO);
		} else if (s.length() > 20) {
			enviarMensaje("El password puede tener hasta 20 caracteres.", FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("El password ha sido cambiado. ¡Cuídalo!", FontType.FONTTYPE_INFO);
			m_password = s;
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

	public void userEntrenaConMascota(byte petIndex) {
		// Comando ENTR
		// Entrenar con una mascota.
		if (!checkAlive()) {
			return;
		}
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = server.npcById(flags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMap(pos().map);
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

	public void commerceBuy(byte slot, short amount) {
		// Comando COMP
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = server.npcById(flags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.comercia()) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}
		if (npc.getInv().isSlotValid(slot)) {
			((NpcMerchant)npc).venderItem(this, slot, amount);
		}
	}

	public void commerceSell(byte slot, short amount) {
		// Comando VEND
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (flags().TargetNpc == 0) {
			return;
		}
		Npc npc = server.npcById(flags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.comercia()) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}

		if (npc.getInv().isSlotValid(slot)) {
			((NpcMerchant)npc).comprarItem(this, slot, amount);
		}
	}

	public void userCompraObj(Npc npc, short slot, int cant) {
		if (npc.getInv().getObjeto(slot).cant <= 0) {
			return;
		}
		short objid = npc.getInv().getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i).objid == objid && (m_inv.getObjeto(i).cant + cant) <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = m_inv.getSlotLibre();
			if (slot_inv == 0) {
				enviarMensaje("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
				return;
			}
		}
		// Mete el obj en el slot
		if (m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			// Menor que MAX_INV_OBJS
			m_inv.getObjeto(slot_inv).objid = objid;
			m_inv.getObjeto(slot_inv).cant += cant;
			ObjectInfo info = findObj(objid);
			// Le sustraemos el valor en oro del obj comprado
			double infla = (npc.getInflacion() * info.Valor) / 100.0;
			double dto = flags().Descuento;
			if (dto == 0) {
				dto = 1; // evitamos dividir por 0!
			}
			double unidad = ((info.Valor + infla) / dto);
			int monto = (int) (unidad * cant);
			stats().addGold( -monto );
			// tal vez suba el skill comerciar ;-)
			subirSkill(Skill.SKILL_Comerciar);
			if (info.objType == ObjType.Llaves) {
				Log.logVentaCasa(m_nick + " compro " + info.Nombre);
			}
			((NpcMerchant)npc).quitarNpcInvItem(slot, cant);
		} else {
			enviarMensaje("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
		}
	}

	public void updateVentanaComercio(short objIndex, int amount) {
		ObjectInfo objInfo = findObj(objIndex);
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
		int ptsComercio = stats().userSkills(Skill.SKILL_Comerciar);
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
			enviarMensaje("Ya estás comerciando", FontType.FONTTYPE_INFO);
			return;
		}
		
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_MERCHANT);
		if (npc != null) {
			// ¿El Npc puede comerciar?
			if (!npc.comercia()) {
				if (npc.getDesc().length() > 0) {
					hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
				}
				return;
			}
			
			// Mandamos el Inventario
			((NpcMerchant)npc).enviarNpcInv(this);
			enviarInventario();
			sendUpdateUserStats();
			
			// Iniciamos el comercio con el Npc
			sendPacket(new CommerceInitResponse());
			flags().Comerciando = true;
		}
		
		if (flags().TargetUser > 0) {
            //User commerce...
            //Can he commerce??
			if (isCounselor()) {
				enviarMensaje("No puedes vender items.", FontType.FONTTYPE_WARNING);
                return;
			}
            
			Player targetPlayer = server.playerById(flags().TargetUser);
			if (targetPlayer == null) {
				return;
			}
            //Is the other one dead??
			if (!targetPlayer.isAlive()) {
				enviarMensaje("¡¡No puedes comerciar con los muertos!!", FontType.FONTTYPE_INFO);
                return;
			}
            
            //Is it me??
			if (targetPlayer == this) {
				enviarMensaje("No puedes comerciar con vos mismo...", FontType.FONTTYPE_INFO);
                return;
			}
            
            //Check distance
			if (pos().distance(targetPlayer.pos()) > 3) {
				enviarMensaje("Estás demasiado lejos del usuario.", FontType.FONTTYPE_INFO);
                return;
			}
            
            //Is he already trading?? is it with me or someone else??
			if (targetPlayer.isTrading() && targetPlayer.flags().TargetUser != this.getId()) {
				enviarMensaje("No puedes comerciar con el usuario en este momento.", FontType.FONTTYPE_INFO);
			}
            
            //Initialize some variables...
			m_comUsu.destUsu = flags().TargetUser;
			m_comUsu.cant = 0;
			m_comUsu.objeto = 0;
			m_comUsu.acepto = false;
            
            //Rutina para comerciar con otro usuario
            m_comUsu.iniciarComercioConUsuario(flags().TargetUser);
		}
		
		enviarMensaje("Primero haz click izquierdo sobre el personaje.", FontType.FONTTYPE_INFO);
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
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (mapa.hayObjeto(pos().x, pos().y) && mapa.getObjeto(pos().x, pos().y).obj_ind == FOGATA) {
			// enviar(MSG_DOK);
			if (!flags().Descansar) {
				enviarMensaje("Te acomodas junto a la fogata y comienzas a descansar.", FontType.FONTTYPE_INFO);
			} else {
				enviarMensaje("Te levantas.", FontType.FONTTYPE_INFO);
			}
			flags().Descansar = !flags().Descansar;
		} else {
			if (flags().Descansar) {
				enviarMensaje("Te levantas.", FontType.FONTTYPE_INFO);
				flags().Descansar = false;
				// enviar(MSG_DOK);
				return;
			}
			enviarMensaje("No hay ninguna fogata junto a la cual descansar.", FontType.FONTTYPE_INFO);
		}
	}

	public void doEnviarEstads() {
		// Comando /EST
		sendUserStatsTxt(this);
	}

	public void sendFame() {
		sendPacket(new FameResponse((int) m_reputacion.asesinoRep, (int) m_reputacion.bandidoRep,
				(int) m_reputacion.burguesRep, (int) m_reputacion.ladronRep, (int) m_reputacion.nobleRep,
				(int) m_reputacion.plebeRep, (int) m_reputacion.getPromedio()));
	}

	/**
	 * Send spell information to user.
	 * 
	 * @param spell slot number
	 */
	public void doInfoHechizo(String s) {
		// Comando INFS"
		// Spell information
		short slot = Short.parseShort(s);
		m_spells.sendMeSpellInfo(slot);
	}

	public void doNavega() {
		double modNave = clazz.clazz().modNavegacion();
		ObjectInfo barco = m_inv.getBarco();
		if (stats().userSkills(Skill.SKILL_Navegacion) / modNave < barco.MinSkill) {
			enviarMensaje("No tenes suficientes conocimientos para usar este barco.", FontType.FONTTYPE_INFO);
			enviarMensaje("Necesitas " + (int) (barco.MinSkill * modNave) + " puntos en navegacion.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!flags().Navegando) {
			m_infoChar.m_cabeza = 0;
			if (isAlive()) {
				m_infoChar.m_cuerpo = barco.Ropaje;
			} else {
				m_infoChar.m_cuerpo = OBJ_INDEX_FRAGATA_FANTASMAL;
			}
			m_infoChar.m_escudo = NingunEscudo;
			m_infoChar.m_arma = NingunArma;
			m_infoChar.m_casco = NingunCasco;
			flags().Navegando = true;
		} else {
			flags().Navegando = false;
			if (isAlive()) {
				m_infoChar.m_cabeza = m_origChar.m_cabeza;
				if (m_inv.tieneArmaduraEquipada()) {
					m_infoChar.m_cuerpo = m_inv.getArmadura().Ropaje;
				} else {
					cuerpoDesnudo();
				}
				if (m_inv.tieneEscudoEquipado()) {
					m_infoChar.m_escudo = m_inv.getEscudo().ShieldAnim;
				}
				if (m_inv.tieneArmaEquipada()) {
					m_infoChar.m_arma = m_inv.getArma().WeaponAnim;
				}
				if (m_inv.tieneCascoEquipado()) {
					m_infoChar.m_casco = m_inv.getCasco().CascoAnim;
				}
			} else {
				m_infoChar.m_cuerpo = OBJ_INDEX_CUERPO_MUERTO;
				m_infoChar.m_cabeza = OBJ_INDEX_CABEZA_MUERTO;
				m_infoChar.m_escudo = NingunEscudo;
				m_infoChar.m_arma = NingunArma;
				m_infoChar.m_casco = NingunCasco;
			}
		}
		sendCharacterChange();
		sendPacket(new NavigateToggleResponse());
	}

	public void tratarDeHacerFogata() {
		MapPos targetPos = MapPos.mxy(flags().TargetObjMap, flags().TargetObjX, flags().TargetObjY);
		Map mapa = server.getMap(targetPos.map);
		if (!mapa.isLegalPos(targetPos, false)) {
			return;
		}

		if (!isAlive()) {
			enviarMensaje("No puedes hacer fogatas estando muerto.", FontType.FONTTYPE_INFO);
			return;
		}
	
		if (mapa.getObjeto(targetPos.x, targetPos.y).obj_ind != Constants.Leña) {
			enviarMensaje("Necesitas clickear sobre Leña para hacer ramitas", FontType.FONTTYPE_INFO);
		    return;
		}
		
		if (pos().distance(targetPos) > 2) {
			enviarMensaje("Estás demasiado lejos para prender la fogata.", FontType.FONTTYPE_INFO);
		    return;
		}
		
		if (mapa.getObjeto(targetPos.x, targetPos.y).obj_cant < 3) {
			enviarMensaje("Necesitas por lo menos tres troncos para hacer una fogata.", FontType.FONTTYPE_INFO);
			return;
		}
		
		int suerte = 1; 
		if (stats().userSkills(Skill.SKILL_Supervivencia) < 6) {
			suerte = 3; // 33%
		} else if (stats().userSkills(Skill.SKILL_Supervivencia) < 34) {
			suerte = 2; // 66%
		} else if (stats().userSkills(Skill.SKILL_Supervivencia) > 34) {
			suerte = 1; // 100%
		}
		boolean exito = Util.Azar(1, suerte) == 1;
		
		if (exito) {
			short objid = FOGATA_APAG;
			int cant = mapa.getObjeto(targetPos.x, targetPos.y).obj_cant / 3;
			if (cant > 1) {
				enviarMensaje("Has hecho " + cant + " fogatas.", FontType.FONTTYPE_INFO);
			} else {
				enviarMensaje("Has hecho una fogata.", FontType.FONTTYPE_INFO);
			}
			mapa.quitarObjeto(targetPos.x, targetPos.y);
			mapa.agregarObjeto(objid, cant, targetPos.x, targetPos.y);
		} else {
			if (flags().UltimoMensaje != 10) {
				enviarMensaje("No has podido hacer la fogata.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 10;
			}
		}
		subirSkill(Skill.SKILL_Supervivencia);
		
		server.getTrashCollector().add(targetPos);
	}

	public void useItem(short slot) {
		if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid == 0) {
			return;
		}
		m_inv.useInvItem(slot);
	}

	private void doLingotes() {
		if (m_inv.getObjeto(flags().TargetObjInvSlot).cant < 5) {
			enviarMensaje("No tienes suficientes minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		ObjectInfo info = findObj(flags().TargetObjInvIndex);
		if (info.objType != ObjType.Minerales) {
			enviarMensaje("Debes utilizar minerales para hacer un lingote.", FontType.FONTTYPE_INFO);
			return;
		}
		m_inv.quitarUserInvItem(flags().TargetObjInvSlot, 5);
		enviarObjetoInventario(flags().TargetObjInvSlot);
		if (Util.Azar(1, info.MinSkill) <= 10) {
			enviarMensaje("Has obtenido un lingote!!!", FontType.FONTTYPE_INFO);
			Map mapa = server.getMap(pos().map);
			if (m_inv.agregarItem(info.LingoteIndex, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(info.LingoteIndex, 1));
			}
			enviarMensaje("¡Has obtenido un lingote!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 7) {
				enviarMensaje("Los minerales no eran de buena calidad, no has logrado hacer un lingote.",
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
					&& info.MinSkill <= stats().userSkills(Skill.SKILL_Mineria) / clazz.clazz().modFundicion()) {
				doLingotes();
			} else {
				enviarMensaje("No tenes conocimientos de mineria suficientes para trabajar este mineral.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	private double calcularPoderDomador() {
		return stats().userAttributes[ATRIB_CARISMA] * (stats().userSkills(Skill.SKILL_Domar) / clazz.clazz().modDomar())
				+ Util.Azar(1, stats().userAttributes[ATRIB_CARISMA] / 3)
				+ Util.Azar(1, stats().userAttributes[ATRIB_CARISMA] / 3)
				+ Util.Azar(1, stats().userAttributes[ATRIB_CARISMA] / 3);
	}

	private void doDomar(Npc npc) {
		if (getUserPets().isFullPets()) {
			enviarMensaje("No podes controlar mas criaturas.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (npc.getPetUserOwner() == this) {
			enviarMensaje("La criatura ya te ha aceptado como su amo.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (npc.getPetUserOwner() != null) {
			enviarMensaje("La criatura ya tiene amo.", FontType.FONTTYPE_INFO);
			return;
		}
		
		double suerteDoma = calcularPoderDomador();
		if (npc.domable() <= suerteDoma) {
			getUserPets().addPet(npc);
			npc.setPetUserOwner(this);
			npc.followMaster();
			enviarMensaje("La criatura te ha aceptado como su amo.", FontType.FONTTYPE_INFO);
			subirSkill(Skill.SKILL_Domar);
			// y hacemos respawn del npc original para reponerlo.
			Npc.spawnNpc(npc.getNPCtype(), MapPos.mxy(npc.pos().map, (short) 0, (short) 0), false, true);
		} else {
			if (flags().UltimoMensaje != 5) {
				enviarMensaje("No has logrado domar la criatura.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 5;
			}
		}
	}

	private boolean suerteMineria() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Mineria) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doMineria() {
		stats().quitarStamina(clazz.clazz().getEsfuerzoExcavar());
		if (suerteMineria()) {
			if (flags().TargetObj == 0) {
				return;
			}
			short objid = findObj(flags().TargetObj).MineralIndex;
			int cant = clazz.clazz().getCantMinerales();
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				Map mapa = server.getMap(pos().map);
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has extraido algunos minerales!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 9) {
				enviarMensaje("¡No has conseguido nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 9;
			}
		}
		subirSkill(Skill.SKILL_Mineria);
		flags().Trabajando = true;
	}

	private boolean suerteTalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 13, 7, 7 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Talar) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doTalar() {
		stats().quitarStamina(clazz.clazz().getEsfuerzoTalar());
		if (suerteTalar()) {
			int cant = clazz.clazz().getCantLeños();
			short objid = Leña;
			Map mapa = server.getMap(pos().map);
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has conseguido algo de leña!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 8) {
				enviarMensaje("No has conseguido leña. Intenta otra vez.", FontType.FONTTYPE_INFO);
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
				if (victima.m_inv.getObjeto(slot).objid > 0) {
					if (victima.m_inv.getObjeto(slot).esRobable()) {
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
				if (victima.m_inv.getObjeto(slot).objid > 0) {
					if (victima.m_inv.getObjeto(slot).esRobable()) {
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
			short objid = m_inv.getObjeto(slot).objid;
			if (cant > m_inv.getObjeto(slot).cant) {
				cant = m_inv.getObjeto(slot).cant;
			}
			victima.m_inv.quitarUserInvItem(slot, cant);
			victima.enviarObjetoInventario(slot);
			Map mapa = server.getMap(pos().map);
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			enviarObjetoInventario(slot);
			ObjectInfo info = findObj(objid);
			enviarMensaje("Has robado " + cant + " " + info.Nombre, FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("No has logrado robar un objetos.", FontType.FONTTYPE_INFO);
		}
	}

	private boolean suerteRobar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Robar) / 10)]);
		return (Util.Azar(1, rango) < 3);
	}

	private void doRobar(Player victima) {
		Map mapa = server.getMap(pos().map);
		if (!mapa.esZonaSegura() || triggerZonaPelea(victima) != MapCell.TRIGGER6_AUSENTE) {
			return;
		}
		if (flags().Privilegios < 2) {
			if (suerteRobar()) {
				// Exito robo
				if ((Util.Azar(1, 50) < 25) && (clazz == Clazz.Thief)) {
					if (victima.m_inv.tieneObjetosRobables()) {
						robarObjeto(victima);
					} else {
						enviarMensaje(victima.m_nick + " no tiene objetos.", FontType.FONTTYPE_INFO);
					}
				} else { // Roba oro
					if (victima.stats().getGold() > 0) {
						int cantidadRobada = Util.Azar(1, 100);
						victima.stats().addGold( -cantidadRobada );
						stats().addGold( cantidadRobada );
						enviarMensaje("Le has robado " + cantidadRobada + " monedas de oro a " + victima.m_nick, FontType.FONTTYPE_INFO);
					} else {
						enviarMensaje(m_nick + " no tiene oro.", FontType.FONTTYPE_INFO);
					}
				}
			} else {
				enviarMensaje("¡No has logrado robar nada!", FontType.FONTTYPE_INFO);
				victima.enviarMensaje("¡" + m_nick + " ha intentado robarte!", FontType.FONTTYPE_INFO);
				victima.enviarMensaje("¡" + m_nick + " es un criminal!", FontType.FONTTYPE_INFO);
			}
			if (!isCriminal()) {
				volverCriminal();
			}
			if (m_faccion.ArmadaReal) {
				m_faccion.expulsarFaccionReal();
			}
			m_reputacion.incLandron(vlLadron);
			subirSkill(Skill.SKILL_Robar);
		}
	}

	private boolean suertePescarCaña() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Pesca) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarCaña() {
		stats().quitarStamina(clazz.clazz().getEsfuerzoPescar());
		if (suertePescarCaña()) {
			Map mapa = server.getMap(pos().map);
			if (m_inv.agregarItem(OBJ_PESCADO, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(OBJ_PESCADO, 1));
			}
			enviarMensaje("¡Has pescado un lindo pez!", FontType.FONTTYPE_INFO);
		} else {
			if (flags().UltimoMensaje != 6) {
				enviarMensaje("¡No has pescado nada!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 6;
			}
		}
		subirSkill(Skill.SKILL_Pesca);
		flags().Trabajando = true;
	}

	private boolean suertePescarRed() {
		final short[] suerte = { 60, 54, 49, 43, 38, 32, 27, 21, 16, 11, 11 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Pesca) / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarRed() {
		stats().quitarStamina(clazz.clazz().getEsfuerzoPescar());
		int skills = stats().userSkills(Skill.SKILL_Pesca);
		if (skills == 0) {
			return;
		}
		if (suertePescarRed()) {
			int cant = (clazz == Clazz.Fisher) ? Util.Azar(1, 5) : 1;
			short objid = (short) PESCADOS_RED[Util.Azar(1, PESCADOS_RED.length) - 1];
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				Map mapa = server.getMap(pos().map);
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has pescado algunos peces!", FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("¡No has pescado nada!", FontType.FONTTYPE_INFO);
		}
		subirSkill(Skill.SKILL_Pesca);
	}

	public void workLeftClick(byte x, byte y, short skill) {
		Pos pos = new Pos(x, y);
		if (!isAlive() || flags().Descansar || flags().Meditando || !pos.isValid()) {
			return;
		}
		if (!pos().inRangoVision(pos)) {
			sendPositionUpdate();
			return;
		}
		Map mapa = server.getMap(pos().map);
		Player tu = null;
		MapObject obj = null;

		switch (skill) {
		case Skill.SKILL_Proyectiles:
			// Nos aseguramos que este usando un arma de proyectiles
			if (!m_inv.tieneArmaEquipada()) {
				return;
			}
			if (!m_inv.getArma().esProyectil()) {
				return;
			}
			if (!m_inv.tieneMunicionEquipada()) {
				enviarMensaje("No tienes municiones.", FontType.FONTTYPE_INFO);
				return;
			}
			// Quitamos stamina
			if (stats().stamina >= 10) {
				stats().quitarStamina(Util.Azar(1, 10));
			} else {
				enviarMensaje("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
				return;
			}

			mapa.lookAtTile(this, x, y);
			tu = server.playerById(flags().TargetUser);
			Npc npc = server.npcById(flags().TargetNpc);
			if (npc != null) {
				if (!npc.getAttackable()) {
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
						enviarMensaje(
								"No puedes atacar a ciudadanos, para hacerlo, antes debes desactivar el seguro con la tecla S",
								FONTTYPE_FIGHT);
						return;
					}
				}
				usuarioAtacaUsuario(tu);
			}
			// Consumir munición.
			int slotMunicion = m_inv.getMunicionSlot();
			m_inv.quitarUserInvItem(m_inv.getMunicionSlot(), 1);
			if (m_inv.getObjeto(slotMunicion) != null && m_inv.getObjeto(slotMunicion).cant > 0) {
				m_inv.equipar(slotMunicion);
			}
			enviarObjetoInventario(slotMunicion);
			break;
			
			
		case Skill.SKILL_Magia:
			if (!counters().intervaloPermiteLanzarSpell()) {
				return;
			}
			if (isCounselor())
				return;
			mapa.lookAtTile(this, x, y);
			if (flags().Hechizo > 0) {
				m_spells.lanzarHechizo(server.getHechizo(flags().Hechizo));
				flags().Hechizo = 0;
			} else {
				enviarMensaje("¡Primero selecciona el hechizo que deseas lanzar!", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Pesca:
			if (!counters().intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneArmaEquipada()) {
				enviarMensaje("Deberías equiparte la caña o la red.", FontType.FONTTYPE_INFO);
				return;
			}
			if (m_inv.getArma().ObjIndex != OBJ_INDEX_CAÑA
					&& m_inv.getArma().ObjIndex != OBJ_INDEX_RED_PESCA) {
				enviarMensaje("Deberías equiparte la caña o la red.", FontType.FONTTYPE_INFO);
				return;
			}
			if (mapa.getTrigger(pos().x, pos().y) == 1) {
				enviarMensaje("No puedes pescar desde donde te encuentras.", FontType.FONTTYPE_INFO);
				return;
			}
			if (mapa.hayAgua(x, y)) {
				enviarSonido(SOUND_PESCAR);
				switch (m_inv.getArma().ObjIndex) {
				case OBJ_INDEX_CAÑA:
					doPescarCaña();
					break;
				case OBJ_INDEX_RED_PESCA:
					if (pos().distance(MapPos.mxy(pos().map, x, y)) > 2) {
						enviarMensaje("Estás demasiado lejos para pescar.", FontType.FONTTYPE_INFO);
						return;
					}
					doPescarRed();
					break;
				}
			} else {
				enviarMensaje("No hay agua donde pescar. Busca un lago, rio o mar.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Robar:
			if (!mapa.esZonaSegura()) {
				if (!counters().intervaloPermiteTrabajar()) {
					return;
				}
				mapa.lookAtTile(this, x, y);
				if (flags().TargetUser > 0 && flags().TargetUser != getId()) {
					tu = server.playerById(flags().TargetUser);
					if (tu.isAlive()) {
						MapPos wpaux = MapPos.mxy(pos().map, x, y);
						if (wpaux.distance(pos()) > 2) {
							enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
							return;
						}
						// Nos aseguramos que el trigger le permite robar
						if (mapa.getTrigger(tu.pos().x, tu.pos().y) == 4) {
							enviarMensaje("No podes robar aquí.", FontType.FONTTYPE_WARNING);
							return;
						}
						doRobar(tu);
					}
				} else {
					enviarMensaje("No hay a quien robarle!.", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("¡No podes robarle en zonas seguras!.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Talar:
			if (!counters().intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneArmaEquipada()) {
				enviarMensaje("Deberías equiparte el hacha de leñador.", FontType.FONTTYPE_INFO);
				return;
			}
			if (m_inv.getArma().ObjIndex != HACHA_LEÑADOR) {
				enviarMensaje("Deberías equiparte el hacha de leñador.", FontType.FONTTYPE_INFO);
				return;
			}
			obj = mapa.getObjeto(x, y);
			if (obj != null) {
				MapPos wpaux = MapPos.mxy(pos().map, x, y);
				if (wpaux.distance(pos()) > 2) {
					enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
					return;
				}
				// ¿Hay un arbol donde cliqueo?
				if (obj.getInfo().objType == ObjType.Arboles) {
					enviarSonido(SOUND_TALAR);
					doTalar();
				}
			} else {
				enviarMensaje("No hay ningun arbol ahi.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Mineria:
			if (!counters().intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneArmaEquipada()) {
				return;
			}
			if (m_inv.getArma().ObjIndex != PIQUETE_MINERO) {
				doSALIR();
				return;
			}
			mapa.lookAtTile(this, x, y);
			obj = mapa.getObjeto(x, y);
			if (obj != null) {
				MapPos wpaux = MapPos.mxy(pos().map, x, y);
				if (wpaux.distance(pos()) > 2) {
					enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
					return;
				}
				// ¿Hay un yacimiento donde cliqueo?
				if (obj.getInfo().objType == ObjType.Yacimiento) {
					enviarSonido(SOUND_MINERO);
					doMineria();
				} else {
					enviarMensaje("Ahi no hay ningun yacimiento.", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ningun yacimiento.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Domar:
			// Modificado 25/11/02
			// Optimizado y solucionado el bug de la doma de
			// criaturas hostiles.
			mapa.lookAtTile(this, x, y);
			if (flags().TargetNpc > 0) {
				npc = server.npcById(flags().TargetNpc);
				if (npc.domable() > 0) {
					MapPos wpaux = MapPos.mxy(pos().map, x, y);
					if (wpaux.distance(pos()) > 2) {
						enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
						return;
					}
					if (npc.atacadoPorUsuario()) {
						enviarMensaje("No puedes domar una criatura que está luchando con un jugador.", FontType.FONTTYPE_INFO);
						return;
					}
					doDomar(npc);
				} else {
					enviarMensaje("No puedes domar a esa criatura.", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("No hay ninguna criatura alli!.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_FundirMetal:
			mapa.lookAtTile(this, x, y);
			if (flags().TargetObj > 0) {
				if (findObj(flags().TargetObj).objType == ObjType.Fragua) {
					fundirMineral();
				} else {
					enviarMensaje("Ahi no hay ninguna fragua.", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ninguna fragua.", FontType.FONTTYPE_INFO);
			}
			break;
			
			
		case Skill.SKILL_Herreria:
			mapa.lookAtTile(this, x, y);
			if (flags().TargetObj > 0) {
				if (findObj(flags().TargetObj).objType == ObjType.Yunque) {
					m_inv.enviarArmasConstruibles();
					m_inv.enviarArmadurasConstruibles();
					sendPacket(new ShowBlacksmithFormResponse());
				} else {
					enviarMensaje("Ahi no hay ningun yunque.", FontType.FONTTYPE_INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ningun yunque.", FontType.FONTTYPE_INFO);
			}
			break;
		}
	}

	public void handleWork(int skill) {
		// Comando UK
		if (!checkAlive()) {
			return;
		}

		switch (skill) {
		case Skill.SKILL_Robar:
			sendPacket(new WorkRequestTargetResponse((byte)Skill.SKILL_Robar));
			break;
		case Skill.SKILL_Magia:
			sendPacket(new WorkRequestTargetResponse((byte)Skill.SKILL_Magia));
			break;
		case Skill.SKILL_Domar:
			sendPacket(new WorkRequestTargetResponse((byte)Skill.SKILL_Domar));
			break;
		case Skill.SKILL_Ocultarse:
			if (flags().Navegando) {
				if (flags().UltimoMensaje != 3) {
					enviarMensaje("No puedes ocultarte si estas navegando.", FontType.FONTTYPE_INFO);
					flags().UltimoMensaje = 3;
				}
				return;
			}
			if (flags().Oculto) {
				if (flags().UltimoMensaje != 2) {
					enviarMensaje("Ya estabas oculto.", FontType.FONTTYPE_INFO);
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
			m_spells.castSpell(slot);
			handleWork(Skill.SKILL_Magia);
		}
	}

	public void doInformacion() {
		// Comando /INFORMACION
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_INFORMATION);
		if (npc == null) {
			return;
		}		
		if (!npc.esFaccion()) {
			if (!m_faccion.ArmadaReal) {
				hablar(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			hablar(COLOR_BLANCO,
					"Tu deber es combatir criminales, cada 100 criminales que derrotes te dare una recompensa.",
					npc.getId());
		} else {
			if (!m_faccion.FuerzasCaos) {
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
			enviarMensaje("Comienzas a meditar.", FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("Dejas de meditar.", FontType.FONTTYPE_INFO);
		}
		flags().Meditando = !flags().Meditando;
		if (flags().Meditando) {
			m_counters.tInicioMeditar = Util.millis();
			int segs = (TIEMPO_INICIO_MEDITAR / 1000);
			enviarMensaje("Te estás concentrando. En " + segs + " segundos comenzarás a meditar.", FontType.FONTTYPE_INFO);
			m_infoChar.m_loops = LoopAdEternum;
			if (stats().ELV < 15) {
				enviarCFX(FXMEDITARCHICO, LoopAdEternum);
				m_infoChar.m_fx = FXMEDITARCHICO;
			} else if (stats().ELV < 30) {
				enviarCFX(FXMEDITARMEDIANO, LoopAdEternum);
				m_infoChar.m_fx = FXMEDITARMEDIANO;
			} else {
				enviarCFX(FXMEDITARGRANDE, LoopAdEternum);
				m_infoChar.m_fx = FXMEDITARGRANDE;
			}
		} else {
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
			enviarCFX(0, 0);
		}
	}

	public void doResucitar() {
		// Comando /RESUCITAR
		Npc npc = getNearNpcSelected(DISTANCE_PRIEST);
		if (npc == null) {
			return;
		}		
		if (!npc.esSacerdote() && !npc.esSacerdoteNewbies()) {
			enviarMensaje("No poseo el poder de revivir a otros, mejor encuentra un sacerdote.", FontType.FONTTYPE_INFO);
			return;
		}
		if (npc.esSacerdoteNewbies() && !esNewbie()) {
			enviarMensaje("Lo siento, sólo puedo resucitar newbies.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (isAlive()) {
			enviarMensaje("¡JA! Debes estar muerto para resucitarte.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!existePersonaje()) {
			enviarMensaje("!!El personaje no existe, cree uno nuevo.", FontType.FONTTYPE_INFO);
			doSALIR();
			return;
		}
		revivirUsuario();
		enviarMensaje("¡¡Has sido resucitado!!", FontType.FONTTYPE_INFO);
	}

	public void doCurar() {
		// Comando /CURAR
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_PRIEST);
		if (npc == null) {
			return;
		}		
		if (!npc.esSacerdote() && !npc.esSacerdoteNewbies()) {
			enviarMensaje("No poseo el poder para curar a otros, mejor encuentra un sacerdote.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!checkAlive("¡Solo puedo curar a los vivos! ¡Resucítate primero!")) {
			return;
		}
		stats().MinHP = stats().MaxHP;
		sendUpdateUserStats();
		enviarMensaje("¡¡Has sido curado!!", FontType.FONTTYPE_INFO);
	}

	public void cambiarDescripcion(String s) {
		// Comando "/DESC "
		s = s.trim();
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		if (!Util.asciiValidos(s)) {
			enviarMensaje("La descripcion tiene caracteres invalidos.", FontType.FONTTYPE_INFO);
			return;
		}
		m_desc = s;
		enviarMensaje("La descripcion a cambiado.", FontType.FONTTYPE_INFO);
	}

	public void changeHeading(byte heading) {
		m_infoChar.m_dir = heading;
		sendCharacterChange();
	}

	public void sendCharacterChange() {
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		mapa.enviarAlArea(pos().x, pos().y, createCC());
	}

	public void enviarValCode() {
		// FIXME
		// enviar(MSG_VAL, (short) 345, (short) 343);
	}

	public void doSALIR() {
		if (m_saliendo) {
			return;
		}
		m_saliendo = true;
		log.info("saliendo: " + this.getNick());
		boolean wasLogged = flags().UserLogged;
		try {
			Map mapa = server.getMap(pos().map);
			if (mapa != null && mapa.hasPlayer(this)) {
				getUserPets().removeAll();
				mapa.exitMap(this);
			}
			if (wasLogged) {
				if (server.isRaining()) {
					// Detener la lluvia.
					sendPacket(new RainToggleResponse());
				}
				sendPacket(new DisconnectResponse());
			}
			flags().UserLogged = false;
		} catch (Exception ex) {
			log.fatal("ERROR EN doSALIR(): ", ex);
		} finally {
			server.dropPlayer(this);
			if (wasLogged) {
				try {
					userStorage.saveUserToStorage();
				} catch (Exception ex) {
					log.fatal("ERROR EN doSALIR() - saveUser(): ", ex);
				}
			}
		}
		log.info("Salió: " + getNick());
	}

	public void throwDices() { // and get lucky!
		
		// TODO dados fáciles, hacerlo configurable
		stats().userAttributes[ATRIB_FUERZA] = (byte) (Util.Azar(16, 18));
		stats().userAttributes[ATRIB_AGILIDAD] = (byte) (Util.Azar(16, 18));
		stats().userAttributes[ATRIB_INTELIGENCIA] = (byte) (Util.Azar(16, 18));
		stats().userAttributes[ATRIB_CARISMA] = (byte) (Util.Azar(16, 18));
		stats().userAttributes[ATRIB_CONSTITUCION] = (byte) (Util.Azar(16, 18));

		sendPacket(new DiceRollResponse(
				stats().userAttributes[ATRIB_FUERZA], 
				stats().userAttributes[ATRIB_AGILIDAD],
				stats().userAttributes[ATRIB_INTELIGENCIA], 
				stats().userAttributes[ATRIB_CARISMA], 
				stats().userAttributes[ATRIB_CONSTITUCION]));
	}

	/**
	 * Procesa el clic izquierdo del mouse sobre el mapa
	 * 
	 * @param x es la posición x del clic
	 * @param y es la posición y del clic
	 */
	public void leftClickOnMap(byte x, byte y) {
		// Clic con el botón primario del mouse
		Map map = server.getMap(pos().map);
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
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}

		// ¿Posicion valida?
		if (Pos.isValid(x, y)) {
			// ¿Hay un objeto en el tile?
			if (mapa.hayObjeto(x, y)) {
				MapObject obj = mapa.getObjeto(x, y);
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
				MapObject obj = mapa.buscarObjeto(x, y);
				Npc npc;
				if (obj != null) {
					flags().TargetObj = obj.getInfo().ObjIndex;
					if (obj.getInfo().objType == ObjType.Puertas) {
						mapa.accionParaPuerta(obj.x, obj.y, this);
					}
				} else if ((npc = mapa.getNPC(x, y)) != null) {
					flags().TargetNpc = npc.getId();
					if (npc.comercia()) {
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
					} else if (npc.esSacerdote() || npc.esSacerdoteNewbies()) {
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
					enviarMensaje("No ves nada interesante.", FontType.FONTTYPE_INFO);
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
		Map mapa = server.getMap(pos().map);
		if (mapa != null) {
			mapa.enviarAlArea(pos().x, pos().y,
					new ChatOverHeadResponse(text, getId(), 
							Color.r(COLOR_BLANCO), Color.g(COLOR_BLANCO), Color.b(COLOR_BLANCO)));
		}
		if (isCounselor()) {
			Log.logGM(m_nick, "El consejero dijo: " + text);
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
		Map mapa = server.getMap(pos().map);
		if (mapa != null) {
			mapa.enviarAlArea(pos().x, pos().y,
				new ChatOverHeadResponse(text, getId(), 
						Color.r(Color.COLOR_ROJO), Color.g(Color.COLOR_ROJO), Color.b(Color.COLOR_ROJO)));
		}
		if (isCounselor()) {
			Log.logGM(m_nick, "El consejero gritó: " + text);
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
		
		Map map = server.getMap(pos().map);
		if (map == null) {
			return;
		}
		Player targetUser = server.playerById(targetIndex);
		if (targetUser != null) {
			if (map.buscarEnElArea(pos().x, pos().y, targetUser.getId()) == null) {
				enviarMensaje("Estas muy lejos de " + targetUser.getNick(), FontType.FONTTYPE_INFO);
			} else {
				if (isCounselor()) {
					Log.logGM(m_nick, "El consejero le susurró a " + targetUser.getNick() + ": " + text);
				}
				
				// send to target user
				targetUser.sendPacket(new ChatOverHeadResponse(text, getId(), 
								Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));
				// send to source user
				sendPacket(new ChatOverHeadResponse(text, getId(), 
						Color.r(Color.COLOR_AZUL), Color.g(Color.COLOR_AZUL), Color.b(Color.COLOR_AZUL)));
				
				if (!isGM() || isCounselor()) {
					// send to admins at area
					map.enviarAlAreaAdminsNoConsejeros(pos().x, pos().y,
							new ChatOverHeadResponse("a " + targetUser.getNick() + "> " + text, this.getId(),
									Color.r(Color.COLOR_AMARILLO), Color.g(Color.COLOR_AMARILLO), Color.b(Color.COLOR_AMARILLO)));
				}
			}
		} else {
			enviarMensaje("Usuario inexistente.", FontType.FONTTYPE_INFO);
		}
	}

	public void walk(Heading heading) {
		try {
			speedHackMover.check();
		} catch (SpeedHackException e) {
			enviarMensaje(e.getMessage(), FontType.FONTTYPE_SERVER);
			doSALIR();
			return;
		}

		if (m_counters.Saliendo) {
			enviarMensaje("/SALIR cancelado!.", FontType.FONTTYPE_INFO);
			m_counters.Saliendo = false;
		}

		if (flags().Paralizado) {
			if (flags().UltimoMensaje != 1) {
				enviarMensaje("No puedes moverte por estar paralizado.", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 1;
			}
			return;
		}
		if (flags().Descansar) {
			flags().Descansar = false;
			sendPacket(new RestOKResponse());
			enviarMensaje("Has dejado de descansar.", FontType.FONTTYPE_INFO);
		}
		if (flags().Meditando) {
			flags().Meditando = false;
			sendPacket(new MeditateToggleResponse());
			enviarMensaje("Dejas de meditar.", FontType.FONTTYPE_INFO);
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
			enviarCFX(0, 0);
		}
		if (flags().Oculto) {
			if (clazz != Clazz.Thief) {
				volverseVisible();
			}
		}
		move(heading);
		flags().Trabajando = false;
	}

	private void volverseVisible() {
		enviarMensaje("¡Has vuelto a ser visible!", FontType.FONTTYPE_INFO);
		flags().Oculto = false;
		flags().Invisible = false;
		Map map = server.getMap(pos().map);
		map.enviarAlArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)0));
	}

	private void move(Heading heading) {
		MapPos oldPos = pos().copy();
		Map map = server.getMap(pos().map);
		if (map == null) {
			return;
		}
		
		infoChar().setDir(heading);
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
				boolean enviarData 	= pos().map != targetPos.map;
				if (!enterIntoMap(targetPos.map, targetPos.x, targetPos.y, withFX, enviarData)) {
					// if fails, go back to the closest original position
					MapPos warpPos = map.closestLegalPosPj(oldPos.x, oldPos.y, flags().Navegando, isGM());
					warpUser(warpPos.map, warpPos.x, warpPos.y, true);
				};
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
			enviarMensaje("¡¡No puedes atacar a nadie!!", FontType.FONTTYPE_INFO);
			return;
		} else {
			if (m_inv.tieneArmaEquipada()) {
				if (m_inv.getArma().esProyectil()) {
					enviarMensaje("No podés usar asi esta arma.", FontType.FONTTYPE_INFO);
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
			enviarMensaje("No puedes recoger ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		getObj();
	}

	public void dropObject(byte slot, int cant) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden tirar objetos.")) {
			return;
		}
		if (isSailing()) {
			enviarMensaje("No puedes tirar objetos mientras navegas.", FontType.FONTTYPE_INFO);
			return;
		}
		if (isCounselor()) {
			enviarMensaje("No puedes tirar ningún objeto.", FontType.FONTTYPE_INFO);
			return;
		}
		if (slot == FLAGORO) {
			tirarOro(cant);
			sendUpdateUserStats();
		} else {
			if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
				if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid > 0) {
					m_inv.dropObj(slot, cant);
				}
			}
		}
	}

	public void getObj() {
		Map mapa = server.getMap(pos().map);
		// ¿Hay algun obj?
		if (mapa.hayObjeto(pos().x, pos().y)) {
			// ¿Esta permitido agarrar este obj?
			MapObject obj = mapa.getObjeto(pos().x, pos().y);
			if (!obj.getInfo().esAgarrable()) {
				enviarMensaje("El objeto no se puede agarrar.", FontType.FONTTYPE_INFO);
			} else {
				int agregados = m_inv.agregarItem(obj.obj_ind, obj.obj_cant);
				if (agregados < obj.obj_cant) {
					mapa.quitarObjeto(pos().x, pos().y);
					mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(obj.obj_ind, obj.obj_cant - agregados));
				} else {
					// Quitamos el objeto
					mapa.quitarObjeto(pos().x, pos().y);
					if (isGM()) {
						Log.logGM(m_nick, "Agarró: " + obj.obj_ind + " objeto=" + obj.getInfo().Nombre);
					}
				}
			}
		} else {
			enviarMensaje("No hay nada aqui.", FontType.FONTTYPE_INFO);
		}
	}

	public void equipItem(short slot) {
		// Comando EQUI
		if (!checkAlive("¡¡Estas muerto!! Solo puedes usar items cuando estas vivo.")) {
			return;
		}
		if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
			if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid > 0) {
				m_inv.equipar(slot); // EquiparInvItem
			}
		}
	}

	public void enviarInventario() {
		// updateUserInv
		for (int i = 1; i <= m_inv.size(); i++) {
			enviarObjetoInventario(i);
		}
	}

	public void enviarObjetoInventario(int slot) {
		InventoryObject inv = m_inv.getObjeto(slot);
		
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

	private void enviarIndiceUsuario() {
		sendPacket(new UserIndexInServerResponse(getId()));
	}

	private void enviarIP() {
		sendPacket(new UserCharIndexInServerResponse(getId()));
	}

	public void enviarMensaje(String msg, FontType fuente) {
		sendPacket(new ConsoleMsgResponse(msg, fuente.id()));
	}

	public void enviarHabla(int color, String msg, short id) {
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
				(byte)stats().ELV, // level 
				stats().ELU, // pasar de nivel
				stats().Exp)); // expteriencia
	}

	public void enviarEstadsHambreSed() {
		sendPacket(new UpdateHungerAndThirstResponse(
				(byte) stats().maxDrinked, 
				(byte) stats().drinked, 
				(byte) stats().maxEaten, 
				(byte) stats().eaten));
	}


	private boolean enterIntoMap(short mapNumber, byte x, byte y, boolean withFX, boolean sendingData) {
		if (m_saliendo) {
			return false;
		}
		MapPos originalPos = pos().copy();

		// exit from the old map
		if (pos().map != 0) {
			Map oldMap = server.getMap(pos().map);
			if (oldMap == null) {
				return false;
			}
			if (oldMap.hasPlayer(this) && !oldMap.exitMap(this)) {
				// it fails to exit map
				log.fatal(this.getNick() + " no pudo salir del mapa actual");
			}
		}
		
		Map targetMap = server.getMap(mapNumber);
		if (targetMap == null) {
			log.warn("No existe el mapa " + mapNumber);
			return false;
		}

		if (targetMap.isForbbidenMap(this)) {
			return false;
		}

		MapPos freePos = targetMap.closestLegalPosPj(x, y, flags().Navegando, isGM());

		// enter into the new map
		if (!targetMap.enterMap(this, freePos.x, freePos.y)) {
			enviarMensaje("No se pudo entrar en el mapa deseado", FontType.FONTTYPE_WARNING);
			return false;
		}
		
		pos().set(mapNumber, freePos.x, freePos.y);
		if (sendingData) {
			sendPacket(new ChangeMapResponse((short)mapNumber, (short)targetMap.getVersion()));
		}
		sendPacket(createCC());

		targetMap.areasData.loadUser(this);
		sendPositionUpdate();

		if (withFX) {
			enviarCFX(1, 0);
			if (flags().UserLogged) { // No hacer sonido en el LOGIN.
				enviarSonido(SND_WARP);
			}
		}
		
		enviarIP();
		if (sendingData) {
			// Enviarme los m_clients del mapa, sin contarme a mi
			targetMap.enviarClientes(this);
			// Enviarme los objetos del mapa.
			targetMap.enviarObjetos(this);
			// Enviarme las posiciones bloqueadas del mapa.
			targetMap.enviarBQs(this);
		}
		
		if (originalPos.map != mapNumber) {
			warpMascotas();
		}
		
		return true;
	}

	public void enviarCFX(int fx, int val) {
		Map m = server.getMap(pos().map);
		if (m == null) {
			return;
		}
		m.sendCreateFX(pos().x, pos().y, getId(), fx, val);
	}

	public boolean estaParalizado() {
		return flags().Paralizado;
	}

	public void revivirUsuario() {
		if (m_counters.Saliendo) {
			return;
		}
		Map m = server.getMap(pos().map);
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
		m_infoChar.m_cabeza = m_origChar.m_cabeza;
		cuerpoDesnudo();
		sendCharacterChange();
		sendUpdateUserStats();
		enviarEstadsHambreSed();
	}

	public boolean warpUser(short targetMap, byte x, byte y, boolean withFX) {
		MapPos oldPos = pos().copy();
		// Quitar el dialogo
		Map map = server.getMap(pos().map);
		if (map != null) {
			map.enviarAlArea(x, y, new RemoveCharDialogResponse(getId()));
		}
		
		boolean changingMap = pos().map != targetMap; 
		
		// Si el destino es distinto a la posición actual
		if (changingMap || pos().x != x || pos().y != y) {
			Map newMap = server.getMap(targetMap);
			MapPos pos_libre = newMap.closestLegalPosPj(x, y, flags().Navegando, isGM());
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
			map.enviarAlArea(x, y, new SetInvisibleResponse(getId(), (byte)1));			
		}
		if (withFX && !flags().AdminInvisible) {
			enviarSonido(SND_WARP);
			enviarCFX(FXWARP, 0);
		}
		warpMascotas();
		
		return true;
	}

	private void warpMascotas() {
		// copy list first
		var pets = getUserPets().getPets().stream().collect(Collectors.toList());
		
		pets.forEach(pet -> {
			if (pet.getContadores().TiempoExistencia > 0) {
				// Es una mascota de invocación. Se pierde al cambiar de mapa
				getUserPets().removePet(pet);
			} else if (pet.puedeReSpawn()) {
				// Es una mascota domada que puede hacer respawn
				
				Map oldMapa = server.getMap(pet.pos().map);
				Map newMapa = server.getMap(pos().map);
				MapPos lugarLibre = newMapa.closestLegalPosNpc(pos().x, pos().y, pet.esAguaValida(), pet.esTierraInvalida(), true);
				
				if (lugarLibre != null) {
					// La mascota lo sigue al nuevo mapa, y mantiene su
					// control.
					oldMapa.exitNpc(pet);
					// envio un BP adicional, porque ya sali del mapa y
					// sino no soy notificado de que salio la mascota.
					// enviar(MSG_QDL, m_mascotas[i].getId());
					// enviar(MSG_BP, m_mascotas[i].getId());
					if (newMapa.enterNpc(pet, lugarLibre.x, lugarLibre.y)) {
						// FIXME !!!
					}
				} else {
					// La mascota no puede seguirlo al nuevo mapa, asi que
					// pierde su control.
					getUserPets().removePet(pet);
				}
			} else {
				// La mascota no puede seguirlo al nuevo mapa, asi que
				// pierde su control.
				getUserPets().removePet(pet);
			}
		});
		
		if (pets.size() < getUserPets().getPets().size()) {
			enviarMensaje("Pierdes el control de tus mascotas.", FontType.FONTTYPE_INFO);
		}
	}

	public void encarcelar(int minutos, String gm_name) {
		m_counters.Pena = minutos;
		if (warpUser(GameServer.WP_PRISION.map, GameServer.WP_PRISION.x, GameServer.WP_PRISION.y, true)) {
			if (gm_name == null) {
				enviarMensaje("Has sido encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.FONTTYPE_INFO);
			} else {
				enviarMensaje(gm_name + " te ha encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void efectoLluvia() {
		if (flags().UserLogged) {
			Map mapa = server.getMap(pos().map);
			if (server.isRaining() && mapa.intemperie(pos().x, pos().y) && mapa.getZona() != ZONA_DUNGEON) {
				int modifi = Util.porcentaje(stats().maxStamina, 3);
				stats().quitarStamina(modifi);
				enviarMensaje("¡¡Has perdido stamina, busca pronto refugio de la lluvia!!.", FontType.FONTTYPE_INFO);
				sendUpdateUserStats();
			}
		}
	}

	public void efectoParalisisUser() {
		if (m_counters.Paralisis > 0) {
			m_counters.Paralisis--;
		} else {
			flags().Paralizado = false;
			sendPacket(new ParalizeOKResponse());
		}
	}

	private boolean suerteMostrarse() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Ocultarse) / 10)]);
		if (clazz != Clazz.Thief) {
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
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Ocultarse) / 10)]);
		if (clazz != Clazz.Thief) {
			rango += 50;
		}
		return (Util.Azar(1, rango) <= 5);
	}

	private void volverseOculto() {
		flags().Oculto = true;
		flags().Invisible = true;
		Map map = server.getMap(pos().map);
		map.enviarAlArea(pos().x, pos().y, new SetInvisibleResponse(getId(), (byte)1));
		enviarMensaje("¡Te has escondido entre las sombras!", FontType.FONTTYPE_INFO);
	}

	public void doOcultarse() {
		if (suerteOcultarse()) {
			volverseOculto();
			subirSkill(Skill.SKILL_Ocultarse);
		} else {
			if (flags().UltimoMensaje != 4) {
				enviarMensaje("¡No has logrado esconderte!", FontType.FONTTYPE_INFO);
				flags().UltimoMensaje = 4;
			}
		}
		flags().Trabajando = true;
	}

	public void decirPalabrasMagicas(String palabrasMagicas) {
		hablar(COLOR_CYAN, palabrasMagicas, getId());
	}

	public void paralizar(Spell hechizo) {
		if (!flags().Paralizado) {
			flags().Paralizado = true;
			counters().Paralisis = IntervaloParalizado;
			enviarSonido(hechizo.WAV);
			enviarCFX(hechizo.FXgrh, hechizo.loops);
			sendPacket(new ParalizeOKResponse());
		}
	}

	public void hablar(int color, String texto, int quienId) {
		if (texto.length() > MAX_TEXTO_HABLAR) {
			texto = texto.substring(0, MAX_TEXTO_HABLAR);
		}

		Map mapa = server.getMap(pos().map);
		if (mapa != null) {
			mapa.enviarAlArea(pos().x, pos().y,
					new ChatOverHeadResponse(texto, (short)quienId,
							Color.r(color), Color.g(color), Color.b(color)));
		}
	}

	public Npc crearMascotaInvocacion(int npcId, MapPos targetPos) {
		if (getUserPets().isFullPets()) {
			return null;
		}
		Npc npc = Npc.spawnPetNpc(npcId, targetPos, true, server);
		if (npc == null) {
			return null;
		}

		getUserPets().addPet(npc);
		npc.setPetUserOwner(this);
		npc.getContadores().TiempoExistencia = IntervaloInvocacion; // Duración que tendrá la invocación
		npc.setSpellSpawnedPet(true);
		npc.setGiveGLD(0);
		npc.followMaster();
		npc.sendPlayWave(SND_WARP);
		npc.sendCreateFX(FXWARP, 0);
		npc.activate();

		return npc;
	}

	public Object[] ccParams() {
		byte crimi = (byte) (isCriminal() ? 1 : 0);

		Object[] params = { 
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
				
				(String)m_nick + getClan(), 
				(byte)crimi, 
				(byte)flags().Privilegios };

		return params;
	}
	
	private String getClan() {
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
		return " <" + guildUser.getGuildName() + ">";
	}

	public void enviarCC() {
		sendPacket(createCC());
	}

	public CharacterCreateResponse createCC() {
		return new CharacterCreateResponse(
				getId(), 
				m_infoChar.getCuerpo(), 
				m_infoChar.getCabeza(), 
				m_infoChar.getDir(),
				
				pos().x, pos().y, 
				m_infoChar.getArma(), 
				m_infoChar.getEscudo(), 
				m_infoChar.getCasco(),
				m_infoChar.getFX(), 
				(short) m_infoChar.m_loops,
				m_nick + getClan(),
				(byte) (this.isCriminal() ? 1 : 0),
				(byte)flags().Privilegios);
	}

	private void enviarLogged() {
		sendPacket(new LoggedMessageResponse());
	}

	public void enviarObjeto(int objId, int x, int y) {
		short grhIndex = findObj(objId).GrhIndex;

		sendPacket(new ObjectCreateResponse((byte)x, (byte)y, (short)grhIndex));
	}

	public void enviarBQ(int x, int y, boolean bloqueado) {
		byte bq = 0;
		if (bloqueado)
			bq = 1;
		sendPacket(new BlockPositionResponse((byte)x, (byte)y, (byte)bq));
	}

	public void cuerpoDesnudo() {
		if (this.flags().Mimetizado) {
			mimetizadoChar.cuerpoDesnudo(race, gender);
		} else {
			m_infoChar.cuerpoDesnudo(race, gender);
		}
		flags().Desnudo = true;
	}

	public void quitarMascota(Npc npc) {
		if ( !getUserPets().hasPets()) {
			return;
		}
		
		getUserPets().removePet(npc);
	}

	public void enviarSonido(int sonido) {
		Map mapa = server.getMap(pos().map);
		// Sonido
		if (mapa != null) {
			mapa.enviarAlArea(pos().x, pos().y, new PlayWaveResponse((byte) sonido, pos().x, pos().y));
		}
	}

	public void userDie() {
		if (isGM()) {
			return;
		}
		Map mapa = server.getMap(pos().map);
		// Sonido
		enviarSonido(SND_USERMUERTE);
		// Quitar el dialogo del usuario muerto
		// mapa.enviarAlArea(pos().x, pos().y, MSG_QDL, m_id);
		stats().MinHP = 0;
		stats().stamina = 0;
		flags().AtacadoPorNpc = 0;
		flags().AtacadoPorUser = 0;
		flags().Envenenado = false;
		flags().Muerto = true;
		if (flags().AtacadoPorNpc > 0) {
			Npc npc = server.npcById(flags().AtacadoPorNpc);
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
			// enviar(MSG_DOK);
		}
		// <<<< Meditando >>>>
		if (flags().Meditando) {
			flags().Meditando = false;
			sendPacket(new MeditateToggleResponse());
		}
		// desequipar armadura
		if (m_inv.tieneArmaduraEquipada()) {
			m_inv.desequiparArmadura();
		}
		// desequipar arma
		if (m_inv.tieneArmaEquipada()) {
			m_inv.desequiparArma();
		}
		// desequipar casco
		if (m_inv.tieneCascoEquipado()) {
			m_inv.desequiparCasco();
		}
		// desequipar municiones
		if (m_inv.tieneMunicionEquipada()) {
			m_inv.desequiparMunicion();
		}
		// << Si es zona de trigger 6, no pierde el inventario >>
		if (mapa.getTrigger(pos().x, pos().y) != MapCell.TRIGGER6_PERMITE) {
			// << Si es newbie no pierde el inventario >>
			if (!esNewbie() || isCriminal()) {
				tirarTodo();
			} else {
				tirarTodosLosItemsNoNewbies();
			}
		}
		// << Reseteamos los posibles FX sobre el personaje >>
		if (m_infoChar.m_loops == LoopAdEternum) {
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
		}
		// << Cambiamos la apariencia del char >>
		if (!flags().Navegando) {
			m_infoChar.m_cuerpo = OBJ_INDEX_CUERPO_MUERTO;
			m_infoChar.m_cabeza = OBJ_INDEX_CABEZA_MUERTO;
			m_infoChar.m_escudo = NingunEscudo;
			m_infoChar.m_arma = NingunArma;
			m_infoChar.m_casco = NingunCasco;
		} else {
			m_infoChar.m_cuerpo = OBJ_INDEX_FRAGATA_FANTASMAL;
		}
		
		getUserPets().removeAll();
		sendCharacterChange();
		sendUpdateUserStats();
	}

	public void tirarTodo() {
		Map mapa = server.getMap(pos().map);
		if (mapa.getTrigger(pos().x, pos().y) == MapCell.TRIGGER_ARENA_DUELOS || isGM()) {
			return;
		}
		tirarTodosLosItems();
		tirarOro(stats().getGold());
	}

	private void tirarTodosLosItems() {
		Map m = server.getMap(pos().map);
		for (int i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i) != null && m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = findObj(m_inv.getObjeto(i).objid);
				if (info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(m_inv.getObjeto(i).objid, m_inv.getObjeto(i).cant);
					m_inv.quitarUserInvItem(i, obj_inv.cant);
					enviarObjetoInventario(i);
					if (info_obj.itemSeCae()) {
						m.tirarItemAlPiso(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarTodosLosItemsNoNewbies() {
		Map m = server.getMap(pos().map);
		for (int i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = findObj(m_inv.getObjeto(i).objid);
				if (!info_obj.esNewbie() && info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(m_inv.getObjeto(i).objid, m_inv.getObjeto(i).cant);
					m_inv.quitarUserInvItem(i, obj_inv.cant);
					enviarObjetoInventario(i);
					if (info_obj.itemSeCae()) {
						m.tirarItemAlPiso(pos().x, pos().y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarOro(int cantidad) {
		if (cantidad > MAX_INVENTORY_OBJS) {
			return;
		}
		Map m = server.getMap(pos().map);
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
					Log.logGM(m_nick,
							"Tiró " + oi.cant + " unidades del objeto " + findObj(oi.objid).Nombre);
				}
				m.tirarItemAlPiso(pos().x, pos().y, oi);
			}
		}
	}

	public void volverCriminal() {
		Map mapa = server.getMap(pos().map);
		if (mapa.getTrigger(pos().x, pos().y) == MapCell.TRIGGER_ARENA_DUELOS) {
			return;
		}
		if (flags().Privilegios < 2) {
			if (!isCriminal()) {
				m_reputacion.condenar();
				if (m_faccion.ArmadaReal) {
					m_faccion.expulsarFaccionReal();
				}
				refreshUpdateTagAndStatus();
				System.out.println(this.getNick() + " ahora es criminal");
			}
		}
	}

	public void subirSkill(int skill) {
		if (!flags().Hambre && !flags().Sed) {
			int prob = 0;
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

			int aumenta = Util.Azar(1, prob);

			int lvl = stats().ELV;
			// FIXME
			// if (lvl > MAX_SKILLS)
			// return;

			if (stats().userSkills(skill) >= Skill.MAX_SKILL_POINTS) {
				return;
			}
			if (aumenta == 7 && stats().userSkills(skill) < Skill.levelSkill[lvl]) {
				stats().addSkillPoints(skill, (byte) 1);
				enviarMensaje("¡Has mejorado tu skill " + Skill.skillsNames[skill] + " en un punto!. Ahora tienes "
						+ stats().userSkills(skill) + " pts.", FontType.FONTTYPE_INFO);
				stats().addExp((byte) 50);
				enviarMensaje("¡Has ganado 50 puntos de experiencia!", FONTTYPE_FIGHT);
				checkUserLevel();
			}
		}
	}

	public void userAsignaSkill(int skill, int amount) {
		if (stats().userSkills(skill) >= Skill.MAX_SKILL_POINTS 
				|| stats().userSkills(skill) + (amount) > 100
				|| stats().SkillPts < amount) {
			return;
		}

		stats().addSkillPoints(skill, (byte) amount);
		stats().SkillPts -= amount;
	}

	public boolean esNewbie() {
		return stats().ELV <= LimiteNewbie;
	}
	
	public boolean esArmada() {
		return m_faccion.ArmadaReal;
	}

	public boolean esCaos() {
		return m_faccion.FuerzasCaos;
	}

	public void sendSkills() {
		// Comando ESKI
		sendPacket(new SendSkillsResponse(stats().skills()));
	}

	public void enviarSubirNivel(short skillPoints) {
		sendPacket(new LevelUpResponse(skillPoints));
	}

	public void envenenar() {
		flags().Envenenado = true;
		enviarMensaje("¡¡La criatura te ha envenenado!!", FONTTYPE_FIGHT);
	}

	public void modificarSkills(String s) {
		// Comando "SKSE" Modificar skills
		StringTokenizer st = new StringTokenizer(s, ",");
		byte skills[] = new byte[Skill.MAX_SKILLS + 1];
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
	}

	public void checkUserLevel() {
		// ¿Alcanzo el maximo nivel?
		if (stats().ELV == STAT_MAXELV) {
			stats().Exp = 0;
			stats().ELU = 0;
			return;
		}
		while (stats().Exp >= stats().ELU) {
			// La exp alcanzó el máximo del nivel, entonces sube de nivel.
			boolean wasNewbie = esNewbie();
			enviarSonido(SOUND_NIVEL);
			enviarMensaje("¡Has subido de nivel!", FontType.FONTTYPE_INFO);
			short skillPoints = (short) ((stats().ELV == 1) ? 10 : 5);
			stats().SkillPts += skillPoints;
			enviarMensaje("Has ganado " + skillPoints + " skillpoints.", FontType.FONTTYPE_INFO);
			stats().ELV++;
			stats().Exp -= stats().ELU;
			if (wasNewbie && !esNewbie()) {
				m_inv.quitarObjsNewbie();
				salirDeDN();
			}
			stats().ELU *= (stats().ELV < 11) ? 1.5 : ((stats().ELV < 25) ? 1.3 : 1.2);
			clazz.clazz().subirEstads(this);
			sendSkills();
			enviarSubirNivel(skillPoints);
			sendUpdateUserStats();
		}
	}

	private void salirDeDN() {
		// Si el usuario dejó de ser Newbie, y estaba en el Dungeon Newbie
		// es transportado a su hogar de origen.
		if (pos().map == DUNGEON_NEWBIE_MAP) {
			MapPos ciudad = server.getCiudadPos(m_hogar);
			warpUser(ciudad.map, ciudad.x, ciudad.y, true);
		}
	}

	public long poderEvasionEscudo() {
		return (long) ((stats().userSkills(Skill.SKILL_Defensa) * clazz.clazz().modEvasionDeEscudoClase()) / 2);
	}

	public double poderEvasion() {
		double tmp = 0;
		if (stats().userSkills(Skill.SKILL_Tacticas) < 31) {
			tmp = stats().userSkills(Skill.SKILL_Tacticas) * clazz.clazz().modificadorEvasion();
		} else if (stats().userSkills(Skill.SKILL_Tacticas) < 61) {
			tmp = (stats().userSkills(Skill.SKILL_Tacticas) + stats().userAttributes[ATRIB_AGILIDAD])
					* clazz.clazz().modificadorEvasion();
		} else if (stats().userSkills(Skill.SKILL_Tacticas) < 91) {
			tmp = (stats().userSkills(Skill.SKILL_Tacticas) + (2 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorEvasion();
		} else {
			tmp = (stats().userSkills(Skill.SKILL_Tacticas) + (3 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorEvasion();
		}
		return (tmp + (2.5 * Util.Max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueArma() {
		double tmp = 0;
		if (stats().userSkills(Skill.SKILL_Armas) < 31) {
			tmp = stats().userSkills(Skill.SKILL_Armas) * clazz.clazz().modificadorPoderAtaqueArmas();
		} else if (stats().userSkills(Skill.SKILL_Armas) < 61) {
			tmp = ((stats().userSkills(Skill.SKILL_Armas) + stats().userAttributes[ATRIB_AGILIDAD])
					* clazz.clazz().modificadorPoderAtaqueArmas());
		} else if (stats().userSkills(Skill.SKILL_Armas) < 91) {
			tmp = ((stats().userSkills(Skill.SKILL_Armas) + (2 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueArmas());
		} else {
			tmp = ((stats().userSkills(Skill.SKILL_Armas) + (3 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueArmas());
		}
		return tmp + (2.5 * Util.Max(stats().ELV - 12, 0));
	}

	public double poderAtaqueProyectil() {
		double tmp = 0;
		if (stats().userSkills(Skill.SKILL_Proyectiles) < 31) {
			tmp = (stats().userSkills(Skill.SKILL_Proyectiles) * clazz.clazz().modificadorPoderAtaqueProyectiles());
		} else if (stats().userSkills(Skill.SKILL_Proyectiles) < 61) {
			tmp = ((stats().userSkills(Skill.SKILL_Proyectiles) + stats().userAttributes[ATRIB_AGILIDAD])
					* clazz.clazz().modificadorPoderAtaqueProyectiles());
		} else if (stats().userSkills(Skill.SKILL_Proyectiles) < 91) {
			tmp = ((stats().userSkills(Skill.SKILL_Proyectiles) + (2 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueProyectiles());
		} else {
			tmp = ((stats().userSkills(Skill.SKILL_Proyectiles) + (3 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueProyectiles());
		}
		return (tmp + (2.5 * Util.Max(stats().ELV - 12, 0)));
	}

	public double poderAtaqueWresterling() {
		double tmp = 0;
		if (stats().userSkills(Skill.SKILL_Wresterling) < 31) {
			tmp = (stats().userSkills(Skill.SKILL_Wresterling) * clazz.clazz().modificadorPoderAtaqueArmas());
		} else if (stats().userSkills(Skill.SKILL_Wresterling) < 61) {
			tmp = (stats().userSkills(Skill.SKILL_Wresterling) + stats().userAttributes[ATRIB_AGILIDAD])
					* clazz.clazz().modificadorPoderAtaqueArmas();
		} else if (stats().userSkills(Skill.SKILL_Wresterling) < 91) {
			tmp = (stats().userSkills(Skill.SKILL_Wresterling) + (2 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueArmas();
		} else {
			tmp = (stats().userSkills(Skill.SKILL_Wresterling) + (3 * stats().userAttributes[ATRIB_AGILIDAD]))
					* clazz.clazz().modificadorPoderAtaqueArmas();
		}
		return tmp + (2.5 * Util.Max(stats().ELV - 12, 0));
	}

	public boolean userImpactoNpc(Npc npc) {
		double poderAtaque = 0;
		if (m_inv.tieneArmaEquipada()) {
			// Usando un arma
			if (m_inv.getArma().esProyectil()) {
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
			if (m_inv.tieneArmaEquipada()) {
				if (m_inv.getArma().esProyectil()) {
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
		long skillTacticas = stats().userSkills(Skill.SKILL_Tacticas);
		long skillDefensa = stats().userSkills(Skill.SKILL_Defensa);
		// Esta usando un escudo ???
		if (m_inv.tieneEscudoEquipado()) {
			userEvasion += poderEvasionEscudo();
		}
		double probExito = Util.Max(10, Util.Min(90, 50 + ((npcPoderAtaque - userEvasion) * 0.4)));
		boolean impacto = (Util.Azar(1, 100) <= probExito);
		// ¿El usuario esta usando un escudo ???
		if (!impacto && m_inv.tieneEscudoEquipado()) {
			double probRechazo = Util.Max(10, Util.Min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
			boolean rechazo = (Util.Azar(1, 100) <= probRechazo);
			if (rechazo) {
				// Se rechazo el ataque con el escudo
				enviarSonido(SND_ESCUDO);
				// enviar(MSG_7);
				subirSkill(Skill.SKILL_Defensa);
			}
		}
		return impacto;
	}

	public int calcularDaño(Npc npc) {
		long dañoArma = 0;
		long dañoMaxArma = 0;
		long dañoUsuario = 0;
		double modifClase = 0;
		if (m_inv.tieneArmaEquipada()) {
			ObjectInfo arma = m_inv.getArma();
			// Ataca a un npc?
			if (npc != null) {
				// Usa la mata dragones?
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) { // Usa la
					// matadragones?
					modifClase = clazz.clazz().modicadorDañoClaseArmas();
					if (npc.getNPCtype() == Npc.NPCTYPE_DRAGON) { // Ataca
						// dragon?
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					} else { // Sino es dragon daño es 1
						dañoArma = 1;
						dañoMaxArma = 1;
					}
				} else { // daño comun
					if (arma.esProyectil()) {
						modifClase = clazz.clazz().modicadorDañoClaseProyectiles();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = m_inv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = clazz.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			} else { // Ataca usuario
				if (arma.ObjIndex == OBJ_INDEX_ESPADA_MATA_DRAGONES) {
					modifClase = clazz.clazz().modicadorDañoClaseArmas();
					dañoArma = 1; // Si usa la espada matadragones daño es 1
					dañoMaxArma = 1;
				} else {
					if (arma.esProyectil()) {
						modifClase = clazz.clazz().modicadorDañoClaseProyectiles();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = m_inv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = clazz.clazz().modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			}
		}
		dañoUsuario = Util.Azar(stats().MinHIT, stats().MaxHIT);
		double daño = (((3 * dañoArma) + ((dañoMaxArma / 5) * Util.Max(0, (stats().userAttributes[ATRIB_FUERZA] - 15)))
				+ dañoUsuario) * modifClase);
		return (int) daño;
	}

	public void userDañoNpc(Npc npc) {
		int daño = calcularDaño(npc);
		// esta navegando? si es asi le sumamos el daño del barco
		if (flags().Navegando) {
			daño += Util.Azar(m_inv.getBarco().MinHIT, m_inv.getBarco().MaxHIT);
		}
		daño -= npc.stats().Def;
		if (daño < 0) {
			daño = 0;
		}
		npc.stats().MinHP -= daño;
		if (daño > 0) {
			sendPacket(new UserHitNPCResponse(daño));
			npc.calcularDarExp(this, daño);
		} else {
			enviarSonido(SOUND_SWING);
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
			if (npc.getNPCtype() == Npc.NPCTYPE_DRAGON) {
				quitarObjetos(OBJ_INDEX_ESPADA_MATA_DRAGONES, 1);
			}
			getUserPets().petsFollowMaster();

			npc.muereNpc(this);
		}
	}

	public boolean puedeApuñalar() {
		if (m_inv.tieneArmaEquipada()) {
			return ((stats().userSkills(Skill.SKILL_Apuñalar) >= MIN_APUÑALAR) && m_inv.getArma().apuñala())
					|| ((clazz == Clazz.Assassin) && m_inv.getArma().apuñala());
		}
		return false;
	}

	public void quitarObjetos(int objIdx, int cant) {
		if (cant <= 0) {
			return;
		}
		int cant_quitar;
		for (int i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i).objid == objIdx) {
				cant_quitar = m_inv.getObjeto(i).cant < cant ? m_inv.getObjeto(i).cant : cant;
				cant -= cant_quitar;
				m_inv.desequipar(i);
				m_inv.quitarUserInvItem(i, cant_quitar);
				enviarObjetoInventario(i);
				if (cant <= 0) {
					break; // Terminar.
				}
			}
		}
	}

	public void npcDaño(Npc npc) {
		int daño = Util.Azar(npc.stats().MinHIT, npc.stats().MaxHIT);
		int defbarco = 0;
		if (isSailing()) {
			ObjectInfo barco = m_inv.getBarco();
			defbarco = Util.Azar(barco.MinDef, barco.MaxDef);
		}
		byte lugar = (byte)Util.Azar(1, 6);
		switch (lugar) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (m_inv.tieneCascoEquipado()) {
				ObjectInfo casco = m_inv.getCasco();
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
			if (m_inv.tieneArmaduraEquipada()) {
				ObjectInfo armadura = m_inv.getArmadura();
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
			// enviar(MSG_6); // Le informamos que ha muerto ;)
			// Si lo mato un guardia
			if (isCriminal() && npc.isNpcGuard()) {
				m_reputacion.decAsesino(vlAsesino / 4);
				m_reputacion.decBandido(vlAsalto / 4);
				m_reputacion.decLadron(vlCazador / 3);
			}
			if (npc.getPetUserOwner() != null) {
				npc.getPetUserOwner().getUserPets().petsFollowMaster();
			} else {
				// Al matarlo no lo sigue mas
				if (npc.stats().Alineacion == 0) {
					npc.restoreOldMovement();
				}
			}
			userDie();
		}
	}

	public void usuarioAtacaNpc(Npc npc) {
		if (pos().distance(npc.pos()) > MAX_DISTANCIA_ARCO) {
			enviarMensaje("Estás muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		if (m_faccion.ArmadaReal && npc.getPetUserOwner() != null && !npc.getPetUserOwner().isCriminal()) {
			enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.getPetUserOwner() != null && flags().Seguro && !npc.getPetUserOwner().isCriminal()) {
			enviarMensaje("Debes quitar el seguro para atacar a una mascota de un ciudadano.", FontType.FONTTYPE_WARNING);
			return;
		}
		if (npc.stats().Alineacion == 0 && flags().Seguro) {
			enviarMensaje("Debes quitar el seguro para atacar a una criatura no hostil.", FontType.FONTTYPE_WARNING);
			return;
		}
		npcAtacado(npc);
		if (userImpactoNpc(npc)) {
			if (npc.getSonidoAtaqueExitoso() > 0) {
				enviarSonido(npc.getSonidoAtaqueExitoso());
			} else {
				enviarSonido(SND_IMPACTO2);
			}
			userDañoNpc(npc);
		} else {
			enviarSonido(SOUND_SWING);
			sendPacket(new UserSwingResponse());
		}
	}

	public void safeToggle() {
		flags().Seguro = !flags().Seguro;
	}

	public void cambiarModoCombate() {
		// Entrar o salir modo combate
		if (flags().ModoCombate) {
			enviarMensaje("Has salido del modo de combate.", FontType.FONTTYPE_INFO);
		} else {
			enviarMensaje("Has pasado al modo de combate.", FontType.FONTTYPE_INFO);
		}
		flags().ModoCombate = !flags().ModoCombate;
	}

	public void doONLINE() {
		// Comando /ONLINE
		// Muestra los usuarios conectados.
		StringBuffer msg = new StringBuffer();
		int cant = 0;
		for (Object element : server.getUsuariosConectados()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append((String) element);
		}
		enviarMensaje(msg.toString(), FontType.FONTTYPE_INFO);
		enviarMensaje("Cantidad de usuarios: " + cant, FontType.FONTTYPE_INFO);
	}

	public void usuarioAtaca() {
		if (counters().intervaloPermiteAtacar()) {
			// Pierde stamina
			if (stats().stamina >= 10) {
				// stats().quitarStamina(Util.Azar(1, 10));
			} else {
				enviarMensaje("Estas muy cansado para luchar.", FontType.FONTTYPE_INFO);
				return;
			}
			MapPos attackPos = pos().copy();
			attackPos.moveToHeading(Heading.value(m_infoChar.getDir()));
			// Exit if not legal
			if (!attackPos.isValid()) {
				enviarSonido(SOUND_SWING);
				return;
			}
			Map mapa = server.getMap(pos().map);
			Player cliente = mapa.getCliente(attackPos.x, attackPos.y);
			// Look for user
			if (cliente != null) {
				usuarioAtacaUsuario(cliente);
				sendUpdateUserStats();
				cliente.sendUpdateUserStats();
				return;
			}
			// Look for Npc
			Npc npc = mapa.getNPC(attackPos.x, attackPos.y);
			if (npc != null) {
				if (npc.getAttackable()) {
					if (npc.getPetUserOwner() != null && mapa.esZonaSegura()) {
						enviarMensaje("No podés atacar mascotas en zonas seguras", FONTTYPE_FIGHT);
						return;
					}
					usuarioAtacaNpc(npc);
				} else {
					enviarMensaje("No podés atacar a este Npc", FONTTYPE_FIGHT);
				}
				sendUpdateUserStats();
				return;
			}
			enviarSonido(SOUND_SWING);
			sendUpdateUserStats();
			flags().Trabajando = false;
		} else {
			log.info("NO PUEDE ATACAR");
		}
	}

	public boolean usuarioImpacto(Player victima) {
		double probExito = 0;
		long skillTacticas = victima.stats().userSkills(Skill.SKILL_Tacticas);
		long skillDefensa = victima.stats().userSkills(Skill.SKILL_Defensa);
		ObjectInfo arma = m_inv.getArma();
		boolean proyectil = (arma != null) && arma.esProyectil();
		// Calculamos el poder de evasion...
		double userPoderEvasion = victima.poderEvasion();
		double userPoderEvasionEscudo = 0;
		if (victima.m_inv.tieneEscudoEquipado()) {
			userPoderEvasionEscudo = victima.poderEvasionEscudo();
			userPoderEvasion += userPoderEvasionEscudo;
		}
		// El atacante esta usando un arma ???
		double poderAtaque = 0;
		if (m_inv.tieneArmaEquipada()) {
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
		if (victima.m_inv.tieneEscudoEquipado()) {
			// Fallo ???
			if (!huboImpacto) {
				double probRechazo = Util.Max(10, Util.Min(90, 100 * (skillDefensa / (skillDefensa + skillTacticas))));
				boolean huboRechazo = (Util.Azar(1, 100) <= probRechazo);
				if (huboRechazo) {
					// Se rechazo el ataque con el escudo!
					enviarSonido(SND_ESCUDO);
					// enviar(MSG_8);
					// victima.enviar(MSG_7);
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
			enviarMensaje("Estás muy lejos para disparar.", FONTTYPE_FIGHT);
			return;
		}
		usuarioAtacadoPorUsuario(victima);
		if (usuarioImpacto(victima)) {
			enviarSonido(SND_IMPACTO);
			if (!victima.isSailing()) {
				victima.enviarCFX(FXSANGRE, 0);
			}
			userDañoUser(victima);
		} else {
			enviarSonido(SOUND_SWING);
			// FIXME
			// enviar(MSG_U1);
			// victima.enviar(MSG_U3, m_nick);
		}
	}

	public void userDañoUser(Player victima) {
		int daño = calcularDaño(null);
		int defbarco = 0;
		envenenarUsuario(victima); // revisar... FIXME
		if (isSailing()) {
			ObjectInfo barco = m_inv.getBarco();
			daño += Util.Azar(barco.MinHIT, barco.MaxHIT);
		}
		if (victima.isSailing()) {
			ObjectInfo barco = victima.m_inv.getBarco();
			defbarco = Util.Azar(barco.MinDef, barco.MaxDef);
		}
		int lugar = Util.Azar(1, 6);
		switch (lugar) {
		case bCabeza:
			// Si tiene casco absorbe el golpe
			if (victima.m_inv.tieneCascoEquipado()) {
				ObjectInfo casco = victima.m_inv.getCasco();
				int absorbido = Util.Azar(casco.MinDef, casco.MaxDef);
				absorbido += defbarco;
				daño -= absorbido;
				if (daño < 0) {
					daño = 1;
				}
			}
			break;
		default:
			// Si tiene armadura absorbe el golpe
			if (victima.m_inv.tieneEscudoEquipado()) {
				ObjectInfo escudo = victima.m_inv.getEscudo();
				int absorbido = Util.Azar(escudo.MinDef, escudo.MaxDef);
				absorbido += defbarco;
				daño -= absorbido;
				if (daño < 0) {
					daño = 1;
				}
			}
		}
		// enviar(MSG_N5, lugar, daño, victima.getNick());
		// victima.enviar(MSG_N4, lugar, daño, getNick());
		victima.stats().quitarHP(daño);
		if (!flags().Hambre && !flags().Sed) {
			if (m_inv.tieneArmaEquipada()) {
				// Si usa un arma quizas suba "Combate con armas"
				subirSkill(Skill.SKILL_Armas);
			} else {
				// sino tal vez lucha libre
				subirSkill(Skill.SKILL_Wresterling);
			}
			subirSkill(Skill.SKILL_Tacticas);
			// Trata de apuñalar por la espalda al enemigo
			if (puedeApuñalar()) {
				apuñalar(victima, daño);
				subirSkill(Skill.SKILL_Apuñalar);
			}
		}
		if (victima.stats().MinHP <= 0) {
			contarMuerte(victima);
			getUserPets().petsFollowMaster(victima.getId());
			actStats(victima);
		}
		// Controla el nivel del usuario
		checkUserLevel();
	}

	public void actStats(Player victima) {
		int daExp = victima.stats().ELV * 2;
		stats().addExp(daExp);
		// Lo mata
		this.enviarMensaje("Has matado " + victima.m_nick + "!", FONTTYPE_FIGHT);
		enviarMensaje("Has ganado " + daExp + " puntos de experiencia.", FONTTYPE_FIGHT);
		victima.enviarMensaje(m_nick + " te ha matado!", FONTTYPE_FIGHT);
		if (triggerZonaPelea(victima) != MapCell.TRIGGER6_PERMITE) {
			if (!victima.isCriminal()) {
				m_reputacion.incAsesino(vlAsesino * 2);
				m_reputacion.burguesRep = 0;
				m_reputacion.nobleRep = 0;
				m_reputacion.plebeRep = 0;
			} else {
				m_reputacion.incNoble(vlNoble);
			}
		}
		victima.userDie();
		stats().incUsuariosMatados();
		log.info("ASESINATO: " + m_nick + " asesino a " + victima.m_nick);
	}

	public void contarMuerte(Player usuarioMuerto) {
		if (usuarioMuerto.esNewbie()) {
			return;
		}
		if (triggerZonaPelea(usuarioMuerto) == MapCell.TRIGGER6_PERMITE) {
			return;
		}
		
		// TODO move this to UserFaction
		if (usuarioMuerto.isCriminal()) {
			if (!flags().LastCrimMatado.equalsIgnoreCase(usuarioMuerto.m_nick)) {
				flags().LastCrimMatado = usuarioMuerto.m_nick;
				m_faccion.CriminalesMatados++;
			}
			if (m_faccion.CriminalesMatados > MAXUSERMATADOS) {
				m_faccion.CriminalesMatados = 0;
				m_faccion.RecompensasReal = 0;
			}
			
		} else {
			if (!flags().LastCiudMatado.equalsIgnoreCase(usuarioMuerto.m_nick)) {
				flags().LastCiudMatado = usuarioMuerto.m_nick;
				m_faccion.CiudadanosMatados++;
			}
			if (m_faccion.CiudadanosMatados > MAXUSERMATADOS) {
				m_faccion.CiudadanosMatados = 0;
				m_faccion.RecompensasCaos = 0;
			}
		}
	}

	public void usuarioAtacadoPorUsuario(Player victima) {
		if (triggerZonaPelea(victima) == MapCell.TRIGGER6_PERMITE) {
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
			m_reputacion.incNoble(vlNoble);
		} else {
			m_reputacion.incBandido(vlAsalto);
		}
		
		allPetsAttackUser(victima);
		victima.allPetsAttackUser(this);
	}

	public void allPetsAttackUser(Player objetivo) {
		getUserPets().getPets().forEach(pet -> {
			pet.setAttackedBy(objetivo.getNick());
			pet.defenderse();
		});
	}

	public boolean puedeAtacar(Player victima) {
		Map mapa = server.getMap(victima.pos().map);

		if (!victima.isAlive()) {
			enviarMensaje("No puedes atacar a un espíritu", FontType.FONTTYPE_INFO);
			return false;
		}
		int t = triggerZonaPelea(victima);
		if (t == MapCell.TRIGGER6_PERMITE) {
			return true;
		} else if (t == MapCell.TRIGGER6_PROHIBE) {
			return false;
		}
		if (mapa.esZonaSegura()) {
			enviarMensaje("Esta es una zona segura, aqui no puedes atacar usuarios.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (mapa.getTrigger(victima.pos().x, victima.pos().y) == 4) {
			enviarMensaje("No puedes pelear aqui.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isCriminal() && m_faccion.ArmadaReal) {
			enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar ciudadanos.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (victima.m_faccion.FuerzasCaos && m_faccion.FuerzasCaos) {
			enviarMensaje("Los seguidores de las Fuerzas del Caos tienen prohibido atacarse entre sí.",
					FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!checkAlive("No puedes atacar porque estas muerto.")) {
			return false;
		}
		// Se asegura que la victima no es un GM
		if (victima.isGM()) {
			enviarMensaje("¡¡No puedes atacar a los administradores del juego!!", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (!victima.isAlive()) {
			enviarMensaje("No puedes atacar a un espíritu.", FontType.FONTTYPE_WARNING);
			return false;
		}
		if (flags().Seguro) {
			if (!victima.isCriminal()) {
				enviarMensaje(
						"No puedes atacar ciudadanos, para hacerlo debes desactivar el seguro apretando la tecla S.",
						FONTTYPE_FIGHT);
				return false;
			}
		}
		// Implementacion de trigger 7 - Para torneos con espectadores
		if (mapa.getTrigger(pos().x, pos().y) == 7) {
			if (mapa.getTrigger(pos().x, pos().y) == 7
					&& mapa.getTrigger(victima.pos().x, victima.pos().y) != 7) {
				enviarMensaje("Para atacar a ese usuario, él se debe encontrar en tu misma zona.", FONTTYPE_FIGHT);
			} else if (mapa.getTrigger(pos().x, pos().y) != 7
					&& mapa.getTrigger(victima.pos().x, victima.pos().y) == 7) {
				enviarMensaje("Para atacar a ese usuario, debes encontrarte en la misma zona que él.", FONTTYPE_FIGHT);
			}
			return false;
		}
		return true;
	}

	private boolean suerteApuñalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Apuñalar) / 10)]);
		return (Util.Azar(1, rango) == 3);
	}

	public void apuñalar(Npc victimaNPC, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 2;
			victimaNPC.stats().MinHP -= daño;
			enviarMensaje("Has apuñalado a la criatura por " + daño, FONTTYPE_FIGHT);
			subirSkill(Skill.SKILL_Apuñalar);
			victimaNPC.calcularDarExp(this, daño);
		} else {
			enviarMensaje("¡No has logrado apuñalar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void apuñalar(Player victimaUsuario, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 1.5;
			victimaUsuario.stats().MinHP -= daño;
			enviarMensaje("Has apuñalado a " + victimaUsuario.m_nick + " por " + daño, FONTTYPE_FIGHT);
			victimaUsuario.enviarMensaje("Te ha apuñalado " + m_nick + " por " + daño, FONTTYPE_FIGHT);
		} else {
			enviarMensaje("¡No has logrado apuñalar a tu enemigo!", FONTTYPE_FIGHT);
		}
	}

	public void npcAtacado(Npc npc) {
		// Guardamos el usuario que ataco el npc
		npc.setAttackedBy(m_nick);
		if (npc.getPetUserOwner() != null) {
			npc.getPetUserOwner().allPetsAttackUser(this);
		}
		if (esMascotaCiudadano(npc)) {
			volverCriminal();
			npc.defenderse();
		} else {
			// Reputacion
			if (npc.stats().Alineacion == 0 && npc.getPetUserOwner() == null) {
				if (npc.getNPCtype() == Npc.NPCTYPE_GUARDIAS) {
					volverCriminal();
				} else {
					m_reputacion.incBandido(vlAsalto);
				}
			} else if (npc.stats().Alineacion == 1) {
				m_reputacion.incPlebe(vlCazador / 2);
			}
			// hacemos que el npc se defienda
			npc.defenderse();
			getUserPets().petsAttackNpc(npc);
		}
	}

	public boolean esMascotaCiudadano(Npc npc) {
		if (npc.getPetUserOwner() != null) {
			if (!npc.getPetUserOwner().isCriminal()) {
				npc.getPetUserOwner().enviarMensaje("¡¡" + m_nick + " esta atacando a tu mascota!!", FONTTYPE_FIGHT);
				return true;
			}
		}
		return false;
	}

	public void connectNewUser(String nick, String clave, byte raza, byte genero, byte clazz, String email, byte hogar) {
		// Validar los datos recibidos :-)
		this.m_nick = nick;
		if (!server.getAdmins().nombrePermitido(m_nick)) {
			enviarError("Los nombres de los personajes deben pertencer a la fantasia, el nombre indicado es invalido.");
			return;
		}
		if (!Util.asciiValidos(m_nick)) {
			enviarError("Nombre invalido.");
			return;
		}
		if (existePersonaje()) {
			enviarError("Ya existe el personaje.");
			return;
		}
		this.flags().Muerto = false;
		this.flags().Escondido = false;
		this.m_reputacion.asesinoRep = 0;
		this.m_reputacion.bandidoRep = 0;
		this.m_reputacion.burguesRep = 0;
		this.m_reputacion.ladronRep = 0;
		this.m_reputacion.nobleRep = 1000;
		this.m_reputacion.plebeRep = 30;
		this.clazz = Clazz.value(clazz);
		this.race = UserRace.value(raza);
		this.gender = UserGender.value(genero);
		this.m_email = email;
		this.m_hogar = hogar;
		// FIXME
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		// if (!atributosValidos()) {
		// enviarError("Atributos invalidos.");
		// return;
		// }
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		initAttributes(raza);
		this.stats().SkillPts = 10;

		this.m_password = clave;
		this.m_infoChar.setDir(Heading.SOUTH);
		this.m_infoChar.cuerpoYCabeza(race(), gender());
		this.m_infoChar.m_arma = NingunArma;
		this.m_infoChar.m_escudo = NingunEscudo;
		this.m_infoChar.m_casco = NingunCasco;
		this.m_origChar = new CharInfo(m_infoChar);

		this.stats().inicializarEstads(this.clazz);
		
		// Inicializar hechizos:
		if (this.clazz.clazz().esMagica()) {
			m_spells.setSpell(1, HECHIZO_DARDO_MAGICO);
		}
		
		// ???????????????? INVENTARIO ¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿
		this.m_inv.clear();
		this.m_inv.setObjeto(1, new InventoryObject(MANZANA_ROJA_NEWBIES, 100, false));
		this.m_inv.setObjeto(2, new InventoryObject(BOTELLA_AGUA_NEWBIES, 100, false));
		this.m_inv.setArma(3, new InventoryObject(DAGA_NEWBIES, 1, true));
		
		switch (race()) {
		case RAZA_HUMANO:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_1, 1, true));
			break;
		case RAZA_ELFO:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_2, 1, true));
			break;
		case RAZA_DROW:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_3, 1, true));
			break;
		case RAZA_ENANO:
			m_inv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		case RAZA_GNOMO:
			m_inv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		}

		userStorage.saveUserToStorage();
		connectUser(nick, clave);
	}

	static String getPjFile(String nick) {
		// FIXME
		return Constants.CHARFILES_FOLDER + java.io.File.separator + nick.toLowerCase() + ".chr";
	}

	public boolean existePersonaje() {
		// TODO revisar para qué y cuándo se usa esto... está raro como chequeo de consistencia -gorlok
		return Util.existeArchivo(getPjFile(m_nick));
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

	public void initAttributes(int raza) {
		stats().userAttributes[ATRIB_FUERZA] 		+= race().modificadorFuerza();
		stats().userAttributes[ATRIB_AGILIDAD] 		+= race().modificadorAgilidad();
		stats().userAttributes[ATRIB_INTELIGENCIA] 	+= race().modificadorInteligencia();
		stats().userAttributes[ATRIB_CARISMA] 		+= race().modificadorCarisma();
		stats().userAttributes[ATRIB_CONSTITUCION] 	+= race().modificadorConstitucion();
	}

	public void enviarError(String msg) {
		log.warn("ERROR: " + msg);
		sendPacket(new ErrorMsgResponse(msg));
	}

	public void connectUser(String nick, String passwd) {
		m_nick = nick;
		m_password = passwd;
		try {
			// ¿Existe el personaje?
			if (!existePersonaje()) {
				enviarError("El personaje no existe. Compruebe el nombre de usuario.");
				return;
			}

			// Reseteamos los FLAGS
			flags().Escondido = false;
			flags().TargetNpc = 0;
			flags().TargetObj = 0;
			flags().TargetUser = 0;
			m_infoChar.m_fx = 0;

			if (server.isPlayerAlreadyConnected(this)) {
				enviarError("Perdon, pero ya esta conectado.");
				return;
			}

			// ¿Es el passwd valido?
			if (!m_password.equals(userStorage.loadPasswordFromStorage(m_nick))) {
				enviarError("Clave incorrecta.");
				return;
			}
			this.userStorage.loadUserFromStorage();

			if (server.getAdmins().esDios(m_nick)) {
				flags().Privilegios = 3;
				Log.logGM(m_nick, "El GM-DIOS se conectó desde la ip=" + m_ip);
			} else if (server.getAdmins().esSemiDios(m_nick)) {
				flags().Privilegios = 2;
				Log.logGM(m_nick, "El GM-SEMIDIOS se conectó desde la ip=" + m_ip);
			} else if (server.getAdmins().esConsejero(m_nick)) {
				flags().Privilegios = 1;
				Log.logGM(m_nick, "El GM-CONSEJERO se conectó desde la ip=" + m_ip);
			} else {
				// Usuario no privilegiado.
				flags().Privilegios = 0;
			}
			if (m_inv.getEscudoSlot() == 0) {
				m_infoChar.m_escudo = NingunEscudo;
			}
			if (m_inv.getCascoSlot() == 0) {
				m_infoChar.m_casco = NingunCasco;
			}
			if (m_inv.getArmaSlot() == 0) {
				m_infoChar.m_arma = NingunArma;
			}

			if (flags().Navegando) {
				m_infoChar.m_cuerpo = !isAlive() ? OBJ_INDEX_FRAGATA_FANTASMAL : m_inv.getBarco().Ropaje;
				m_infoChar.m_cabeza = 0;
				m_infoChar.m_arma = NingunArma;
				m_infoChar.m_escudo = NingunEscudo;
				m_infoChar.m_casco = NingunCasco;
			}

			enviarInventario();
			m_spells.enviarHechizos();
			sendUpdateUserStats();

			if (pos().map == 0) {
				// Posicion de comienzo
				if (m_hogar == CIUDAD_NIX) {
					setPos(server.getCiudadPos(CIUDAD_NIX).copy());
				} else if (m_hogar == CIUDAD_ULLA) {
					setPos(server.getCiudadPos(CIUDAD_ULLA).copy());
				} else if (m_hogar == CIUDAD_BANDER) {
					setPos(server.getCiudadPos(CIUDAD_BANDER).copy());
				} else if (m_hogar == CIUDAD_LINDOS) {
					setPos(server.getCiudadPos(CIUDAD_LINDOS).copy());
				} else {
					m_hogar = CIUDAD_ULLA;
					setPos(server.getCiudadPos(CIUDAD_ULLA).copy());
				}
			}
			
			if (!enterIntoMap(pos().map, pos().x, pos().y, true, true)) {
				enviarError("No pudo ingresar al mapa");
				return;
			}

			// agush ;-)
			if (server.isRaining()) {
				sendPacket(new RainToggleResponse());
			}
			if (!isCriminal()) {
				flags().Seguro = true;
				sendPacket(new SafeModeOnResponse());
			} else {
				sendPacket(new SafeModeOffResponse());
				flags().Seguro = false;
			}

			enviarIndiceUsuario();
			enviarIP();
			enviarLogged();
			
			flags().UserLogged = true;

		} catch (Exception e) {
			log.fatal("ERROR EN connectUser(), nick=" + m_nick, e);
			enviarError("Hubo un error conectando.");
			
		} finally {
			if (!flags().UserLogged) {
				// if anything went wrong...
				doSALIR();
				m_nick = "";
			}
		}
	}

	public void doServerVersion() {
		enviarMensaje("-=<>[ Bienvenido a Argentun Online ]<>=-", FontType.FONTTYPE_SERVER);
		enviarMensaje("-=<>[    Powered by Gorlok's AO    ]<>=-", FontType.FONTTYPE_SERVER);
		enviarMensaje("Server version: " + VERSION, FontType.FONTTYPE_SERVER);
	}

	public void doDebug(String s) {
		if ("".equals(s)) {
			enviarMensaje("DEBUG: " + (server.isShowDebug() ? "ON" : "OFF"), FontType.FONTTYPE_INFO);
		} else if (s.equalsIgnoreCase("ON")) {
			server.setShowDebug(true);
			enviarMensaje("DEBUG: activado", FontType.FONTTYPE_INFO);
		} else if (s.equalsIgnoreCase("OFF")) {
			server.setShowDebug(false);
			enviarMensaje("DEBUG: desactivado", FontType.FONTTYPE_INFO);
		}
	}

	public void efectoCegueEstu() {
		if (m_counters.Ceguera > 0) {
			m_counters.Ceguera--;
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
		if (m_counters.Frio < IntervaloFrio) {
			m_counters.Frio++;
		} else {
			Map mapa = server.getMap(pos().map);
			if (mapa.getTerreno() == TERRENO_NIEVE) {
				enviarMensaje("¡¡Estas muriendo de frio, abrígate o morirás!!.", FontType.FONTTYPE_INFO);
				int modifi = Util.porcentaje(stats().MaxHP, 5);
				stats().MinHP -= modifi;

				if (stats().MinHP < 1) {
					enviarMensaje("¡¡Has muerto de frio!!.", FontType.FONTTYPE_INFO);
					stats().MinHP = 0;
					userDie();
				}
			} else {
				if (stats().stamina > 0) { // Vericación agregada por gorlok
					int modifi = Util.porcentaje(stats().maxStamina, 5);
					stats().quitarStamina(modifi);
					enviarMensaje("¡¡Has perdido stamina, si no te abrigas rápido la perderás toda!!.", FontType.FONTTYPE_INFO);
				}
			}
			m_counters.Frio = 0;
			sendUpdateUserStats();
		}
	}

	public boolean sanar(int intervalo) {
		Map mapa = server.getMap(pos().map);
		short trigger = mapa.getTrigger(pos().x, pos().y);
		if (trigger == 1 || trigger == 2 || trigger == 4) {
			return false;
		}
		// Con el paso del tiempo se va sanando... pero muy lentamente ;-)
		if (stats().MinHP < stats().MaxHP) {
			if (m_counters.HPCounter < intervalo) {
				m_counters.HPCounter++;
			} else {
				int mashit = Util.Azar(2, Util.porcentaje(stats().maxStamina, 5));
				m_counters.HPCounter = 0;
				stats().MinHP += mashit;

				if (stats().MinHP > stats().MaxHP) {
					stats().MinHP = stats().MaxHP;
				}
				enviarMensaje("Has sanado.", FontType.FONTTYPE_INFO);
				
				sendUpdateUserStats();
				return true;
			}
		}
		return false;
	}

	private boolean suerteMeditar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (stats().userSkills(Skill.SKILL_Meditar) / 10)]);
		return (Util.Azar(1, rango) == 1);
	}

	public void meditar() {
		long tActual = Util.millis();
		if (tActual - m_counters.tInicioMeditar < TIEMPO_INICIO_MEDITAR) {
			return;
		}
		if (stats().stamina < stats().maxStamina) {
			return;
		}
		m_counters.IdleCount = 0;
		if (stats().mana >= stats().maxMana) {
			enviarMensaje("Has terminado de meditar.", FontType.FONTTYPE_INFO);
			sendPacket(new MeditateToggleResponse());
			flags().Meditando = false;
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
			enviarCFX(0, 0);
			return;
		}
		if (suerteMeditar()) {
			int cant = Util.porcentaje(stats().maxMana, 3);
			stats().aumentarMana(cant);
			enviarMensaje("¡Has recuperado " + cant + " puntos de mana!", FontType.FONTTYPE_INFO);
			sendUpdateUserStats();
			subirSkill(Skill.SKILL_Meditar);
		}
	}

	private boolean efectoVeneno() {
		if (m_counters.Veneno < IntervaloVeneno) {
			m_counters.Veneno++;
		} else {
			enviarMensaje("Estas envenenado, si no te curas moriras.", FontType.FONTTYPE_VENENO);
			m_counters.Veneno = 0;
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
		if (m_counters.Invisibilidad < IntervaloInvisible) {
			m_counters.Invisibilidad++;
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
			stats().restoreAtributos();
		}
	}

	public void sendUserAttributes() {
		sendPacket(new AttributesResponse(
				stats().userAttributes[ATRIB_FUERZA], 
				stats().userAttributes[ATRIB_AGILIDAD], 
				stats().userAttributes[ATRIB_INTELIGENCIA],
				stats().userAttributes[ATRIB_CARISMA], 
				stats().userAttributes[ATRIB_CONSTITUCION]));		
	}

	public boolean aplicarHambreYSed() {
		// Sed
		boolean enviarEstads = false;
		if (stats().drinked > 0) {
			if (m_counters.drinkCounter < IntervaloSed) {
				m_counters.drinkCounter++;
			} else {
				m_counters.drinkCounter = 0;
				stats().quitarSed(10);
				if (stats().drinked <= 0) {
					stats().drinked = 0;
					flags().Sed = true;
				}
				enviarEstads = true;
			}
		}
		// hambre
		if (stats().eaten > 0) {
			if (m_counters.foodCounter < IntervaloHambre) {
				m_counters.foodCounter++;
			} else {
				m_counters.foodCounter = 0;
				stats().quitarHambre(10);
				if (stats().eaten <= 0) {
					stats().eaten = 0;
					flags().Hambre = true;
				}
				enviarEstads = true;
			}
		}
		return enviarEstads;
	}

	public boolean recStamina(int intervalo) {
		Map mapa = server.getMap(pos().map);
		short trigger = mapa.getTrigger(pos().x, pos().y);
		if (trigger == 2 || trigger == 3 || trigger == 4) {
			return false;
		}
		if (stats().stamina < stats().maxStamina) {
			if (m_counters.STACounter < intervalo) {
				m_counters.STACounter++;
			} else {
				m_counters.STACounter = 0;
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
			if (pet.getContadores().TiempoExistencia > 0) {
				pet.getContadores().TiempoExistencia--;
				if (pet.getContadores().TiempoExistencia == 0) {
					quitarMascota(pet);
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
			boolean bEnviarStats = false;
			boolean bEnviarAyS = false;
			
			// Call DoTileEvents(iUserIndex, .Pos.map, .Pos.X, .Pos.Y)
			
			NumeroPaquetesPorMiliSec = 0;
			if (flags().Paralizado) {
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
					bEnviarStats = efectoVeneno();
				}
				if (!flags().AdminInvisible && flags().Invisible) {
					efectoInvisibilidad();
				}
				duracionPociones();
				bEnviarAyS = aplicarHambreYSed();
				Map mapa = server.getMap(pos().map);
				if (!(server.isRaining() && mapa.intemperie(pos().x, pos().y))) {
					if (!flags().Descansar && !flags().Hambre && !flags().Sed) {
						// No esta descansando
						if (sanar(SanaIntervaloSinDescansar))
							bEnviarStats = true;
						if (recStamina(StaminaIntervaloSinDescansar))
							bEnviarStats = true;
						
					} else if (flags().Descansar) {
						// esta descansando

						if (sanar(SanaIntervaloDescansar))
							bEnviarStats = true;
						if (recStamina(StaminaIntervaloDescansar))
							bEnviarStats = true;

						// termina de descansar automaticamente
						if (stats().MaxHP == stats().MinHP && stats().maxStamina == stats().stamina) {
							sendPacket(new MeditateToggleResponse());
							enviarMensaje("Has terminado de descansar.", FontType.FONTTYPE_INFO);
							flags().Descansar = false;
						}
					}
				}
				// Verificar muerte por hambre
				if (stats().eaten <= 0 && !isGM()) {
					enviarMensaje("¡¡Has muerto de hambre!!.", FontType.FONTTYPE_INFO);
					stats().eaten = 0;
					userDie();
				}
				// Verificar muerte de sed
				if (stats().drinked <= 0 && !isGM()) {
					enviarMensaje("¡¡Has muerto de sed!!.", FontType.FONTTYPE_INFO);
					stats().drinked = 0;
					userDie();
				}
				if (bEnviarStats) {
					sendUpdateUserStats();
				}
				if (bEnviarAyS) {
					enviarEstadsHambreSed();
				}
				if (getUserPets().hasPets()) {
					checkSummonTimeout();
				}
			}
		} else {
			m_counters.IdleCount++;
			if (m_counters.IdleCount > IntervaloParaConexion) {
				m_counters.IdleCount = 0;
				doSALIR();
			}
		}
	}

	private String getTituloFaccion() {
		if (m_faccion.ArmadaReal) {
			return " <Ejercito real> <" + m_faccion.tituloReal() + ">";
		} else if (m_faccion.FuerzasCaos) {
			return " <Fuerzas del caos> <" + m_faccion.tituloCaos() + ">";
		}
		return "";
	}

	public String getTagsDesc() {
		var msg = new StringBuilder()
				.append(getNick());
		if (esNewbie()) {
			msg.append(" <NEWBIE>");
		}
		msg.append(getTituloFaccion());
		if (getGuildInfo().esMiembroClan()) {
			msg.append(" <" + guildUser.getGuildName() + ">");
		}
		if (m_desc.length() > 0) {
			msg.append(" - " + m_desc);
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
			return (gender == UserGender.GENERO_MUJER);
		} else if (infoObj.esParaHombres()) {
			return (gender == UserGender.GENERO_HOMBRE);
		} else {
			return true;
		}
	}

	public boolean checkRazaUsaRopa(short objid) {
		// Verifica si la raza puede usar esta ropa
		boolean canUse = false;
		
		var infoObj = findObj(objid);
		switch (race) {
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
		if (infoObj.esParaRazaDrow() && race != UserRace.RAZA_DROW) {
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
		for (int i = 1; i <= m_inv.size(); i++) {
			if (m_inv.getObjeto(i).objid == objid) {
				total += m_inv.getObjeto(i).cant;
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
				enviarMensaje("No tenes suficientes madera.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		return true;
	}

	private boolean herreroTieneMateriales(short objid) {
		ObjectInfo info = findObj(objid);
		if (info.LingH > 0) {
			if (!tieneObjetos(LingoteHierro, info.LingH)) {
				enviarMensaje("No tienes suficientes lingotes de hierro.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		if (info.LingP > 0) {
			if (!tieneObjetos(LingotePlata, info.LingP)) {
				enviarMensaje("No tienes suficientes lingotes de plata.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		if (info.LingO > 0) {
			if (!tieneObjetos(LingoteOro, info.LingO)) {
				enviarMensaje("No tienes suficientes lingotes de oro.", FontType.FONTTYPE_INFO);
				return false;
			}
		}
		return true;
	}

	private boolean puedeConstruir(short objid) {
		ObjectInfo info = findObj(objid);
		return herreroTieneMateriales(objid) && stats().userSkills(Skill.SKILL_Herreria) >= info.SkHerreria;
	}

	private boolean puedeConstruirHerreria(short objid) {
		for (int i = 0; i < server.getArmasHerrero().length; i++) {
			if (server.getArmasHerrero()[i] == objid) {
				return true;
			}
		}
		for (int i = 0; i < server.getArmadurasHerrero().length; i++) {
			if (server.getArmadurasHerrero()[i] == objid) {
				return true;
			}
		}
		return false;
	}

	private void herreroConstruirItem(short objid) {
		if (puedeConstruir(objid) && puedeConstruirHerreria(objid)) {
			Map mapa = server.getMap(pos().map);
			if (mapa == null) {
				return;
			}
			ObjectInfo info = findObj(objid);
			herreroQuitarMateriales(objid);
			// AGREGAR FX
			if (info.objType == ObjType.Weapon) {
				enviarMensaje("Has construido el arma!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.ESCUDO) {
				enviarMensaje("Has construido el escudo!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.CASCO) {
				enviarMensaje("Has construido el casco!.", FontType.FONTTYPE_INFO);
			} else if (info.objType == ObjType.Armadura) {
				enviarMensaje("Has construido la armadura!.", FontType.FONTTYPE_INFO);
			}
			if (m_inv.agregarItem(objid, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Herreria);
			enviarInventario();
			enviarSonido(MARTILLOHERRERO);
			flags().Trabajando = true;
		}
	}

	private boolean puedeConstruirCarpintero(short objid) {
		for (int i = 0; i < server.getObjCarpintero().length; i++) {
			if (server.getObjCarpintero()[i] == objid) {
				return true;
			}
		}
		return false;
	}

	private void carpinteroConstruirItem(short objid) {
		ObjectInfo info = findObj(objid);
		Map mapa = server.getMap(pos().map);
		if (mapa == null) {
			return;
		}
		if (carpinteroTieneMateriales(objid) && stats().userSkills(Skill.SKILL_Carpinteria) >= info.SkCarpinteria
				&& puedeConstruirCarpintero(objid) && m_inv.getArma().ObjIndex == SERRUCHO_CARPINTERO) {
			carpinteroQuitarMateriales(objid);
			enviarMensaje("¡Has construido el objeto!", FontType.FONTTYPE_INFO);
			if (m_inv.agregarItem(objid, 1) < 1) {
				mapa.tirarItemAlPiso(pos().x, pos().y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Carpinteria);
			enviarInventario();
			enviarSonido(LABUROCARPINTERO);
			flags().Trabajando = true;
		}
	}

	// ################################# FIN TRABAJO ###################################

	public void volverCiudadano() {
		Map mapa = server.getMap(pos().map);
		if (mapa.getTrigger(pos().x, pos().y) == MapCell.TRIGGER_ARENA_DUELOS) {
			return;
		}
		boolean eraCrimi = isCriminal();
		m_reputacion.perdonar();
		if (eraCrimi) {
			refreshUpdateTagAndStatus();
		}

		System.out.println(this.getNick() + "ahora es ciuda");

	}

	public void refreshUpdateTagAndStatus() {
		Map mapa = server.getMap(pos().map);
		mapa.enviarAlArea(pos().x, pos().y,
				new UpdateTagAndStatusResponse(getId(), isCriminal() ? (byte)1 : (byte)0, getTagsDesc()));
	}

	public void quitGame() {
		if (flags().UserLogged && !m_counters.Saliendo) {
			m_counters.Saliendo = true;
			Map mapa = server.getMap(pos().map);
			if (mapa != null && mapa.esZonaSegura()) {
				m_counters.SalirCounter = 1; // 1 segundo.
			} else {
				m_counters.SalirCounter = IntervaloCerrarConexion; // 10 segundos
				enviarMensaje("Cerrando... Se cerrará el juego en " + IntervaloCerrarConexion + " segundos...",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	private void sendUserBovedaTxt(Player usuario) {
		enviarMensaje("El usuario " + usuario.getNick(), FontType.FONTTYPE_INFOBOLD);
		enviarMensaje("Tiene " + usuario.m_bancoInv.getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		InventoryObject obj;
		ObjectInfo iobj;
		for (int i = 1; i <= usuario.m_bancoInv.size(); i++) {
			if ((obj = usuario.m_bancoInv.getObjeto(i)) != null) {
				iobj = findObj(obj.objid);
				enviarMensaje(" Objeto " + i + ": " + iobj.Nombre + " Cantidad:" + obj.cant, FontType.FONTTYPE_INFO);
			}
		}
	}

	private void sendUserBovedaTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			enviarMensaje("Pj: " + pj, FontType.FONTTYPE_INFOBOLD);
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				int cantObjs = ini.getInt("BancoInventory", "CantidadItems");
				enviarMensaje("Tiene " + cantObjs + " objetos.", FontType.FONTTYPE_INFO);
				// Lista de objetos en el banco.
				ObjectInfo iobj;
				for (int i = 0; i < MAX_BANCOINVENTORY_SLOTS; i++) {
					String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = findObj(objid);
						enviarMensaje(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.FONTTYPE_INFO);
					}
				}
			} catch (java.io.IOException e) {
				log.fatal("Error sendUserBovedaTxtFromChar", e);
			}
		} else {
			enviarMensaje("Usuario inexistente: " + pj, FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(short dir, short slot) {
		if (dir < 1 || dir > 2) {
			return;
		}
		if (slot < 1 || slot > MAX_HECHIZOS) {
			return;
		}

		m_spells.moveSpell(slot, dir);
	}

	private void sendUserMiniStatsTxt(Player usuario) {
		if (usuario == null) {
			return;
		}
		enviarMensaje("Pj: " + usuario.getNick() + " Clase: " + usuario.clazz.clazz().getName(), FontType.FONTTYPE_INFOBOLD);
		enviarMensaje("CiudadanosMatados: " + usuario.userFaction().CiudadanosMatados + " CriminalesMatados: "
				+ usuario.userFaction().CriminalesMatados + " UsuariosMatados: " + usuario.stats().usuariosMatados,
				FontType.FONTTYPE_INFO);
		enviarMensaje("NPCsMuertos: " + usuario.stats().NPCsMuertos + " Pena: " + usuario.m_counters.Pena,
				FontType.FONTTYPE_INFO);
	}

	private void sendUserMiniStatsTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				enviarMensaje("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.FONTTYPE_INFOBOLD);
				enviarMensaje("CiudadanosMatados: " + ini.getLong("FACCIONES", "CiudMatados") + " CriminalesMatados: "
						+ ini.getLong("FACCIONES", "CrimMatados") + " UsuariosMatados: "
						+ ini.getInt("MUERTES", "UserMuertes"), FontType.FONTTYPE_INFO);
				enviarMensaje("NPCsMuertos: " + ini.getInt("MUERTES", "NpcsMuertes") + " Pena: "
						+ ini.getLong("COUNTERS", "PENA"), FontType.FONTTYPE_INFO);
				boolean ban = ini.getShort("FLAGS", "Ban") == 1;
				enviarMensaje("Ban: " + (ban ? "si" : "no"), FontType.FONTTYPE_INFO);
				if (ban) {
					enviarMensaje("Ban por: " + ini.getString("BAN", "BannedBy") + " Motivo: "
							+ ini.getString("BAN", "Reason"), FONTTYPE_FIGHT);
				}
			} catch (java.io.IOException e) {
				log.fatal("ERROR sendUserMiniStatsTxtFromChar", e);
			}
		} else {
			enviarMensaje("El pj no existe: " + pj, FontType.FONTTYPE_INFO);
		}
	}

	private void sendUserInvTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				enviarMensaje("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.FONTTYPE_INFOBOLD);
				enviarMensaje("Tiene " + ini.getShort("Inventory", "CantidadItems") + " objetos.", FontType.FONTTYPE_INFO);
				ObjectInfo iobj;
				for (int i = 0; i < MAX_INVENTORY_SLOTS; i++) {
					String tmp = ini.getString("Inventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = findObj(objid);
						enviarMensaje(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.FONTTYPE_INFO);
					}
				}
			} catch (java.io.IOException e) {
				log.fatal("ERROR sendUserInvTxtFromChar", e);
			}
		} else {
			enviarMensaje("El pj no existe: " + pj, FontType.FONTTYPE_INFO);
		}
	}

	private int triggerZonaPelea(Player victima) {
		if (victima == null) {
			return MapCell.TRIGGER6_AUSENTE;
		}
		int t1 = server.getMap(pos().map).getTrigger(pos().x, pos().y);
		int t2 = server.getMap(victima.pos().map).getTrigger(victima.pos().x, victima.pos().y);
		if (t1 != MapCell.TRIGGER_ARENA_DUELOS && t2 != MapCell.TRIGGER_ARENA_DUELOS) {
			return MapCell.TRIGGER6_AUSENTE;
		}
		if (t1 == t2) {
			return MapCell.TRIGGER6_PERMITE;
		}
		return MapCell.TRIGGER6_PROHIBE;
	}

	private void envenenarUsuario(Player victima) {
		// UserEnvenenar
		ObjectInfo arma = m_inv.getArma();
		if (arma != null) {
			if (arma.esProyectil()) {
				arma = m_inv.getMunicion();
			}
			if (arma == null) {
				return;
			}
			if (arma.envenena()) {
				if (Util.Azar(1, 100) < 60) {
					victima.flags().Envenenado = true;
					victima.enviarMensaje(m_nick + " te ha envenenado!!", FONTTYPE_FIGHT);
					enviarMensaje("Has envenenado a " + victima.getNick() + "!!", FONTTYPE_FIGHT);
				}
			}
		}
	}

	public static String getChrInfo(String pj) {
		// ¿Existe el personaje?
		if (!Util.existeArchivo(getPjFile(pj))) {
			return null;
		}
		try {
			IniFile ini = new IniFile(getPjFile(pj));
			StringBuffer sb = new StringBuffer(pj).append(",").append(ini.getString("INIT", "Raza")).append(",")
					.append(ini.getString("INIT", "Clase")).append(",").append(ini.getString("INIT", "Genero"))
					.append(",").append(ini.getString("STATS", "ELV")).append(",").append(ini.getString("STATS", "GLD"))
					.append(",").append(ini.getString("STATS", "BANCO")).append(",")
					.append(ini.getString("REP", "Promedio")).append(",").append(ini.getString("Guild", "FundoClan"))
					.append(",").append(ini.getString("Guild", "EsGuildLeader")).append(",")
					.append(ini.getString("Guild", "Echadas")).append(",").append(ini.getString("Guild", "Solicitudes"))
					.append(",").append(ini.getString("Guild", "SolicitudesRechazadas")).append(",")
					.append(ini.getString("Guild", "VecesFueGuildLeader")).append(",")
					// .append(ini.getString("Guild", "YaVoto")).append(",") // FIXME
					.append(ini.getString("Guild", "ClanesParticipo")).append(",")
					.append(ini.getString("Guild", "ClanFundado")).append(",")
					.append(ini.getString("Guild", "GuildName")).append(",")
					.append(ini.getString("FACCIONES", "EjercitoReal")).append(",")
					.append(ini.getString("FACCIONES", "EjercitoCaos")).append(",")
					.append(ini.getString("FACCIONES", "CiudMatados")).append(",")
					.append(ini.getString("FACCIONES", "CiudMatados")).append(",");
			return sb.toString();
		} catch (java.io.IOException e) {
			log.fatal("ERROR getChrInfo", e);
		}
		return null;
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
		return this.stats().userSkills(Skill.SKILL_Herreria) / this.clazz.clazz().modHerreria();
	}
	
	/**
	 * Devuelve el skill de Carpinteria efectivo, aplicando su modificador de clase.
	 * @return valor del skill efectivo
	 */
	public double skillCarpinteriaEfectivo() {
		return this.stats().userSkills(Skill.SKILL_Carpinteria) / this.clazz.clazz().modCarpinteria();
	}
	
	public void agregarHechizo(int slot) {
		this.m_spells.agregarHechizo(slot);
	}
	
	public void doBalance() {
		// Comando /BALANCE
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// Se asegura que el target es un npc
		if (flags().TargetNpc == 0) {
			enviarMensaje("Debes seleccionar un personaje.", FontType.FONTTYPE_INFO);
			return;
		}
		Npc npc = server.npcById(flags().TargetNpc);
		if (npc.pos().distance(pos()) > 3) {
			enviarMensaje("Estas demasiado lejos del vendedor.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (npc.isBankCashier() && (npc instanceof NpcCashier)) {
			((NpcCashier)npc).balance(this);
		} else if (npc.isGambler() && (npc instanceof NpcGambler)) {
			((NpcGambler)npc).balance(this);
		} else {
			enviarHabla(Color.COLOR_BLANCO, "No entiendo de eso. Habla con alguien más.", getId());
		}
	}
	
}