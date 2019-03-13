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
package org.ArgentumOnline.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ArgentumOnline.server.api.ManagerApi;
import org.ArgentumOnline.server.forum.ForumManager;
import org.ArgentumOnline.server.gm.ManagerServer;
import org.ArgentumOnline.server.gm.Motd;
import org.ArgentumOnline.server.guilds.GuildManager;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.net.NetworkServer;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.net.upnp.NetworkUPnP;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcLoader;
import org.ArgentumOnline.server.protocol.RainToggleResponse;
import org.ArgentumOnline.server.quest.Quest;
import org.ArgentumOnline.server.user.Player;
import org.ArgentumOnline.server.user.Spell;
import org.ArgentumOnline.server.util.Feedback;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;

/**
 * Server main class
 * @author gorlok
 */
public class GameServer implements Constants {
	private static Logger log = LogManager.getLogger();

    private boolean useUPnP = false; // FIXME configure this

    private HashMap<Short, Player> players = new HashMap<>();
    private HashMap<Short, Npc> npcs = new HashMap<>();

	private List<Player> playersToDrop = new LinkedList<>();
	private List<Short> deadNpcs = new LinkedList<>();

	private List<Spell> spells = new LinkedList<>();
	private List<Map> maps = new LinkedList<>();
	private List<Quest> quests = new LinkedList<>();

    private List<MapPos> trashCollector = new LinkedList<>();

    private short[] armasHerrero;
    private short[] armadurasHerrero;
    private short[] objCarpintero;

    boolean running = false;
    boolean doingBackup = false;

    private short lastId = 0;

    boolean raining = false;

    private MapPos[] cities;

    private long startTime = 0;

    public static MapPos WP_PRISION = MapPos.mxy(66, 75, 47);
    public static MapPos WP_LIBERTAD = MapPos.mxy(66, 75, 65);

    private boolean showDebug = false;

    private GuildManager guildManager;
    private Motd motd;
    private ForumManager forumManager;
    private NpcLoader npcLoader;
    private ManagerServer manager;
    private ObjectInfoStorage objectInfoStorage;
    private GamblerStats gamblerStats;

    private Feedback feedback = new Feedback();// FIXME

    private NetworkServer ns;

    private GameServer() {
    	// start API management server
    	new ManagerApi(this);
    	
    	// start network game server
    	this.ns = new NetworkServer(Constants.SERVER_PORT);
    	
    	
    	// initialize game server
    	this.guildManager = new GuildManager(this);
    	this.motd = new Motd();
    	this.forumManager = new ForumManager();
    	this.npcLoader = new NpcLoader(this);
    	this.manager = new ManagerServer(this);
    	this.objectInfoStorage = new ObjectInfoStorage();
    	this.gamblerStats = new GamblerStats();
    }    	

    private static GameServer instance = null;

    public synchronized static GameServer instance() {
        if (instance == null) {
			instance = new GameServer();
		}
        return instance;
    }
    
    public GamblerStats getGamblerStats() {
		return this.gamblerStats;
	}

    public ObjectInfoStorage getObjectInfoStorage() {
		return this.objectInfoStorage;
	}

    public ManagerServer manager() {
		return this.manager;
	}

    public Motd getMotd() {
    	return this.motd;
    }

    public GuildManager getGuildMngr() {
    	return this.guildManager;
    }

    public ForumManager getForumManager() {
		return this.forumManager;
	}

    public NpcLoader getNpcLoader() {
		return this.npcLoader;
	}

    public List<Player> players() {
    	return this.players.values().stream()
    			.collect(Collectors.toList());
    }

    public List<Npc> npcs() {
    	return new ArrayList<>(this.npcs.values());
    }

    public long runningTimeInSecs() {
        return (Util.millis() - this.startTime) / 1000;
    }

	public String calculateUptime() {
		long tsegs = runningTimeInSecs();
		long segs = tsegs % 60;
		long mins = tsegs / 60;
		long horas = mins / 60;
		long dias = horas / 24;

		return new StringBuilder()
			.append(dias)
			.append("d ")
			.append(horas)
			.append("h ")
			.append(mins)
			.append("m ")
			.append(segs)
			.append("s").toString();
	}

