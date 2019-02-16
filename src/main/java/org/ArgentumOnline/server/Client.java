/**
 * Client.java
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.anticheat.SpeedHackCheck;
import org.ArgentumOnline.server.anticheat.SpeedHackException;
import org.ArgentumOnline.server.classes.AssassinClass;
import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.classes.CharClassManager;
import org.ArgentumOnline.server.classes.FishermanClass;
import org.ArgentumOnline.server.classes.HunterClass;
import org.ArgentumOnline.server.classes.ThiefClass;
import org.ArgentumOnline.server.gm.GmRequest;
import org.ArgentumOnline.server.guilds.Guild;
import org.ArgentumOnline.server.guilds.GuildInfo;
import org.ArgentumOnline.server.inventory.Inventory;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.inventory.UserInventory;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapCell;
import org.ArgentumOnline.server.map.MapObject;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.MapPos.Direction;
import org.ArgentumOnline.server.protocol.BufferWriter;
import org.ArgentumOnline.server.protocol.ServerPacketID;
import org.ArgentumOnline.server.quest.UserQuest;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class Client extends AbstractCharacter implements Constants {
	private static Logger log = LogManager.getLogger();

	public int areaID = 0;

	public int areaPerteneceX = 0;
	public int areaPerteneceY = 0;

	public int areaRecibeX = 0;
	public int areaRecibeY = 0;

	public int minX = 0;
	public int minY = 0;

	public List<Integer> lengthClient = new LinkedList<Integer>();

	String m_nick = "";

	String m_password = "";

	public SocketChannel socketChannel = null;

	private final static int BUFFER_SIZE = 1024;
	public ByteBuffer colaClient = ByteBuffer.allocate(2048);
	public ByteBuffer clientBuffer;

	String m_desc = ""; // Descripcion

	CharClass m_clase = HunterClass.getInstance();

	short m_raza = RAZA_HUMANO;

	short m_genero = GENERO_HOMBRE;

	String m_email = "";

	short m_hogar = CIUDAD_ULLA;

	int m_cantMascotas = 0;

	Npc m_mascotas[] = new Npc[MAXMASCOTAS];

	int m_cantHechizos = 0;

	UserSpells m_spells;

	long NumeroPaquetesPorMiliSec = 0;

	long BytesTransmitidosUser = 0;

	long BytesTransmitidosSvr = 0;

	GuildInfo m_guildInfo = new GuildInfo(this);

	Guild m_guild = null;

	int m_prevCRC = 0;

	long PacketNumber = 0;

	long RandKey = 0;

	boolean m_saliendo = false;

	String m_ip = "";

	String m_banned_by = "";

	String m_banned_reason = "";

	UserFlags m_flags = new UserFlags();

	UserStats m_estads = new UserStats();

	Reputation m_reputacion = new Reputation();

	Factions m_faccion;

	MapPos m_pos = MapPos.empty();

	Inventory m_bancoInv = new Inventory(MAX_BANCOINVENTORY_SLOTS);

	UserCounters m_counters = new UserCounters();

	public UserTrade m_comUsu = new UserTrade();

	SpeedHackCheck speedHackMover = new SpeedHackCheck("SpeedHack de mover");

	public UserQuest m_quest;

	UserInventory m_inv;

	AojServer server;

	/** Creates a new instance of Client */
	public Client(SocketChannel socketChannel, AojServer aoserver) {
		this.clientBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		this.clientBuffer.order(ByteOrder.LITTLE_ENDIAN);

		this.server = aoserver;
		this.setId(server.getNextId());
		this.m_spells = new UserSpells(server, this);
		this.m_quest = new UserQuest(this.server);
		this.m_inv = new UserInventory(this.server, this, 20);
		this.m_faccion = new Factions(this.server, this);
		this.socketChannel = socketChannel;
		java.net.InetSocketAddress addr = (java.net.InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
		if (addr != null) {
			this.m_ip = addr.getAddress().getHostAddress();
			log.info(this.m_nick + " conectado desde " + this.m_ip);
		}
	}

	/**
	 * JAO: with this, we set the user area ID
	 */
	public void setArea(int id) {
		if (DEBUG)
			System.out.println("AREAID:" + id);
		this.areaID = id;
	}

	/**
	 * JAO: setter in X area
	 */
	public void setAreaPerteneceX(int value) {
		this.areaPerteneceX = value;
	}

	/**
	 * JAO: setter in Y area
	 */

	public void setAreaPerteneceY(int value) {
		this.areaPerteneceY = value;
	}

	/**
	 * JAO: adyacent X user area
	 */

	public void setAreaRecibeX(int value) {
		this.areaRecibeX = value;
	}

	/**
	 * JAO: adyacent Y user area
	 */

	public void setAreaRecibeY(int value) {
		this.areaRecibeY = value;
	}

	/**
	 * JAO: min x pos area
	 */

	public void setMinX(int value) {
		this.minX = value;
	}

	/**
	 * JAO: min y pos area
	 */

	public void setMinY(int value) {
		this.minY = value;
	}

	/**
	 * JAO: return the area id
	 */

	public int getArea() {
		return this.areaID;
	}

	/**
	 * JAO: give the area in X
	 */

	public int getAreaPerteneceX() {
		return this.areaPerteneceX;
	}

	/**
	 * JAO: give the area in Y
	 */

	public int getAreaPerteneceY() {
		return this.areaPerteneceY;
	}

	/**
	 * JAO: give the adyacent area X
	 */

	public int getAreaRecibeX() {
		return this.areaRecibeX;
	}

	/**
	 * JAO: give the adyacent area Y
	 */

	public int getAreaRecibeY() {
		return this.areaRecibeY;
	}

	/**
	 * JAO: return the lowest value in X
	 */

	public int getMinX() {
		return this.minX;
	}

	/**
	 * JAO: return the lowest value in Y
	 */

	public int getMinY() {
		return this.minY;
	}

	/**
	 * Gives the current user level.
	 */
	public int level() {
		return m_estads.ELV;
	}

	public GuildInfo guildInfo() {
		return m_guildInfo;
	}

	public int genCRC() {
		// FIXME
		return (m_prevCRC = m_prevCRC % CRCKEY);
	}

	/**
	 * Gives the user race.
	 * 
	 * @return race
	 */
	public short getRaza() {
		return m_raza;
	}

	/**
	 * Gives the user position.
	 * 
	 * @return position
	 */
	public MapPos getPos() {
		return m_pos;
	}

	/**
	 * Gives the user nickname.
	 * 
	 * @return nickname
	 */
	public String getNick() {
		return m_nick;
	}

	/**
	 * Gives the user character class.
	 * 
	 * @return the character's class
	 */
	public CharClass getClase() {
		return m_clase;
	}

	public UserQuest getQuest() {
		return m_quest;
	}

	public void nextQuest() {
		m_quest.m_nroQuest++;
		m_quest.m_enQuest = true;
	}

	/**
	 * Returns true if the character is criminal.
	 * 
	 * @return true if character is criminal.
	 */
	public boolean esCriminal() {
		return m_reputacion.esCriminal();
	}

	/**
	 * Returns true if the character is alive (not a ghost).
	 * 
	 * @return true if character is alive (not a ghost).
	 */
	public boolean isAlive() {
		return !m_flags.Muerto;
	}

	/**
	 * Returns true if the character is not visible.
	 * 
	 * @return true if the character is not visible.
	 */
	public boolean estaInvisible() {
		return m_flags.Invisible;
	}

	/**
	 * Returns true if the character is hidden.
	 * 
	 * @return true if the character is hidden.
	 */
	public boolean estaOculto() {
		return m_flags.Oculto;
	}

	/**
	 * Returns true if the character is sailing.
	 * 
	 * @return true if the character is sailing.
	 */
	public boolean estaNavegando() {
		return m_flags.Navegando;
	}

	/**
	 * Returns true if the character is a god.
	 * 
	 * @return true if the character is a god.
	 */
	public boolean esDios() {
		return m_flags.Privilegios == 3;
	}

	/**
	 * Returns true if the character is a half-god.
	 * 
	 * @return true if the character is a half-god.
	 */
	public boolean esSemiDios() {
		return m_flags.Privilegios == 2;
	}

	/**
	 * Returns true if the character is an adviser.
	 * 
	 * @return true if the character is an adviser.
	 */
	public boolean esConsejero() {
		return m_flags.Privilegios == 1;
	}

	/**
	 * Returns true if the character is a game master.
	 * 
	 * @return true if the character is a game master.
	 */
	public boolean esGM() {
		return m_flags.Privilegios > 0;
	}

	/**
	 * Returns true if the character is working.
	 * 
	 * @return true if the character is working.
	 */
	public boolean estaTrabajando() {
		return m_flags.Trabajando;
	}

	/**
	 * Gives the user's stats.
	 * 
	 * @return the user's stats.
	 */
	public UserStats getEstads() {
		return m_estads;
	}

	/**
	 * Gives the user's reputation.
	 * 
	 * @return the user's reputation.
	 */
	public Reputation getReputacion() {
		return m_reputacion;
	}

	public short thatDir() {
		return m_infoChar.getDir();
	}

	/**
	 * Gives the user's flags.
	 * 
	 * @return user's flags
	 */
	public UserFlags getFlags() {
		return m_flags;
	}

	/**
	 * Gives the user's counters.
	 * 
	 * @return user's counters.
	 */
	public UserCounters getCounters() {
		return m_counters;
	}

	/**
	 * Gives a string representation of the user.
	 * 
	 * @return string representation of the user.
	 */
	@Override
	public String toString() {
		return "Cliente(id=" + getId() + " nick=" + m_nick + ")";
	}

	/**
	 * Gives the user's faction.
	 * 
	 * @return user's faction.
	 */
	public Factions getFaccion() {
		return m_faccion;
	}

	/**
	 * Gives the user's inventory.
	 * 
	 * @return user's inventory.
	 */
	public UserInventory getInv() {
		return m_inv;
	}

	/**
	 * Returns true if the user has the attack lock.
	 * 
	 * @return true if the user has the attack lock.
	 */
	public boolean tieneSeguro() {
		return m_flags.Seguro;
	}

	/**
	 * Sends a message to the user.
	 * 
	 * @param msg    is the message type to send.
	 * @param params is the parameters of the message (optional).
	 */
	public synchronized void enviar(ServerPacketID msg, Object... params) {
		if (m_saliendo) {
			return;
		}
		try {
			log.debug(">>" + m_nick + ">> " + msg);

			this.clientBuffer.clear();

			BufferWriter.write(this.clientBuffer, msg, params);

			this.clientBuffer.flip();

			if (socketChannel.write(clientBuffer) == 0) {
				// FIXME: ESTO SIGNIFICA QUE NO PUDO ESCRIBIR, HAY QUE ENCOLAR
				// SI NO ESCRIBIO TODO!!!
				log.error("zero bytes writen!");
			}
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
		return esCriminal() ? "<CRIMINAL>" : "<CIUDADANO>";
	}

	public void doModMapInfo(String accion, int valor) {
		// Comando /MODMAPINFO
		if ("".equals(accion)) {
			enviarMensaje("Parámetros inválidos!", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/MODMAPINFO " + accion + " " + valor);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		if (accion.equalsIgnoreCase("PK")) {
			if (valor == 0 || valor == 1) {
				mapa.m_pk = (valor == 1);
				enviarMensaje("PK cambiado.", FontType.INFO);
			}
			enviarMensaje("Mapa " + m_pos.map + " PK: " + (mapa.m_pk ? "SI" : "NO"), FontType.INFO);
		} else if (accion.equalsIgnoreCase("BACKUP")) {
			if (valor == 0 || valor == 1) {
				mapa.m_backup = (valor == 1);
				enviarMensaje("BACKUP cambiado.", FontType.INFO);
			}
			enviarMensaje("Mapa " + m_pos.map + " Backup: " + (mapa.m_backup ? "SI" : "NO"), FontType.INFO);
		}
	}

	public void doModificarCaracter(String nick, String accion, int valor) {
		// MODIFICA CARACTER
		// Comando /MOD
		Log.logGM(m_nick, "/MOD " + nick + " " + accion + " " + valor);
		if ("".equals(nick)) {
			enviarMensaje("Parámetros inválidos!", FontType.INFO);
			return;
		}
		Client usuario = server.getUsuario(nick);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (accion.equalsIgnoreCase("ORO")) {
			if (valor < 95001) {
				usuario.m_estads.setGold(valor);
				usuario.refreshStatus(1);
			} else {
				enviarMensaje(
						"No esta permitido utilizar valores mayores a 95000. Su comando ha quedado en los logs del juego.",
						FontType.INFO);
			}
		} else if (accion.equalsIgnoreCase("EXP")) {
			if (valor < 1000000) {
				usuario.m_estads.Exp += valor;
				usuario.checkUserLevel();
				usuario.refreshStatus(5);
			} else {
				enviarMensaje(
						"No esta permitido utilizar valores mayores a 999999. Su comando ha quedado en los logs del juego.",
						FontType.INFO);
			}
		} else if (accion.equalsIgnoreCase("BODY")) {
			usuario.m_infoChar.m_cuerpo = (short) valor;
			usuario.enviarCP();
		} else if (accion.equalsIgnoreCase("HEAD")) {
			usuario.m_infoChar.m_cabeza = (short) valor;
			usuario.enviarCP();
		} else if (accion.equalsIgnoreCase("CRI")) {
			usuario.m_faccion.CriminalesMatados = valor;
		} else if (accion.equalsIgnoreCase("CIU")) {
			usuario.m_faccion.CiudadanosMatados = valor;
		} else if (accion.equalsIgnoreCase("LEVEL")) {
			usuario.m_estads.ELV = valor;
		} else {
			enviarMensaje("Comando no permitido o inválido.", FontType.INFO);
		}
	}

	public void doCrearItem(short objid) {
		// Crear Item
		// Comando /CI
		Log.logGM(m_nick, "/CI " + objid + " pos=" + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			if (mapa.hayObjeto(m_pos.x, m_pos.y)) {
				return;
			}
			if (mapa.hayTeleport(m_pos.x, m_pos.y)) {
				return;
			}
			if (server.getInfoObjeto(objid) == null) {
				return;
			}
			mapa.agregarObjeto(objid, 1, m_pos.x, m_pos.y);
		}
	}

	public void doGuardaMapa() {
		// Guardar el mapa actual.
		// Comando /GUARDAMAPA
		Log.logGM(m_nick, "/GUARDAMAPA " + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.saveMapData();
			enviarMensaje("Mapa guardado.", FontType.INFO);
		}
	}

	public void doDestObj() {
		// Destruir el objeto de la posición actual.
		// Comando /DEST
		Log.logGM(m_nick, "/DEST " + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.quitarObjeto(m_pos.x, m_pos.y);
		}
	}

	public void doBloqPos() {
		// Bloquear la posición actual.
		// Comando /BLOQ
		Log.logGM(m_nick, "/BLOQ " + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			if (mapa.estaBloqueado(m_pos.x, m_pos.y)) {
				mapa.desbloquearTerreno(m_pos.x, m_pos.y);
				enviarMensaje("Posicion desbloqueada.", FontType.INFO);
			} else {
				mapa.bloquearTerreno(m_pos.x, m_pos.y);
				enviarMensaje("Posicion bloqueada.", FontType.INFO);
			}
		}
	}

	public void doMassKill() {
		// Quita todos los NPCs del area.
		// Comando /MASSKILL
		Log.logGM(m_nick, "/MASSKILL " + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.quitarNpcsArea(m_pos.x, m_pos.y);
		}
	}

	public void doTrigger(byte t) {
		// Consulta o cambia el trigger de la posición actual.
		// Comando /TRIGGER
		Log.logGM(m_nick, "/TRIGGER " + t + " " + m_pos);
		Map mapa = server.getMapa(m_pos.map);
		mapa.setTrigger(m_pos.x, m_pos.y, t);
		enviarMensaje("Trigger " + mapa.getTrigger(m_pos.x, m_pos.y) + " en " + m_pos, FontType.INFO);
	}

	public void doMassDest() {
		// Quita todos los objetos del area
		// Comando /MASSDEST
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		mapa.objectMassDestroy(m_pos.x, m_pos.y);
		Log.logGM(m_nick, "/MASSDEST ");
	}

	public void doEcharTodosPjs() {
		// Comando /ECHARTODOSPJS
		// Comando para echar a todos los pjs conectados no privilegiados.
		server.echarPjsNoPrivilegiados();
		enviarMensaje("Los PJs no privilegiados fueron echados.", FontType.INFO);
		Log.logGM(m_nick, "/ECHARTODOSPJS");
	}

	public void doBorrarSOS() {
		// Comando /BORRAR SOS
		// Comando para borrar todos pedidos /GM pendientes
		server.getPedidosAyudaGM().clear();
		enviarMensaje("Todos los /GM pendientes han sido eliminados.", FontType.INFO);
		Log.logGM(m_nick, "/BORRAR SOS");
	}

	public void doComandoNave() {
		// Comando /NAVE
		// Comando para depurar la navegacion
		enviarMensaje("Comando deshabilitado o sin efecto en AOJ.", FontType.INFO);
	}

	public void doShowInt() {
		// Comando /SHOW INT
		// Comando para abrir la ventana de config de intervalos en el server.
		enviarMensaje("Comando deshabilitado o sin efecto en AOJ.", FontType.INFO);
	}

	public void doActualizarMandamientosClan(String s) {
		// Comando DESCOD
		server.getGuildMngr().updateCodexAndDesc(this, s);
	}

	// /// Clanes / Guilds
	public void doFundarClan() {
		// Comando /FUNDARCLAN
		if (m_guildInfo.m_fundoClan) {
			enviarMensaje("Ya has fundado un clan, solo se puede fundar uno por personaje.", FontType.INFO);
			return;
		}
		if (server.getGuildMngr().canCreateGuild(this)) {
			// enviar(MSG_SHOWFUN);
		}
	}

	public void doInfoClan() {
		// Comando GLINFO
		if (m_guildInfo.m_esGuildLeader) {
			server.getGuildMngr().sendGuildLeaderInfo(this);
		} else {
			server.getGuildMngr().sendGuildsList(this);
		}
	}

	public void doCrearClan(String guildName) {
		// Comando CIG
		server.getGuildMngr().crearClan(this, guildName);
	}

	public boolean esMiembroClan() {
		return m_guildInfo.m_guildName.length() != 0;
	}

	public void doSalirClan() {
		// Salir del clan.
		// Comando /SALIRCLAN
		if (m_guildInfo.esGuildLeader()) {
			enviarMensaje("Eres líder del clan, no puedes salir del mismo.", FontType.INFO);
		} else if (!esMiembroClan()) {
			enviarMensaje("No perteneces a ningún clan.", FontType.INFO);
		} else {
			m_guild.enviarMensaje(m_nick + " decidió dejar el clan.", FontType.GUILD);
			salirClan();
		}
	}

	public void ingresarClan(String clan) {
		// El usuario ingresa a un clan.
		m_guildInfo.m_guildName = clan;
		m_guildInfo.incIngresos();
	}

	public void salirClan() {
		// El usuario sale del clan.
		m_guild.removeMember(m_nick);
		m_guildInfo.incEchadas();
		m_guildInfo.resetGuild();
	}

	public void doVotarClan(String s) {
		// Comando /VOTO
		server.getGuildMngr().computeVote(this, s);
	}

	public void enviarMensajeVotacion() {
		enviarMensaje("Hoy es la votación para elegir un nuevo lider del clan!!.", FontType.GUILD);
		enviarMensaje("La eleccion durara 24 horas, se puede votar a cualquier miembro del clan.", FontType.GUILD);
		enviarMensaje("Para votar escribe /VOTO NICKNAME.", FontType.GUILD);
		enviarMensaje("Solo se computarÁ un voto por miembro.", FontType.GUILD);
	}

	public void doAceptarOfertaPaz(String s) {
		// Comando ACEPPEAT
		server.getGuildMngr().acceptPeaceOffer(this, s);
	}

	public void doRecibirOfertaPaz(String s) {
		// Comando PEACEOFF
		StringTokenizer st = new StringTokenizer(s, ",");
		String user = st.nextToken();
		String desc = st.nextToken();
		server.getGuildMngr().recievePeaceOffer(this, user, desc);
	}

	public void doEnviarPedidoPaz(String s) {
		// Comnando PEACEDET
		StringTokenizer st = new StringTokenizer(s, ",");
		String user = st.nextToken();
		String desc = st.nextToken();
		server.getGuildMngr().sendPeaceRequest(this, user, desc);
	}

	public void doEnviarPeticion(String s) {
		// Comando ENVCOMEN
		server.getGuildMngr().sendPeticion(this, s);
	}

	public void doEnviarProposiciones() {
		// Comando ENVPROPP
		server.getGuildMngr().sendPeacePropositions(this);
	}

	public void doDeclararGuerra(String s) {
		// Comando DECGUERR
		server.getGuildMngr().declareWar(this, s);
	}

	public void doDeclararAlianza(String s) {
		// Comando DECALIAD
		server.getGuildMngr().declareAllie(this, s);
	}

	public void doSetNewURL(String s) {
		// Comando NEWWEBSI
		server.getGuildMngr().setNewURL(this, s);
	}

	public void doAceptarMiembroClan(String s) {
		// Comando ACEPTARI
		server.getGuildMngr().acceptClanMember(this, s);
	}

	public void doRechazarPedido(String s) {
		// Comando RECHAZAR
		server.getGuildMngr().denyRequest(this, s);
	}

	public void doEcharMiembro(String s) {
		// Comando ECHARCLA
		server.getGuildMngr().echarMember(this, s);
	}

	public void doActualizarGuildNews(String s) {
		// Comando ACTGNEWS
		server.getGuildMngr().updateGuildNews(this, s);
	}

	public void doCharInfoClan(String s) {
		// Comando 1HRINFO<
		server.getGuildMngr().sendCharInfo(this, s);
	}

	public void doSolicitudIngresoClan(String guildName, String desc) {
		// Comando SOLICITUD
		server.getGuildMngr().solicitudIngresoClan(this, guildName, desc);
	}

	public void doClanDetails(String s) {
		// Comando CLANDETAILS
		server.getGuildMngr().sendGuildDetails(this, s);
	}

	public void doUsuariosEnMapa() {
		// Comando /ONLINEMAP
		// Devuelve la lista de usuarios en el mapa.
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		StringBuffer msg = new StringBuffer();
		int cant = 0;
		for (String usuario : mapa.getUsuarios()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append(usuario);
		}
		enviarMensaje("Usuarios en el mapa: " + msg, FontType.INFO);
	}

	public void doUsuariosTrabajando() {
		// Comando /TRABAJANDO
		// Devuelve la lista de usuarios trabajando.
		StringBuffer msg = new StringBuffer();
		int cant = 0;
		for (String usuario : server.getUsuariosTrabajando()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append(usuario);
		}
		enviarMensaje("Usuarios trabajando: " + msg, FontType.INFO);
	}

	public void doPanelGM() {
		// Comando /PANELGM
		// enviar(MSG_ABPANEL);
	}

	public void doListaUsuarios() {
		// Comando LISTUSU
		StringBuffer msg = new StringBuffer();
		for (String usuario : server.getUsuariosConectados()) {
			msg.append(usuario);
			msg.append(",");
		}
		if (msg.length() > 0 && msg.charAt(msg.length() - 1) == ',') {
			msg.deleteCharAt(msg.length() - 1);
		}
		// enviar(MSG_LISTUSU, msg.toString());
	}

	public void doApostar(int cant) {
		// Comando /APOSTAR
		// Comando /APOSTAR basado en la idea de DarkLight,
		// pero con distinta probabilidad de exito.
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_PET);
		if (npc == null) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_TIMBERO) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en apostar.", npc.getId());
			return;
		}
		if (cant < 0) {
			hablar(COLOR_BLANCO, "Has ingresado una apuesta inválida.", npc.getId());
			return;
		}
		if (cant < 1) {
			hablar(COLOR_BLANCO, "El mínimo de apuesta es 1 moneda.", npc.getId());
			return;
		}
		if (cant > APUESTA_MAXIMA) {
			hablar(COLOR_BLANCO, "El máximo de apuesta es " + APUESTA_MAXIMA + " monedas.", npc.getId());
			return;
		}
		if (m_estads.getGold() < cant) {
			hablar(COLOR_BLANCO, "No tienes esa cantidad.", npc.getId());
			return;
		}
		if (Util.Azar(1, 100) <= 45) {
			m_estads.addGold( cant );
			hablar(COLOR_BLANCO, "Felicidades! Has ganado " + cant + " monedas de oro!", npc.getId());
			/*
			 * fixme Apuestas.Perdidas += cant; Call WriteVar(DatPath & "apuestas.dat",
			 * "Main", "Perdidas", CStr(Apuestas.Perdidas))
			 */
		} else {
			m_estads.addGold( -cant );
			hablar(COLOR_BLANCO, "Lo siento, has perdido " + cant + " monedas de oro.", npc.getId());
			/*
			 * fixme Apuestas.Ganancias = Apuestas.Ganancias + N Call WriteVar(DatPath &
			 * "apuestas.dat", "Main", "Ganancias", CStr(Apuestas.Ganancias))
			 */
		}
		/*
		 * fixme Apuestas.Jugadas = Apuestas.Jugadas + 1 Call WriteVar(DatPath &
		 * "apuestas.dat", "Main", "Jugadas", CStr(Apuestas.Jugadas))
		 */
		refreshStatus(1);
	}

	public void doUptime() {
		// Comando /UPTIME
		enviarMensaje("Uptime: " + server.calculateUptime(), FontType.INFO);
	}
	
	public void doPonerMensajeForo(String titulo, String texto) {
		// Comando DEMSG
		if (getFlags().TargetObj == 0) {
			return;
		}
		ObjectInfo iobj = server.getInfoObjeto(getFlags().TargetObj);
		if (iobj.esForo()) {
			server.getForumManager().ponerMensajeForo(iobj.ForoID, titulo, texto);
		}
	}

	public void doArmaduraImperial1(short armadura) {
		// Comando /AI1
		if (armadura < 0) {
			enviarMensaje("Armadura Imperial 1 es " + Factions.ArmaduraImperial1, FontType.INFO);
		} else {
			Factions.ArmaduraImperial1 = armadura;
			enviarMensaje("Armadura Imperial 1 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraImperial2(short armadura) {
		// Comando /AI2
		if (armadura < 0) {
			enviarMensaje("Armadura Imperial 2 es " + Factions.ArmaduraImperial2, FontType.INFO);
		} else {
			Factions.ArmaduraImperial2 = armadura;
			enviarMensaje("Armadura Imperial 2 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraImperial3(short armadura) {
		// Comando /AI3
		if (armadura < 0) {
			enviarMensaje("Armadura Imperial 3 es " + Factions.ArmaduraImperial3, FontType.INFO);
		} else {
			Factions.ArmaduraImperial3 = armadura;
			enviarMensaje("Armadura Imperial 3 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraImperial4(short armadura) {
		// Comando /AI4
		if (armadura < 0) {
			enviarMensaje("Tunica Mago Imperial es " + Factions.TunicaMagoImperial, FontType.INFO);
		} else {
			Factions.TunicaMagoImperial = armadura;
			enviarMensaje("Tunica Mago Imperial actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraImperial5(short armadura) {
		// Comando /AI5
		if (armadura < 0) {
			enviarMensaje("Tunica Mago Imperial Enanos es " + Factions.TunicaMagoImperialEnanos, FontType.INFO);
		} else {
			Factions.TunicaMagoImperialEnanos = armadura;
			enviarMensaje("Tunica Mago Imperial Enanos actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraCaos1(short armadura) {
		// Comando /AC1
		if (armadura < 0) {
			enviarMensaje("Armadura Caos 1 es " + Factions.ArmaduraCaos1, FontType.INFO);
		} else {
			Factions.ArmaduraCaos1 = armadura;
			enviarMensaje("Armadura Caos 1 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraCaos2(short armadura) {
		// Comando /AC2
		if (armadura < 0) {
			enviarMensaje("Armadura Caos 2 es " + Factions.ArmaduraCaos2, FontType.INFO);
		} else {
			Factions.ArmaduraCaos2 = armadura;
			enviarMensaje("Armadura Caos 2 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraCaos3(short armadura) {
		// Comando /AC3
		if (armadura < 0) {
			enviarMensaje("Armadura Caos 3 es " + Factions.ArmaduraCaos3, FontType.INFO);
		} else {
			Factions.ArmaduraCaos3 = armadura;
			enviarMensaje("Armadura Caos 3 actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraCaos4(short armadura) {
		// Comando /AC4
		if (armadura < 0) {
			enviarMensaje("Tunica Mago Caos es " + Factions.TunicaMagoCaos, FontType.INFO);
		} else {
			Factions.TunicaMagoCaos = armadura;
			enviarMensaje("Tunica Mago Caos actualizada.", FontType.INFO);
		}
	}

	public void doArmaduraCaos5(short armadura) {
		// Comando /AC5
		if (armadura < 0) {
			enviarMensaje("Tunica Mago Caos Enanos es " + Factions.TunicaMagoCaosEnanos, FontType.INFO);
		} else {
			Factions.TunicaMagoCaosEnanos = armadura;
			enviarMensaje("Tunica Mago Caos Enanos actualizada.", FontType.INFO);
		}
	}

	public void doMascotas(String s) {
		// Comando /MASCOTAS
		// Informa cantidad, nombre y ubicación de las mascotas.
		Client usuario;
		if (!"".equals(s)) {
			usuario = server.getUsuario(s);
		} else {
			usuario = this;
		}
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		enviarMensaje(usuario.getNick() + " tiene " + m_cantMascotas + " mascotas.", FontType.DEBUG);
		for (int i = 0; i < m_mascotas.length; i++) {
			if (m_mascotas[i] != null) {
				enviarMensaje(" mascota " + m_mascotas[i].getName() + "[" + (i + 1) + "] esta en "
						+ m_mascotas[i].getPos() + " tiempo=" + m_mascotas[i].getContadores().TiempoExistencia,
						FontType.DEBUG);
			}
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
		if (getPos().distance(npc.getPos()) > 4) {
			hablar(COLOR_BLANCO, "Jeje, acércate o no podré escucharte. ¡Estás demasiado lejos!", npc.getId());
			return;
		}
		if (!npc.esFaccion()) {
			getFaccion().enlistarArmadaReal(npc);
		} else {
			getFaccion().enlistarCaos(npc);
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
			if (!getFaccion().ArmadaReal) {
				hablar(COLOR_BLANCO, "No perteneces a las tropas reales!!!", npc.getId());
				return;
			}
			getFaccion().recompensaArmadaReal(npc);
		} else {
			if (!getFaccion().FuerzasCaos) {
				hablar(COLOR_BLANCO, "No perteneces a las fuerzas del caos!!!", npc.getId());
				return;
			}
			getFaccion().recompensaCaos(npc);
		}
	}

	public void doCrearTeleport(short dest_mapa, short dest_x, short dest_y) {
		// Comando /CT mapa_dest x_dest y_dest
		// Crear Teleport
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		// Se creara el Teleport una celda arriba de la posicion del GM.
		short x = getPos().x;
		short y = (short) (getPos().y - 1);
		if (mapa.hayObjeto(x, y)) {
			enviarMensaje("Lo siento, no hay lugar para crear un teleport arriba del usuario. Prueba en otro lugar.",
					FontType.WARNING);
			return;
		}
		if (mapa.hayTeleport(x, y)) {
			enviarMensaje("Lo siento, ya hay un teleport arriba del usuario. Prueba en otro lugar.", FontType.WARNING);
			return;
		}
		Map mapaDest = server.getMapa(dest_mapa);
		if (mapaDest == null || !Pos.isValid(dest_x, dest_y)) {
			enviarMensaje("Ups! Debes indicar coordenadas válidas.", FontType.WARNING);
			return;
		}
		mapa.crearTeleport(x, y, dest_mapa, dest_x, dest_y);
		Log.logGM(m_nick, "Creó un teleport: " + m_pos + " que apunta a: " + dest_mapa + " " + dest_x + " " + dest_y);
		enviarMensaje("¡Teleport creado!", FontType.INFO);
	}

	public void doDestruirTeleport() {
		// Comando /DT
		// Destruir un teleport, toma el ultimo clic
		if (getFlags().TargetMap == 0 || getFlags().TargetX == 0 || getFlags().TargetY == 0) {
			enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.WARNING);
			return;
		}
		short m = getFlags().TargetMap;
		Map mapa = server.getMapa(m);
		if (mapa == null) {
			enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.WARNING);
			return;
		}
		short x = getFlags().TargetX;
		short y = getFlags().TargetY;
		if (!mapa.hayTeleport(x, y)) {
			enviarMensaje("¡Debes hacer clic sobre el Teleport que deseas destruir!", FontType.WARNING);
			return;
		}
		Log.logGM(m_nick, "Destruyó un teleport, con /DT mapa=" + m + " x=" + x + " y=" + y);
		mapa.destruirTeleport(x, y);
	}

	public void doMataNpc() {
		// Quitar Npc
		// Comando /MATA indiceNpc
		Npc npc = server.getNPC(m_flags.TargetNpc);
		if (npc == null) {
			enviarMensaje("Debés hacer clic sobre un Npc y luego escribir /MATA. PERO MUCHO CUIDADO!", FontType.INFO);
			return;
		}
		npc.quitarNPC();
		Log.logGM(m_nick, "/MATA " + npc);
	}

	public void doCrearCriatura(short indiceNpc) {
		// Crear criatura, toma directamente el indice
		// Comando /ACC indiceNpc
		if (server.getNPC(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, m_pos, true, false);
		} else {
			enviarMensaje("Indice de Npc invalido.", FontType.INFO);
		}
	}

	public void doCrearCriaturaRespawn(short indiceNpc) {
		// Crear criatura con respawn, toma directamente el indice
		// Comando /RACC indiceNpc
		if (server.getNPC(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, m_pos, true, true);
		} else {
			enviarMensaje("Indice de Npc invalido.", FontType.INFO);
		}
	}

	public void doEnviarMiniEstadisticas() {
		enviar(ServerPacketID.littleStats, (int) m_faccion.CiudadanosMatados, (int) m_faccion.CriminalesMatados,
				(int) m_estads.usuariosMatados, (int) m_estads.NPCsMuertos, m_clase.getName(), (int) m_counters.Pena);

		sendUserAtributos();
		enviarSkills();
		doEnviarFama();

	}

	public boolean isLogged() {
		return m_flags.UserLogged;
	}

	public void doPASSDAY() {
		// Comando /PASSDAY
		Log.logGM(getNick(), "/PASSDAY");
		server.getGuildMngr().dayElapsed();
	}

	public void doBackup() {
		// Comando /DOBACKUP
		// Hacer un backup del mundo.
		server.doBackup();
	}

	public void doGrabar() {
		// Comando /GRABAR
		// Guardar todos los usuarios conectados.
		server.guardarUsuarios();
	}

	public void doApagar() {
		// Comando /APAGAR
		log.info("SERVIDOR APAGADO POR " + m_nick);
		Log.logGM(getNick(), "APAGO EL SERVIDOR");
		server.shutdown();
	}

	public void doLluvia() {
		// Comando /LLUVIA
		if (server.estaLloviendo()) {
			server.detenerLluvia();
		} else {
			server.iniciarLluvia();
		}
	}

	public void doSystemMsg(String s) {
		// Mensaje del sistema
		// Comando /SMSG
		s = s.trim();
		Log.logGM(m_nick, "Envió el mensaje del sistema: " + s);
		// server.enviarATodos(MSG_SYSMSG, s);
	}

	public void doBanIP(String s) {
		// Ban x IP
		// Comando /BANIP
		Client usuario = server.getUsuario(s);
		String ip = "";
		if (usuario == null) {
			Log.logGM(m_nick, "hizo /BanIP " + s);
			ip = s;
		} else {
			Log.logGM(m_nick, "hizo /BanIP " + s + " - " + usuario.m_ip);
			ip = usuario.m_ip;
		}
		List<String> bannedIPs = server.getBannedIPs();
		if (bannedIPs.contains(ip)) {
			enviarMensaje("La IP " + ip + " ya se encuentra en la lista de bans.", FontType.INFO);
			return;
		}
		bannedIPs.add(ip);
		server.enviarMensajeAAdmins(m_nick + " Baneo la IP " + ip, FontType.FIGHT);
		if (usuario != null) {
			server.logBan(usuario.m_nick, m_nick, "Ban por IP desde Nick");
			server.enviarMensajeAAdmins(m_nick + " echo a " + usuario.m_nick + ".", FontType.FIGHT);
			server.enviarMensajeAAdmins(m_nick + " Banned a " + usuario.m_nick + ".", FontType.FIGHT);
			// Ponemos el flag de ban a 1
			usuario.m_flags.Ban = true;
			Log.logGM(m_nick, "Echo a " + usuario.m_nick);
			Log.logGM(m_nick, "BAN a " + usuario.m_nick);
			usuario.doSALIR();
		}
	}

	public void doUnbanIP(String s) {
		// Desbanea una IP
		// Comando /UNBANIP
		Log.logGM(m_nick, "/UNBANIP " + s);
		List<String> bannedIPs = server.getBannedIPs();
		if (bannedIPs.contains(s)) {
			bannedIPs.remove(s);
			enviarMensaje("La IP " + s + " se ha quitado de la lista de bans.", FontType.INFO);
		} else {
			enviarMensaje("La IP " + s + " NO se encuentra en la lista de bans.", FontType.INFO);
		}
	}

	public void doNick2IP(String s) {
		// Comando /NICK2IP
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		enviarMensaje("El ip de " + s + " es " + usuario.m_ip, FontType.INFO);
	}

	public void doIP2Nick(String s) {
		// Comando /IP2NICK
		List<String> usuarios = server.getUsuarioConIP(s);
		if (usuarios.isEmpty()) {
			enviarMensaje("No hay usuarios con dicha ip", FontType.INFO);
		} else {
			StringBuffer nicks = new StringBuffer();
			for (String usuario : usuarios) {
				if (nicks.length() == 0) {
					nicks.append(usuario);
				} else {
					nicks.append(", ").append(usuario);
				}
			}
			enviarMensaje("Nicks: " + nicks, FontType.INFO);
		}
	}

	public String getIP() {
		return m_ip;
	}

	public void doResetInv() {
		// Resetea el inventario
		// Comando /RESETINV
		if (m_flags.TargetNpc == 0) {
			return;
		}
		Npc npc = server.getNPC(m_flags.TargetNpc);
		npc.getInv().clear();
		enviarMensaje("El inventario del npc " + npc.getName() + " ha sido vaciado.", FontType.INFO);
		Log.logGM(m_nick, "/RESETINV " + npc.toString());
	}

	public void doLimpiarMundo() {
		// Comando /LIMPIAR
		server.limpiarMundo(this);
	}

	public void doRMSG(String s) {
		// Mensaje del servidor
		// Comando /RMSG
		Log.logGM(m_nick, "Mensaje Broadcast: " + s);
		if (!s.equals("")) {
			// server.enviarATodos(MSG_TALK, s + FontType.TALK);
		}
	}

	public void sendSpawnList() {
		// Crear criatura
		// Comando /CC
		// Sub EnviarSpawnList(ByVal UserIndex As Integer)
		List<String> params = new LinkedList<String>();
		params.add("" + server.getSpawnList().length);
		for (String name : server.getSpawnListNames()) {
			params.add(name);
		}
		// enviar(MSG_SPL, params.toArray());
	}

	public void doSPA(short index) {
		// Spawn una criatura !!!!!
		// SPA
		short[] spawnList = server.getSpawnList();
		if (index > 0 && index <= spawnList.length) {
			Npc npc = Npc.spawnNpc(spawnList[index - 1], m_pos, true, false);
			Log.logGM(m_nick, "Sumoneo al Npc " + npc.toString());
		}
	}

	public void doSeguir() {
		// Comando /SEGUIR
		if (m_flags.TargetNpc > 0) {
			Npc npc = server.getNPC(m_flags.TargetNpc);
			npc.seguirUsuario(m_nick);
		}
	}

	public void doSUM(String s) {
		// Comando /SUM usuario
		if (s.length() == 0) {
			return;
		}
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("El usuario esta offline.", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "Hizo /SUM " + s);
		if (usuario.warpUser(m_pos.map, m_pos.x, m_pos.y, true)) {
			enviarMensaje(usuario.m_nick + " ha sido trasportado.", FontType.INFO);
			usuario.enviarMensaje("Has sido trasportado.", FontType.INFO);
			Log.logGM(m_nick, "/SUM " + usuario.m_nick + " Map:" + m_pos.map + " X:" + m_pos.x + " Y:" + m_pos.y);
		}
	}

	public void doBan(String nombre, String motivo) {
		// Comando /BAN
		Client usuario = server.getUsuario(nombre);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > m_flags.Privilegios) {
			enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/BAN " + nombre + " por: " + motivo);
		server.logBan(usuario.m_nick, m_nick, motivo);
		server.enviarMensajeAAdmins(m_nick + " echo a " + usuario.m_nick + ".", FontType.FIGHT);
		server.enviarMensajeAAdmins(m_nick + " Banned a " + usuario.m_nick + ".", FontType.FIGHT);
		// Ponemos el flag de ban a 1
		usuario.m_flags.Ban = true;
		if (usuario.esGM()) {
			m_flags.Ban = true;
			doSALIR();
			server.enviarMensajeAAdmins(m_nick + " banned from this server por bannear un Administrador.",
					FontType.FIGHT);
		}
		Log.logGM(m_nick, "Echo a " + usuario.m_nick);
		Log.logGM(m_nick, "BAN a " + usuario.m_nick);
		usuario.doSALIR();
	}

	public void doUnban(String s) {
		// Comando /UNBAN
		Log.logGM(m_nick, "/UNBAN " + s);
		server.unBan(s);
		Log.logGM(m_nick, "Hizo /UNBAN a " + s);
		enviarMensaje(s + " unbanned.", FontType.INFO);
	}

	public void doEchar(String s) {
		// Echar usuario
		// Comando /ECHAR
		Log.logGM(m_nick, "quizo /ECHAR a " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > m_flags.Privilegios) {
			enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		// server.enviarATodos(MSG_TALK, m_nick + " echo a "
		// + usuario.m_nick + ".", FontType.INFO.toString());
		// usuario.doSALIR();
		Log.logGM(m_nick, "Echó a " + usuario.m_nick);
	}

	public void doEncarcelar(String s) {
		// Comando /CARCEL
		Log.logGM(m_nick, "quizo /CARCEL " + s);
		StringTokenizer st = new StringTokenizer(s, " ");
		short minutos;
		String nombre;
		try {
			minutos = Short.parseShort(st.nextToken());
			nombre = st.nextToken();
		} catch (Exception e) {
			enviarMensaje("Error en el comando. Formato: /CARCEL minutos usuario", FontType.WARNING);
			return;
		}
		Client usuario = server.getUsuario(nombre);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > m_flags.Privilegios) {
			enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		if (usuario.m_counters.Pena > 0) {
			enviarMensaje("El usuario ya esta en la carcel. Le quedan " + m_counters.Pena + " minutos.",
					FontType.WARNING);
			return;
		}
		if (minutos > 30) {
			enviarMensaje("No puedes encarcelar por mas de 30 minutos!", FontType.INFO);
			return;
		}
		usuario.encarcelar(minutos, m_nick);
	}

	public void doPerdonar(String s) {
		// Comando /PERDON usuario
		// Perdonar a un usuario. Volverlo cuidadano.
		Log.logGM(m_nick, "quizo /PERDON " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.esNewbie()) {
			if (usuario.m_reputacion.esIntachable()) {
				enviarMensaje("No hay que perdonarle a " + usuario.getNick(), FontType.INFO);
				return;
			}
			usuario.volverCiudadano();
			enviarMensaje(usuario.getNick() + " ha sido perdonado.", FontType.INFO);
			usuario.enviarMensaje("Los dioses te han perdonado por esta vez.", FontType.INFO);
		} else {
			Log.logGM(m_nick, "Intento perdonar un personaje de nivel avanzado.");
			enviarMensaje("Solo se permite perdonar newbies.", FontType.INFO);
		}
	}

	public void doCondenar(String s) {
		// Comando /CONDEN usuario
		// Condenar a un usuario. Volverlo criminal.
		Log.logGM(m_nick, "quizo /CONDEN " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_reputacion.esCriminal()) {
			enviarMensaje(usuario.getNick() + " ya es un criminal condenado.", FontType.INFO);
			return;
		}
		usuario.volverCriminal();
		enviarMensaje(usuario.getNick() + " ha sido condenado.", FontType.INFO);
		usuario.enviarMensaje("Los dioses te han condenado por tus acciones.", FontType.INFO);
	}

	public void doRevivir(String s) {
		// Comando /REVIVIR
		Log.logGM(m_nick, "quizo /REVIVIR " + s);
		Client usuario;
		if (!s.equalsIgnoreCase("YO") && s.length() > 0) {
			usuario = server.getUsuario(s);
			if (usuario == null) {
				enviarMensaje("Usuario offline.", FontType.INFO);
				return;
			}
		} else {
			usuario = this;
		}
		if (usuario.isAlive()) {
			enviarMensaje(usuario.m_nick + " no esta muerto!", FontType.INFO);
		} else {
			usuario.revivirUsuario();
			usuario.enviarMensaje(m_nick + " te ha resucitado.", FontType.INFO);
			Log.logGM(m_nick, "Resucitó a " + usuario.m_nick);
		}
	}

	public void doONLINEGM() {
		// Comando /ONLINEGM
		if (!esGM()) {
			return;
		}
		List<String> gms = server.getGMsOnline();
		if (gms.size() > 0) {
			StringBuffer msg = new StringBuffer();
			for (String usuario : gms) {
				if (msg.length() > 0) {
					msg.append(", ");
				}
				msg.append(usuario);
			}
			enviarMensaje("GM online: " + msg.toString(), FontType.INFO);
		} else {
			enviarMensaje("No hay GMs online.", FontType.INFO);
		}
	}

	public void doInfoUsuario(String s) {
		// INFO DE USER
		// Comando /INFO usuario
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/INFO " + s);
		sendUserStatsTxt(usuario);
	}

	private void sendUserStatsTxt(Client usuario) {
		enviarMensaje("Estadisticas de: " + usuario.m_nick, FontType.INFO_B);
		enviarMensaje("Nivel: " + usuario.m_estads.ELV + "  EXP: " + usuario.m_estads.Exp + "/" + usuario.m_estads.ELU,
				FontType.INFO);
		enviarMensaje("Salud: " + usuario.m_estads.MinHP + "/" + usuario.m_estads.MaxHP + "  Mana: "
				+ usuario.m_estads.mana + "/" + usuario.m_estads.maxMana + "  Energia: " + usuario.m_estads.stamina
				+ "/" + usuario.m_estads.maxStamina, FontType.INFO);
		if (usuario.m_inv.tieneArmaEquipada()) {
			enviarMensaje("Menor Golpe/Mayor Golpe: " + usuario.m_estads.MinHIT + "/" + usuario.m_estads.MaxHIT + " ("
					+ m_inv.getArma().MinHIT + "/" + m_inv.getArma().MaxHIT + ")", FontType.INFO);
		} else {
			enviarMensaje("Menor Golpe/Mayor Golpe: " + usuario.m_estads.MinHIT + "/" + usuario.m_estads.MaxHIT,
					FontType.INFO);
		}
		if (usuario.m_inv.tieneArmaduraEquipada()) {
			enviarMensaje("(CUERPO) Min Def/Max Def: " + usuario.m_inv.getArmadura().MinDef + "/"
					+ usuario.m_inv.getArmadura().MaxDef, FontType.INFO);
		} else {
			enviarMensaje("(CUERPO) Min Def/Max Def: 0", FontType.INFO);
		}
		if (usuario.m_inv.tieneCascoEquipado()) {
			enviarMensaje("(CABEZA) Min Def/Max Def: " + m_inv.getCasco().MinDef + "/" + m_inv.getCasco().MaxDef,
					FontType.INFO);
		} else {
			enviarMensaje("(CABEZA) Min Def/Max Def: 0", FontType.INFO);
		}
		if (esMiembroClan()) {
			enviarMensaje("Clan: " + usuario.m_guildInfo.m_guildName, FontType.INFO);
			if (usuario.m_guildInfo.m_esGuildLeader) {
				if (usuario.m_guildInfo.m_clanFundado.equals(usuario.m_guildInfo.m_guildName)) {
					enviarMensaje("Status: Fundador/Lider", FontType.INFO);
				} else {
					enviarMensaje("Status: Lider", FontType.INFO);
				}
			} else {
				enviarMensaje("Status: " + usuario.m_guildInfo.m_guildPoints, FontType.INFO);
			}
			enviarMensaje("User GuildPoints: " + usuario.m_guildInfo.m_guildPoints, FontType.INFO);
		}
		enviarMensaje("Oro: " + usuario.m_estads.getGold() + "  Posicion: " + usuario.m_pos.x + "," + usuario.m_pos.y
				+ " en mapa " + usuario.m_pos.map, FontType.INFO);
	}

	public void doInvUser(String s) {
		// Inventario del usuario.
		// Comando /INV
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/INV " + s);
		sendUserInvTxt(usuario);
	}

	private void sendUserInvTxt(Client usuario) {
		enviarMensaje(usuario.m_nick, FontType.INFO);
		enviarMensaje(" Tiene " + usuario.m_inv.getCantObjs() + " objetos.", FontType.INFO);
		for (int i = 1; i <= usuario.m_inv.getSize(); i++) {
			if (usuario.m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info = server.getInfoObjeto(usuario.m_inv.getObjeto(i).objid);
				enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.m_inv.getObjeto(i).cant,
						FontType.INFO);
			}
		}
	}

	public void doBovUser(String s) {
		// Boveda del usuario
		// Comando /BOV
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/BOV " + s);
		sendUserBovTxt(usuario);
	}

	private void sendUserBovTxt(Client usuario) {
		enviarMensaje(usuario.m_nick, FontType.INFO);
		enviarMensaje(" Tiene " + usuario.m_bancoInv.getCantObjs() + " objetos.", FontType.INFO);
		for (int i = 1; i <= usuario.m_bancoInv.getSize(); i++) {
			if (usuario.m_bancoInv.getObjeto(i).objid > 0) {
				ObjectInfo info = server.getInfoObjeto(usuario.m_bancoInv.getObjeto(i).objid);
				enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.m_bancoInv.getObjeto(i).cant,
						FontType.INFO);
			}
		}
	}

	public void doSkillsUser(String s) {
		// Skills del usuario
		// Comando /SKILLS
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(m_nick, "/SKILLS " + s);
		sendUserSkillsTxt(usuario);
	}

	private void sendUserSkillsTxt(Client usuario) {
		enviarMensaje(usuario.m_nick, FontType.INFO);
		for (int i = 1; i <= Skill.MAX_SKILLS; i++) {
			enviarMensaje(" " + Skill.skillsNames[i] + " = " + usuario.m_estads.userSkills[i], FontType.INFO);
		}
	}

	public void doMensajeALosGM(String s) {
		// Mensaje para los GMs
		if (!esGM()) {
			return;
		}
		if (s.length() > 0) {
			Log.logGM(m_nick, "Mensaje para GMs: " + s);
			server.enviarMensajeALosGMs(m_nick + "> " + s);
		}
	}

	public void doPedirAyudaAlGM(String s) {
		// Comando /GM
		// Pedir ayuda a los GMs.
		List<GmRequest> pedidos = server.getPedidosAyudaGM();
		GmRequest pedido = new GmRequest(m_nick, s);
		if (!pedidos.contains(pedido)) {
			pedidos.add(pedido);
			enviarMensaje("El mensaje ha sido entregado, ahora solo debes esperar que se desocupe algun GM.",
					FontType.INFO);
		} else {
			pedidos.remove(pedido);
			pedidos.add(pedido);
			enviarMensaje(
					"Ya habias mandado un mensaje, tu mensaje ha sido movido al final de la cola de mensajes. Ten paciencia.",
					FontType.INFO);
		}
	}

	public void doGuardarComentario(String s) {
		// Comando /REM comentario
		Log.logGM(m_nick, "Hace el comentario: " + s);
		enviarMensaje("Comentario salvado...", FontType.INFO);
	}

	public void doEnviarHora() {
		// Comando /HORA
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
		java.util.Date ahora = new java.util.Date();
		String fecha = df.format(ahora);
		df = new java.text.SimpleDateFormat("HH:mm");
		String hora = df.format(ahora);
		Log.logGM(m_nick, "consultó la /HORA");
		enviarMensaje("Hora: " + hora + " Fecha: " + fecha, FontType.INFO);
	}

	public void doDonde(String s) {
		// Comando /DONDE
		// ¿Donde esta fulano?
		s = s.trim();
		Client usuario;
		if (s.length() == 0) {
			usuario = this;
		} else {
			usuario = server.getUsuario(s);
			if (usuario == null) {
				enviarMensaje("Usuario offline.", FontType.INFO);
				return;
			}
			Log.logGM(m_nick, "consultó /DONDE " + usuario.m_nick);
		}
		enviarMensaje("Ubicacion de " + usuario.m_nick + ": " + usuario.m_pos.map + ", " + usuario.m_pos.x + ", "
				+ usuario.m_pos.y + ".", FontType.INFO);
	}

	public void doEnviarCantidadHostiles(short m) {
		if (m < 1) {
			enviarMensaje("Has ingresado un número de mapa inválido.", FontType.INFO);
			return;
		}
		Map mapa = server.getMapa(m);
		if (mapa != null) {
			Log.logGM(m_nick, "Consultó el número de enemigos en el mapa, /NENE " + m);
			// enviar(MSG_NENE, mapa.getCantHostiles());
		} else {
			enviarMensaje("El mapa no existe.", FontType.INFO);
		}
	}

	public void doTeleploc() {
		// Comando /TELEPLOC
		if (warpUser(m_flags.TargetMap, m_flags.TargetX, m_flags.TargetY, true)) {
			Log.logGM(m_nick,
					"hizo un /TELEPLOC a x=" + m_flags.TargetX + " y=" + m_flags.TargetY + " mapa=" + m_pos.map);
		}
	}

	public void doTeleportUsuario(String nombre, short m, short x, short y) {
		// Comando /TELEP
		// Teleportar
		if (m < 1) {
			enviarMensaje("Parámetros incorrectos: /TELEP usuario mapa x y", FontType.WARNING);
			return;
		}
		Map mapa = server.getMapa(m);
		if (mapa == null) {
			return;
		}
		if (nombre.length() == 0) {
			return;
		}

		Client usuario = this;
		if (!nombre.equalsIgnoreCase("YO")) {
			if (m_flags.Privilegios < 2) {
				return;
			}
			usuario = server.getUsuario(nombre);
		}
		if (!Pos.isValid(x, y)) {
			return;
		}
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.warpUser(m, x, y, true)) {
			usuario.enviarMensaje(usuario.m_nick + " transportado.", FontType.INFO);
			Log.logGM(m_nick,
					"Transportó con un /TELEP a " + usuario.m_nick + " hacia el mapa=" + m + " x=" + x + " y=" + y);
		}
	}

	public void doMostrarAyuda() {
		// Comando /SHOW SOS
		for (GmRequest pedido : server.getPedidosAyudaGM()) {
			// enviar("RSOS" + pedido.usuario + ": " + pedido.msg);
			// enviar(MSG_RSOS, pedido.usuario);
		}
		// enviar(MSG_MSOS);
	}

	public void doFinAyuda(String s) {
		// Comando SOSDONE
		// String nombre = s.substring(0, s.indexOf(':'));
		server.getPedidosAyudaGM().remove(new GmRequest(s, ""));
	}

	public void doIrAUsuario(String s) {
		// Comando /IRA
		if (s.length() == 0) {
			return;
		}
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (warpUser(usuario.m_pos.map, usuario.m_pos.x, usuario.m_pos.y, true)) {
			if (!m_flags.AdminInvisible) {
				usuario.enviarMensaje(m_nick + " se ha trasportado hacia donde te encuentras.", FontType.INFO);
			}
			Log.logGM(m_nick, "Hizo un /IRA " + usuario.m_nick + " mapa=" + usuario.m_pos.map + " x=" + usuario.m_pos.x
					+ " y=" + usuario.m_pos.y);
		}
	}

	public void doHacerInvisible() {
		// Comando /INVISIBLE
		doAdminInvisible();
		Log.logGM(m_nick, "Hizo un /INVISIBLE");
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
			npc.m_movement = Npc.MOV_ESTATICO;
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
			npc.followAmo();
			npc.expresar();
		}
	}

	public void doAyuda() {
		// Comando /AYUDA
		String[] ayuda = server.readHelp();
		for (String element : ayuda) {
			enviarMensaje(element, FontType.INFO);
		}
	}

	public void doDepositarOroBanco(int cant) {
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

	public void doRetirar(int cant) {
		// Comando /RETIRAR
		// Este comando tiene 2 usos:
		// a) Retirar oro del banco.
		// b) Salir de la facción Armada/Caos
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_CASHIER);
		if (npc != null) {
			if (npc.isBankCashier()) {
				((NpcCashier)npc).retirarOroBanco(this, cant);
			} else if (npc.esNoble()) {
				retirarUsuarioFaccion(npc);
			}
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

	public void doBoveda() {
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
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.INFO);
			}
		}
	}

	
	public Npc getNearNpcSelected(int distance) {
		// Se asegura que el target es un npc
		if (m_flags.TargetNpc == 0) {
			enviarMensaje("Debes seleccionar un personaje cercano para poder interactuar.", FontType.INFO);
		} else {
			Npc npc = server.getNPC(m_flags.TargetNpc);
			if (checkNpcNear(npc, distance)) {
				return npc;
			}
		}
		return null;
	}
	
	private boolean checkNpcNear(Npc npc, int distance) {
		if (npc.getPos().distance(m_pos) > distance) {
			enviarMensaje("Estas demasiado lejos.", FontType.INFO);
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
			enviarMensaje(message, FontType.INFO);
			return false;
		}
	}

	public void doDepositarBoveda(short slot, int cant) {
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
				userDepositaItem(slot, cant);
			} else {
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.INFO);
			}
		}
	}

	public void doRetirarBoveda(short slot, int cant) {
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
				enviarMensaje("No te puedo ayudar. Busca al banquero.", FontType.INFO);
			}
		}
	}

	private void userDepositaItem(short slot, int cant) {
		// El usuario deposita un item
		refreshStatus(1);
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
		for (int i = 1; i <= m_bancoInv.getSize(); i++) {
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
			enviarMensaje("No tienes mas espacio en el banco!!", FontType.INFO);
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
		refreshStatus(1);
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
		for (short i = 1; i <= m_inv.getSize(); i++) {
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
			enviarMensaje("No podés tener mas objetos.", FontType.INFO);
			return;
		}
		// Mete el obj en el slot
		if (m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			m_inv.getObjeto(slot_inv).objid = objid;
			m_inv.getObjeto(slot_inv).cant += cant;
			quitarBancoInvItem(slot, cant);
		} else {
			enviarMensaje("No podés tener mas objetos.", FontType.INFO);
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
		enviar(ServerPacketID.bancoOk);
	}

	private void iniciarDeposito() {
		// Hacemos un Update del inventario del usuario
		updateBankUserInv();
		// Actualizamos el dinero
		refreshStatus(1);

		enviar(ServerPacketID.wBank);

		m_flags.Comerciando = true;
	}

	private void updateBankUserInv(short slot) {
		// Actualiza un solo slot
		// Actualiza el inventario
		if (m_bancoInv.getObjeto(slot).objid > 0) {
			sendBanObj(slot, m_bancoInv.getObjeto(slot));
		} else {
			enviar(ServerPacketID.oBank, (byte) slot, (short) 0);
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
			ObjectInfo info = server.getInfoObjeto(obj_inv.objid);
			enviar(ServerPacketID.oBank, (byte) slot, info.ObjIndex, info.Nombre, obj_inv.cant, info.GrhIndex,
					(byte) info.ObjType, info.MaxHIT, info.MinHIT, info.MaxDef);
		} else {
			// enviar(serverPacketID.oBank, (byte) slot, (short) 0);
		}
	}

	public void doFinBanco() {
		// Comando FINBAN
		// User sale del modo BANCO
		m_flags.Comerciando = false;
		enviar(ServerPacketID.fBank);
	}

	public void cambiarPasswd(String s) {
		// Comando /PASSWD
		s = s.trim();
		if (s.length() < 6) {
			enviarMensaje("El password debe tener al menos 6 caracteres.", FontType.INFO);
		} else if (s.length() > 20) {
			enviarMensaje("El password puede tener hasta 20 caracteres.", FontType.INFO);
		} else {
			enviarMensaje("El password ha sido cambiado. ¡Cuídalo!", FontType.INFO);
			m_password = s;
		}
	}

	public void doConstruyeHerreria(short objid) {
		if (objid < 1) {
			return;
		}
		ObjectInfo info = server.getInfoObjeto(objid);
		if (info.SkHerreria == 0) {
			return;
		}
		herreroConstruirItem(objid);
	}

	public void doConstruyeCarpinteria(short objid) {
		if (objid < 1) {
			return;
		}
		ObjectInfo info = server.getInfoObjeto(objid);
		if (info.SkCarpinteria == 0) {
			return;
		}
		carpinteroConstruirItem(objid);
	}

	public void doEntrenarMascota(String s) {
		// Comando ENTR
		// Entrenar con una mascota.
		if (!checkAlive()) {
			return;
		}
		if (m_flags.TargetNpc == 0) {
			return;
		}
		Npc npc = server.getNPC(m_flags.TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
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
			short slot = Short.parseShort(s);			
			npcTrainer.spawnTrainerPet(slot, npc);
		}
	}

	public void doComprar(byte slot, short cant) {
		// Comando COMP
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (m_flags.TargetNpc == 0) {
			return;
		}
		Npc npc = server.getNPC(m_flags.TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.comercia()) {
			// npc.hablarAlArea(COLOR_BLANCO, "No tengo ningun interes en
			// comerciar.");
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}
		// StringTokenizer st = new StringTokenizer(s, ",");
		// short slot = Short.parseShort(st.nextToken());
		// int cant = Short.parseShort(st.nextToken());
		if (slot < 1 || slot > npc.getInv().getSize()) {
			return;
		}
		// User compra el item del slot rdata
		
		npc.venderItem(this, slot, cant);
	}

	public void doVender(byte slot, short cant) {
		// Comando VEND
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (m_flags.TargetNpc == 0) {
			return;
		}
		Npc npc = server.getNPC(m_flags.TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.comercia()) {
			hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}

		if (slot < 1 || slot > npc.getInv().getSize()) {
			return;
		}
		// User vende el item del slot rdata
		npc.comprarItem(this, slot, cant);
	}

	public void userCompraObj(Npc npc, short slot, int cant) {
		if (npc.getInv().getObjeto(slot).cant <= 0) {
			return;
		}
		short objid = npc.getInv().getObjeto(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= m_inv.getSize(); i++) {
			if (m_inv.getObjeto(i).objid == objid && (m_inv.getObjeto(i).cant + cant) <= MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = m_inv.getSlotLibre();
			if (slot_inv == 0) {
				enviarMensaje("No podés tener mas objetos.", FontType.INFO);
				return;
			}
		}
		// Mete el obj en el slot
		if (m_inv.getObjeto(slot_inv).cant + cant <= MAX_INVENTORY_OBJS) {
			// Menor que MAX_INV_OBJS
			m_inv.getObjeto(slot_inv).objid = objid;
			m_inv.getObjeto(slot_inv).cant += cant;
			ObjectInfo info = server.getInfoObjeto(objid);
			// Le sustraemos el valor en oro del obj comprado
			double infla = (npc.m_inflacion * info.Valor) / 100.0;
			double dto = m_flags.Descuento;
			if (dto == 0) {
				dto = 1; // evitamos dividir por 0!
			}
			double unidad = ((info.Valor + infla) / dto);
			int monto = (int) (unidad * cant);
			m_estads.addGold( -monto );
			// tal vez suba el skill comerciar ;-)
			subirSkill(Skill.SKILL_Comerciar);
			if (info.ObjType == OBJTYPE_LLAVES) {
				Log.logVentaCasa(m_nick + " compro " + info.Nombre);
			}
			npc.quitarNpcInvItem(slot, cant);
		} else {
			enviarMensaje("No podés tener mas objetos.", FontType.INFO);
		}
	}

	public void updateVentanaComercio(short slot, short npcInv) {
		// enviar(MSG_TRANSOK, slot, npcInv);
		enviar(ServerPacketID.tradeOk);

	}

	public void doFinComerciar() {
		m_flags.Comerciando = false;
		enviar(ServerPacketID.MSG_FINCOM);
	}

	public double descuento() {
		// Establece el descuento en funcion del skill comercio
		final double indicesDto[] = { 1.0, // 0-5
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
		int ptsComercio = m_estads.userSkills[Skill.SKILL_Comerciar];
		m_flags.Descuento = indicesDto[(short) (ptsComercio / 5)];
		return m_flags.Descuento;
	}

	public void doComerciar() {
		// Comando /COMERCIAR
		if (!checkAlive()) {
			return;
		}
		if (esConsejero()) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		Npc npc = getNearNpcSelected(DISTANCE_MERCHANT);
		if (npc == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.comercia()) {
			if (npc.getDesc().length() > 0) {
				hablar(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			}
			return;
		}
		// Iniciamos el comercio con el Npc
		iniciarComercioNPC(npc);
		// ///// FIXME - TODO
	}

	public void iniciarComercioNPC(Npc npc) {
		// Mandamos el Inventario
		npc.enviarNpcInv(this);
		// Hacemos un Update del inventario del usuario
		enviarInventario();
		// Atcualizamos el dinero
		refreshStatus(1);
		// Mostramos la ventana pa' comerciar y ver ladear la osamenta. jajaja
		// enviar(MSG_INITCOM);
		m_flags.Comerciando = true;
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
			((NpcTrainer)npc).enviarListaCriaturas(this);
		}
	}

	public void doDescansar() {
		// Comando /DESCANSAR
		if (!checkAlive("¡¡Estas muerto!! Solo podes usar items cuando estas vivo.")) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		if (mapa.hayObjeto(m_pos.x, m_pos.y) && mapa.getObjeto(m_pos.x, m_pos.y).obj_ind == FOGATA) {
			// enviar(MSG_DOK);
			if (!m_flags.Descansar) {
				enviarMensaje("Te acomodas junto a la fogata y comienzas a descansar.", FontType.INFO);
			} else {
				enviarMensaje("Te levantas.", FontType.INFO);
			}
			m_flags.Descansar = !m_flags.Descansar;
		} else {
			if (m_flags.Descansar) {
				enviarMensaje("Te levantas.", FontType.INFO);
				m_flags.Descansar = false;
				// enviar(MSG_DOK);
				return;
			}
			enviarMensaje("No hay ninguna fogata junto a la cual descansar.", FontType.INFO);
		}
	}

	public void doEnviarEstads() {
		// Comando /EST
		sendUserStatsTxt(this);
	}

	// or long?...
	public void doEnviarFama() {
		enviar(ServerPacketID.userFame, (int) m_reputacion.asesinoRep, (int) m_reputacion.bandidoRep,
				(int) m_reputacion.burguesRep, (int) m_reputacion.ladronRep, (int) m_reputacion.nobleRep,
				(int) m_reputacion.plebeRep);
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
		double modNave = m_clase.modNavegacion();
		ObjectInfo barco = m_inv.getBarco();
		if (m_estads.userSkills[Skill.SKILL_Navegacion] / modNave < barco.MinSkill) {
			enviarMensaje("No tenes suficientes conocimientos para usar este barco.", FontType.INFO);
			enviarMensaje("Necesitas " + (int) (barco.MinSkill * modNave) + " puntos en navegacion.", FontType.INFO);
			return;
		}
		if (!m_flags.Navegando) {
			m_infoChar.m_cabeza = 0;
			if (isAlive()) {
				m_infoChar.m_cuerpo = barco.Ropaje;
			} else {
				m_infoChar.m_cuerpo = iFragataFantasmal;
			}
			m_infoChar.m_escudo = NingunEscudo;
			m_infoChar.m_arma = NingunArma;
			m_infoChar.m_casco = NingunCasco;
			m_flags.Navegando = true;
		} else {
			m_flags.Navegando = false;
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
				m_infoChar.m_cuerpo = iCuerpoMuerto;
				m_infoChar.m_cabeza = iCabezaMuerto;
				m_infoChar.m_escudo = NingunEscudo;
				m_infoChar.m_arma = NingunArma;
				m_infoChar.m_casco = NingunCasco;
			}
		}
		enviarCP();
		enviar(ServerPacketID.navigateToggle);
	}

	public void tratarDeHacerFogata() {
		MapPos pos = MapPos.mxy(m_flags.TargetObjMap, m_flags.TargetObjX, m_flags.TargetObjY);
		Map mapa = server.getMapa(pos.map);
		if (!mapa.isLegalPos(pos, false)) {
			return;
		}
		if (mapa.getObjeto(pos.x, pos.y).obj_cant < 3) {
			enviarMensaje("Necesitas al menos tres troncos para hacer una fogata.", FontType.INFO);
			return;
		}
		final short indiceSuerte[] = { 0, 3, 3, 3, 3, 3, 2, 2, 2, 2, 1 };
		// FIXME: REVISAR SI DEBE SER DIVIDIDO X 10 o NO.
		short suerte = indiceSuerte[m_estads.userSkills[Skill.SKILL_Supervivencia] / 10];
		if (Util.Azar(1, suerte) == 1) {
			short objid = FOGATA_APAG;
			int cant = mapa.getObjeto(pos.x, pos.y).obj_cant / 3;
			if (cant > 1) {
				enviarMensaje("Has hecho " + cant + " fogatas.", FontType.INFO);
			} else {
				enviarMensaje("Has hecho una fogata.", FontType.INFO);
			}
			mapa.quitarObjeto(pos.x, pos.y);
			mapa.agregarObjeto(objid, cant, pos.x, pos.y);
			server.getTrashCollector().add(pos);
		} else {
			if (m_flags.UltimoMensaje != 10) {
				enviarMensaje("No has podido hacer la fogata.", FontType.INFO);
				m_flags.UltimoMensaje = 10;
			}
		}
		subirSkill(Skill.SKILL_Supervivencia);
	}

	public void usarItem(short slot) {
		if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid == 0) {
			return;
		}
		m_inv.useInvItem(slot);
	}

	private void doLingotes() {
		if (m_inv.getObjeto(m_flags.TargetObjInvSlot).cant < 5) {
			enviarMensaje("No tienes suficientes minerales para hacer un lingote.", FontType.INFO);
			return;
		}
		ObjectInfo info = server.getInfoObjeto(m_flags.TargetObjInvIndex);
		if (info.ObjType != OBJTYPE_MINERALES) {
			enviarMensaje("Debes utilizar minerales para hacer un lingote.", FontType.INFO);
			return;
		}
		m_inv.quitarUserInvItem(m_flags.TargetObjInvSlot, 5);
		enviarObjetoInventario(m_flags.TargetObjInvSlot);
		if (Util.Azar(1, info.MinSkill) <= 10) {
			enviarMensaje("Has obtenido un lingote!!!", FontType.INFO);
			Map mapa = server.getMapa(m_pos.map);
			if (m_inv.agregarItem(info.LingoteIndex, 1) < 1) {
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(info.LingoteIndex, 1));
			}
			enviarMensaje("¡Has obtenido un lingote!", FontType.INFO);
		} else {
			if (m_flags.UltimoMensaje != 7) {
				enviarMensaje("Los minerales no eran de buena calidad, no has logrado hacer un lingote.",
						FontType.INFO);
				m_flags.UltimoMensaje = 7;
			}
		}
		m_flags.Trabajando = true;
	}

	private void fundirMineral() {
		if (m_flags.TargetObjInvIndex > 0) {
			ObjectInfo info = server.getInfoObjeto(m_flags.TargetObjInvIndex);
			if (info.ObjType == OBJTYPE_MINERALES
					&& info.MinSkill <= m_estads.userSkills[Skill.SKILL_Mineria] / m_clase.modFundicion()) {
				doLingotes();
			} else {
				enviarMensaje("No tenes conocimientos de mineria suficientes para trabajar este mineral.",
						FontType.INFO);
			}
		}
	}

	private double calcularPoderDomador() {
		return m_estads.userAtributos[ATRIB_CARISMA] * (m_estads.userSkills[Skill.SKILL_Domar] / m_clase.modDomar())
				+ Util.Azar(1, m_estads.userAtributos[ATRIB_CARISMA] / 3)
				+ Util.Azar(1, m_estads.userAtributos[ATRIB_CARISMA] / 3)
				+ Util.Azar(1, m_estads.userAtributos[ATRIB_CARISMA] / 3);
	}

	private void doDomar(Npc npc) {
		if (m_cantMascotas < MAXMASCOTAS) {
			if (npc.getPetUserOwner() == this) {
				enviarMensaje("La criatura ya te ha aceptado como su amo.", FontType.INFO);
				return;
			}
			if (npc.getPetUserOwner() != null) {
				enviarMensaje("La criatura ya tiene amo.", FontType.INFO);
				return;
			}
			double suerteDoma = calcularPoderDomador();
			if (npc.domable() <= suerteDoma) {
				m_cantMascotas++;
				int slot = freeMascotaSlot();
				m_mascotas[slot - 1] = npc;
				npc.setPetUserOwner(this);
				npc.followAmo();
				enviarMensaje("La criatura te ha aceptado como su amo.", FontType.INFO);
				subirSkill(Skill.SKILL_Domar);
				// hacemos respawn del npc para reponerlo.
				Npc.spawnNpc(npc.getNPCtype(), MapPos.mxy(npc.getPos().map, (short) 0, (short) 0), false, true);
			} else {
				if (m_flags.UltimoMensaje != 5) {
					enviarMensaje("No has logrado domar la criatura.", FontType.INFO);
					m_flags.UltimoMensaje = 5;
				}
			}
		} else {
			enviarMensaje("No podes controlar mas criaturas.", FontType.INFO);
		}
	}

	private boolean suerteMineria() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Mineria] / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doMineria() {
		m_estads.quitarStamina(m_clase.getEsfuerzoExcavar());
		if (suerteMineria()) {
			if (m_flags.TargetObj == 0) {
				return;
			}
			short objid = server.getInfoObjeto(m_flags.TargetObj).MineralIndex;
			int cant = m_clase.getCantMinerales();
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				Map mapa = server.getMapa(m_pos.map);
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has extraido algunos minerales!", FontType.INFO);
		} else {
			if (m_flags.UltimoMensaje != 9) {
				enviarMensaje("¡No has conseguido nada!", FontType.INFO);
				m_flags.UltimoMensaje = 9;
			}
		}
		subirSkill(Skill.SKILL_Mineria);
		m_flags.Trabajando = true;
	}

	private boolean suerteTalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 13, 7, 7 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Talar] / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doTalar() {
		m_estads.quitarStamina(m_clase.getEsfuerzoTalar());
		if (suerteTalar()) {
			int cant = m_clase.getCantLeños();
			short objid = Leña;
			Map mapa = server.getMapa(m_pos.map);
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				// Tiro al piso los items no agregados
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has conseguido algo de leña!", FontType.INFO);
		} else {
			if (m_flags.UltimoMensaje != 8) {
				enviarMensaje("No has conseguido leña. Intenta otra vez.", FontType.INFO);
				m_flags.UltimoMensaje = 8;
			}
		}
		subirSkill(Skill.SKILL_Talar);
		m_flags.Trabajando = true;
	}

	private void robarObjeto(Client victima) {
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
			Map mapa = server.getMapa(m_pos.map);
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, cant - agregados));
			}
			enviarObjetoInventario(slot);
			ObjectInfo info = server.getInfoObjeto(objid);
			enviarMensaje("Has robado " + cant + " " + info.Nombre, FontType.INFO);
		} else {
			enviarMensaje("No has logrado robar un objetos.", FontType.INFO);
		}
	}

	private boolean suerteRobar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Robar] / 10)]);
		return (Util.Azar(1, rango) < 3);
	}

	private void doRobar(Client victima) {
		Map mapa = server.getMapa(m_pos.map);
		if (!mapa.esZonaSegura() || triggerZonaPelea(victima) != MapCell.TRIGGER6_AUSENTE) {
			return;
		}
		if (m_flags.Privilegios < 2) {
			if (suerteRobar()) {
				// Exito robo
				if ((Util.Azar(1, 50) < 25) && (m_clase == ThiefClass.getInstance())) {
					if (victima.m_inv.tieneObjetosRobables()) {
						robarObjeto(victima);
					} else {
						enviarMensaje(victima.m_nick + " no tiene objetos.", FontType.INFO);
					}
				} else { // Roba oro
					if (victima.m_estads.getGold() > 0) {
						int cantidadRobada = Util.Azar(1, 100);
						victima.m_estads.addGold( -cantidadRobada );
						m_estads.addGold( cantidadRobada );
						enviarMensaje("Le has robado " + cantidadRobada + " monedas de oro a " + victima.m_nick, FontType.INFO);
					} else {
						enviarMensaje(m_nick + " no tiene oro.", FontType.INFO);
					}
				}
			} else {
				enviarMensaje("¡No has logrado robar nada!", FontType.INFO);
				victima.enviarMensaje("¡" + m_nick + " ha intentado robarte!", FontType.INFO);
				victima.enviarMensaje("¡" + m_nick + " es un criminal!", FontType.INFO);
			}
			if (!esCriminal()) {
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
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Pesca] / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarCaña() {
		m_estads.quitarStamina(m_clase.getEsfuerzoPescar());
		if (suertePescarCaña()) {
			Map mapa = server.getMapa(m_pos.map);
			if (m_inv.agregarItem(OBJ_PESCADO, 1) < 1) {
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(OBJ_PESCADO, 1));
			}
			enviarMensaje("¡Has pescado un lindo pez!", FontType.INFO);
		} else {
			if (m_flags.UltimoMensaje != 6) {
				enviarMensaje("¡No has pescado nada!", FontType.INFO);
				m_flags.UltimoMensaje = 6;
			}
		}
		subirSkill(Skill.SKILL_Pesca);
		m_flags.Trabajando = true;
	}

	private boolean suertePescarRed() {
		final short[] suerte = { 60, 54, 49, 43, 38, 32, 27, 21, 16, 11, 11 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Pesca] / 10)]);
		return (Util.Azar(1, rango) < 6);
	}

	private void doPescarRed() {
		m_estads.quitarStamina(m_clase.getEsfuerzoPescar());
		int skills = m_estads.userSkills[Skill.SKILL_Pesca];
		if (skills == 0) {
			return;
		}
		if (suertePescarRed()) {
			int cant = (m_clase == FishermanClass.getInstance()) ? Util.Azar(1, 5) : 1;
			short objid = (short) PESCADOS_RED[Util.Azar(1, PESCADOS_RED.length) - 1];
			int agregados = m_inv.agregarItem(objid, cant);
			if (agregados < cant) {
				Map mapa = server.getMapa(m_pos.map);
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, cant - agregados));
			}
			enviarMensaje("¡Has pescado algunos peces!", FontType.INFO);
		} else {
			enviarMensaje("¡No has pescado nada!", FontType.INFO);
		}
		subirSkill(Skill.SKILL_Pesca);
	}

	public void doWLC(short x, short y, short tLong) {
		// Comando WLC
		// Clic izquierdo en modo trabajo
		// StringTokenizer st = new StringTokenizer(s, ",");
		// short x = Short.parseShort(st.nextToken());
		// short y = Short.parseShort(st.nextToken());
		// short tLong = Short.parseShort(st.nextToken());
		// Log.serverLogger().fine("x=" + x);
		// Log.serverLogger().fine("y=" + y);
		// Log.serverLogger().fine("tLong=" + tLong);

		Pos pos = new Pos(x, y);
		if (!isAlive() || m_flags.Descansar || m_flags.Meditando || !pos.isValid()) {
			return;
		}
		if (!m_pos.inRangoVision(pos)) {
			enviarPU();
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		Client tu = null;
		MapObject obj = null;

		switch (tLong) {
		case Skill.SKILL_Proyectiles:
			// Nos aseguramos que este usando un arma de proyectiles
			if (!m_inv.tieneArmaEquipada()) {
				return;
			}
			if (!m_inv.getArma().esProyectil()) {
				return;
			}
			if (!m_inv.tieneMunicionEquipada()) {
				enviarMensaje("No tienes municiones.", FontType.INFO);
				return;
			}
			// Quitamos stamina
			if (m_estads.stamina >= 10) {
				m_estads.quitarStamina(Util.Azar(1, 10));
			} else {
				enviarMensaje("Estas muy cansado para luchar.", FontType.INFO);
				return;
			}

			mapa.consultar(this, x, y);
			tu = server.getCliente(m_flags.TargetUser);
			Npc npc = server.getNPC(m_flags.TargetNpc);
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
				if (m_flags.Seguro) {
					if (!tu.esCriminal()) {
						enviarMensaje(
								"No puedes atacar a ciudadanos, para hacerlo, antes debes desactivar el seguro con la tecla S",
								FontType.FIGHT);
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
			if (!intervaloPermiteLanzarSpell()) {
				return;
			}
			if (esConsejero())
				return;
			mapa.consultar(this, x, y);
			if (m_flags.Hechizo > 0) {
				m_spells.lanzarHechizo(server.getHechizo(m_flags.Hechizo));
				m_flags.Hechizo = 0;
			} else {
				enviarMensaje("¡Primero selecciona el hechizo que deseas lanzar!", FontType.INFO);
			}
			break;
		case Skill.SKILL_Pesca:
			if (!intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneHerramientaEquipada()) {
				enviarMensaje("Deberías equiparte la caña o la red.", FontType.INFO);
				return;
			}
			if (m_inv.getHerramienta().ObjIndex != OBJTYPE_CAÑA
					&& m_inv.getHerramienta().ObjIndex != OBJTYPE_RED_PESCA) {
				enviarMensaje("Deberías equiparte la caña o la red.", FontType.INFO);
				return;
			}
			if (mapa.getTrigger(m_pos.x, m_pos.y) == 1) {
				enviarMensaje("No puedes pescar desde donde te encuentras.", FontType.INFO);
				return;
			}
			if (mapa.hayAgua(x, y)) {
				enviarSonido(SOUND_PESCAR);
				switch (m_inv.getHerramienta().ObjIndex) {
				case OBJTYPE_CAÑA:
					doPescarCaña();
					break;
				case OBJTYPE_RED_PESCA:
					if (m_pos.distance(MapPos.mxy(m_pos.map, x, y)) > 2) {
						enviarMensaje("Estás demasiado lejos para pescar.", FontType.INFO);
						return;
					}
					doPescarRed();
					break;
				}
			} else {
				enviarMensaje("No hay agua donde pescar. Busca un lago, rio o mar.", FontType.INFO);
			}
			break;
		case Skill.SKILL_Robar:
			if (!mapa.esZonaSegura()) {
				if (!intervaloPermiteTrabajar()) {
					return;
				}
				mapa.consultar(this, x, y);
				if (m_flags.TargetUser > 0 && m_flags.TargetUser != getId()) {
					tu = server.getCliente(m_flags.TargetUser);
					if (tu.isAlive()) {
						MapPos wpaux = MapPos.mxy(m_pos.map, x, y);
						if (wpaux.distance(m_pos) > 2) {
							enviarMensaje("Estas demasiado lejos.", FontType.INFO);
							return;
						}
						// Nos aseguramos que el trigger le permite robar
						if (mapa.getTrigger(tu.getPos().x, tu.getPos().y) == 4) {
							enviarMensaje("No podes robar aquí.", FontType.WARNING);
							return;
						}
						doRobar(tu);
					}
				} else {
					enviarMensaje("No hay a quien robarle!.", FontType.INFO);
				}
			} else {
				enviarMensaje("¡No podes robarle en zonas seguras!.", FontType.INFO);
			}
			break;
		case Skill.SKILL_Talar:
			if (!intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneHerramientaEquipada()) {
				enviarMensaje("Deberías equiparte el hacha de leñador.", FontType.INFO);
				return;
			}
			if (m_inv.getHerramienta().ObjIndex != HACHA_LEÑADOR) {
				enviarMensaje("Deberías equiparte el hacha de leñador.", FontType.INFO);
				return;
			}
			obj = mapa.getObjeto(x, y);
			if (obj != null) {
				MapPos wpaux = MapPos.mxy(m_pos.map, x, y);
				if (wpaux.distance(m_pos) > 2) {
					enviarMensaje("Estas demasiado lejos.", FontType.INFO);
					return;
				}
				// ¿Hay un arbol donde cliqueo?
				if (obj.getInfo().ObjType == OBJTYPE_ARBOLES) {
					enviarSonido(SOUND_TALAR);
					doTalar();
				}
			} else {
				enviarMensaje("No hay ningun arbol ahi.", FontType.INFO);
			}
			break;
		case Skill.SKILL_Mineria:
			if (!intervaloPermiteTrabajar()) {
				return;
			}
			if (!m_inv.tieneHerramientaEquipada()) {
				return;
			}
			if (m_inv.getHerramienta().ObjIndex != PIQUETE_MINERO) {
				doSALIR();
				return;
			}
			mapa.consultar(this, x, y);
			obj = mapa.getObjeto(x, y);
			if (obj != null) {
				MapPos wpaux = MapPos.mxy(m_pos.map, x, y);
				if (wpaux.distance(m_pos) > 2) {
					enviarMensaje("Estas demasiado lejos.", FontType.INFO);
					return;
				}
				// ¿Hay un yacimiento donde cliqueo?
				if (obj.getInfo().ObjType == OBJTYPE_YACIMIENTO) {
					enviarSonido(SOUND_MINERO);
					doMineria();
				} else {
					enviarMensaje("Ahi no hay ningun yacimiento.", FontType.INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ningun yacimiento.", FontType.INFO);
			}
			break;
		case Skill.SKILL_Domar:
			// Modificado 25/11/02
			// Optimizado y solucionado el bug de la doma de
			// criaturas hostiles.
			mapa.consultar(this, x, y);
			if (m_flags.TargetNpc > 0) {
				npc = server.getNPC(m_flags.TargetNpc);
				if (npc.domable() > 0) {
					MapPos wpaux = MapPos.mxy(m_pos.map, x, y);
					if (wpaux.distance(m_pos) > 2) {
						enviarMensaje("Estas demasiado lejos.", FontType.INFO);
						return;
					}
					if (npc.atacadoPorUsuario()) {
						enviarMensaje("No puedes domar una criatura que está luchando con un jugador.", FontType.INFO);
						return;
					}
					doDomar(npc);
				} else {
					enviarMensaje("No puedes domar a esa criatura.", FontType.INFO);
				}
			} else {
				enviarMensaje("No hay ninguna criatura alli!.", FontType.INFO);
			}
			break;
		case Skill.SKILL_FundirMetal:
			mapa.consultar(this, x, y);
			if (m_flags.TargetObj > 0) {
				if (server.getInfoObjeto(m_flags.TargetObj).ObjType == OBJTYPE_FRAGUA) {
					fundirMineral();
				} else {
					enviarMensaje("Ahi no hay ninguna fragua.", FontType.INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ninguna fragua.", FontType.INFO);
			}
			break;
		case Skill.SKILL_Herreria:
			mapa.consultar(this, x, y);
			if (m_flags.TargetObj > 0) {
				if (server.getInfoObjeto(m_flags.TargetObj).ObjType == OBJTYPE_YUNQUE) {
					m_inv.enviarArmasConstruibles();
					m_inv.enviarArmadurasConstruibles();
					// enviar(MSG_SFH);
				} else {
					enviarMensaje("Ahi no hay ningun yunque.", FontType.INFO);
				}
			} else {
				enviarMensaje("Ahi no hay ningun yunque.", FontType.INFO);
			}
			break;
		}
	}

	public void doUK(short val) {
		// Comando UK
		if (!checkAlive()) {
			return;
		}

		switch (val) {
		case Skill.SKILL_Robar:
			enviar(ServerPacketID.MSG_TO1, Skill.SKILL_Robar);
			break;
		case Skill.SKILL_Magia:
			enviar(ServerPacketID.MSG_TO1, Skill.SKILL_Magia);
			break;
		case Skill.SKILL_Domar:
			enviar(ServerPacketID.MSG_TO1, Skill.SKILL_Domar);
			break;
		case Skill.SKILL_Ocultarse:
			if (m_flags.Navegando) {
				if (m_flags.UltimoMensaje != 3) {
					enviarMensaje("No puedes ocultarte si estas navegando.", FontType.INFO);
					m_flags.UltimoMensaje = 3;
				}
				return;
			}
			if (m_flags.Oculto) {
				if (m_flags.UltimoMensaje != 2) {
					enviarMensaje("Ya estabas oculto.", FontType.INFO);
					m_flags.UltimoMensaje = 2;
				}
				return;
			}
			doOcultarse();
			break;
		}
	}

	public void doLanzarHechizo(short slot) {
		// Comando LH
		// Fixed by agush
		if (checkAlive()) {
			m_spells.castSpell(slot);
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

	public void doMeditar() {
		// Comando /MEDITAR
		if (!checkAlive("¡¡Estas muerto!! Solo los vivos pueden meditar.")) {
			return;
		}
		enviar(ServerPacketID.medOk);
		if (!m_flags.Meditando) {
			enviarMensaje("Comienzas a meditar.", FontType.INFO);
		} else {
			enviarMensaje("Dejas de meditar.", FontType.INFO);
		}
		m_flags.Meditando = !m_flags.Meditando;
		if (m_flags.Meditando) {
			m_counters.tInicioMeditar = server.getMillis();
			int segs = (TIEMPO_INICIO_MEDITAR / 1000);
			enviarMensaje("Te estás concentrando. En " + segs + " segundos comenzarás a meditar.", FontType.INFO);
			m_infoChar.m_loops = LoopAdEternum;
			if (m_estads.ELV < 15) {
				enviarCFX(FXMEDITARCHICO, LoopAdEternum);
				m_infoChar.m_fx = FXMEDITARCHICO;
			} else if (m_estads.ELV < 30) {
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
		if (!npc.esSacerdote()) {
			enviarMensaje("No poseo el poder de revivir a otros, mejor encuentra un sacerdote.", FontType.INFO);
			return;
		}
		if (!checkAlive("¡JA! Debes estar muerto para resucitarte.")) {
			return;
		}
		if (!existePersonaje()) {
			enviarMensaje("!!El personaje no existe, cree uno nuevo.", FontType.INFO);
			doSALIR();
			return;
		}
		revivirUsuario();
		enviarMensaje("¡¡Has sido resucitado!!", FontType.INFO);
	}

	public void doCurar() {
		// Comando /CURAR
		// Se asegura que el target es un npc
		Npc npc = getNearNpcSelected(DISTANCE_PRIEST);
		if (npc == null) {
			return;
		}		
		if (!npc.esSacerdote()) {
			enviarMensaje("No poseo el poder para curar a otros, mejor encuentra un sacerdote.", FontType.INFO);
			return;
		}
		if (!checkAlive("¡Solo puedo curar a los vivos! ¡Resucítate primero!")) {
			return;
		}
		m_estads.MinHP = m_estads.MaxHP;
		// enviarEstadsUsuario();
		refreshStatus(2);
		enviarMensaje("¡¡Has sido curado!!", FontType.INFO);
	}

	public void cambiarDescripcion(String s) {
		// Comando "/DESC "
		s = s.trim();
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		if (!Util.asciiValidos(s)) {
			enviarMensaje("La descripcion tiene caracteres invalidos.", FontType.INFO);
			return;
		}
		m_desc = s;
		enviarMensaje("La descripcion a cambiado.", FontType.INFO);
	}

	/**
	 * Change user look direction
	 * 
	 * @param dir is new direction
	 */
	public void changeDir(short dir) {

		Map mapa = server.getMapa(m_pos.map);
		short crimi = 0;
		if (esCriminal())
			crimi = 1;

		mapa.enviarAlArea(getPos().x, getPos().y, ServerPacketID.MSG_CP, getId(), m_infoChar.getCuerpo(),
				m_infoChar.getCabeza(), m_infoChar.getDir(), m_infoChar.getArma(), m_infoChar.getEscudo(),
				m_infoChar.getCasco(), m_infoChar.m_fx, m_infoChar.m_loops);

		// mapa.enviarAlArea(getPos().x, getPos().y, serverPacketID.CC, getId(),
		// m_infoChar.getCuerpo(), m_infoChar.getCabeza(), m_infoChar.getDir(),
		// getPos().x, getPos().y, m_infoChar.getArma(), m_infoChar.getEscudo(),
		// m_infoChar.getCasco(), m_infoChar.getFX(), (short) 999, m_nick + getClan(),
		// crimi, m_flags.Privilegios);

		m_infoChar.m_dir = dir;

	}

	public void enviarCP() {
		// Enviar ChangePlayer a todos.
		// Sub ChangeUserChar.
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}

		short crimi = 0;
		if (esCriminal())
			crimi = 1;

		mapa.enviarAlArea(getPos().x, getPos().y, ServerPacketID.MSG_CP, getId(), m_infoChar.getCuerpo(),
				m_infoChar.getCabeza(), m_infoChar.getDir(), m_infoChar.getArma(), m_infoChar.getEscudo(),
				m_infoChar.getCasco(), m_infoChar.m_fx, m_infoChar.m_loops);

		// mapa.enviarATodos(serverPacketID.MSG_CP, getId(), m_infoChar.getCuerpo(),
		// m_infoChar.getCabeza(), m_infoChar.getDir(), m_infoChar.getArma(),
		// m_infoChar.getEscudo(), m_infoChar.getCasco(), m_infoChar.m_fx,
		// m_infoChar.m_loops);

	}

	public void enviarValCode() {
		// //// fixme
		// <<<<<<<<<<< MODULO PRIVADO DE CADA IMPLEMENTACION >>>>>>
		// UserList(UserIndex).flags.ValCoDe = CInt(RandomNumber(20000, 32000))
		// UserList(UserIndex).RandKey = CLng(RandomNumber(0, 99999))
		// UserList(UserIndex).PrevCRC = UserList(UserIndex).RandKey
		// UserList(UserIndex).PacketNumber = 100
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		// enviar(MSG_VAL, (short) 345, (short) 343);
	}

	public void doSALIR() {
		if (m_saliendo) {
			return;
		}
		m_saliendo = true;
		log.info(this + " haciendo doSALIR()");
		boolean wasLogged = m_flags.UserLogged;
		try {
			Map mapa = server.getMapa(m_pos.map);
			if (mapa != null && mapa.estaCliente(this)) {
				try {
					quitarMascotas();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					mapa.salir(this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (wasLogged) {
				try {
					if (server.estaLloviendo()) {
						// Detener la lluvia.
						enviar(ServerPacketID.userRain);
					}
					enviar(ServerPacketID.finOk);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			m_flags.UserLogged = false;
		} catch (Exception ex) {
			log.fatal("ERROR EN doSALIR(): ", ex);
		} finally {
			try {
				server.closeConnection(this);
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
			if (wasLogged) {
				try {
					saveUser();
				} catch (Exception ex) {
					log.fatal("ERROR EN doSALIR() - saveUser(): ", ex);
				}
			}
		}
		log.info(m_nick + ": Hasta la vista, baby!");
	}

	public void tirarDados() {
		m_estads.userAtributos[0] = (byte) (Util.Azar(16, 18));
		m_estads.userAtributos[1] = (byte) (Util.Azar(16, 18));
		m_estads.userAtributos[2] = (byte) (Util.Azar(16, 18));
		m_estads.userAtributos[3] = (byte) (Util.Azar(16, 18));
		m_estads.userAtributos[4] = (byte) (Util.Azar(16, 18));

		enviar(ServerPacketID.dropDices, m_estads.userAtributos[0], m_estads.userAtributos[1],
				m_estads.userAtributos[2], m_estads.userAtributos[3], m_estads.userAtributos[4]);
	}

	public void borrarPersonaje(String s) {
		enviarError("Comando no implementado: " + s);
		/*
		 * FIXME: para implementar algun dia... Case "BORR" ' <<< borra
		 */
	}

	/**
	 * Procesa el clic izquierdo del mouse sobre el mapa
	 * 
	 * @param x es la posición x del clic
	 * @param y es la posición y del clic
	 */
	public void clicIzquierdoMapa(short x, short y) {
		// Clic con el botón primario del mouse
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.consultar(this, x, y);
		}
	}

	/**
	 * Procesa el clic derecho del mouse sobre el mapa
	 * 
	 * @param x es la posición x del clic
	 * @param y es la posición y del clic
	 */
	public void clicDerechoMapa(short x, short y) {
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}

		// ¿Posicion valida?
		if (Pos.isValid(x, y)) {
			// ¿Hay un objeto en el tile?
			if (mapa.hayObjeto(x, y)) {
				MapObject obj = mapa.getObjeto(x, y);
				m_flags.TargetObj = obj.getInfo().ObjIndex;
				switch (obj.getInfo().ObjType) {
				case OBJTYPE_PUERTAS:
					mapa.accionParaPuerta(x, y, this);
					break;
				case OBJTYPE_CARTELES:
					mapa.accionParaCartel(x, y, this);
					break;
				case OBJTYPE_FOROS:
					mapa.accionParaForo(x, y, this);
					break;
				case OBJTYPE_LEÑA:
					if (m_flags.TargetObj == FOGATA_APAG) {
						mapa.accionParaRamita(x, y, this);
					}
					break;
				}
			} else {
				// ¿Hay un objeto que ocupa más de un tile?
				MapObject obj = mapa.buscarObjeto(x, y);
				Npc npc;
				if (obj != null) {
					m_flags.TargetObj = obj.getInfo().ObjIndex;
					// enviar(MSG_SELE, obj.getInfo().ObjType,
					// obj.getInfo().Nombre, "OBJ");
					if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
						mapa.accionParaPuerta(obj.x, obj.y, this);
					}
				} else if ((npc = mapa.getNPC(x, y)) != null) {
					if (npc.comercia()) {
						// Doble clic sobre un comerciante, hace /COMERCIAR
						if (!checkAlive()) {
							return;
						}
						if (checkNpcNear(npc, DISTANCE_MERCHANT)) {
							// Iniciamos el comercio con el Npc
							iniciarComercioNPC(npc);
						}
					} else if (npc.isBankCashier()) {
						if (!checkAlive()) {
							return;
						}
						// Extensión de AOJ - 16/08/2004
						// Doble clic sobre el banquero hace /BOVEDA
						if (checkNpcNear(npc, DISTANCE_CASHIER)) {
							iniciarDeposito();
						}
					} else if (npc.esSacerdote()) {
						// Extensión de AOJ - 01/02/2007
						// Doble clic sobre el sacerdote hace /RESUCITAR o
						// /CURAR
						if (isAlive()) {
							doCurar();
						} else {
							doResucitar();
						}
					}
				} else {
					m_flags.TargetNpc = 0;
					m_flags.TargetNpcTipo = 0;
					m_flags.TargetUser = 0;
					m_flags.TargetObj = 0;
					enviarMensaje("No ves nada interesante.", FontType.INFO);
				}
			}
		}
	}

	/** Comando para hablar (;) */
	public void doHablar(String s) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.enviarAlArea(m_pos.x, m_pos.y, ServerPacketID.dialog, COLOR_BLANCO, s, getId());
		}
		if (esConsejero()) {
			Log.logGM(m_nick, "El consejero dijo: " + s);
		}
	}

	/** Comando para gritar (-) */
	public void doGritar(String s) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			// mapa.enviarAlArea(m_pos.x, m_pos.y, MSG_TALK,
			// COLOR_ROJO, s, getId());
		}
		if (esConsejero()) {
			Log.logGM(m_nick, "El consejero gritó: " + s);
		}
	}

	/** Comando para susurrarle al oido a un usuario (\) */
	public void doSusurrar(String s) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden comunicarse con el mundo de los vivos. ")) {
			return;
		}
		if (s.length() > MAX_MENSAJE) {
			s = s.substring(0, MAX_MENSAJE);
		}
		int sep;
		if ((sep = s.indexOf(' ')) > -1 && (s.length() > sep + 1)) {
			String nombre = s.substring(0, sep);
			s = s.substring(sep + 1);
			Map mapa = server.getMapa(m_pos.map);
			if (mapa == null) {
				return;
			}
			Client usuario = server.getUsuario(nombre);
			if (usuario != null) {
				if (mapa.buscarEnElArea(m_pos.x, m_pos.y, usuario.getId()) == null) {
					enviarMensaje("Estas muy lejos de " + nombre, FontType.INFO);
				} else {
					if (esConsejero()) {
						Log.logGM(m_nick, "El consejero le susurró a " + nombre + ": " + s);
					}
					// usuario.enviarHabla(COLOR_AZUL, s, getId());
					// enviarHabla(COLOR_AZUL, s, getId());
					// if (!esGM() || esConsejero()) {
					// mapa.enviarAlAreaAdminsNoConsejeros(m_pos.x,
					// m_pos.y, MSG_TALK, COLOR_AMARILLO, "a "
					// + usuario.getNick() + "> " + s, this
					// .getId());
					// }
				}
			} else {
				enviarMensaje("Usuario inexistente.", FontType.INFO);
			}
		}
	}

	public void mover(Direction dir) {
		try {
			speedHackMover.check();
		} catch (SpeedHackException e) {
			enviarMensaje(e.getMessage(), FontType.DEBUG);
			doSALIR();
			return;
		}

		if (m_counters.Saliendo) {
			enviarMensaje("/SALIR cancelado!.", FontType.INFO);
			m_counters.Saliendo = false;
		}

		if (m_flags.Paralizado) {
			if (m_flags.UltimoMensaje != 1) {
				enviarMensaje("No puedes moverte por estar paralizado.", FontType.INFO);
				m_flags.UltimoMensaje = 1;
			}
			return;
		}
		if (m_flags.Descansar) {
			m_flags.Descansar = false;
			// enviar(MSG_DOK);
			enviarMensaje("Has dejado de descansar.", FontType.INFO);
		}
		if (m_flags.Meditando) {
			m_flags.Meditando = false;
			enviar(ServerPacketID.medOk);
			enviarMensaje("Dejas de meditar.", FontType.INFO);
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
			enviarCFX(0, 0);
		}
		if (m_flags.Oculto) {
			if (m_clase != ThiefClass.getInstance()) {
				volverseVisible();
			}
		}
		moverUsuario(dir);
		m_flags.Trabajando = false;
	}

	private void volverseVisible() {
		enviarMensaje("¡Has vuelto a ser visible!", FontType.INFO);
		m_flags.Oculto = false;
		m_flags.Invisible = false;
		Map m = server.getMapa(m_pos.map);
		// m.enviarATodos(MSG_NOVER, m_id, 0);
	}

	private void moverUsuario(Direction dir) {
		MapPos new_pos = m_pos.copy();
		new_pos.moveToDir(dir);

		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		m_infoChar.setDir(dir);
		if (new_pos.isValid() && mapa.isFree(new_pos.x, new_pos.y) && !mapa.estaBloqueado(new_pos.x, new_pos.y)) {
			mapa.mover(this, new_pos.x, new_pos.y);
			if (mapa.hayTeleport(new_pos.x, new_pos.y)) {
				MapPos pos = mapa.getTeleport(new_pos.x, new_pos.y);
				boolean conFX = (mapa.hayObjeto(new_pos.x, new_pos.y)
						&& mapa.getObjeto(new_pos.x, new_pos.y).getInfo().ObjType == OBJTYPE_TELEPORT);
				boolean enviarData = m_pos.map != pos.map;
				cambiarMapa(pos.map, pos.x, pos.y, conFX, enviarData);
			}
		} else {
			enviarPU();
		}
	}

	public void doAtacar() {
		if (!checkAlive("¡¡No puedes atacar a nadie por estar muerto!!")) {
			return;
		}
		if (esConsejero()) {
			enviarMensaje("¡¡No puedes atacar a nadie!!", FontType.INFO);
			return;
		} else {
			if (m_inv.tieneArmaEquipada()) {
				if (m_inv.getArma().esProyectil()) {
					enviarMensaje("No podés usar asi esta arma.", FontType.INFO);
					return;
				}
			}
			usuarioAtaca();
			// piedra libre para todos los compas!
			if (estaOculto()) {
				volverseVisible();
			}
		}
	}

	public void enviarPU() {
		enviar(ServerPacketID.MSG_PU, m_pos.x, m_pos.y);
	}

	public void agarrarObjeto() {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden recoger objetos.")) {
			return;
		}
		if (esConsejero()) {
			enviarMensaje("No puedes recoger ningún objeto.", FontType.INFO);
			return;
		}
		getObj();
	}

	public void tirarObjeto(short slot, int cant) {
		if (!checkAlive("¡¡Estas muerto!! Los muertos no pueden tirar objetos.")) {
			return;
		}
		if (estaNavegando()) {
			enviarMensaje("No puedes tirar objetos mientras navegas.", FontType.INFO);
			return;
		}
		if (esConsejero()) {
			enviarMensaje("No puedes tirar ningún objeto.", FontType.INFO);
			return;
		}
		if (slot == FLAGORO) {
			tirarOro(cant);
			// enviarEstadsUsuario();
			refreshStatus(1);
		} else {
			if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
				if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid > 0) {
					m_inv.dropObj(slot, cant);
				}
			}
		}
	}

	public void getObj() {
		Map mapa = server.getMapa(m_pos.map);
		// ¿Hay algun obj?
		if (mapa.hayObjeto(m_pos.x, m_pos.y)) {
			// ¿Esta permitido agarrar este obj?
			MapObject obj = mapa.getObjeto(m_pos.x, m_pos.y);
			if (!obj.getInfo().esAgarrable()) {
				enviarMensaje("El objeto no se puede agarrar.", FontType.INFO);
			} else {
				int agregados = m_inv.agregarItem(obj.obj_ind, obj.obj_cant);
				if (agregados < obj.obj_cant) {
					mapa.quitarObjeto(m_pos.x, m_pos.y);
					mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(obj.obj_ind, obj.obj_cant - agregados));
				} else {
					// Quitamos el objeto
					mapa.quitarObjeto(m_pos.x, m_pos.y);
					if (esGM()) {
						Log.logGM(m_nick, "Agarró: " + obj.obj_ind + " objeto=" + obj.getInfo().Nombre);
					}
				}
			}
		} else {
			enviarMensaje("No hay nada aqui.", FontType.INFO);
		}
	}

	public void equiparObjeto(short slot) {
		// Comando EQUI
		if (!checkAlive("¡¡Estas muerto!! Solo puedes usar items cuando estas vivo.")) {
			return;
		}
		// StringTokenizer st = new StringTokenizer(s, ",");
		// short slot = Short.parseShort(st.nextToken());
		if (slot > 0 && slot <= MAX_INVENTORY_SLOTS) {
			if (m_inv.getObjeto(slot) != null && m_inv.getObjeto(slot).objid > 0) {
				m_inv.equipar(slot); // EquiparInvItem
			}
		}
	}

	public void enviarInventario() {
		// updateUserInv
		for (int i = 1; i <= m_inv.getSize(); i++) {
			enviarObjetoInventario(i);
		}
	}

	public void setNullObject(int slot) {
		enviar(ServerPacketID.deleteObject, (byte) slot);
	}

	public void enviarObjetoInventario(int slot) {
		InventoryObject objInv = m_inv.getObjeto(slot);

		if (objInv != null && objInv.objid != 0) {
			ObjectInfo io = server.getInfoObjeto(objInv.objid);

			enviar(ServerPacketID.MSG_CSI, (byte) slot, objInv.objid, io.Nombre, (short) objInv.cant,
					(byte) (objInv.equipado ? 1 : 0), io.GrhIndex, (byte) io.ObjType, io.MaxHIT, io.MinHIT, io.MaxDef,
					(int) io.Valor);

		} else {
			enviar(ServerPacketID.MSG_CSI, (byte) slot, (short) 0);
		}
	}

	private void enviarIndiceUsuario() {
		// enviar(MSG_UI, getId());
		enviar(ServerPacketID.IP, getId());
	}

	public void enviarMensaje(String msg, FontType fuente) {
		// enviar(MSG_TALK, msg, fuente.toString());
		enviar(ServerPacketID.talk, msg, fuente.toString());
	}

	public void enviarHabla(int color, String msg, short id) {
		// enviar(MSG_DIALOG, color, msg, id);
		enviar(ServerPacketID.dialog, color, msg, id);
	}

	public void refreshStatus(int value) {
		byte id = (byte) value;

		switch (value) {

		case 1: // GOLD
			enviar(ServerPacketID.MSG_REFRESH, id, m_estads.getGold());
			break;

		case 2: // user hp
			enviar(ServerPacketID.MSG_REFRESH, id, (short) m_estads.MinHP, (short) m_estads.MaxHP);
			break;

		case 3: // user mana
			enviar(ServerPacketID.MSG_REFRESH, id, (short) m_estads.mana, (short) m_estads.maxMana);
			break;

		case 4: // user stamina
			enviar(ServerPacketID.MSG_REFRESH, id, (short) m_estads.stamina, (short) m_estads.maxStamina);
			break;

		case 5: // user level, exp & elu
			enviar(ServerPacketID.MSG_REFRESH, id, m_estads.ELV, m_estads.Exp, m_estads.ELU);
			break;

		default:
			return;

		}

	}

	public void enviarEstadsHambreSed() {
		// Ejemplo: EHYS100,100,100,100
		// "EHYS" MaxAGU MinAGU MaxHam MinHam
		// enviar(MSG_EHYS, m_estads.MaxAGU, m_estads.MinAGU,
		// m_estads.MaxHam, m_estads.MinHam);
	}

	public void petDelete() {
		int i = 0;

		while (i < m_mascotas.length) {

			if (m_mascotas[i] != null) {

				Npc npc;

				npc = m_mascotas[i];
				m_mascotas[i].releasePet();
				m_mascotas[i] = null;
				m_cantMascotas--;

				npc.quitarNPC();

				// Evitamos crear una variable al dope
				if (i < 1)
					enviarMensaje("Pierdes el control de tus mascotas.", FontType.INFO);
			}
			i++;
		}
	}

	private void cambiarMapa(short mapa, short x, short y) {
		if (m_saliendo) {
			return;
		}
		cambiarMapa(mapa, x, y, true, true);
	}

	private void cambiarMapa(short mapa, short x, short y, boolean conFX, boolean enviarData) {

		short oldMap = m_pos.map;
		if (m_pos.map != 0) {
			Map m = server.getMapa(m_pos.map);
			if (m == null) {
				return;
			}
			if (m.estaCliente(this) && !m.salir(this)) {
				// No pudo salir del mapa :)
				log.fatal(this + "> No pudo salir del mapa actual");
			}
		}
		Map m = server.getMapa(mapa);
		if (m == null) {
			return;
		}

		// Agus:Mapa restringido?
		if (m.getForbbiden() && !esNewbie()) {
			warpUser(getPos().map, this.m_pos.x--, this.m_pos.y--, true);
			return;
		}

		MapPos pos_libre = m.closestLegalPosPj(x, y, m_flags.Navegando, esGM());
		if (m.entrar(this, pos_libre.x, pos_libre.y)) {
			m_pos = MapPos.mxy(mapa, pos_libre.x, pos_libre.y);

			Map old = server.getMapa(oldMap);

			// Agus: check if user pet exists
			if (oldMap != mapa)
				petDelete();

			if (enviarData) {
				enviar(ServerPacketID.CMP, mapa);
			}

			short crimi = 0;
			if (esCriminal())
				crimi = 1;

			// enviar(serverPacketID.CC, getId(), m_infoChar.getCuerpo(),
			// m_infoChar.getCabeza(), m_infoChar.getDir(), getPos().x, getPos().y,
			// m_infoChar.getArma(), m_infoChar.getEscudo(), m_infoChar.getCasco(),
			// m_infoChar.getFX(), (short) 999, m_nick + getClan(), crimi,
			// m_flags.Privilegios);

			m.areasData.loadUser(this);
			enviarPU();

			// old.areasData.userDisconnect(this);
			// old.areasData.resetUser(this);
			// old.areasData.sendToArea(x, y, this.m_id, serverPacketID.MSG_BP, this.m_id);
			// old.areasData.sendToArea(m, x, y, serverPacketID.MSG_BP, this.m_id);

			// m.areasData.areasLogged(this);

			if (conFX) {
				enviarCFX(1, 0);
				if (m_flags.UserLogged) { // No hacer sonido en el LOGIN.
					enviarSonido(SND_WARP);
				}
			}
			// enviarIP();
			if (enviarData) {
				// Enviarme los m_clients del mapa, sin contarme a mi
				// m.enviarClientes(this);
				// Enviarme los objetos del mapa.
				// m.enviarObjetos(this);
				// Enviarme las posiciones bloqueadas del mapa.
				m.enviarBQs(this);
			}
		} else {
			enviarMensaje("No se pudo entrar en el mapa deseado", FontType.WARNING);
		}

	}

	public void enviarCFX(int fx, int val) {
		Map m = server.getMapa(m_pos.map);
		if (m == null) {
			return;
		}
		m.enviarCFX(m_pos.x, m_pos.y, getId(), fx, val);
	}

	public boolean estaParalizado() {
		return m_flags.Paralizado;
	}

	public void revivirUsuario() {
		if (m_counters.Saliendo) {
			return;
		}
		Map m = server.getMapa(m_pos.map);
		if (m == null) {
			return;
		}
		m_flags.Muerto = false;
		m_estads.MinHP = m_estads.MaxHP;// 10;
		if (m_estads.drinked <= 0) {
			m_estads.drinked = 10;
		}
		if (m_estads.eaten <= 0) {
			m_estads.eaten = 10;
		}
		m_infoChar.m_cabeza = m_origChar.m_cabeza;
		cuerpoDesnudo();
		enviarCP();
		refreshStatus(2);
		enviarEstadsHambreSed();
	}

	public boolean warpUser(short m, short x, short y, boolean conFX) {
		// WarpUserChar(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal
		// X As Integer, ByVal Y As Integer, Optional ByVal FX As Boolean =
		// False)
		// Quitar el dialogo
		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			// mapa.enviarATodos(MSG_QDL, m_id);
			// enviar(MSG_QTDL);
		}
		// Si el destino es distinto a la posición actual
		if (m_pos.map != m || m_pos.x != x || m_pos.y != y) {
			short oldMap = m_pos.map;
			Map newMap = server.getMapa(m);
			MapPos pos_libre = newMap.closestLegalPosPj(x, y, m_flags.Navegando, esGM());
			if (pos_libre == null) {
				log.warn("WARPUSER FALLO: no hay un lugar libre cerca de mapa=" + m + " x=" + x + " y=" + y);
				return false;
			}
			x = pos_libre.x;
			y = pos_libre.y;
			if (oldMap != m) {
				cambiarMapa(m, x, y, false, true);
			} else {
				cambiarMapa(m, x, y, false, false);
			}
		}
		enviarPU();
		// Call UpdateUserMap(UserIndex)
		// Seguis invisible al pasar de mapa
		if ((m_flags.Invisible || m_flags.Oculto) && !m_flags.AdminInvisible) {
			// mapa.enviarATodos(MSG_NOVER, m_id, 1);
		}
		if (conFX && !m_flags.AdminInvisible) { // FX
			enviarSonido(SND_WARP);
			enviarCFX(FXWARP, 0);
		}
		// warpMascotas();

		// Agus: byte bye user pet...
		petDelete();

		return true;
	}

	private void warpMascotas() {
		// FIXME - revisar
		int invocadosMatados = 0;
		Map oldMapa, newMapa;
		Npc npc;
		MapPos lugarLibre;
		for (int i = 0; i < m_mascotas.length; i++) {
			if (m_mascotas[i] != null && m_mascotas[i].m_contadores.TiempoExistencia > 0) {
				// Es una mascota de invocación. Se pierde al cambiar de mapa.
				oldMapa = server.getMapa(m_mascotas[i].getPos().map);
				oldMapa.salir(m_mascotas[i]);
				npc = m_mascotas[i];
				m_mascotas[i].releasePet();
				m_mascotas[i] = null;
				m_cantMascotas--;
				invocadosMatados++;
				npc.quitarNPC();
			} else if (m_mascotas[i] != null) {
				// Es una mascota domada.
				if (m_mascotas[i].puedeReSpawn()) {
					// La mascota puede hacer respawn
					oldMapa = server.getMapa(m_mascotas[i].getPos().map);
					newMapa = server.getMapa(m_pos.map);
					lugarLibre = newMapa.closestLegalPosNpc(m_pos.x, m_pos.y, m_mascotas[i].esAguaValida(),
							m_mascotas[i].esTierraInvalida(), true);
					if (lugarLibre != null) {
						// La mascota lo sigue al nuevo mapa, y mantiene su
						// control.
						oldMapa.salir(m_mascotas[i]);
						// envio un BP adicional, porque ya sali del mapa y
						// sino no soy notificado de que salio la mascota.
						// enviar(MSG_QDL, m_mascotas[i].getId());
						// enviar(MSG_BP, m_mascotas[i].getId());
						if (newMapa.entrar(m_mascotas[i], lugarLibre.x, lugarLibre.y)) {
							// FIXME !!!
						}
					} else {
						// La mascota no puede seguirlo al nuevo mapa, asi que
						// pierde su control.
						m_mascotas[i].releasePet();
						m_mascotas[i] = null;
						m_cantMascotas--;
						invocadosMatados++;
					}
				} else {
					// La mascota no puede seguirlo al nuevo mapa, asi que
					// pierde su control.
					m_mascotas[i].releasePet();
					m_mascotas[i] = null;
					m_cantMascotas--;
					invocadosMatados++;
				}
			}
		}
		if (invocadosMatados > 0) {
			enviarMensaje("Pierdes el control de tus mascotas.", FontType.INFO);
		}
	}

	private void quitarMascotas() {
		Map mapa;
		Npc npc;
		for (int i = 0; i < m_mascotas.length; i++) {
			if (m_mascotas[i] != null) {
				mapa = server.getMapa(m_mascotas[i].getPos().map);
				mapa.salir(m_mascotas[i]);
				npc = m_mascotas[i];
				m_mascotas[i].releasePet();
				m_mascotas[i] = null;
				m_cantMascotas--;
				npc.quitarNPC();
			}
		}
		if (m_cantMascotas != 0) {
			System.out.println("ERROR: Luego de quitarMascotas() la cant de mascotas != 0 !!!");
		}
	}

	public void encarcelar(int minutos, String gm_name) {
		m_counters.Pena = minutos;
		if (warpUser(AojServer.WP_PRISION.map, AojServer.WP_PRISION.x, AojServer.WP_PRISION.y, true)) {
			if (gm_name == null) {
				enviarMensaje("Has sido encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.INFO);
			} else {
				enviarMensaje(gm_name + " te ha encarcelado, deberas permanecer en la carcel " + minutos + " minutos.",
						FontType.INFO);
			}
		}
	}

	public void efectoLluvia() {
		if (m_flags.UserLogged) {
			Map mapa = server.getMapa(m_pos.map);
			if (server.estaLloviendo() && mapa.intemperie(m_pos.x, m_pos.y) && mapa.getZona() != ZONA_DUNGEON) {
				int modifi = Util.porcentaje(m_estads.maxStamina, 3);
				m_estads.quitarStamina(modifi);
				enviarMensaje("¡¡Has perdido stamina, busca pronto refugio de la lluvia!!.", FontType.INFO);
				refreshStatus(4);
			}
		}
	}

	public void efectoParalisisUser() {
		if (m_counters.Paralisis > 0) {
			m_counters.Paralisis--;
		} else {
			m_flags.Paralizado = false;
			enviar(ServerPacketID.paradOk);
		}
	}

	private boolean suerteMostrarse() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 7, 7 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Ocultarse] / 10)]);
		if (m_clase != ThiefClass.getInstance()) {
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
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Ocultarse] / 10)]);
		if (m_clase != ThiefClass.getInstance()) {
			rango += 50;
		}
		return (Util.Azar(1, rango) <= 5);
	}

	private void volverseOculto() {
		m_flags.Oculto = true;
		m_flags.Invisible = true;
		Map mapa = server.getMapa(m_pos.map);
		// mapa.enviarATodos(MSG_NOVER, m_id, 1);
		enviarMensaje("¡Te has escondido entre las sombras!", FontType.INFO);
	}

	public void doOcultarse() {
		if (suerteOcultarse()) {
			volverseOculto();
			subirSkill(Skill.SKILL_Ocultarse);
		} else {
			if (m_flags.UltimoMensaje != 4) {
				enviarMensaje("¡No has logrado esconderte!", FontType.INFO);
				m_flags.UltimoMensaje = 4;
			}
		}
		m_flags.Trabajando = true;
	}

	public void decirPalabrasMagicas(String palabrasMagicas) {
		hablar(COLOR_CYAN, palabrasMagicas, getId());
	}

	public void paralizar(Spell hechizo) {
		if (!getFlags().Paralizado) {
			getFlags().Paralizado = true;
			getCounters().Paralisis = IntervaloParalizado;
			enviarSonido(hechizo.WAV);
			enviarCFX(hechizo.FXgrh, hechizo.loops);
			enviar(ServerPacketID.paradOk);
		}
	}

	public void hablar(int color, String texto, int quienId) {
		if (texto.length() > MAX_TEXTO_HABLAR) {
			texto = texto.substring(0, MAX_TEXTO_HABLAR);
		}

		short id = (short) quienId;

		Map mapa = server.getMapa(m_pos.map);
		if (mapa != null) {
			mapa.enviarAlArea(getPos().x, getPos().y, ServerPacketID.dialog, color, texto, (short) quienId);
			// mapa.enviarATodos(serverPacketID.dialog, color, texto, (short) quienId);
		}
	}

	private int freeMascotaSlot() {
		for (int i = 0; i < m_mascotas.length; i++) {
			if (m_mascotas[i] == null) {
				return i + 1;
			}
		}
		return 0;
	}

	public Npc crearMascotaInvocacion(int npcId, MapPos targetPos) {
		if (m_cantMascotas >= MAXMASCOTAS) {
			return null;
		}
		Npc npc = Npc.crearNPC(npcId, targetPos, true, server);
		if (npc == null) {
			return null;
		}

		m_cantMascotas++;
		int slot = freeMascotaSlot();
		m_mascotas[slot - 1] = npc;
		npc.setPetUserOwner(this);
		npc.getContadores().TiempoExistencia = IntervaloInvocacion;
		npc.setGiveGLD(0);
		npc.followAmo();
		npc.enviarSonido(SND_WARP);
		npc.enviarCFX(FXWARP, 0);
		npc.activar();

		return npc;
	}

	public Object[] ccParams() {
		// Object[] params = { m_infoChar.getCuerpo(),
		// m_infoChar.getCabeza(), m_infoChar.getDir(), getId(),
		// getPos().x, getPos().y, m_infoChar.getArma(),
		// m_infoChar.getEscudo(), m_infoChar.getFX(), (short) 999,
		// m_infoChar.getCasco(), m_nick + getClan(),
		// (esCriminal() ? 255 : 0), m_flags.Privilegios };

		short crimi = 0;
		if (esCriminal())
			crimi = 1;

		Object[] params = { getId(), m_infoChar.getCuerpo(), m_infoChar.getCabeza(), m_infoChar.getDir(), getPos().x,
				getPos().y, m_infoChar.getArma(), m_infoChar.getEscudo(), m_infoChar.getCasco(), m_infoChar.getFX(),
				(short) 999, m_nick + getClan(), crimi, m_flags.Privilegios };

		return params;
	}

	private String getClan() {
		if (esGM()) {
			if (esDios()) {
				return " <GAME MASTER>";
			}
			if (esSemiDios()) {
				return " <SEMIDIOS>";
			}
			return " <CONSEJERO>";
		}
		if (!esMiembroClan()) {
			return "";
		}
		return " <" + m_guildInfo.getGuildName() + ">";
	}

	public void enviarCC() {

		// {getId(), m_infoChar.getCuerpo(), m_infoChar.getCabeza(),
		// m_infoChar.getDir(), getPos().x, getPos().y ,m_infoChar.getArma(),
		// m_infoChar.getEscudo(), m_infoChar.getCasco(), m_infoChar.getFX(), (short)
		// 999, m_nick + getClan(), crimi, m_flags.Privilegios};

		enviar(ServerPacketID.CC, getId(), m_infoChar.getCuerpo(), m_infoChar.getCabeza(), m_infoChar.getDir(),
				getPos().x, getPos().y, m_infoChar.getArma(), m_infoChar.getEscudo(), m_infoChar.getCasco(),
				m_infoChar.getFX(), (short) 999, m_nick + getClan(), (short) 1, m_flags.Privilegios);
	}

	/*
	 * private void enviarATodosCC() { Mapa m = server.getMapa(m_pos.mapa); if (m ==
	 * null) return; m.enviarATodos(getCC()); }
	 */

	private void enviarIP() {
		// enviar(MSG_IP, getId());
	}

	private void enviarLogged() {
		// enviar(MSG_LOGGED);
		enviar(ServerPacketID.login);
	}

	private void enviarCambioMapa(short mapa) {
		// enviar(MSG_CM, (short) mapa);
		// enviarMidi(13);
	}

	private void enviarMidi(int midi) {
		enviarMidi("" + midi + "-1");
	}

	private void enviarMidi(String midi) {
		// enviar(MSG_TM, midi);
	}

	public void enviarObjeto(int objId, int x, int y) {
		short grhIndex = server.getInfoObjeto(objId).GrhIndex;

		enviar(ServerPacketID.MSG_HO, grhIndex, (short) x, (short) y);
	}

	public void enviarBQ(int x, int y, boolean bloqueado) {
		short bq = 0;
		if (bloqueado)
			bq = 1;
		enviar(ServerPacketID.MSG_BQ, (short) x, (short) y, bq);
	}

	public void cuerpoDesnudo() {
		// DarCuerpoDesnudo
		m_infoChar.cuerpoDesnudo(m_raza, m_genero);
		m_flags.Desnudo = true;
	}

	public void quitarMascota(Npc npc) {
		if (m_cantMascotas < 1) {
			return;
		}
		for (int i = 0; i < m_mascotas.length; i++) {
			if (m_mascotas[i] == npc) {
				m_mascotas[i].releasePet();
				m_mascotas[i] = null;
				m_cantMascotas--;
				if (m_cantMascotas < 0) {
					m_cantMascotas = 0;
				}
				return;
			}
		}
	}

	public void enviarSonido(int sonido) {
		Map mapa = server.getMapa(m_pos.map);
		// Sonido
		if (mapa != null) {
			// mapa.enviarAlArea(m_pos.x, m_pos.y, MSG_TW, sonido);
			mapa.enviarAlArea(m_pos.x, m_pos.y, ServerPacketID.MSG_TW, (byte) sonido, m_pos.x, m_pos.y);
		}
	}

	public void userDie() {
		if (esGM()) {
			return;
		}
		Map mapa = server.getMapa(m_pos.map);
		// Sonido
		enviarSonido(SND_USERMUERTE);
		// Quitar el dialogo del usuario muerto
		// mapa.enviarAlArea(m_pos.x, m_pos.y, MSG_QDL, m_id);
		m_estads.MinHP = 0;
		m_estads.stamina = 0;
		m_flags.AtacadoPorNpc = 0;
		m_flags.AtacadoPorUser = 0;
		m_flags.Envenenado = false;
		m_flags.Muerto = true;
		if (m_flags.AtacadoPorNpc > 0) {
			Npc npc = server.getNPC(m_flags.AtacadoPorNpc);
			if (npc != null) {
				npc.oldMovement();
			} else {
				m_flags.AtacadoPorNpc = 0;
			}
		}
		// <<<< Paralisis >>>>
		if (m_flags.Paralizado) {
			m_flags.Paralizado = false;
			enviar(ServerPacketID.paradOk);
		}
		// <<<< Descansando >>>>
		if (m_flags.Descansar) {
			m_flags.Descansar = false;
			// enviar(MSG_DOK);
		}
		// <<<< Meditando >>>>
		if (m_flags.Meditando) {
			m_flags.Meditando = false;
			enviar(ServerPacketID.medOk);
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
		// desequipar herramienta
		if (m_inv.tieneHerramientaEquipada()) {
			m_inv.desequiparHerramienta();
		}
		// desequipar municiones
		if (m_inv.tieneMunicionEquipada()) {
			m_inv.desequiparMunicion();
		}
		// << Si es zona de trigger 6, no pierde el inventario >>
		if (mapa.getTrigger(m_pos.x, m_pos.y) != MapCell.TRIGGER6_PERMITE) {
			// << Si es newbie no pierde el inventario >>
			if (!esNewbie() || esCriminal()) {
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
		if (!m_flags.Navegando) {
			m_infoChar.m_cuerpo = iCuerpoMuerto;
			m_infoChar.m_cabeza = iCabezaMuerto;
			m_infoChar.m_escudo = NingunEscudo;
			m_infoChar.m_arma = NingunArma;
			m_infoChar.m_casco = NingunCasco;
		} else {
			m_infoChar.m_cuerpo = iFragataFantasmal;
		}
		for (Npc element : m_mascotas) {
			if (element != null) {
				if (element.getContadores().TiempoExistencia > 0) {
					element.muereNpc(null);
				} else {
					element.setPetUserOwner((Client) null);
					element.oldMovement();
				}
			}
		}
		m_cantMascotas = 0;
		enviarCP(); // Actualizar m_clients para mostrar el fantasmita.
		refreshStatus(2);
	}

	public void tirarTodo() {
		Map mapa = server.getMapa(m_pos.map);
		if (mapa.getTrigger(m_pos.x, m_pos.y) == MapCell.TRIGGER_ARENA_DUELOS || esGM()) {
			return;
		}
		tirarTodosLosItems();
		tirarOro(m_estads.getGold());
	}

	private void tirarTodosLosItems() {
		Map m = server.getMapa(m_pos.map);
		for (int i = 1; i <= m_inv.getSize(); i++) {
			if (m_inv.getObjeto(i) != null && m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = server.getInfoObjeto(m_inv.getObjeto(i).objid);
				if (info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(m_inv.getObjeto(i).objid, m_inv.getObjeto(i).cant);
					m_inv.quitarUserInvItem(i, obj_inv.cant);
					enviarObjetoInventario(i);
					if (info_obj.itemSeCae()) {
						m.tirarItemAlPiso(m_pos.x, m_pos.y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarTodosLosItemsNoNewbies() {
		Map m = server.getMapa(m_pos.map);
		for (int i = 1; i <= m_inv.getSize(); i++) {
			if (m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info_obj = server.getInfoObjeto(m_inv.getObjeto(i).objid);
				if (!info_obj.esNewbie() && info_obj.itemSeCae()) {
					InventoryObject obj_inv = new InventoryObject(m_inv.getObjeto(i).objid, m_inv.getObjeto(i).cant);
					m_inv.quitarUserInvItem(i, obj_inv.cant);
					enviarObjetoInventario(i);
					if (info_obj.itemSeCae()) {
						m.tirarItemAlPiso(m_pos.x, m_pos.y, obj_inv);
					}
				}
			}
		}
	}

	public void tirarOro(int cantidad) {
		if (cantidad > MAX_INVENTORY_OBJS) {
			return;
		}
		Map m = server.getMapa(m_pos.map);
		// SI EL Npc TIENE ORO LO TIRAMOS
		if ((cantidad > 0) && (cantidad <= m_estads.getGold())) {
			while ((cantidad > 0) && (m_estads.getGold() > 0)) {
				InventoryObject oi = new InventoryObject(OBJ_ORO, cantidad);
				if ((cantidad > MAX_INVENTORY_OBJS) && (m_estads.getGold() > MAX_INVENTORY_OBJS)) {
					oi.cant = MAX_INVENTORY_OBJS;
					m_estads.addGold( -MAX_INVENTORY_OBJS);
					cantidad -= oi.cant;
				} else {
					oi.cant = cantidad;
					m_estads.addGold( -cantidad );
					cantidad -= oi.cant;
				}
				if (esGM()) {
					Log.logGM(m_nick,
							"Tiró " + oi.cant + " unidades del objeto " + server.getInfoObjeto(oi.objid).Nombre);
				}
				m.tirarItemAlPiso(m_pos.x, m_pos.y, oi);
			}
		}
	}

	public void volverCriminal() {
		Map mapa = server.getMapa(m_pos.map);
		if (mapa.getTrigger(m_pos.x, m_pos.y) == MapCell.TRIGGER_ARENA_DUELOS) {
			return;
		}
		if (m_flags.Privilegios < 2) {
			if (!esCriminal()) {
				m_reputacion.condenar();
				if (m_faccion.ArmadaReal) {
					m_faccion.expulsarFaccionReal();
				}
				refreshPk();
				System.out.println(this.getNick() + " ahora es criminal");
			}
		}
	}

	public void subirSkill(int skill) {
		if (!m_flags.Hambre && !m_flags.Sed) {
			int prob = 0;
			if (m_estads.ELV <= 3) {
				prob = 25;
			} else if (m_estads.ELV < 6) {
				prob = 35;
			} else if (m_estads.ELV < 10) {
				prob = 40;
			} else if (m_estads.ELV < 20) {
				prob = 45;
			} else {
				prob = 50;
			}

			int aumenta = Util.Azar(1, prob);

			int lvl = m_estads.ELV;
			// if (lvl > MAX_SKILLS)
			// return;

			if (m_estads.getUserSkill(skill) >= Skill.MAX_SKILL_POINTS) {
				return;
			}
			if (aumenta == 7 && m_estads.getUserSkill(skill) < Skill.levelSkill[lvl]) {
				m_estads.addSkillPoints(skill, (byte) 1);
				enviarMensaje("¡Has mejorado tu skill " + Skill.skillsNames[skill] + " en un punto!. Ahora tienes "
						+ m_estads.getUserSkill(skill) + " pts.", FontType.INFO);
				m_estads.addExp((byte) 50);
				enviarMensaje("¡Has ganado 50 puntos de experiencia!", FontType.FIGHT);
				checkUserLevel();
			}
		}
	}

	public void userAsignaSkill(int skill, int amount) {
		if (m_estads.getUserSkill(skill) >= Skill.MAX_SKILL_POINTS || m_estads.getUserSkill(skill) + (amount) > 100
				|| m_estads.SkillPts < amount)
			return;

		m_estads.addSkillPoints(skill, (byte) amount);
		m_estads.SkillPts -= amount;

	}

	public boolean esNewbie() {
		return m_estads.ELV <= LimiteNewbie;
	}

	public void enviarSkills() {
		// Comando ESKI
		List<Byte> skills = new LinkedList<Byte>();
		for (int i = 0; i <= Skill.MAX_SKILLS; i++) {

			if (i == 0) {
				skills.add((byte) m_estads.getSkillPoints());
			} else {
				skills.add(m_estads.getUserSkill(i));
			}
		}

		enviar(ServerPacketID.userSkills, skills.toArray());
	}

	public void enviarSubirNivel(int pts) {
		// enviar(MSG_SUNI, pts);
	}

	public void envenenar() {
		m_flags.Envenenado = true;
		enviarMensaje("¡¡La criatura te ha envenenado!!", FontType.FIGHT);
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
				m_estads.setSkillPoints(0); // Pierde los skills points
				// acumulados por tramposo!
				doSALIR(); // Cerrar la conexion como castigo.
				return;
			}
			total += skills[i];
		}
		if (total > m_estads.SkillPts) {
			Log.logHack(m_nick + " IP:" + m_ip + " trató de hackear los skills (skills > SkillPts).");
			m_estads.setSkillPoints(0); // Pierde los skills points
			// acumulados
			// por tramposo!
			doSALIR(); // Cerrar la conexion como castigo.
			return;
		}
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		m_estads.subirSkills(skills);
	}

	public void checkUserLevel() {
		// ¿Alcanzo el maximo nivel?
		if (m_estads.ELV == STAT_MAXELV) {
			m_estads.Exp = 0;
			m_estads.ELU = 0;
			return;
		}
		while (m_estads.Exp >= m_estads.ELU) {
			// La exp alcanzó el máximo del nivel, entonces sube de nivel.
			boolean wasNewbie = esNewbie();
			enviarSonido(SOUND_NIVEL);
			enviarMensaje("¡Has subido de nivel!", FontType.INFO);
			int pts = (m_estads.ELV == 1) ? 10 : 5;
			m_estads.SkillPts += pts;
			enviarMensaje("Has ganado " + pts + " skillpoints.", FontType.INFO);
			m_estads.ELV++;
			m_estads.Exp -= m_estads.ELU;
			if (wasNewbie && !esNewbie()) {
				m_inv.quitarObjsNewbie();
				salirDeDN();
			}
			m_estads.ELU *= (m_estads.ELV < 11) ? 1.5 : ((m_estads.ELV < 25) ? 1.3 : 1.2);
			m_clase.subirEstads(this);
			enviarSkills();
			enviarSubirNivel(pts);
			refreshStatus(5);
		}
	}

	private void salirDeDN() {
		// Si el usuario dejó de ser Newbie, y estaba en el Dungeon Newbie
		// es transportado a su hogar de origen.
		if (m_pos.map == 37) {
			MapPos ciudad = server.getCiudadPos(m_hogar);
			warpUser(ciudad.map, ciudad.x, ciudad.y, true);
		}
	}

	public long poderEvasionEscudo() {
		return (long) ((m_estads.userSkills[Skill.SKILL_Defensa] * m_clase.modEvasionDeEscudoClase()) / 2);
	}

	public double poderEvasion() {
		double tmp = 0;
		if (m_estads.userSkills[Skill.SKILL_Tacticas] < 31) {
			tmp = m_estads.userSkills[Skill.SKILL_Tacticas] * m_clase.modificadorEvasion();
		} else if (m_estads.userSkills[Skill.SKILL_Tacticas] < 61) {
			tmp = (m_estads.userSkills[Skill.SKILL_Tacticas] + m_estads.userAtributos[ATRIB_AGILIDAD])
					* m_clase.modificadorEvasion();
		} else if (m_estads.userSkills[Skill.SKILL_Tacticas] < 91) {
			tmp = (m_estads.userSkills[Skill.SKILL_Tacticas] + (2 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorEvasion();
		} else {
			tmp = (m_estads.userSkills[Skill.SKILL_Tacticas] + (3 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorEvasion();
		}
		return (tmp + (2.5 * Util.Max(m_estads.ELV - 12, 0)));
	}

	public double poderAtaqueArma() {
		double tmp = 0;
		if (m_estads.userSkills[Skill.SKILL_Armas] < 31) {
			tmp = m_estads.userSkills[Skill.SKILL_Armas] * m_clase.modificadorPoderAtaqueArmas();
		} else if (m_estads.userSkills[Skill.SKILL_Armas] < 61) {
			tmp = ((m_estads.userSkills[Skill.SKILL_Armas] + m_estads.userAtributos[ATRIB_AGILIDAD])
					* m_clase.modificadorPoderAtaqueArmas());
		} else if (m_estads.userSkills[Skill.SKILL_Armas] < 91) {
			tmp = ((m_estads.userSkills[Skill.SKILL_Armas] + (2 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueArmas());
		} else {
			tmp = ((m_estads.userSkills[Skill.SKILL_Armas] + (3 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueArmas());
		}
		return tmp + (2.5 * Util.Max(m_estads.ELV - 12, 0));
	}

	public double poderAtaqueProyectil() {
		double tmp = 0;
		if (m_estads.userSkills[Skill.SKILL_Proyectiles] < 31) {
			tmp = (m_estads.userSkills[Skill.SKILL_Proyectiles] * m_clase.modificadorPoderAtaqueProyectiles());
		} else if (m_estads.userSkills[Skill.SKILL_Proyectiles] < 61) {
			tmp = ((m_estads.userSkills[Skill.SKILL_Proyectiles] + m_estads.userAtributos[ATRIB_AGILIDAD])
					* m_clase.modificadorPoderAtaqueProyectiles());
		} else if (m_estads.userSkills[Skill.SKILL_Proyectiles] < 91) {
			tmp = ((m_estads.userSkills[Skill.SKILL_Proyectiles] + (2 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueProyectiles());
		} else {
			tmp = ((m_estads.userSkills[Skill.SKILL_Proyectiles] + (3 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueProyectiles());
		}
		return (tmp + (2.5 * Util.Max(m_estads.ELV - 12, 0)));
	}

	public double poderAtaqueWresterling() {
		double tmp = 0;
		if (m_estads.userSkills[Skill.SKILL_Wresterling] < 31) {
			tmp = (m_estads.userSkills[Skill.SKILL_Wresterling] * m_clase.modificadorPoderAtaqueArmas());
		} else if (m_estads.userSkills[Skill.SKILL_Wresterling] < 61) {
			tmp = (m_estads.userSkills[Skill.SKILL_Wresterling] + m_estads.userAtributos[ATRIB_AGILIDAD])
					* m_clase.modificadorPoderAtaqueArmas();
		} else if (m_estads.userSkills[Skill.SKILL_Wresterling] < 91) {
			tmp = (m_estads.userSkills[Skill.SKILL_Wresterling] + (2 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueArmas();
		} else {
			tmp = (m_estads.userSkills[Skill.SKILL_Wresterling] + (3 * m_estads.userAtributos[ATRIB_AGILIDAD]))
					* m_clase.modificadorPoderAtaqueArmas();
		}
		return tmp + (2.5 * Util.Max(m_estads.ELV - 12, 0));
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
		long skillTacticas = m_estads.userSkills[Skill.SKILL_Tacticas];
		long skillDefensa = m_estads.userSkills[Skill.SKILL_Defensa];
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
				if (arma.SubTipo == SUBTYPE_MATADRAGONES) { // Usa la
					// matadragones?
					modifClase = m_clase.modicadorDañoClaseArmas();
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
						modifClase = m_clase.modicadorDañoClaseProyectiles();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = m_inv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = m_clase.modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			} else { // Ataca usuario
				if (arma.SubTipo == SUBTYPE_MATADRAGONES) {
					modifClase = m_clase.modicadorDañoClaseArmas();
					dañoArma = 1; // Si usa la espada matadragones daño es 1
					dañoMaxArma = 1;
				} else {
					if (arma.esProyectil()) {
						modifClase = m_clase.modicadorDañoClaseProyectiles();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
						if (arma.esMunicion()) {
							ObjectInfo proyectil = m_inv.getMunicion();
							dañoArma += Util.Azar(proyectil.MinHIT, proyectil.MaxHIT);
							dañoMaxArma = arma.MaxHIT;
						}
					} else {
						modifClase = m_clase.modicadorDañoClaseArmas();
						dañoArma = Util.Azar(arma.MinHIT, arma.MaxHIT);
						dañoMaxArma = arma.MaxHIT;
					}
				}
			}
		}
		dañoUsuario = Util.Azar(m_estads.MinHIT, m_estads.MaxHIT);
		double daño = (((3 * dañoArma) + ((dañoMaxArma / 5) * Util.Max(0, (m_estads.userAtributos[ATRIB_FUERZA] - 15)))
				+ dañoUsuario) * modifClase);
		return (int) daño;
	}

	public void userDañoNpc(Npc npc) {
		int daño = calcularDaño(npc);
		// esta navegando? si es asi le sumamos el daño del barco
		if (m_flags.Navegando) {
			daño += Util.Azar(m_inv.getBarco().MinHIT, m_inv.getBarco().MaxHIT);
		}
		daño -= npc.getEstads().Def;
		if (daño < 0) {
			daño = 0;
		}
		npc.getEstads().MinHP -= daño;
		if (daño > 0) {
			enviar(ServerPacketID.MSG_USERHITNPC, daño);
			npc.calcularDarExp(this, daño);
		} else {
			enviarSonido(SOUND_SWING);
			enviar(ServerPacketID.MSG_USERSWING);
		}
		if (npc.getEstads().MinHP > 0) {
			// Trata de apuñalar por la espalda al enemigo
			if (puedeApuñalar()) {
				apuñalar(npc, daño);
				subirSkill(Skill.SKILL_Apuñalar);
			}
		}
		if (npc.getEstads().MinHP <= 0) {
			// Si era un Dragon perdemos la espada matadragones
			if (npc.getNPCtype() == Npc.NPCTYPE_DRAGON) {
				quitarObjetos(iEspadaMataDragones, 1);
			}
			for (Npc element : m_mascotas) {
				if (element != null) {
					element.followAmo();
				}
			}

			npc.muereNpc(this);
		}
	}

	public boolean puedeApuñalar() {
		if (m_inv.tieneArmaEquipada()) {
			return ((m_estads.userSkills[Skill.SKILL_Apuñalar] >= MIN_APUÑALAR) && m_inv.getArma().apuñala())
					|| ((m_clase == AssassinClass.getInstance()) && m_inv.getArma().apuñala());
		}
		return false;
	}

	public void quitarObjetos(int objIdx, int cant) {
		if (cant <= 0) {
			return;
		}
		int cant_quitar;
		for (int i = 1; i <= m_inv.getSize(); i++) {
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
		int daño = Util.Azar(npc.m_estads.MinHIT, npc.m_estads.MaxHIT);
		int defbarco = 0;
		if (estaNavegando()) {
			ObjectInfo barco = m_inv.getBarco();
			defbarco = Util.Azar(barco.MinDef, barco.MaxDef);
		}
		int lugar = Util.Azar(1, 6);
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
		// enviar(MSG_N2, lugar, daño);
		enviar(ServerPacketID.msgN1, (byte) 1, lugar, daño);
		if (m_flags.Privilegios == 0) {
			m_estads.MinHP -= daño;
		}

		refreshStatus(2);

		// Muere el usuario
		if (m_estads.MinHP <= 0) {
			// enviar(MSG_6); // Le informamos que ha muerto ;)
			// Si lo mato un guardia
			if (esCriminal() && npc.m_NPCtype == Npc.NPCTYPE_GUARDIAS) {
				m_reputacion.decAsesino(vlAsesino / 4);
				m_reputacion.decBandido(vlAsalto / 4);
				m_reputacion.decLadron(vlCazador / 3);
			}
			if (npc.getPetUserOwner() != null) {
				npc.getPetUserOwner().allFollowAmo();
			} else {
				// Al matarlo no lo sigue mas
				if (npc.m_estads.Alineacion == 0) {
					npc.restoreOldMovement();
				}
			}
			userDie();
		}
	}

	/** Ordenar a las mascotas del usuario atacar a un Npc */
	public void checkPets(Npc npc) {
		for (Npc element : m_mascotas) {
			if (element != null) {
				if (element != npc) {
					if (element.m_targetNpc == null) {
						element.m_targetNpc = npc;
					}
					element.m_movement = Npc.MOV_NPC_ATACA_NPC;
				}
			}
		}
	}

	private void allFollowAmo() {
		for (Npc element : m_mascotas) {
			if (element != null) {
				element.followAmo();
			}
		}
	}

	/**
	 * Las mascotas que tienen como objetivo al Npc target, deben volver a seguir al
	 * amo
	 */
	public void mascotasFollowAmo(Npc target) {
		for (Npc element : m_mascotas) {
			if ((element != null) && (element.m_targetNpc == target)) {
				element.followAmo();
			}
		}
	}

	public void usuarioAtacaNpc(Npc npc) {
		if (m_pos.distance(npc.getPos()) > MAX_DISTANCIA_ARCO) {
			enviarMensaje("Estás muy lejos para disparar.", FontType.FIGHT);
			return;
		}
		if (m_faccion.ArmadaReal && npc.getPetUserOwner() != null && !npc.getPetUserOwner().esCriminal()) {
			enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
					FontType.WARNING);
			return;
		}
		if (npc.getPetUserOwner() != null && m_flags.Seguro && !npc.getPetUserOwner().esCriminal()) {
			enviarMensaje("Debes quitar el seguro para atacar a una mascota de un ciudadano.", FontType.WARNING);
			return;
		}
		if (npc.getEstads().Alineacion == 0 && m_flags.Seguro) {
			enviarMensaje("Debes quitar el seguro para atacar a una criatura no hostil.", FontType.WARNING);
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
			enviar(ServerPacketID.MSG_USERSWING);
		}
	}

	public void cambiarSeguro() {
		m_flags.Seguro = !m_flags.Seguro;
	}

	public void cambiarModoCombate() {
		// Entrar o salir modo combate
		if (m_flags.ModoCombate) {
			enviarMensaje("Has salido del modo de combate.", FontType.INFO);
		} else {
			enviarMensaje("Has pasado al modo de combate.", FontType.INFO);
		}
		m_flags.ModoCombate = !m_flags.ModoCombate;
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
		enviarMensaje(msg.toString(), FontType.INFO);
		enviarMensaje("Cantidad de usuarios: " + cant, FontType.INFO);
	}

	public void usuarioAtaca() {
		if (intervaloPermiteAtacar()) {
			// Pierde stamina
			if (m_estads.stamina >= 10) {
				// m_estads.quitarStamina(Util.Azar(1, 10));
			} else {
				enviarMensaje("Estas muy cansado para luchar.", FontType.INFO);
				return;
			}
			MapPos attackPos = m_pos.copy();
			attackPos.moveToDir(Direction.value(m_infoChar.getDir()));
			// Exit if not legal
			if (!attackPos.isValid()) {
				enviarSonido(SOUND_SWING);
				return;
			}
			Map mapa = server.getMapa(m_pos.map);
			Client cliente = mapa.getCliente(attackPos.x, attackPos.y);
			// Look for user
			if (cliente != null) {
				usuarioAtacaUsuario(cliente);
				// enviarEstadsUsuario();
				// cliente.enviarEstadsUsuario();
				return;
			}
			// Look for Npc
			Npc npc = mapa.getNPC(attackPos.x, attackPos.y);
			if (npc != null) {
				if (npc.getAttackable()) {
					if (npc.getPetUserOwner() != null && mapa.esZonaSegura()) {
						enviarMensaje("No podés atacar mascotas en zonas seguras", FontType.FIGHT);
						return;
					}
					usuarioAtacaNpc(npc);
				} else {
					enviarMensaje("No podés atacar a este Npc", FontType.FIGHT);
				}
				refreshStatus(4);
				return;
			}
			enviarSonido(SOUND_SWING);
			refreshStatus(4);
			m_flags.Trabajando = false;
		} else {
			log.info("NO PUEDE ATACAR");
		}
	}

	public boolean usuarioImpacto(Client victima) {
		double probExito = 0;
		long skillTacticas = victima.m_estads.userSkills[Skill.SKILL_Tacticas];
		long skillDefensa = victima.m_estads.userSkills[Skill.SKILL_Defensa];
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

	public void usuarioAtacaUsuario(Client victima) {
		if (!puedeAtacar(victima)) {
			return;
		}
		if (m_pos.distance(victima.getPos()) > MAX_DISTANCIA_ARCO) {
			enviarMensaje("Estás muy lejos para disparar.", FontType.FIGHT);
			return;
		}
		usuarioAtacadoPorUsuario(victima);
		if (usuarioImpacto(victima)) {
			enviarSonido(SND_IMPACTO);
			if (!victima.estaNavegando()) {
				victima.enviarCFX(FXSANGRE, 0);
			}
			userDañoUser(victima);
		} else {
			enviarSonido(SOUND_SWING);
			// enviar(MSG_U1);
			// victima.enviar(MSG_U3, m_nick);
		}
	}

	public void userDañoUser(Client victima) {
		int daño = calcularDaño(null);
		int defbarco = 0;
		envenenarUsuario(victima); // revisar... FIXME
		if (estaNavegando()) {
			ObjectInfo barco = m_inv.getBarco();
			daño += Util.Azar(barco.MinHIT, barco.MaxHIT);
		}
		if (victima.estaNavegando()) {
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
		victima.m_estads.quitarHP(daño);
		if (!m_flags.Hambre && !m_flags.Sed) {
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
		if (victima.m_estads.MinHP <= 0) {
			contarMuerte(victima);
			for (Npc element : m_mascotas) {
				if (element != null) {
					if (element.getTarget() == victima.getId()) {
						element.setTarget(0);
					}
					element.followAmo();
				}
			}
			actStats(victima);
		}
		// Controla el nivel del usuario
		checkUserLevel();
	}

	public void actStats(Client victima) {
		int daExp = victima.m_estads.ELV * 2;
		m_estads.addExp(daExp);
		// Lo mata
		this.enviarMensaje("Has matado " + victima.m_nick + "!", FontType.FIGHT);
		enviarMensaje("Has ganado " + daExp + " puntos de experiencia.", FontType.FIGHT);
		victima.enviarMensaje(m_nick + " te ha matado!", FontType.FIGHT);
		if (triggerZonaPelea(victima) != MapCell.TRIGGER6_PERMITE) {
			if (!victima.esCriminal()) {
				m_reputacion.incAsesino(vlAsesino * 2);
				m_reputacion.burguesRep = 0;
				m_reputacion.nobleRep = 0;
				m_reputacion.plebeRep = 0;
			} else {
				m_reputacion.incNoble(vlNoble);
			}
		}
		victima.userDie();
		m_estads.incUsuariosMatados();
		log.info("ASESINATO: " + m_nick + " asesino a " + victima.m_nick);
	}

	public void contarMuerte(Client usuarioMuerto) {
		if (usuarioMuerto.esNewbie()) {
			return;
		}
		if (triggerZonaPelea(usuarioMuerto) == MapCell.TRIGGER6_PERMITE) {
			return;
		}
		if (usuarioMuerto.esCriminal()) {
			if (!m_flags.LastCrimMatado.equalsIgnoreCase(usuarioMuerto.m_nick)) {
				m_flags.LastCrimMatado = usuarioMuerto.m_nick;
				m_faccion.CriminalesMatados++;
			}
			if (m_faccion.CriminalesMatados > MAXUSERMATADOS) {
				m_faccion.CriminalesMatados = 0;
				m_faccion.RecompensasReal = 0;
			}
		} else {
			if (!m_flags.LastCiudMatado.equalsIgnoreCase(usuarioMuerto.m_nick)) {
				m_flags.LastCiudMatado = usuarioMuerto.m_nick;
				m_faccion.CiudadanosMatados++;
			}
			if (m_faccion.CiudadanosMatados > MAXUSERMATADOS) {
				m_faccion.CiudadanosMatados = 0;
				m_faccion.RecompensasCaos = 0;
			}
		}
	}

	public void usuarioAtacadoPorUsuario(Client victima) {
		if (triggerZonaPelea(victima) == MapCell.TRIGGER6_PERMITE) {
			return;
		}
		if (!esMiembroClan() || !victima.esMiembroClan()) {
			if (!esCriminal() && !victima.esCriminal()) {
				volverCriminal();
			}
			if (!victima.esCriminal()) {
				m_reputacion.incBandido(vlAsalto);
			} else {
				m_reputacion.incNoble(vlNoble);
			}
		} else { // Están en clan
			if (!m_guild.isEnemy(victima.m_guildInfo.m_guildName)) {
				if (!esCriminal() && !victima.esCriminal()) {
					volverCriminal();
				}
				if (!victima.esCriminal()) {
					m_reputacion.incBandido(vlAsalto);
				} else {
					m_reputacion.incNoble(vlNoble);
				}
			} else {
				if (!victima.esCriminal()) {
					m_reputacion.incBandido(vlAsalto);
				} else {
					m_reputacion.incNoble(vlNoble);
				}
			}
		}
		allMascotasAtacanUser(victima);
		victima.allMascotasAtacanUser(this);
	}

	public void allMascotasAtacanUser(Client objetivo) {
		for (Npc element : m_mascotas) {
			if (element != null) {
				element.setAttackedBy(objetivo.getNick());
				element.defenderse();
			}
		}
	}

	public boolean puedeAtacar(Client victima) {
		Map mapa = server.getMapa(victima.getPos().map);

		if (!victima.isAlive()) {
			enviarMensaje("No puedes atacar a un espíritu", FontType.INFO);
			return false;
		}
		int t = triggerZonaPelea(victima);
		if (t == MapCell.TRIGGER6_PERMITE) {
			return true;
		} else if (t == MapCell.TRIGGER6_PROHIBE) {
			return false;
		}
		if (mapa.esZonaSegura()) {
			enviarMensaje("Esta es una zona segura, aqui no puedes atacar usuarios.", FontType.WARNING);
			return false;
		}
		if (mapa.getTrigger(victima.getPos().x, victima.getPos().y) == 4) {
			enviarMensaje("No puedes pelear aqui.", FontType.WARNING);
			return false;
		}
		if (!victima.esCriminal() && m_faccion.ArmadaReal) {
			enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar ciudadanos.", FontType.WARNING);
			return false;
		}
		if (victima.m_faccion.FuerzasCaos && m_faccion.FuerzasCaos) {
			enviarMensaje("Los seguidores de las Fuerzas del Caos tienen prohibido atacarse entre sí.",
					FontType.WARNING);
			return false;
		}
		if (!checkAlive("No puedes atacar porque estas muerto.")) {
			return false;
		}
		// Se asegura que la victima no es un GM
		if (victima.esGM()) {
			enviarMensaje("¡¡No puedes atacar a los administradores del juego!!", FontType.WARNING);
			return false;
		}
		if (!victima.isAlive()) {
			enviarMensaje("No puedes atacar a un espíritu.", FontType.WARNING);
			return false;
		}
		if (m_flags.Seguro) {
			if (!victima.esCriminal()) {
				enviarMensaje(
						"No puedes atacar ciudadanos, para hacerlo debes desactivar el seguro apretando la tecla S.",
						FontType.FIGHT);
				return false;
			}
		}
		// Implementacion de trigger 7 - Para torneos con espectadores
		if (mapa.getTrigger(m_pos.x, m_pos.y) == 7) {
			if (mapa.getTrigger(m_pos.x, m_pos.y) == 7
					&& mapa.getTrigger(victima.getPos().x, victima.getPos().y) != 7) {
				enviarMensaje("Para atacar a ese usuario, él se debe encontrar en tu misma zona.", FontType.FIGHT);
			} else if (mapa.getTrigger(m_pos.x, m_pos.y) != 7
					&& mapa.getTrigger(victima.getPos().x, victima.getPos().y) == 7) {
				enviarMensaje("Para atacar a ese usuario, debes encontrarte en la misma zona que él.", FontType.FIGHT);
			}
			return false;
		}
		return true;
	}

	private boolean suerteApuñalar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Apuñalar] / 10)]);
		return (Util.Azar(1, rango) == 3);
	}

	public void apuñalar(Npc victimaNPC, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 2;
			victimaNPC.getEstads().MinHP -= daño;
			enviarMensaje("Has apuñalado a la criatura por " + daño, FontType.FIGHT);
			subirSkill(Skill.SKILL_Apuñalar);
			victimaNPC.calcularDarExp(this, daño);
		} else {
			enviarMensaje("¡No has logrado apuñalar a tu enemigo!", FontType.FIGHT);
		}
	}

	public void apuñalar(Client victimaUsuario, int daño) {
		// DoApuñalar
		if (suerteApuñalar()) {
			daño *= 1.5;
			victimaUsuario.m_estads.MinHP -= daño;
			enviarMensaje("Has apuñalado a " + victimaUsuario.m_nick + " por " + daño, FontType.FIGHT);
			victimaUsuario.enviarMensaje("Te ha apuñalado " + m_nick + " por " + daño, FontType.FIGHT);
		} else {
			enviarMensaje("¡No has logrado apuñalar a tu enemigo!", FontType.FIGHT);
		}
	}

	public void npcAtacado(Npc npc) {
		// Guardamos el usuario que ataco el npc
		npc.setAttackedBy(m_nick);
		if (npc.getPetUserOwner() != null) {
			npc.getPetUserOwner().allMascotasAtacanUser(this);
		}
		if (esMascotaCiudadano(npc)) {
			volverCriminal();
			npc.defenderse();
		} else {
			// Reputacion
			if (npc.getEstads().Alineacion == 0 && npc.getPetUserOwner() == null) {
				if (npc.getNPCtype() == Npc.NPCTYPE_GUARDIAS) {
					volverCriminal();
				} else {
					m_reputacion.incBandido(vlAsalto);
				}
			} else if (npc.getEstads().Alineacion == 1) {
				m_reputacion.incPlebe(vlCazador / 2);
			}
			// hacemos que el npc se defienda
			npc.defenderse();
			checkPets(npc);
		}
	}

	public boolean esMascotaCiudadano(Npc npc) {
		if (npc.getPetUserOwner() != null) {
			if (!npc.getPetUserOwner().esCriminal()) {
				npc.getPetUserOwner().enviarMensaje("¡¡" + m_nick + " esta atacando a tu mascota!!", FontType.FIGHT);
				return true;
			}
		}
		return false;
	}

	public void connectNewUser(String nick, String clave, short raza, short genero, CharClass clase, String email,
			short hogar) {
		// Validar los datos recibidos :-)
		m_nick = nick;
		if (!server.nombrePermitido(m_nick)) {
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
		m_flags.Muerto = false;
		m_flags.Escondido = false;
		m_reputacion.asesinoRep = 0;
		m_reputacion.bandidoRep = 0;
		m_reputacion.burguesRep = 0;
		m_reputacion.ladronRep = 0;
		m_reputacion.nobleRep = 1000;
		m_reputacion.plebeRep = 30;
		m_clase = clase;
		m_raza = raza;
		m_genero = genero;
		m_email = email;
		m_hogar = hogar;
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		// if (!atributosValidos()) {
		// enviarError("Atributos invalidos.");
		// return;
		// }
		// %%%%%%%%%%%%% PREVENIR HACKEO DE LOS ATRIBUTOS %%%%%%%%%%%%%
		inicializarAtributos(raza);
		// m_estads.setSkills(skills);

		m_estads.SkillPts = 10;

		m_password = clave;
		m_infoChar.setDir(Direction.SOUTH);
		m_infoChar.cuerpoYCabeza(raza, genero);
		m_infoChar.m_arma = NingunArma;
		m_infoChar.m_escudo = NingunEscudo;
		m_infoChar.m_casco = NingunCasco;
		m_origChar = new CharInfo(m_infoChar);

		m_estads.inicializarEstads(clase);
		// Inicializar hechizos:
		if (clase.esMagica()) {
			m_spells.setSpell(1, HECHIZO_DARDO_MAGICO);
		}
		// ???????????????? INVENTARIO ¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿
		m_inv.clear();
		m_inv.setObjeto(1, new InventoryObject(MANZANA_ROJA_NEWBIES, 100, false));
		m_inv.setObjeto(2, new InventoryObject(BOTELLA_AGUA_NEWBIES, 100, false));
		m_inv.setArma(3, new InventoryObject(DAGA_NEWBIES, 1, true));
		switch (raza) {
		case RAZA_HUMANO:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_1, 1, true));
			break;
		case RAZA_ELFO:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_2, 1, true));
			break;
		case RAZA_ELFO_OSCURO:
			m_inv.setArmadura(4, new InventoryObject(VESTIMENTAS_COMUNES_NEWBIES_3, 1, true));
			break;
		case RAZA_ENANO:
			m_inv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		case RAZA_GNOMO:
			m_inv.setArmadura(4, new InventoryObject(ROPA_ENANO_NEWBIES, 1, true));
			break;
		}

		saveUser();
		connectUser(nick, clave);
	}

	private static String getPjFile(String nick) {
		return "pjs" + java.io.File.separator + nick.toLowerCase() + ".chr";
	}

	// TODO revisar para qué y cuándo se usa esto... está raro como chequeo de consistencia -gorlok
	public boolean existePersonaje() {
		return Util.existeArchivo(getPjFile(m_nick));
	}

	// FIXME esto no tiene porque estar en esta clase...
	public short indiceRaza(String nombreRaza) {
		if (nombreRaza.equalsIgnoreCase("HUMANO")) {
			return RAZA_HUMANO;
		} else if (nombreRaza.equalsIgnoreCase("ELFO")) {
			return RAZA_ELFO;
		} else if (nombreRaza.equalsIgnoreCase("ELFO OSCURO")) {
			return RAZA_ELFO_OSCURO;
		} else if (nombreRaza.equalsIgnoreCase("ENANO")) {
			return RAZA_ENANO;
		} else if (nombreRaza.equalsIgnoreCase("GNOMO")) {
			return RAZA_GNOMO;
		} else {
			// / :-?
			return RAZA_HUMANO;
		}
	}

	// FIXME esto no tiene porque estar en esta clase...
	public short indiceCiudad(String ciudad) {
		for (short i = 0; i < Constants.CIUDADES_NOMBRES.length; i++) {
			if (Constants.CIUDADES_NOMBRES[i].equalsIgnoreCase(ciudad)) {
				return i;
			}
		}
		return 0;
	}

	public void inicializarAtributos(int raza) {
		m_estads.userAtributos[ATRIB_FUERZA] += modificadorFuerza[raza];
		m_estads.userAtributos[ATRIB_AGILIDAD] += modificadorAgilidad[raza];
		m_estads.userAtributos[ATRIB_INTELIGENCIA] += modificadorInteligencia[raza];
		m_estads.userAtributos[ATRIB_CARISMA] += modificadorCarisma[raza];
		m_estads.userAtributos[ATRIB_CONSTITUCION] += modificadorConstitucion[raza];
	}

	public void enviarError(String msg) {
		log.warn("ERROR: " + msg);
		enviar(ServerPacketID.msgErr, msg);
	}

	public void saveUser() {
		// / guardar en archivo .chr
		try {
			IniFile ini = new IniFile();
			ini.setValue("FLAGS", "Muerto", m_flags.Muerto);
			ini.setValue("FLAGS", "Escondido", m_flags.Escondido);
			ini.setValue("FLAGS", "Hambre", m_flags.Hambre);
			ini.setValue("FLAGS", "Sed", m_flags.Sed);
			ini.setValue("FLAGS", "Desnudo", m_flags.Desnudo);
			ini.setValue("FLAGS", "Ban", m_flags.Ban);
			ini.setValue("FLAGS", "Navegando", m_flags.Navegando);
			ini.setValue("FLAGS", "Envenenado", m_flags.Envenenado);
			ini.setValue("FLAGS", "Paralizado", m_flags.Paralizado);

			ini.setValue("COUNTERS", "Pena", m_counters.Pena);

			ini.setValue("FACCIONES", "EjercitoReal", m_faccion.ArmadaReal);
			ini.setValue("FACCIONES", "EjercitoCaos", m_faccion.FuerzasCaos);
			ini.setValue("FACCIONES", "CiudMatados", m_faccion.CiudadanosMatados);
			ini.setValue("FACCIONES", "CrimMatados", m_faccion.CriminalesMatados);
			ini.setValue("FACCIONES", "rArCaos", m_faccion.RecibioArmaduraCaos);
			ini.setValue("FACCIONES", "rArReal", m_faccion.RecibioArmaduraReal);
			ini.setValue("FACCIONES", "rExCaos", m_faccion.RecibioExpInicialCaos);
			ini.setValue("FACCIONES", "rExReal", m_faccion.RecibioExpInicialReal);
			ini.setValue("FACCIONES", "recCaos", m_faccion.RecompensasCaos);
			ini.setValue("FACCIONES", "recReal", m_faccion.RecompensasReal);

			ini.setValue("GUILD", "EsGuildLeader", m_guildInfo.m_esGuildLeader);
			ini.setValue("GUILD", "Echadas", m_guildInfo.m_echadas);
			ini.setValue("GUILD", "Solicitudes", m_guildInfo.m_solicitudes);
			ini.setValue("GUILD", "SolicitudesRechazadas", m_guildInfo.m_solicitudesRechazadas);
			ini.setValue("GUILD", "VecesFueGuildLeader", m_guildInfo.m_vecesFueGuildLeader);
			ini.setValue("GUILD", "YaVoto", m_guildInfo.m_yaVoto);
			ini.setValue("GUILD", "FundoClan", m_guildInfo.m_fundoClan);
			ini.setValue("GUILD", "GuildName", m_guildInfo.m_guildName);
			ini.setValue("GUILD", "ClanFundado", m_guildInfo.m_clanFundado);
			ini.setValue("GUILD", "ClanesParticipo", m_guildInfo.m_clanesParticipo);
			ini.setValue("GUILD", "GuildPts", m_guildInfo.m_guildPoints);

			ini.setValue("QUEST", "NroQuest", m_quest.m_nroQuest);
			ini.setValue("QUEST", "EnQuest", m_quest.m_enQuest);
			ini.setValue("QUEST", "RealizoQuest", m_quest.m_realizoQuest);
			ini.setValue("QUEST", "Recompensa", m_quest.m_recompensa);

			ini.setValue("BAN", "BannedBy", m_banned_by);
			ini.setValue("BAN", "Reason", m_banned_reason);

			// ¿Fueron modificados los atributos del usuario?
			if (!m_flags.TomoPocion) {
				for (int i = 0; i < m_estads.userAtributos.length; i++) {
					ini.setValue("ATRIBUTOS", "AT" + (i + 1), m_estads.userAtributos[i]);
				}
			} else {
				for (int i = 0; i < m_estads.userAtributos.length; i++) {
					ini.setValue("ATRIBUTOS", "AT" + (i + 1), m_estads.userAtributosBackup[i]);
				}
			}

			for (int i = 1; i < m_estads.userSkills.length; i++) {
				ini.setValue("SKILLS", "SK" + i, m_estads.userSkills[i]);
			}

			ini.setValue("CONTACTO", "Email", m_email);

			ini.setValue("INIT", "Genero", m_genero);
			ini.setValue("INIT", "Raza", m_raza);
			ini.setValue("INIT", "Hogar", m_hogar);
			ini.setValue("INIT", "Clase", m_clase.getName());
			ini.setValue("INIT", "Password", m_password);
			ini.setValue("INIT", "Desc", m_desc);
			ini.setValue("INIT", "Heading", m_infoChar.m_dir);

			if (m_flags.Muerto || m_flags.Invisible || m_flags.Navegando) {
				ini.setValue("INIT", "Head", m_origChar.m_cabeza);
				ini.setValue("INIT", "Body", m_origChar.m_cuerpo);
			} else {
				ini.setValue("INIT", "Head", m_infoChar.m_cabeza);
				ini.setValue("INIT", "Body", m_infoChar.m_cuerpo);
			}

			ini.setValue("INIT", "Arma", m_infoChar.m_arma);
			ini.setValue("INIT", "Escudo", m_infoChar.m_escudo);
			ini.setValue("INIT", "Casco", m_infoChar.m_casco);
			ini.setValue("INIT", "LastIP", m_ip);
			ini.setValue("INIT", "Position", m_pos.map + "-" + m_pos.x + "-" + m_pos.y);

			ini.setValue("STATS", "GLD", m_estads.getGold());
			ini.setValue("STATS", "BANCO", m_estads.getBankGold());

			// ini.setValue("STATS", "MET", m_estads.MET);
			ini.setValue("STATS", "MaxHP", m_estads.MaxHP);
			ini.setValue("STATS", "MinHP", m_estads.MinHP);

			// ini.setValue("STATS", "FIT", m_estads.FIT);
			ini.setValue("STATS", "MaxSTA", m_estads.maxStamina);
			ini.setValue("STATS", "MinSTA", m_estads.stamina);

			ini.setValue("STATS", "MaxMAN", m_estads.maxMana);
			ini.setValue("STATS", "MinMAN", m_estads.mana);

			ini.setValue("STATS", "MaxHIT", m_estads.MaxHIT);
			ini.setValue("STATS", "MinHIT", m_estads.MinHIT);

			ini.setValue("STATS", "MaxAGU", m_estads.maxDrinked);
			ini.setValue("STATS", "MinAGU", m_estads.drinked);

			ini.setValue("STATS", "MaxHAM", m_estads.maxEaten);
			ini.setValue("STATS", "MinHAM", m_estads.eaten);

			ini.setValue("STATS", "SkillPtsLibres", m_estads.SkillPts);

			ini.setValue("STATS", "EXP", m_estads.Exp);
			ini.setValue("STATS", "ELV", m_estads.ELV);
			ini.setValue("STATS", "ELU", m_estads.ELU);

			ini.setValue("MUERTES", "UserMuertes", m_estads.usuariosMatados);
			// ini.setValue("MUERTES", "CrimMuertes",
			// m_estads.criminalesMatados);
			ini.setValue("MUERTES", "NpcsMuertes", m_estads.NPCsMuertos);

			int cant = m_bancoInv.getSize();
			for (int i = 0; i < m_bancoInv.getSize(); i++) {
				if (m_bancoInv.getObjeto(i + 1) == null) {
					ini.setValue("BancoInventory", "Obj" + (i + 1), "0-0");
					cant--;
				} else {
					ini.setValue("BancoInventory", "Obj" + (i + 1),
							m_bancoInv.getObjeto(i + 1).objid + "-" + m_bancoInv.getObjeto(i + 1).cant);
				}
			}
			ini.setValue("BancoInventory", "CantidadItems", cant);

			cant = m_inv.getSize();
			for (int i = 0; i < m_inv.getSize(); i++) {
				if (m_inv.getObjeto(i + 1) == null) {
					ini.setValue("Inventory", "Obj" + (i + 1), "0-0-0");
					cant--;
				} else {
					ini.setValue("Inventory", "Obj" + (i + 1), m_inv.getObjeto(i + 1).objid + "-"
							+ m_inv.getObjeto(i + 1).cant + "-" + (m_inv.getObjeto(i + 1).equipado ? 1 : 0));
				}
			}
			ini.setValue("Inventory", "CantidadItems", cant);
			ini.setValue("Inventory", "WeaponEqpSlot", m_inv.getArmaSlot());
			ini.setValue("Inventory", "ArmourEqpSlot", m_inv.getArmaduraSlot());
			ini.setValue("Inventory", "CascoEqpSlot", m_inv.getCascoSlot());
			ini.setValue("Inventory", "EscudoEqpSlot", m_inv.getEscudoSlot());
			ini.setValue("Inventory", "BarcoSlot", m_inv.getBarcoSlot());
			ini.setValue("Inventory", "MunicionSlot", m_inv.getMunicionSlot());
			ini.setValue("Inventory", "HerramientaSlot", m_inv.getHerramientaSlot());
			ini.setValue("Inventory", "EspadaMataDragonesSlot", m_inv.getEspadaMataDragonesSlot());

			// Reputacion
			ini.setValue("REP", "Asesino", m_reputacion.asesinoRep);
			ini.setValue("REP", "Bandido", m_reputacion.bandidoRep);
			ini.setValue("REP", "Burguesia", m_reputacion.burguesRep);
			ini.setValue("REP", "Ladrones", m_reputacion.ladronRep);
			ini.setValue("REP", "Nobles", m_reputacion.nobleRep);
			ini.setValue("REP", "Plebe", m_reputacion.plebeRep);

			ini.setValue("REP", "Promedio", m_reputacion.getPromedio());

			for (int slot = 1; slot <= m_spells.getCount(); slot++) {
				ini.setValue("HECHIZOS", "H" + slot, m_spells.getSpell(slot));
			}

			cant = m_cantMascotas;
			short cad = 0;
			for (int i = 0; i < m_mascotas.length; i++) {
				// Mascota valida?
				if (m_mascotas[i] != null) {
					// Nos aseguramos que la criatura no fue invocada
					if (m_mascotas[i].m_contadores.TiempoExistencia == 0) {
						cad = m_mascotas[i].getNPCtype();
					} else { // Si fue invocada no la guardamos
						cad = 0;
						cant--;
					}
					ini.setValue("MASCOTAS", "MAS" + (i + 1), cad);
				} else {
					cant--;
				}
			}
			if (cant < 0) {
				cant = 0;
			}
			ini.setValue("MASCOTAS", "NroMascotas", cant);

			// Devuelve el head de muerto
			if (!isAlive()) {
				m_infoChar.m_cabeza = iCabezaMuerto;
			}

			// Guardar todo
			ini.store(getPjFile(m_nick));
		} catch (Exception e) {
			log.fatal(m_nick + ": ERROR EN SAVEUSER()", e);
		}
	}

	public void connectUser(String nick, String passwd) {
		m_nick = nick;
		m_password = passwd;
		try {

			// ¿Existe el personaje?
			if (!existePersonaje()) {
				enviarError("El personaje no existe. Compruebe el nombre de usuario.");
				m_nick = "";
				return;
			}

			IniFile ini = new IniFile(getPjFile(m_nick));
			// Reseteamos los FLAGS
			m_flags.Escondido = false;
			m_flags.TargetNpc = 0;
			m_flags.TargetObj = 0;
			m_flags.TargetUser = 0;
			m_infoChar.m_fx = 0;

			if (server.usuarioYaConectado(this)) {
				enviarError("Perdon, pero ya esta conectado.");
				return;
			}

			// ¿Es el passwd valido?
			if (!m_password.equals(ini.getString("INIT", "Password"))) {
				enviarError("Clave incorrecta.");
				return;
			}
			// Cargamos los datos del personaje
			loadUserInit(ini);
			loadUserStats(ini);
			if (!validateChr()) {
				enviarError("Error en el personaje.");
				return;
			}

			loadUserReputacion(ini);

			if (server.esDios(m_nick)) {
				m_flags.Privilegios = 3;
				Log.logGM(m_nick, "El GM-DIOS se conectó desde la ip=" + m_ip);
			} else if (server.esSemiDios(m_nick)) {
				m_flags.Privilegios = 2;
				Log.logGM(m_nick, "El GM-SEMIDIOS se conectó desde la ip=" + m_ip);
			} else if (server.esConsejero(m_nick)) {
				m_flags.Privilegios = 1;
				Log.logGM(m_nick, "El GM-CONSEJERO se conectó desde la ip=" + m_ip);
			} else {
				// Usuario no privilegiado.
				m_flags.Privilegios = 0;
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

			if (m_flags.Navegando) {
				m_infoChar.m_cuerpo = !isAlive() ? iFragataFantasmal : m_inv.getBarco().Ropaje;
				m_infoChar.m_cabeza = 0;
				m_infoChar.m_arma = NingunArma;
				m_infoChar.m_escudo = NingunEscudo;
				m_infoChar.m_casco = NingunCasco;
			}

			enviarInventario();
			m_spells.enviarHechizos();

			int max_refresh = 5;

			for (int i = 1; i <= max_refresh; i++) {
				refreshStatus(i);
			}

			if (m_pos.map == 0) {
				// Posicion de comienzo
				if (m_hogar == CIUDAD_NIX) {
					m_pos = server.getCiudadPos(CIUDAD_NIX).copy();
				} else if (m_hogar == CIUDAD_ULLA) {
					m_pos = server.getCiudadPos(CIUDAD_ULLA).copy();
				} else if (m_hogar == CIUDAD_BANDER) {
					m_pos = server.getCiudadPos(CIUDAD_BANDER).copy();
				} else if (m_hogar == CIUDAD_LINDOS) {
					m_pos = server.getCiudadPos(CIUDAD_LINDOS).copy();
				} else {
					m_hogar = CIUDAD_ULLA;
					m_pos = server.getCiudadPos(CIUDAD_ULLA).copy();
				}
			} else {
				// if (MapData(m_pos.Map, m_pos.x,
				// m_pos.y).UserIndex <> 0 Then
				// Call CloseSocket(MapData(m_pos.Map, m_pos.x,
				// m_pos.y).UserIndex)
			}

			// agush ;-)
			if (server.estaLloviendo())
				enviar(ServerPacketID.userRain);

			if (!esCriminal()) {
				m_flags.Seguro = true;
				enviar(ServerPacketID.safeToggle, (byte) 1);
			} else {
				enviar(ServerPacketID.safeToggle, (byte) 0);
				m_flags.Seguro = false;
			}

			cambiarMapa(m_pos.map, m_pos.x, m_pos.y);

			enviarIndiceUsuario();

			m_flags.UserLogged = true;

			enviarLogged();

		} catch (Exception e) {
			log.fatal("ERROR EN connectUser(), nick=" + m_nick, e);
			enviarError("Hubo un error iniciando el personaje.");
		} finally {
			if (!m_flags.UserLogged) {
				doSALIR();
				m_nick = "";
			}
		}
	}

	private boolean validateChr() {
		return m_infoChar.m_cabeza != 0 && m_infoChar.m_cuerpo != 0 && validateSkills();
	}

	private boolean validateSkills() {
		for (int i = 1; i < m_estads.userSkills.length; i++) {
			if (m_estads.userSkills[i] < 0) {
				return false;
			}
			if (m_estads.userSkills[i] > 100) {
				m_estads.userSkills[i] = 100;
			}
		}
		return true;
	}

	public void doServerVersion() {
		enviarMensaje("-=<>[ Bienvenido a Argentun Online ]<>=-", FontType.WELLCOME);
		enviarMensaje("-=<>[    Powered by AOJ Server     ]<>=-", FontType.WELLCOME);
		enviarMensaje("Server version: " + VERSION, FontType.WELLCOME);
	}

	public void doDebug(String s) {
		if ("".equals(s)) {
			enviarMensaje("DEBUG: " + (server.isShowDebug() ? "ON" : "OFF"), FontType.INFO);
		} else if (s.equalsIgnoreCase("ON")) {
			server.setShowDebug(true);
			enviarMensaje("DEBUG: activado", FontType.INFO);
		} else if (s.equalsIgnoreCase("OFF")) {
			server.setShowDebug(false);
			enviarMensaje("DEBUG: desactivado", FontType.INFO);
		}
	}

	private void sendGuildNews() {
		if (esMiembroClan()) {
			server.getGuildMngr().sendGuildNews(this);
		}
	}

	private void loadUserInit(IniFile ini) throws java.io.IOException {
		m_faccion.ArmadaReal = ini.getShort("FACCIONES", "EjercitoReal") == 1;
		m_faccion.FuerzasCaos = ini.getShort("FACCIONES", "EjercitoCaos") == 1;
		m_faccion.CiudadanosMatados = ini.getLong("FACCIONES", "CiudMatados");
		m_faccion.CriminalesMatados = ini.getLong("FACCIONES", "CrimMatados");
		m_faccion.RecibioArmaduraCaos = ini.getShort("FACCIONES", "rArCaos") == 1;
		m_faccion.RecibioArmaduraReal = ini.getShort("FACCIONES", "rArReal") == 1;
		m_faccion.RecibioExpInicialCaos = ini.getShort("FACCIONES", "rExCaos") == 1;
		m_faccion.RecibioExpInicialReal = ini.getShort("FACCIONES", "rExReal") == 1;
		m_faccion.RecompensasCaos = ini.getShort("FACCIONES", "recCaos");
		m_faccion.RecompensasReal = ini.getShort("FACCIONES", "recReal");

		m_flags.Muerto = ini.getShort("FLAGS", "Muerto") == 1;
		m_flags.Escondido = ini.getShort("FLAGS", "Escondido") == 1;
		m_flags.Hambre = ini.getShort("FLAGS", "Hambre") == 1;
		m_flags.Sed = ini.getShort("FLAGS", "Sed") == 1;
		m_flags.Desnudo = ini.getShort("FLAGS", "Desnudo") == 1;
		m_flags.Envenenado = ini.getShort("FLAGS", "Envenenado") == 1;
		m_flags.Paralizado = ini.getShort("FLAGS", "Paralizado") == 1;
		m_flags.Ban = ini.getShort("FLAGS", "Ban") == 1;
		if (m_flags.Paralizado) {
			m_counters.Paralisis = IntervaloParalizado;
		}
		m_flags.Navegando = ini.getShort("FLAGS", "Navegando") == 1;
		m_counters.Pena = ini.getLong("COUNTERS", "Pena");
		m_email = ini.getString("CONTACTO", "Email");
		m_genero = (byte) ini.getShort("INIT", "Genero");
		m_clase = CharClassManager.getInstance().getClase(ini.getString("INIT", "Clase").toUpperCase());
		if (m_clase == null) {
			throw new java.io.IOException("Clase desconocida: " + ini.getString("INIT", "Clase").toUpperCase());
		}
		m_raza = (byte) ini.getShort("INIT", "Raza");
		m_hogar = (byte) ini.getShort("INIT", "Hogar");
		m_infoChar.m_dir = ini.getShort("INIT", "Heading");

		m_origChar.m_cabeza = ini.getShort("INIT", "Head");
		m_origChar.m_cuerpo = ini.getShort("INIT", "Body");
		m_origChar.m_arma = ini.getShort("INIT", "Arma");
		m_origChar.m_escudo = ini.getShort("INIT", "Escudo");
		m_origChar.m_casco = ini.getShort("INIT", "Casco");
		m_origChar.m_dir = (short)Direction.SOUTH.ordinal();

		m_banned_by = ini.getString("BAN", "BannedBy");
		m_banned_reason = ini.getString("BAN", "Reason");

		if (isAlive()) {
			m_infoChar.m_cabeza = m_origChar.m_cabeza;
			m_infoChar.m_cuerpo = m_origChar.m_cuerpo;
			m_infoChar.m_arma = m_origChar.m_arma;
			m_infoChar.m_escudo = m_origChar.m_escudo;
			m_infoChar.m_casco = m_origChar.m_casco;
			m_infoChar.m_dir = m_origChar.m_dir;
		} else {
			m_infoChar.m_cabeza = iCabezaMuerto;
			m_infoChar.m_cuerpo = iCuerpoMuerto;
			m_infoChar.m_arma = NingunArma;
			m_infoChar.m_escudo = NingunEscudo;
			m_infoChar.m_casco = NingunCasco;
		}

		m_desc = ini.getString("INIT", "Desc");
		{
			StringTokenizer st = new StringTokenizer(ini.getString("INIT", "Position"), "-");
			m_pos.map = Short.parseShort(st.nextToken());
			m_pos.x = Short.parseShort(st.nextToken());
			m_pos.y = Short.parseShort(st.nextToken());
		}

		// int banco_cant = ini.getInt("BancoInventory", "CantidadItems");
		// Lista de objetos en el banco.
		for (int i = 0; i < m_bancoInv.getSize(); i++) {
			String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			m_bancoInv.setObjeto(i + 1,
					new InventoryObject(Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken())));
		}

		// int cant = ini.getInt("Inventory", "CantidadItems");
		// Lista de objetos del inventario del usuario.
		for (int i = 0; i < m_inv.getSize(); i++) {
			String tmp = ini.getString("Inventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			m_inv.setObjeto(i + 1, new InventoryObject(Short.parseShort(st.nextToken()),
					Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken()) == 1));
		}
		m_inv.setArmaSlot(ini.getShort("Inventory", "WeaponEqpSlot"));
		m_inv.setArmaduraSlot(ini.getShort("Inventory", "ArmourEqpSlot"));
		m_flags.Desnudo = (m_inv.getArmaduraSlot() == 0);
		m_inv.setEscudoSlot(ini.getShort("Inventory", "EscudoEqpSlot"));
		m_inv.setCascoSlot(ini.getShort("Inventory", "CascoEqpSlot"));
		m_inv.setBarcoSlot(ini.getShort("Inventory", "BarcoSlot"));
		m_inv.setMunicionSlot(ini.getShort("Inventory", "MunicionSlot"));
		m_inv.setHerramientaSlot(ini.getShort("Inventory", "HerramientaSlot"));

		m_cantMascotas = ini.getShort("Mascotas", "NroMascotas");
		// Lista de mascotas.
		for (int i = 0; i < m_cantMascotas; i++) {
			m_mascotas[i] = server.getNPC(ini.getShort("Mascotas", "Mas" + (i + 1)));
		}

		m_guildInfo.m_fundoClan = ini.getShort("Guild", "FundoClan") == 1;
		m_guildInfo.m_esGuildLeader = ini.getShort("Guild", "EsGuildLeader") == 1;
		m_guildInfo.m_echadas = ini.getLong("Guild", "Echadas");
		m_guildInfo.m_solicitudes = ini.getLong("Guild", "Solicitudes");
		m_guildInfo.m_solicitudesRechazadas = ini.getLong("Guild", "SolicitudesRechazadas");
		m_guildInfo.m_vecesFueGuildLeader = ini.getLong("Guild", "VecesFueGuildLeader");
		m_guildInfo.m_yaVoto = ini.getShort("Guild", "YaVoto") == 1;
		m_guildInfo.m_clanesParticipo = ini.getLong("Guild", "ClanesParticipo");
		m_guildInfo.m_guildPoints = ini.getLong("Guild", "GuildPts");
		m_guildInfo.m_clanFundado = ini.getString("Guild", "ClanFundado");
		m_guildInfo.m_guildName = ini.getString("Guild", "GuildName");

		m_quest.m_nroQuest = ini.getShort("QUEST", "NroQuest");
		m_quest.m_recompensa = ini.getShort("QUEST", "Recompensa");
		m_quest.m_enQuest = (ini.getShort("QUEST", "EnQuest") == 1);
		m_quest.m_realizoQuest = (ini.getShort("QUEST", "RealizoQuest") == 1);
	}

	private void loadUserStats(IniFile ini) {
		for (int i = 0; i < NUMATRIBUTOS; i++) {
			m_estads.userAtributos[i] = (byte) ini.getShort("ATRIBUTOS", "AT" + (i + 1));
			m_estads.userAtributosBackup[i] = m_estads.userAtributos[i];
		}

		for (int i = 1; i < m_estads.userSkills.length; i++) {
			m_estads.userSkills[i] = (byte) ini.getShort("SKILLS", "SK" + i);
		}

		for (int slot = 1; slot <= m_spells.getCount(); slot++) {
			m_spells.setSpell(slot, ini.getShort("HECHIZOS", "H" + slot));
		}

		m_estads.setGold(ini.getInt("STATS", "GLD"));
		m_estads.setBankGold(ini.getInt("STATS", "BANCO"));

		m_estads.MaxHP = ini.getInt("STATS", "MaxHP");
		m_estads.MinHP = ini.getInt("STATS", "MinHP");

		m_estads.stamina = ini.getInt("STATS", "MinSTA");
		m_estads.maxStamina = ini.getInt("STATS", "MaxSTA");

		m_estads.maxMana = ini.getInt("STATS", "MaxMAN");
		m_estads.mana = ini.getInt("STATS", "MinMAN");

		m_estads.MaxHIT = ini.getInt("STATS", "MaxHIT");
		m_estads.MinHIT = ini.getInt("STATS", "MinHIT");

		m_estads.maxDrinked = ini.getInt("STATS", "MaxAGU");
		m_estads.drinked = ini.getInt("STATS", "MinAGU");

		m_estads.maxEaten = ini.getInt("STATS", "MaxHAM");
		m_estads.eaten = ini.getInt("STATS", "MinHAM");

		m_estads.SkillPts = ini.getInt("STATS", "SkillPtsLibres");

		m_estads.Exp = ini.getInt("STATS", "EXP");
		m_estads.ELU = ini.getInt("STATS", "ELU");
		m_estads.ELV = ini.getInt("STATS", "ELV");

		m_estads.usuariosMatados = ini.getInt("MUERTES", "UserMuertes");
		// m_estads.criminalesMatados = ini.getInt("MUERTES",
		// "CrimMuertes");
		m_estads.NPCsMuertos = ini.getInt("MUERTES", "NpcsMuertes");
	}

	private void loadUserReputacion(IniFile ini) {
		m_reputacion.asesinoRep = ini.getDouble("REP", "Asesino");
		m_reputacion.bandidoRep = ini.getDouble("REP", "Bandido");
		m_reputacion.burguesRep = ini.getDouble("REP", "Burguesia");
		m_reputacion.ladronRep = ini.getDouble("REP", "Ladrones");
		m_reputacion.nobleRep = ini.getDouble("REP", "Nobles");
		m_reputacion.plebeRep = ini.getDouble("REP", "Plebe");
	}

	public void efectoCegueEstu() {
		if (m_counters.Ceguera > 0) {
			m_counters.Ceguera--;
		} else {
			if (m_flags.Ceguera) {
				m_flags.Ceguera = false;
				// enviar(MSG_NSEGUE);
			} else {
				m_flags.Estupidez = false;
				// enviar(MSG_NESTUP);
			}
		}
	}

	public void efectoFrio() {
		if (m_counters.Frio < IntervaloFrio) {
			m_counters.Frio++;
		} else {
			Map mapa = server.getMapa(m_pos.map);
			if (mapa.getTerreno() == TERRENO_NIEVE) {
				enviarMensaje("¡¡Estas muriendo de frio, abrígate o morirás!!.", FontType.INFO);
				int modifi = Util.porcentaje(m_estads.MaxHP, 5);
				m_estads.MinHP -= modifi;
				refreshStatus(2);

				if (m_estads.MinHP < 1) {
					enviarMensaje("¡¡Has muerto de frio!!.", FontType.INFO);
					m_estads.MinHP = 0;
					userDie();
				}
			} else {
				if (m_estads.stamina > 0) { // Vericación agregada por gorlok
					int modifi = Util.porcentaje(m_estads.maxStamina, 5);
					m_estads.quitarStamina(modifi);
					enviarMensaje("¡¡Has perdido stamina, si no te abrigas rápido la perderás toda!!.", FontType.INFO);
				}
			}
			m_counters.Frio = 0;
			refreshStatus(4);
		}
	}

	public boolean sanar(int intervalo) {
		Map mapa = server.getMapa(m_pos.map);
		short trigger = mapa.getTrigger(m_pos.x, m_pos.y);
		if (trigger == 1 || trigger == 2 || trigger == 4) {
			return false;
		}
		// Con el paso del tiempo se va sanando... pero muy lentamente ;-)
		if (m_estads.MinHP < m_estads.MaxHP) {
			if (m_counters.HPCounter < intervalo) {
				m_counters.HPCounter++;
			} else {
				int mashit = Util.Azar(2, Util.porcentaje(m_estads.maxStamina, 5));
				m_counters.HPCounter = 0;
				m_estads.MinHP += mashit;
				refreshStatus(2);

				if (m_estads.MinHP > m_estads.MaxHP) {
					m_estads.MinHP = m_estads.MaxHP;
				}
				enviarMensaje("Has sanado.", FontType.INFO);
				return true;
			}
		}
		return false;
	}

	private boolean suerteMeditar() {
		final short[] suerte = { 35, 30, 28, 24, 22, 20, 18, 15, 10, 5, 5 };
		short rango = (suerte[(short) (m_estads.userSkills[Skill.SKILL_Meditar] / 10)]);
		return (Util.Azar(1, rango) == 1);
	}

	public void meditar() {
		long tActual = server.getMillis();
		if (tActual - m_counters.tInicioMeditar < TIEMPO_INICIO_MEDITAR) {
			return;
		}
		if (m_estads.stamina < m_estads.maxStamina) {
			return;
		}
		m_counters.IdleCount = 0;
		if (m_estads.mana >= m_estads.maxMana) {
			enviarMensaje("Has terminado de meditar.", FontType.INFO);
			enviar(ServerPacketID.medOk);
			m_flags.Meditando = false;
			m_infoChar.m_fx = 0;
			m_infoChar.m_loops = 0;
			enviarCFX(0, 0);
			return;
		}
		if (suerteMeditar()) {
			int cant = Util.porcentaje(m_estads.maxMana, 3);
			m_estads.aumentarMana(cant);
			enviarMensaje("¡Has recuperado " + cant + " puntos de mana!", FontType.INFO);
			refreshStatus(3);
			subirSkill(Skill.SKILL_Meditar);
		}
	}

	private boolean efectoVeneno() {
		if (m_counters.Veneno < IntervaloVeneno) {
			m_counters.Veneno++;
		} else {
			enviarMensaje("Estas envenenado, si no te curas moriras.", FontType.VENENO);
			m_counters.Veneno = 0;
			m_estads.MinHP -= Util.Azar(1, 5);
			refreshStatus(2);
			if (m_estads.MinHP < 1) {
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
		if (m_flags.DuracionEfecto > 0) {
			m_flags.DuracionEfecto--;
		} else if (m_flags.DuracionEfecto == 0) {
			m_flags.TomoPocion = false;
			m_flags.TipoPocion = 0;
			// Volver los atributos al estado normal
			m_estads.restoreAtributos();
		}
	}

	public void sendUserAtributos() {
		enviar(ServerPacketID.userAtri, m_estads.userAtributos[0], m_estads.userAtributos[1], m_estads.userAtributos[2],
				m_estads.userAtributos[3], m_estads.userAtributos[4]);
	}

	public boolean aplicarHambreYSed() {
		// Sed
		boolean enviarEstads = false;
		if (m_estads.drinked > 0) {
			if (m_counters.drinkCounter < IntervaloSed) {
				m_counters.drinkCounter++;
			} else {
				m_counters.drinkCounter = 0;
				m_estads.quitarSed(10);
				if (m_estads.drinked <= 0) {
					m_estads.drinked = 0;
					m_flags.Sed = true;
				}
				enviarEstads = true;
			}
		}
		// hambre
		if (m_estads.eaten > 0) {
			if (m_counters.foodCounter < IntervaloHambre) {
				m_counters.foodCounter++;
			} else {
				m_counters.foodCounter = 0;
				m_estads.quitarHambre(10);
				if (m_estads.eaten <= 0) {
					m_estads.eaten = 0;
					m_flags.Hambre = true;
				}
				enviarEstads = true;
			}
		}
		return enviarEstads;
	}

	public boolean recStamina(int intervalo) {
		Map mapa = server.getMapa(m_pos.map);
		short trigger = mapa.getTrigger(m_pos.x, m_pos.y);
		if (trigger == 2 || trigger == 3 || trigger == 4) {
			return false;
		}
		if (m_estads.stamina < m_estads.maxStamina) {
			if (m_counters.STACounter < intervalo) {
				m_counters.STACounter++;
			} else {
				m_counters.STACounter = 0;
				int massta = Util.Azar(1, Util.porcentaje(m_estads.maxStamina, 5));
				m_estads.aumentarStamina(massta);
				refreshStatus(4);
				// enviarMensaje("Te sientes menos cansado.", FontType.INFO);
				return true;
			}
		}
		return false;
	}

	private void tiempoInvocacion() {
		for (Npc element : m_mascotas) {
			if (element != null) {
				if (element.getContadores().TiempoExistencia > 0) {
					element.getContadores().TiempoExistencia--;
					if (element.getContadores().TiempoExistencia == 0) {
						Npc mascota = element;
						quitarMascota(element);
						mascota.muereNpc(null);
					}
				}
			}
		}
	}

	long lTirarBasura;

	/** Procesa eventos de los usuarios */
	public void procesarEventos() {
		// Is user logged?
		if (m_flags.UserLogged) {
			boolean bEnviarStats = false;
			boolean bEnviarAyS = false;
			NumeroPaquetesPorMiliSec = 0;
			if (m_flags.Paralizado) {
				efectoParalisisUser();
			}
			if (m_flags.Ceguera || m_flags.Estupidez) {
				efectoCegueEstu();
			}
			if (isAlive()) {
				if (m_flags.Desnudo && m_flags.Privilegios == 0) {
					efectoFrio();
				}
				if (m_flags.Meditando) {
					meditar();
				}
				if (m_flags.Envenenado && m_flags.Privilegios == 0) {
					bEnviarStats = efectoVeneno();
				}
				if (!m_flags.AdminInvisible && m_flags.Invisible) {
					efectoInvisibilidad();
				}
				duracionPociones();
				bEnviarAyS = aplicarHambreYSed();
				Map mapa = server.getMapa(m_pos.map);
				if (!(server.estaLloviendo() && mapa.intemperie(m_pos.x, m_pos.y))) {
					if (!m_flags.Descansar && !m_flags.Hambre && !m_flags.Sed) {
						// No esta descansando

						if (sanar(SanaIntervaloSinDescansar))
							refreshStatus(2);
						if (recStamina(StaminaIntervaloSinDescansar))
							refreshStatus(4);

						// bEnviarStats = sanar(SanaIntervaloSinDescansar);
						// bEnviarStats = recStamina(StaminaIntervaloSinDescansar);
					} else if (m_flags.Descansar) {
						// esta descansando

						if (sanar(SanaIntervaloDescansar))
							refreshStatus(2);
						if (recStamina(StaminaIntervaloDescansar))
							refreshStatus(4);

						// bEnviarStats = sanar(SanaIntervaloDescansar);
						// bEnviarStats = recStamina(StaminaIntervaloDescansar);
						// termina de descansar automaticamente
						if (m_estads.MaxHP == m_estads.MinHP && m_estads.maxStamina == m_estads.stamina) {
							// enviar(MSG_DOK);
							enviarMensaje("Has terminado de descansar.", FontType.INFO);
							m_flags.Descansar = false;
						}
					}
				}
				// Verificar muerte por hambre
				if (m_estads.eaten <= 0 && !esGM()) {
					enviarMensaje("¡¡Has muerto de hambre!!.", FontType.INFO);
					m_estads.eaten = 0;
					userDie();
				}
				// Verificar muerte de sed
				if (m_estads.drinked <= 0 && !esGM()) {
					enviarMensaje("¡¡Has muerto de sed!!.", FontType.INFO);
					m_estads.drinked = 0;
					userDie();
				}
				// if (bEnviarStats) {
				// enviarEstadsUsuario();
				// }
				if (bEnviarAyS) {
					enviarEstadsHambreSed();
				}
				if (m_cantMascotas > 0) {
					tiempoInvocacion();
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
		StringBuffer msg = new StringBuffer(m_nick);
		if (esNewbie()) {
			msg.append(" <NEWBIE>");
		}
		msg.append(getTituloFaccion());
		if (esMiembroClan()) {
			msg.append(" <" + m_guildInfo.getGuildName() + ">");
		}
		if (m_desc.length() > 0) {
			msg.append(" - " + m_desc);
		}
		if (esDios()) {
			msg.append(" <GAME MASTER>");
		} else if (esSemiDios()) {
			msg.append(" <SEMIDIOS>");
		} else if (esConsejero()) {
			msg.append(" <CONSEJERO>");
		} else if (esCriminal()) {
			msg.append(" <CRIMINAL>");
		} else {
			msg.append(" <CIUDADANO>");
		}
		return msg.toString();
	}

	public FontType getTagColor() {
		if (esDios()) {
			return FontType.TAG_GOD;
		}
		if (esSemiDios()) {
			return FontType.TAG_SEMIGOD;
		}
		if (esConsejero()) {
			return FontType.TAG_CONSEJERO;
		}
		if (esCriminal()) {
			return FontType.TAG_CRIMINAL;
		}
		return FontType.TAG_CIUDADANO;
	}

	public boolean sexoPuedeUsarItem(short objid) {
		ObjectInfo infoObj = server.getInfoObjeto(objid);
		if (infoObj.esParaMujeres()) {
			return (m_genero == GENERO_MUJER);
		} else if (infoObj.esParaHombres()) {
			return (m_genero == GENERO_HOMBRE);
		} else {
			return true;
		}
	}

	public boolean checkRazaUsaRopa(short objid) {
		// Verifica si la raza puede usar esta ropa
		ObjectInfo infoObj = server.getInfoObjeto(objid);
		if (m_raza == RAZA_HUMANO || m_raza == RAZA_ELFO || m_raza == RAZA_ELFO_OSCURO) {
			return !infoObj.esParaRazaEnana();
		}
		return infoObj.esParaRazaEnana();
	}

	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////
	// ESTO PROVIENE DE TRABAJO.BAS

	private boolean tieneObjetos(short objid, int cant) {
		int total = 0;
		for (int i = 1; i <= m_inv.getSize(); i++) {
			if (m_inv.getObjeto(i).objid == objid) {
				total += m_inv.getObjeto(i).cant;
			}
		}
		return (cant <= total);
	}

	private void herreroQuitarMateriales(short objid) {
		ObjectInfo info = server.getInfoObjeto(objid);
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
		ObjectInfo info = server.getInfoObjeto(objid);
		if (info.Madera > 0) {
			quitarObjetos(Leña, info.Madera);
		}
	}

	private boolean carpinteroTieneMateriales(short objid) {
		ObjectInfo info = server.getInfoObjeto(objid);
		if (info.Madera > 0) {
			if (!tieneObjetos(Leña, info.Madera)) {
				enviarMensaje("No tenes suficientes madera.", FontType.INFO);
				return false;
			}
		}
		return true;
	}

	private boolean herreroTieneMateriales(short objid) {
		ObjectInfo info = server.getInfoObjeto(objid);
		if (info.LingH > 0) {
			if (!tieneObjetos(LingoteHierro, info.LingH)) {
				enviarMensaje("No tienes suficientes lingotes de hierro.", FontType.INFO);
				return false;
			}
		}
		if (info.LingP > 0) {
			if (!tieneObjetos(LingotePlata, info.LingP)) {
				enviarMensaje("No tienes suficientes lingotes de plata.", FontType.INFO);
				return false;
			}
		}
		if (info.LingO > 0) {
			if (!tieneObjetos(LingoteOro, info.LingO)) {
				enviarMensaje("No tienes suficientes lingotes de oro.", FontType.INFO);
				return false;
			}
		}
		return true;
	}

	private boolean puedeConstruir(short objid) {
		ObjectInfo info = server.getInfoObjeto(objid);
		return herreroTieneMateriales(objid) && m_estads.getUserSkill(Skill.SKILL_Herreria) >= info.SkHerreria;
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
			Map mapa = server.getMapa(m_pos.map);
			if (mapa == null) {
				return;
			}
			ObjectInfo info = server.getInfoObjeto(objid);
			herreroQuitarMateriales(objid);
			// AGREGAR FX
			if (info.ObjType == OBJTYPE_WEAPON) {
				enviarMensaje("Has construido el arma!.", FontType.INFO);
			} else if (info.ObjType == OBJTYPE_ESCUDO) {
				enviarMensaje("Has construido el escudo!.", FontType.INFO);
			} else if (info.ObjType == OBJTYPE_CASCO) {
				enviarMensaje("Has construido el casco!.", FontType.INFO);
			} else if (info.ObjType == OBJTYPE_ARMOUR) {
				enviarMensaje("Has construido la armadura!.", FontType.INFO);
			}
			if (m_inv.agregarItem(objid, 1) < 1) {
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Herreria);
			enviarInventario();
			enviarSonido(MARTILLOHERRERO);
			m_flags.Trabajando = true;
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
		ObjectInfo info = server.getInfoObjeto(objid);
		Map mapa = server.getMapa(m_pos.map);
		if (mapa == null) {
			return;
		}
		if (carpinteroTieneMateriales(objid) && m_estads.getUserSkill(Skill.SKILL_Carpinteria) >= info.SkCarpinteria
				&& puedeConstruirCarpintero(objid) && m_inv.getHerramienta().ObjIndex == SERRUCHO_CARPINTERO) {
			carpinteroQuitarMateriales(objid);
			enviarMensaje("¡Has construido el objeto!", FontType.INFO);
			if (m_inv.agregarItem(objid, 1) < 1) {
				mapa.tirarItemAlPiso(m_pos.x, m_pos.y, new InventoryObject(objid, 1));
			}
			subirSkill(Skill.SKILL_Carpinteria);
			enviarInventario();
			enviarSonido(LABUROCARPINTERO);
			m_flags.Trabajando = true;
		}
	}

	// //################################# FIN TRABAJO
	// ###################################

	private void doAdminInvisible() {
		if (!m_flags.AdminInvisible) {
			m_flags.AdminInvisible = true;
			m_flags.Invisible = true;
			m_flags.OldBody = m_infoChar.m_cuerpo;
			m_flags.OldHead = m_infoChar.m_cabeza;
			m_infoChar.m_cuerpo = 0;
			m_infoChar.m_cabeza = 0;
		} else {
			m_flags.AdminInvisible = false;
			m_flags.Invisible = false;
			m_infoChar.m_cuerpo = m_flags.OldBody;
			m_infoChar.m_cabeza = m_flags.OldHead;
		}
		enviarCP();
	}

	private void volverCiudadano() {
		Map mapa = server.getMapa(m_pos.map);
		if (mapa.getTrigger(m_pos.x, m_pos.y) == MapCell.TRIGGER_ARENA_DUELOS) {
			return;
		}
		boolean eraCrimi = esCriminal();
		m_reputacion.perdonar();
		if (eraCrimi) {
			refreshCiu();
		}

		System.out.println(this.getNick() + "ahora es ciuda");

	}

	private void actualizarUserChar() {
		Map mapa = server.getMapa(m_pos.map);
		// mapa.enviarATodos(MSG_BP, getId());
		// mapa.enviarATodos(MSG_CC, ccParams());
	}

	private void refreshPk() {
		Map mapa = server.getMapa(m_pos.map);
		mapa.enviarAlArea(getPos().x, getPos().y, ServerPacketID.updateStats, getId(), (byte) 1);
	}

	public void refreshCiu() {
		Map mapa = server.getMapa(m_pos.map);
		mapa.enviarAlArea(getPos().x, getPos().y, ServerPacketID.updateStats, getId(), (byte) 0);
	}

	public void cerrarUsuario() {
		if (m_flags.UserLogged && !m_counters.Saliendo) {
			m_counters.Saliendo = true;
			Map mapa = server.getMapa(m_pos.map);
			if (mapa != null && mapa.esZonaSegura()) {
				m_counters.SalirCounter = 1; // 1 segundo.
			} else {
				m_counters.SalirCounter = IntervaloCerrarConexion; // Esto
				// es
				// 10
				// segundos
				enviarMensaje("Cerrando... Se cerrará el juego en " + IntervaloCerrarConexion + " segundos...",
						FontType.INFO);
			}
		}
	}

	private void sendUserBovedaTxt(Client usuario) {
		enviarMensaje("El usuario " + usuario.getNick(), FontType.INFO_B);
		enviarMensaje("Tiene " + usuario.m_bancoInv.getCantObjs() + " objetos.", FontType.INFO);
		InventoryObject obj;
		ObjectInfo iobj;
		for (int i = 1; i <= usuario.m_bancoInv.getSize(); i++) {
			if ((obj = usuario.m_bancoInv.getObjeto(i)) != null) {
				iobj = server.getInfoObjeto(obj.objid);
				enviarMensaje(" Objeto " + i + ": " + iobj.Nombre + " Cantidad:" + obj.cant, FontType.INFO);
			}
		}
	}

	private void sendUserBovedaTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			enviarMensaje("Pj: " + pj, FontType.INFO_B);
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				int cantObjs = ini.getInt("BancoInventory", "CantidadItems");
				enviarMensaje("Tiene " + cantObjs + " objetos.", FontType.INFO);
				// Lista de objetos en el banco.
				ObjectInfo iobj;
				for (int i = 0; i < MAX_BANCOINVENTORY_SLOTS; i++) {
					String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = server.getInfoObjeto(objid);
						enviarMensaje(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.INFO);
					}
				}
			} catch (java.io.IOException e) {
				// / fixme
			}
		} else {
			enviarMensaje("Usuario inexistente: " + pj, FontType.INFO);
		}
	}

	public void desplazarHechizo(short dir, short slot) {
		if (dir < 1 || dir > 2) {
			return;
		}
		if (slot < 1 || slot > MAX_HECHIZOS) {
			return;
		}

		m_spells.moveSpell(slot, dir);
	}

	private void sendUserMiniStatsTxt(Client usuario) {
		if (usuario == null) {
			return;
		}
		enviarMensaje("Pj: " + usuario.getNick() + " Clase: " + usuario.m_clase.getName(), FontType.INFO_B);
		enviarMensaje("CiudadanosMatados: " + usuario.getFaccion().CiudadanosMatados + " CriminalesMatados: "
				+ usuario.getFaccion().CriminalesMatados + " UsuariosMatados: " + usuario.getEstads().usuariosMatados,
				FontType.INFO);
		enviarMensaje("NPCsMuertos: " + usuario.getEstads().NPCsMuertos + " Pena: " + usuario.m_counters.Pena,
				FontType.INFO);
	}

	private void sendUserMiniStatsTxtFromChar(String pj) {
		// BanDetailPath = App.Path & "\logs\" & "BanDetail.dat"
		// CharFile = CharPath & CharName & ".chr"
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				enviarMensaje("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.INFO_B);
				enviarMensaje("CiudadanosMatados: " + ini.getLong("FACCIONES", "CiudMatados") + " CriminalesMatados: "
						+ ini.getLong("FACCIONES", "CrimMatados") + " UsuariosMatados: "
						+ ini.getInt("MUERTES", "UserMuertes"), FontType.INFO);
				enviarMensaje("NPCsMuertos: " + ini.getInt("MUERTES", "NpcsMuertes") + " Pena: "
						+ ini.getLong("COUNTERS", "PENA"), FontType.INFO);
				boolean ban = ini.getShort("FLAGS", "Ban") == 1;
				enviarMensaje("Ban: " + (ban ? "si" : "no"), FontType.INFO);
				if (ban) {
					enviarMensaje("Ban por: " + ini.getString("BAN", "BannedBy") + " Motivo: "
							+ ini.getString("BAN", "Reason"), FontType.FIGHT);
				}
			} catch (java.io.IOException e) {
				// fixme
			}
		} else {
			enviarMensaje("El pj no existe: " + pj, FontType.INFO);
		}
	}

	private void sendUserInvTxtFromChar(String pj) {
		if (Util.existeArchivo(getPjFile(pj))) {
			try {
				IniFile ini = new IniFile(getPjFile(pj));
				enviarMensaje("Pj: " + pj + " Clase: " + ini.getString("INIT", "Clase"), FontType.INFO_B);
				enviarMensaje("Tiene " + ini.getShort("Inventory", "CantidadItems") + " objetos.", FontType.INFO);
				ObjectInfo iobj;
				for (int i = 0; i < MAX_INVENTORY_SLOTS; i++) {
					String tmp = ini.getString("Inventory", "Obj" + (i + 1));
					StringTokenizer st = new StringTokenizer(tmp, "-");
					short objid = Short.parseShort(st.nextToken());
					short cant = Short.parseShort(st.nextToken());
					if (objid > 0) {
						iobj = server.getInfoObjeto(objid);
						enviarMensaje(" Objeto " + i + " " + iobj.Nombre + " Cantidad:" + cant, FontType.INFO);
					}
				}
			} catch (java.io.IOException e) {
				// fixme
			}
		} else {
			enviarMensaje("El pj no existe: " + pj, FontType.INFO);
		}
	}

	private int triggerZonaPelea(Client victima) {
		if (victima == null) {
			return MapCell.TRIGGER6_AUSENTE;
		}
		int t1 = server.getMapa(m_pos.map).getTrigger(m_pos.x, m_pos.y);
		int t2 = server.getMapa(victima.getPos().map).getTrigger(victima.getPos().x, victima.getPos().y);
		if (t1 != MapCell.TRIGGER_ARENA_DUELOS && t2 != MapCell.TRIGGER_ARENA_DUELOS) {
			return MapCell.TRIGGER6_AUSENTE;
		}
		if (t1 == t2) {
			return MapCell.TRIGGER6_PERMITE;
		}
		return MapCell.TRIGGER6_PROHIBE;
	}

	private void envenenarUsuario(Client victima) {
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
					victima.m_flags.Envenenado = true;
					victima.enviarMensaje(m_nick + " te ha envenenado!!", FontType.FIGHT);
					enviarMensaje("Has envenenado a " + victima.getNick() + "!!", FontType.FIGHT);
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
					// .append(ini.getString("Guild", "YaVoto")).append(",")
					.append(ini.getString("Guild", "ClanesParticipo")).append(",")
					.append(ini.getString("Guild", "ClanFundado")).append(",")
					.append(ini.getString("Guild", "GuildName")).append(",")
					.append(ini.getString("FACCIONES", "EjercitoReal")).append(",")
					.append(ini.getString("FACCIONES", "EjercitoCaos")).append(",")
					.append(ini.getString("FACCIONES", "CiudMatados")).append(",")
					.append(ini.getString("FACCIONES", "CiudMatados")).append(",");
			return sb.toString();
		} catch (java.io.IOException e) {
			// fixme
		}
		return null;
	}

	// Las siguientes funciones devuelven TRUE o FALSE si el intervalo
	// permite hacerlo. Si devuelve TRUE, setean automaticamente el
	// timer para que no se pueda hacer la accion hasta el nuevo ciclo.

	/** INTERVALO DE CASTING DE HECHIZOS */
	public boolean intervaloPermiteLanzarSpell() {
		long time = server.getMillis();
		if ((time - m_counters.TimerLanzarSpell) >= IntervaloUserPuedeCastear) {
			m_counters.TimerLanzarSpell = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE ATAQUE CUERPO A CUERPO */
	public boolean intervaloPermiteAtacar() {
		long time = server.getMillis();
		if ((time - m_counters.TimerPuedeAtacar) >= IntervaloUserPuedeAtacar) {
			m_counters.TimerPuedeAtacar = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE TRABAJO */
	public boolean intervaloPermiteTrabajar() {
		long time = server.getMillis();
		if ((time - m_counters.TimerPuedeTrabajar) >= IntervaloUserPuedeTrabajar) {
			m_counters.TimerPuedeTrabajar = time;
			return true;
		}
		return false;
	}

	/** INTERVALO DE USAR OBJETOS */
	public boolean intervaloPermiteUsar() {
		long time = server.getMillis();
		if ((time - m_counters.TimerUsar) >= IntervaloUserPuedeUsar) {
			m_counters.TimerUsar = time;
			return true;
		}
		return false;
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
		return this.m_estads.userSkills[Skill.SKILL_Herreria] / this.m_clase.modHerreria();
	}
	
	/**
	 * Devuelve el skill de Carpinteria efectivo, aplicando su modificador de clase.
	 * @return valor del skill efectivo
	 */
	public double skillCarpinteriaEfectivo() {
		return this.m_estads.userSkills[Skill.SKILL_Carpinteria] / this.m_clase.modCarpinteria();
	}
	
	public void agregarHechizo(int slot) {
		this.m_spells.agregarHechizo(slot);
	}
	
}