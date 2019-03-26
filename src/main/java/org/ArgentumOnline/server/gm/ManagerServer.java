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
package org.ArgentumOnline.server.gm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.Tile.Trigger;
import org.ArgentumOnline.server.npc.WorkWatcher;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.ConsoleMsgResponse;
import org.ArgentumOnline.server.protocol.PlayMidiResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.protocol.ShowGMPanelFormResponse;
import org.ArgentumOnline.server.protocol.ShowMessageBoxResponse;
import org.ArgentumOnline.server.protocol.ShowSOSFormResponse;
import org.ArgentumOnline.server.protocol.SpawnListResponse;
import org.ArgentumOnline.server.protocol.UserNameListResponse;
import org.ArgentumOnline.server.user.Player;
import org.ArgentumOnline.server.user.UserAttributes.Attribute;
import org.ArgentumOnline.server.user.UserFaction;
import org.ArgentumOnline.server.user.UserFaction.FactionArmors;
import org.ArgentumOnline.server.user.UserStorage;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManagerServer {
	private static Logger log = LogManager.getLogger();

    private List<String> gods = new ArrayList<>();
    private List<String> demigods = new ArrayList<>();
    private List<String> counsellor = new ArrayList<>();

    private List<String> invalidNames = new ArrayList<>();

    /** User names than asked for help */
    private List<String> helpRequests = new ArrayList<>();
    
    private List<String> bannedIPs = new ArrayList<>();

    private short [] spawnList;
    private String [] spawnListNames;

    GameServer server;

    public ManagerServer(GameServer server) {
    	this.server = server;
	}

	private ObjectInfo findObj(int oid) {
		return this.server.getObjectInfoStorage().getInfoObjeto(oid);
	}

    public boolean isGod(String name) {
        return this.gods.contains(name.toUpperCase());
    }

    public boolean isDemiGod(String name) {
        return this.demigods.contains(name.toUpperCase());
    }

    public boolean isCounsellor(String name) {
        return this.counsellor.contains(name.toUpperCase());
    }

    public boolean isValidUserName(String nombre) {
        return (!this.invalidNames.contains(nombre.toUpperCase()));
    }

    public short[] getSpawnList() {
        return this.spawnList;
    }

    public String[] getSpawnListNames() {
        return this.spawnListNames;
    }

    public void loadAdminsSpawnableCreatures() {
    	log.trace("loading list of spawnable creatures");
        try {
            IniFile ini = new IniFile(Constants.DAT_DIR + File.separator + "Invokar.dat");
            short cant = ini.getShort("INIT", "NumNPCs");
            this.spawnList = new short[cant];
            this.spawnListNames = new String[cant];
            for (int i = 0; i < cant; i++) {
                this.spawnList[i] = ini.getShort("LIST", "NI" + (i+1));
                this.spawnListNames[i] = ini.getString("LIST", "NN" + (i+1));
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
                    new FileInputStream(Constants.DAT_DIR + File.separator + "NombresInvalidos.txt")));
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
            this.counsellor.clear();

            // Cargar dioses:
            IniFile ini = new IniFile(Constants.DAT_DIR + java.io.File.separator + "Server.ini");
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
					this.counsellor.add(nombre);
				}
            }

            UserFaction.loadFactionArmors(ini);
            
            // FIXME add saving this... and move this
            server.setServerRestrictedToGMs(ini.getInt("INIT", "ServerSoloGMs") == 1);
            server.setCreateUserEnabled(ini.getInt("INIT", "PuedeCrearPersonajes") == 1);

			log.warn("Admins loaded");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> helpRequests() {
        return this.helpRequests;
    }

	public void clearAllHelpRequestToGm(Player admin) {
		// Comando /BORRAR SOS
		// Comando para borrar todos pedidos /GM pendientes
		if (!admin.isGM()) {
			return;
		}
    	this.helpRequests().clear();
		admin.sendMessage("Todos los /GM pendientes han sido eliminados.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getNick(), "/BORRAR SOS");
	}

    public List<String> getBannedIPs() {
        return this.bannedIPs;
    }
    
	public void banIPUser(Player admin, String userName, String reason) {
		// Comando /BANIP
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		String bannedIP = user.getIP();
		banUser(admin, userName, reason);
		banIP(admin, bannedIP, reason);
	}

	public void banIP(Player admin, String bannedIP, String reason) {
		// Comando /BANIP
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(bannedIP)) {
			admin.sendMessage("La IP " + bannedIP + " ya se encuentra en la lista de bans.", FontType.FONTTYPE_INFO);
			return;
		}
		bannedIPs.add(bannedIP);
		saveBannedIPList();
		sendMessageToAdmins(admin, admin.getNick() + " Baneo la IP " + bannedIP, FontType.FONTTYPE_SERVER);
		
        // Find every player with that ip and ban him!
		server.players().stream().forEach(p -> {
			if (p.getIP() == bannedIP) {
				banUser(admin, p.getNick(), "Banned IP " + bannedIP + " por: " + reason);
			}
		});
	}

	public void unbanIP(Player admin, String bannedIP) {
		// Comando /UNBANIP
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Log.logGM(admin.getNick(), "/UNBANIP " + bannedIP);
		var bannedIPs = getBannedIPs();
		if (bannedIPs.contains(bannedIP)) {
			bannedIPs.remove(bannedIP);
			saveBannedIPList();
			admin.sendMessage("La IP " + bannedIP + " se ha quitado de la lista de bans.", FontType.FONTTYPE_INFO);
			sendMessageToAdmins(admin, admin.getNick() + " ha quitado la IP " + bannedIP + " de la lista de bans.", 
					FontType.FONTTYPE_SERVER);
		} else {
			admin.sendMessage("La IP " + bannedIP + " NO se encuentra en la lista de bans.", FontType.FONTTYPE_INFO);
		}
	}
	
	public void bannedIPList(Player admin) {
		// Command /BANIPLIST
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "/BANIPLIST");
	    
		if (getBannedIPs().isEmpty()) {
			admin.sendMessage("No hay banned IPs.", FontType.FONTTYPE_INFO);			
		} else {
			admin.sendMessage("Banned IPs: " + String.join(", ", getBannedIPs()), FontType.FONTTYPE_INFO);
		}
	}

	public void bannedIPReload(Player admin) {
		// Command /BANIPRELOAD
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "/BANIPRELOAD");

		loadBannedIPList();
	}
	
	public void loadBannedIPList() {
		final String fileName = Constants.DAT_DIR + File.separator + "BanIps.dat";
		this.bannedIPs.clear();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			stream.forEach( line -> this.bannedIPs.add(line) );
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void saveBannedIPList() {
		final String fileName = Constants.DAT_DIR + File.separator + "BanIps.dat";
		try {
			Files.write(Paths.get(fileName), String.join("\n", this.bannedIPs).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveGmComment(Player admin, String comment) {
		// Comando /REM comentario
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "Hace el comentario: " + comment);
		admin.sendMessage("Comentario salvado...", FontType.FONTTYPE_INFO);
	}

	public void askForHelpToGM(Player user) {
		// Comando /GM
		// Pedir ayuda a los GMs.
		var requests = helpRequests();
		if (!requests.contains(user.getNick())) {
			requests.add(user.getNick());
			user.sendMessage("El mensaje ha sido entregado, ahora solo debes esperar que se desocupe algun GM.",
					FontType.FONTTYPE_INFO);
		} else {
			requests.remove(user.getNick());
			requests.add(user.getNick());
			user.sendMessage(
					"Ya habias mandado un mensaje, tu mensaje ha sido movido al final de la cola de mensajes. Ten paciencia.",
					FontType.FONTTYPE_INFO);
		}
	}

	public void sendHelpRequests(Player admin) {
		// Comando /SHOW SOS
		if (!admin.isGM()) {
			return;
		}
		String sosList = String.join("" + Constants.NULL_CHAR, helpRequests);
		admin.sendPacket(new ShowSOSFormResponse(sosList));
	}

	public void removeHelpRequest(Player admin, String userName) {
		// Comando SOSDONE
		if (!admin.isGM()) {
			return;
		}
		helpRequests().remove(userName);
	}

	public void goToChar(Player admin, String userName) {
		// Comando /IRA
		if ( !admin.isGM() ) {
			return;
		}
		if (userName.length() == 0) {
			return;
		}
		Player usuario = this.server.playerByUserName(userName);
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (admin.warpMe(usuario.pos().map, usuario.pos().x, usuario.pos().y, true)) {
			if (!admin.flags().AdminInvisible) {
				usuario.sendMessage(admin.getNick() + " se ha trasportado hacia donde te encuentras.", FontType.FONTTYPE_INFO);
			}
			Log.logGM(admin.getNick(), "Hizo un /IRA " + usuario.getNick() + " mapa=" + usuario.pos().map + " x=" + usuario.pos().x
					+ " y=" + usuario.pos().y);
		}
	}

	public void turnInvisible(Player admin) {
		// Comando /INVISIBLE
		if ( !admin.flags().isGM() ) {
			return;
		}
		Log.logGM(admin.getNick(), "Hizo un /INVISIBLE");
		
		if (!admin.flags().AdminInvisible) {
			admin.flags().AdminInvisible = true;
			admin.flags().Invisible = true;
			admin.flags().OldBody = admin.infoChar().body;
			admin.flags().OldHead = admin.infoChar().head;
			admin.infoChar().body = 0;
			admin.infoChar().head = 0;
		} else {
			admin.flags().AdminInvisible = false;
			admin.flags().Invisible = false;
			admin.infoChar().body = admin.flags().OldBody;
			admin.infoChar().head = admin.flags().OldHead;
		}
		admin.sendCharacterChange();
	}

	public void sendSpawnCreatureList(Player admin) {
		// Crear criatura
		// Comando /CC
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}
		
		admin.sendPacket(new SpawnListResponse(String.join("\0", getSpawnListNames())));
	}

	public void spawnCreature(Player admin, short index) {
		// Spawn una criatura !!!!!
		// SPA
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}
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

	public void sendUsersOnlineMap(Player admin, int map) {
		// Comando /ONLINEMAP
		// Devuelve la lista de usuarios en el mapa.
		if ( !admin.isGM() ) {
			return;
		}
		
		Map mapa = this.server.getMap(map);
		if (mapa == null) {
			return;
		}
		var userNames = mapa.getPlayers().stream()
				.filter(p -> !p.isGod())
				.map(Player::getNick)
				.collect(Collectors.toList());
		
		admin.sendMessage("Hay " + userNames.size() + 
				" usuarios en el mapa: " + String.join(", ", userNames), 
				FontType.FONTTYPE_INFO);
	}

	public void sendUsersWorking(Player admin) {
		// Comando /TRABAJANDO
		// Devuelve la lista de usuarios trabajando.
		if ( !admin.isGM() ) {
			return;
		}
		
    	// Agrega un (*) al nick del usuario que esté siendo monitoreado por el Centinela
		var users = server.players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && c.isWorking())
			    	.map(p -> p.getNick() + (WorkWatcher.watchingUser(p.getNick()) ? "(*)" : ""))
			    	.collect(Collectors.toList());
		
		admin.sendMessage("Usuarios trabajando: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}
	
	public void sendUsersHiding(Player admin) {
		// Devuelve la lista de usuarios ocultándose.
		if ( !admin.isGM() ) {
			return;
		}
		
		var users = server.players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && c.isHidden()) // FIMXE c.counters().Ocultando > 0
			    	.map(Player::getNick)
			    	.collect(Collectors.toList());
		
		admin.sendMessage("Usuarios ocultándose: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}

	public void sendOnlineRoyalArmy(Player admin) {
		if ( !admin.isGM() ) {
			return;
		}
		
		var users = server.players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.isRoyalArmy())
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
	
		admin.sendMessage("Usuarios de la Armada Real: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}
	
	public void	sendOnlineChaosLegion(Player admin) {
		if ( !admin.isGM() ) {
			return;
		}

		var users = server.players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.isDarkLegion())
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
	
		admin.sendMessage("Usuarios de la Legión Oscura: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}

	public void showGmPanelForm(Player admin) {
		// Comando /PANELGM
		if (!admin.isGM()) {
			return;
		}
		admin.sendPacket(new ShowGMPanelFormResponse());
	}

	public void sendUserNameList(Player admin) {
		// Comando LISTUSU
		if (!admin.isGM()) {
			return;
		}
		String userNamesList = String.join("" + Constants.NULL_CHAR, this.server.getUsuariosConectados());
		admin.sendPacket(new UserNameListResponse(userNamesList));
	}

	public void doPASSDAY(Player admin) {
		// Comando /PASSDAY
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "/PASSDAY");
		this.server.getGuildMngr().dayElapsed();
	}

	public void backupWorld(Player admin) {
		// Comando /DOBACKUP
		// Hacer un backup del mundo.
		if (!admin.isGM()) {
			return;
		}
		this.server.backupWorld();
	}

	public void saveChars(Player admin) {
		// Comando /GRABAR
		// Guardar todos los usuarios conectados.
		if (!admin.isGM()) {
			return;
		}
		this.server.saveUsers();
	}

	public void shutdownServer(Player admin) {
		// Comando /APAGAR
		if (!admin.isGM()) {
			return;
		}
		
        Log.logGM(admin.getNick(), "/APAGAR");
		this.server.sendToAll(new ConsoleMsgResponse(admin.getNick() + " VA A APAGAR EL SERVIDOR!!!", FontType.FONTTYPE_FIGHT.id()));
		
		// wait a few seconds...
		Util.sleep(5 * 1000);
		try {
			List.copyOf(server.players()).forEach(p -> p.quitGame());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	this.server.backupWorld();

		log.info("SERVIDOR APAGADO POR " + admin.getNick());
		Log.logGM(admin.getNick(), "APAGO EL SERVIDOR");
		this.server.shutdown();
	}

	public void toggleRain(Player admin) {
		// Comando /LLUVIA
		if (!admin.isGM()) {
			return;
		}
		if (this.server.isRaining()) {
			this.server.rainStop();
		} else {
			this.server.rainStart();
		}
	}

	public void sendSystemMsg(final Player admin, String msg) {
		// Mensaje del sistema
		// Comando /SMSG
		if (!admin.isGM()) {
			return;
		}
		msg = msg.trim();
		Log.logGM(admin.getNick(), "Envió el mensaje del sistema: " + msg);
		// FIXME
		server.sendToAll(new ShowMessageBoxResponse(msg));
	}

	public void nick2IP(final Player admin, final String userName) {
		// Comando /NICK2IP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		final Player user = this.server.playerByUserName(userName);
		if (user == null) {
			if (!Player.userExists(userName)) {
				admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("El personaje está desconectado.", FontType.FONTTYPE_INFO);
			}
			return;
		}
		Log.logGM(admin.getNick(), "consultó /NICK2IP " + userName);
		admin.sendMessage("La IP de " + userName + " es " + user.getIP(), FontType.FONTTYPE_INFO);

		List<String> userNames = server.players().stream()
			.filter(p ->  p.isLogged() 
					&& p.hasNick() 
					&& !userName.equalsIgnoreCase(p.getNick())
					&& p.getIP().equals(user.getIP()))
			.map( p -> p.getNick())
			.collect(Collectors.toList());
		if (!userNames.isEmpty()) {
			admin.sendMessage("Otros personajes con la misma IP son: " 
					+ String.join(", ", userNames), FontType.FONTTYPE_INFO);
		}
	}

	public void createTeleport(Player admin, short dest_mapa, byte dest_x, byte dest_y) {
		// Comando /CT mapa_dest x_dest y_dest
		// Crear Teleport
		if (!admin.isGM()) {
			return;
		}
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		// Se creara el Teleport una celda arriba de la posicion del GM.
		byte x = admin.pos().x;
		byte y = (byte) (admin.pos().y - 1);
		if (mapa.hasObject(x, y)) {
			admin.sendMessage("Lo siento, no hay lugar para crear un teleport arriba del usuario. Prueba en otro lugar.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		if (mapa.isTeleport(x, y)) {
			admin.sendMessage("Lo siento, ya hay un teleport arriba del usuario. Prueba en otro lugar.", FontType.FONTTYPE_WARNING);
			return;
		}
		Map mapaDest = this.server.getMap(dest_mapa);
		if (mapaDest == null || !Pos.isValid(dest_x, dest_y)) {
			admin.sendMessage("Ups! Debes indicar coordenadas válidas.", FontType.FONTTYPE_WARNING);
			return;
		}
		mapa.createTeleport(x, y, dest_mapa, dest_x, dest_y);
		Log.logGM(admin.getNick(), "Creó un teleport: " + admin.pos() + " que apunta a: " + dest_mapa + " " + dest_x + " " + dest_y);
		admin.sendMessage("¡Teleport creado!", FontType.FONTTYPE_INFO);
	}

	public void destroyTeleport(Player admin) {
		// Comando /DT
		// Destruir un teleport, toma el ultimo clic
		if (!admin.isGM()) {
			return;
		}
		if (admin.flags().TargetObjMap == 0 || admin.flags().TargetObjX == 0 || admin.flags().TargetObjY == 0) {
			admin.sendMessage("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		Map map = this.server.getMap(admin.flags().TargetObjMap);
		if (map == null) {
			admin.sendMessage("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		byte x = admin.flags().TargetObjX;
		byte y = admin.flags().TargetObjY;
		if (!map.isTeleport(x, y)) {
			admin.sendMessage("¡Debes hacer clic sobre el Teleport que deseas destruir!", FontType.FONTTYPE_WARNING);
			return;
		}
		Log.logGM(admin.getNick(), "Destruyó un teleport, con /DT mapa=" + map.getMapNumber() + " x=" + x + " y=" + y);
		map.destroyTeleport(x, y);
	}

	public void killNPCNoRespawn(Player admin) {
		// Quitar Npc
		// Comando /MATA indiceNpc
		if (!admin.isGM() || admin.isDemiGod() || admin.isCounselor()) {
			return;
		}
		Npc npc = this.server.npcById(admin.flags().TargetNpc);
		if (npc == null) {
			admin.sendMessage("Debés hacer clic sobre un Npc y luego escribir /MATA. PERO MUCHO CUIDADO!", FontType.FONTTYPE_INFO);
			return;
		}
		npc.quitarNPC();
		admin.flags().TargetNpc = 0;
		Log.logGM(admin.getNick(), "/MATA " + npc);
	}

	public void doCrearCriatura(Player admin, short indiceNpc) {
		// Crear criatura, toma directamente el indice
		// Comando /ACC indiceNpc
		if (this.server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, false);
		} else {
			admin.sendMessage("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}

	public void doCrearCriaturaRespawn(Player admin, short indiceNpc) {
		// Crear criatura con respawn, toma directamente el indice
		// Comando /RACC indiceNpc
		if (this.server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, true);
		} else {
			admin.sendMessage("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}


	public void doMascotas(Player admin, String s) {
		// Comando /MASCOTAS
		// Informa cantidad, nombre y ubicación de las mascotas.
		Player usuario;
		if (!"".equals(s)) {
			usuario = this.server.playerByUserName(s);
		} else {
			usuario = admin;
		}
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		var pets = usuario.getUserPets().getPets();

		admin.sendMessage(usuario.getNick() + " tiene " + pets.size() + " mascotas.", FontType.FONTTYPE_SERVER);
		pets.forEach(pet -> {
			admin.sendMessage(" mascota " + pet.getName() + " esta en " + pet.pos() + " tiempo=" + pet.counters().TiempoExistencia,
					FontType.FONTTYPE_SERVER);
		});
	}

	public void showUptime(Player admin) {
		// Comando /UPTIME
		if (!admin.isGM()) {
			return;
		}
		admin.sendMessage("Uptime: " + this.server.calculateUptime(), FontType.FONTTYPE_INFO);
	}


	public void doModMapInfo(Player admin, String accion, int valor) {
		// Comando /MODMAPINFO
		if ("".equals(accion)) {
			admin.sendMessage("Parámetros inválidos!", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/MODMAPINFO " + accion + " " + valor);
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		if (accion.equalsIgnoreCase("PK")) {
			if (valor == 0 || valor == 1) {
				mapa.pk = (valor == 1);
				admin.sendMessage("PK cambiado.", FontType.FONTTYPE_INFO);
			}
			admin.sendMessage("Mapa " + admin.pos().map + " PK: " + (mapa.pk ? "SI" : "NO"), FontType.FONTTYPE_INFO);
		} else if (accion.equalsIgnoreCase("BACKUP")) {
			if (valor == 0 || valor == 1) {
				mapa.backup = (valor == 1);
				admin.sendMessage("BACKUP cambiado.", FontType.FONTTYPE_INFO);
			}
			admin.sendMessage("Mapa " + admin.pos().map + " Backup: " + (mapa.backup ? "SI" : "NO"), FontType.FONTTYPE_INFO);
		}
	}

	public void doModificarCaracter(Player admin, String nick, String accion, int valor) {
		// MODIFICA CARACTER
		// Comando /MOD
		Log.logGM(admin.getNick(), "/MOD " + nick + " " + accion + " " + valor);
		if ("".equals(nick)) {
			admin.sendMessage("Parámetros inválidos!", FontType.FONTTYPE_INFO);
			return;
		}
		Player usuario = this.server.playerByUserName(nick);
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (accion.equalsIgnoreCase("ORO")) {
			if (valor < 95001) {
				usuario.stats().setGold(valor);
				usuario.sendUpdateUserStats();
			} else {
				admin.sendMessage(
						"No esta permitido utilizar valores mayores a 95000. Su comando ha quedado en los logs del juego.",
						FontType.FONTTYPE_INFO);
			}
		} else if (accion.equalsIgnoreCase("EXP")) {
			if (valor < 1000000) {
				usuario.stats().Exp += valor;
				usuario.checkUserLevel();
				usuario.sendUpdateUserStats();
			} else {
				admin.sendMessage(
						"No esta permitido utilizar valores mayores a 999999. Su comando ha quedado en los logs del juego.",
						FontType.FONTTYPE_INFO);
			}
		} else if (accion.equalsIgnoreCase("BODY")) {
			usuario.infoChar().body = (short) valor;
			usuario.sendCharacterChange();
		} else if (accion.equalsIgnoreCase("HEAD")) {
			usuario.infoChar().head = (short) valor;
			usuario.sendCharacterChange();
		} else if (accion.equalsIgnoreCase("CRI")) {
			usuario.userFaction().CriminalesMatados = valor;
		} else if (accion.equalsIgnoreCase("CIU")) {
			usuario.userFaction().CiudadanosMatados = valor;
		} else if (accion.equalsIgnoreCase("LEVEL")) {
			usuario.stats().ELV = valor;
		} else {
			admin.sendMessage("Comando no permitido o inválido.", FontType.FONTTYPE_INFO);
		}
	}
	
	public void saveMap(Player admin) {
		// Guardar el mapa actual.
		// Comando /GUARDAMAPA
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "/GUARDAMAPA " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.saveMapBackup();
			admin.sendMessage("Mapa guardado.", FontType.FONTTYPE_INFO);
		}
	}

	public void createItem(Player admin, short objid) {
		// Crear Item
		// Comando /CI
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getNick(), "/CI " + objid + " pos=" + admin.pos());
		Map map = this.server.getMap(admin.pos().map);
		if (map != null) {
			if (map.hasObject(admin.pos().x, admin.pos().y)) {
				return;
			}
			if (map.isTeleport(admin.pos().x, admin.pos().y)) {
				return;
			}
			if (findObj(objid) == null) {
				return;
			}
			map.agregarObjeto(objid, 100, admin.pos().x, admin.pos().y);
			admin.sendMessage("ATENCION: FUERON CREADOS ***100*** ITEMS! TIRE Y /DEST LOS QUE NO NECESITE!!", 
					FontType.FONTTYPE_GUILD);
		}
	}

	public void destroyItem(Player admin) {
		// Destruir el objeto de la posición actual.
		// Comando /DEST
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getNick(), "/DEST " + admin.pos());
		Map map = this.server.getMap(admin.pos().map);
		if (map != null) {
			if (!map.hasObject(admin.pos().x, admin.pos().y)) {
				admin.sendMessage("No hay objetos para destruir en la posición actual", FontType.FONTTYPE_INFO);
				return;
			}
			if (map.isTeleportObject(admin.pos().x, admin.pos().y)) {
				admin.sendMessage("No se pueden destruir teleports así. Utilice /DT.", FontType.FONTTYPE_INFO);
				return;
			}
			map.quitarObjeto(admin.pos().x, admin.pos().y);
		}
	}
	
	public void sendItemsInTheFloor(Player admin) {
    	// HandleItemsInTheFloor
		// Command /PISO
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
    		return;
    	}
		Map map = this.server.getMap(admin.pos().map);
		if (map != null) {
			map.sendItemsInTheFloor(admin);
		}
	}
	
	public void tileBlockedToggle(Player admin) {
		// Bloquear la posición actual.
		// Comando /BLOQ
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getNick(), "/BLOQ " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa != null) {
			if (mapa.isBlocked(admin.pos().x, admin.pos().y)) {
				mapa.unblockTile(admin.pos().x, admin.pos().y);
				admin.sendMessage("Posicion desbloqueada.", FontType.FONTTYPE_INFO);
			} else {
				mapa.blockTile(admin.pos().x, admin.pos().y);
				admin.sendMessage("Posicion bloqueada.", FontType.FONTTYPE_INFO);
			}
		}
	}

	public void killAllNearbyNPCs(Player admin) {
		// Quita todos los NPCs del area.
		// Comando /MASSKILL
		if (!admin.isGM() || admin.isDemiGod() || admin.isCounselor()) {
			return;
		}
		Log.logGM(admin.getNick(), "/MASSKILL " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.quitarNpcsArea(admin);
		}
	}

	public void askTrigger(Player admin) {
		// Comando /TRIGGER (sin argumentos)
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod() || admin.isRoleMaster()) {
    		return;
    	}
		Log.logGM(admin.getNick(), "/TRIGGER " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		
		Trigger t = mapa.getTrigger(admin.pos().x, admin.pos().y);
		admin.sendMessage("Trigger " + t.toString() + "(" + t.ordinal() + ")" + " en " + admin.pos(), 
				FontType.FONTTYPE_INFO);
	}

	public void setTrigger(Player admin, byte trigger) {
		// Comando /TRIGGER (con nuevo trigger)
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod() || admin.isRoleMaster()) {
    		return;
    	}
		Log.logGM(admin.getNick(), "/TRIGGER " + trigger + " " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		
		if (trigger <0 || trigger >= Trigger.values().length) {
			admin.sendMessage("no es un trigger válido", FontType.FONTTYPE_INFO);
			return;
		}
		mapa.setTrigger(admin.pos().x, admin.pos().y, Trigger.values()[trigger]);
		
		Trigger t = mapa.getTrigger(admin.pos().x, admin.pos().y);
		admin.sendMessage("Trigger " + t.toString() + "(" + t.ordinal() + ")" + " en " + admin.pos(), 
				FontType.FONTTYPE_INFO);
	}
	
	public void destroyAllItemsInArea(Player admin) {
		// Quita todos los objetos del area
		// Comando /MASSDEST
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
    		return;
    	}
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa == null) {
			return;
		}
		mapa.objectMassDestroy(admin, admin.pos().x, admin.pos().y);
		Log.logGM(admin.getNick(), "/MASSDEST ");
	}

	public void doResetInv(Player admin) {
		// Resetea el inventario
		// Comando /RESETINV
		if (!admin.isGM()) {
			return;
		}
		if (admin.flags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(admin.flags().TargetNpc);
		npc.npcInv().clear();
		admin.sendMessage("El inventario del npc " + npc.getName() + " ha sido vaciado.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getNick(), "/RESETINV " + npc.toString());
	}

	/**
	 * Broadcast a server message
	 * @param admin sending a server message
	 * @param message to broadcast to all connected users
	 */
	public void sendServerMessage(Player admin, String message) {
		// Comando /RMSG
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "Mensaje Broadcast: " + message);
		if (!message.equals("")) {
			if (admin.flags().isGM()) {
				Log.logGM(admin.getNick(), "Mensaje Broadcast:" + message);
				server.sendToAll(new ConsoleMsgResponse(message, FontType.FONTTYPE_TALK.id()));
			}
		}
	}
	
    public void sendMessageToAdmins(Player admin, String msg, FontType fuente) {
		if (!admin.isGM()) {
			return;
		}
    	for (Player user: server.players()) {
            if (user != null && user.getId() > 0 && user.flags().isGM() && user.isLogged()) {
                user.sendMessage(msg, fuente);
            }
        }
    }

    public void sendMessageToRoyalArmy(Player admin, String msg) {
		if (!admin.isGM()) {
			return;
		}
    	for (Player user: server.players()) {
            if (user != null && user.getId() > 0 && user.isLogged() 
            		&& (user.flags().isGM() || user.isRoyalArmy())) {
                user.sendMessage("ARMADA REAL> " + msg, FontType.FONTTYPE_TALK);
            }
        }
    }

    public void sendMessageToDarkLegion(Player admin, String message) {
		if (!admin.isGM()) {
			return;
		}
    	for (Player user: server.players()) {
            if (user != null && user.getId() > 0 && user.isLogged() 
            		&& (user.flags().isGM() || user.isDarkLegion())) {
                user.sendMessage("LEGION OSCURA> " + message, FontType.FONTTYPE_TALK);
            }
        }
    }

    public void sendMessageToCitizens(Player admin, String message) {
		if (!admin.isGM()) {
			return;
		}
    	for (Player user: server.players()) {
            if (user != null && user.getId() > 0 && user.isLogged()) {
                user.sendMessage("CIUDADANOS> " + message, FontType.FONTTYPE_TALK);
            }
        }
    }

	public void summonChar(Player admin, String userName) {
		// Comando /SUM usuario
		if (!admin.isGM()) {
			return;
		}
		if (userName.length() == 0) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("El usuario esta offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "Hizo /SUM " + userName);
		if (user.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, true)) {
			admin.sendMessage(user.getNick() + " ha sido trasportado.", FontType.FONTTYPE_INFO);
			user.sendMessage("Has sido trasportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "/SUM " + user.getNick() +
					" Map:" + admin.pos().map + " X:" + admin.pos().x + " Y:" + admin.pos().y);
		}
	}

	public void banUser(Player admin, String userName, String reason) {
		// Comando /BAN
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			if (Player.userExists(userName)) {
				user = new Player(server);
				try {
					user.userStorage.loadUserFromStorageOffline(userName);
				} catch (IOException ignore) {
					return;
				}
			} else {
				admin.sendMessage("El usuario no existe.", FontType.FONTTYPE_INFO);
				return;							
			}
		}
		if (user.flags().privileges > admin.flags().privileges) {
			admin.sendMessage("No puedes /BAN a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		sendMessageToAdmins(admin, admin.getNick() + " /BAN a " + user.getNick() + " por: " + reason, FontType.FONTTYPE_SERVER);
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getNick() + ">> /BAN por: " + reason + ". " + sdf.format(new java.util.Date()));
		user.sendError("Has sido expulsado permanentemente del servidor.");
		user.banned(admin.getNick(), reason);
		if (user.isLogged()) {
			user.quitGame();
		}
		UserStorage.banUser(user.getNick(), admin.getNick(), reason);
		Log.logGM(admin.getNick(), "/BAN " + userName + " por: " + reason);
	}

	public void unbanUser(Player admin, String userName) {
		// Comando /UNBAN
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		if (!UserStorage.isUserBanned(userName)) {
			admin.sendMessage("No se puede perdonar, porque el usuario no está expulsado.", FontType.FONTTYPE_INFO);
			return;			
		}
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getNick() + ">> /UNBAN " + sdf.format(new java.util.Date()));
		UserStorage.unBanUser(userName);
		Log.logGM(admin.getNick(), "/UNBAN a " + userName);
		admin.sendMessage(userName + " unbanned.", FontType.FONTTYPE_SERVER);
		sendMessageToAdmins(admin, admin.getNick() + " /UNBAN " + userName + ".", FontType.FONTTYPE_SERVER);
	}

	public void kickUser(Player admin, String userName) {
		// Echar usuario
		// Comando /ECHAR
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "quizo /ECHAR a " + userName);
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.flags().privileges > admin.flags().privileges) {
			admin.sendMessage("No puedes echar usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		server.sendToAll(new ConsoleMsgResponse(
				admin.getNick() + " echo a " + user.getNick() + ".",
				FontType.FONTTYPE_INFO.id()));
		Log.logGM(admin.getNick(), "Echó a " + user.getNick());
		user.sendError("Has sido echado del servidor.");
		user.quitGame();
	}

	public void kickAllUsersNoGm(Player admin) {
		// Comando /ECHARTODOSPJS
		if (!admin.isGM()) {
			return;
		}
		server.players().stream().forEach(p -> {
			if (!p.isGM()) {
				p.sendError("Todos han sido echados del servidor.");
				p.quitGame();				
			}
		});
		server.sendToAll(new ConsoleMsgResponse(
				admin.getNick() + " echo a todos los jugadores.",
				FontType.FONTTYPE_INFO.id()));
		Log.logGM(admin.getNick(), "Echó a todos con /ECHARTODOSPJS");
	}

	public void sendUserToJail(Player admin, String userName, String reason, byte minutes) {
		// Comando /CARCEL minutos usuario
		if (!admin.isGM()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.flags().privileges > admin.flags().privileges) {
			admin.sendMessage("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.counters().Pena > 0) {
			admin.sendMessage("El usuario ya esta en la carcel. Le quedan " + admin.counters().Pena + " minutos.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		Log.logGM(admin.getNick(), " /CARCEL " + userName);
		if (minutes > 60) {
			admin.sendMessage("No puedes encarcelar por mas de 60 minutos!", FontType.FONTTYPE_INFO);
			return;
		}
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getNick() + ">> /CARCEL " + reason + " " + sdf.format(new java.util.Date()));
		user.sendToJail(minutes, admin.getNick());
	}

	public void forgiveUser(Player admin, String userName) {
		// Comando /PERDON usuario
		// Perdonar a un usuario. Volverlo cuidadano.
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "quizo /PERDON " + userName);
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline. No tiene perdón.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isNewbie()) {
			if (user.reputation().esIntachable()) {
				admin.sendMessage("No hay nada que perdonarle a " + user.getNick(), FontType.FONTTYPE_INFO);
				return;
			}
			user.volverCiudadano();
			admin.sendMessage(user.getNick() + " ha sido perdonado.", FontType.FONTTYPE_INFO);
			user.sendMessage("Los dioses te han perdonado por esta vez.", FontType.FONTTYPE_INFO);
		} else {
			Log.logGM(admin.getNick(), "Intentó perdonar un personaje de nivel avanzado.");
			admin.sendMessage("Solo se permite perdonar newbies.", FontType.FONTTYPE_INFO);
		}
	}

	public void turnCriminal(Player admin, String userName) {
		// Comando /CONDEN usuario
		// Condenar a un usuario. Volverlo criminal.
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "quizo /CONDEN " + userName);
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes condenar administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.reputation().esCriminal()) {
			admin.sendMessage(user.getNick() + " ya es un criminal condenado.", FontType.FONTTYPE_INFO);
			return;
		}
		user.volverCriminal();
		admin.sendMessage(user.getNick() + " ha sido condenado.", FontType.FONTTYPE_INFO);
		user.sendMessage("Los dioses te han condenado por tus acciones.", FontType.FONTTYPE_INFO);
	}

	public void reviveUser(Player admin, String userName) {
		// Comando /REVIVIR
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getNick(), "quizo /REVIVIR " + userName);
		Player user;
		if (!userName.equalsIgnoreCase("YO") && userName.length() > 0) {
			user = this.server.playerByUserName(userName);
			if (user == null) {
				admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
		} else {
			user = admin;
		}
		if (user.isAlive()) {
			admin.sendMessage(user.getNick() + " no esta muerto!", FontType.FONTTYPE_INFO);
		} else {
			user.revive();
			user.sendMessage(admin.getNick() + " te ha resucitado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "Resucitó a " + user.getNick());
		}
	}

	public void showGmOnline(Player admin) {
		// Comando /ONLINEGM
		if (!admin.isGM()) {
			return;
		}
		var gmsOnline = this.server.getGMsOnline();
		if (gmsOnline.size() > 0) {
			String gmsList = String.join("" + Constants.NULL_CHAR, gmsOnline);
			admin.sendMessage("GM online: " + gmsList, FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("No hay GMs online.", FontType.FONTTYPE_INFO);
		}
	}

	public void requestCharInfo(Player admin, String userName) {
		// Inventario del usuario.
		// Comando /INFO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 

		sendUserStats(admin, user);

		Log.logGM(admin.getNick(), "/INFO " + userName);
	}

	public void sendUserStats(Player admin, Player user) {
		admin.sendMessage("Estadisticas de: " + user.getNick(), FontType.FONTTYPE_WARNING);
		admin.sendMessage("Nivel: " + user.stats().ELV + "  EXP: " + user.stats().Exp + "/" + user.stats().ELU, FontType.FONTTYPE_INFO);
		admin.sendMessage("Salud: " + user.stats().MinHP + "/" + user.stats().MaxHP + 
				"  Mana: " + user.stats().mana + "/" + user.stats().maxMana + 
				"  Vitalidad: " + user.stats().stamina + "/" + user.stats().maxStamina, FontType.FONTTYPE_INFO);

		if (user.userInv().tieneArmaEquipada()) {
			admin.sendMessage("Menor Golpe/Mayor Golpe: " + user.stats().MinHIT + "/" + user.stats().MaxHIT +
					" (" + user.userInv().getArma().MinHIT + "/" + user.userInv().getArma().MaxHIT + ")", FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("Menor Golpe/Mayor Golpe: " + user.stats().MinHIT + "/" + user.stats().MaxHIT, FontType.FONTTYPE_INFO);
		}
		
		if (user.userInv().tieneArmaduraEquipada()) {
			if (user.userInv().tieneEscudoEquipado()) {
				admin.sendMessage("(CUERPO) Min Def/Max Def: " + user.userInv().getArmadura().MinDef + user.userInv().getEscudo().MinDef + "/" +
						user.userInv().getArmadura().MaxDef + user.userInv().getEscudo().MaxDef, FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("(CUERPO) Min Def/Max Def: " + user.userInv().getArmadura().MinDef + "/" +
						user.userInv().getArmadura().MaxDef, FontType.FONTTYPE_INFO);
			}
		} else {
			admin.sendMessage("(CUERPO) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		
		if (user.userInv().tieneCascoEquipado()) {
			admin.sendMessage("(CABEZA) Min Def/Max Def: " + user.userInv().getCasco().MinDef + "/" +
					user.userInv().getCasco().MaxDef, FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("(CABEZA) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		
		if (user.guildInfo().esMiembroClan()) {
			admin.sendMessage("Clan: " + user.guildInfo().getGuildName(), FontType.FONTTYPE_INFO);
			if (user.guildInfo().esGuildLeader()) {
				admin.sendMessage("Status: Lider", FontType.FONTTYPE_INFO);	
			}
		}

		// FIXME UPTIME
		
		admin.sendMessage("Oro: " + user.stats().getGold() + "  Posición: " + 
				user.pos().x + "," + user.pos().y + " en mapa " + user.pos().map, FontType.FONTTYPE_INFO);
		admin.sendMessage("Dados: Fue. " + user.stats().attr().get(Attribute.FUERZA) +  
				" Agi. " + user.stats().attr().get(Attribute.AGILIDAD) + 
				" Int. " + user.stats().attr().get(Attribute.INTELIGENCIA) + 
				" Car. " + user.stats().attr().get(Attribute.CARISMA) + 
				" Con. " + user.stats().attr().get(Attribute.CONSTITUCION), FontType.FONTTYPE_INFO);
		
		/*
		admin.sendMessage(" Tiene " + user.userInv().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= user.userInv().size(); i++) {
			if (user.userInv().getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(user.userInv().getObjeto(i).objid);
				admin.sendMessage(
						" Objeto " + i + " " + info.Nombre + 
						" Cantidad:" + user.userInv().getObjeto(i).cant,
						FontType.FONTTYPE_INFO);
			}
		}
		*/
	}

	// FIXME
	public void requestCharInv(Player admin, String userName) {
		// Inventario del usuario.
		// Comando /INV
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		admin.sendMessage(user.getNick() + " tiene " + user.userInv().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= user.userInv().size(); i++) {
			if (user.userInv().getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(user.userInv().getObjeto(i).objid);
				admin.sendMessage(
						" Objeto " + i + " " + info.Nombre + 
						" Cantidad:" + user.userInv().getObjeto(i).cant,
						FontType.FONTTYPE_INFO);
			}
		}

		Log.logGM(admin.getNick(), "/INV " + userName);
	}

	public void requestCharStats(Player admin, String userName) {
		// Mini Stats del usuario
		// Comando /STAT
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		admin.sendMessage(" Tiene " + user.userInv().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		
        admin.sendMessage("Pj: " + user.getNick(), FontType.FONTTYPE_INFO);
        admin.sendMessage("CiudadanosMatados: " + user.userFaction().CiudadanosMatados 
        		+ " CriminalesMatados: " + user.userFaction().CriminalesMatados 
        		+ " UsuariosMatados: " + user.stats().usuariosMatados, FontType.FONTTYPE_INFO);
        
        admin.sendMessage("NPCsMuertos: " + user.stats().NPCsMuertos, FontType.FONTTYPE_INFO);
        admin.sendMessage("Clase: " + user.clazz().toString(), FontType.FONTTYPE_INFO);
        admin.sendMessage("Pena: " + user.counters().Pena, FontType.FONTTYPE_INFO);
        
        if (user.isRoyalArmy()) {
            admin.sendMessage("Armada Real Desde: " + user.userFaction().FechaIngreso, FontType.FONTTYPE_INFO);
            admin.sendMessage("Ingresó en Nivel: " + user.userFaction().NivelIngreso 
            		+ " con " + user.userFaction().MatadosIngreso + " Ciudadanos matados.", FontType.FONTTYPE_INFO);
            admin.sendMessage("Veces que Ingresó: " + user.userFaction().Reenlistadas, FontType.FONTTYPE_INFO);
        
        } else if (user.isDarkLegion()) {
            admin.sendMessage("Legion Oscura Desde: " + user.userFaction().FechaIngreso, FontType.FONTTYPE_INFO);
            admin.sendMessage("Ingresó en Nivel: " + user.userFaction().NivelIngreso, FontType.FONTTYPE_INFO);
            admin.sendMessage("Veces que Ingresó: " + user.userFaction().Reenlistadas, FontType.FONTTYPE_INFO);
        
        } else if (user.userFaction().RecibioExpInicialReal) {
            admin.sendMessage("Fue Armada Real", FontType.FONTTYPE_INFO);
            admin.sendMessage("Veces que Ingresó: " + user.userFaction().Reenlistadas, FontType.FONTTYPE_INFO);
        
        } else if (user.userFaction().RecibioExpInicialCaos) {
            admin.sendMessage("Fue Legionario", FontType.FONTTYPE_INFO);
            admin.sendMessage("Veces que Ingresó: " + user.userFaction().Reenlistadas, FontType.FONTTYPE_INFO);
        }
        
        admin.sendMessage("Asesino: " + user.reputation().getAsesinoRep(), FontType.FONTTYPE_INFO);
        admin.sendMessage("Noble: " + user.reputation().getNobleRep(), FontType.FONTTYPE_INFO);
        
        if (user.guildInfo().esMiembroClan()) {
        	admin.sendMessage("Clan: " + user.guildInfo().getGuildName(), FontType.FONTTYPE_INFO);
        }	

		Log.logGM(admin.getNick(), "/STAT " + userName);
	}
	
	
	public void requestCharGold(Player admin, String userName) {
		// Balance del usuario
		// Comando /BAL
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getNick(), "/BAL " + userName);

		admin.sendMessage("El usuario " + user.getNick() + " tiene " + user.stats().getBankGold() + " en el banco", FontType.FONTTYPE_TALK);
	}

	public void requestCharBank(Player admin, String userName) {
		// Boveda del usuario
		// Comando /BOV
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getNick(), "/BOV " + userName);

		admin.sendMessage(user.getNick() + " tiene " + user.getBankInventory().getCantObjs() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= user.getBankInventory().size(); i++) {
			if (user.getBankInventory().getObjeto(i).objid > 0) {
				ObjectInfo info = findObj(user.getBankInventory().getObjeto(i).objid);
				admin.sendMessage(" Objeto " + i + " " + info.Nombre 
						+ " Cantidad:" + user.getBankInventory().getObjeto(i).cant,	FontType.FONTTYPE_INFO);
			}
		}
	}

	public void requestCharSkills(Player admin, String userName) {
		// Skills del usuario
		// Comando /SKILLS
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getNick(), "/SKILLS " + userName);

		admin.sendMessage(user.getNick(), FontType.FONTTYPE_INFO);
		for (Skill skill : Skill.values()) {
			admin.sendMessage(" " + skill + ": " + user.skills().get(skill), FontType.FONTTYPE_INFO);
		}
		admin.sendMessage(" Libres: " + user.skills().getSkillPoints(), FontType.FONTTYPE_INFO);
	}

	public void doMensajeALosGM(Player admin, String chat) {
		// Mensaje para los GMs
		if (!admin.isGM()) {
			return;
		}
		if (chat.length() > 0) {
			Log.logGM(admin.getNick(), "Mensaje para GMs: " + chat);
			this.server.sendMessageToGMs(admin.getNick() + "> " + chat);
		}
	}

	public void sendServerTime(Player admin) {
		// Comando /HORA
		if (!admin.isGM()) {
			return;
		}
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
		java.util.Date ahora = new java.util.Date();
		String fecha = df.format(ahora);
		df = new java.text.SimpleDateFormat("HH:mm");
		String hora = df.format(ahora);
		admin.sendMessage("Hora: " + hora + " Fecha: " + fecha, FontType.FONTTYPE_INFO);
	}

	public void whereIsUser(Player admin, String userName) {
		// Comando /DONDE
		// ¿Donde esta fulano?
		if (!admin.isGM()) {
			return;
		}
		userName = userName.trim();
		Player usuario;
		if (userName.length() == 0) {
			usuario = admin;
		} else {
			usuario = this.server.playerByUserName(userName);
			if (usuario == null) {
				admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
			Log.logGM(admin.getNick(), "consultó /DONDE " + usuario.getNick());
		}
		admin.sendMessage("Ubicacion de " + usuario.getNick() + ": " + 
				usuario.pos().map + ", " + usuario.pos().x + ", " + usuario.pos().y + ".", 
				FontType.FONTTYPE_INFO);
	}
	
	public void sendCreaturesInMap(Player admin, short mapNumber) {
		// Command /NENE
		if (!admin.isGM()) {
			return;
		}
		if (mapNumber < 1) {
			admin.sendMessage("Has ingresado un número de mapa inválido.", FontType.FONTTYPE_INFO);
			return;
		}
		Map map = this.server.getMap(mapNumber);
		if (map != null) {
			Log.logGM(admin.getNick(), "Consultó el número de enemigos en el mapa, /NENE " + mapNumber);
			map.sendCreaturesInMap(admin);
		} else {
			admin.sendMessage("El mapa no existe.", FontType.FONTTYPE_INFO);
		}
	}

	public void warpMeToTarget(Player admin) {
		// Comando /TELEPLOC
		if (!admin.isGM()) {
			return;
		}
		if (admin.warpMe(admin.flags().TargetMap, admin.flags().TargetX, admin.flags().TargetY, true)) {
			
			Log.logGM(admin.getNick(), 
					"hizo un /TELEPLOC a x=" + admin.flags().TargetX + 
					" y=" + admin.flags().TargetY + " mapa=" + admin.flags().TargetMap);
		}
	}

	public void warpUserTo(Player admin, String nombre, short m, byte x, byte y) {
		// Comando /TELEP
		// Teleportar
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}
		
		if (m < 1) {
			admin.sendMessage("Parámetros incorrectos: /TELEP usuario mapa x y", FontType.FONTTYPE_INFO);
			return;
		}
		Map mapa = this.server.getMap(m);
		if (mapa == null) {
			return;
		}
		if (nombre.length() == 0) {
			return;
		}

		Player usuario = admin;
		if (!nombre.equalsIgnoreCase("YO")) {
			usuario = this.server.playerByUserName(nombre);
		}
		if (!Pos.isValid(x, y)) {
			return;
		}
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.warpMe(m, x, y, true)) {
			usuario.sendMessage(usuario.getNick() + " transportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getNick(), "Transportó con un /TELEP a " + usuario.getNick() +
					" hacia el mapa=" + m + " x=" + x + " y=" + y);
		}
	}

	public void lastIp(Player admin, String userName) {
		// Command /LASTIP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod() && !admin.isCounselor()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			if (!Player.userExists(userName)) {
				admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
				return;
			}
			admin.sendMessage("El usuario está desconectado.", FontType.FONTTYPE_INFO);
			user = new Player(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignored) {
				return;
			}
		}
		
		if (user.flags().privileges > admin.flags().privileges) {
			admin.sendMessage("No puedes consultar las últimas IPs de usuarios de mayor jerarquía.", 
					FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/LASTIP " + userName);
		var ipList = user.userStorage.loadLastIPs();
		admin.sendMessage("Últimas IP de " + userName + " son: " + String.join(", ", ipList), 
				FontType.FONTTYPE_INFO);
	}

	public void requestCharEmail(Player admin, String userName) {
		// Command /LASTEMAIL
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/LASTEMAIL " + userName);
		String email;
		try {
			email = UserStorage.emailFromStorage(userName);
			admin.sendMessage("Email de " + userName + " es: " + email, FontType.FONTTYPE_INFO);
		} catch (IOException ignored) {
		}
	}

	public void makeDumb(Player admin, String userName) {
		// Command /ESTUPIDO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!user.isDumb()) {
			Log.logGM(admin.getNick(), "/ESTUPIDO " + userName);
			user.makeDumb();
		} else {
			admin.sendMessage("El usuario ya estaba atontado.", FontType.FONTTYPE_INFO);
		}
	}

	public void makeNoDumb(Player admin, String userName) {
		// Command /NOESTUPIDO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		} 
		if (user.isDumb()) {
			Log.logGM(admin.getNick(), "/NOESTUPIDO " + userName);
			user.makeNoDumb();
		} else {
			admin.sendMessage("El usuario no estaba atontado.", FontType.FONTTYPE_INFO);
		}
	}

	public void executeUser(Player admin, String userName) {
		// Command /EJECUTAR
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("Estás loco?? como vas a piñatear un gm!!!! :@", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isAlive()) {
			Log.logGM(admin.getNick(), "/EJECUTAR " + userName);
			user.userDie();
			server.sendToAll(new ConsoleMsgResponse(
					admin.getNick() + " ha ejecutado a " + user.getNick(),
					FontType.FONTTYPE_EJECUCION.id()));
		} else {
			admin.sendMessage("El usuario no está vivo.", FontType.FONTTYPE_INFO);
		}
	}

	public void silenceUser(Player admin, String userName) {
		// Command /SILENCIAR
		if (!admin.isGM()) {
			return;
		}
		Player user = this.server.playerByUserName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes silenciar a un GM.", FontType.FONTTYPE_WARNING);
			return;
		}
		if (user.isAlive()) {
			if (user.isSilenced()) {
				user.undoSilence();
				admin.sendMessage("Usuario deja de estar silenciado.", FontType.FONTTYPE_INFO);
				Log.logGM(admin.getNick(), " ha dejado de silenciar a " + userName);
				user.sendMessage("Dejas de estar silenciado, pero no abuses del /DENUNCIAR.", FontType.FONTTYPE_SERVER);
			} else {
				user.turnSilence();
				admin.sendMessage("Usuario silenciado.", FontType.FONTTYPE_INFO);
				user.sendPacket(new ShowMessageBoxResponse(
						"ESTIMADO USUARIO, ud ha sido silenciado por los administradores. " +
								"Sus denuncias serán ignoradas por el servidor de aquí en más. " +
						"Utilice /GM para contactar un administrador."));
				user.sendMessage("Has sido silenciado, pero puedes usar /GM para contactar a un administrador.", FontType.FONTTYPE_SERVER);
				Log.logGM(admin.getNick(), " ha silenciado a " + userName);
			}
		} else {
			admin.sendMessage("El usuario no está vivo.", FontType.FONTTYPE_INFO);
		}
	}

	public void punishments(Player admin, String userName) {
		// Command /PENAS
		if (!admin.isGM()) {
			return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		Player user = new Player(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes ver las penas de los administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/PENAS " + userName);

		var punishments = UserStorage.punishments(userName);
		
		if (punishments.isEmpty()) {
			admin.sendMessage("Sin prontuario.", FontType.FONTTYPE_INFO);
		} else {
			int i = 1;
			for (String punishment : punishments) {
				admin.sendMessage( (i++) + " - " + punishment, FontType.FONTTYPE_INFO);
			}
		}
	}

	public void warnUser(Player admin, String userName, String reason) {
		// Command /ADVERTENCIA
		if (!admin.isGM()) {
			return;
		}
		if (userName == null || userName.isBlank() || reason == null || reason.isBlank()) {
			admin.sendMessage("Utilice /advertencia nick@motivo", FontType.FONTTYPE_INFO);
			return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		Player user = new Player(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes advertir a administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/ADVERTENCIA " + userName);

        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getNick() + ">> /ADVERTENCIA " + reason + " " + sdf.format(new java.util.Date()));
	    admin.sendMessage("Has advertido a " + userName, FontType.FONTTYPE_INFO);
	}

	public void removePunishment(Player admin, String userName, byte index, String newText) {
		// Command /BORRARPENA
		if (!admin.isGM()) {
			return;
		}
		if (userName == null || userName.isBlank() || newText == null || newText.isBlank()) {
			admin.sendMessage("Utilice /BORRARPENA Nick@NumeroDePena@NuevoTextoPena", FontType.FONTTYPE_INFO);
			return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		Player user = new Player(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes advertir a administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getNick(), "/BORRARPENA " + userName);

        var punishments = UserStorage.punishments(userName);
        if (index < 1 || index > punishments.size()) {
        	admin.sendMessage("Número de pena inválido. Hay " + punishments.size() + " penas.", FontType.FONTTYPE_INFO);
        	admin.sendMessage("Utilice /BORRARPENA Nick@NumeroDePena@NuevoTextoPena", FontType.FONTTYPE_INFO);
        	return;
        }
        
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.updatePunishment(userName, index, admin.getNick() + ">> /ADVERTENCIA " + newText + " " + sdf.format(new java.util.Date()));
	    admin.sendMessage("Has modificado una pena de " + userName, FontType.FONTTYPE_INFO);
	}

	public void playMidiAll(Player admin, byte midiId) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		server.sendToAll(new ConsoleMsgResponse(admin.getNick() + " broadcast música: " + midiId, FontType.FONTTYPE_SERVER.id()));
		server.sendToAll(new PlayMidiResponse(midiId, (byte)-1));
	}

	public void playMidiToMap(Player admin, short mapNumber, byte midiId) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		
		Map map = server.getMap(mapNumber);
		if (map == null) {
			map = server.getMap(admin.pos().map);
		}
		
		if (midiId == 0) {
			midiId = (byte)map.getMusic();
		}

		map.sendToAll(new PlayMidiResponse(midiId, (byte)-1));
	}

	public void playWaveAll(Player admin, byte waveId) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		server.sendToAll(new PlayWaveResponse(waveId, (byte)0, (byte)0));
	}

	public void playWavToMap(Player admin, byte waveId, short mapNumber, byte x, byte y) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		
		Map map = server.getMap(mapNumber);
		if (map == null || !Pos.isValid(x, y)) {
			map = server.getMap(admin.pos().map);
			x = admin.pos().x;
			y = admin.pos().y;
		}

		server.sendToAll(new PlayWaveResponse(waveId, x, y));
	}

	public void ipToNick(Player admin, String ip) {
		// Comando /NICK2IP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getNick(), "/IP2NICK " + ip);

		List<String> userNames = server.players().stream()
			.filter(p ->  p.isLogged() 
					&& p.hasNick() 
					&& p.flags().privileges < admin.flags().privileges
					&& p.getIP().equals(ip))
			.map( p -> p.getNick())
			.collect(Collectors.toList());
		
		if (!userNames.isEmpty()) {
			admin.sendMessage("Los personajes conectados desde la IP " + ip + "son: " 
					+ String.join(", ", userNames), FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("No se encontraron personajes conectados desde la IP " + ip, 
					FontType.FONTTYPE_INFO);
			
		}
	}

	public void bugReport(Player player, String bugReport) {
		final String fileName = Constants.LOG_DIR + File.separator + "BUGs.log";

		var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("Usuario:")
			.append(player.getNick())
			.append(" Fecha:")
			.append(sdf.format(new java.util.Date()))
			.append("\n")
			.append("BUG:")
			.append(bugReport)
			.append("\n########################################################################\n");
		try {
			try {
				Files.write(
						Paths.get(fileName),
						sb.toString().getBytes(),
						StandardOpenOption.APPEND);
			} catch (java.nio.file.NoSuchFileException e) {
				Files.write(
						Paths.get(fileName),
						sb.toString().getBytes(),
						StandardOpenOption.CREATE_NEW);
			}
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
	}

	public void serverOpenToUsersToggle(Player admin) {
		// Command /HABILITAR
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
        
		server.serverRestrictedToGMsToggle();
		
		if (server.isServerRestrictedToGMs()) {
			admin.sendMessage("Servidor restringido a administradores.", FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("Servidor habilitado para todos.", FontType.FONTTYPE_INFO);
		}
	}

	public void reloadServerIni(Player admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		loadAdmins();
    	admin.sendMessage("Se han recargado el server.ini", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getNick(), admin.getNick() + " ha recargado el server.ini");
	}

	public void alterEmail(Player admin, String userName, String newEmail) {
		// command /AEMAIL
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}

		if (userName == null || userName.isBlank() || newEmail == null || newEmail.isBlank()) { 
			admin.sendMessage("Usar /AEMAIL <pj>-<nuevomail>", FontType.FONTTYPE_INFO);
            return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No existe el charfile de " + userName, FontType.FONTTYPE_INFO);
			return;
		}
		Player user = server.playerByUserName(userName);
		if (user != null) {
			user.setEmail(newEmail);
		}
		UserStorage.updateEmail(userName, newEmail);
        admin.sendMessage("Email de " + userName + " cambiado a " + newEmail, FontType.FONTTYPE_INFO);
        
    	Log.logGM(admin.getNick(), admin.getNick() + " le ha cambiado el mail a " + userName);
	}

	public void alterName(Player admin, String userName, String newName) {
		// command /ANAME
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}

		if (userName == null || userName.isBlank() || newName == null || newName.isBlank()) { 
			admin.sendMessage("Usar: /ANAME nombreActual@nombreNuevo", FontType.FONTTYPE_INFO);
            return;
		}
		Player user = server.playerByUserName(userName);
		if (user != null) {
			admin.sendMessage("El usuario está conectado, debe salir primero.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!Player.userExists(userName)) {
			admin.sendMessage("No existe el charfile de " + userName, FontType.FONTTYPE_INFO);
			return;
		}
		String guildName = UserStorage.getGuildName(userName);
		if (guildName != null) {
			admin.sendMessage("El pj " + userName + " pertenece a un clan, debe salir del mismo con /salirclan para ser transferido.", FontType.FONTTYPE_INFO);
			return;
		}
		if (Player.userExists(newName)) {
			admin.sendMessage("El nombre solicitado ya existe", FontType.FONTTYPE_INFO);
			return;
		}

		try {
			UserStorage.changeName(userName, newName);
		} catch (IOException e) {
			admin.sendMessage("Hubo un error en la transferencia. Copia cancelada.", FontType.FONTTYPE_INFO);
			return;
		}
		admin.sendMessage("Transferencia exitosa", FontType.FONTTYPE_INFO);
		
		var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String reason = admin.getNick() + ">> /BAN por cambio de nombre de " + userName + " a " + newName + ". " + sdf.format(new java.util.Date());
        
        UserStorage.banUser(userName, admin.getNick(), reason);
		UserStorage.addPunishment(userName, reason);
        
		admin.sendMessage("Nombre de " + userName + " cambiado a " + newName, FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getNick(), admin.getNick() + " le ha cambiado el nombre de " + userName + " a " + newName);
	}

	public void roleMasterRequest(Player player, String request) {
		// command /ROL
		if (request != null && !request.isBlank()) {
            player.sendMessage("Su solicitud ha sido enviada", FontType.FONTTYPE_INFO);
            server.sendMessageToRoleMasters(player.getNick() + " PREGUNTA ROL: " + request);
		}
	}

}
