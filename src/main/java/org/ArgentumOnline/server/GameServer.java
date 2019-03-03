/**    
 * GameServer.java
 *
 * Created on 6 de septiembre de 2003, 19:05
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.ArgentumOnline.server.forum.ForumManager;
import org.ArgentumOnline.server.gm.Admins;
import org.ArgentumOnline.server.gm.Motd;
import org.ArgentumOnline.server.guilds.GuildManager;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.net.NettyServer;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.net.upnp.NetworkUPnP;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcLoader;
import org.ArgentumOnline.server.protocol.RainToggleResponse;
import org.ArgentumOnline.server.quest.Quest;
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
	
	public ManageServer manager = new ManageServer(this);
	
    private boolean useUPnP = false; // FIXME configure this

    private HashMap<Short, Player> players = new HashMap<>();
    private HashMap<Integer, Npc> npcs = new HashMap<>();
    
    private List<Player> 	playersToDrop = new LinkedList<>();
    private List<Short> 	deadNpcs = new LinkedList<>();
    
    private List<Spell> 	spells = new LinkedList<>();
    private List<Map> 		maps = new LinkedList<>();    
    private List<Quest> 	quests = new LinkedList<>();
    
    private List<MapPos> trashCollector = new LinkedList<>();
    
    private short[] m_armasHerrero;
    private short[] m_armadurasHerrero;
    private short[] m_objCarpintero;
    
    boolean m_corriendo = false;
    boolean m_haciendoBackup = false;
    
    short id = 0;
    
    boolean m_lloviendo = false;
    
    private MapPos[] m_ciudades;
    
    private long startTime = 0;
    
    public static MapPos WP_PRISION = MapPos.mxy(66, 75, 47);
    public static MapPos WP_LIBERTAD = MapPos.mxy(66, 75, 65);
    
    private boolean m_showDebug = false;
    
    private GuildManager guildManager;
    private Motd motd;
    private ForumManager forumManager;
    private NpcLoader npcLoader;
    private Admins admins;
    private ObjectInfoStorage objectInfoStorage;
    private GamblerStats gamblerStats;

    private Feedback m_feedback = new Feedback();
    
    private NettyServer ns;
    
    private GameServer() {
    	this.ns = new NettyServer(Constants.SERVER_PORT);
    	this.guildManager = new GuildManager(this);
    	this.motd = new Motd();
    	this.forumManager = new ForumManager();
    	this.npcLoader = new NpcLoader(this);
    	this.admins = new Admins(this);
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
		return gamblerStats;
	}

    public ObjectInfoStorage getObjectInfoStorage() {
		return objectInfoStorage;
	}
    
    public Admins getAdmins() {
		return admins;
	}
    
    public Motd getMotd() {
    	return this.motd;
    }
    
    public GuildManager getGuildMngr() {
    	return this.guildManager;
    }
    
    public ForumManager getForumManager() {
		return forumManager;
	}
    
    public NpcLoader getNpcLoader() {
		return npcLoader;
	}
    
    public List<Player> players() {
    	return this.players.values().stream()
    			.collect(Collectors.toList());
    }
    
    public List<Npc> npcs() {
    	return new ArrayList<Npc>(this.npcs.values());
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
				.append(" días, ")
				.append(horas)
				.append(" horas, ")
				.append(mins)
				.append(" mins, ")
				.append(segs)
				.append(" segs.").toString();
	}
    
    public short nextId() {
        return ++this.id;
    }
    
    public boolean isRaining() {
        return this.m_lloviendo;
    }
    
    public boolean isDoingBackup() {
        return this.m_haciendoBackup;
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
        return this.m_armasHerrero;
    }
    
    public short[] getArmadurasHerrero() {
        return this.m_armadurasHerrero;
    }
    
    public short[] getObjCarpintero() {
        return this.m_objCarpintero;
    }
    
    public boolean isShowDebug() {
    	return this.m_showDebug;
    }
    
    public void setShowDebug(boolean value) {
    	this.m_showDebug = value;
    }
    
    public List<String> getUsuariosConectados() {
    	return players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && !c.isGM())
			    	.map(Player::getNick)
			    	.collect(Collectors.toList());
    }
    
    public void echarPjsNoPrivilegiados() {
    	var users = players().stream()
			    	.filter(c -> c.isLogged() && c.hasNick() && !c.isGM())
			    	.collect(Collectors.toList());
    	
    	users.forEach(c -> {
	    	c.enviarMensaje("Servidor> Conexiones temporalmente cerradas por mantenimiento.", FontType.FONTTYPE_SERVER);
	        c.doSALIR();
    	});
    }
	
    public List<String> getUsuariosTrabajando() {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.isWorking())
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
    }
    
    public void shutdown() {
        this.m_corriendo = false;
        this.ns.shutdown();
        System.out.println("=== Goodbye. Server closed. ===");
    }
    
    public List<String> getUsuariosConIP(String ip) {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.getIP().equals(ip))
		    	.map(Player::getNick)
		    	.collect(Collectors.toList());
    }
    
    public List<String> getGMsOnline() {
    	return players().stream()
		    	.filter(c -> c.isLogged() && c.hasNick() && c.isGM())
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
            this.m_corriendo = true;
            this.manager.start();
            
            int fps = 0;
            long worstTime = 0;
        	// Main loop.
            while (this.m_corriendo) {
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
                    lluviaTimer();
                    lastLluviaTimer = now;
                }
                if ((now - lastEventTimer) > 60000) {
                    lluviaEvent();
                    lastEventTimer = now;
                }
                if ((now - lastPiqueteTimer) > 6000) {
                    piqueteTimer();
                    lastPiqueteTimer = now;
                }
                if ((now - lastPurgarPenas) > 60000) {
                    purgePenalties();
                    lastPurgarPenas = now;
                }
                if ((now - lastAutoSaveTimer) > 60000) {
                    autoSaveTimer();
                    lastAutoSaveTimer = now;
                }
                if ((now - lastPasarSegundoTimer) > 1000) { // 1 vez x segundo
                	System.out.println("fps: " + fps + " max-time: " + worstTime + 
                			" Conectados: " + getUsuariosConectados().size() + " GMs:" + getGMsOnline().size());
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
                try {
					Thread.sleep(wait);
				} catch (InterruptedException ignore) {
				}
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
    	return  "total=" + (int) (Runtime.getRuntime().totalMemory() / 1024) +
    			" KB free=" + (int) (Runtime.getRuntime().freeMemory() / 1024) +
				" KB"; 
    }
    
    private void loadMaps(boolean loadBackup) {
    	log.trace("loading maps");
        this.maps = new ArrayList<Map>(CANT_MAPAS);
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
        
        this.spells = new ArrayList<Spell>(cant);
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
        this.quests = new ArrayList<Quest>(cant);
        for (short i = 1; i <= cant; i++) {
            Quest quest = new Quest(this, i);
            quest.load(ini);
            this.quests.add(quest);
        }
    }

    /** Load all initial data */
    private void loadAll(boolean loadBackup) {
    	log.trace("loadAllData started");
        objectInfoStorage.loadObjectsFromStorage();
        loadSpells();
        loadMaps(loadBackup);
        loadQuests();
        loadCities();
        loadBlacksmithingWeapons();
        loadBlacksmithingArmors();
        loadCarpentryObjects();
        admins.loadAdminsSpawnableCreatures();
        admins.loadInvalidNamesList();
        admins.loadAdmins();
        motd.loadMotd();
        log.trace("loadAllData ended");
    }
    
    public Npc createNpc(int npcNumber) {
        Npc npc = getNpcLoader().createNpc(npcNumber);
        this.npcs.put(Integer.valueOf(npc.getId()), npc);
        return npc;
    }

    public Player createPlayer(Channel channel) {
        Player player = new Player(channel, this);
        this.players.put(player.getId(), player);    	
        return player;
    }
    
    public Player findPlayer(Channel channel) {
    	return this.players.values().stream()
		    		.filter(p -> p.channel == channel)
		    		.findFirst()
		    		.get();
    }

    public void deleteNpc(Npc npc) {
        this.deadNpcs.add(npc.getId());
    }
    
    public Npc npcById(int npcId) {
        return this.npcs.get(Integer.valueOf(npcId));
    }
    
    public Player playerById(short id) {
        return this.players.get(id);
    }
    
    public Spell getHechizo(int spell) {
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
        if (!this.m_haciendoBackup) {
            var npcs = new ArrayList<Npc>(npcs());
            npcs.stream()
            	.filter(npc -> npc.isNpcActive() && !npc.isStatic())
            	.forEach(npc -> {
	                if (npc.estaParalizado()) {
	                    npc.efectoParalisisNpc();
	                } else {
	                    // Usamos AI si hay algun user en el mapa
	                    if (npc.pos().isValid()) {
	                        Map mapa = getMap(npc.pos().map);
	                        if (mapa != null && mapa.getCantUsuarios() > 0) {
	                            if (!npc.isStatic()) {
	                                npc.doAI();
	                            }
	                        }
	                    }
	                }
            	});
	        this.deadNpcs.stream().map(npc -> npcs.remove(npc));
	        this.deadNpcs.clear();
        }
    }

    private void pasarSegundo() {
        var paraSalir = new LinkedList<Player>();
        for (Player cli: players()) {
            if (cli.m_counters.Saliendo) {
                cli.m_counters.SalirCounter--;
                if (cli.m_counters.SalirCounter <= 0) {
                    paraSalir.add(cli);
                } else {
                	switch (cli.m_counters.SalirCounter) {
                		case 10:
                			cli.enviarMensaje("En " + cli.m_counters.SalirCounter +" segundos se cerrará el juego...", FontType.FONTTYPE_INFO);
                			break;
                		case 3:
                			cli.enviarMensaje("Gracias por jugar Argentum Online. Vuelve pronto.", FontType.FONTTYPE_INFO);
                			break;
                	}
                }
            }
        }
        for (Player cli: paraSalir) {
            cli.doSALIR();
        }
    }
        
    public void guardarUsuarios() {
        this.m_haciendoBackup = true;
        try {
        	// FIXME
            //enviarATodos(MSG_BKW);
            //enviarATodos(MSG_TALK, "Servidor> Grabando Personajes" + FontType.SERVER);
            for (Player cli: players()) {
                if (cli.isLogged()) {
                    cli.userStorage.saveUserToStorage();
                }
            }
           // enviarATodos(MSG_TALK, "Servidor> Personajes Grabados" + FontType.SERVER);
          //  enviarATodos(MSG_BKW);
        } finally {
            this.m_haciendoBackup = false;
        }
    }
    
    private void loadCities() {
    	log.trace("loading cities");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "Ciudades.dat");
            this.m_ciudades = new MapPos[4];
            this.m_ciudades[CIUDAD_NIX] = MapPos.mxy(ini.getShort("NIX", "MAPA"), ini.getShort("NIX", "X"), ini.getShort("NIX", "Y"));
            this.m_ciudades[CIUDAD_ULLA] = MapPos.mxy(ini.getShort("Ullathorpe", "MAPA"), ini.getShort("Ullathorpe", "X"), ini.getShort("Ullathorpe", "Y"));
            this.m_ciudades[CIUDAD_BANDER] = MapPos.mxy(ini.getShort("Banderbill", "MAPA"), ini.getShort("Banderbill", "X"), ini.getShort("Banderbill", "Y"));
            this.m_ciudades[CIUDAD_LINDOS] = MapPos.mxy(ini.getShort("Lindos", "MAPA"), ini.getShort("Lindos", "X"), ini.getShort("Lindos", "Y"));
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public MapPos getCiudadPos(short ciudad) {
        return this.m_ciudades[ciudad];
    }
    
    private void FX_Timer() {
    	for (Map mapa: this.maps) {
            if ((Util.Azar(1, 150) < 12) && (mapa.getCantUsuarios() > 0)) {
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
            npc.setPuedeAtacar(true);
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
    
    private void lluviaTimer() {
        if (!this.m_lloviendo) {
			return;
		}
        for (Player cli: players()) {
            if (cli != null && cli.getId() > 0) {
                cli.efectoLluvia();
            }
        }
    }
    
    public void enviarATodos(ServerPacket packet) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.isLogged()) {
                cli.sendPacket(packet);
            }
        }
    }
    
    public void enviarAAdmins(ServerPacket packet) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.isGM() && cli.isLogged()) {
                cli.sendPacket(packet);
            }
        }
    }
    
    public void enviarMensajeAAdmins(String msg, FontType fuente) {
    	for (Player cli: players()) {
            if (cli != null && cli.getId() > 0 && cli.isGM() && cli.isLogged()) {
                cli.enviarMensaje(msg, fuente);
            }
        }
    }
    
    long minutosLloviendo = 0;
    long minutosSinLluvia = 0;
    
    public void iniciarLluvia() {
        this.m_lloviendo = true;
        this.minutosSinLluvia = 0;
        enviarATodos(new RainToggleResponse());
    }
    
    public void detenerLluvia() {
        this.m_lloviendo = false;
        this.minutosSinLluvia = 0;
        enviarATodos(new RainToggleResponse());
    }
    
    private void lluviaEvent() {
        if (!this.m_lloviendo) {
            this.minutosSinLluvia++;
            if (this.minutosSinLluvia >= 15 && this.minutosSinLluvia < 1440) {
                if (Util.Azar(1, 100) <= 10) {
                    iniciarLluvia();
                }
            } else if (this.minutosSinLluvia >= 1440) {
                iniciarLluvia();
            }
        } else {
            this.minutosLloviendo++;
            if (this.minutosLloviendo >= 5) {
                detenerLluvia();
            } else {
                if (Util.Azar(1, 100) <= 7) {
                    detenerLluvia();
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
                if (mapa.getTrigger(cli.pos().x, cli.pos().y) == 5) {
                    cli.m_counters.PiqueteC++;
                    cli.enviarMensaje("Estas obstruyendo la via pública, muévete o serás encarcelado!!!", FontType.FONTTYPE_INFO);
                    if (cli.m_counters.PiqueteC > 23) {
                        cli.m_counters.PiqueteC = 0;
                        cli.sendToJail(3, null);
                    }
                } else {
                    if (cli.m_counters.PiqueteC > 0) {
						cli.m_counters.PiqueteC = 0;
					}
                }
                if (this.segundos >= 18) {
                    cli.m_counters.Pasos = 0;
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
                if (cli.m_counters.Pena > 0) {
                    cli.m_counters.Pena--;
                    if (cli.m_counters.Pena < 1) {
                        cli.m_counters.Pena = 0;
                        cli.warpMe(WP_LIBERTAD.map, WP_LIBERTAD.x, WP_LIBERTAD.y, true);
                        cli.enviarMensaje("Has sido liberado!", FontType.FONTTYPE_INFO);
                    }
                }
            }
        }
    }
    
    private void checkIdleUser() {
        // FIXME
    	for (Player player: players()) {
            if (player != null && player.getId() > 0 && player.isLogged()) {
                player.m_counters.IdleCount++;
                if (player.m_counters.IdleCount >= IdleLimit) {
                    player.enviarError("Demasiado tiempo inactivo. Has sido desconectado.");
                    player.doSALIR();
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
            ///////// FIXME
            // aClon.VaciarColeccion();
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
        //checkIdleUser();
        // <<<<<-------- Log the number of users online ------>>>
        log.info("Usuarios conectados: " + getUsuariosConectados().size() + " GMs:" + getGMsOnline().size());
        // <<<<<-------- Log the number of users online ------>>>
    }

    /**
     * Cargar armas de herrería
     */
    private void loadBlacksmithingWeapons() {
    	log.trace("loading blacksmithing weapons");
        try {
            IniFile ini = new IniFile(DATDIR + File.separator + "ArmasHerrero.dat");
            short cant = ini.getShort("INIT", "NumArmas");
            this.m_armasHerrero = new short[cant];
            log.debug("ArmasHerreria cantidad=" + cant);
            for (int i = 0; i < cant; i++) {
                this.m_armasHerrero[i] = ini.getShort("Arma" + (i+1), "Index");
                log.debug("ArmasHerrero[" + i + "]=" + m_armasHerrero[i]);
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
            this.m_armadurasHerrero = new short[cant];
            for (int i = 0; i < cant; i++) {
                this.m_armadurasHerrero[i] = ini.getShort("Armadura" + (i+1), "Index");
                log.debug("ArmadurasHerrero[" + i + "]=" + m_armadurasHerrero[i]);
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
            this.m_objCarpintero = new short[cant];
            for (int i = 0; i < cant; i++) {
                this.m_objCarpintero[i] = ini.getShort("Obj" + (i+1), "Index");
                log.debug("ObjCarpintero[" + i + "]=" + m_objCarpintero[i]);
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

    public void enviarMensajeALosGMs(String msg) {
        for (Player cli: players()) {
            if (cli.isLogged() && cli.isGM()) {
                cli.enviarMensaje(msg, FontType.FONTTYPE_GM);
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
        this.m_haciendoBackup = true;
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
        this.m_haciendoBackup = false;
        log.info("Backup completado con exito");
    }
    
    private void saveGuildsDB() {
        /////////// FIXME
    }
    
    public void limpiarMundo(Player cli) {
    	int cant = limpiarMundo();
        if (cli != null) {
			cli.enviarMensaje("Servidor> Limpieza del mundo completa. Se eliminaron " + cant + " m_objetos.", FontType.FONTTYPE_SERVER);
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
            if (mapa.m_backup) {
				cant++;
			}
        }
        // Guardar los m_mapas
        this.m_feedback.init("Guardando m_mapas modificados", cant);
        int i = 0;
        for (Map mapa: this.maps) {
            if (mapa.m_backup) {
                mapa.saveMapData();
                this.m_feedback.step("Mapa " + (++i));
            }
        }
        this.m_feedback.finish();
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
        List<Npc> spawnNPCs = new ArrayList<Npc>();
        for (Npc npc: npcs()) {
            if (npc.isNpcActive()) {
                if (npc.getNumero() == GUARDIAS && npc.getOrig().isValid()) {
                    npc.quitarNPC(); // fixme, lo elimina del server??? revisar.
                    spawnNPCs.add(npc);
                } else if (npc.getContadores().TiempoExistencia > 0) {
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
        BufferedWriter f = null;
        try {
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)));
            f.write("<stats>");
            f.write("<ao>");
            f.write("<dia>" + dia + "</dia>");
            f.write("<hora>" + hora + "</hora>");
            f.write("<segundos_total>" + this.dayStats.segundos + "</segundos_total>");
            f.write("<max_user>" + this.dayStats.maxUsuarios + "</max_user>");
            f.write("</ao>");
            f.write("</stats>\n");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();    
        } finally {
            try { if (f != null) {
				f.close();
			} } 
            catch (java.io.IOException e) { e.printStackTrace(); }
        }
    }
    
	public void showStatus() {
		System.out.println("Server uptime: " + calculateUptime());
		System.out.println("Usuarios conectados: " + getUsuariosConectados().size() + " GMs:" + getGMsOnline().size());
		System.out.println("Memoria: " + memoryStatus());
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
