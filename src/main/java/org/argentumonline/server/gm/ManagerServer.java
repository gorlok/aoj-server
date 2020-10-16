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
package org.argentumonline.server.gm;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Pos;
import org.argentumonline.server.Skill;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.Tile.Trigger;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.protocol.ConsoleMsgResponse;
import org.argentumonline.server.protocol.PlayMidiResponse;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.protocol.ShowGMPanelFormResponse;
import org.argentumonline.server.protocol.ShowMessageBoxResponse;
import org.argentumonline.server.protocol.SpawnListResponse;
import org.argentumonline.server.protocol.UserNameListResponse;
import org.argentumonline.server.user.FactionArmors;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.user.UserFaction;
import org.argentumonline.server.user.UserStorage;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;
import org.argentumonline.server.util.Util;

public class ManagerServer {
	private static Logger log = LogManager.getLogger();

    private List<String> gods = new ArrayList<>();
    private List<String> demigods = new ArrayList<>();
    private List<String> counsellor = new ArrayList<>();

    private List<String> invalidNames = new ArrayList<>();

    private short [] spawnList;
    private String [] spawnListNames;

    private GameServer server;

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

            FactionArmors.loadFactionArmors(ini);
            
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

	public void saveGmComment(User admin, String comment) {
		// Comando /REM comentario
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "Hace el comentario: " + comment);
		admin.sendMessage("Comentario salvado...", FontType.FONTTYPE_INFO);
	}

	public void goToChar(User admin, String userName) {
		// Comando /IRA
		if ( !admin.isGM() ) {
			return;
		}
		if (userName.length() == 0) {
			return;
		}
		User usuario = this.server.userByName(userName);
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (admin.warpMe(usuario.pos().map, usuario.pos().x, usuario.pos().y, true)) {
			if (!admin.getFlags().AdminInvisible) {
				usuario.sendMessage(admin.getUserName() + " se ha trasportado hacia donde te encuentras.", FontType.FONTTYPE_INFO);
			}
			Log.logGM(admin.getUserName(), "Hizo un /IRA " + usuario.getUserName() + " mapa=" + usuario.pos().map + " x=" + usuario.pos().x
					+ " y=" + usuario.pos().y);
		}
	}

	public void turnInvisible(User admin) {
		// Comando /INVISIBLE
		if ( !admin.getFlags().isGM() ) {
			return;
		}
		Log.logGM(admin.getUserName(), "Hizo un /INVISIBLE");
		
		if (!admin.getFlags().AdminInvisible) {
			admin.getFlags().AdminInvisible = true;
			admin.getFlags().Invisible = true;
			admin.getFlags().OldBody = admin.infoChar().body;
			admin.getFlags().OldHead = admin.infoChar().head;
			admin.infoChar().body = 0;
			admin.infoChar().head = 0;
		} else {
			admin.getFlags().AdminInvisible = false;
			admin.getFlags().Invisible = false;
			admin.infoChar().body = admin.getFlags().OldBody;
			admin.infoChar().head = admin.getFlags().OldHead;
		}
		admin.sendCharacterChange();
	}

	public void sendSpawnCreatureList(User admin) {
		// Crear criatura
		// Comando /CC
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}
		
		admin.sendPacket(new SpawnListResponse(String.join("\0", getSpawnListNames())));
	}

	public void spawnCreature(User admin, short index) {
		// Spawn una criatura !!!!!
		// SPA
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}
		short[] spawnList = getSpawnList();
		if (index > 0 && index <= spawnList.length) {
			Npc npc = Npc.spawnNpc(spawnList[index - 1], admin.pos(), true, false);
			Log.logGM(admin.getUserName(), "Sumoneo al Npc " + npc.toString());
		}
	}
	
	public void royalArmyArmour(User admin, byte index, short armour) {
		// Commands /AI1 to /AI5
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		switch (index) {
		case 1:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_1, armour);
			break;
		case 2:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_2, armour);
			break;
		case 3:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_IMPERIAL_3, armour);
			break;
		case 4:
			FactionArmors.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL, armour);
			break;
		case 5:
			FactionArmors.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS, armour);
			break;
		}
	}
	
	public void darkLegionArmour(User admin, byte index, short armour) {
		// Commands /AC1 to /AC5
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		switch (index) {
		case 1:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_1, armour);
			break;
		case 2:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_2, armour);
			break;
		case 3:
			FactionArmors.updateFactionArmor(admin, FactionArmors.ARMADURA_CAOS_3, armour);
			break;
		case 4:
			FactionArmors.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS, armour);
			break;
		case 5:
			FactionArmors.updateFactionArmor(admin, FactionArmors.TUNICA_MAGO_CAOS_ENANOS, armour);
			break;
		}
	}

	public void sendUsersOnlineMap(User admin, int map) {
		// Comando /ONLINEMAP
		// Devuelve la lista de usuarios en el mapa.
		if ( !admin.isGM() ) {
			return;
		}
		
		Map mapa = this.server.getMap(map);
		if (mapa == null) {
			return;
		}
		var userNames = mapa.getUsers().stream()
				.filter(p -> !p.isGod())
				.map(User::getUserName)
				.collect(Collectors.toList());
		
		admin.sendMessage("Hay " + userNames.size() + 
				" usuarios en el mapa: " + String.join(", ", userNames), 
				FontType.FONTTYPE_INFO);
	}

	public void sendUsersWorking(User admin) {
		// Comando /TRABAJANDO
		// Devuelve la lista de usuarios trabajando.
		if ( !admin.isGM() ) {
			return;
		}
		
    	// Agrega un (*) al nombre del usuario que esté siendo monitoreado por el Centinela
		var users = server.getUsers().stream()
			    	.filter(c -> c.isLogged() && c.hasUserName() && c.isWorking())
			    	.map(p -> decorateUserName(p))
			    	.collect(Collectors.toList());
		
		admin.sendMessage("Usuarios trabajando: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}

	private String decorateUserName(User p) {
		return p.getUserName() + (server.getWorkWatcher().watchingUser(p.getUserName()) ? "(*)" : "");
	}
	
	public void sendUsersHiding(User admin) {
		// Devuelve la lista de usuarios ocultándose.
		if ( !admin.isGM() ) {
			return;
		}
		
		var users = server.getUsers().stream()
			    	.filter(c -> c.isLogged() && c.hasUserName() && c.isHidden()) // FIMXE c.counters().Ocultando > 0
			    	.map(User::getUserName)
			    	.collect(Collectors.toList());
		
		admin.sendMessage("Usuarios ocultándose: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}

	public void sendOnlineRoyalArmy(User admin) {
		if ( !admin.isGM() ) {
			return;
		}
		
		var users = server.getUsers().stream()
		    	.filter(c -> c.isLogged() && c.hasUserName() && c.isRoyalArmy())
		    	.map(User::getUserName)
		    	.collect(Collectors.toList());
	
		admin.sendMessage("Usuarios de la Armada Real: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}
	
	public void	sendOnlineChaosLegion(User admin) {
		if ( !admin.isGM() ) {
			return;
		}

		var users = server.getUsers().stream()
		    	.filter(c -> c.isLogged() && c.hasUserName() && c.isDarkLegion())
		    	.map(User::getUserName)
		    	.collect(Collectors.toList());
	
		admin.sendMessage("Usuarios de la Legión Oscura: " + String.join(", ", users), FontType.FONTTYPE_INFO);
	}

	public void showGmPanelForm(User admin) {
		// Comando /PANELGM
		if (!admin.isGM()) {
			return;
		}
		admin.sendPacket(new ShowGMPanelFormResponse());
	}

	public void sendUserNameList(User admin) {
		// Comando LISTUSU
		if (!admin.isGM()) {
			return;
		}
		String userNamesList = String.join("" + Constants.NULL_CHAR, this.server.getUsuariosConectados());
		admin.sendPacket(new UserNameListResponse(userNamesList));
	}

	public void backupWorld(User admin) {
		// Comando /DOBACKUP
		// Hacer un backup del mundo.
		if (!admin.isGM()) {
			return;
		}
		this.server.backupWorld();
	}

	public void saveChars(User admin) {
		// Comando /GRABAR
		// Guardar todos los usuarios conectados.
		if (!admin.isGM()) {
			return;
		}
		this.server.saveUsers();
	}

	public void shutdownServer(User admin) {
		// Comando /APAGAR
		if (!admin.isGM()) {
			return;
		}
		
        Log.logGM(admin.getUserName(), "/APAGAR");
		this.server.sendToAll(new ConsoleMsgResponse(admin.getUserName() + " VA A APAGAR EL SERVIDOR!!!", FontType.FONTTYPE_FIGHT.id()));
		
		// wait a few seconds...
		Util.sleep(5 * 1000);
		try {
			List.copyOf(server.getUsers()).forEach(p -> p.quitGame());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	this.server.backupWorld();

		log.info("SERVIDOR APAGADO POR " + admin.getUserName());
		Log.logGM(admin.getUserName(), "APAGO EL SERVIDOR");
		this.server.shutdown();
	}

	public void toggleRain(User admin) {
		// Comando /LLUVIA
		if (!admin.isGM()) {
			return;
		}
		toggleRain();
	}

	public void toggleRain() {
		if (this.server.isRaining()) {
			this.server.rainStop();
		} else {
			this.server.rainStart();
		}
	}

	public void sendSystemMsg(final User admin, String msg) {
		// Mensaje del sistema
		// Comando /SMSG
		if (!admin.isGM()) {
			return;
		}
		msg = msg.trim();
		Log.logGM(admin.getUserName(), "Envió el mensaje del sistema: " + msg);
		// FIXME
		server.sendToAll(new ShowMessageBoxResponse(msg));
	}

	public void userNameToIp(final User admin, final String userName) {
		// Comando /NICK2IP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		final User user = this.server.userByName(userName);
		if (user == null) {
			if (!User.userExists(userName)) {
				admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("El personaje está desconectado.", FontType.FONTTYPE_INFO);
			}
			return;
		}
		Log.logGM(admin.getUserName(), "consultó /NICK2IP " + userName);
		admin.sendMessage("La IP de " + userName + " es " + user.getIP(), FontType.FONTTYPE_INFO);

		List<String> userNames = server.getUsers().stream()
			.filter(p ->  p.isLogged() 
					&& p.hasUserName() 
					&& !userName.equalsIgnoreCase(p.getUserName())
					&& p.getIP().equals(user.getIP()))
			.map( p -> p.getUserName())
			.collect(Collectors.toList());
		if (!userNames.isEmpty()) {
			admin.sendMessage("Otros personajes con la misma IP son: " 
					+ String.join(", ", userNames), FontType.FONTTYPE_INFO);
		}
	}

	public void createTeleport(User admin, short dest_mapa, byte dest_x, byte dest_y) {
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
		Log.logGM(admin.getUserName(), "Creó un teleport: " + admin.pos() + " que apunta a: " + dest_mapa + " " + dest_x + " " + dest_y);
		admin.sendMessage("¡Teleport creado!", FontType.FONTTYPE_INFO);
	}

	public void destroyTeleport(User admin) {
		// Comando /DT
		// Destruir un teleport, toma el ultimo clic
		if (!admin.isGM()) {
			return;
		}
		if (admin.getFlags().TargetObjMap == 0 || admin.getFlags().TargetObjX == 0 || admin.getFlags().TargetObjY == 0) {
			admin.sendMessage("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		Map map = this.server.getMap(admin.getFlags().TargetObjMap);
		if (map == null) {
			admin.sendMessage("Debes hacer clic sobre el Teleport que deseas destruir.", FontType.FONTTYPE_WARNING);
			return;
		}
		byte x = admin.getFlags().TargetObjX;
		byte y = admin.getFlags().TargetObjY;
		if (!map.isTeleport(x, y)) {
			admin.sendMessage("¡Debes hacer clic sobre el Teleport que deseas destruir!", FontType.FONTTYPE_WARNING);
			return;
		}
		Log.logGM(admin.getUserName(), "Destruyó un teleport, con /DT mapa=" + map.getMapNumber() + " x=" + x + " y=" + y);
		map.destroyTeleport(x, y);
	}
	
	public void npcFollow(User admin) {
		// Command /SEGUIR
		if (!admin.isGM() || admin.isCounselor()) {
			return;
		}

		Npc npc = this.server.npcById(admin.getFlags().TargetNpc);
		if (npc == null) {
			admin.sendMessage("Haz clic sobre un Npc y luego escribe /SEGUIR", FontType.FONTTYPE_INFO);
			return;
		}
		
		npc.followUser(admin.getUserName());
		npc.unparalize();
		npc.unimmobilize();
	}

	public void killNpc(User admin) {
		// Quitar Npc
		// Comando //RMATA 
		if (!admin.isGM()) {
			return;
		}
		
        // Los consejeros no pueden RMATAr nada en el mapa pretoriano
		if (admin.isCounselor()) {
			if (admin.pos().map == Constants.MAPA_PRETORIANO) {
				admin.sendMessage("Los consejeros no pueden usar este comando en el mapa pretoriano.", FontType.FONTTYPE_INFO);
				return;
			}
		}
		
		Npc npc = this.server.npcById(admin.getFlags().TargetNpc);
		if (npc == null) {
			admin.sendMessage("Haz clic sobre un Npc y luego escribe /RMATA. PERO MUCHO CUIDADO!", FontType.FONTTYPE_INFO);
			return;
		}
		admin.sendMessage("RMATAs (con posible respawn) a: " + npc.getName(), FontType.FONTTYPE_INFO);
		npc.quitarNPC();
		admin.getFlags().TargetNpc = 0;
		Log.logGM(admin.getUserName(), "/RMATA " + npc);
	}

	
	public void killNpcNoRespawn(User admin) {
		// Quitar Npc
		// Comando /MATA indiceNpc
		if (!admin.isGM() || admin.isDemiGod() || admin.isCounselor()) {
			return;
		}
		Npc npc = this.server.npcById(admin.getFlags().TargetNpc);
		if (npc == null) {
			admin.sendMessage("Debés hacer clic sobre un Npc y luego escribir /MATA. PERO MUCHO CUIDADO!", FontType.FONTTYPE_INFO);
			return;
		}
		npc.quitarNPC();
		admin.getFlags().TargetNpc = 0;
		Log.logGM(admin.getUserName(), "/MATA " + npc);
	}

	public void createNpc(User admin, short indiceNpc) {
		// Crear criatura, toma directamente el indice
		// Comando /ACC indiceNpc
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		if (this.server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, false);
		} else {
			admin.sendMessage("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}

	public void createNpcWithRespawn(User admin, short indiceNpc) {
		// Crear criatura con respawn, toma directamente el indice
		// Comando /RACC indiceNpc
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		if (this.server.npcById(indiceNpc) != null) {
			Npc.spawnNpc(indiceNpc, admin.pos(), true, true);
		} else {
			admin.sendMessage("Indice de Npc invalido.", FontType.FONTTYPE_INFO);
		}
	}


	public void showUptime(User admin) {
		// Comando /UPTIME
		if (!admin.isGM()) {
			return;
		}
		admin.sendMessage("Uptime: " + this.server.calculateUptime(), FontType.FONTTYPE_INFO);
	}


	public void saveMap(User admin) {
		// Guardar el mapa actual.
		// Comando /GUARDAMAPA
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/GUARDAMAPA " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.saveMapBackup();
			admin.sendMessage("Mapa guardado.", FontType.FONTTYPE_INFO);
		}
	}

	public void createItem(User admin, short objid) {
		// Crear Item
		// Comando /CI
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/CI " + objid + " pos=" + admin.pos());
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
			map.addObject(objid, 100, admin.pos().x, admin.pos().y);
			admin.sendMessage("ATENCION: FUERON CREADOS ***100*** ITEMS! TIRE Y /DEST LOS QUE NO NECESITE!!", 
					FontType.FONTTYPE_GUILD);
		}
	}

	public void destroyItem(User admin) {
		// Destruir el objeto de la posición actual.
		// Comando /DEST
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/DEST " + admin.pos());
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
			map.removeObject(admin.pos().x, admin.pos().y);
		}
	}
	
	public void sendItemsInTheFloor(User admin) {
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
	
	public void tileBlockedToggle(User admin) {
		// Bloquear la posición actual.
		// Comando /BLOQ
		if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/BLOQ " + admin.pos());
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

	public void killAllNearbyNpcs(User admin) {
		// Quita todos los NPCs del area.
		// Comando /MASSKILL
		if (!admin.isGM() || admin.isDemiGod() || admin.isCounselor()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/MASSKILL " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		if (mapa != null) {
			mapa.removeNpcsArea(admin);
		}
	}

	public void askTrigger(User admin) {
		// Comando /TRIGGER (sin argumentos)
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod() || admin.isRoleMaster()) {
    		return;
    	}
		Log.logGM(admin.getUserName(), "/TRIGGER " + admin.pos());
		Map mapa = this.server.getMap(admin.pos().map);
		
		Trigger t = mapa.getTrigger(admin.pos().x, admin.pos().y);
		admin.sendMessage("Trigger " + t.toString() + "(" + t.ordinal() + ")" + " en " + admin.pos(), 
				FontType.FONTTYPE_INFO);
	}

	public void setTrigger(User admin, byte trigger) {
		// Comando /TRIGGER (con nuevo trigger)
    	if (!admin.isGM() || admin.isCounselor() || admin.isDemiGod() || admin.isRoleMaster()) {
    		return;
    	}
		Log.logGM(admin.getUserName(), "/TRIGGER " + trigger + " " + admin.pos());
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
	
	public void destroyAllItemsInArea(User admin) {
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
		Log.logGM(admin.getUserName(), "/MASSDEST ");
	}

	public void resetNpcInventory(User admin) {
		// Resetea el inventario
		// Comando /RESETINV
		if (!admin.isGM()) {
			return;
		}
		if (admin.getFlags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(admin.getFlags().TargetNpc);
		npc.npcInv().clear();
		admin.sendMessage("El inventario del npc " + npc.getName() + " ha sido vaciado.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getUserName(), "/RESETINV " + npc.toString());
	}

	public void summonChar(User admin, String userName) {
		// Comando /SUM usuario
		if (!admin.isGM()) {
			return;
		}
		if (userName.length() == 0) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("El usuario esta offline.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "Hizo /SUM " + userName);
		if (user.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, true)) {
			admin.sendMessage(user.getUserName() + " ha sido trasportado.", FontType.FONTTYPE_INFO);
			user.sendMessage("Has sido trasportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getUserName(), "/SUM " + user.getUserName() +
					" Map:" + admin.pos().map + " X:" + admin.pos().x + " Y:" + admin.pos().y);
		}
	}

	public void kickUser(User admin, String userName) {
		// Echar usuario
		// Comando /ECHAR
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "quizo /ECHAR a " + userName);
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.getFlags().privileges > admin.getFlags().privileges) {
			admin.sendMessage("No puedes echar usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		server.sendToAll(new ConsoleMsgResponse(
				admin.getUserName() + " echo a " + user.getUserName() + ".",
				FontType.FONTTYPE_INFO.id()));
		Log.logGM(admin.getUserName(), "Echó a " + user.getUserName());
		user.sendError("Has sido echado del servidor.");
		user.quitGame();
	}

	public void kickAllUsersNoGm(User admin) {
		// Comando /ECHARTODOSPJS
		if (!admin.isGM()) {
			return;
		}
		server.getUsers().stream().forEach(p -> {
			if (!p.isGM()) {
				p.sendError("Todos han sido echados del servidor.");
				p.quitGame();				
			}
		});
		server.sendToAll(new ConsoleMsgResponse(
				admin.getUserName() + " echo a todos los jugadores.",
				FontType.FONTTYPE_INFO.id()));
		Log.logGM(admin.getUserName(), "Echó a todos con /ECHARTODOSPJS");
	}

	public void sendUserToJail(User admin, String userName, String reason, byte minutes) {
		// Comando /CARCEL minutos usuario
		if (!admin.isGM()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.getFlags().privileges > admin.getFlags().privileges) {
			admin.sendMessage("No puedes encarcelar a usuarios de mayor jerarquia a la tuya!", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.getCounters().Pena > 0) {
			admin.sendMessage("El usuario ya esta en la carcel. Le quedan " + admin.getCounters().Pena + " minutos.",
					FontType.FONTTYPE_WARNING);
			return;
		}
		Log.logGM(admin.getUserName(), " /CARCEL " + userName);
		if (minutes > 60) {
			admin.sendMessage("No puedes encarcelar por mas de 60 minutos!", FontType.FONTTYPE_INFO);
			return;
		}
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getUserName() + ">> /CARCEL " + reason + " " + sdf.format(new java.util.Date()));
		user.sendToJail(minutes, admin.getUserName());
	}

	public void forgiveUser(User admin, String userName) {
		// Comando /PERDON usuario
		// Perdonar a un usuario. Volverlo cuidadano.
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "quizo /PERDON " + userName);
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline. No tiene perdón.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isNewbie()) {
			if (user.getReputation().esIntachable()) {
				admin.sendMessage("No hay nada que perdonarle a " + user.getUserName(), FontType.FONTTYPE_INFO);
				return;
			}
			user.volverCiudadano();
			admin.sendMessage(user.getUserName() + " ha sido perdonado.", FontType.FONTTYPE_INFO);
			user.sendMessage("Los dioses te han perdonado por esta vez.", FontType.FONTTYPE_INFO);
		} else {
			Log.logGM(admin.getUserName(), "Intentó perdonar un personaje de nivel avanzado.");
			admin.sendMessage("Solo se permite perdonar newbies.", FontType.FONTTYPE_INFO);
		}
	}

	public void turnCriminal(User admin, String userName) {
		// Comando /CONDEN usuario
		// Condenar a un usuario. Volverlo criminal.
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "quizo /CONDEN " + userName);
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes condenar administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.getReputation().esCriminal()) {
			admin.sendMessage(user.getUserName() + " ya es un criminal condenado.", FontType.FONTTYPE_INFO);
			return;
		}
		user.turnCriminal();
		admin.sendMessage(user.getUserName() + " ha sido condenado.", FontType.FONTTYPE_INFO);
		user.sendMessage("Los dioses te han condenado por tus acciones.", FontType.FONTTYPE_INFO);
	}

	public void reviveUser(User admin, String userName) {
		// Comando /REVIVIR
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "quizo /REVIVIR " + userName);
		User user;
		if (!userName.equalsIgnoreCase("YO") && userName.length() > 0) {
			user = this.server.userByName(userName);
			if (user == null) {
				admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
		} else {
			user = admin;
		}
		if (user.isAlive()) {
			admin.sendMessage(user.getUserName() + " no esta muerto!", FontType.FONTTYPE_INFO);
		} else {
			user.revive();
			user.sendMessage(admin.getUserName() + " te ha resucitado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getUserName(), "Resucitó a " + user.getUserName());
		}
	}

	public void showGmOnline(User admin) {
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

	public void requestCharInfo(User admin, String userName) {
		// Inventario del usuario.
		// Comando /INFO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 

		sendUserStats(admin, user);

		Log.logGM(admin.getUserName(), "/INFO " + userName);
	}

	public void sendUserStats(User admin, User user) {
		admin.sendMessage("Estadisticas de: " + user.getUserName(), FontType.FONTTYPE_WARNING);
		admin.sendMessage("Nivel: " + user.getStats().ELV + "  EXP: " + user.getStats().Exp + "/" + user.getStats().ELU, FontType.FONTTYPE_INFO);
		admin.sendMessage("Salud: " + user.getStats().MinHP + "/" + user.getStats().MaxHP + 
				"  Mana: " + user.getStats().mana + "/" + user.getStats().maxMana + 
				"  Vitalidad: " + user.getStats().stamina + "/" + user.getStats().maxStamina, FontType.FONTTYPE_INFO);

		if (user.getUserInv().tieneArmaEquipada()) {
			admin.sendMessage("Menor Golpe/Mayor Golpe: " + user.getStats().MinHIT + "/" + user.getStats().MaxHIT +
					" (" + user.getUserInv().getArma().MinHIT + "/" + user.getUserInv().getArma().MaxHIT + ")", FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("Menor Golpe/Mayor Golpe: " + user.getStats().MinHIT + "/" + user.getStats().MaxHIT, FontType.FONTTYPE_INFO);
		}
		
		if (user.getUserInv().tieneArmaduraEquipada()) {
			if (user.getUserInv().tieneEscudoEquipado()) {
				admin.sendMessage("(CUERPO) Min Def/Max Def: " + user.getUserInv().getArmadura().MinDef + user.getUserInv().getEscudo().MinDef + "/" +
						user.getUserInv().getArmadura().MaxDef + user.getUserInv().getEscudo().MaxDef, FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("(CUERPO) Min Def/Max Def: " + user.getUserInv().getArmadura().MinDef + "/" +
						user.getUserInv().getArmadura().MaxDef, FontType.FONTTYPE_INFO);
			}
		} else {
			admin.sendMessage("(CUERPO) Min Def/Max Def: 0", FontType.FONTTYPE_INFO);
		}
		
		if (user.getUserInv().tieneCascoEquipado()) {
			admin.sendMessage("(CABEZA) Min Def/Max Def: " + user.getUserInv().getCasco().MinDef + "/" +
					user.getUserInv().getCasco().MaxDef, FontType.FONTTYPE_INFO);
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
		
		admin.sendMessage("Oro: " + user.getStats().getGold() + "  Posición: " + 
				user.pos().x + "," + user.pos().y + " en mapa " + user.pos().map, FontType.FONTTYPE_INFO);
		admin.sendMessage("Dados: Fue. " + user.getStats().attr().get(Attribute.STRENGTH) +  
				" Agi. " + user.getStats().attr().get(Attribute.AGILITY) + 
				" Int. " + user.getStats().attr().get(Attribute.INTELIGENCE) + 
				" Car. " + user.getStats().attr().get(Attribute.CHARISMA) + 
				" Con. " + user.getStats().attr().get(Attribute.CONSTITUTION), FontType.FONTTYPE_INFO);
		
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
	public void requestCharInv(User admin, String userName) {
		// Inventario del usuario.
		// Comando /INV
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		admin.sendMessage(user.getUserName() + " tiene " + user.getUserInv().getObjectsCount() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= user.getUserInv().getSize(); i++) {
			if (user.getUserInv().getObject(i).objid > 0) {
				ObjectInfo info = findObj(user.getUserInv().getObject(i).objid);
				admin.sendMessage(
						" Objeto " + i + " " + info.Nombre + 
						" Cantidad:" + user.getUserInv().getObject(i).cant,
						FontType.FONTTYPE_INFO);
			}
		}

		Log.logGM(admin.getUserName(), "/INV " + userName);
	}

	public void requestCharStats(User admin, String userName) {
		// Mini Stats del usuario
		// Comando /STAT
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		admin.sendMessage(" Tiene " + user.getUserInv().getObjectsCount() + " objetos.", FontType.FONTTYPE_INFO);
		
        admin.sendMessage("Pj: " + user.getUserName(), FontType.FONTTYPE_INFO);
        admin.sendMessage("CiudadanosMatados: " + user.userFaction().citizensKilled 
        		+ " CriminalesMatados: " + user.userFaction().criminalsKilled 
        		+ " UsuariosMatados: " + user.getStats().usuariosMatados, FontType.FONTTYPE_INFO);
        
        admin.sendMessage("NPCsMuertos: " + user.getStats().NPCsMuertos, FontType.FONTTYPE_INFO);
        admin.sendMessage("Clase: " + user.clazz().toString(), FontType.FONTTYPE_INFO);
        admin.sendMessage("Pena: " + user.getCounters().Pena, FontType.FONTTYPE_INFO);
        
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
        
        admin.sendMessage("Asesino: " + user.getReputation().getAsesinoRep(), FontType.FONTTYPE_INFO);
        admin.sendMessage("Noble: " + user.getReputation().getNobleRep(), FontType.FONTTYPE_INFO);
        
        if (user.guildInfo().esMiembroClan()) {
        	admin.sendMessage("Clan: " + user.guildInfo().getGuildName(), FontType.FONTTYPE_INFO);
        }	

		Log.logGM(admin.getUserName(), "/STAT " + userName);
	}
	
	
	public void requestCharGold(User admin, String userName) {
		// Balance del usuario
		// Comando /BAL
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getUserName(), "/BAL " + userName);

		admin.sendMessage("El usuario " + user.getUserName() + " tiene " + user.getStats().getBankGold() + " en el banco", FontType.FONTTYPE_TALK);
	}

	public void requestCharBank(User admin, String userName) {
		// Boveda del usuario
		// Comando /BOV
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getUserName(), "/BOV " + userName);

		admin.sendMessage(user.getUserName() + " tiene " + user.getBankInventory().getObjectsCount() + " objetos.", FontType.FONTTYPE_INFO);
		for (int i = 1; i <= user.getBankInventory().getSize(); i++) {
			if (user.getBankInventory().getObject(i).objid > 0) {
				ObjectInfo info = findObj(user.getBankInventory().getObject(i).objid);
				admin.sendMessage(" Objeto " + i + " " + info.Nombre 
						+ " Cantidad:" + user.getBankInventory().getObject(i).cant,	FontType.FONTTYPE_INFO);
			}
		}
	}

	public void requestCharSkills(User admin, String userName) {
		// Skills del usuario
		// Comando /SKILLS
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_WARNING);

			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignore) {
				return;
			}
		} 
		Log.logGM(admin.getUserName(), "/SKILLS " + userName);

		admin.sendMessage(user.getUserName(), FontType.FONTTYPE_INFO);
		for (Skill skill : Skill.values()) {
			admin.sendMessage(" " + skill + ": " + user.skills().get(skill), FontType.FONTTYPE_INFO);
		}
		admin.sendMessage(" Libres: " + user.skills().getSkillPoints(), FontType.FONTTYPE_INFO);
	}

	public void sendServerTime(User admin) {
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

	public void whereIsUser(User admin, String userName) {
		// Comando /DONDE
		// ¿Donde esta fulano?
		if (!admin.isGM()) {
			return;
		}
		userName = userName.trim();
		User usuario;
		if (userName.length() == 0) {
			usuario = admin;
		} else {
			usuario = this.server.userByName(userName);
			if (usuario == null) {
				admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
				return;
			}
			Log.logGM(admin.getUserName(), "consultó /DONDE " + usuario.getUserName());
		}
		admin.sendMessage("Ubicacion de " + usuario.getUserName() + ": " + 
				usuario.pos().map + ", " + usuario.pos().x + ", " + usuario.pos().y + ".", 
				FontType.FONTTYPE_INFO);
	}
	
	public void sendCreaturesInMap(User admin, short mapNumber) {
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
			Log.logGM(admin.getUserName(), "Consultó el número de enemigos en el mapa, /NENE " + mapNumber);
			map.sendCreaturesInMap(admin);
		} else {
			admin.sendMessage("El mapa no existe.", FontType.FONTTYPE_INFO);
		}
	}

	public void warpMeToTarget(User admin) {
		// Comando /TELEPLOC
		if (!admin.isGM()) {
			return;
		}
		if (admin.warpMe(admin.getFlags().TargetMap, admin.getFlags().TargetX, admin.getFlags().TargetY, true)) {
			
			Log.logGM(admin.getUserName(), 
					"hizo un /TELEPLOC a x=" + admin.getFlags().TargetX + 
					" y=" + admin.getFlags().TargetY + " mapa=" + admin.getFlags().TargetMap);
		}
	}

	public void warpUserTo(User admin, String nombre, short m, byte x, byte y) {
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

		User usuario = admin;
		if (!nombre.equalsIgnoreCase("YO")) {
			usuario = this.server.userByName(nombre);
		}
		if (!Pos.isValid(x, y)) {
			return;
		}
		if (usuario == null) {
			admin.sendMessage("Usuario offline.", FontType.FONTTYPE_INFO);
			return;
		}
		if (usuario.warpMe(m, x, y, true)) {
			usuario.sendMessage(usuario.getUserName() + " transportado.", FontType.FONTTYPE_INFO);
			Log.logGM(admin.getUserName(), "Transportó con un /TELEP a " + usuario.getUserName() +
					" hacia el mapa=" + m + " x=" + x + " y=" + y);
		}
	}

	public void lastIp(User admin, String userName) {
		// Command /LASTIP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod() && !admin.isCounselor()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			if (!User.userExists(userName)) {
				admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
				return;
			}
			admin.sendMessage("El usuario está desconectado.", FontType.FONTTYPE_INFO);
			user = new User(server);
			try {
				user.userStorage.loadUserFromStorageOffline(userName);
			} catch (IOException ignored) {
				return;
			}
		}
		
		if (user.getFlags().privileges > admin.getFlags().privileges) {
			admin.sendMessage("No puedes consultar las últimas IPs de usuarios de mayor jerarquía.", 
					FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "/LASTIP " + userName);
		var ipList = user.userStorage.loadLastIPs();
		admin.sendMessage("Últimas IP de " + userName + " son: " + String.join(", ", ipList), 
				FontType.FONTTYPE_INFO);
	}

	public void requestCharEmail(User admin, String userName) {
		// Command /LASTEMAIL
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "/LASTEMAIL " + userName);
		String email;
		try {
			email = UserStorage.emailFromStorage(userName);
			admin.sendMessage("Email de " + userName + " es: " + email, FontType.FONTTYPE_INFO);
		} catch (IOException ignored) {
		}
	}

	public void makeDumb(User admin, String userName) {
		// Command /ESTUPIDO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!user.isDumb()) {
			Log.logGM(admin.getUserName(), "/ESTUPIDO " + userName);
			user.makeDumb();
		} else {
			admin.sendMessage("El usuario ya estaba atontado.", FontType.FONTTYPE_INFO);
		}
	}

	public void makeNoDumb(User admin, String userName) {
		// Command /NOESTUPIDO
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		} 
		if (user.isDumb()) {
			Log.logGM(admin.getUserName(), "/NOESTUPIDO " + userName);
			user.makeNoDumb();
		} else {
			admin.sendMessage("El usuario no estaba atontado.", FontType.FONTTYPE_INFO);
		}
	}

	public void executeUser(User admin, String userName) {
		// Command /EJECUTAR
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		User user = this.server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("Estás loco?? como vas a piñatear un gm!!!! :@", FontType.FONTTYPE_INFO);
			return;
		}
		if (user.isAlive()) {
			Log.logGM(admin.getUserName(), "/EJECUTAR " + userName);
			user.userDie();
			server.sendToAll(new ConsoleMsgResponse(
					admin.getUserName() + " ha ejecutado a " + user.getUserName(),
					FontType.FONTTYPE_EJECUCION.id()));
		} else {
			admin.sendMessage("El usuario no está vivo.", FontType.FONTTYPE_INFO);
		}
	}

	public void silenceUser(User admin, String userName) {
		// Command /SILENCIAR
		if (!admin.isGM()) {
			return;
		}
		User user = this.server.userByName(userName);
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
				Log.logGM(admin.getUserName(), " ha dejado de silenciar a " + userName);
				user.sendMessage("Dejas de estar silenciado, pero no abuses del /DENUNCIAR.", FontType.FONTTYPE_SERVER);
			} else {
				user.turnSilence();
				admin.sendMessage("Usuario silenciado.", FontType.FONTTYPE_INFO);
				user.sendPacket(new ShowMessageBoxResponse(
						"ESTIMADO USUARIO, ud ha sido silenciado por los administradores. " +
								"Sus denuncias serán ignoradas por el servidor de aquí en más. " +
						"Utilice /GM para contactar un administrador."));
				user.sendMessage("Has sido silenciado, pero puedes usar /GM para contactar a un administrador.", FontType.FONTTYPE_SERVER);
				Log.logGM(admin.getUserName(), " ha silenciado a " + userName);
			}
		} else {
			admin.sendMessage("El usuario no está vivo.", FontType.FONTTYPE_INFO);
		}
	}

	public void punishments(User admin, String userName) {
		// Command /PENAS
		if (!admin.isGM()) {
			return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		User user = new User(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes ver las penas de los administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "/PENAS " + userName);

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

	public void warnUser(User admin, String userName, String reason) {
		// Command /ADVERTENCIA
		if (!admin.isGM()) {
			return;
		}
		if (userName == null || userName.isBlank() || reason == null || reason.isBlank()) {
			admin.sendMessage("Utilice /advertencia nick@motivo", FontType.FONTTYPE_INFO);
			return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		User user = new User(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes advertir a administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "/ADVERTENCIA " + userName);

        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.addPunishment(userName, admin.getUserName() + ">> /ADVERTENCIA " + reason + " " + sdf.format(new java.util.Date()));
	    admin.sendMessage("Has advertido a " + userName, FontType.FONTTYPE_INFO);
	}

	public void removePunishment(User admin, String userName, byte index, String newText) {
		// Command /BORRARPENA
		if (!admin.isGM()) {
			return;
		}
		if (userName == null || userName.isBlank() || newText == null || newText.isBlank()) {
			admin.sendMessage("Utilice /BORRARPENA Nick@NumeroDePena@NuevoTextoPena", FontType.FONTTYPE_INFO);
			return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No hay ningún personaje con ese nombre.", FontType.FONTTYPE_INFO);
			return;
		}
		User user = new User(server);
		try {
			user.userStorage.loadUserFromStorageOffline(userName);
		} catch (IOException ignore) {
			return;
		}
		if (user.isGM()) {
			admin.sendMessage("No puedes advertir a administradores.", FontType.FONTTYPE_INFO);
			return;
		}
		Log.logGM(admin.getUserName(), "/BORRARPENA " + userName);

        var punishments = UserStorage.punishments(userName);
        if (index < 1 || index > punishments.size()) {
        	admin.sendMessage("Número de pena inválido. Hay " + punishments.size() + " penas.", FontType.FONTTYPE_INFO);
        	admin.sendMessage("Utilice /BORRARPENA Nick@NumeroDePena@NuevoTextoPena", FontType.FONTTYPE_INFO);
        	return;
        }
        
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		UserStorage.updatePunishment(userName, index, admin.getUserName() + ">> /ADVERTENCIA " + newText + " " + sdf.format(new java.util.Date()));
	    admin.sendMessage("Has modificado una pena de " + userName, FontType.FONTTYPE_INFO);
	}

	public void playMidiAll(User admin, byte midiId) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		server.sendToAll(new ConsoleMsgResponse(admin.getUserName() + " broadcast música: " + midiId, FontType.FONTTYPE_SERVER.id()));
		server.sendToAll(new PlayMidiResponse(midiId, (byte)-1));
	}

	public void playMidiToMap(User admin, short mapNumber, byte midiId) {
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

	public void playWaveAll(User admin, byte waveId) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		server.sendToAll(new PlayWaveResponse(waveId, (byte)0, (byte)0));
	}

	public void playWavToMap(User admin, byte waveId, short mapNumber, byte x, byte y) {
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

	public void ipToUserName(User admin, String ip) {
		// Comando /NICK2IP
		if (!admin.isGod() && !admin.isAdmin() && !admin.isDemiGod()) {
			return;
		}
		Log.logGM(admin.getUserName(), "/IP2NICK " + ip);

		List<String> userNames = server.getUsers().stream()
			.filter(p ->  p.isLogged() 
					&& p.hasUserName() 
					&& p.getFlags().privileges < admin.getFlags().privileges
					&& p.getIP().equals(ip))
			.map( p -> p.getUserName())
			.collect(Collectors.toList());
		
		if (!userNames.isEmpty()) {
			admin.sendMessage("Los personajes conectados desde la IP " + ip + "son: " 
					+ String.join(", ", userNames), FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("No se encontraron personajes conectados desde la IP " + ip, 
					FontType.FONTTYPE_INFO);
			
		}
	}

	public void bugReport(User user, String bugReport) {
		final String fileName = Constants.LOG_DIR + File.separator + "BUGs.log";

		var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("Usuario:")
			.append(user.getUserName())
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

	public void serverOpenToUsersToggle(User admin) {
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

	public void reloadServerIni(User admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		loadAdmins();
    	admin.sendMessage("Se han recargado el server.ini", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getUserName(), admin.getUserName() + " ha recargado el server.ini");
	}

	public void alterEmail(User admin, String userName, String newEmail) {
		// command /AEMAIL
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}

		if (userName == null || userName.isBlank() || newEmail == null || newEmail.isBlank()) { 
			admin.sendMessage("Usar /AEMAIL <pj>-<nuevomail>", FontType.FONTTYPE_INFO);
            return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No existe el charfile de " + userName, FontType.FONTTYPE_INFO);
			return;
		}
		User user = server.userByName(userName);
		if (user != null) {
			user.setEmail(newEmail);
		}
		UserStorage.updateEmail(userName, newEmail);
        admin.sendMessage("Email de " + userName + " cambiado a " + newEmail, FontType.FONTTYPE_INFO);
        
    	Log.logGM(admin.getUserName(), admin.getUserName() + " le ha cambiado el mail a " + userName);
	}

	public void alterName(User admin, String userName, String newName) {
		// command /ANAME
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}

		if (userName == null || userName.isBlank() || newName == null || newName.isBlank()) { 
			admin.sendMessage("Usar: /ANAME nombreActual@nombreNuevo", FontType.FONTTYPE_INFO);
            return;
		}
		User user = server.userByName(userName);
		if (user != null) {
			admin.sendMessage("El usuario está conectado, debe salir primero.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!User.userExists(userName)) {
			admin.sendMessage("No existe el charfile de " + userName, FontType.FONTTYPE_INFO);
			return;
		}
		String guildName = UserStorage.getGuildName(userName);
		if (guildName != null) {
			admin.sendMessage("El pj " + userName + " pertenece a un clan, debe salir del mismo con /salirclan para ser transferido.", FontType.FONTTYPE_INFO);
			return;
		}
		if (User.userExists(newName)) {
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
		String reason = admin.getUserName() + ">> /BAN por cambio de nombre de " + userName + " a " + newName + ". " + sdf.format(new java.util.Date());
        
        UserStorage.banUser(userName, admin.getUserName(), reason);
		UserStorage.addPunishment(userName, reason);
        
		admin.sendMessage("Nombre de " + userName + " cambiado a " + newName, FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getUserName(), admin.getUserName() + " le ha cambiado el nombre de " + userName + " a " + newName);
	}

	public void roleMasterRequest(User user, String request) {
		// command /ROL
		if (request != null && !request.isBlank()) {
            user.sendMessage("Su solicitud ha sido enviada", FontType.FONTTYPE_INFO);
            server.sendMessageToRoleMasters(user.getUserName() + " PREGUNTA ROL: " + request);
		}
	}

	public void acceptRoyalCouncilMember(User admin, String userName) {
		// Command /ACEPTCONSE
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			admin.sendMessage("Usuario offline", FontType.FONTTYPE_INFO);
		} else {
			targetUser.getFlags().removeChaosCouncil();
			targetUser.getFlags().addRoyalCouncil();
			
			server.sendToAll(new ConsoleMsgResponse(userName + " fue aceptado en el honorable Consejo Real de Banderbill.", FontType.FONTTYPE_CONSEJO.id()));
			targetUser.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, false);
		}
	}

	public void acceptChaosCouncilMember(User admin, String userName) {
		// command /ACEPTCONSECAOS
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			admin.sendMessage("Usuario offline", FontType.FONTTYPE_INFO);
		} else {
			targetUser.getFlags().removeRoyalCouncil();
			targetUser.getFlags().addChaosCouncil();
			
			server.sendToAll(new ConsoleMsgResponse(userName + " fue aceptado en el Concilio de las Sombras.", FontType.FONTTYPE_CONSEJO.id()));
			targetUser.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, false);
		}
	}

	public void councilKick(User admin, String userName) {
		// Command /ACEPTCONSECAOS
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			if (User.userExists(userName)) {
				UserStorage.councilKick(userName);
				admin.sendMessage("Usuario offline. Echado de los consejos.", FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("No se encuentra el charfile de " + userName, FontType.FONTTYPE_INFO);
			}
		} else {
			if (targetUser.isRoyalCouncil()) {
				targetUser.getFlags().removeRoyalCouncil();
				targetUser.sendMessage("Has sido echado del Consejo de Banderbill", FontType.FONTTYPE_TALK);
				targetUser.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, false);
				server.sendToAll(new ConsoleMsgResponse(userName + " fue expulsado del honorable Consejo Real de Banderbill.", FontType.FONTTYPE_CONSEJO.id()));
			}
			if (targetUser.isChaosCouncil()) {
				targetUser.getFlags().removeChaosCouncil();
				targetUser.sendMessage("Has sido echado del Concilio de las Sombras", FontType.FONTTYPE_TALK);
				targetUser.warpMe(admin.pos().map, admin.pos().x, admin.pos().y, false);
				server.sendToAll(new ConsoleMsgResponse(userName + " fue expulsado del Concilio de las Sombras.", FontType.FONTTYPE_CONSEJO.id()));
			}
		}
	}

	public void royalArmyKickForEver(User admin, String userName) {
		// Command /NOREAL
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " echo de la Armada Real a " + userName);
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			if (User.userExists(userName)) {
				UserFaction.royalArmyKick(admin, userName);
				admin.sendMessage(userName + " fue expulsado de la Armada Real y prohibido su reingreso.", FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("No se encuentra el charfile de " + userName, FontType.FONTTYPE_INFO);
			}
		} else {
			targetUser.userFaction().royalArmyKickForEver(admin.getUserName());
			admin.sendMessage(userName + " fue expulsado de las Armada Real y prohibido su reingreso.", FontType.FONTTYPE_INFO);
		}
	}

	public void chaosLegionKickForEver(User admin, String userName) {
		// Command /NOCAOS
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " echo de la Legión Oscura a " + userName);
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			if (User.userExists(userName)) {
				UserFaction.chaosLegionKick(admin, userName);
				admin.sendMessage(userName + " fue expulsado de la Legión Oscura y prohibido su reingreso.", FontType.FONTTYPE_INFO);
			} else {
				admin.sendMessage("No se encuentra el charfile de " + userName, FontType.FONTTYPE_INFO);
			}
		} else {
			targetUser.userFaction().darkLegionKickForEver(admin.getUserName());
			admin.sendMessage(userName + " fue expulsado de las Legión Oscura y prohibido su reingreso.", FontType.FONTTYPE_INFO);
		}
	}

	public void resetFactions(User admin, String userName) {
		// Command /RAJAR
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " /RAJAR " + userName);
		User targetUser = server.userByName(userName);
		if (targetUser == null) {
			if (User.userExists(userName)) {
				UserFaction.resetFactions(userName);
			} else {
				admin.sendMessage("El usuario " + userName + " no existe.", FontType.FONTTYPE_INFO);
			}
		} else {
			targetUser.userFaction().reset();
		}
	}

	public void handleCheckSlot(User admin, String userName, byte slot) {
		// Command /SLOT
		if (!admin.isGod() && !admin.isDemiGod() && !admin.isAdmin()) {
			return;
		}
		
		User user = server.userByName(userName);
		if (user == null) {
			admin.sendMessage("Usuario desconectado.", FontType.FONTTYPE_INFO);
		} else {
			Log.logGM(admin.getUserName(), admin.getUserName() + " Checkeo el slot " + slot + " de " + userName);
			
			if (user.getUserInv().isValidSlot(slot)) {
				if (user.getUserInv().isEmpty(slot)) {
					admin.sendMessage("No hay Objeto en el slot seleccionado.", FontType.FONTTYPE_INFO);
				} else {
					var obj = user.getUserInv().getObject(slot);
					admin.sendMessage(" Objeto (" + slot + ") " + obj.objInfo().Nombre 
							+ " Cantidad:" + obj.cant, FontType.FONTTYPE_INFO);
				}
			} else {
				admin.sendMessage("Slot inválido.", FontType.FONTTYPE_INFO);
			}
		}
	           
	}
	
}
