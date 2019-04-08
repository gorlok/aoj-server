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
import org.argentumonline.server.gm.BannIP;
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
import org.argentumonline.server.user.Spell;
import org.argentumonline.server.user.User;
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

    private HashMap<Short, User> users = new HashMap<>();
    private HashMap<Short, Npc> npcs = new HashMap<>();

	private List<User> usersToDrop = new LinkedList<>();
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
    private BannIP bannIP;
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
    	this.bannIP = new BannIP(this);
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
    
    public BannIP getBannIP() {
		return this.bannIP;
	}

    public NpcLoader getNpcLoader() {
		return this.npcLoader;
	}

    public List<User> getUsers() {
    	return this.users.values().stream()
    			.collect(Collectors.toList());
    }

    public List<Npc> getNpcs() {
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
    	} while (users.containsKey(this.lastId) || npcs.containsKey(this.lastId));
    	
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
    	return getUsers().stream()
			    	.filter(c -> c.isLogged() && c.hasUserName() && !c.flags().isGM())
			    	.map(User::getUserName)
			    	.collect(Collectors.toList());
    }

    public void echarPjsNoPrivilegiados() {
    	var users = getUsers().stream()
			    	.filter(c -> c.isLogged() && c.hasUserName() && !c.flags().isGM())
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
    	return getUsers().stream()
		    	.filter(c -> c.isLogged() && c.hasUserName() && c.getIP().equals(ip))
		    	.map(User::getUserName)
		    	.collect(Collectors.toList());
    }

    public List<String> getGMsOnline() {
    	return getUsers().stream()
		    	.filter(c -> c.isLogged() && c.hasUserName() && c.flags().isGM())
		    	.map(User::getUserName)
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
                
                removeDroppedUsers();

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

    public synchronized void dropUser(User user) {
    	user.closeConnection();
    	this.usersToDrop.add(user);
    }

    private synchronized void removeDroppedUsers() {
    	// Se hace aqui para evitar problemas de concurrencia
    	this.usersToDrop.stream().forEach(u -> this.users.remove(u.getId()));
    	this.usersToDrop.clear();
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
        getBannIP().loadBannedIPList();
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

    public void createUser(Channel channel) {
        User user = new User(this);
        user.setChannel(channel);
        
        if (getBannIP().isIpBanned(user.getIP())) {
        	user.sendError("Su IP se encuentra bloqueada en este servidor.");
        	user.quitGame();
        	return;
        }
        
        this.users.put(user.getId(), user);
    }

    public Optional<User> findUser(Channel channel) {
    	return this.users.values().stream()
		    		.filter(p -> p.getChannel() == channel)
		    		.findFirst();
    }

    public void deleteNpc(Npc npc) {
        this.deadNpcs.add(npc.getId());
    }

    public Npc npcById(short npcId) {
        return this.npcs.get(npcId);
    }

    public User userById(short id) {
        return this.users.get(id);
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
    
    public User userByName(String userName) {
    	if ("".equals(userName)) {
			return null;
		}
    	
    	Optional<User> founded = getUsers().stream()
    		.filter(u -> userName.equalsIgnoreCase(u.getUserName()))
    		.findFirst();
    	
    	return founded.isPresent() ? founded.get() : null;
    }

    public boolean isUserAlreadyConnected(String userName) {
    	User foundedUser = userByName(userName);
    	return foundedUser != null &&
    			userName.equalsIgnoreCase(foundedUser.getUserName());
    }

    private void npcAiTimer(long now) {
        // TIMER_AI_Timer()
        if ((now - lastNpcAI) > 400) {
            lastNpcAI = now;
    	
	        if (!this.doingBackup) {
	            getNpcs().stream()
	            	.filter(npc -> npc.isNpcActive() && !npc.isStatic())
	            	.forEach(npc -> {
		                if (npc.isParalized()) {
		                    npc.efectoParalisisNpc();
		                } else {
		                    // Usamos AI si hay algun user en el mapa
		                    if (npc.pos().isValid()) {
		                        Map map = getMap(npc.pos().map);
		                        if (map != null && map.getUsersCount() > 0) {
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
	        List<User> readyToQuit = new LinkedList<>();
	        
	        getUsers().stream().forEach(u -> {
	            if (u.counters().Saliendo) {
	                u.counters().SalirCounter--;
	                if (u.counters().SalirCounter <= 0) {
	                    readyToQuit.add(u);
	                } else {
	                	switch (u.counters().SalirCounter) {
	                		case 10:
	                			u.sendMessage("En " + u.counters().SalirCounter +" segundos se cerrará el juego...", FontType.FONTTYPE_INFO);
	                			break;
	                		case 3:
	                			u.sendMessage("Gracias por jugar Argentum Online. Vuelve pronto.", FontType.FONTTYPE_INFO);
	                			break;
	                	}
	                }
	            }
	        });
	        readyToQuit.stream().forEach(User::quitGame);
	        
	        getWorkWatcher().passSecond();
        }
    }

    public void saveUsers() {
        this.doingBackup = true;
    	getUsers().stream()
    		.filter(User::isLogged)
    		.forEach(User::saveUser);
        this.doingBackup = false;
    }

    private void loadCities() {
    	log.trace("loading cities");
        try {
            IniFile ini = new IniFile(DAT_DIR + File.separator + "Ciudades.dat");
            this.cities = new MapPos[City.values().length];
            loadCity(ini, City.NIX, "NIX");
            loadCity(ini, City.ULLATHORPE, "Ullathorpe");
            loadCity(ini, City.BANDERBILL, "Banderbill");
            loadCity(ini, City.LINDOS, "Lindos");
            loadCity(ini, City.ARGHAL, "Arghal");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

	private void loadCity(IniFile ini, City ciudad, String section) {
		this.cities[ciudad.id()] = 
				MapPos.mxy(ini.getShort(section, "MAPA"), 
						ini.getShort(section, "X"), 
						ini.getShort(section, "Y"));
	}

    public MapPos getCiudadPos(City ciudad) {
        return this.cities[ciudad.id()];
    }

    private void soundFxTimer(long now) {
        if ((now - lastFX) > 200) {
            lastFX = now;
            maps.stream()
            	.filter(Map::isHasUsers)
            	.forEach(m -> {
            		if (Util.random(1, 150) < 12) {
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
            
            getUsers().stream()
            	.filter(User::hasId)
            	.forEach(User::procesarEventos);
        }
    }

    private void npcAtacaTimer(long now) {
        if ((now - lastNpcAtacaTimer) > 2000) {
            lastNpcAtacaTimer = now;
    	
            getNpcs().stream().forEach(Npc::startAttacking);
        }
    }

    private void hiddingTimer(long now) {
        if ((now - lastTimerOculto) > 3_000) {
            lastTimerOculto = now;
            
            getUsers().stream()
            	.filter(User::hasId)
            	.filter(User::isHidden)
            	.forEach(User::updateHiding);
        }
    }

    private void rainingEffectTimer(long now) {
    	if (!this.raining) {
    		return;
    	}
        if ((now - lastLluviaTimer) > 1500) {
            lastLluviaTimer = now;

            getUsers().stream()
            	.filter(User::hasId)
            	.forEach(User::rainingEffect);
        }
    }

    public void sendToAll(ServerPacket packet) {
    	getUsers().stream()
	    	.filter(User::hasId)
	    	.filter(User::isLogged)
	    	.forEach(u -> u.sendPacket(packet));
    }

    public void sendToAdmins(ServerPacket packet) {
    	getUsers().stream()
	    	.filter(User::hasId)
	    	.filter(User::isLogged)
	    	.filter(User::isGM)
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
	                if (Util.random(1, 100) <= 10) {
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
	                if (Util.random(1, 100) <= 7) {
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

            getUsers().stream()
		    	.filter(User::hasId)
		    	.filter(User::isLogged)
		    	.forEach(User::checkPiquete);
        }
    }

    public void purgePenaltiesTimer(long now) {
        if ((now - lastPurgarPenas) > 60_000) {
            lastPurgarPenas = now;
            
            getUsers().stream()
		    	.filter(User::hasId)
		    	.filter(User::isLogged)
		    	.forEach(User::checkPenalties);
        }
    }

    private void checkIdleUserTimer(long now) {
        if ((now - lastCheckIdleUser) > 60_000) {
            lastCheckIdleUser = now;
    	
            getUsers().stream()
		    	.filter(User::hasId)
		    	.filter(User::isLogged)
		    	.forEach(User::checkIdle);
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
    	getUsers().stream()
			.filter(p -> p.isLogged() && p.isGM())
			.forEach(p -> p.sendMessage(msg, FontType.FONTTYPE_GM));
    }

    public void sendMessageToRoleMasters(String msg) {
    	getUsers().stream()
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

    public void cleanWorld(User admin) {
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
        // Ver cuántos mapas necesitan backup.
        
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
            getNpcs().stream()
            	.filter(Npc::isBackup)
            	.forEach(npc -> npc.backupNpc(ini));

            // Guardar todo
            ini.store("worldBackup" + File.separator + "backNPCs.dat");
        } catch (Exception e) {
            log.fatal("worldSave(): ERROR EN BACKUP NPCS", e);
        }
        sendToAll(new ConsoleMsgResponse("Servidor> WorldSave ha concluído", 
        		FontType.FONTTYPE_SERVER.id()));
    }

    private void reSpawnOrigPosNpcs() {
        List<Npc> spawnNPCs = new ArrayList<>();
        getNpcs().stream()
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
    
    
	/**
	 * Broadcast a server message
	 * @param admin sending a server message
	 * @param message to broadcast to all connected users
	 */
	public void sendServerMessage(User admin, String message) {
		// Comando /RMSG
		if (!admin.isGM()) {
			return;
		}
		Log.logGM(admin.getUserName(), "Mensaje Broadcast: " + message);
		if (!message.equals("")) {
			if (admin.flags().isGM()) {
				Log.logGM(admin.getUserName(), "Mensaje Broadcast:" + message);
				sendToAll(new ConsoleMsgResponse(message, FontType.FONTTYPE_TALK.id()));
			}
		}
	}
	
    public void sendMessageToAdmins(User admin, String message, FontType fuente) {
		if (!admin.isGM()) {
			return;
		}
		getUsers().stream()
			.filter(u -> u != null && u.getId() > 0 && u.isLogged() && u.isGM())
			.forEach(u -> u.sendMessage(message, fuente));
    }

    public void sendMessageToRoyalArmy(User admin, String message) {
		if (!admin.isGM()) {
			return;
		}
		getUsers().stream()
			.filter(u -> u != null && u.getId() > 0 && u.isLogged()
					&& (u.flags().isGM() || u.isRoyalArmy()))
			.forEach(u -> u.sendMessage("ARMADA REAL> " + message, FontType.FONTTYPE_TALK));
    }

    public void sendMessageToDarkLegion(User admin, String message) {
		if (!admin.isGM()) {
			return;
		}
		getUsers().stream()
			.filter(u -> u != null && u.getId() > 0 && u.isLogged()
					&& (u.flags().isGM() || u.isDarkLegion()))
			.forEach(u -> u.sendMessage("LEGION OSCURA> " + message, FontType.FONTTYPE_TALK));
    }

    public void sendMessageToCitizens(User admin, String message) {
		if (!admin.isGM()) {
			return;
		}
		getUsers().stream()
			.filter(u -> u != null && u.getId() > 0 && u.isLogged() && !u.isCriminal())
			.forEach(u -> u.sendMessage("CIUDADANOS> " + message, FontType.FONTTYPE_TALK));
    }

    public void sendMessageToCriminals(User admin, String message) {
		if (!admin.isGM()) {
			return;
		}
		getUsers().stream()
			.filter(u -> u != null && u.getId() > 0 && u.isLogged() && u.isCriminal())
			.forEach(u -> u.sendMessage("CRIMINALES> " + message, FontType.FONTTYPE_TALK));
    }
    
    public void sendCouncilMessage(User user, String message) {
    	if (user.isRoyalCouncil()) {
    		getUsers().stream()
	    		.filter(u -> u != null && u.getId() > 0 && u.isLogged() && u.isRoyalCouncil())
	    		.forEach(u -> u.sendMessage("(Consejero) " + user.getUserName() + " > " + message,
	    				FontType.FONTTYPE_CONSEJO));
    	} else if (user.isChaosCouncil()) {
    		getUsers().stream()
	    		.filter(u -> u != null && u.getId() > 0 && u.isLogged() && u.isChaosCouncil())
	    		.forEach(u -> u.sendMessage("(Consejero) " + user.getUserName() + " > " + message,
    				FontType.FONTTYPE_CONSEJOCAOS));
    	}
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

    public void reloadObjects(User admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
    	this.objectInfoStorage.loadObjectsFromStorage();
    	admin.sendMessage("Se han recargado los objetos", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getUserName(), admin.getUserName() + " ha recargado los objetos.");
    }

	public void reloadSpells(User admin) {
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		loadSpells();
    	admin.sendMessage("Se han recargado los hechizos", FontType.FONTTYPE_INFO);
    	Log.logGM(admin.getUserName(), admin.getUserName() + " ha recargado los hechizos.");
	}

}