    public short nextId() {
    	do { 
    		this.lastId++;
    		if (this.lastId < 0) {
    			// just in case, Java's short type is signed
    			this.lastId = 1;
    		}
    	} while (players.containsKey(this.lastId) || npcs.containsKey(this.lastId));
    	
        return this.lastId;
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean isDoingBackup() {
        return this.doingBackup;
    }

    public List<MapPos> getTrashCollector() {
        return this.trashCollector;
    }

    public Quest quest(int n) {
        return this.quests.get(n - 1);
    }

    public int questCount() {
        return this.quests.size();
    }

    public short[] getArmasHerrero() {
        return this.armasHerrero;
    }

    public short[] getArmadurasHerrero() {
        return this.armadurasHerrero;
    }

    public short[] getObjCarpintero() {
        return this.objCarpintero;
    }

    public boolean isShowDebug() {
    	return this.showDebug;
    }

    public void setShowDebug(boolean value) {
    	this.showDebug = value;
    }

    public List<String> getUsuariosConectados() {
    	return players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && !c.flags().isGM())
			    	.map(Player::getNick)
			    	.collect(Collectors.toList());
    }

    public void echarPjsNoPrivilegiados() {
    	var users = players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && !c.flags().isGM())
			    	.collect(Collectors.toList());

