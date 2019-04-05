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
package org.argentumonline.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.api.ManagerApi;
import org.argentumonline.server.forum.ForumManager;
import org.argentumonline.server.gm.ManagerServer;
import org.argentumonline.server.gm.Motd;
import org.argentumonline.server.guilds.GuildManager;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.net.NetworkServer;
import org.argentumonline.server.net.ServerPacket;
import org.argentumonline.server.net.upnp.NetworkUPnP;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcLoader;
import org.argentumonline.server.npc.WorkWatcher;
import org.argentumonline.server.protocol.ConsoleMsgResponse;
import org.argentumonline.server.protocol.RainToggleResponse;
import org.argentumonline.server.quest.Quest;
import org.argentumonline.server.user.Player;
import org.argentumonline.server.user.Spell;
import org.argentumonline.server.util.Feedback;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;
import org.argentumonline.server.util.Util;

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
    boolean serverRestrictedToGMs = false;
    boolean createUserEnabled = true;

	private short lastId = 0;

    boolean raining = false;

    private MapPos[] cities;

    private long startTime = 0;

    private boolean showDebug = false;

    private GuildManager guildManager;
    private Motd motd;
    private ForumManager forumManager;
    private NpcLoader npcLoader;
    private ManagerServer manager;
    private ObjectInfoStorage objectInfoStorage;
    private GamblerStats gamblerStats;
    private WorkWatcher workWatcher;
    private NetworkServer ns;

    private Feedback feedback = new Feedback();// FIXME

    int fps = 0;
    long worstTime = 0;

    private long lastPasarSegundoTimer;
    private long lastNpcAI;
    private long lastFX;
    private long lastGameTimer;
    private long lastNpcAtacaTimer;
    private long lastTimerOculto;
    private long lastLluviaTimer;
    private long lastEventTimer;
    private long lastPiqueteTimer;
    private long lastPurgarPenas;
    private long lastCheckIdleUser;
    private long lastPassMinuteTimer;
    
    private static GameServer instance = null;
    public synchronized static GameServer instance() {
    	if (instance == null) {
    		instance = new GameServer();
    	}
    	return instance;
    }

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
    	this.workWatcher = new WorkWatcher(this);
    }
    
    private void init() {
        this.startTime = System.currentTimeMillis();
        
        this.lastPasarSegundoTimer = this.startTime;
        this.lastNpcAI = this.startTime;
        this.lastFX = this.startTime;
        this.lastGameTimer = this.startTime;
        this.lastNpcAtacaTimer = this.startTime;
        this.lastTimerOculto = this.startTime;
        this.lastLluviaTimer = this.startTime;
        this.lastEventTimer = this.startTime;
        this.lastPiqueteTimer = this.startTime;
        this.lastPurgarPenas = this.startTime;
        this.lastCheckIdleUser = this.startTime;
        this.lastPassMinuteTimer = this.startTime;
        
        this.running = true;
    }
    
    public WorkWatcher getWorkWatcher() {
		return workWatcher;
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

    public Motd motd() {
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
        return (System.currentTimeMillis() - this.startTime) / 1000;
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

    public boolean isServerRestrictedToGMs() {
		return serverRestrictedToGMs;
	}
    
    public void setServerRestrictedToGMs(boolean serverRestrictedToGMs) {
		this.serverRestrictedToGMs = serverRestrictedToGMs;
	}
    
	public void serverRestrictedToGMsToggle() {
		this.serverRestrictedToGMs = !this.serverRestrictedToGMs;
	}
	
	public boolean isCreateUserEnabled() {
		return createUserEnabled;
	}
	
	public void setCreateUserEnabled(boolean createUserEnabled) {
		this.createUserEnabled = createUserEnabled;
	}

    /** Main loop of the game. */
    private void runGameLoop() {
        loadAll(loadBackup);

        init();
        try {
        	if (this.useUPnP) {
        		NetworkUPnP.openUPnP();
        	}
            while (this.running) {
                fps++;
                long now = System.currentTimeMillis();
                
                npcAiTimer(now);
                soundFxTimer(now);
                gameTimer(now);
                npcAtacaTimer(now);
                hiddingTimer(now);
                rainingEffectTimer(now);
                rainEventTimer(now);
                piqueteTimer(now);
                purgePenaltiesTimer(now);
                checkIdleUserTimer(now);
                passMinuteTimer(now);
                passSecondTimer(now);
                
                removeDropedPlayers();

                long ellapsed = System.currentTimeMillis() - now;
                if (ellapsed > worstTime) {
                	worstTime = ellapsed;
                }
                long wait = (1000 - ellapsed);
                if (wait < 0) wait = 1;
                if (wait > 40) wait = 40;
                // capped at 60 fps
                Util.sleep(wait);
            }
        } finally {
            backupWorld();
            saveUsers();
            log.info("Server apagado");
        }
    }

    public synchronized void dropPlayer(Player user) {
    	user.closeConnection();
    	this.playersToDrop.add(user);
    }

    private synchronized void removeDropedPlayers() {
    	// Se hace aqui para evitar problemas de concurrencia
    	this.playersToDrop.stream().forEach(u -> this.players.remove(u.getId()));
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
            ini.load(DAT_DIR + java.io.File.separator + "Hechizos.dat");
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
            ini.load(DAT_DIR + java.io.File.separator + "Quests.dat");
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
        this.manager.loadBannedIPList();
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

    public void createPlayer(Channel channel) {
        Player player = new Player(this);
        player.setChannel(channel);
        
        if (manager().getBannedIPs().contains(player.getIP())) {
        	player.sendError("Su IP se encuentra bloqueada en este servidor.");
        	player.quitGame();
        	return;
        }
        
        this.players.put(player.getId(), player);
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
    
    public Player playerByUserName(String userName) {
    	if ("".equals(userName)) {
			return null;
		}
    	
    	Optional<Player> founded = players().stream()
    		.filter(u -> userName.equalsIgnoreCase(u.getNick()))
    		.findFirst();
    	
    	return founded.isPresent() ? founded.get() : null;
    }

    public boolean isPlayerAlreadyConnected(String userName) {
    	Player foundedPlayer = playerByUserName(userName);
    	return foundedPlayer != null &&
    			userName.equalsIgnoreCase(foundedPlayer.getNick());
    }

    private void npcAiTimer(long now) {
        // TIMER_AI_Timer()
        if ((now - lastNpcAI) > 400) {
            lastNpcAI = now;
    	
	        if (!this.doingBackup) {
	            npcs().stream()
	            	.filter(npc -> npc.isNpcActive() && !npc.isStatic())
	            	.forEach(npc -> {
		                if (npc.isParalized()) {
		                    npc.efectoParalisisNpc();
		                } else {
		                    // Usamos AI si hay algun user en el mapa
		                    if (npc.pos().isValid()) {
		                        Map map = getMap(npc.pos().map);
		                        if (map != null && map.getPlayersCount() > 0) {
	                                npc.doAI();
		                        }
		                    }
		                }
	            	});
		        this.deadNpcs.stream().map(npcId -> this.npcs.remove(npcId));
		        this.deadNpcs.clear();
	        }
        }
    }

    private void passSecondTimer(long now) {
        if ((now - lastPasarSegundoTimer) > 1000) { // 1 vez x segundo
//        	System.out.format("fps: %2d   max-time: %3dms    online: %3d    gm: %3d\n", 
//        			fps, worstTime, getUsuariosConectados().size(), getGMsOnline().size());
        	fps = 0;
        	worstTime = 0;
            lastPasarSegundoTimer = now;
	        List<Player> readyToQuit = new LinkedList<>();
	        
	        players().stream().forEach(u -> {
	            if (u.counters().Saliendo) {
	                u.counters().SalirCounter--;
	                if (u.counters().SalirCounter <= 0) {
	                    readyToQuit.add(u);
	                } else {
	                	switch (u.counters().SalirCounter) {
	                		case 10:
	                			u.sendMessage("En " + u.counters().SalirCounter +" segundos se cerrar� el juego...", FontType.FONTTYPE_INFO);
	                			break;
	                		case 3:
	                			u.sendMessage("Gracias por jugar Argentum Online. Vuelve pronto.", FontType.FONTTYPE_INFO);
	                			break;
	                	}
	                }
	            }
	        });
	        readyToQuit.stream().forEach(Player::quitGame);
	        
	        getWorkWatcher().passSecond();
        }
    }

    public void saveUsers() {
        this.doingBackup = true;
    	players().stream()
    		.filter(Player::isLogged)
    		.forEach(Player::saveUser);
        this.doingBackup = false;
    }

    private void loadCities() {
    	log.trace("loading cities");
        try {
            IniFile ini = new IniFile(DAT_DIR + File.separator + "Ciudades.dat");
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

    private void soundFxTimer(long now) {
        if ((now - lastFX) > 200) {
            lastFX = now;
            maps.stream()
            	.filter(Map::isHasPlayers)
            	.forEach(m -> {
            		if (Util.Azar(1, 150) < 12) {
            			m.soundFx();
            		}
            	});
        }
    }

    private void gameTimer(long now) {
    	// This is like GameTimer_Timer
    	// <<<<<< Procesa eventos de los usuarios >>>>>>
        if ((now - lastGameTimer) > 40) {
            lastGameTimer = now;
            
            players().stream()
            	.filter(Player::hasId)
            	.forEach(Player::procesarEventos);
        }
    }

    private void npcAtacaTimer(long now) {
        if ((now - lastNpcAtacaTimer) > 2000) {
            lastNpcAtacaTimer = now;
    	
            npcs().stream().forEach(Npc::startAttacking);
        }
    }

    private void hiddingTimer(long now) {
        if ((now - lastTimerOculto) > 3_000) {
            lastTimerOculto = now;
            
            players().stream()
            	.filter(Player::hasId)
            	.filter(Player::isHidden)
            	.forEach(Player::updateHiding);
        }
    }

    private void rainingEffectTimer(long now) {
    	if (!this.raining) {
    		return;
    	}
        if ((now - lastLluviaTimer) > 1500) {
            lastLluviaTimer = now;

            players().stream()
            	.filter(Player::hasId)
            	.forEach(Player::rainingEffect);
        }
    }

    public void sendToAll(ServerPacket packet) {
    	players().stream()
	    	.filter(Player::hasId)
	    	.filter(Player::isLogged)
	    	.forEach(u -> u.sendPacket(packet));
    }

    public void sendToAdmins(ServerPacket packet) {
    	players().stream()
	    	.filter(Player::hasId)
	    	.filter(Player::isLogged)
	    	.filter(Player::isGM)
	    	.forEach(u -> u.sendPacket(packet));
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

    private void rainEventTimer(long now) {
        if ((now - lastEventTimer) > 60_000) {
            lastEventTimer = now;
    	
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
    }

    public void piqueteTimer(long now) {
    	// check every second
        if ((now - lastPiqueteTimer) > 1_000) {
            lastPiqueteTimer = now;

            players().stream()
		    	.filter(Player::hasId)
		    	.filter(Player::isLogged)
		    	.forEach(Player::checkPiquete);
        }
    }

    public void purgePenaltiesTimer(long now) {
        if ((now - lastPurgarPenas) > 60_000) {
            lastPurgarPenas = now;
            
            players().stream()
		    	.filter(Player::hasId)
		    	.filter(Player::isLogged)
		    	.forEach(Player::checkPenalties);
        }
    }

    private void checkIdleUserTimer(long now) {
        if ((now - lastCheckIdleUser) > 60_000) {
            lastCheckIdleUser = now;
    	
            players().stream()
		    	.filter(Player::hasId)
		    	.filter(Player::isLogged)
		    	.forEach(Player::checkIdle);
        }
    }

    long minutesWorldSave = 0;
    long minutesLastClean = 0;
    long minutesRunning = 0;
    long hoursRunning = 0;
    DailyStats dayStats = new DailyStats();

    private void passMinuteTimer(long now) {
    	// fired every minute
        if ((now - lastPassMinuteTimer) > 60_000) {
            lastPassMinuteTimer = now;
    	
	    	
	        this.minutesRunning++;
	        if (this.minutesRunning == 60) {
	            this.hoursRunning++;
	            if (this.hoursRunning == 24) {
	                saveDayStats();
	                this.dayStats.reset();
	                getGuildMngr().dayElapsed();
	                this.hoursRunning = 0;
	            }
	            this.minutesRunning = 0;
	        }
	        this.minutesWorldSave++;
	        if (this.minutesWorldSave >= IntervaloMinutosWs) {
	            backupWorld();
	            this.minutesWorldSave = 0;
	        }
	        if (this.minutesLastClean >= 15) {
	            this.minutesLastClean = 0;
	            reSpawnOrigPosNpcs(); // respawn de los guardias en las pos originales
	            cleanWorld();
	        } else {
	            this.minutesLastClean++;
	        }
	        getWorkWatcher().passMinute();
	        
	        log.info("Usuarios conectados: " + getUsuariosConectados().size() + " GMs:" + getGMsOnline().size());
        }
    }

    private void loadBlacksmithingWeapons() {
    	log.trace("loading blacksmithing weapons");
        try {
            IniFile ini = new IniFile(DAT_DIR + File.separator + "ArmasHerrero.dat");
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
            IniFile ini = new IniFile(DAT_DIR + File.separator + "ArmadurasHerrero.dat");
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
            IniFile ini = new IniFile(DAT_DIR + File.separator + "ObjCarpintero.dat");
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
            IniFile ini = new IniFile(DAT_DIR + File.separator + "Help.dat");
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
    	players().stream()
			.filter(p -> p.isLogged() && p.isGM())
			.forEach(p -> p.sendMessage(msg, FontType.FONTTYPE_GM));
    }

    public void sendMessageToRoleMasters(String msg) {
    	players().stream()
    		.filter(p -> p.isLogged() && p.isRoleMaster())
    		.forEach(p -> p.sendMessage(msg, FontType.FONTTYPE_GUILDMSG));
    }
    
    //--- fixme ------ fixme ------ fixme ------ fixme ------ fixme ------ fixme ---
    public void backupWorld() {
    	// doBackup
    	// FIXME
        this.doingBackup = true;
        //enviarATodos(MSG_BKW);
      //  enviarATodos(MSG_TALK, "Servidor> Realizando WorldSave..." + FontType.SERVER);
        saveGuildsDB();
        cleanWorld();
        worldSave();
        //modGuilds.v_RutinaElecciones
        getWorkWatcher().reset(); // Reseteamos al Centinela        
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

    public void cleanWorld(Player admin) {
    	// Comando /LIMPIAR
		if (!admin.isGM()) {
			return;
		}
    	int removedObjects = cleanWorld();
        if (admin != null) {
			admin.sendMessage("Servidor> Limpieza del mundo completa. Se eliminaron " + removedObjects + " objetos.", 
					FontType.FONTTYPE_SERVER);
		}
    }

    private int cleanWorld() {
    	this.trashCollector.stream()
    		.forEach(pos -> {
    			getMap(pos.map).removeObject(pos.x, pos.y);
    		});
    	int removedObjects = this.trashCollector.size();
    	this.trashCollector.clear();
        return removedObjects;
    }

    private synchronized void worldSave() {
        sendToAll(new ConsoleMsgResponse("Servidor> Iniciando WorldSave", FontType.FONTTYPE_SERVER.id()));
        // Hacer un respawn de los guardias en las pos originales.
        reSpawnOrigPosNpcs();
        // Ver cu�ntos mapas necesitan backup.
        
        List<Map> mapsToBackup = this.maps.stream()
        	.filter(Map::isBackup)
        	.collect(Collectors.toList());
        
        // Guardar los mapas
        this.feedback.init("Guardando mapas modificados", mapsToBackup.size());
        
        mapsToBackup.stream()
        	.forEach(map -> {
        		map.saveMapBackup();
        		this.feedback.step("Mapa " + map.getMapNumber());
        	});
        this.feedback.finish();
        
        // Guardar los NPCs
        try {
            IniFile ini = new IniFile();
            npcs().stream()
            	.filter(Npc::isBackup)
            	.forEach(npc -> npc.backupNpc(ini));

            // Guardar todo
            ini.store("worldBackup" + File.separator + "backNPCs.dat");
        } catch (Exception e) {
            log.fatal("worldSave(): ERROR EN BACKUP NPCS", e);
        }
        sendToAll(new ConsoleMsgResponse("Servidor> WorldSave ha conclu�do", 
        		FontType.FONTTYPE_SERVER.id()));
    }

    private void reSpawnOrigPosNpcs() {
        List<Npc> spawnNPCs = new ArrayList<>();
        npcs().stream()
        	.filter(Npc::isNpcActive)
        	.forEach(npc -> {
        		if (npc.getNumber() == GUARDIAS && npc.getOrig().isValid()) {
        			npc.quitarNPC(); // FIXME, lo elimina del server??? revisar.
        			spawnNPCs.add(npc);
        		} else if (npc.counters().TiempoExistencia > 0) {
        			npc.muereNpc(null);
        		}
        	});
        spawnNPCs.stream().forEach(Npc::reSpawnNpc);
    }

    private void saveDayStats() {
    	SimpleDateFormat df_xml = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat df_dia = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df_hora = new SimpleDateFormat("HH:mm:ss");
        var fecha = new java.util.Date();
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

    public void reloadObjects(Player admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
    	this.objectInfoStorage.loadObjectsFromStorage();
    	admin.sendMessage("Se han recargado los objetos", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getNick(), admin.getNick() + " ha recargado los objetos.");
    }

	public void reloadSpells(Player admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		loadSpells();
    	admin.sendMessage("Se han recargado los hechizos", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getNick(), admin.getNick() + " ha recargado los hechizos.");
	}

}