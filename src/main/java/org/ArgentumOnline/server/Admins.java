package org.ArgentumOnline.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.Factions.FactionArmors;
import org.ArgentumOnline.server.gm.GmRequest;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admins {
	private static Logger log = LogManager.getLogger();
	
    private List<String> m_dioses = new ArrayList<>();
    private List<String> m_semidioses = new ArrayList<>();
    private List<String> m_consejeros = new ArrayList<>();
    
    private List<String> m_nombresInvalidos = new ArrayList<>();
    
    private List<GmRequest> m_pedidosAyudaGM = new ArrayList<>();
    private List<String> m_bannedIPs = new ArrayList<>();

    private short [] m_spawnList;
    private String [] m_spawnListNames;
    
    AojServer server;

    public Admins(AojServer server) {
    	this.server = server;
	}
    
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}

    public boolean esDios(String name) {
        return this.m_dioses.contains(name.toUpperCase());
    }
    
    public boolean esSemiDios(String name) {
        return this.m_semidioses.contains(name.toUpperCase());
    }
    
    public boolean esConsejero(String name) {
        return this.m_consejeros.contains(name.toUpperCase());
    }
    
    public boolean nombrePermitido(String nombre) {
        return (!this.m_nombresInvalidos.contains(nombre.toUpperCase()));
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
        this.m_nombresInvalidos.clear();
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
					this.m_nombresInvalidos.add(str);
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
            this.m_dioses.clear();
            this.m_semidioses.clear();
            this.m_consejeros.clear();
            
            // Cargar dioses:
            IniFile ini = new IniFile(Constants.DATDIR + java.io.File.separator + "Server.ini");
            short cant = ini.getShort("DIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("DIOSES", "Dios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_dioses.add(nombre);
				}
            }
            // Cargar semidioses:
            cant = ini.getShort("SEMIDIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("SEMIDIOSES", "Semidios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_semidioses.add(nombre);
				}
            }
            // Cargar consejeros:
            cant = ini.getShort("CONSEJEROS", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("CONSEJEROS", "Consejero"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_consejeros.add(nombre);
				}
            }
            
            Factions.loadFactionArmors(ini);
			
			log.warn("Admins recargados");			
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public List<GmRequest> getPedidosAyudaGM() {
        return this.m_pedidosAyudaGM;
    }
    
	public void clearAllHelpRequestToGm(Client admin) {
		// TODO
		// Comando /BORRAR SOS
		// Comando para borrar todos pedidos /GM pendientes
    	this.getPedidosAyudaGM().clear();
		admin.enviarMensaje("Todos los /GM pendientes han sido eliminados.", FontType.INFO);
		Log.logGM(admin.getNick(), "/BORRAR SOS");
	}

    public List<String> getBannedIPs() {
        return this.m_bannedIPs;
    }

	public void doBanIP(Client admin, String s) {
		// Ban x IP
		// Comando /BANIP
		var usuario = server.getUsuario(s);
		var ip = "";
		if (usuario == null) {
			Log.logGM(admin.getNick(), "hizo /BanIP " + s);
			ip = s;
		} else {
			Log.logGM(admin.getNick(), "hizo /BanIP " + s + " - " + usuario.m_ip);
			ip = usuario.m_ip;
		}
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(ip)) {
			admin.enviarMensaje("La IP " + ip + " ya se encuentra en la lista de bans.", FontType.INFO);
			return;
		}
		bannedIPs.add(ip);
		server.enviarMensajeAAdmins(admin.getNick() + " Baneo la IP " + ip, FontType.FIGHT);
		if (usuario != null) {
			server.logBan(usuario.m_nick, admin.getNick(), "Ban por IP desde Nick");
			server.enviarMensajeAAdmins(admin.getNick() + " echo a " + usuario.m_nick + ".", FontType.FIGHT);
			server.enviarMensajeAAdmins(admin.getNick() + " Banned a " + usuario.m_nick + ".", FontType.FIGHT);
			// Ponemos el flag de ban a 1
			usuario.getFlags().Ban = true;
			Log.logGM(admin.getNick(), "Echo a " + usuario.m_nick);
			Log.logGM(admin.getNick(), "BAN a " + usuario.m_nick);
			usuario.doSALIR();
		}
	}

	public void doUnbanIP(Client admin, String s) {
		// Desbanea una IP
		// Comando /UNBANIP
		Log.logGM(admin.getNick(), "/UNBANIP " + s);
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(s)) {
			bannedIPs.remove(s);
			admin.enviarMensaje("La IP " + s + " se ha quitado de la lista de bans.", FontType.INFO);
		} else {
			admin.enviarMensaje("La IP " + s + " NO se encuentra en la lista de bans.", FontType.INFO);
		}
	}

	public void doPedirAyudaAlGM(Client user, String s) {
		// Comando /GM
		// Pedir ayuda a los GMs.
		var pedidos = getPedidosAyudaGM();
		var pedido = new GmRequest(user.getNick(), s);
		if (!pedidos.contains(pedido)) {
			pedidos.add(pedido);
			user.enviarMensaje("El mensaje ha sido entregado, ahora solo debes esperar que se desocupe algun GM.",
					FontType.INFO);
		} else {
			pedidos.remove(pedido);
			pedidos.add(pedido);
			user.enviarMensaje(
					"Ya habias mandado un mensaje, tu mensaje ha sido movido al final de la cola de mensajes. Ten paciencia.",
					FontType.INFO);
		}
	}

	public void doGuardarComentario(Client admin, String s) {
		// Comando /REM comentario
		Log.logGM(admin.getNick(), "Hace el comentario: " + s);
		admin.enviarMensaje("Comentario salvado...", FontType.INFO);
	}

	public void doMostrarAyuda(Client admin) {
		// Comando /SHOW SOS
		for (var pedido : getPedidosAyudaGM()) {
			// admin.enviar("RSOS" + pedido.usuario + ": " + pedido.msg);
			// admin.enviar(MSG_RSOS, pedido.usuario);
		}
		// admin.enviar(MSG_MSOS);
	}

	public void doFinAyuda(Client admin, String s) {
		// Comando SOSDONE
		// String nombre = s.substring(0, s.indexOf(':'));
		getPedidosAyudaGM().remove(new GmRequest(s, ""));
	}

	public void doIrAUsuario(Client admin, String s) {
		// Comando /IRA
		if (s.length() == 0) {
			return;
		}
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (admin.warpUser(usuario.m_pos.map, usuario.m_pos.x, usuario.m_pos.y, true)) {
			if (!admin.getFlags().AdminInvisible) {
				usuario.enviarMensaje(admin.getNick() + " se ha trasportado hacia donde te encuentras.", FontType.INFO);
			}
			Log.logGM(admin.getNick(), "Hizo un /IRA " + usuario.m_nick + " mapa=" + usuario.m_pos.map + " x=" + usuario.m_pos.x
					+ " y=" + usuario.m_pos.y);
		}
	}

	public void doHacerInvisible(Client admin) {
		// Comando /INVISIBLE
		doAdminInvisible(admin);
		Log.logGM(admin.getNick(), "Hizo un /INVISIBLE");
	}

	private void doAdminInvisible(Client admin) {
		if (!admin.getFlags().AdminInvisible) {
			admin.getFlags().AdminInvisible = true;
			admin.getFlags().Invisible = true;
			admin.getFlags().OldBody = admin.getInfoChar().m_cuerpo;
			admin.getFlags().OldHead = admin.getInfoChar().m_cabeza;
			admin.getInfoChar().m_cuerpo = 0;
			admin.getInfoChar().m_cabeza = 0;
		} else {
			admin.getFlags().AdminInvisible = false;
			admin.getFlags().Invisible = false;
			admin.getInfoChar().m_cuerpo = admin.getFlags().OldBody;
			admin.getInfoChar().m_cabeza = admin.getFlags().OldHead;
		}
		admin.enviarCP();
	}

	public void sendSpawnCreatureList(Client admin) {
		// Crear criatura
		// Comando /CC
		List<String> params = new LinkedList<String>();
		params.add("" + getSpawnList().length);
		for (String name : getSpawnListNames()) {
			params.add(name);
		}
		// enviar(MSG_SPL, params.toArray());
	}

	public void doSpawnCreature(Client admin, short index) {
		// Spawn una criatura !!!!!
		// SPA
		short[] spawnList = getSpawnList();
		if (index > 0 && index <= spawnList.length) {
			Npc npc = Npc.spawnNpc(spawnList[index - 1], admin.getPos(), true, false);
			Log.logGM(admin.getNick(), "Sumoneo al Npc " + npc.toString());
		}
	}
	
	public void doArmaduraImperial1(Client admin, short armadura) {
		// Comando /AI1
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_1);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_1, armadura);
		}
	}

	public void doArmaduraImperial2(Client admin, short armadura) {
		// Comando /AI2
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_2);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_2, armadura);
		}
	}

	public void doArmaduraImperial3(Client admin, short armadura) {
		// Comando /AI3
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_3);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_3, armadura);
		}
	}

	public void doArmaduraImperial4(Client admin, short armadura) {
		// Comando /AI4
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL, armadura);
		}
	}

	public void doArmaduraImperial5(Client admin, short armadura) {
		// Comando /AI5
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS, armadura);
		}
	}

	public void doArmaduraCaos1(Client admin, short armadura) {
		// Comando /AC1
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_1);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_1, armadura);
		}
	}

	public void doArmaduraCaos2(Client admin, short armadura) {
		// Comando /AC2
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_2);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_2, armadura);
		}
	}

	public void doArmaduraCaos3(Client admin, short armadura) {
		// Comando /AC3
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.ARMADURA_CAOS_3);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_3, armadura);
		}
	}

	public void doArmaduraCaos4(Client admin, short armadura) {
		// Comando /AC4
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS, armadura);
		}
	}

	public void doArmaduraCaos5(Client admin, short armadura) {
		// Comando /AC5
		if (armadura < 0) {
			Factions.sendFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS_ENANOS);
		} else {
			Factions.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS_ENANOS, armadura);
		}
	}

	public void doUsuariosEnMapa(Client admin) {
		// Comando /ONLINEMAP
		// Devuelve la lista de usuarios en el mapa.
		Map mapa = server.getMapa(admin.getPos().map);
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
		admin.enviarMensaje("Usuarios en el mapa: " + msg, FontType.INFO);
	}

	public void doUsuariosTrabajando(Client admin) {
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
		admin.enviarMensaje("Usuarios trabajando: " + msg, FontType.INFO);
	}

	public void doPanelGM(Client admin) {
		// Comando /PANELGM
		// enviar(MSG_ABPANEL);
	}

	public void doListaUsuarios(Client admin) {
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
    
	public void doPASSDAY(Client admin) {
		// Comando /PASSDAY
		Log.logGM(admin.getNick(), "/PASSDAY");
		server.getGuildMngr().dayElapsed();
	}

	public void doBackup(Client admin) {
		// Comando /DOBACKUP
		// Hacer un backup del mundo.
		server.doBackup();
	}

	public void doGrabar(Client admin) {
		// Comando /GRABAR
		// Guardar todos los usuarios conectados.
		server.guardarUsuarios();
	}

	public void doApagar(Client admin) {
		// Comando /APAGAR
		log.info("SERVIDOR APAGADO POR " + admin.getNick());
		Log.logGM(admin.getNick(), "APAGO EL SERVIDOR");
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

	public void doSystemMsg(Client admin, String msg) {
		// Mensaje del sistema
		// Comando /SMSG
		msg = msg.trim();
		Log.logGM(admin.getNick(), "Envió el mensaje del sistema: " + msg);
		// server.enviarATodos(MSG_SYSMSG, s);
	}

	public void doNick2IP(Client admin, String userName) {
		// Comando /NICK2IP
		Client usuario = server.getUsuario(userName);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		admin.enviarMensaje("El ip de " + userName + " es " + usuario.m_ip, FontType.INFO);
	}

	public void doCrearTeleport(Client admin, short dest_mapa, short dest_x, short dest_y) {
		// Comando /CT mapa_dest x_dest y_dest
		// Crear Teleport
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa == null) {
			return;
		}
		// Se creara el Teleport una celda arriba de la posicion del GM.
		short x = admin.getPos().x;
		short y = (short) (admin.getPos().y - 1);
		if (mapa.hayObjeto(x, y)) {
			admin.enviarMensaje("Lo siento, no hay lugar para crear un teleport arriba del usuario. Prueba en otro lugar.",
					FontType.WARNING);
			return;
		}
		if (mapa.hayTeleport(x, y)) {
			admin.enviarMensaje("Lo siento, ya hay un teleport arriba del usuario. Prueba en otro lugar.", FontType.WARNING);
			return;
		}
		Map mapaDest = server.getMapa(dest_mapa);
		if (mapaDest == null || !Pos.isValid(dest_x, dest_y)) {
			admin.enviarMensaje("Ups! Debes indicar coordenadas válidas.", FontType.WARNING);
			return;
		}
		mapa.crearTeleport(x, y, dest_mapa, dest_x, dest_y);
		Log.logGM(admin.getNick(), "Creó un teleport: " + admin.getPos() + " que apunta a: " + dest_mapa + " " + dest_x + " " + dest_y);
		admin.enviarMensaje("¡Teleport creado!", FontType.INFO);
	}

	public void doDestruirTeleport(Client admin) {
		// Comando /DT
		// Destruir un teleport, toma el ultimo clic
		if (admin.getFlags().TargetMap == 0 || admin.getFlags().TargetX == 0 || admin.getFlags().TargetY == 0) {
			admin.enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.WARNING);
			return;
		}
		short m = admin.getFlags().TargetMap;
		Map mapa = server.getMapa(m);
		if (mapa == null) {
			admin.enviarMensaje("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.WARNING);
			return;
		}
		short x = admin.getFlags().TargetX;
		short y = admin.getFlags().TargetY;
		if (!mapa.hayTeleport(x, y)) {
			admin.enviarMensaje("¡Debes hacer clic sobre el Teleport que deseas destruir!", FontType.WARNING);
			return;
		}
		Log.logGM(admin.getNick(), "Destruyó un teleport, con /DT mapa=" + m + " x=" + x + " y=" + y);
		mapa.destruirTeleport(x, y);
	}

	public void doMataNpc(Client admin) {
		// Quitar Npc
		// Comando /MATA indiceNpc
		Npc npc = server.getNpcById(admin.getFlags().TargetNpc);
		if (npc == null) {
			admin.enviarMensaje("Debés hacer clic sobre un Npc y luego escribir /MATA. PERO MUCHO CUIDADO!", FontType.INFO);
			return;
		}
		npc.quitarNPC();
		Log.logGM(admin.getNick(), "/MATA " + npc);
	}

	public void doCrearCriatura(Client admin, short indiceNpc) {
		// Crear criatura, toma directamente el indice
		// Comando /ACC indiceNpc
		if (server.getNpcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.getPos(), true, false);
		} else {
			admin.enviarMensaje("Indice de Npc invalido.", FontType.INFO);
		}
	}

	public void doCrearCriaturaRespawn(Client admin, short indiceNpc) {
		// Crear criatura con respawn, toma directamente el indice
		// Comando /RACC indiceNpc
		if (server.getNpcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.getPos(), true, true);
		} else {
			admin.enviarMensaje("Indice de Npc invalido.", FontType.INFO);
		}
	}


	public void doMascotas(Client admin, String s) {
		// Comando /MASCOTAS
		// Informa cantidad, nombre y ubicación de las mascotas.
		Client usuario;
		if (!"".equals(s)) {
			usuario = server.getUsuario(s);
		} else {
			usuario = admin;
		}
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		admin.enviarMensaje(usuario.getNick() + " tiene " + usuario.m_cantMascotas + " mascotas.", FontType.DEBUG);
		for (int i = 0; i < usuario.m_mascotas.length; i++) {
			if (usuario.m_mascotas[i] != null) {
				admin.enviarMensaje(
						" mascota " + usuario.m_mascotas[i].getName() + "[" + (i + 1) + "] esta en "
						+ usuario.m_mascotas[i].getPos() + " tiempo=" + usuario.m_mascotas[i].getContadores().TiempoExistencia,
						FontType.DEBUG);
			}
		}
	}

	public void doUptime(Client admin) {
		// Comando /UPTIME
		admin.enviarMensaje("Uptime: " + server.calculateUptime(), FontType.INFO);
	}
	

	public void doModMapInfo(Client admin, String accion, int valor) {
		// Comando /MODMAPINFO
		if ("".equals(accion)) {
			admin.enviarMensaje("Parámetros inválidos!", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/MODMAPINFO " + accion + " " + valor);
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa == null) {
			return;
		}
		if (accion.equalsIgnoreCase("PK")) {
			if (valor == 0 || valor == 1) {
				mapa.m_pk = (valor == 1);
				admin.enviarMensaje("PK cambiado.", FontType.INFO);
			}
			admin.enviarMensaje("Mapa " + admin.getPos().map + " PK: " + (mapa.m_pk ? "SI" : "NO"), FontType.INFO);
		} else if (accion.equalsIgnoreCase("BACKUP")) {
			if (valor == 0 || valor == 1) {
				mapa.m_backup = (valor == 1);
				admin.enviarMensaje("BACKUP cambiado.", FontType.INFO);
			}
			admin.enviarMensaje("Mapa " + admin.getPos().map + " Backup: " + (mapa.m_backup ? "SI" : "NO"), FontType.INFO);
		}
	}

	public void doModificarCaracter(Client admin, String nick, String accion, int valor) {
		// MODIFICA CARACTER
		// Comando /MOD
		Log.logGM(admin.getNick(), "/MOD " + nick + " " + accion + " " + valor);
		if ("".equals(nick)) {
			admin.enviarMensaje("Parámetros inválidos!", FontType.INFO);
			return;
		}
		Client usuario = server.getUsuario(nick);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (accion.equalsIgnoreCase("ORO")) {
			if (valor < 95001) {
				usuario.m_estads.setGold(valor);
				usuario.refreshStatus(1);
			} else {
				admin.enviarMensaje(
						"No esta permitido utilizar valores mayores a 95000. Su comando ha quedado en los logs del juego.",
						FontType.INFO);
			}
		} else if (accion.equalsIgnoreCase("EXP")) {
			if (valor < 1000000) {
				usuario.m_estads.Exp += valor;
				usuario.checkUserLevel();
				usuario.refreshStatus(5);
			} else {
				admin.enviarMensaje(
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
			admin.enviarMensaje("Comando no permitido o inválido.", FontType.INFO);
		}
	}

	public void doCrearItem(Client admin, short objid) {
		// Crear Item
		// Comando /CI
		Log.logGM(admin.getNick(), "/CI " + objid + " pos=" + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa != null) {
			if (mapa.hayObjeto(admin.getPos().x, admin.getPos().y)) {
				return;
			}
			if (mapa.hayTeleport(admin.getPos().x, admin.getPos().y)) {
				return;
			}
			if (findObj(objid) == null) {
				return;
			}
			mapa.agregarObjeto(objid, 1, admin.getPos().x, admin.getPos().y);
		}
	}

	public void doGuardaMapa(Client admin) {
		// Guardar el mapa actual.
		// Comando /GUARDAMAPA
		Log.logGM(admin.getNick(), "/GUARDAMAPA " + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa != null) {
			mapa.saveMapData();
			admin.enviarMensaje("Mapa guardado.", FontType.INFO);
		}
	}

	public void doDestObj(Client admin) {
		// Destruir el objeto de la posición actual.
		// Comando /DEST
		Log.logGM(admin.getNick(), "/DEST " + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa != null) {
			mapa.quitarObjeto(admin.getPos().x, admin.getPos().y);
		}
	}

	public void doBloqPos(Client admin) {
		// Bloquear la posición actual.
		// Comando /BLOQ
		Log.logGM(admin.getNick(), "/BLOQ " + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa != null) {
			if (mapa.estaBloqueado(admin.getPos().x, admin.getPos().y)) {
				mapa.desbloquearTerreno(admin.getPos().x, admin.getPos().y);
				admin.enviarMensaje("Posicion desbloqueada.", FontType.INFO);
			} else {
				mapa.bloquearTerreno(admin.getPos().x, admin.getPos().y);
				admin.enviarMensaje("Posicion bloqueada.", FontType.INFO);
			}
		}
	}

	public void doMassKill(Client admin) {
		// Quita todos los NPCs del area.
		// Comando /MASSKILL
		Log.logGM(admin.getNick(), "/MASSKILL " + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa != null) {
			mapa.quitarNpcsArea(admin.getPos().x, admin.getPos().y);
		}
	}

	public void doTrigger(Client admin, byte t) {
		// Consulta o cambia el trigger de la posición actual.
		// Comando /TRIGGER
		Log.logGM(admin.getNick(), "/TRIGGER " + t + " " + admin.getPos());
		Map mapa = server.getMapa(admin.getPos().map);
		mapa.setTrigger(admin.getPos().x, admin.getPos().y, t);
		admin.enviarMensaje("Trigger " + mapa.getTrigger(admin.getPos().x, admin.getPos().y) + 
				" en " + admin.getPos(), FontType.INFO);
	}

	public void doMassDest(Client admin) {
		// Quita todos los objetos del area
		// Comando /MASSDEST
		Map mapa = server.getMapa(admin.getPos().map);
		if (mapa == null) {
			return;
		}
		mapa.objectMassDestroy(admin.getPos().x, admin.getPos().y);
		Log.logGM(admin.getNick(), "/MASSDEST ");
	}

	public void doEcharTodosPjs(Client admin) {
		// Comando /ECHARTODOSPJS
		// Comando para echar a todos los pjs conectados no privilegiados.
		server.echarPjsNoPrivilegiados();
		admin.enviarMensaje("Los PJs no privilegiados fueron echados.", FontType.INFO);
		Log.logGM(admin.getNick(), "/ECHARTODOSPJS");
	}

	public void doShowInt(Client admin) {
		// Comando /SHOW INT
		// Comando para abrir la ventana de config de intervalos en el server.
		admin.enviarMensaje("Comando deshabilitado o sin efecto en AOJ.", FontType.INFO);
	}

	public void doIP2Nick(Client admin, String s) {
		// Comando /IP2NICK
		List<String> usuarios = server.getUsuariosConIP(s);
		if (usuarios.isEmpty()) {
			admin.enviarMensaje("No hay usuarios con dicha ip", FontType.INFO);
		} else {
			admin.enviarMensaje("Nicks: " + String.join(",", usuarios), FontType.INFO);
		}
	}

	public void doResetInv(Client admin) {
		// Resetea el inventario
		// Comando /RESETINV
		if (admin.getFlags().TargetNpc == 0) {
			return;
		}
		Npc npc = server.getNpcById(admin.getFlags().TargetNpc);
		npc.getInv().clear();
		admin.enviarMensaje("El inventario del npc " + npc.getName() + " ha sido vaciado.", FontType.INFO);
		Log.logGM(admin.getNick(), "/RESETINV " + npc.toString());
	}

	public void doLimpiarMundo(Client admin) {
		// Comando /LIMPIAR
		server.limpiarMundo(admin);
	}

	public void doRMSG(Client admin, String s) {
		// Mensaje del servidor
		// Comando /RMSG
		Log.logGM(admin.getNick(), "Mensaje Broadcast: " + s);
		if (!s.equals("")) {
			// server.enviarATodos(MSG_TALK, s + FontType.TALK);
		}
	}

	public void doSUM(Client admin, String s) {
		// Comando /SUM usuario
		if (s.length() == 0) {
			return;
		}
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("El usuario esta offline.", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "Hizo /SUM " + s);
		if (usuario.warpUser(admin.getPos().map, admin.getPos().x, admin.getPos().y, true)) {
			admin.enviarMensaje(usuario.m_nick + " ha sido trasportado.", FontType.INFO);
			usuario.enviarMensaje("Has sido trasportado.", FontType.INFO);
			Log.logGM(admin.getNick(), "/SUM " + usuario.m_nick + 
					" Map:" + admin.getPos().map + " X:" + admin.getPos().x + " Y:" + admin.getPos().y);
		}
	}

	public void doBan(Client admin, String nombre, String motivo) {
		// Comando /BAN
		Client usuario = server.getUsuario(nombre);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > admin.getFlags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/BAN " + nombre + " por: " + motivo);
		server.logBan(usuario.m_nick, admin.getNick(), motivo);
		server.enviarMensajeAAdmins(admin.getNick() + " echo a " + usuario.m_nick + ".", FontType.FIGHT);
		server.enviarMensajeAAdmins(admin.getNick() + " Banned a " + usuario.m_nick + ".", FontType.FIGHT);
		// Ponemos el flag de ban a 1
		usuario.m_flags.Ban = true;
		if (usuario.esGM()) {
			admin.getFlags().Ban = true;
			admin.doSALIR();
			server.enviarMensajeAAdmins(admin.getNick() + " banned from this server por bannear un Administrador.",
					FontType.FIGHT);
		}
		Log.logGM(admin.getNick(), "Echo a " + usuario.m_nick);
		Log.logGM(admin.getNick(), "BAN a " + usuario.m_nick);
		usuario.doSALIR();
	}

	public void doUnban(Client admin, String s) {
		// Comando /UNBAN
		Log.logGM(admin.getNick(), "/UNBAN " + s);
		server.unBan(s);
		Log.logGM(admin.getNick(), "Hizo /UNBAN a " + s);
		admin.enviarMensaje(s + " unbanned.", FontType.INFO);
	}

	public void doEchar(Client admin, String s) {
		// Echar usuario
		// Comando /ECHAR
		Log.logGM(admin.getNick(), "quizo /ECHAR a " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > admin.getFlags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		// server.enviarATodos(MSG_TALK, m_nick + " echo a "
		// + usuario.m_nick + ".", FontType.INFO.toString());
		// usuario.doSALIR();
		Log.logGM(admin.getNick(), "Echó a " + usuario.m_nick);
	}

	public void doEncarcelar(Client admin, String s) {
		// Comando /CARCEL
		Log.logGM(admin.getNick(), "quizo /CARCEL " + s);
		StringTokenizer st = new StringTokenizer(s, " ");
		short minutos;
		String nombre;
		try {
			minutos = Short.parseShort(st.nextToken());
			nombre = st.nextToken();
		} catch (Exception e) {
			admin.enviarMensaje("Error en el comando. Formato: /CARCEL minutos usuario", FontType.WARNING);
			return;
		}
		Client usuario = server.getUsuario(nombre);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_flags.Privilegios > admin.getFlags().Privilegios) {
			admin.enviarMensaje("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.INFO);
			return;
		}
		if (usuario.m_counters.Pena > 0) {
			admin.enviarMensaje("El usuario ya esta en la carcel. Le quedan " + admin.getCounters().Pena + " minutos.",
					FontType.WARNING);
			return;
		}
		if (minutos > 30) {
			admin.enviarMensaje("No puedes encarcelar por mas de 30 minutos!", FontType.INFO);
			return;
		}
		usuario.encarcelar(minutos, admin.getNick());
	}

	public void doPerdonar(Client admin, String s) {
		// Comando /PERDON usuario
		// Perdonar a un usuario. Volverlo cuidadano.
		Log.logGM(admin.getNick(), "quizo /PERDON " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.esNewbie()) {
			if (usuario.m_reputacion.esIntachable()) {
				admin.enviarMensaje("No hay que perdonarle a " + usuario.getNick(), FontType.INFO);
				return;
			}
			usuario.volverCiudadano();
			admin.enviarMensaje(usuario.getNick() + " ha sido perdonado.", FontType.INFO);
			usuario.enviarMensaje("Los dioses te han perdonado por esta vez.", FontType.INFO);
		} else {
			Log.logGM(admin.getNick(), "Intento perdonar un personaje de nivel avanzado.");
			admin.enviarMensaje("Solo se permite perdonar newbies.", FontType.INFO);
		}
	}

	public void doCondenar(Client admin, String s) {
		// Comando /CONDEN usuario
		// Condenar a un usuario. Volverlo criminal.
		Log.logGM(admin.getNick(), "quizo /CONDEN " + s);
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.m_reputacion.esCriminal()) {
			admin.enviarMensaje(usuario.getNick() + " ya es un criminal condenado.", FontType.INFO);
			return;
		}
		usuario.volverCriminal();
		admin.enviarMensaje(usuario.getNick() + " ha sido condenado.", FontType.INFO);
		usuario.enviarMensaje("Los dioses te han condenado por tus acciones.", FontType.INFO);
	}

	public void doRevivir(Client admin, String s) {
		// Comando /REVIVIR
		Log.logGM(admin.getNick(), "quizo /REVIVIR " + s);
		Client usuario;
		if (!s.equalsIgnoreCase("YO") && s.length() > 0) {
			usuario = server.getUsuario(s);
			if (usuario == null) {
				admin.enviarMensaje("Usuario offline.", FontType.INFO);
				return;
			}
		} else {
			usuario = admin;
		}
		if (usuario.isAlive()) {
			admin.enviarMensaje(usuario.m_nick + " no esta muerto!", FontType.INFO);
		} else {
			usuario.revivirUsuario();
			usuario.enviarMensaje(admin.getNick() + " te ha resucitado.", FontType.INFO);
			Log.logGM(admin.getNick(), "Resucitó a " + usuario.m_nick);
		}
	}

	public void doONLINEGM(Client admin) {
		// Comando /ONLINEGM
		if (!admin.esGM()) {
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
			admin.enviarMensaje("GM online: " + msg.toString(), FontType.INFO);
		} else {
			admin.enviarMensaje("No hay GMs online.", FontType.INFO);
		}
	}

	public void doInvUser(Client admin, String s) {
		// Inventario del usuario.
		// Comando /INV
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/INV " + s);

		admin.enviarMensaje(usuario.m_nick, FontType.INFO);
		admin.enviarMensaje(" Tiene " + usuario.m_inv.getCantObjs() + " objetos.", FontType.INFO);
		for (int i = 1; i <= usuario.m_inv.size(); i++) {
			if (usuario.m_inv.getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(usuario.m_inv.getObjeto(i).objid);
				admin.enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.m_inv.getObjeto(i).cant,
						FontType.INFO);
			}
		}
	}

	public void doBovUser(Client admin, String s) {
		// Boveda del usuario
		// Comando /BOV
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/BOV " + s);

		admin.enviarMensaje(usuario.m_nick, FontType.INFO);
		admin.enviarMensaje(" Tiene " + usuario.m_bancoInv.getCantObjs() + " objetos.", FontType.INFO);
		for (int i = 1; i <= usuario.m_bancoInv.size(); i++) {
			if (usuario.m_bancoInv.getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(usuario.m_bancoInv.getObjeto(i).objid);
				admin.enviarMensaje(" Objeto " + i + " " + info.Nombre + " Cantidad:" + usuario.m_bancoInv.getObjeto(i).cant,
						FontType.INFO);
			}
		}
	}

	public void doSkillsUser(Client admin, String s) {
		// Skills del usuario
		// Comando /SKILLS
		Client usuario = server.getUsuario(s);
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/SKILLS " + s);

		admin.enviarMensaje(usuario.m_nick, FontType.INFO);
		for (int i = 1; i <= Skill.MAX_SKILLS; i++) {
			admin.enviarMensaje(" " + Skill.skillsNames[i] + " = " + usuario.m_estads.userSkills[i], FontType.INFO);
		}
	}

	public void doMensajeALosGM(Client admin, String s) {
		// Mensaje para los GMs
		if (!admin.esGM()) {
			return;
		}
		if (s.length() > 0) {
			Log.logGM(admin.getNick(), "Mensaje para GMs: " + s);
			server.enviarMensajeALosGMs(admin.getNick() + "> " + s);
		}
	}

	public void doEnviarHora(Client admin) {
		// Comando /HORA
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
		java.util.Date ahora = new java.util.Date();
		String fecha = df.format(ahora);
		df = new java.text.SimpleDateFormat("HH:mm");
		String hora = df.format(ahora);
		admin.enviarMensaje("Hora: " + hora + " Fecha: " + fecha, FontType.INFO);
	}

	public void doDonde(Client admin, String s) {
		// Comando /DONDE
		// ¿Donde esta fulano?
		s = s.trim();
		Client usuario;
		if (s.length() == 0) {
			usuario = admin;
		} else {
			usuario = server.getUsuario(s);
			if (usuario == null) {
				admin.enviarMensaje("Usuario offline.", FontType.INFO);
				return;
			}
			Log.logGM(admin.getNick(), "consultó /DONDE " + usuario.m_nick);
		}
		admin.enviarMensaje("Ubicacion de " + usuario.m_nick + ": " + usuario.m_pos.map + ", " + usuario.m_pos.x + ", "
				+ usuario.m_pos.y + ".", FontType.INFO);
	}

	public void doEnviarCantidadHostiles(Client admin, short m) {
		if (m < 1) {
			admin.enviarMensaje("Has ingresado un número de mapa inválido.", FontType.INFO);
			return;
		}
		Map mapa = server.getMapa(m);
		if (mapa != null) {
			Log.logGM(admin.getNick(), "Consultó el número de enemigos en el mapa, /NENE " + m);
			// enviar(MSG_NENE, mapa.getCantHostiles());
		} else {
			admin.enviarMensaje("El mapa no existe.", FontType.INFO);
		}
	}

	public void doTeleploc(Client admin) {
		// Comando /TELEPLOC
		if (admin.warpUser(admin.getFlags().TargetMap, admin.getFlags().TargetX, admin.getFlags().TargetY, true)) {
			Log.logGM(admin.getNick(), "hizo un /TELEPLOC a x=" + admin.getFlags().TargetX + 
					" y=" + admin.getFlags().TargetY + " mapa=" + admin.getFlags().TargetMap);
		}
	}

	public void doTeleportUsuario(Client admin, String nombre, short m, short x, short y) {
		// Comando /TELEP
		// Teleportar
		if (m < 1) {
			admin.enviarMensaje("Parámetros incorrectos: /TELEP usuario mapa x y", FontType.WARNING);
			return;
		}
		Map mapa = server.getMapa(m);
		if (mapa == null) {
			return;
		}
		if (nombre.length() == 0) {
			return;
		}

		Client usuario = admin;
		if (!nombre.equalsIgnoreCase("YO")) {
			if (admin.getFlags().Privilegios < 2) {
				return;
			}
			usuario = server.getUsuario(nombre);
		}
		if (!Pos.isValid(x, y)) {
			return;
		}
		if (usuario == null) {
			admin.enviarMensaje("Usuario offline.", FontType.INFO);
			return;
		}
		if (usuario.warpUser(m, x, y, true)) {
			usuario.enviarMensaje(usuario.m_nick + " transportado.", FontType.INFO);
			Log.logGM(admin.getNick(), "Transportó con un /TELEP a " + usuario.m_nick + 
					" hacia el mapa=" + m + " x=" + x + " y=" + y);
		}
	}

	
}