    	users.forEach(c -> {
	    	c.sendMessage("Servidor> Conexiones temporalmente cerradas por mantenimiento.", FontType.FONTTYPE_SERVER);
	        c.quitGame();
    	});
    }

    public List<String> getUsuariosTrabajando() {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.isWorking())
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
    }

    public void shutdown() {
        this.running = false;
        this.ns.shutdown();
        System.out.println("=== Goodbye. Server closed. ===");
        System.exit(0);
    }

    public List<String> getUsuariosConIP(String ip) {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.getIP().equals(ip))
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
    }

    public List<String> getGMsOnline() {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.flags().isGM())
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
    }

    public boolean isLoadBackup() {
		return loadBackup;
	}

    /** Main loop of the game. */
    private void runGameLoop() {
        loadAll(loadBackup);

        //networkServer = NetworkServer.startServer(this);
        try {
        	if (this.useUPnP) {
        		NetworkUPnP.openUPnP();
        	}

            this.startTime = Util.millis();
            long lastNpcAI = this.startTime;
            long lastFX = this.startTime;
            long lastGameTimer = this.startTime;
            long lastNpcAtacaTimer = this.startTime;
            long lastTimerOculto = this.startTime;
            long lastLluviaTimer = this.startTime;
            long lastEventTimer = this.startTime;
            long lastPiqueteTimer = this.startTime;
            long lastPurgarPenas = this.startTime;
            long lastAutoSaveTimer = this.startTime;
            long lastPasarSegundoTimer = this.startTime;
            this.running = true;

            int fps = 0;
            long worstTime = 0;
        	// Main loop.
            while (this.running) {
                fps++;
                long now = Util.millis();
                if ((now - lastNpcAI) > 400) {
                    doAI();
                    lastNpcAI = now;
                }
                if ((now - lastFX) > 200) {
                    FX_Timer();
                    lastFX = now;
                }
                if ((now - lastGameTimer) > 40) { // 40 ??
                    gameTimer();
                    lastGameTimer = now;
                }
                if ((now - lastNpcAtacaTimer) > 2000) { // 4000 ??
                    npcAtacaTimer();
                    lastNpcAtacaTimer = now;
                }
                if ((now - lastTimerOculto) > 500) { // 3000 ??
                    timerOculto();
                    lastTimerOculto = now;
                }
                if ((now - lastLluviaTimer) > 1500) { // 60000 ??
                    rainEffectTimer();
                    lastLluviaTimer = now;
                }
                if ((now - lastEventTimer) > 60000) {
                    rainEvent();
                    lastEventTimer = now;
                }
                if ((now - lastPiqueteTimer) > 6000) {
                    piqueteTimer();
                    lastPiqueteTimer = now;
                }
                if ((now - lastPurgarPenas) > 60000) {
                    purgePenalties();
                    checkIdleUser();
                    lastPurgarPenas = now;
                }
                if ((now - lastAutoSaveTimer) > 60000) {
                    autoSaveTimer();
                    lastAutoSaveTimer = now;
                }
                if ((now - lastPasarSegundoTimer) > 1000) { // 1 vez x segundo
//                	System.out.format("fps: %2d   max-time: %3dms    online: %3d    gm: %3d\n", 
//                			fps, worstTime, getUsuariosConectados().size(), getGMsOnline().size());
                	fps = 0;
                	worstTime = 0;
                    pasarSegundo();
                    lastPasarSegundoTimer = now;
                }
                removeDropedPlayers();

                long ellapsed = Util.millis() - now;
                if (ellapsed > worstTime) worstTime = ellapsed;
                long wait = (1000 - ellapsed);
                if (wait < 0) wait = 1;
                if (wait > 40) wait = 40;
                Util.sleep(wait);
            }
        } finally {
            doBackup();
            guardarUsuarios();
            log.info("Server apagado por doBackUp");
        }
    }

    public synchronized void dropPlayer(Player player) {
    	player.closeConnection();
    	this.playersToDrop.add(player);
    }

    private synchronized void removeDropedPlayers() {
    	// Se hace aqui para evitar problemas de concurrencia
    	for (Player player: this.playersToDrop) {
            this.players.remove(player.getId());
    	}
    	this.playersToDrop.clear();
    }

    private static String memoryStatus() {
    	return  "total " + (int) (Runtime.getRuntime().totalMemory() / 1024) +
    			"KB free " + (int) (Runtime.getRuntime().freeMemory() / 1024) +
				"KB";
    }

    private void loadMaps(boolean loadBackup) {
    	log.trace("loading maps");
        this.maps = new ArrayList<>(CANT_MAPAS);
        Map mapa;
        for (short i = 1; i <= CANT_MAPAS; i++) {
            mapa = new Map(i, this);
            mapa.load(loadBackup);
            this.maps.add(mapa);
        }
    }

    private void loadSpells() {
    	log.trace("loading spells");
        IniFile ini = new IniFile();
        try {
            ini.load(DATDIR + java.io.File.separator + "Hechizos.dat");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        int cant = ini.getShort("INIT", "NumeroHechizos");

        this.spells = new ArrayList<>(cant);
        for (int i = 0; i < cant; i++) {
            Spell hechizo = new Spell(i+1);
        	hechizo.load(ini);
            this.spells.add(hechizo);
        }
    }

    private void loadQuests() {
    	log.trace("loading quests");
        IniFile ini = new IniFile();
        try {
            ini.load(DATDIR + java.io.File.separator + "Quests.dat");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        short cant = ini.getShort("INIT", "NumQuests");
        this.quests = new ArrayList<>(cant);
        for (short i = 1; i <= cant; i++) {
            Quest quest = new Quest(this, i);
            quest.load(ini);
            this.quests.add(quest);
        }
    }

    /** Load all initial data */
    private void loadAll(boolean loadBackup) {
    	log.trace("loadAllData started");
        this.objectInfoStorage.loadObjectsFromStorage();
        loadSpells();
        loadMaps(loadBackup);
        loadQuests();
        loadCities();
        loadBlacksmithingWeapons();
        loadBlacksmithingArmors();
        loadCarpentryObjects();
        this.manager.loadAdminsSpawnableCreatures();
        this.manager.loadInvalidNamesList();
        this.manager.loadAdmins();
        this.motd.loadMotd();
        log.trace("loadAllData ended");
    }

    public Npc createNpc(int npcNumber) {
        Npc npc = getNpcLoader().createNpc(npcNumber);
        this.npcs.put(npc.getId(), npc);
        return npc;
    }

    public Player createPlayer(Channel channel) {
        Player player = new Player(channel, this);
        this.players.put(player.getId(), player);
        return player;
    }

    public Optional<Player> findPlayer(Channel channel) {
    	return this.players.values().stream()
		    		.filter(p -> p.getChannel() == channel)
		    		.findFirst();
    }

    public void deleteNpc(Npc npc) {
        this.deadNpcs.add(npc.getId());
    }

    public Npc npcById(short npcId) {
        return this.npcs.get(npcId);
    }

    public Player playerById(short id) {
        return this.players.get(id);
    }

    public Spell getSpell(int spell) {
        return this.spells.get(spell - 1);
    }

    public Map getMap(int map) {
        if (map > 0 && map <= this.maps.size()) {
			return this.maps.get(map - 1);
		}
		return null;
    }

    public Player playerByUserName(String nombre) {
    	if ("".equals(nombre)) {
			return null;
		}
    	for (Player player: players()) {
            if (player.getNick().equalsIgnoreCase(nombre)) {
                return player;
            }
        }
        return null;
    }

    public boolean isPlayerAlreadyConnected(Player player) {
    	for (Player other: players()) {
            if (player != other
            		&& player.getNick().equalsIgnoreCase(other.getNick())) {
                return true;
            }
        }
        return false;
    }

    private void doAI() {
        // TIMER_AI_Timer()
        if (!this.doingBackup) {
            var npcs = new ArrayList<>(npcs());
            npcs.stream()
            	.filter(npc -> npc.isNpcActive() && !npc.isStatic())
            	.forEach(npc -> {
	                if (npc.isParalized()) {
	                    npc.efectoParalisisNpc();
	                } else {
	                    // Usamos AI si hay algun user en el mapa
	                    if (npc.pos().isValid()) {
	                        Map mapa = getMap(npc.pos().map);
	                        if (mapa != null && mapa.getPlayersCount() > 0) {
	                            if (!npc.isStatic()) {
	                                npc.doAI();
	                            }
	                        }
	                    }
	                }
            	});
	        this.deadNpcs.stream().map(npcId -> this.npcs.remove(npcId));
	        this.deadNpcs.clear();
        }
    }

    private void pasarSegundo() {
        var paraSalir = new LinkedList<Player>();
        for (Player cli: players()) {
            if (cli.counters().Saliendo) {
                cli.counters().SalirCounter--;
                if (cli.counters().SalirCounter <= 0) {
                    paraSalir.add(cli);
                } else {
                	switch (cli.counters().SalirCounter) {
                		case 10:
                			cli.sendMessage("En " + cli.counters().SalirCounter +" segundos se cerrar� el juego...", FontType.FONTTYPE_INFO);
                			break;
                		case 3:
                			cli.sendMessage("Gracias por jugar Argentum Online. Vuelve pronto.", FontType.FONTTYPE_INFO);
                			break;
                	}
                }
            }
        }
        for (Player cli: paraSalir) {
            cli.quitGame();
        }
    }

    public void guardarUsuarios() {
        this.doingBackup = true;
        try {
            for (Player cli: players()) {
                if (cli.isLogged()) {
                    cli.saveUser();
                }
            }
        } finally {
            this.doingBackup = false;
        }
    }

    private void loadCities() {
    	log.trace("loading cities");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "Ciudades.dat");
            this.cities = new MapPos[Ciudad.values().length];
            loadCity(ini, Ciudad.NIX, "NIX");
            loadCity(ini, Ciudad.ULLATHORPE, "Ullathorpe");
            loadCity(ini, Ciudad.BANDERBILL, "Banderbill");
            loadCity(ini, Ciudad.LINDOS, "Lindos");
            loadCity(ini, Ciudad.ARGHAL, "Arghal");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

	private void loadCity(IniFile ini, Ciudad ciudad, String section) {
		this.cities[ciudad.id()] = 
				MapPos.mxy(ini.getShort(section, "MAPA"), 
						ini.getShort(section, "X"), 
						ini.getShort(section, "Y"));
	}

    public MapPos getCiudadPos(Ciudad ciudad) {
        return this.cities[ciudad.id()];
    }

    private void FX_Timer() {
    	for (Map mapa: this.maps) {
            if ((Util.Azar(1, 150) < 12) && (mapa.getPlayersCount() > 0)) {
                mapa.doFX();
            }
        }
    }

    private void gameTimer() {
    	// This is like GameTimer_Timer
        // <<<<<< Procesa eventos de los usuarios >>>>>>
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0) {
                cli.procesarEventos();
            }
        }
    }

    private void npcAtacaTimer() {
    	for (Npc npc: npcs()) {
            npc.setCanAttack(true);
        }
    }

    private void timerOculto() {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0) {
                if (cli.isHidden()) {
					cli.doPermanecerOculto();
				}
            }
        }
    }

    private void rainEffectTimer() {
        if (!this.raining) {
			return;
		}
        for (Player cli: players()) {
            if (cli != null && cli.getId() > 0) {
                cli.efectoLluvia();
            }
        }
    }

    public void sendToAll(ServerPacket packet) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.isLogged()) {
                cli.sendPacket(packet);
            }
        }
    }

    public void sendToAdmins(ServerPacket packet) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.flags().isGM() && cli.isLogged()) {
                cli.sendPacket(packet);
            }
        }
    }

    public void sendMessageToAdmins(String msg, FontType fuente) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.flags().isGM() && cli.isLogged()) {
                cli.sendMessage(msg, fuente);
            }
        }
    }

    long minutosLloviendo = 0;
    long minutosSinLluvia = 0;

    public void rainStart() {
        this.raining = true;
        this.minutosSinLluvia = 0;
        sendToAll(new RainToggleResponse());
    }

    public void rainStop() {
        this.raining = false;
        this.minutosSinLluvia = 0;
        sendToAll(new RainToggleResponse());
    }

    private void rainEvent() {
        if (!this.raining) {
            this.minutosSinLluvia++;
            if (this.minutosSinLluvia >= 15 && this.minutosSinLluvia < 1440) {
                if (Util.Azar(1, 100) <= 10) {
                    rainStart();
                }
            } else if (this.minutosSinLluvia >= 1440) {
                rainStart();
            }
        } else {
            this.minutosLloviendo++;
            if (this.minutosLloviendo >= 5) {
                rainStop();
            } else {
                if (Util.Azar(1, 100) <= 7) {
                    rainStop();
                }
            }
        }
    }

    int segundos = 0;
    public void piqueteTimer() {
        this.segundos += 6;
        for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.isLogged()) {
                Map mapa = getMap(cli.pos().map);
                if (mapa.isAntiPiquete(cli.pos().x, cli.pos().y)) {
                    cli.counters().PiqueteC++;
                    cli.sendMessage("Estas obstruyendo la via p�blica, mu�vete o ser�s encarcelado!!!", FontType.FONTTYPE_INFO);
                    if (cli.counters().PiqueteC > 23) {
                        cli.counters().PiqueteC = 0;
                        cli.sendToJail(3, null);
                    }
                } else {
                    if (cli.counters().PiqueteC > 0) {
						cli.counters().PiqueteC = 0;
					}
                }
                if (this.segundos >= 18) {
                    cli.counters().Pasos = 0;
                }
            }
        }
        if (this.segundos >= 18) {
			this.segundos = 0;
		}
    }

    public void purgePenalties() {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.flags().UserLogged) {
                if (cli.counters().Pena > 0) {
                    cli.counters().Pena--;
                    if (cli.counters().Pena < 1) {
                        cli.counters().Pena = 0;
                        cli.warpMe(WP_LIBERTAD.map, WP_LIBERTAD.x, WP_LIBERTAD.y, true);
                        cli.sendMessage("Has sido liberado!", FontType.FONTTYPE_INFO);
                    }
                }
            }
        }
    }

    private void checkIdleUser() {
    	for (Player player: players()) {
            if (player != null && player.getId() > 0 && player.isLogged()) {
                player.counters().IdleCount++;
                if (player.counters().IdleCount >= IdleLimit) {
                    player.sendError("Demasiado tiempo inactivo. Has sido desconectado.");
                    player.quitGame();
                }
            }
        }
    }

    long minutos = 0;
    long minsRunning = 0;
    long minutosLatsClean = 0;
    long minsSocketReset  = 0;
    long minsPjesSave = 0;
    long horas = 0;
    DailyStats dayStats = new DailyStats();

    private void autoSaveTimer() {
        // fired every minute
    	
    	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//    	System.out.println("[autoSaveTimer] " + df.format(new Date()));
    	
        this.minsRunning++;
        if (this.minsRunning == 60) {
            this.horas++;
            if (this.horas == 24) {
                saveDayStats();
                this.dayStats.reset();
                getGuildMngr().dayElapsed();
                this.horas = 0;
            }
            this.minsRunning = 0;
        }
        this.minutos++;
        if (this.minutos >= IntervaloMinutosWs) {
            doBackup();
            this.minutos = 0;
        }
        if (this.minutosLatsClean >= 15) {
            this.minutosLatsClean = 0;
            reSpawnOrigPosNpcs(); // respawn de los guardias en las pos originales
            limpiarMundo();
        } else {
            this.minutosLatsClean++;
        }
        purgePenalties();
        checkIdleUser();
        // <<<<<-------- Log the number of users online ------>>>
        log.info("Usuarios conectados: " + getUsuariosConectados().size() + " GMs:" + getGMsOnline().size());
        // <<<<<-------- Log the number of users online ------>>>
    }

    private void loadBlacksmithingWeapons() {
    	log.trace("loading blacksmithing weapons");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "ArmasHerrero.dat");
            short cant = ini.getShort("INIT", "NumArmas");
            this.armasHerrero = new short[cant];
            log.debug("ArmasHerreria cantidad=" + cant);
            for (int i = 0; i < cant; i++) {
                this.armasHerrero[i] = ini.getShort("Arma" + (i+1), "Index");
                log.debug("ArmasHerrero[" + i + "]=" + this.armasHerrero[i]);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBlacksmithingArmors() {
    	log.trace("loading backsmithing armors");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "ArmadurasHerrero.dat");
            short cant = ini.getShort("INIT", "NumArmaduras");
            log.debug("ArmadurasHerrero cantidad=" + cant);
            this.armadurasHerrero = new short[cant];
            for (int i = 0; i < cant; i++) {
                this.armadurasHerrero[i] = ini.getShort("Armadura" + (i+1), "Index");
                log.debug("ArmadurasHerrero[" + i + "]=" + this.armadurasHerrero[i]);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCarpentryObjects() {
    	log.trace("loading carpentry objects");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "ObjCarpintero.dat");
            short cant = ini.getShort("INIT", "NumObjs");
            log.debug("ObjCarpintero cantidad=" + cant);
            this.objCarpintero = new short[cant];
            for (int i = 0; i < cant; i++) {
                this.objCarpintero[i] = ini.getShort("Obj" + (i+1), "Index");
                log.debug("ObjCarpintero[" + i + "]=" + this.objCarpintero[i]);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readHelp() {
        String[] lineas = null;
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "Help.dat");
            short cant = ini.getShort("INIT", "NumLines");
            lineas = new String[cant];
            for (int i = 0; i < cant; i++) {
                lineas[i] = ini.getString("Help", "Line" + (i+1));
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return lineas;
    }

    public void sendMessageToGMs(String msg) {
        for (Player cli: players()) {
            if (cli.isLogged() && cli.flags().isGM()) {
                cli.sendMessage(msg, FontType.FONTTYPE_GM);
            }
        }
    }

    public void logBan(String bannedUser, String gm, String motivo) {
        /////////// FIXME
        /////////// FIXME
        /////////// FIXME
        // Sub LogBan(ByVal BannedIndex As Integer, ByVal UserIndex As Integer, ByVal motivo As String)
        System.out.println(" BAN: " + bannedUser + " by " + gm + " reason: " + motivo);
    }

    public void unBan(String usuario) {
        /////////// FIXME
        /////////// FIXME
        /////////// FIXME
        // Public Function UnBan(ByVal Name As String) As Boolean
        System.out.println(" UNBAN: " + usuario);
    }

    //--- fixme ------ fixme ------ fixme ------ fixme ------ fixme ------ fixme ---
    public void doBackup() {
    	// FIXME
        this.doingBackup = true;
        //enviarATodos(MSG_BKW);
      //  enviarATodos(MSG_TALK, "Servidor> Realizando WorldSave..." + FontType.SERVER);
        saveGuildsDB();
        limpiarMundo();
        worldSave();
      //  enviarATodos(MSG_TALK, "Servidor> WorldSave terminado." + FontType.SERVER);
      //  enviarATodos(MSG_BKW);
        /*********** FIXME
        estadisticasWeb.Informar(EVENTO_NUEVO_CLAN, 0)
         ******************/
        this.doingBackup = false;
        log.info("Backup completado con exito");
    }

    private void saveGuildsDB() {
        /////////// FIXME
    }

    public void limpiarMundo(Player cli) {
    	int cant = limpiarMundo();
        if (cli != null) {
			cli.sendMessage("Servidor> Limpieza del mundo completa. Se eliminaron " + cant + " m_objetos.", FontType.FONTTYPE_SERVER);
		}
    }

    private int limpiarMundo() {
    	int cant = 0;
    	for (MapPos pos: this.trashCollector) {
            Map mapa = getMap(pos.map);
            mapa.quitarObjeto(pos.x, pos.y);
            cant++;
        }
        this.trashCollector.clear();
        return cant;
    }

    private synchronized void worldSave() {
    	// FIXME
        //enviarATodos("||%%%% POR FAVOR ESPERE, INICIANDO WORLDSAVE %%%%" + FontType.FONTTYPE_INFO);
        // Hacer un respawn de los guardias en las pos originales.
        reSpawnOrigPosNpcs();
        // Ver cuantos m_mapas necesitan backup.
        int cant = 0;
        for (Map mapa: this.maps) {
            if (mapa.backup) {
				cant++;
			}
        }
        // Guardar los m_mapas
        this.feedback.init("Guardando m_mapas modificados", cant);
        int i = 0;
        for (Map mapa: this.maps) {
            if (mapa.backup) {
                mapa.saveMapData();
                this.feedback.step("Mapa " + (++i));
            }
        }
        this.feedback.finish();
        // Guardar los NPCs
        try {
            IniFile ini = new IniFile();
            for (Npc npc: npcs()) {
                if (npc.getBackup()) {
                    npc.backup(ini);
                }
            }
            // Guardar todo
            ini.store("worldBackup" + File.separator + "backNPCs.dat");
        } catch (Exception e) {
            log.fatal("worldSave(): ERROR EN BACKUP NPCS", e);
        }
        //enviarATodos("||%%%% WORLDSAVE LISTO %%%%" + FontType.FONTTYPE_INFO);
    }

    private void reSpawnOrigPosNpcs() {
        List<Npc> spawnNPCs = new ArrayList<>();
        for (Npc npc: npcs()) {
            if (npc.isNpcActive()) {
                if (npc.getNumero() == GUARDIAS && npc.getOrig().isValid()) {
                    npc.quitarNPC(); // fixme, lo elimina del server??? revisar.
                    spawnNPCs.add(npc);
                } else if (npc.counters().TiempoExistencia > 0) {
                    npc.muereNpc(null);
                }
            }
        }
        for (Npc npc: spawnNPCs) {
            npc.reSpawnNpc();
        }
    }

    private void saveDayStats() {
        SimpleDateFormat df_dia = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df_xml = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat df_hora = new SimpleDateFormat("HH:mm:ss");
        java.util.Date fecha = new java.util.Date();
        String dia = df_dia.format(fecha);
        String hora = df_hora.format(fecha);
        String filename = "logs" + File.separator + "stats-" + df_xml.format(fecha) + ".xml";

        try (BufferedWriter  f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)))) {
            f.write("<stats>");
            f.write("<ao>");
            f.write("<dia>" + dia + "</dia>");
            f.write("<hora>" + hora + "</hora>");
            f.write("<segundos_total>" + this.dayStats.segundos + "</segundos_total>");
            f.write("<max_user>" + this.dayStats.maxUsuarios + "</max_user>");
            f.write("</ao>");
            f.write("</stats>\n");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

	public ServerStatus serverStatus() {
		var status = new ServerStatus();
		status.uptime = calculateUptime();
		status.usersOnline = getUsuariosConectados().size();
		status.memoryStatus = memoryStatus();
		return status;
	}
	
	class ServerStatus {
		String uptime;
		int usersOnline;
		String memoryStatus;
	}

 
	private static boolean loadBackup = false;
    public static void main(String[] args) {
        loadBackup = !(args.length > 0 && args[0].equalsIgnoreCase("reset"));
        if (loadBackup) {
			log.info("Arrancando usando el backup");
		} else {
			log.info("Arrancando sin usar backup");
		}
        GameServer.instance().runGameLoop();
    }

}
