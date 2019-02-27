/**
 * Map.java
 *
 * Created on 14 de septiembre de 2003, 16:48
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
package org.ArgentumOnline.server.map;

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.areas.AreasAO;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.net.ServerPacket;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.BlockPositionResponse;
import org.ArgentumOnline.server.protocol.CharacterMoveResponse;
import org.ArgentumOnline.server.protocol.CharacterRemoveResponse;
import org.ArgentumOnline.server.protocol.CreateFXResponse;
import org.ArgentumOnline.server.protocol.ObjectCreateResponse;
import org.ArgentumOnline.server.protocol.ObjectDeleteResponse;
import org.ArgentumOnline.server.protocol.PlayWaveResponse;
import org.ArgentumOnline.server.protocol.RemoveCharDialogResponse;
import org.ArgentumOnline.server.util.BytesReader;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class Map implements Constants {
	private static Logger log = LogManager.getLogger();

    final static String FOLDER_MAPS = "maps";
    final static String FOLDER_WORLDBACKUP = "worldBackup";
    
    /** Número de mapa. */
    private short nroMapa;
    public AreasAO areasData = new AreasAO();
    
    // Cabecera archivo .map
    private short version   = 0;
    //private byte desc[] = new byte[255];
    private String desc;
    //private int crc     = 0;
    //private int mw      = 0;
    
    // Información del archivo .dat
    String  m_name  = "";
    String  m_music = "";
    int     m_numUsers = 0; // FIXME se usa?
    int     m_mapVersion = 0; // FIXME se usa?
    short   m_terreno = TERRENO_BOSQUE;
    short   m_zona = ZONA_CAMPO;
    public boolean m_pk = false;
    boolean m_restringir = false;
    public boolean m_backup = false;
    short   m_version = 0; // FIXME se usa?
    MapPos m_startPos = MapPos.empty(); // FIXME se usa?
    
    GameServer server;
    
    /** Bloques o celdas del mapa. */
    MapCell m_cells[][] = new MapCell[MAPA_ANCHO][MAPA_ALTO];
    
    /** Objetos en el mapa. */
    Vector<MapCell> bloquesConObjetos = new Vector<MapCell>();
    
    /** Clientes en el mapa */
    Vector<Player> m_clients = new Vector<Player>();
    
    /** NPCs en el mapa */
    Vector<Npc> m_npcs = new Vector<Npc>();
    
    /** Creates a new instance of Map */
    public Map(short nroMapa, GameServer server) {
    	this.server = server;
        this.nroMapa = nroMapa;
        
        this.areasData.initAreas(server, this);
        
       // this.areasData.makeAreas(server, nroMapa);
        

        
        for (int x = 0; x < MAPA_ANCHO; x++) {
            for (int y = 0; y < MAPA_ALTO; y++) {
                this.m_cells[x][y] = new MapCell((short)(x+1), (short)(y+1));
            }
        }
    }
    
    public int getNroMapa() {
        return this.nroMapa;
    }
    
    public short getVersion() {
		return version;
	}
    
    public short getZona() {
        return this.m_zona;
    }
    
    public String getMusic() {
        return this.m_music;
    }
    
    public short getTerreno() {
        return this.m_terreno;
    }
    
    public MapObject getObjeto(byte x, byte y) {
    	if (this.m_cells[x-1][y-1].hayObjeto()) {
			return new MapObject(this.m_cells[x-1][y-1].getObjInd(),
    				this.m_cells[x-1][y-1].getObjCant(), x, y);
		}
		return null;
    }
    
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
	
    public Npc getNPC(byte  x, byte y) {
        return this.m_cells[x-1][y-1].getNpc();
    }
    
    public short getTrigger(byte  x, byte y) {
        return this.m_cells[x-1][y-1].getTrigger();
    }
    
    public void setTrigger(byte  x, byte y, byte value) {
        this.m_cells[x-1][y-1].setTrigger(value);
    }
    
    public boolean testSpawnTrigger(byte  x, byte y) {
        int trigger = this.m_cells[x-1][y-1].getTrigger();
        return trigger != 3 && trigger != 2 && trigger != 1;
    }
    
    public boolean testSpawnTriggerNpc(short mapa, byte  x, byte y, boolean bajoTecho) {
    	return testSpawnTriggerNpc(MapPos.mxy(mapa, x, y), bajoTecho);
    }
    
    public boolean testSpawnTriggerNpc(MapPos pos, boolean bajoTecho) {
        int trigger = this.getTrigger(pos.x, pos.y);
        if (bajoTecho) {
			return trigger != MapCell.TRIGGER_POS_INVALIDA && 
			trigger != MapCell.TRIGGER_NO_RESPAWN;
		}
		return trigger != MapCell.TRIGGER_POS_INVALIDA && 
		trigger != MapCell.TRIGGER_NO_RESPAWN && 
		trigger != MapCell.TRIGGER_BAJO_TECHO;
    }
    
    public boolean getForbbiden() {
    	return this.m_restringir;
    }
    
    //FIX BY AGUSH
    public boolean hayAgua(byte  x, byte y) {    	
    	if (x < MAPA_ANCHO && x > 0 &&
    			y < MAPA_ALTO && y > 0) {    		
            
            if (this.m_cells[x-1][y-1].getGrh(0) >= 1505 
            		&& this.m_cells[x-1][y-1].getGrh(0) <= 1520 &&
            		this.m_cells[x-1][y-1].getGrh(1) == 0) {
            	return true;
            }
            
    	}
    	
    	return false;

    }
    
    public boolean estaCliente(Player cliente) {
        return this.m_clients.contains(cliente);
    }
    
    public boolean intemperie(byte  x, byte y) {
        // Public Function Intemperie(ByVal UserIndex As Integer) As Boolean
        if (this.m_zona != ZONA_DUNGEON) {
            short trigger = getTrigger(x, y);
            if (trigger != 1 && trigger != 2 && trigger != 4) {
				return true;
			}
        }
        return false;
    }
    
    public int getCantUsuarios() {
        return this.m_clients.size();
    }
    
    public List<String> getUsuarios() {
        Vector<String> usuarios = new Vector<String>();
        for (Player cli: this.m_clients) {
            if (!"".equals(cli.getNick())) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    // FIXME
    public Player spUser(int value) {
    	return this.m_clients.get(value);
    }
    
    // FIXME
    public Npc spNpc(int value) {
    	return this.m_npcs.get(value);
    }
    
    public boolean esZonaSegura() {
        return this.m_pk;
    }
    
    public void load(boolean loadBackup) {
        try {
            loadDatFile("Mapa" + this.nroMapa + ".dat", loadBackup);
            loadMapFile("Mapa" + this.nroMapa + ".map", loadBackup);
            loadInfFile("Mapa" + this.nroMapa + ".inf", loadBackup);
        } catch (java.io.FileNotFoundException e) {
            log.warn("Archivo de mapa %d faltante.", this.nroMapa);
        } catch (java.io.IOException e) {
            log.warn("Error leyendo archivo de mapa %d", this.nroMapa);
        } catch (Exception e) {
        	log.warn("Error con mapa " + this.nroMapa, e);
        }
    }
    
    
    private void loadDatFile(String datFileName, boolean loadBackup) 
    throws java.io.FileNotFoundException, java.io.IOException {
    	log.trace("loading file %s", datFileName);
        // Cargar información del archivo .dat
        IniFile ini = new IniFile();
        if (loadBackup && Files.exists(Paths.get(FOLDER_WORLDBACKUP + File.separator + datFileName))) {
			ini.load(FOLDER_WORLDBACKUP + File.separator + datFileName);
		} else {
			ini.load(FOLDER_MAPS + File.separator + datFileName);			
		}
        String section = "Mapa" + this.nroMapa;
        this.m_name = ini.getString(section, "Name");
        this.m_music = ini.getString(section, "MusicNum");
        this.m_pk = (ini.getInt(section, "PK") == 1);
        this.m_restringir = (ini.getString(section, "Restringir").equals("Si"));
        this.m_backup = (ini.getInt(section, "BackUp") == 1);
        String tipo_terreno = ini.getString(section, "Terreno").toUpperCase();
        if (tipo_terreno.equals("BOSQUE")) {
            this.m_terreno = TERRENO_BOSQUE;
        } else if (tipo_terreno.equals("DESIERTO")) {
            this.m_terreno = TERRENO_DESIERTO;
        } else if (tipo_terreno.equals("NIEVE")) {
            this.m_terreno = TERRENO_NIEVE;
        } else {
            this.m_terreno = TERRENO_BOSQUE;
        }
        String tipo_zona = ini.getString(section, "Zona");
        if (tipo_zona.equals("CAMPO")) {
            this.m_zona = ZONA_CAMPO;
        } else if (tipo_zona.equals("CIUDAD")) {
            this.m_zona = ZONA_CIUDAD;
        } else if (tipo_zona.equals("DUNGEON")) {
            this.m_zona = ZONA_DUNGEON;
        } else {
            this.m_zona = ZONA_CAMPO;
        }
        
        // FIXME esta información sigue estando en los mapas?
        String startPos = ini.getString(section, "StartPos");
        if (startPos.length() > 4) {
            String mapa = startPos.substring(0, startPos.indexOf('-'));
            String x = startPos.substring(startPos.indexOf('-')+1, startPos.lastIndexOf('-'));
            String y = startPos.substring(startPos.lastIndexOf('-')+1);
            this.m_startPos.map = Short.parseShort(mapa);
            this.m_startPos.x = Byte.parseByte(x);
            this.m_startPos.y = Byte.parseByte(y);
        }
        if (this.m_startPos.map == 0) {
			this.m_startPos.map = 45;
		}
        if (this.m_startPos.x == 0) {
			this.m_startPos.x = 45;
		}
        if (this.m_startPos.y == 0) {
			this.m_startPos.y = 45;
			// int     m_numUsers = 0;
			// int     m_mapVersion = 0;
		}
    }
    
    
    /**
     * Carga el archivo .map dentro de la carpeta backup
     * by Agush
     */
    private void loadMapFile(String mapFileName, boolean loadBackup)
    throws java.io.IOException {
    	log.trace("loading file %s", mapFileName);
	    
    	Path path = Paths.get(FOLDER_WORLDBACKUP + File.separator + mapFileName);
	    if (!loadBackup || !Files.exists(path)) {
	    	path = Paths.get(FOLDER_MAPS + File.separator + mapFileName);
	    }
	    
	    byte[] bytes = Files.readAllBytes(path);
	    if (bytes == null) {
	    	log.error("Error, archivo de mapa inexistente: " + mapFileName);
	    	return;            	
	    }
	    
	    try {
            int byflags = 0;
            var reader = new BytesReader();
            reader.setBytes(bytes);
            
            this.version = reader.readShort();
            reader.skipBytes(255);
            
            reader.readInt();
            reader.readInt();
            reader.skipBytes(8);
            
            for (int y = 0; y < MAPA_ALTO; y++) {
                for (int x = 0; x < MAPA_ANCHO; x++) {
                	
                	byflags = reader.readByte();
                	
                	if ((byflags & 1) == 1) {
                		this.m_cells[x][y].setBloqueado(true);
                	}
                    
                	this.m_cells[x][y].setGrh(0, Util.leShort(reader.readShort()));
                    
                    if ((byflags & 2) == 2) {
                    	this.m_cells[x][y].setGrh(1, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 4) == 4) {
                    	this.m_cells[x][y].setGrh(2, Util.leShort(reader.readShort()));
                    }
                    if ((byflags & 8) == 8) {
                    	this.m_cells[x][y].setGrh(3, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 16) == 16) {
                    	this.m_cells[x][y].setTrigger((byte) Util.leShort(reader.readShort()));
                    }
                    
                }
            }         
            
        } catch (Exception e) {
        	log.error("ERROR LOADING %s", mapFileName);
        }
    }

    /**
     * Carga el archivo .inf de la carpeta backup
     * by Agush
     */
    private void loadInfFile(String infFileName, boolean loadBackup) 
    throws java.io.IOException {
    	log.trace("load file %s", infFileName);

        Path path = Paths.get(FOLDER_WORLDBACKUP + File.separator + infFileName);
        if (!loadBackup || !Files.exists(path)) {
        	path = Paths.get(FOLDER_MAPS + File.separator + infFileName);
        	if (!Files.exists(path)) {
        		log.error("Error, archivo de mapa inexistente: " + infFileName);
        		return;
        	}
        }
        
        try {
	        Npc npc = null;
	        int byflags = 0;
	        byte[] array = Files.readAllBytes(path);
	        
	        BytesReader reader = new BytesReader();
	        reader.setBytes(array);
	        
	        reader.skipBytes(10);
	        reader.mark();
	        
	        for (int y = 0; y < MAPA_ALTO; y++) {
	            for (int x = 0; x < MAPA_ANCHO; x++) {
	            	
	            	byflags = reader.readByte();
	            	
	            	if ((byflags & 1) == 1) {
						this.m_cells[x][y].setTeleport(MapPos.mxy(Util.leShort(reader.readShort())
								,Util.leShort(reader.readShort()), Util.leShort(reader.readShort())));
	            	}
	                
	                if ((byflags & 2) == 2) {
	                    short npcId = Util.leShort(reader.readShort());
	                    if (npcId > 0) {
	                        // Crear un nuevo Npc.
	                        this.m_cells[x][y].setNpc(this.server.createNpc(npcId));
	                        npc = this.m_cells[x][y].getNpc();
	                        this.m_npcs.add(npc);
	                        npc.pos().map = this.nroMapa;
	                        npc.pos().x = (byte) (x+1);
	                        npc.pos().y = (byte) (y+1);
	                        npc.getOrig().map = this.nroMapa;
	                        if (npc.respawnOrigPos()) {
		                        npc.getOrig().x = (byte) (x+1);
		                        npc.getOrig().y = (byte) (y+1);
	                        } else {
		                        npc.getOrig().x = (byte)0;
		                        npc.getOrig().y = (byte)0;                        
	                        }
	                        npc.activar();
	                        // JAO: Sistema de areas!!
	                        //  this.areasData.setNpcArea(npc);
	                        this.areasData.loadNpc(npc);
	                    }
	                }
	                
	                if ((byflags & 4) == 4) {
						short obj_ind = Util.leShort(reader.readShort());
						short obj_cant = Util.leShort(reader.readShort());
						agregarObjeto(obj_ind, obj_cant, (byte)(x+1), (byte)(y+1)); // FIXME ignora el resultado ? y si no pudo agregarlo?
						enviarPuerta(getObjeto((byte)(x+1), (byte)(y+1)));
	                }
	            }
	        }         
        
        } catch (Exception e) {
        	log.error("Error, cargando " + infFileName, e);
        }
    }
    
    public boolean entrar(Player cliente, byte  x, byte y) {
        if (this.m_cells[x-1][y-1].getClienteId() != 0) {
			return false;
		}
        this.m_clients.add(cliente);
        this.m_cells[x-1][y-1].setClienteId(cliente.getId());
        //enviarATodosExc(cliente.getId(), serverPacketID.CC, cliente.ccParams());
        //this.areasData.sendToArea(x, y, cliente.getId(), serverPacketID.CC, cliente.ccParams());
        
        cliente.pos().set(this.nroMapa, x, y); // REVISAR
        return true;
    }
    
    public boolean salir(Player cliente) {
        short x = cliente.pos().x;
        short y = cliente.pos().y;
        try {
	        //if (estaCliente(cliente)) {
	        	try {
	        		this.areasData.sendToUserArea(cliente, new RemoveCharDialogResponse(cliente.getId()));
	        		this.areasData.sendToUserArea(cliente, new CharacterRemoveResponse(cliente.getId()));
	        	} finally {
	        		this.m_clients.remove(cliente);
	        		// Nuevo sistema de areas by JAO
	        		//this.areasData.userDisconnect(cliente);
	        		this.areasData.resetUser(cliente);
	        	}
	        //} else {
	            // El cliente no esta en el mapa.
	            //return true;
	        //}
        } finally {
	        if (this.m_cells[x-1][y-1].getClienteId() != cliente.getId()) {
	            // Hay una inconsistencia: no se encuentra donde deberia :-S
	            return false;
	        	//System.exit(1); // ERROR MUY FEO - FIXME
	        }
	        this.m_cells[x-1][y-1].setClienteId((short) 0);
        }
        return true;
    }
    
    public void moverNpc(Npc npc, byte x, byte y) {
        if (this.m_cells[x-1][y-1].getNpc() != null) {
			log.fatal("ERRRRRRRRRRORRRRRRRRRRRR en moverNpc: " + npc);
		}
        this.m_cells[npc.pos().x-1][npc.pos().y-1].setNpc(null);
        this.m_cells[x-1][y-1].setNpc(npc);
        
        this.areasData.checkUpdateNeededNpc(npc, npc.getInfoChar().getDir());
        this.areasData.sendToNPCArea(npc, new CharacterMoveResponse(npc.getId(), x, y));
        
    }
    
    public boolean isFree(byte  x, byte y) {
        return (this.m_cells[x-1][y-1].getClienteId() == 0) && (this.m_cells[x-1][y-1].getNpc() == null);
    }
    
    public boolean estaBloqueado(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return true;
		}
        return this.m_cells[x-1][y-1].estaBloqueado();
    }
    
    public boolean entrar(Npc npc, byte  x, byte y) {
        if (this.m_cells[x-1][y-1].getNpc() != null) {
			return false;
		}
        
        this.m_cells[x-1][y-1].setNpc(npc);
        this.m_npcs.add(npc);
        
        npc.pos().set(this.nroMapa, x, y);
      
        this.areasData.loadNpc(npc);
        
		//this.areasData.setNpcArea(npc);
		//this.areasData.sendToArea(x, y, -1, serverPacketID.MSG_CCNPC, npc.ccParams());
        
        return true;
    }
    
    public boolean salir(Npc npc) {
        byte x = npc.pos().x;
        byte y = npc.pos().y;
        try {
        	enviarAlArea(x, y, new RemoveCharDialogResponse(npc.getId()));
	        enviarAlArea(x, y, new CharacterRemoveResponse(npc.getId()));
        } finally {
            this.m_cells[x-1][y-1].setNpc(null);
	        this.m_npcs.remove(npc);
	        npc.pos().set(this.nroMapa, (short)0, (short)0);
        }
        return true;
    }
    
    public void quitarNpcsArea(short pos_x, short pos_y) {
        short x1 = (short) (pos_x - MinXBorder + 1);
        short x2 = (short) (pos_x + MaxXBorder + 1);
        short y1 = (short) (pos_y - MinYBorder + 1);
        short y2 = (short) (pos_y + MaxYBorder + 1);
        if (x1 < 1) {
			x1 = 1;
		}
        if (x2 > 100) {
			x2 = 100;
		}
        if (y1 < 1) {
			y1 = 1;
		}
        if (y2 > 100) {
			y2 = 100;
		}
        Npc npc;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
            	npc = this.m_cells[x-1][y-1].getNpc();
                if (npc != null) { 
                	
                	//agush: fix mascotas ;-)
                	if (npc.getPetUserOwner() != null) {
                		Player masterUser = (npc.getPetUserOwner());
                		masterUser.quitarMascota(npc);
                	}
                	
                	salir(npc);
                }
            }
        }
    }    
    
    public boolean agregarObjeto(short objid, int cant, byte x, byte y) {
    	if (hayObjeto(x, y)) {
            log.warn("Intento de agregar objeto sobre otro: objid=" + objid + " cant" + cant + " mapa" + this.nroMapa + " x=" + x + " y=" + y);
    		return false;
    	}
    	this.m_cells[x-1][y-1].setObj(objid, cant);
        this.bloquesConObjetos.add(this.m_cells[x-1][y-1]);
        short grhIndex = findObj(objid).GrhIndex;
        
        if (DEBUG)
        	log.debug(grhIndex + "-" + x + "-" + y);
        
        this.areasData.sendToAreaByPos(this, x, y, new ObjectCreateResponse((byte)x ,(byte)y, (short)grhIndex));
        return true;
    }
    
    public void quitarObjeto(byte  x, byte y) {
    	short obj = this.m_cells[x-1][y-1].getObjInd(); // ?
    	this.bloquesConObjetos.remove(this.m_cells[x-1][y-1]);
        this.m_cells[x-1][y-1].quitarObjeto();
        enviarAlArea(x, y, new ObjectDeleteResponse(x,y));
    }
    
    public void bloquearTerreno(byte x, byte y) {
        this.m_cells[x-1][y-1].setBloqueado(true);
        this.m_cells[x-1][y-1].setModificado(true);
        enviarAlArea(x, y, new BlockPositionResponse(x, y, (byte)1));
    }
    
    public void desbloquearTerreno(byte x, byte y) {
        this.m_cells[x-1][y-1].setBloqueado(false);
        this.m_cells[x-1][y-1].setModificado(true);
        enviarAlArea(x, y, new BlockPositionResponse(x, y, (byte)0));
    }
    
    public void abrirCerrarPuerta(MapObject obj) {
        if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
            // Es un objeto tipo puerta.
            if (obj.getInfo().estaCerrada()) {
                // Abrir puerta.
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = findObj(obj.getInfo().IndexAbierta);
                //obj = agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            } else {
                // Cerrar puerta
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = findObj(obj.getInfo().IndexCerrada);
                //obj = agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            }
            obj = getObjeto(obj.x, obj.y);
            enviarPuerta(obj);
            enviarAlArea(obj.x, obj.y, new PlayWaveResponse(SND_PUERTA, obj.x, obj.y));
        }
    }
    
    private void enviarPuerta(MapObject obj) {
        if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
            // Es un objeto tipo puerta.
            if (obj.getInfo().estaCerrada()) {
                bloquearTerreno((byte) (obj.x-1), obj.y);
                bloquearTerreno(obj.x, obj.y);
            } else {
                desbloquearTerreno((byte) (obj.x-1), obj.y);
                desbloquearTerreno(obj.x, obj.y);
            }
            enviarAlArea(obj.x,obj.y, new ObjectCreateResponse(obj.x, obj.y, (short)obj.getInfo().GrhIndex));
        }
    }
    
    public void enviarCFX(byte  x, byte y, int id, int fx, int val) {
    	enviarAlArea(x,y, new CreateFXResponse((short) id, (short) fx, (short) val));
    }
    
    public void enviarATodos(ServerPacket packet) {
        enviarATodosExc(-1, packet);
    }
    
    public void enviarATodosExc(int excepto, ServerPacket packet) {
        for (Object element : this.m_clients) {
            Player cliente = (Player) element;
            if (cliente.getId() != excepto) {
        		cliente.enviar(packet);
            }
        }
    }
    
    public void enviarAlArea(byte x, byte y, ServerPacket packet) {
        enviarAlArea(x, y, -1, packet);
    }
        
    public void enviarAlArea(byte pos_x, byte pos_y, int excepto, ServerPacket packet) {
    	//areasData.sendToArea(pos_x, pos_y, excepto, msg, params);
    	areasData.sendToAreaButIndex(this, pos_x, pos_y, excepto, packet);
    }
    
    public void enviarAlAreaAdminsNoConsejeros(short pos_x, short pos_y, ServerPacket packet) {
        short x1 = (short) (pos_x - MinXBorder + 1);
        short x2 = (short) (pos_x + MaxXBorder + 1);
        short y1 = (short) (pos_y - MinYBorder + 1);
        short y2 = (short) (pos_y + MaxYBorder + 1);
        if (x1 < 1) {
			x1 = 1;
		}
        if (x2 > 100) {
			x2 = 100;
		}
        if (y1 < 1) {
			y1 = 1;
		}
        if (y2 > 100) {
			y2 = 100;
		}
        Player cliente;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
                if (this.m_cells[x-1][y-1].getClienteId() > 0) {
                    cliente = this.server.getClientById(this.m_cells[x-1][y-1].getClienteId());
                    if (cliente != null && (cliente.esDios() || cliente.esSemiDios())) {
						cliente.enviar(packet);
					}
                }
            }
        }
    }
    
    public Player buscarEnElArea(short pos_x, short pos_y, short cli_id) {
        short x1 = (short) (pos_x - MinXBorder + 1);
        short x2 = (short) (pos_x + MaxXBorder + 1);
        short y1 = (short) (pos_y - MinYBorder + 1);
        short y2 = (short) (pos_y + MaxYBorder + 1);
        if (x1 < 1) {
			x1 = 1;
		}
        if (x2 > 100) {
			x2 = 100;
		}
        if (y1 < 1) {
			y1 = 1;
		}
        if (y2 > 100) {
			y2 = 100;
		}
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
                if (this.m_cells[x-1][y-1].getClienteId() == cli_id) {
					return this.server.getClientById(this.m_cells[x-1][y-1].getClienteId());
				}
            }
        }
        return null;
    }
    
    /** Enviarme los m_clients del mapa, sin contarme a mi */
   // public void enviarClientes(Client cliente) {
     //   for (Object element : this.m_clients) {
       //     Client cli = (Client) element;
         //   if (!cli.equals(cliente)) {
			//	//cliente.enviar(MSG_CC, cli.ccParams());
            	//cliente.enviar(serverPacketID.CC, cli.ccParams());
			//}
        //}
   // }
    
    /** Enviarme los objetos del mapa. */
    public void enviarObjetos(Player cliente) {
        for (Object element : this.bloquesConObjetos) {
            MapCell b = (MapCell) element;
            cliente.enviarObjeto(b.getObjInd(), b.getX(), b.getY());
        }
    }
    
    /** Enviarme los NPCs del mapa. */
    public void enviarNPCs(Player cliente) {
        for (Object element : this.m_npcs) {
            Npc npc = (Npc) element;
            cliente.enviar(npc.createCC());
        }
    }
    
    /** Enviarme las posiciones bloqueadas del mapa. */
    public void enviarBQs(Player cliente) {
        for (int y = 0; y < MAPA_ALTO; y++) {
            for (int x = 0; x < MAPA_ANCHO; x++) {
                if (this.m_cells[x][y].estaModificado()) {
					cliente.enviarBQ(x+1, y+1, this.m_cells[x][y].estaBloqueado());
				}
            }
        }
    }
    
    public void mover(Player cliente, byte x, byte y) {
        this.m_cells[cliente.pos().x-1][cliente.pos().y-1].setClienteId((short) 0);
        this.m_cells[x-1][y-1].setClienteId(cliente.getId());
        cliente.pos().set(this.nroMapa, x, y);
        
		//JAO: Nuevo sistema de areas !!
        this.areasData.sendToAreaButIndex(this, x, y, cliente.getId(),
        		new CharacterMoveResponse(cliente.getId(), x, y));
        this.areasData.checkUpdateNeededUser(cliente, cliente.getInfoChar().getDir());
    }
    
    public boolean hayTeleport(byte  x, byte y) {
        return this.m_cells[x-1][y-1].hayTeleport();
    }
    
    public void crearTeleport(byte x, byte y, short dest_mapa, byte dest_x, byte dest_y) {
        if (dest_mapa > 0 && dest_x > 0 && dest_y > 0) {
			this.m_cells[x-1][y-1].setTeleport(MapPos.mxy(dest_mapa, dest_x, dest_y));
		}
        //Objeto obj = agregarObjeto(OBJ_TELEPORT, 1, x, y);
        agregarObjeto(OBJ_TELEPORT, 1, x, y);
    }
    
    public void destruirTeleport(byte  x, byte y) {
        if (!hayTeleport(x, y)) {
			return;
		}
        quitarObjeto(x, y);
        this.m_cells[x-1][y-1].setTeleport(null);
    }    
    
    public boolean hayObjeto(byte  x, byte y) {
        return (this.m_cells[x-1][y-1].hayObjeto());
    }
    
    public boolean hayCliente(byte  x, byte y) {
        return (this.m_cells[x-1][y-1].getClienteId() != 0 && getCliente(x, y) != null); // FIXME
    }
    
    public boolean hayNpc(byte  x, byte y) {
        return (this.m_cells[x-1][y-1].getNpc() != null);
    }
    
    public Player getCliente(byte  x, byte y) {
        return this.server.getClientById(this.m_cells[x-1][y-1].getClienteId());
    }
    
    public Npc getNpc(byte  x, byte y) {
        return this.m_cells[x-1][y-1].getNpc();
    }
    
    public MapObject buscarObjeto(byte  x, byte y) {
        // Ver si hay un objeto en los alrededores...
        if (hayObjeto(x, y)) {
            return getObjeto(x, y);
        }
        if (hayObjeto((byte) (x+1), y)) {
            return getObjeto((byte) (x+1), y);
        }
        if (hayObjeto((byte) (x+1), (byte) (y+1))) {
            return getObjeto((byte) (x+1), (byte) (y+1));
        }
        if (hayObjeto(x, (byte) (y+1))) {
            return getObjeto(x, (byte) (y+1));
        }
        return null;
    }
    public Player buscarCliente(byte  x, byte y) {
        // Ver si hay un cliente en los alrededores...
        if (hayCliente(x, (byte) (y+1))) {
            return getCliente(x, (byte) (y+1));
        }
        if (hayCliente(x, y)) {
            return getCliente(x, y);
        }
        return null;
    }
    public Npc buscarNpc(byte  x, byte y) {
        // Ver si hay un NPC en los alrededores...
        if (hayNpc(x, (byte) (y+1))) {
            return getNpc(x, (byte) (y+1));
        }
        if (hayNpc(x, y)) {
            return getNpc(x, y);
        }
        return null;
    }
    
    public void consultar(Player cliente, byte  x, byte y) {
        // Sub LookatTile(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer)
    	
    	if (!cliente.pos().inRangoVision(x, y)) {
            return;
        }
        
        boolean hayAlgo = false;
        // Ver si hay un objeto en los alrededores...
        MapObject obj = buscarObjeto(x, y);
        if (obj != null) {
            cliente.enviarMensaje(obj.getInfo().Nombre + " - " + obj.obj_cant, FontType.FONTTYPE_INFO);
            cliente.getFlags().TargetObj = obj.getInfo().ObjIndex;
            cliente.getFlags().TargetObjMap = this.nroMapa;
            cliente.getFlags().TargetObjX = obj.x;
            cliente.getFlags().TargetObjY = obj.y;
            hayAlgo = true;
        }
        // Ver si hay un Npc...
        MapCell bloque = this.m_cells[x-1][y-1];
        Npc npc;
        if ((npc = buscarNpc(x, y)) != null) {
            hayAlgo = true;
            if (cliente.esGM() && this.server.isShowDebug()) { 
            	// Info para DEBUG by Gorlok
            	/*
                cliente.enviarMensaje(npc.m_name + " id=" + npc.getId() + 
                " nro=" + npc.m_numero + " type=" + npc.getNPCtype() + 
                " mov=" + npc.m_movement, FontType.FONTTYPE_SERVER);
                cliente.enviarMensaje("AI NPC: " + npc.m_name + " id=" + npc.getId() + 
                " nro=" + npc.m_numero + " type=" + npc.getNPCtype() + 
                " mov=" + npc.m_movement + " atby=" + npc.m_attackedBy +
				" oldmov=" + npc.m_oldMovement + " hostil=" + npc.esHostil(), FontType.FONTTYPE_SERVER);
				*/
            }
            if (npc.m_desc.length() > 0) {
                cliente.enviarHabla(COLOR_BLANCO, npc.m_desc, npc.getId());
            } 
            String msg = "";
            if (npc.getPetUserOwner() != null) {
                msg = npc.m_name + " es mascota de " + npc.getPetUserOwner().getNick();
            } else {
                msg = npc.m_name;
            }
            msg = msg + " " + npc.estadoVida(cliente);
            cliente.enviarMensaje(msg, FontType.FONTTYPE_INFO);
            cliente.getFlags().TargetNpc = npc.getId();
            cliente.getFlags().TargetMap = this.nroMapa;
            cliente.getFlags().TargetX = x;
            cliente.getFlags().TargetY = y;
            cliente.getFlags().TargetUser = 0;
            cliente.getFlags().TargetObj = 0;
        }
        
        
        Player cli;
        if ((cli = buscarCliente(x, y)) != null) {
        	
            if (!cli.getFlags().AdminInvisible) {
            	
                cliente.enviarMensaje("Ves a " + cli.getTagsDesc(), cli.getTagColor());
                cliente.getFlags().TargetUser = cli.getId();
                cliente.getFlags().TargetNpc = 0;
                cliente.getFlags().TargetObj = 0;
                cliente.getFlags().TargetMap = this.nroMapa;
                cliente.getFlags().TargetX = x;
                cliente.getFlags().TargetY = y;
                hayAlgo = true;
            }
        }
        if (!hayAlgo) {
            cliente.getFlags().TargetNpc = 0;
            cliente.getFlags().TargetNpcTipo = 0;
            cliente.getFlags().TargetUser = 0;
            cliente.getFlags().TargetObj = 0;
            cliente.getFlags().TargetObjMap = 0;
            cliente.getFlags().TargetObjX = 0;
            cliente.getFlags().TargetObjY = 0;
            cliente.getFlags().TargetMap = this.nroMapa;
            cliente.getFlags().TargetX = x;
            cliente.getFlags().TargetY = y;
            //cliente.enviarMensaje("No ves nada interesante.", FontType.FONTTYPE_INFO);
        }
        // FIXME: REVISAR SI ESTO VA...
        cliente.getFlags().TargetX = x;
        cliente.getFlags().TargetY = y;
       
        
    }
    
    public MapPos getTeleport(byte  x, byte y) {
        return this.m_cells[x-1][y-1].getTeleport();
    }
    
    public void accionParaRamita(byte x, byte y, Player cliente) {
        if (Util.distance(cliente.pos().x, cliente.pos().y, x, y) > 2) {
            cliente.enviarMensaje("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        int suerte = 0;
        int skillSupervivencia = cliente.getEstads().getUserSkill(Skill.SKILL_Supervivencia);        
        if (skillSupervivencia == 0) {
			suerte = 0;
		} else if (skillSupervivencia < 6) {
			suerte = 3;
		} else if (skillSupervivencia <= 10) {
			suerte = 2;
		} else {
			suerte = 1;
		}
        if (Util.Azar(1, suerte) == 1) {
        	quitarObjeto(x, y);
            agregarObjeto(FOGATA, 1, x, y);
            cliente.enviarMensaje("Has prendido la fogata.", FontType.FONTTYPE_INFO);
         //   enviarAlArea(x, y, MSG_FO);
        } else {
            cliente.enviarMensaje("No has podido hacer fuego.", FontType.FONTTYPE_INFO);
        }
        // Si no tiene hambre o sed quizas suba el skill supervivencia
        if (!cliente.getFlags().Hambre && !cliente.getFlags().Sed) {
			cliente.subirSkill(Skill.SKILL_Supervivencia);
		}
    }
    
    public void accionParaForo(byte  x, byte y, Player cliente) {
        if (Util.distance(cliente.pos().x, cliente.pos().y, x, y) > 2) {
            cliente.enviarMensaje("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        // ¿Hay mensajes?
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().ObjType != OBJTYPE_FOROS) {
            return;
        }
        String foroId = obj.getInfo().ForoID;
        this.server.getForumManager().enviarMensajesForo(foroId, cliente);
    }
    
    public void accionParaPuerta(byte  x, byte y, Player cliente) {
        if (Util.distance(cliente.pos().x, cliente.pos().y, x, y) > 2) {
            cliente.enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().ObjType != OBJTYPE_PUERTAS) {
			return;
		}
        if (obj.getInfo().Clave == 0) {
            abrirCerrarPuerta(obj);
            cliente.getFlags().TargetObj = obj.getInfo().ObjIndex;
        } else {
            cliente.enviarMensaje("La puerta esta cerrada con llave.", FontType.FONTTYPE_INFO);
        }
    }
    
    public void accionParaCartel(byte  x, byte y, Player cliente) {
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().ObjType != OBJTYPE_CARTELES) {
			return;
		}
        if (obj.getInfo().Texto.length() > 0) {
        	// FIXME
           // cliente.enviar(MSG_MCAR, obj.getInfo().Texto, obj.getInfo().GrhSecundario);
        }
    }
    
    public MapPos tirarItemAlPiso(byte  x, byte y, InventoryObject obj) {
        MapPos nuevaPos = tilelibre(x, y);
        log.debug("tirarItemAlPiso: x=" + nuevaPos.x + " y=" + nuevaPos.y);
        if (nuevaPos != null) {
            if (agregarObjeto(obj.objid, obj.cant, nuevaPos.x, nuevaPos.y)) {
                return nuevaPos;
            }
        }
        return null;
    }
    
    public boolean esPosLibreObjeto(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() &&
        !hayAgua(x,y) &&
        !hayObjeto(x, y) && 
		!hayTeleport(x, y);
    }
    
    /** 
     * Busca una posicion libre para depositar un objeto y que sea lo más cercana a la posición original 
     */
    public MapPos tilelibre(byte orig_x, byte orig_y) {
        if (esPosLibreObjeto(orig_x, orig_y)) {
			return MapPos.mxy(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 15; radio++) {
            byte x1 = (byte) (orig_x - radio);
            byte x2 = (byte) (orig_x + radio);
            byte y1 = (byte) (orig_y - radio);
            byte y2 = (byte) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (byte x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibreObjeto(x, y1)) {
					return MapPos.mxy(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibreObjeto(x, y2)) {
					return MapPos.mxy(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibreObjeto(x1, y)) {
					return MapPos.mxy(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibreObjeto(x2, y)) {
					return MapPos.mxy(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    public boolean isLegalPos(MapPos pos, boolean puedeAgua) {
        if (!pos.isValid()) {
			return false;
		}
        if (!puedeAgua) {
            return !this.m_cells[pos.x-1][pos.y-1].estaBloqueado() &&
            this.m_cells[pos.x-1][pos.y-1].getClienteId() == 0 &&
            this.m_cells[pos.x-1][pos.y-1].getNpc() == null &&
            !hayAgua(pos.x, pos.y);
        }
        return !this.m_cells[pos.x-1][pos.y-1].estaBloqueado() &&
        this.m_cells[pos.x-1][pos.y-1].getClienteId() == 0 &&
        this.m_cells[pos.x-1][pos.y-1].getNpc() == null &&
        hayAgua(pos.x, pos.y);
    }
    
    public boolean isLegalPosNPC(MapPos pos, boolean puedeAgua) {
        if (!pos.isValid()) {
			return false;
		}
        if (!puedeAgua) {
            return !this.m_cells[pos.x-1][pos.y-1].estaBloqueado() &&
            this.m_cells[pos.x-1][pos.y-1].getClienteId() == 0 &&
            this.m_cells[pos.x-1][pos.y-1].getNpc() == null &&
            this.m_cells[pos.x-1][pos.y-1].getTrigger() != POSINVALIDA &&
            !hayAgua(pos.x, pos.y);
        }
        return !this.m_cells[pos.x-1][pos.y-1].estaBloqueado() &&
        this.m_cells[pos.x-1][pos.y-1].getClienteId() == 0 &&
        this.m_cells[pos.x-1][pos.y-1].getNpc() == null &&
        this.m_cells[pos.x-1][pos.y-1].getTrigger() != POSINVALIDA;
    }
    
    public boolean existIndex(MapPos pos) {
        return this.m_cells[pos.x][pos.y].getClienteId() == 0;
    }
    
    private boolean esPosLibrePj(byte  x, byte y, boolean navegando, boolean esAdmin) {
    	if (esAdmin) {
			return esPosLibreAdmin(x, y); // Los Admins no respetan las leyes de la física :P
		} else if (navegando) {
			return esPosLibreConAgua(x, y) && !hayTeleport(x, y) && !estaBloqueado(x, y);
		} else {
			return esPosLibreSinAgua(x, y) && !hayTeleport(x, y) && !estaBloqueado(x, y);
		}
    }
    
    /** 
     * Busca una posicion válida para un PJ, y que sea lo más cercana a la posición original 
     */
    public MapPos closestLegalPosPj(byte orig_x, byte orig_y, boolean navegando, boolean esAdmin) {
        if (esPosLibrePj(orig_x, orig_y, navegando, esAdmin)) {
			return MapPos.mxy(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            byte x1 = (byte) (orig_x - radio);
            byte x2 = (byte) (orig_x + radio);
            byte y1 = (byte) (orig_y - radio);
            byte y2 = (byte) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (byte x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibrePj(x, y1, navegando, esAdmin)) {
					return MapPos.mxy(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibrePj(x, y2, navegando, esAdmin)) {
					return MapPos.mxy(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibrePj(x1, y, navegando, esAdmin)) {
					return MapPos.mxy(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibrePj(x2, y, navegando, esAdmin)) {
					return MapPos.mxy(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean esPosLibreNpc(byte  x, byte y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
    	if (hayAgua(x, y)) {
        	// Si el npc puede estar sobre agua, comprobamos si es una posicion libre con agua
            if (esAguaValida) {
				return esPosLibreConAgua(x, y) && !hayTeleport(x, y) && !hayObjeto(x, y) && testSpawnTriggerNpc(this.nroMapa, x, y, bajoTecho);
			}
    	} else {
            // Comprobamos si es una posición libre sin agua
            if (!esTierraInvalida) {
				return esPosLibreSinAgua(x, y) && !hayTeleport(x, y) && !hayObjeto(x, y) && testSpawnTriggerNpc(this.nroMapa, x, y, bajoTecho);
			}
    	}
        return false;
    }
    
    /** 
     * Busca una posicion válida para un Npc, y que sea lo más cercana a la posición original 
     */
    public MapPos closestLegalPosNpc(byte orig_x, byte orig_y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
        if (esPosLibreNpc(orig_x, orig_y, esAguaValida, esTierraInvalida, bajoTecho)) {
			return MapPos.mxy(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            byte x1 = (byte) (orig_x - radio);
            byte x2 = (byte) (orig_x + radio);
            byte y1 = (byte) (orig_y - radio);
            byte y2 = (byte) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (byte x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibreNpc(x, y1, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibreNpc(x, y2, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibreNpc(x1, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibreNpc(x2, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean esPosLibreConAgua(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() && 
        this.m_cells[x-1][y-1].getClienteId() == 0 &&
        this.m_cells[x-1][y-1].getNpc() == null && 
        hayAgua(x,y);
    }
    
    private boolean esPosLibreSinAgua(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() &&
        this.m_cells[x-1][y-1].getClienteId() == 0 &&
        this.m_cells[x-1][y-1].getNpc() == null &&
        !hayAgua(x,y);
    }
    
    private boolean esPosLibreAdmin(byte  x, byte y) {
    	// Los Admins no respetan las leyes de la física :P
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return this.m_cells[x-1][y-1].getClienteId() == 0 && this.m_cells[x-1][y-1].getNpc() == null;
    }
    
    /** Devuelve la cantidad de enemigos que hay en el mapa */
    public int getCantHostiles() {
        // NPCHostiles
        int cant = 0;
        for (Object element : this.m_npcs) {
            Npc npc = (Npc) element;
            // ¿esta vivo?
            if (npc.isNpcActive() && npc.esHostil() && npc.m_estads.Alineacion == 2) {
                cant++;
            }
        }
        return cant;
    }
    
    public void doFX() {
        if (getCantUsuarios() > 0 && Util.Azar(1, 150) < 12) {
            switch (this.m_terreno) {
                case TERRENO_BOSQUE:
                    int n = Util.Azar(1, 100);
                    switch (this.m_zona) {
                        case ZONA_CAMPO:
                        case ZONA_CIUDAD:
                            if (!this.server.estaLloviendo()) {
                                if (n < 15) {
                                   // enviarATodos(MSG_TW, SND_AVE2);
                                } else if (n < 30) {
                                  //  enviarATodos(MSG_TW, SND_AVE);
                                } else if (n <= 35) {
                                  //  enviarATodos(MSG_TW, SND_GRILLO);
                                } else if (n <= 40) {
                                 //   enviarATodos(MSG_TW, SND_GRILLO2);
                                } else if (n <= 45) {
                                 //   enviarATodos(MSG_TW, SND_AVE3);
                                }
                            }
                            break;
                    }
            }
        }
    }
    
    // Esto no hace falta por ahora, era para hacer funcionar a los teleports,
    // y estoy haciendo que los teleports trabajen de otra manera.
    //    public void doTileEvents(Cliente cliente) {
    //        //Public Sub DoTileEvents(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer)
    //        boolean hayFX = false;
    //        // Controla las salidas
    //        if (cliente.getPos().isValid()) {
    //            short x = cliente.getPos().x;
    //            short y = cliente.getPos().y;
    //            if (hayObjeto(x, y)) {
    //                hayFX = (getObjeto(x, y).getInfo().ObjType == OBJTYPE_TELEPORT);
    //            }
    //            if (hayTeleport(x, y)) {
    //                // ¿Es mapa de newbies?
    //                if (m_restringir) {
    //                    // ¿El usuario es un newbie?
    //                    if (cliente.esNewbie()) {
    //                        if (LegalPos(MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y, PuedeAtravesarAgua(UserIndex)) Then
    //                            If FxFlag Then // ¿FX?
    //                                Call WarpUserChar(UserIndex, MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y, True)
    //                            Else
    //                                Call WarpUserChar(UserIndex, MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y)
    //                            End If
    //                        Else
    //                            Call ClosestLegalPos(MapData(Map, X, Y).TileExit, nPos)
    //                            If nPos.X <> 0 And nPos.Y <> 0 Then
    //                                If FxFlag Then
    //                                    Call WarpUserChar(UserIndex, nPos.Map, nPos.X, nPos.Y, True)
    //                                Else
    //                                    Call WarpUserChar(UserIndex, nPos.Map, nPos.X, nPos.Y)
    //                                End If
    //                            End If
    //                        End If
    //                    } else { // No es newbie
    //                        Call SendData(ToIndex, UserIndex, 0, "||Mapa exclusivo para newbies." & FONTTYPE_INFO)
    //                        Call ClosestLegalPos(UserList(UserIndex).Pos, nPos)
    //                        If nPos.X <> 0 And nPos.Y <> 0 Then
    //                                Call WarpUserChar(UserIndex, nPos.Map, nPos.X, nPos.Y)
    //                        End If
    //                    }
    //                } else { // No es un mapa de newbies
    //                    If LegalPos(MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y, PuedeAtravesarAgua(UserIndex)) Then
    //                        If FxFlag Then
    //                            Call WarpUserChar(UserIndex, MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y, True)
    //                        Else
    //                            Call WarpUserChar(UserIndex, MapData(Map, X, Y).TileExit.Map, MapData(Map, X, Y).TileExit.X, MapData(Map, X, Y).TileExit.Y)
    //                        End If
    //                    Else
    //                        Call ClosestLegalPos(MapData(Map, X, Y).TileExit, nPos)
    //                        If nPos.X <> 0 And nPos.Y <> 0 Then
    //                            If FxFlag Then
    //                                Call WarpUserChar(UserIndex, nPos.Map, nPos.X, nPos.Y, True)
    //                            Else
    //                                Call WarpUserChar(UserIndex, nPos.Map, nPos.X, nPos.Y)
    //                            End If
    //                        End If
    //                    End If
    //                }
    //            }
    //        }
    //    }
    
    
    //Agus: ARREGLAR ESTO MOMENTANEAMENTE DESACTIVADO
    // FIXME
    public void saveMapData() {
        // Public Sub SaveMapData(ByVal N As Integer)
       // saveInfFile("worldBackup" + File.separator + "Mapa" + this.nroMapa + ".inf");
       // saveMapFile("worldBackup" + File.separator + "Mapa" + this.nroMapa + ".map");
       // saveDat("worldBackup" + File.separator + "Mapa" + this.nroMapa + ".dat");
    }
    
    private void saveDat(String datFileName) {
        // Escribir archivo .dat
        try {
            IniFile ini = new IniFile();
            String section = "Mapa" + this.nroMapa;
            ini.setValue(section, "Name", this.m_name);
            ini.setValue(section, "MusicNum", this.m_music);
            ini.setValue(section, "StartPos", this.m_startPos.map + "-" + this.m_startPos.x + "-" + this.m_startPos.y);
            ini.setValue(section, "Terreno", TERRENOS[this.m_terreno]);
            ini.setValue(section, "Zona", ZONAS[this.m_zona]);
            ini.setValue(section, "Restringir", (this.m_restringir ? "Si" : "No"));
            ini.setValue(section, "BackUp", this.m_backup);
            ini.setValue(section, "PK", this.m_pk);
            ini.store(datFileName);
        } catch (Exception e) {
            log.fatal("ERROR GUARDANDO MAPA " + this.nroMapa, e);
        }
    }
    
    private void saveMapFile(String filename) {
        ////////// ARCHIVO .MAP
        // guardar cabecera del archivo .map
        log.info("Guardando mapa: " + filename);
        try {
            DataOutputStream f =
            new DataOutputStream(
            new BufferedOutputStream(
            new FileOutputStream(filename)));
            try {
                f.writeShort(Util.leShort(this.version));
                byte tmp[] = new byte[255];
                for (int i = 0; i < this.desc.length() && i < 256; i++) {
					tmp[i] = (byte)this.desc.charAt(i);
				}
                f.write(tmp); // 255 bytes... por que no 256??
                f.writeInt(0); // crc
                f.writeInt(0); // mw
                f.writeLong(0); // 8 bytes de relleno.
                // leer detalle del archivo .map
                for (int y = 0; y < MAPA_ALTO; y++) {
                    for (int x = 0; x < MAPA_ANCHO; x++) {
                    	f.writeByte(this.m_cells[x][y].estaBloqueado() ? 1 : 0);
                        //f.writeShort(this.m_cells[x][y].hayAgua() ? Util.leShort((short)1505) : 0);
                        f.writeShort(0);
                        f.writeShort(0);
                        f.writeShort(0);
                        f.writeShort(Util.leShort(this.m_cells[x][y].getTrigger()));
                        f.writeShort(0); // 2 bytes de relleno.
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveMapFile " + this.nroMapa, e);
        }
    }
    
    // FIXME!!!
    private void saveInfFile(String filename) {
        //////// ARCHIVO .INF
        log.info("Guardando mapa: " + filename);
        try {
            DataOutputStream f =
            new DataOutputStream(
            new BufferedOutputStream(
            new FileOutputStream(filename)));
            try {
                byte[] skip10 = new byte[10];
                f.write(skip10); // saltear los primeros 10 bytes.
                // leer detalle del archivo .inf
                for (short y = 0; y < MAPA_ALTO; y++) {
                    for (short x = 0; x < MAPA_ANCHO; x++) {
                        // Es el destino a otro lugar (para teleports, etc.)
                        // Son cero (0) si no hay un Teleport en esta celda.
                        if (this.m_cells[x][y].hayTeleport()) {
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().map));
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().x));
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().y));
                        } else {
                            f.writeShort(0);
                            f.writeShort(0);
                            f.writeShort(0);
                        }
                        // Indice del Npc que esta en este bloque.
                        // Es cero (0) si no hay Npc.
                        f.writeShort((this.m_cells[x][y].getNpc() != null) ? Util.leShort((short)this.m_cells[x][y].getNpc().getNumero()) : 0);
                        // Indice del Objeto que esta en este bloque.
                        // Es cero (0) si no hay objeto.
                        if (this.m_cells[x][y].hayObjeto()) {
                            f.writeShort(Util.leShort(getObjeto((byte)(x+1), (byte)(y+1)).getInfo().ObjIndex));
                            f.writeShort(Util.leShort((short)getObjeto((byte)(x+1), (byte)(y+1)).obj_cant));
                        } else {
                            f.writeInt(0);
                        }
                        f.writeShort(0); // Indice del cliente que esta en este bloque.
                        f.writeShort(0); // Saltear 2 bytes de relleno...
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveInfFile " + this.nroMapa, e);
        }
    }
    
    public void objectMassDestroy(byte pos_x, byte pos_y) {
        byte x1 = (byte) (pos_x - MinXBorder + 1);
        byte x2 = (byte) (pos_x + MaxXBorder + 1);
        byte y1 = (byte) (pos_y - MinYBorder + 1);
        byte y2 = (byte) (pos_y + MaxYBorder + 1);
        if (x1 < 1) {
			x1 = 1;
		}
        if (x2 > 100) {
			x2 = 100;
		}
        if (y1 < 1) {
			y1 = 1;
		}
        if (y2 > 100) {
			y2 = 100;
		}
        for (byte y = y1; y <= y2; y++) {
            for (byte x = x1; x <= x2; x++) {
                if (hayObjeto(x, y) && getObjeto(x, y).getInfo().itemNoEsDeMapa()) {
                    quitarObjeto(x, y);
                }
            }
        }
    }    
    
    public void construirAreaObj(Player cliente, byte  x, byte y) {
    	MapObject obj = getObjeto(x,y);
    	if (obj != null) cliente.enviar(new ObjectCreateResponse((byte)x, (byte)y, (short)obj.getInfo().GrhIndex));
    }
    
    public void construirAreaNpc(Player cliente, Npc npc) {
    	cliente.enviar(npc.createCC());
    }
    
}

