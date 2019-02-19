package org.ArgentumOnline.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    

}
