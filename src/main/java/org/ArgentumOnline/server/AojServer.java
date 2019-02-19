/**    
 * AojServer.java
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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ArgentumOnline.server.forum.ForumManager;
import org.ArgentumOnline.server.gm.Motd;
import org.ArgentumOnline.server.guilds.GuildManager;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.net.upnp.NetworkUPnP;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcLoader;
import org.ArgentumOnline.server.protocol.ClientProcessThread;
import org.ArgentumOnline.server.protocol.ServerPacketID;
import org.ArgentumOnline.server.quest.Quest;
import org.ArgentumOnline.server.util.Feedback;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
 * AOJ Server main class
 * @author gorlok
 */
public class AojServer implements Constants {
	private static Logger log = LogManager.getLogger();
	
	public ManageServer manager = new ManageServer(this);
	
    private ClientProcessThread processThread;
    
    private boolean useUPnP = true;
    
    private ServerSocketChannel server;
    private Selector selector;
    private final static int BUFFER_SIZE = 1024;
    private ByteBuffer serverBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    
    private HashMap<SocketChannel, Client> m_clientSockets = new HashMap<SocketChannel, Client>();
    private HashMap<Short, Client> m_clientes = new HashMap<Short, Client>();
    private HashMap<Integer, Npc> 	m_npcs       = new HashMap<Integer, Npc>();
    
    private List<Client> m_clientes_eliminar = new LinkedList<Client>();
    private List<Short> m_npcs_muertos = new Vector<Short>();
    
    private List<Spell> 	m_hechizos = new Vector<Spell>();
    private List<Map> 		m_mapas = new Vector<Map>();    
    private List<Quest> 	m_quests = new Vector<Quest>();
    
    private List<MapPos> m_trashCollector = new Vector<MapPos>();
    
    private short [] m_armasHerrero;
    private short [] m_armadurasHerrero;
    private short [] m_objCarpintero;
    
    boolean m_corriendo = false;
    boolean m_haciendoBackup = false;
    
    short id = 0;
    
    boolean m_lloviendo = false;
    
    private MapPos[] m_ciudades;
    
    private long startTime = 0;
    
    public static MapPos WP_PRISION = MapPos.mxy(66, 75, 47);
    public static MapPos WP_LIBERTAD = MapPos.mxy(66, 75, 65);
    
    private boolean m_showDebug = false;
    
    private GuildManager m_guildMngr;
    private Motd motd;
    private ForumManager forumManager;
    private NpcLoader npcLoader;
    private Admins admins;
    private ObjectInfoStorage objectInfoStorage;
    
    private AojServer() {
    	this.m_guildMngr = new GuildManager(this);
    	this.motd = new Motd();
    	this.forumManager = new ForumManager();
    	this.npcLoader = new NpcLoader(this);
    	this.admins = new Admins(this);
    	this.objectInfoStorage = new ObjectInfoStorage();
    }

    private static AojServer instance = null;
    public static AojServer instance() {
        if (instance == null) {
			instance = new AojServer();
		}
        return instance;
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
    	return this.m_guildMngr;
    }
    
    public ForumManager getForumManager() {
		return forumManager;
	}
    
    public NpcLoader getNpcLoader() {
		return npcLoader;
	}
    
    public Collection<Client> getClientes() {
    	return this.m_clientes.values();
    }
    
    public Collection<Npc> getNpcs() {
    	return this.m_npcs.values();
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
		return String.format("%d dias, %d horas, %d mins, %d segs.", dias, horas, mins, segs);
	}
    
    public short getNextId() {
        return ++this.id;
    }
    
    public boolean estaLloviendo() {
        return this.m_lloviendo;
    }
    
    public boolean estaHaciendoBackup() {
        return this.m_haciendoBackup;
    }
    
    public List<MapPos> getTrashCollector() {
        return this.m_trashCollector;
    }
    
    public Quest getQuest(int n) {
        return this.m_quests.get(n - 1);
    }
    
    public int getQuestCount() {
        return this.m_quests.size();
    }
    
    public short [] getArmasHerrero() {
        return this.m_armasHerrero;
    }
    
    public short [] getArmadurasHerrero() {
        return this.m_armadurasHerrero;
    }
    
    public short [] getObjCarpintero() {
        return this.m_objCarpintero;
    }
    
