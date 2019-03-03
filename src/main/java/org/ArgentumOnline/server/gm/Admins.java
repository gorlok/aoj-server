package org.ArgentumOnline.server.gm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserFaction.FactionArmors;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.SpawnListResponse;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admins {
	private static Logger log = LogManager.getLogger();
	
    private List<String> gods = new ArrayList<>();
    private List<String> demigods = new ArrayList<>();
    private List<String> advisers = new ArrayList<>();
    
    private List<String> invalidNames = new ArrayList<>();
    
    private List<GmRequest> helpRequests = new ArrayList<>();
    private List<String> bannedIPs = new ArrayList<>();

    private short [] m_spawnList;
    private String [] m_spawnListNames;
    
    GameServer server;

    public Admins(GameServer server) {
    	this.server = server;
	}
    
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}

    public boolean esDios(String name) {
        return this.gods.contains(name.toUpperCase());
    }
    
    public boolean esSemiDios(String name) {
        return this.demigods.contains(name.toUpperCase());
    }
    
    public boolean esConsejero(String name) {
        return this.advisers.contains(name.toUpperCase());
    }
    
    public boolean nombrePermitido(String nombre) {
        return (!this.invalidNames.contains(nombre.toUpperCase()));
    }
    
    public short[] getSpawnList() {
        return this.m_spawnList;
    }
    
    public String[] getSpawnListNames() {
        return this.m_spawnListNames;
    }

    public void loadAdminsSpawnableCreatures() {
    	log.trace("loading list of spawnable creatures");
        try {
            IniFile ini = new IniFile(Constants.DATDIR + File.separator + "Invokar.dat");
            short cant = ini.getShort("INIT", "NumNPCs");
            this.m_spawnList = new short[cant];
            this.m_spawnListNames = new String[cant];
            for (int i = 0; i < cant; i++) {
                this.m_spawnList[i] = ini.getShort("LIST", "NI" + (i+1));
                this.m_spawnListNames[i] = ini.getString("LIST", "NN" + (i+1));
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadInvalidNamesList() {
    	log.trace("loading invalid names list");
        this.invalidNames.clear();
        try {
            BufferedReader f = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(Constants.DATDIR + File.separator + "NombresInvalidos.txt")));
            try {
                String str = f.readLine();
                if (str == null) {
					return;
				}
                str = str.trim().toUpperCase();
                if (!"".equals(str)) {
					this.invalidNames.add(str);
				}
            } finally {
                f.close();
            }
        } catch (java.io.FileNotFoundException e) {
            log.warn("Error abriendo archivo de nombres inválidos");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            log.warn("Error leyendo archivo de nombres inválidos");
            e.printStackTrace();
        }
    }
    
    public void loadAdmins() {
    	log.trace("loading admins");
        try {
            // Limpiar las listas de admins.
            this.gods.clear();
            this.demigods.clear();
            this.advisers.clear();
            
            // Cargar dioses:
            IniFile ini = new IniFile(Constants.DATDIR + java.io.File.separator + "Server.ini");
            short cant = ini.getShort("DIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("DIOSES", "Dios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.gods.add(nombre);
				}
            }
            // Cargar semidioses:
            cant = ini.getShort("SEMIDIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("SEMIDIOSES", "Semidios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.demigods.add(nombre);
				}
            }
            // Cargar consejeros:
            cant = ini.getShort("CONSEJEROS", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("CONSEJEROS", "Consejero"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.advisers.add(nombre);
				}
            }
            
            UserFaction.loadFactionArmors(ini);
			
			log.warn("Admins recargados");			
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public List<GmRequest> getPedidosAyudaGM() {
        return this.helpRequests;
    }
    
	public void clearAllHelpRequestToGm(Player admin) {
		// TODO
		// Comando /BORRAR SOS
		// Comando para borrar todos pedidos /GM pendientes
    	this.getPedidosAyudaGM().clear();
		admin.enviarMensaje("Todos los /GM pendientes han sido eliminados.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getNick(), "/BORRAR SOS");
	}

    public List<String> getBannedIPs() {
        return this.bannedIPs;
    }

	public void doBanIP(Player admin, String s) {
		// Ban x IP
		// Comando /BANIP
		var usuario = server.playerByUserName(s);
		var ip = "";
		if (usuario == null) {
			Log.logGM(admin.getNick(), "hizo /BanIP " + s);
			ip = s;
		} else {
			Log.logGM(admin.getNick(), "hizo /BanIP " + s + " - " + usuario.getIP());
			ip = usuario.getIP();
		}
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(ip)) {
			admin.enviarMensaje("La IP " + ip + " ya se encuentra en la lista de bans.", FontType.FONTTYPE_INFO);
			return;
		}
		bannedIPs.add(ip);
		server.enviarMensajeAAdmins(admin.getNick() + " Baneo la IP " + ip, FontType.FONTTYPE_FIGHT);
		if (usuario != null) {
			server.logBan(usuario.getNick(), admin.getNick(), "Ban por IP desde Nick");
			server.enviarMensajeAAdmins(admin.getNick() + " echo a " + usuario.getNick() + ".", FontType.FONTTYPE_FIGHT);
			server.enviarMensajeAAdmins(admin.getNick() + " Banned a " + usuario.getNick() + ".", FontType.FONTTYPE_FIGHT);
			// Ponemos el flag de ban a 1
			usuario.flags().Ban = true;
			Log.logGM(admin.getNick(), "Echo a " + usuario.getNick());
			Log.logGM(admin.getNick(), "BAN a " + usuario.getNick());
			usuario.doSALIR();
		}
	}

	public void doUnbanIP(Player admin, String s) {
		// Desbanea una IP
		// Comando /UNBANIP
		Log.logGM(admin.getNick(), "/UNBANIP " + s);
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(s)) {
			bannedIPs.remove(s);
			admin.enviarMensaje("La IP " + s + " se ha quitado de la lista de bans.", FontType.FONTTYPE_INFO);
		} else {
			admin.enviarMensaje("La IP " + s + " NO se encuentra en la lista de bans.", FontType.FONTTYPE_INFO);
		}
	}

	public void doPedirAyudaAlGM(Player user, String s) {
		// Comando /GM
		// Pedir ayuda a los GMs.
		var pedidos = getPedidosAyudaGM();
		var pedido = new GmRequest(user.getNick(), s);
		if (!pedidos.contains(pedido)) {
			pedidos.add(pedido);
			user.enviarMensaje("El mensaje ha sido entregado, ahora solo debes esperar que se desocupe algun GM.",
					FontType.FONTTYPE_INFO);
		} else {
			pedidos.remove(pedido);
			pedidos.add(pedido);
			user.enviarMensaje(
					"Ya habias mandado un mensaje, tu mensaje ha sido movido al final de la cola de mensajes. Ten paciencia.",
					FontType.FONTTYPE_INFO);
		}
	}

	public void doGuardarComentario(Player admin, String s) {
		// Comando /REM comentario
		Log.logGM(admin.getNick(), "Hace el comentario: " + s);
		admin.enviarMensaje("Comentario salvado...", FontType.FONTTYPE_INFO);
	}

	public void doMostrarAyuda(Player admin) {
		// Comando /SHOW SOS
		for (var pedido : getPedidosAyudaGM()) {
			// admin.enviar("RSOS" + pedido.usuario + ": " + pedido.msg);
			// admin.enviar(MSG_RSOS, pedido.usuario);
		}
		// admin.enviar(MSG_MSOS);
	}

	public void doFinAyuda(Player admin, String s) {
		// Comando SOSDONE
		// String nombre = s.substring(0, s.indexOf(':'));
		getPedidosAyudaGM().remove(new GmRequest(s, ""));
	}

	public void doIrAUsuario(Player admin, String s) {
		// Comando /IRA
		if (s.length() == 0) {
			return;
		}
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (admin.warpMe(usuario.pos().map, usuario.pos().x, usuario.pos().y, true)) {
			if (!admin.flags().AdminInvisible) {
				usuario.enviarMensaje(admin.getNick() + " se ha trasportado hacia donde te encuentras.", FontType.FONTTYPE_INFO);
			}
			Log.logGM(admin.getNick(), "Hizo un /IRA " + usuario.getNick() + " mapa=" + usuario.pos().map + " x=" + usuario.pos().x
					+ " y=" + usuario.pos().y);
		}
	}

	public void doHacerInvisible(Player admin) {
		// Comando /INVISIBLE
		doAdminInvisible(admin);
		Log.logGM(admin.getNick(), "Hizo un /INVISIBLE");
	}

	private void doAdminInvisible(Player admin) {
		if (!admin.flags().AdminInvisible) {
			admin.flags().AdminInvisible = true;
			admin.flags().Invisible = true;
			admin.flags().OldBody = admin.infoChar().m_cuerpo;
			admin.flags().OldHead = admin.infoChar().m_cabeza;
			admin.infoChar().m_cuerpo = 0;
			admin.infoChar().m_cabeza = 0;
		} else {
			admin.flags().AdminInvisible = false;
			admin.flags().Invisible = false;
			admin.infoChar().m_cuerpo = admin.flags().OldBody;
			admin.infoChar().m_cabeza = admin.flags().OldHead;
		}
		admin.sendCharacterChange();
	}

	public void sendSpawnCreatureList(Player admin) {
		// Crear criatura
		// Comando /CC
		List<String> params = new LinkedList<String>();
		params.add("" + getSpawnList().length);
		for (String name : getSpawnListNames()) {
			params.add(name);
		}
		admin.sendPacket(new SpawnListResponse(Arrays.toString(params.toArray())));
	}

	public void doSpawnCreature(Player admin, short index) {
		// Spawn una criatura !!!!!
		// SPA
		short[] spawnList = getSpawnList();
		if (index > 0 && index <= spawnList.length) {
			Npc npc = Npc.spawnNpc(spawnList[index - 1], admin.pos(), true, false);
			Log.logGM(admin.getNick(), "Sumoneo al Npc " + npc.toString());
		}
	}
	
	public void doArmaduraImperial1(Player admin, short armadura) {
		// Comando /AI1
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_1);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_1, armadura);
		}
	}

	public void doArmaduraImperial2(Player admin, short armadura) {
		// Comando /AI2
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_2);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_2, armadura);
		}
	}

	public void doArmaduraImperial3(Player admin, short armadura) {
		// Comando /AI3
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_3);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_3, armadura);
		}
	}

	public void doArmaduraImperial4(Player admin, short armadura) {
		// Comando /AI4
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL, armadura);
		}
	}

	public void doArmaduraImperial5(Player admin, short armadura) {
		// Comando /AI5
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS, armadura);
		}
	}

	public void doArmaduraCaos1(Player admin, short armadura) {
		// Comando /AC1
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_1);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_1, armadura);
		}
	}

	public void doArmaduraCaos2(Player admin, short armadura) {
		// Comando /AC2
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_2);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_2, armadura);
		}
	}

	public void doArmaduraCaos3(Player admin, short armadura) {
		// Comando /AC3
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_3);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_3, armadura);
		}
	}

	public void doArmaduraCaos4(Player admin, short armadura) {
		// Comando /AC4
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS, armadura);
		}
	}

	public void doArmaduraCaos5(Player admin, short armadura) {
		// Comando /AC5
		if (armadura < 0) {
			UserFaction.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS_ENANOS);
		} else {
			UserFaction.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS_ENANOS, armadura);
		}
	}

	public void doUsuariosEnMapa(Player admin) {
		// Comando /ONLINEMAP
		// Devuelve la lista de usuarios en el mapa.
		Map mapa = server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		var msg = new StringBuilder();
		int cant = 0;
		for (String usuario : mapa.getUsuarios()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append(usuario);
		}
		admin.enviarMensaje("Usuarios en el mapa: " + msg, FontType.FONTTYPE_INFO);
	}

	public void doUsuariosTrabajando(Player admin) {
		// Comando /TRABAJANDO
		// Devuelve la lista de usuarios trabajando.
		var msg = new StringBuilder();
		int cant = 0;
		for (String usuario : server.getUsuariosTrabajando()) {
			if (cant > 0) {
				msg.append(",");
			}
			cant++;
			msg.append(usuario);
		}
		admin.enviarMensaje("Usuarios trabajando: " + msg, FontType.FONTTYPE_INFO);
	}

	public void doPanelGM(Player admin) {
		// Comando /PANELGM
		// enviar(MSG_ABPANEL);
	}

	public void doListaUsuarios(Player admin) {
		// Comando LISTUSU
		var msg = new StringBuilder();
		for (String usuario : server.getUsuariosConectados()) {
			msg.append(usuario);
			msg.append(",");
		}
		if (msg.length() > 0 && msg.charAt(msg.length() - 1) == ',') {
			msg.deleteCharAt(msg.length() - 1);
		}
		// enviar(MSG_LISTUSU, msg.toString());
	}
    
	public void doPASSDAY(Player admin) {
		// Comando /PASSDAY
		Log.logGM(admin.getNick(), "/PASSDAY");
		server.getGuildMngr().dayElapsed();
	}

	public void doBackup(Player admin) {
		// Comando /DOBACKUP
		// Hacer un backup del mundo.
		server.doBackup();
	}

	public void doGrabar(Player admin) {
		// Comando /GRABAR
		// Guardar todos los usuarios conectados.
		server.guardarUsuarios();
	}

	public void doApagar(Player admin) {
		// Comando /APAGAR
		log.info("SERVIDOR APAGADO POR " + admin.getNick());
		Log.logGM(admin.getNick(), "APAGO EL SERVIDOR");
		server.shutdown();
	}

	public void doLluvia() {
		// Comando /LLUVIA
		if (server.isRaining()) {
			server.detenerLluvia();
		} else {
			server.iniciarLluvia();
		}
	}

	public void doSystemMsg(Player admin, String msg) {
		// Mensaje del sistema
		// Comando /SMSG
		msg = msg.trim();
		Log.logGM(admin.getNick(), "Envió el mensaje del sistema: " + msg);
		// server.enviarATodos(MSG_SYSMSG, s);
	}

	public void doNick2IP(Player admin, String userName) {
		// Comando /NICK2IP
		Player usuario = server.playerByUserName(userName);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		admin.enviarMensaje("El ip de " + userName + " es " + usuario.getIP(), FontType.FONTTYPE_INFO);
	}

	public void doCrearTeleport(Player admin, short dest_mapa, byte dest_x, byte dest_y) {
		// Comando /CT mapa_dest x_dest y_dest
		// Crear Teleport
		Map mapa = server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		// Se creara el Teleport una celda arriba de la posicion del GM.
		byte x = admin.pos().x;
		byte y = (byte) (admin.pos().y - 1);
		if (mapa.hasObject(x, y)) {
			admin.enviarMensaje("Lo siento, no hay lugar para crear un teleport arriba del usuario. Prueba en otro lugar.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (mapa.isTeleport(x, y)) {
			admin.enviarMensaje("Lo siento, ya hay un teleport arriba del usuario. Prueba en otro lugar.", FontType.FONTTYPE_WARNING);
			return;
		}
		Map mapaDest = server.getMap(dest_mapa);
		if (mapaDest == null || !Pos.isValid(dest_x, dest_y)) {
			admin.enviarMensaje("Ups! Debes indicar coordenadas válidas.", FontType.FONTTYPE_WARNING);
			return;
		}
		mapa.createTeleport(x, y, dest_mapa, dest_x, dest_y);
		Log.logGM(admin.getNick(), "Creó un teleport: " + admin.pos() + " que apunta a: " + dest_mapa + " " + dest_x + " " + dest_y);
		admin.enviarMensaje("¡Teleport creado!", FontType.FONTTYPE_INFO);
	}

	public void doDestruirTeleport(Player admin) {
		// Comando /DT
		// Destruir un teleport, toma el ultimo clic
		if (admin.flags().TargetMap == 0 || admin.flags().TargetX == 0 || admin.flags().TargetY == 0) {
			admin.enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		short m = admin.flags().TargetMap;
		Map mapa = server.getMap(m);
		if (mapa == null) {
			admin.enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		byte x = admin.flags().TargetX;
		byte y = admin.flags().TargetY;
		if (!mapa.isTeleport(x, y)) {
			admin.enviarMensaje("¡Debes hacer clic sobre el Teleport que deseas destruir!", FontType.FONTTYPE_WARNING);
			return;
		}
		Log.logGM(admin.getNick(), "Destruyó un teleport, con /DT mapa=" + m + " x=" + x + " y=" + y);
		mapa.destroyTeleport(x, y);
	}

	public void doMataNpc(Player admin) {
		// Quitar Npc
		// Comando /MATA indiceNpc
		Npc npc = server.npcById(admin.flags().TargetNpc);
		if (npc == null) {
			admin.enviarMensaje("Debés hacer clic sobre un Npc y luego escribir /MATA. PERO MUCHO CUIDADO!", FontType.FONTTYPE_INFO);
			return;
		}
		npc.quitarNPC();
		Log.logGM(admin.getNick(), "/MATA " + npc);
	}

	public void doCrearCriatura(Player admin, short indiceNpc) {
		// Crear criatura, toma directamente el indice
		// Comando /ACC indiceNpc
		if (server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, false);
		} else {
			admin.enviarMensaje("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}

	public void doCrearCriaturaRespawn(Player admin, short indiceNpc) {
		// Crear criatura con respawn, toma directamente el indice
		// Comando /RACC indiceNpc
		if (server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, true);
		} else {
			admin.enviarMensaje("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}


	public void doMascotas(Player admin, String s) {
		// Comando /MASCOTAS
		// Informa cantidad, nombre y ubicación de las mascotas.
		Player usuario;
		if (!"".equals(s)) {
			usuario = server.playerByUserName(s);
		} else {
			usuario = admin;
		}
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		var pets = usuario.getUserPets().getPets();
		
		admin.enviarMensaje(usuario.getNick() + " tiene " + pets.size() + " mascotas.", FontType.FONTTYPE_SERVER);
		pets.forEach(pet -> {
			admin.enviarMensaje(" mascota " + pet.getName() + " esta en " + pet.pos() + " tiempo=" + pet.getContadores().TiempoExistencia,
					FontType.FONTTYPE_SERVER);
		});
	}

	public void doUptime(Player admin) {
		// Comando /UPTIME
		admin.enviarMensaje("Uptime: " + server.calculateUptime(), FontType.FONTTYPE_INFO);
	}
	

	public void doModMapInfo(Player admin, String accion, int valor) {
		// Comando /MODMAPINFO
		if ("".equals(accion)) {
			admin.enviarMensaje("Parámetros inválidos!", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/MODMAPINFO " + accion + " " + valor);
		Map mapa = server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		if (accion.equalsIgnoreCase("PK")) {
			if (valor == 0 || valor == 1) {
				mapa.m_pk = (valor == 1);
				admin.enviarMensaje("PK cambiado.", FontType.FONTTYPE_INFO);
			}
			admin.enviarMensaje("Mapa " + admin.pos().map + " PK: " + (mapa.m_pk ? "SI" : "NO"), FontType.FONTTYPE_INFO);
		} else if (accion.equalsIgnoreCase("BACKUP")) {
			if (valor == 0 || valor == 1) {
				mapa.m_backup = (valor == 1);
				admin.enviarMensaje("BACKUP cambiado.", FontType.FONTTYPE_INFO);
			}
			admin.enviarMensaje("Mapa " + admin.pos().map + " Backup: " + (mapa.m_backup ? "SI" : "NO"), FontType.FONTTYPE_INFO);
		}
	}

	public void doModificarCaracter(Player admin, String nick, String accion, int valor) {
		// MODIFICA CARACTER
		// Comando /MOD
		Log.logGM(admin.getNick(), "/MOD " + nick + " " + accion + " " + valor);
		if ("".equals(nick)) {
			admin.enviarMensaje("Parámetros inválidos!", FontType.FONTTYPE_INFO);
			return;
		}
		Player usuario = server.playerByUserName(nick);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (accion.equalsIgnoreCase("ORO")) {
			if (valor < 95001) {
				usuario.stats().setGold(valor);
				usuario.sendUpdateUserStats();
			} else {
				admin.enviarMensaje(
						"No esta permitido utilizar valores mayores a 95000. Su comando ha quedado en los logs del juego.",
						FontType.FONTTYPE_INFO);
			}
		} else if (accion.equalsIgnoreCase("EXP")) {
			if (valor < 1000000) {
				usuario.stats().Exp += valor;
				usuario.checkUserLevel();
				usuario.sendUpdateUserStats();
			} else {
				admin.enviarMensaje(
						"No esta permitido utilizar valores mayores a 999999. Su comando ha quedado en los logs del juego.",
						FontType.FONTTYPE_INFO);
			}
		} else if (accion.equalsIgnoreCase("BODY")) {
			usuario.infoChar().m_cuerpo = (short) valor;
			usuario.sendCharacterChange();
		} else if (accion.equalsIgnoreCase("HEAD")) {
			usuario.infoChar().m_cabeza = (short) valor;
			usuario.sendCharacterChange();
		} else if (accion.equalsIgnoreCase("CRI")) {
			usuario.userFaction().CriminalesMatados = valor;
		} else if (accion.equalsIgnoreCase("CIU")) {
			usuario.userFaction().CiudadanosMatados = valor;
		} else if (accion.equalsIgnoreCase("LEVEL")) {
			usuario.stats().ELV = valor;
		} else {
			admin.enviarMensaje("Comando no permitido o inválido.", FontType.FONTTYPE_INFO);
		}
	}

	public void doCrearItem(Player admin, short objid) {
		// Crear Item
		// Comando /CI
		Log.logGM(admin.getNick(), "/CI " + objid + " pos=" + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		if (mapa != null) {
			if (mapa.hasObject(admin.pos().x, admin.pos().y)) {
				return;
			}
			if (mapa.isTeleport(admin.pos().x, admin.pos().y)) {
				return;
			}
			if (findObj(objid) == null) {
				return;
			}
			mapa.agregarObjeto(objid, 1, admin.pos().x, admin.pos().y);
		}
	}

	public void doGuardaMapa(Player admin) {
		// Guardar el mapa actual.
		// Comando /GUARDAMAPA
		Log.logGM(admin.getNick(), "/GUARDAMAPA " + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.saveMapData();
			admin.enviarMensaje("Mapa guardado.", FontType.FONTTYPE_INFO);
		}
	}

	public void doDestObj(Player admin) {
		// Destruir el objeto de la posición actual.
		// Comando /DEST
		Log.logGM(admin.getNick(), "/DEST " + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.quitarObjeto(admin.pos().x, admin.pos().y);
		}
	}

	public void doBloqPos(Player admin) {
		// Bloquear la posición actual.
		// Comando /BLOQ
		Log.logGM(admin.getNick(), "/BLOQ " + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		if (mapa != null) {
			if (mapa.isBlocked(admin.pos().x, admin.pos().y)) {
				mapa.desbloquearTerreno(admin.pos().x, admin.pos().y);
				admin.enviarMensaje("Posicion desbloqueada.", FontType.FONTTYPE_INFO);
			} else {
				mapa.bloquearTerreno(admin.pos().x, admin.pos().y);
				admin.enviarMensaje("Posicion bloqueada.", FontType.FONTTYPE_INFO);
			}
		}
	}

	public void doMassKill(Player admin) {
		// Quita todos los NPCs del area.
		// Comando /MASSKILL
		Log.logGM(admin.getNick(), "/MASSKILL " + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.quitarNpcsArea(admin.pos().x, admin.pos().y);
		}
	}

	public void doTrigger(Player admin, byte t) {
		// Consulta o cambia el trigger de la posición actual.
		// Comando /TRIGGER
		Log.logGM(admin.getNick(), "/TRIGGER " + t + " " + admin.pos());
		Map mapa = server.getMap(admin.pos().map);
		mapa.setTrigger(admin.pos().x, admin.pos().y, t);
		admin.enviarMensaje("Trigger " + mapa.getTrigger(admin.pos().x, admin.pos().y) + 
				" en " + admin.pos(), FontType.FONTTYPE_INFO);
	}

	public void doMassDest(Player admin) {
		// Quita todos los objetos del area
		// Comando /MASSDEST
		Map mapa = server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		mapa.objectMassDestroy(admin.pos().x, admin.pos().y);
		Log.logGM(admin.getNick(), "/MASSDEST ");
	}

	public void doEcharTodosPjs(Player admin) {
		// Comando /ECHARTODOSPJS
		// Comando para echar a todos los pjs conectados no privilegiados.
		server.echarPjsNoPrivilegiados();
		admin.enviarMensaje("Los PJs no privilegiados fueron echados.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getNick(), "/ECHARTODOSPJS");
	}

	public void doShowInt(Player admin) {
		// Comando /SHOW INT
		// Comando para abrir la ventana de config de intervalos en el server.
		admin.enviarMensaje("Comando deshabilitado o sin efecto en AOJ.", FontType.FONTTYPE_INFO);
	}

	public void doIP2Nick(Player admin, String s) {
		// Comando /IP2NICK
		List<String> usuarios = server.getUsuariosConIP(s);
		if (usuarios.isEmpty()) {
			admin.enviarMensaje("No hay usuarios con dicha ip", FontType.FONTTYPE_INFO);
		} else {
			admin.enviarMensaje("Nicks: " + String.join(",", usuarios), FontType.FONTTYPE_INFO);
		}
	}

	public void doResetInv(Player admin) {
		// Resetea el inventario
		// Comando /RESETINV
		if (admin.flags().TargetNpc == 0) {
			return;
		}
		Npc npc = server.npcById(admin.flags().TargetNpc);
		npc.getInv().clear();
		admin.enviarMensaje("El inventario del npc " + npc.getName() + " ha sido vaciado.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getNick(), "/RESETINV " + npc.toString());
	}

	public void doLimpiarMundo(Player admin) {
		// Comando /LIMPIAR
		server.limpiarMundo(admin);
	}

	public void doRMSG(Player admin, String s) {
		// Mensaje del servidor
		// Comando /RMSG
		Log.logGM(admin.getNick(), "Mensaje Broadcast: " + s);
		if (!s.equals("")) {
			// server.enviarATodos(MSG_TALK, s + FontType.FONTTYPE_TALK);
		}
	}

	public void doSUM(Player admin, String s) {
		// Comando /SUM usuario
		if (s.length() == 0) {
			return;
		}
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("El usuario esta offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "Hizo /SUM " + s);
		if (usuario.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, true)) {
			admin.enviarMensaje(usuario.getNick() + " ha sido trasportado.", FontType.FONTTYPE_INFO);
			usuario.enviarMensaje("Has sido trasportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "/SUM " + usuario.getNick() + 
					" Map:" + admin.pos().map + " X:" + admin.pos().x + " Y:" + admin.pos().y);
		}
	}

	public void doBan(Player admin, String nombre, String motivo) {
		// Comando /BAN
		Player usuario = server.playerByUserName(nombre);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.flags().Privilegios > admin.flags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/BAN " + nombre + " por: " + motivo);
		server.logBan(usuario.getNick(), admin.getNick(), motivo);
		server.enviarMensajeAAdmins(admin.getNick() + " echo a " + usuario.getNick() + ".", FontType.FONTTYPE_FIGHT);
		server.enviarMensajeAAdmins(admin.getNick() + " Banned a " + usuario.getNick() + ".", FontType.FONTTYPE_FIGHT);
		// Ponemos el flag de ban a 1
		usuario.flags().Ban = true;
		if (usuario.isGM()) {
			admin.flags().Ban = true;
			admin.doSALIR();
			server.enviarMensajeAAdmins(admin.getNick() + " banned from this server por bannear un Administrador.",
					FontType.FONTTYPE_FIGHT);
		}
		Log.logGM(admin.getNick(), "Echo a " + usuario.getNick());
		Log.logGM(admin.getNick(), "BAN a " + usuario.getNick());
		usuario.doSALIR();
	}

	public void doUnban(Player admin, String s) {
		// Comando /UNBAN
		Log.logGM(admin.getNick(), "/UNBAN " + s);
		server.unBan(s);
		Log.logGM(admin.getNick(), "Hizo /UNBAN a " + s);
		admin.enviarMensaje(s + " unbanned.", FontType.FONTTYPE_INFO);
	}

	public void doEchar(Player admin, String s) {
		// Echar usuario
		// Comando /ECHAR
		Log.logGM(admin.getNick(), "quizo /ECHAR a " + s);
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.flags().Privilegios > admin.flags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		// server.enviarATodos(MSG_TALK, getNick() + " echo a "
		// + usuario.getNick() + ".", FontType.FONTTYPE_INFO.toString());
		// usuario.doSALIR();
		Log.logGM(admin.getNick(), "Echó a " + usuario.getNick());
	}

	public void doEncarcelar(Player admin, String s) {
		// Comando /CARCEL
		Log.logGM(admin.getNick(), "quizo /CARCEL " + s);
		StringTokenizer st = new StringTokenizer(s, " ");
		short minutos;
		String nombre;
		try {
			minutos = Short.parseShort(st.nextToken());
			nombre = st.nextToken();
		} catch (Exception e) {
			admin.enviarMensaje("Error en el comando. Formato: /CARCEL minutos usuario", FontType.FONTTYPE_WARNING);
			return;
		}
		Player usuario = server.playerByUserName(nombre);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.flags().Privilegios > admin.flags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.counters().Pena > 0) {
			admin.enviarMensaje("El usuario ya esta en la carcel. Le quedan " + admin.counters().Pena + " minutos.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (minutos > 30) {
			admin.enviarMensaje("No puedes encarcelar por mas de 30 minutos!", FontType.FONTTYPE_INFO);
			return;
		}
		usuario.sendToJail(minutos, admin.getNick());
	}

	public void doPerdonar(Player admin, String s) {
		// Comando /PERDON usuario
		// Perdonar a un usuario. Volverlo cuidadano.
		Log.logGM(admin.getNick(), "quizo /PERDON " + s);
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.esNewbie()) {
			if (usuario.reputation().esIntachable()) {
				admin.enviarMensaje("No hay que perdonarle a " + usuario.getNick(), FontType.FONTTYPE_INFO);
				return;
			}
			usuario.volverCiudadano();
			admin.enviarMensaje(usuario.getNick() + " ha sido perdonado.", FontType.FONTTYPE_INFO);
			usuario.enviarMensaje("Los dioses te han perdonado por esta vez.", FontType.FONTTYPE_INFO);
		} else {
			Log.logGM(admin.getNick(), "Intento perdonar un personaje de nivel avanzado.");
			admin.enviarMensaje("Solo se permite perdonar newbies.", FontType.FONTTYPE_INFO);
		}
	}

	public void doCondenar(Player admin, String s) {
		// Comando /CONDEN usuario
		// Condenar a un usuario. Volverlo criminal.
		Log.logGM(admin.getNick(), "quizo /CONDEN " + s);
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.reputation().esCriminal()) {
			admin.enviarMensaje(usuario.getNick() + " ya es un criminal condenado.", FontType.FONTTYPE_INFO);
			return;
		}
		usuario.volverCriminal();
		admin.enviarMensaje(usuario.getNick() + " ha sido condenado.", FontType.FONTTYPE_INFO);
		usuario.enviarMensaje("Los dioses te han condenado por tus acciones.", FontType.FONTTYPE_INFO);
	}

	public void doRevivir(Player admin, String s) {
		// Comando /REVIVIR
		Log.logGM(admin.getNick(), "quizo /REVIVIR " + s);
		Player usuario;
		if (!s.equalsIgnoreCase("YO") && s.length() > 0) {
			usuario = server.playerByUserName(s);
			if (usuario == null) {
				admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
		} else {
			usuario = admin;
		}
		if (usuario.isAlive()) {
			admin.enviarMensaje(usuario.getNick() + " no esta muerto!", FontType.FONTTYPE_INFO);
		} else {
			usuario.revive();
			usuario.enviarMensaje(admin.getNick() + " te ha resucitado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "Resucitó a " + usuario.getNick());
		}
	}

	public void doONLINEGM(Player admin) {
		// Comando /ONLINEGM
		if (!admin.isGM()) {
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
			admin.enviarMensaje("GM online: " + msg.toString(), FontType.FONTTYPE_INFO);
		} else {
			admin.enviarMensaje("No hay GMs online.", FontType.FONTTYPE_INFO);
		}
	}

	public void doInvUser(Player admin, String s) {
		// Inventario del usuario.
		// Comando /INV
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/INV " + s);

		admin.enviarMensaje(usuario.getNick(), FontType.FONTTYPE_INFO);
		admin.enviarMensaje(" Tiene " + usuario.userInv().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= usuario.userInv().size(); i++) {
			if (usuario.userInv().getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(usuario.userInv().getObjeto(i).objid);
				admin.enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.userInv().getObjeto(i).cant,
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void doBovUser(Player admin, String s) {
		// Boveda del usuario
		// Comando /BOV
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/BOV " + s);

		admin.enviarMensaje(usuario.getNick(), FontType.FONTTYPE_INFO);
		admin.enviarMensaje(" Tiene " + usuario.getBankInventory().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= usuario.getBankInventory().size(); i++) {
			if (usuario.getBankInventory().getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(usuario.getBankInventory().getObjeto(i).objid);
				admin.enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.getBankInventory().getObjeto(i).cant,
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void doSkillsUser(Player admin, String s) {
		// Skills del usuario
		// Comando /SKILLS
		Player usuario = server.playerByUserName(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/SKILLS " + s);

		admin.enviarMensaje(usuario.getNick(), FontType.FONTTYPE_INFO);
		for (Skill skill : Skill.values()) {
			admin.enviarMensaje(" " + skill + " = " + usuario.skills().get(skill), FontType.FONTTYPE_INFO);
		}
	}

	public void doMensajeALosGM(Player admin, String s) {
		// Mensaje para los GMs
		if (!admin.isGM()) {
			return;
		}
		if (s.length() > 0) {
			Log.logGM(admin.getNick(), "Mensaje para GMs: " + s);
			server.enviarMensajeALosGMs(admin.getNick() + "> " + s);
		}
	}

	public void doEnviarHora(Player admin) {
		// Comando /HORA
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
		java.util.Date ahora = new java.util.Date();
		String fecha = df.format(ahora);
		df = new java.text.SimpleDateFormat("HH:mm");
		String hora = df.format(ahora);
		admin.enviarMensaje("Hora: " + hora + " Fecha: " + fecha, FontType.FONTTYPE_INFO);
	}

	public void doDonde(Player admin, String s) {
		// Comando /DONDE
		// ¿Donde esta fulano?
		s = s.trim();
		Player usuario;
		if (s.length() == 0) {
			usuario = admin;
		} else {
			usuario = server.playerByUserName(s);
			if (usuario == null) {
				admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
			Log.logGM(admin.getNick(), "consultó /DONDE " + usuario.getNick());
		}
		admin.enviarMensaje("Ubicacion de " + usuario.getNick() + ": " + usuario.pos().map + ", " + usuario.pos().x + ", "
				+ usuario.pos().y + ".", FontType.FONTTYPE_INFO);
	}

	public void doEnviarCantidadHostiles(Player admin, short m) {
		if (m < 1) {
			admin.enviarMensaje("Has ingresado un número de mapa inválido.", FontType.FONTTYPE_INFO);
			return;
		}
		Map mapa = server.getMap(m);
		if (mapa != null) {
			Log.logGM(admin.getNick(), "Consultó el número de enemigos en el mapa, /NENE " + m);
			// enviar(MSG_NENE, mapa.getCantHostiles());
		} else {
			admin.enviarMensaje("El mapa no existe.", FontType.FONTTYPE_INFO);
		}
	}

	public void doTeleploc(Player admin) {
		// Comando /TELEPLOC
		if (admin.warpMe(admin.flags().TargetMap, admin.flags().TargetX, admin.flags().TargetY, true)) {
			Log.logGM(admin.getNick(), "hizo un /TELEPLOC a x=" + admin.flags().TargetX + 
					" y=" + admin.flags().TargetY + " mapa=" + admin.flags().TargetMap);
		}
	}

	public void doTeleportUsuario(Player admin, String nombre, short m, byte x, byte y) {
		// Comando /TELEP
		// Teleportar
		if (m < 1) {
			admin.enviarMensaje("Parámetros incorrectos: /TELEP usuario mapa x y", FontType.FONTTYPE_WARNING);
			return;
		}
		Map mapa = server.getMap(m);
		if (mapa == null) {
			return;
		}
		if (nombre.length() == 0) {
			return;
		}

		Player usuario = admin;
		if (!nombre.equalsIgnoreCase("YO")) {
			if (admin.flags().Privilegios < 2) {
				return;
			}
			usuario = server.playerByUserName(nombre);
		}
		if (!Pos.isValid(x, y)) {
			return;
		}
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.warpMe(m, x, y, true)) {
			usuario.enviarMensaje(usuario.getNick() + " transportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "Transportó con un /TELEP a " + usuario.getNick() + 
					" hacia el mapa=" + m + " x=" + x + " y=" + y);
		}
	}
	
}