    public boolean isShowDebug() {
    	return this.m_showDebug;
    }
    
    public void setShowDebug(boolean value) {
    	this.m_showDebug = value;
    }
    
    public List<String> getUsuariosConectados() {
        Vector<String> usuarios = new Vector<String>();
        for (Client cli: getClientes()) {
            if (!"".equals(cli.getNick()) && !cli.esGM()) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    public void echarPjsNoPrivilegiados() {
        for (Client cli: getClientes()) {
            if (!"".equals(cli.getNick()) && !cli.esGM() && cli.isLogged()) {
            	cli.enviarMensaje("Servidor> Conexiones temporalmente cerradas por mantenimiento.", FontType.SERVER);
                cli.doSALIR();
            }
        }
    }
	
    public List<String> getUsuariosTrabajando() {
        Vector<String> usuarios = new Vector<String>();
        for (Client cli: getClientes()) {
            if (!"".equals(cli.getNick()) && cli.estaTrabajando()) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    public void shutdown() {
        this.m_corriendo = false;
        this.processThread.endThread();
        System.out.println("=== Goodbye. Server closed. ===");
    }
    
    public List<String> getUsuarioConIP(String ip) {
        Vector<String> usuarios = new Vector<String>();
        for (Client cli: getClientes()) {
            if (!"".equals(cli.getNick()) && cli.getIP().equals(ip)) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    public List<String> getGMsOnline() {
        Vector<String> usuarios = new Vector<String>();
        for (Client cli: getClientes()) {
            if (cli.getNick() != null && cli.getNick().length() > 0 && cli.esGM()) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    private void initServerSocket() 
    throws java.io.IOException {
        this.server = ServerSocketChannel.open();
        this.server.configureBlocking(false);
        this.server.socket().bind(new InetSocketAddress(SERVER_PORT));
        log.info("Escuchando en el puerto " + SERVER_PORT);
        this.selector = Selector.open();
        this.server.register(this.selector, SelectionKey.OP_ACCEPT);
    }
    
    private void acceptConnection() 
    throws java.io.IOException {
        // get client socket channel.
        SocketChannel clientSocket = this.server.accept();
        // Non blocking I/O
        clientSocket.configureBlocking(false);
        // recording to the selector (reading)
        clientSocket.register(this.selector, SelectionKey.OP_READ);
        Client cliente = new Client(clientSocket, this);
        this.m_clientSockets.put(clientSocket, cliente);
        this.m_clientes.put(cliente.getId(), cliente);
        log.info("NUEVA CONEXION");
    }
    
    public void closeConnection(Client cliente) 
    throws java.io.IOException {
        log.info("cerrando conexion");
        
        this.m_clientSockets.remove(cliente.socketChannel);
        this.m_clientes_eliminar.add(cliente);
        cliente.socketChannel.close();
    }
    
    /** Lee datos de una conexión existente. */
    private void readConnection(SocketChannel clientSocket) 
    throws java.io.IOException {
        Client cliente = this.m_clientSockets.get(clientSocket);
        log.info("Recibiendo del cliente: " + cliente);
        // Read bytes coming from the client.
        this.serverBuffer.clear();
        try {
            clientSocket.read(this.serverBuffer);
            
            // process the message.
            this.serverBuffer.flip();
            
            if (this.serverBuffer.limit() > 0) {
            	cliente.lengthClient.add(this.serverBuffer.limit());
            	cliente.colaClient.put(this.serverBuffer.array());
                
                this.processThread.addClientQueue(cliente);
            }else {
            	cliente.doSALIR();
            }
            
        } catch (Exception e) {
            cliente.doSALIR();
            return;
        }       
    }
    
    boolean loadBackup;
    public boolean isLoadBackup() {
		return loadBackup;
	}
    
    /** Main loop of the game. */
    public void run(boolean loadBackup) {
    	this.loadBackup = loadBackup;
        loadAllData(loadBackup);
        
        (this.processThread = new ClientProcessThread()).start();
        
        try {
            initServerSocket();
        	if (this.useUPnP) {
        		NetworkUPnP.openUPnP();
        	}
            // Main loop.
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
            //setupCleanShutdown();
            while (this.m_corriendo) {
                // Wainting for events...
                this.selector.select(40); // milisegundos
                Set<SelectionKey> keys = this.selector.selectedKeys();
                for (SelectionKey key: keys) {
                    // if isAcceptable, then a client required a connection.
                    if (key.isAcceptable()) {
                        log.info("nueva conexion");
                        acceptConnection();
                        continue;
                    }
                    // if isReadable, then the server is ready to read
                    if (key.isReadable()) {
                        log.info("leer de conexion");
                        readConnection((SocketChannel) key.channel());
                        continue;
                    }
                }
                keys.clear();
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
                    purgarPenas();
                    lastPurgarPenas = now;
                }
                if ((now - lastAutoSaveTimer) > 60000) {
                    autoSaveTimer();
                    lastAutoSaveTimer = now;
                }
                if ((now - lastPasarSegundoTimer) > 1000) { // 1 vez x segundo
                    pasarSegundo();
                    lastPasarSegundoTimer = now;
                }
                eliminarClientes();
            }
        } catch (UnknownHostException ex) {
            log.fatal("AOServer run loop", ex);
        } catch (java.net.BindException ex) {
            log.fatal("El puerto ya está en uso. ¿El servidor ya está corriendo?", ex);
        } catch (IOException ex) {
            log.fatal("AOServer run loop", ex);
        } finally {
            doBackup();
            guardarUsuarios();
            log.info("Server apagado por doBackUp");
        }
    }
    
    private synchronized void eliminarClientes() {
    	// La eliminación de m_clientes de la lista de m_clientes se hace aquí
    	// para evitar modificaciones concurrentes al hashmap, entre otras cosas.
    	for (Client cliente: this.m_clientes_eliminar) {
            this.m_clientes.remove(cliente.getId());
    	}
    	this.m_clientes_eliminar.clear();
    }

    private static String memoryStatus() {
    	return  "total=" + (int) (Runtime.getRuntime().totalMemory() / 1024) +
    			" KB free=" + (int) (Runtime.getRuntime().freeMemory() / 1024) +
				" KB"; 
    }
    
    /** Load maps / Cargar los m_mapas */
    private void loadMaps(boolean loadBackup) {
    	log.trace("loading maps");
        this.m_mapas = new Vector<Map>();
        Map mapa;
        for (short i = 1; i <= CANT_MAPAS; i++) {
            mapa = new Map(i, this);
            mapa.load(loadBackup);
            this.m_mapas.add(mapa);
        }
    }
    
    /** Load spells / Cargar los hechizos */
    private void loadSpells() {
    	log.trace("loading spells");
        this.m_hechizos = new Vector<Spell>();
        IniFile ini = new IniFile();
        try {
            ini.load(DATDIR + java.io.File.separator + "Hechizos.dat");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        int cant = ini.getShort("INIT", "NumeroHechizos");
        
        for (int i = 0; i < cant; i++) {
            Spell hechizo = new Spell(i+1);
        	hechizo.load(ini);
            this.m_hechizos.add(hechizo);
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
        this.m_quests = new Vector<Quest>();        
        for (short i = 1; i <= cant; i++) {
            Quest quest = new Quest(this, i);
            quest.load(ini);
            this.m_quests.add(quest);
        }
    }

    /** Load all initial data / Cargar todos los datos iniciales */
    private void loadAllData(boolean loadBackup) {
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
        this.m_npcs.put(Integer.valueOf(npc.getId()), npc);
        return npc;
    }
    
    public void deleteNpc(Npc npc) {
        this.m_npcs_muertos.add(npc.getId());
    }
    
    public Npc getNpcById(int npcId) {
        return this.m_npcs.get(Integer.valueOf(npcId));
    }
    
    public Client getClientById(short id) {
        return this.m_clientes.get(id);
    }
    
    public Spell getHechizo(int spell) {
        return this.m_hechizos.get(spell - 1);
    }
    
    public Map getMapa(int mapa) {
        if (mapa > 0 && mapa <= this.m_mapas.size()) {
			return this.m_mapas.get(mapa - 1);
		}
		return null;
    }
    
    public Client getUsuario(String nombre) {
    	if ("".equals(nombre)) {
			return null;
		}
    	for (Client cliente: getClientes()) {
            if (cliente.getNick().equalsIgnoreCase(nombre)) {
                return cliente;
            }
        }
        return null;
    }
    
    public boolean usuarioYaConectado(Client cliente) {
    	for (Client c: getClientes()) {
            if (cliente != c && cliente.getNick().equalsIgnoreCase(c.getNick())) {
                return true;
            }
        }
        return false;
    }
    
    private void doAI() {
        // TIMER_AI_Timer()
        if (!this.m_haciendoBackup) {
            // Update NPCs
            Vector<Npc> npcs = new Vector<Npc>(getNpcs());
            for (Npc npc: npcs) {
                if (npc.isNpcActive()) { // Nos aseguramos que sea INTELIGENTE!
                    if (npc.estaParalizado()) {
                        npc.efectoParalisisNpc();
                    } else {
                        // Usamos AI si hay algun user en el mapa
                        if (npc.getPos().isValid()) {
                            Map mapa = getMapa(npc.getPos().map);
                            if (mapa != null && mapa.getCantUsuarios() > 0) {
                                if (!npc.isStatic()) {
                                    npc.doAI();
                                }
                            }
                        }
                    }
                }
            }
            for (Object element : this.m_npcs_muertos) {
                this.m_npcs.remove(element);
            }
            this.m_npcs_muertos.clear();
        }
    }

    private void pasarSegundo() {
        Vector<Client> paraSalir = new Vector<Client>();
        for (Client cli: getClientes()) {
            if (cli.m_counters.Saliendo) {
                cli.m_counters.SalirCounter--;
                if (cli.m_counters.SalirCounter <= 0) {
                    paraSalir.add(cli);
                } else {
                	switch (cli.m_counters.SalirCounter) {
                		case 10:
                			cli.enviarMensaje("En " + cli.m_counters.SalirCounter +" segundos se cerrará el juego...", FontType.INFO);
                			break;
                		case 3:
                			cli.enviarMensaje("Gracias por jugar Argentum Online. Vuelve pronto.", FontType.INFO);
                			break;
                	}
                }
            }
        }
        for (Client cli: paraSalir) {
            cli.doSALIR();
        }
    }
        
    public void guardarUsuarios() {
        this.m_haciendoBackup = true;
        try {
            //enviarATodos(MSG_BKW);
            //enviarATodos(MSG_TALK, "Servidor> Grabando Personajes" + FontType.SERVER);
            for (Client cli: getClientes()) {
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
    	for (Map mapa: this.m_mapas) {
            if ((Util.Azar(1, 150) < 12) && (mapa.getCantUsuarios() > 0)) {
                mapa.doFX();
            }
        }
    }
    
    private void gameTimer() {
        // <<<<<< Procesa eventos de los usuarios >>>>>>
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0) {
                cli.procesarEventos();
            }
        }
    }
    
    private void npcAtacaTimer() {
    	for (Npc npc: getNpcs()) {
            npc.setPuedeAtacar(true);
        }
    }
    
    private void timerOculto() {
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0) {
                if (cli.estaOculto()) {
					cli.doPermanecerOculto();
				}
            }
        }
    }
    
    private void lluviaTimer() {
        if (!this.m_lloviendo) {
			return;
		}
        for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0) {
                cli.efectoLluvia();
            }
        }
    }
    
    public void enviarATodos(ServerPacketID msg, Object... params) {
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0 && cli.isLogged()) {
                cli.enviar(msg, params);
            }
        }
    }
    
    public void enviarAAdmins(ServerPacketID msg, Object... params) {
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0 && cli.esGM() && cli.isLogged()) {
                cli.enviar(msg, params);
            }
        }
    }
    
    public void enviarMensajeAAdmins(String msg, FontType fuente) {
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0 && cli.esGM() && cli.isLogged()) {
                cli.enviarMensaje(msg, fuente);
            }
        }
    }
    
    long minutosLloviendo = 0;
    long minutosSinLluvia = 0;
    
    public void iniciarLluvia() {
        this.m_lloviendo = true;
        this.minutosSinLluvia = 0;
        enviarATodos(ServerPacketID.userRain);
    }
    
    public void detenerLluvia() {
        this.m_lloviendo = false;
        this.minutosSinLluvia = 0;
        enviarATodos(ServerPacketID.userRain);
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
        for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0 && cli.isLogged()) {
                Map mapa = getMapa(cli.getPos().map);
                if (mapa.getTrigger(cli.getPos().x, cli.getPos().y) == 5) {
                    cli.m_counters.PiqueteC++;
                    cli.enviarMensaje("Estas obstruyendo la via pública, muévete o serás encarcelado!!!", FontType.INFO);
                    if (cli.m_counters.PiqueteC > 23) {
                        cli.m_counters.PiqueteC = 0;
                        cli.encarcelar(3, null);
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
    
    public void purgarPenas() {
    	for (Client cli: getClientes()) {
            if (cli != null && cli.getId() > 0 && cli.getFlags().UserLogged) {
                if (cli.m_counters.Pena > 0) {
                    cli.m_counters.Pena--;
                    if (cli.m_counters.Pena < 1) {
                        cli.m_counters.Pena = 0;
                        cli.warpUser(WP_LIBERTAD.map, WP_LIBERTAD.x, WP_LIBERTAD.y, true);
                        cli.enviarMensaje("Has sido liberado!", FontType.INFO);
                    }
                }
            }
        }
    }
    
    private void checkIdleUser() {
    	for (Client cliente: getClientes()) {
            if (cliente != null && cliente.getId() > 0 && cliente.isLogged()) {
                cliente.m_counters.IdleCount++;
                if (cliente.m_counters.IdleCount >= IdleLimit) {
                    cliente.enviarError("Demasiado tiempo inactivo. Has sido desconectado.");
                    cliente.doSALIR();
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
            ///////// fixme
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
        purgarPenas();
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
        for (Client cli: getClientes()) {
            if (cli.isLogged() && cli.esGM()) {
                cli.enviarMensaje(msg, FontType.TALKGM);
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
        // Public Sub DoBackUp()
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
        /////////// fixme - fixme - fixme
    }
    
    public void limpiarMundo(Client cli) {
    	int cant = limpiarMundo();
        if (cli != null) {
			cli.enviarMensaje("Servidor> Limpieza del mundo completa. Se eliminaron " + cant + " m_objetos.", FontType.SERVER);
		}
    }
    
    private int limpiarMundo() {
        //Sub LimpiarMundo()
    	int cant = 0;
    	for (MapPos pos: this.m_trashCollector) {
            Map mapa = getMapa(pos.map);
            mapa.quitarObjeto(pos.x, pos.y);
            cant++;
        }
        this.m_trashCollector.clear();
        return cant;
    }
    
    Feedback m_feedback = new Feedback();
    
    private synchronized void worldSave() {
        //enviarATodos("||%%%% POR FAVOR ESPERE, INICIANDO WORLDSAVE %%%%" + FontType.INFO);
        // Hacer un respawn de los guardias en las pos originales.
        reSpawnOrigPosNpcs();
        // Ver cuantos m_mapas necesitan backup.
        int cant = 0;
        for (Map mapa: this.m_mapas) {
            if (mapa.m_backup) {
				cant++;
			}
        }
        // Guardar los m_mapas
        this.m_feedback.init("Guardando m_mapas modificados", cant);
        int i = 0;
        for (Map mapa: this.m_mapas) {
            if (mapa.m_backup) {
                mapa.saveMapData();
                this.m_feedback.step("Mapa " + (++i));
            }
        }
        this.m_feedback.finish();
        // Guardar los NPCs
        try {
            IniFile ini = new IniFile();
            for (Npc npc: getNpcs()) {
                if (npc.getBackup()) {
                    npc.backup(ini);
                }
            }
            // Guardar todo
            ini.store("worldBackup" + File.separator + "backNPCs.dat");
        } catch (Exception e) {
            log.fatal("worldSave(): ERROR EN BACKUP NPCS", e);
        }
        //enviarATodos("||%%%% WORLDSAVE LISTO %%%%" + FontType.INFO);
    }

    
    private void reSpawnOrigPosNpcs() {
        List<Npc> spawnNPCs = new Vector<Npc>();
        for (Npc npc: getNpcs()) {
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

    public static void main(String[] args) {
        boolean loadBackup = !(args.length > 0 && args[0].equalsIgnoreCase("reset"));
        if (loadBackup) {
			log.info("Arrancando usando el backup");
		} else {
			log.info("Arrancando sin usar backup");
		}
        AojServer.instance().run(loadBackup);
    }

}
