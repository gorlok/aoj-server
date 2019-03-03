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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjType;
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
import org.ArgentumOnline.server.protocol.ShowSignalResponse;
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
    private short mapNumber;
    public AreasAO areasData = new AreasAO();
    
    // Cabecera archivo .map
    private short version   = 0;
    //private byte desc[] = new byte[255];
    private String desc = "";
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
    String m_restringir = ""; // Restringir = "NEWBIE" (newbies), "ARMADA", "CAOS", "FACCION" or "NO".
    public boolean m_backup = false;
    short   m_version = 0; // FIXME se usa?
    MapPos m_startPos = MapPos.empty(); // FIXME se usa?
    
    GameServer server;
    
    MapCell cells[][] = new MapCell[MAPA_ANCHO][MAPA_ALTO];
    
    List<MapCell> objects = new ArrayList<MapCell>();
    List<Player> players = new ArrayList<Player>();
    List<Npc> npcs = new ArrayList<Npc>();
    
    /** Creates a new instance of Map */
    public Map(short nroMapa, GameServer server) {
    	this.server = server;
        this.mapNumber = nroMapa;
        
        this.areasData.initAreas(server, this);
        
       // this.areasData.makeAreas(server, nroMapa);
        

        
        for (int x = 0; x < MAPA_ANCHO; x++) {
            for (int y = 0; y < MAPA_ALTO; y++) {
                this.cells[x][y] = new MapCell((short)(x+1), (short)(y+1));
            }
        }
    }
    
    public int getNroMapa() {
        return this.mapNumber;
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
    	if (this.cells[x-1][y-1].hayObjeto()) {
			return new MapObject(this.cells[x-1][y-1].getObjInd(),
    				this.cells[x-1][y-1].getObjCant(), x, y);
		}
		return null;
    }
    
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
	
    public Npc getNPC(byte  x, byte y) {
        return this.cells[x-1][y-1].getNpc();
    }
    
    public short getTrigger(byte  x, byte y) {
        return this.cells[x-1][y-1].getTrigger();
    }
    
    public void setTrigger(byte  x, byte y, byte value) {
        this.cells[x-1][y-1].setTrigger(value);
    }
    
    public boolean testSpawnTrigger(byte  x, byte y) {
        int trigger = this.cells[x-1][y-1].getTrigger();
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
    
    //FIX BY AGUSH
    public boolean hayAgua(byte  x, byte y) {    	
    	if (x < MAPA_ANCHO && x > 0 &&
    			y < MAPA_ALTO && y > 0) {    		
            
            if (this.cells[x-1][y-1].getGrh(0) >= 1505 
            		&& this.cells[x-1][y-1].getGrh(0) <= 1520 &&
            		this.cells[x-1][y-1].getGrh(1) == 0) {
            	return true;
            }
            
    	}
    	
    	return false;

    }
    
    public boolean hasPlayer(Player player) {
        return this.players.contains(player);
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
        return this.players.size();
    }
    
    public List<String> getUsuarios() {
        List<String> userNames = new LinkedList<>();
        for (Player cli: this.players) {
            if (!"".equals(cli.getNick())) {
                userNames.add(cli.getNick());
            }
        }
        return userNames;
    }
    
    // FIXME
    public Player spUser(int value) {
    	return this.players.get(value);
    }
    
    // FIXME
    public Npc spNpc(int value) {
    	return this.npcs.get(value);
    }
    
    public boolean esZonaSegura() {
        return this.m_pk;
    }
    
    public void load(boolean loadBackup) {
        try {
            loadDatFile("Mapa" + this.mapNumber + ".dat", loadBackup);
            loadMapFile("Mapa" + this.mapNumber + ".map", loadBackup);
            loadInfFile("Mapa" + this.mapNumber + ".inf", loadBackup);
        } catch (java.io.FileNotFoundException e) {
            log.warn("Archivo de mapa %d faltante.", this.mapNumber);
        } catch (java.io.IOException e) {
            log.warn("Error leyendo archivo de mapa %d", this.mapNumber);
        } catch (Exception e) {
        	log.warn("Error con mapa " + this.mapNumber, e);
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
        String section = "Mapa" + this.mapNumber;
        this.m_name = ini.getString(section, "Name");
        this.m_music = ini.getString(section, "MusicNum");
        this.m_pk = (ini.getInt(section, "PK") == 1);
        this.m_restringir = ini.getString(section, "Restringir");
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
                		this.cells[x][y].blocked(true);
                	}
                    
                	this.cells[x][y].setGrh(0, Util.leShort(reader.readShort()));
                    
                    if ((byflags & 2) == 2) {
                    	this.cells[x][y].setGrh(1, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 4) == 4) {
                    	this.cells[x][y].setGrh(2, Util.leShort(reader.readShort()));
                    }
                    if ((byflags & 8) == 8) {
                    	this.cells[x][y].setGrh(3, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 16) == 16) {
                    	this.cells[x][y].setTrigger((byte) Util.leShort(reader.readShort()));
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
						this.cells[x][y].teleport(MapPos.mxy(Util.leShort(reader.readShort())
								,Util.leShort(reader.readShort()), Util.leShort(reader.readShort())));
	            	}
	                
	                if ((byflags & 2) == 2) {
	                    short npcId = Util.leShort(reader.readShort());
	                    if (npcId > 0) {
	                        // Crear un nuevo Npc.
	                        this.cells[x][y].setNpc(this.server.createNpc(npcId));
	                        npc = this.cells[x][y].getNpc();
	                        this.npcs.add(npc);
	                        npc.pos().map = this.mapNumber;
	                        npc.pos().x = (byte) (x+1);
	                        npc.pos().y = (byte) (y+1);
	                        npc.getOrig().map = this.mapNumber;
	                        if (npc.respawnOrigPos()) {
		                        npc.getOrig().x = (byte) (x+1);
		                        npc.getOrig().y = (byte) (y+1);
	                        } else {
		                        npc.getOrig().x = (byte)0;
		                        npc.getOrig().y = (byte)0;                        
	                        }
	                        npc.activate();
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
    
    public boolean enterMap(Player player, byte  x, byte y) {
        if (this.cells[x-1][y-1].playerId() != 0) {
			return false;
		}
        this.players.add(player);
        this.cells[x-1][y-1].playerId(player.getId());
        enviarAlArea(x, y, player.getId(), player.createCC());
        player.pos().set(this.mapNumber, x, y);
        return true;
    }
    
    public boolean exitMap(Player player) {
        short x = player.pos().x;
        short y = player.pos().y;
        try {
        	try {
        		this.areasData.sendToUserArea(player, new RemoveCharDialogResponse(player.getId()));
        		this.areasData.sendToUserArea(player, new CharacterRemoveResponse(player.getId()));
        	} finally {
        		this.players.remove(player);
        		this.areasData.resetUser(player);
        	}
        } finally {
	        if (this.cells[x-1][y-1].playerId() != player.getId()) {
	        	log.fatal("INCONSISTENCIA: el jugador no se encuentra donde debería");
	            return false;
	        }
	        this.cells[x-1][y-1].playerId((short) 0);
        }
        return true;
    }
    
    public void moverNpc(Npc npc, byte x, byte y) {
        if (this.cells[x-1][y-1].getNpc() != null) {
			log.fatal("ERRRRRRRRRRORRRRRRRRRRRR en moverNpc: " + npc);
		}
        this.cells[npc.pos().x-1][npc.pos().y-1].setNpc(null);
        this.cells[x-1][y-1].setNpc(npc);
        
        this.areasData.checkUpdateNeededNpc(npc, npc.infoChar().getDir());
        this.areasData.sendToNPCArea(npc, new CharacterMoveResponse(npc.getId(), x, y));
        
    }
    
    public boolean isFree(byte  x, byte y) {
        return (this.cells[x-1][y-1].playerId() == 0) && (this.cells[x-1][y-1].getNpc() == null);
    }
    
    public boolean isBlocked(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return true;
		}
        return this.cells[x-1][y-1].isBlocked();
    }
    
    public boolean enterNpc(Npc npc, byte  x, byte y) {
        if (this.cells[x-1][y-1].getNpc() != null) {
			return false;
		}
        
        this.cells[x-1][y-1].setNpc(npc);
        this.npcs.add(npc);
        
        npc.pos().set(this.mapNumber, x, y);
      
        this.areasData.loadNpc(npc);
        
        // FIXME
		//this.areasData.setNpcArea(npc);
		//this.areasData.sendToArea(x, y, -1, serverPacketID.MSG_CCNPC, npc.ccParams());
        
        return true;
    }
    
    public boolean exitNpc(Npc npc) {
        byte x = npc.pos().x;
        byte y = npc.pos().y;
        try {
        	enviarAlArea(x, y, new RemoveCharDialogResponse(npc.getId()));
	        enviarAlArea(x, y, new CharacterRemoveResponse(npc.getId()));
        } finally {
            this.cells[x-1][y-1].setNpc(null);
	        this.npcs.remove(npc);
	        npc.pos().set(this.mapNumber, (short)0, (short)0);
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
            	npc = this.cells[x-1][y-1].getNpc();
                if (npc != null) { 
                	
                	//agush: fix mascotas ;-)
                	if (npc.getPetUserOwner() != null) {
                		Player masterUser = (npc.getPetUserOwner());
                		masterUser.quitarMascota(npc);
                	}
                	exitNpc(npc);
                }
            }
        }
    }    
    
    public boolean agregarObjeto(short objid, int cant, byte x, byte y) {
    	if (hasObject(x, y)) {
            log.warn("Intento de agregar objeto sobre otro: objid=" + objid + " cant" + cant + " mapa" + this.mapNumber + " x=" + x + " y=" + y);
    		return false;
    	}
    	this.cells[x-1][y-1].setObj(objid, cant);
        this.objects.add(this.cells[x-1][y-1]);
        short grhIndex = findObj(objid).GrhIndex;
        
        if (DEBUG) 
        	log.debug(grhIndex + "-" + x + "-" + y);
        
        this.areasData.sendToAreaByPos(this, x, y, new ObjectCreateResponse((byte)x ,(byte)y, (short)grhIndex));
        return true;
    }
    
    public void quitarObjeto(byte  x, byte y) {
    	this.objects.remove(this.cells[x-1][y-1]);
        this.cells[x-1][y-1].quitarObjeto();
        enviarAlArea(x, y, new ObjectDeleteResponse(x,y));
    }
    
    public void bloquearTerreno(byte x, byte y) {
        this.cells[x-1][y-1].blocked(true);
        this.cells[x-1][y-1].modified(true);
        enviarAlArea(x, y, new BlockPositionResponse(x, y, (byte)1));
    }
    
    public void desbloquearTerreno(byte x, byte y) {
        this.cells[x-1][y-1].blocked(false);
        this.cells[x-1][y-1].modified(true);
        enviarAlArea(x, y, new BlockPositionResponse(x, y, (byte)0));
    }
    
    public void abrirCerrarPuerta(MapObject obj) {
        if (obj.getInfo().objType == ObjType.Puertas) {
            // Es un objeto tipo puerta.
            if (obj.getInfo().estaCerrada()) {
                // Abrir puerta.
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = findObj(obj.getInfo().IndexAbierta);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            } else {
                // Cerrar puerta
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = findObj(obj.getInfo().IndexCerrada);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            }
            obj = getObjeto(obj.x, obj.y);
            enviarPuerta(obj);
            enviarAlArea(obj.x, obj.y, new PlayWaveResponse(SND_PUERTA, obj.x, obj.y));
        }
    }
    
    private void enviarPuerta(MapObject obj) {
        if (obj.getInfo().objType == ObjType.Puertas) {
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
    
    public void sendCreateFX(byte  x, byte y, int id, int fx, int val) {
    	enviarAlArea(x,y, new CreateFXResponse((short) id, (short) fx, (short) val));
    }
    
    public void enviarATodos(ServerPacket packet) {
        enviarATodosExc(-1, packet);
    }
    
    public void enviarATodosExc(int exceptId, ServerPacket packet) {
        for (Player player : this.players) {
            if (player.getId() != exceptId) {
        		player.sendPacket(packet);
            }
        }
    }
    
    public void enviarAlArea(byte x, byte y, ServerPacket packet) {
        enviarAlArea(x, y, -1, packet);
    }
        
    public void enviarAlArea(byte pos_x, byte pos_y, int excepto, ServerPacket packet) {
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
        Player player;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
                if (this.cells[x-1][y-1].playerId() > 0) {
                    player = this.server.playerById(this.cells[x-1][y-1].playerId());
                    if (player != null && (player.isGod() || player.isDemiGod())) {
						player.sendPacket(packet);
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
                if (this.cells[x-1][y-1].playerId() == cli_id) {
					return this.server.playerById(this.cells[x-1][y-1].playerId());
				}
            }
        }
        return null;
    }
    
    /** Enviarme los otros jugadores del mapa, sin contarme a mi */
    public void sendOtherPlayers(Player player) {
    	for (Player otherPlayer : this.players) { 
    		if (!otherPlayer.equals(player)) {
    			player.sendPacket(otherPlayer.createCC());
    		}
    	}
    }
    
    /** Enviarme los objetos del mapa. */
    public void sendObjects(Player player) {
        for (MapCell object : this.objects) {
            player.sendObject(object.getObjInd(), object.getX(), object.getY());
        }
    }
    
    /** Enviarme los NPCs del mapa. */
    public void sendNpcs(Player player) {
        for (Npc npc : this.npcs) {
            player.sendPacket(npc.createCC());
        }
    }
    
    /** Enviarme las posiciones bloqueadas del mapa. */
    public void sendBlockedPositions(Player player) {
        for (int y = 0; y < MAPA_ALTO; y++) {
            for (int x = 0; x < MAPA_ANCHO; x++) {
                if (this.cells[x][y].isModified()) {
					player.sendBlockedPosition(x+1, y+1, this.cells[x][y].isBlocked());
				}
            }
        }
    }
    
    public void movePlayer(Player player, byte x, byte y) {
        this.cells[player.pos().x-1][player.pos().y-1].playerId((short) 0);
        this.cells[x-1][y-1].playerId(player.getId());
        player.pos().set(this.mapNumber, x, y);
        
		//JAO: Nuevo sistema de areas !!
        this.areasData.sendToAreaButIndex(this, x, y, player.getId(),
        		new CharacterMoveResponse(player.getId(), x, y));
        this.areasData.checkUpdateNeededUser(player, player.infoChar().getDir());
    }
    
    public boolean isTeleport(byte  x, byte y) {
        return this.cells[x-1][y-1].isTeleport();
    }
    
    public boolean isTeleportObject(byte x, byte y) {
    	return isTeleport(x, y) 
    			&& hasObject(x, y) 
				&& (getObjeto(x, y).getInfo().objType == ObjType.Teleport);    	
    }
    
    public void createTeleport(byte x, byte y, short dest_mapa, byte dest_x, byte dest_y) {
        if (dest_mapa > 0 && dest_x > 0 && dest_y > 0) {
			this.cells[x-1][y-1].teleport(MapPos.mxy(dest_mapa, dest_x, dest_y));
		}
        agregarObjeto(OBJ_TELEPORT, 1, x, y);
    }
    
    public void destroyTeleport(byte  x, byte y) {
        if (!isTeleport(x, y)) {
			return;
		}
        quitarObjeto(x, y);
        this.cells[x-1][y-1].teleport(null);
    }    
    
    public boolean hasObject(byte  x, byte y) {
        return (this.cells[x-1][y-1].hayObjeto());
    }
    
    public boolean hasPlayer(byte  x, byte y) {
        return (this.cells[x-1][y-1].playerId() != 0 && getPlayer(x, y) != null); // FIXME
    }
    
    public boolean hasNpc(byte  x, byte y) {
        return (this.cells[x-1][y-1].getNpc() != null);
    }
    
    public Player getPlayer(byte  x, byte y) {
        return this.server.playerById(this.cells[x-1][y-1].playerId());
    }
    
    public Npc getNpc(byte  x, byte y) {
        return this.cells[x-1][y-1].getNpc();
    }
    
    public MapObject queryObject(byte  x, byte y) {
        // Ver si hay un objeto en los alrededores...
        if (hasObject(x, y)) {
            return getObjeto(x, y);
        }
        if (hasObject((byte) (x+1), y)) {
            return getObjeto((byte) (x+1), y);
        }
        if (hasObject((byte) (x+1), (byte) (y+1))) {
            return getObjeto((byte) (x+1), (byte) (y+1));
        }
        if (hasObject(x, (byte) (y+1))) {
            return getObjeto(x, (byte) (y+1));
        }
        return null;
    }
    public Player queryPlayer(byte  x, byte y) {
        // Ver si hay un jugador en los alrededores...
        if (hasPlayer(x, (byte) (y+1))) {
            return getPlayer(x, (byte) (y+1));
        }
        if (hasPlayer(x, y)) {
            return getPlayer(x, y);
        }
        return null;
    }
    public Npc queryNpc(byte  x, byte y) {
        // Ver si hay un NPC en los alrededores...
        if (hasNpc(x, (byte) (y+1))) {
            return getNpc(x, (byte) (y+1));
        }
        if (hasNpc(x, y)) {
            return getNpc(x, y);
        }
        return null;
    }
    
    public void lookAtTile(Player player, byte  x, byte y) {
    	if (!player.pos().inRangoVision(x, y)) {
            return;
        }
        boolean foundSomething = false;
        
        // Ver si hay un objeto en los alrededores...
        MapObject obj = queryObject(x, y);
        if (obj != null) {
            player.enviarMensaje(obj.getInfo().Nombre + " - " + obj.obj_cant, FontType.FONTTYPE_INFO);
            player.flags().TargetObj = obj.getInfo().ObjIndex;
            player.flags().TargetObjMap = this.mapNumber;
            player.flags().TargetObjX = obj.x;
            player.flags().TargetObjY = obj.y;
            foundSomething = true;
        }
        
        // Ver si hay un Npc...
        Npc npc;
        if ((npc = queryNpc(x, y)) != null) {
            foundSomething = true;
            if (npc.m_desc.length() > 0) {
                player.enviarHabla(COLOR_BLANCO, npc.m_desc, npc.getId());
            } 
            String msg = "";
            if (npc.getPetUserOwner() != null) {
                msg = npc.m_name + " es mascota de " + npc.getPetUserOwner().getNick();
            } else {
                msg = npc.m_name;
            }
            msg = msg + " " + npc.estadoVida(player);
            player.enviarMensaje(msg, FontType.FONTTYPE_INFO);
            player.flags().TargetNpc = npc.getId();
            player.flags().TargetMap = this.mapNumber;
            player.flags().TargetX = x;
            player.flags().TargetY = y;
            player.flags().TargetUser = 0;
            player.flags().TargetObj = 0;
        }
        
        // Ver si hay un jugador
        Player anotherPlayer;
        if ((anotherPlayer = queryPlayer(x, y)) != null) {
            if (!anotherPlayer.flags().AdminInvisible) {
                player.enviarMensaje("Ves a " + anotherPlayer.getTagsDesc(), anotherPlayer.getTagColor());
                player.flags().TargetUser = anotherPlayer.getId();
                player.flags().TargetNpc = 0;
                player.flags().TargetObj = 0;
                player.flags().TargetMap = this.mapNumber;
                player.flags().TargetX = x;
                player.flags().TargetY = y;
                foundSomething = true;
            }
        }
        
        if (!foundSomething) {
            player.flags().TargetNpc = 0;
            player.flags().TargetNpcTipo = 0;
            player.flags().TargetUser = 0;
            player.flags().TargetObj = 0;
            player.flags().TargetObjMap = 0;
            player.flags().TargetObjX = 0;
            player.flags().TargetObjY = 0;
            player.flags().TargetMap = this.mapNumber;
            player.flags().TargetX = x;
            player.flags().TargetY = y;
            player.enviarMensaje("No ves nada interesante.", FontType.FONTTYPE_INFO);
        }

        // FIXME: REVISAR SI ESTO VA...
        player.flags().TargetX = x;
        player.flags().TargetY = y;
    }
    
    public MapPos teleportTarget(byte  x, byte y) {
        return this.cells[x-1][y-1].teleport();
    }
    
    public void accionParaRamita(byte x, byte y, Player player) {
        if (Util.distance(player.pos().x, player.pos().y, x, y) > 2) {
            player.enviarMensaje("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        int suerte = 0;
        int skillSupervivencia = player.skills().get(Skill.SKILL_Supervivencia);        
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
            player.enviarMensaje("Has prendido la fogata.", FontType.FONTTYPE_INFO);
         //   enviarAlArea(x, y, MSG_FO);
        } else {
            player.enviarMensaje("No has podido hacer fuego.", FontType.FONTTYPE_INFO);
        }
        // Si no tiene hambre o sed quizas suba el skill supervivencia
        if (!player.flags().Hambre && !player.flags().Sed) {
			player.subirSkill(Skill.SKILL_Supervivencia);
		}
    }
    
    public void accionParaForo(byte  x, byte y, Player player) {
        if (Util.distance(player.pos().x, player.pos().y, x, y) > 2) {
            player.enviarMensaje("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        // ¿Hay mensajes?
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().objType != ObjType.Foros) {
            return;
        }
        String foroId = obj.getInfo().ForoID;
        this.server.getForumManager().enviarMensajesForo(foroId, player);
    }
    
    public void accionParaPuerta(byte  x, byte y, Player player) {
        if (Util.distance(player.pos().x, player.pos().y, x, y) > 2) {
            player.enviarMensaje("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().objType != ObjType.Puertas) {
			return;
		}
        if (obj.getInfo().Clave == 0) {
            abrirCerrarPuerta(obj);
            player.flags().TargetObj = obj.getInfo().ObjIndex;
        } else {
            player.enviarMensaje("La puerta esta cerrada con llave.", FontType.FONTTYPE_INFO);
        }
    }
    
    public void accionParaCartel(byte  x, byte y, Player player) {
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().objType != ObjType.Carteles) {
			return;
		}
        if (obj.getInfo().Texto.length() > 0) {
        	player.sendPacket(new ShowSignalResponse(obj.getInfo().Texto, obj.getInfo().GrhSecundario));
        }
    }
    
    public MapPos tirarItemAlPiso(byte  x, byte y, InventoryObject obj) {
        MapPos newPos = tilelibre(x, y);
        if (newPos != null) {
	        log.debug("tirarItemAlPiso: x=" + newPos.x + " y=" + newPos.y);
	        if (newPos != null) {
	            if (agregarObjeto(obj.objid, obj.cant, newPos.x, newPos.y)) {
	                return newPos;
	            }
	        }
        }
        return null;
    }
    
    public boolean esPosLibreObjeto(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.cells[x-1][y-1].isBlocked() &&
        !hayAgua(x,y) &&
        !hasObject(x, y) && 
		!isTeleport(x, y);
    }
    
    /** 
     * Busca una posicion libre para depositar un objeto y que sea lo más cercana a la posición original 
     */
    public MapPos tilelibre(byte orig_x, byte orig_y) {
        if (esPosLibreObjeto(orig_x, orig_y)) {
			return MapPos.mxy(this.mapNumber, orig_x, orig_y);
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
					return MapPos.mxy(this.mapNumber, x, y1);
				}
                // lado inferior
                if (esPosLibreObjeto(x, y2)) {
					return MapPos.mxy(this.mapNumber, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibreObjeto(x1, y)) {
					return MapPos.mxy(this.mapNumber, x1, y);
				}
                // lado derecho
                if (esPosLibreObjeto(x2, y)) {
					return MapPos.mxy(this.mapNumber, x2, y);
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
            return !this.cells[pos.x-1][pos.y-1].isBlocked() &&
            this.cells[pos.x-1][pos.y-1].playerId() == 0 &&
            this.cells[pos.x-1][pos.y-1].getNpc() == null &&
            !hayAgua(pos.x, pos.y);
        }
        return !this.cells[pos.x-1][pos.y-1].isBlocked() &&
        this.cells[pos.x-1][pos.y-1].playerId() == 0 &&
        this.cells[pos.x-1][pos.y-1].getNpc() == null &&
        hayAgua(pos.x, pos.y);
    }
    
    public boolean isLegalPosNPC(MapPos pos, boolean puedeAgua) {
        if (!pos.isValid()) {
			return false;
		}
        if (!puedeAgua) {
            return !this.cells[pos.x-1][pos.y-1].isBlocked() &&
            this.cells[pos.x-1][pos.y-1].playerId() == 0 &&
            this.cells[pos.x-1][pos.y-1].getNpc() == null &&
            this.cells[pos.x-1][pos.y-1].getTrigger() != POSINVALIDA &&
            !hayAgua(pos.x, pos.y);
        }
        return !this.cells[pos.x-1][pos.y-1].isBlocked() &&
        this.cells[pos.x-1][pos.y-1].playerId() == 0 &&
        this.cells[pos.x-1][pos.y-1].getNpc() == null &&
        this.cells[pos.x-1][pos.y-1].getTrigger() != POSINVALIDA;
    }
    
    private boolean isFreePosForPlayer(byte  x, byte y, boolean navegando, boolean esAdmin) {
    	if (esAdmin) {
			return isFreePosForAdmin(x, y); // Los Admins no respetan las leyes de la física :P
		} else if (navegando) {
			return isFreePosWithWater(x, y) && !isTeleport(x, y) && !isBlocked(x, y);
		} else {
			return isFreePosWithoutWater(x, y) && !isTeleport(x, y) && !isBlocked(x, y);
		}
    }
    
    /** 
     * Busca una posicion válida para un jugador, y que sea lo más cercana a la posición original 
     */
    public MapPos closestLegalPosPlayer(byte orig_x, byte orig_y, boolean navegando, boolean esAdmin) {
        if (isFreePosForPlayer(orig_x, orig_y, navegando, esAdmin)) {
			return MapPos.mxy(this.mapNumber, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            byte x1 = (byte) (orig_x - radio);
            byte x2 = (byte) (orig_x + radio);
            byte y1 = (byte) (orig_y - radio);
            byte y2 = (byte) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (byte x = x1; x <= x2; x++) {
                // lado superior
                if (isFreePosForPlayer(x, y1, navegando, esAdmin)) {
					return MapPos.mxy(this.mapNumber, x, y1);
				}
                // lado inferior
                if (isFreePosForPlayer(x, y2, navegando, esAdmin)) {
					return MapPos.mxy(this.mapNumber, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (isFreePosForPlayer(x1, y, navegando, esAdmin)) {
					return MapPos.mxy(this.mapNumber, x1, y);
				}
                // lado derecho
                if (isFreePosForPlayer(x2, y, navegando, esAdmin)) {
					return MapPos.mxy(this.mapNumber, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean isFreePosForNpc(byte  x, byte y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
    	if (hayAgua(x, y)) {
        	// Si el npc puede estar sobre agua, comprobamos si es una posicion libre con agua
            if (esAguaValida) {
				return isFreePosWithWater(x, y) && !isTeleport(x, y) && !hasObject(x, y) && testSpawnTriggerNpc(this.mapNumber, x, y, bajoTecho);
			}
    	} else {
            // Comprobamos si es una posición libre sin agua
            if (!esTierraInvalida) {
				return isFreePosWithoutWater(x, y) && !isTeleport(x, y) && !hasObject(x, y) && testSpawnTriggerNpc(this.mapNumber, x, y, bajoTecho);
			}
    	}
        return false;
    }
    
    /** 
     * Busca una posicion válida para un Npc, y que sea lo más cercana a la posición original 
     */
    public MapPos closestLegalPosNpc(byte orig_x, byte orig_y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
        if (isFreePosForNpc(orig_x, orig_y, esAguaValida, esTierraInvalida, bajoTecho)) {
			return MapPos.mxy(this.mapNumber, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            byte x1 = (byte) (orig_x - radio);
            byte x2 = (byte) (orig_x + radio);
            byte y1 = (byte) (orig_y - radio);
            byte y2 = (byte) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (byte x = x1; x <= x2; x++) {
                // lado superior
                if (isFreePosForNpc(x, y1, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.mapNumber, x, y1);
				}
                // lado inferior
                if (isFreePosForNpc(x, y2, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.mapNumber, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (isFreePosForNpc(x1, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.mapNumber, x1, y);
				}
                // lado derecho
                if (isFreePosForNpc(x2, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return MapPos.mxy(this.mapNumber, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean isFreePosWithWater(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.cells[x-1][y-1].isBlocked() && 
        this.cells[x-1][y-1].playerId() == 0 &&
        this.cells[x-1][y-1].getNpc() == null && 
        hayAgua(x,y);
    }
    
    private boolean isFreePosWithoutWater(byte  x, byte y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.cells[x-1][y-1].isBlocked() &&
        this.cells[x-1][y-1].playerId() == 0 &&
        this.cells[x-1][y-1].getNpc() == null &&
        !hayAgua(x,y);
    }
    
    private boolean isFreePosForAdmin(byte  x, byte y) {
    	// Los Admins no respetan las leyes de la física :P
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return this.cells[x-1][y-1].playerId() == 0 && this.cells[x-1][y-1].getNpc() == null;
    }
    
    /** Devuelve la cantidad de enemigos que hay en el mapa */
    public int getHostilesCount() {
        // NPCHostiles
        int cant = 0;
        for (Object element : this.npcs) {
            Npc npc = (Npc) element;
            // ¿esta vivo?
            if (npc.isNpcActive() && npc.esHostil() && npc.m_estads.Alineacion == 2) {
                cant++;
            }
        }
        return cant;
    }
    
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
            String section = "Mapa" + this.mapNumber;
            ini.setValue(section, "Name", this.m_name);
            ini.setValue(section, "MusicNum", this.m_music);
            ini.setValue(section, "StartPos", this.m_startPos.map + "-" + this.m_startPos.x + "-" + this.m_startPos.y);
            ini.setValue(section, "Terreno", TERRENOS[this.m_terreno]);
            ini.setValue(section, "Zona", ZONAS[this.m_zona]);
            ini.setValue(section, "Restringir", this.m_restringir);
            ini.setValue(section, "BackUp", this.m_backup);
            ini.setValue(section, "PK", this.m_pk);
            ini.store(datFileName);
        } catch (Exception e) {
            log.fatal("ERROR GUARDANDO MAPA " + this.mapNumber, e);
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
                    	f.writeByte(this.cells[x][y].isBlocked() ? 1 : 0);
                        //f.writeShort(this.m_cells[x][y].hayAgua() ? Util.leShort((short)1505) : 0);
                        f.writeShort(0);
                        f.writeShort(0);
                        f.writeShort(0);
                        f.writeShort(Util.leShort(this.cells[x][y].getTrigger()));
                        f.writeShort(0); // 2 bytes de relleno.
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveMapFile " + this.mapNumber, e);
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
                        if (this.cells[x][y].isTeleport()) {
                            f.writeShort(Util.leShort(this.cells[x][y].teleport().map));
                            f.writeShort(Util.leShort(this.cells[x][y].teleport().x));
                            f.writeShort(Util.leShort(this.cells[x][y].teleport().y));
                        } else {
                            f.writeShort(0);
                            f.writeShort(0);
                            f.writeShort(0);
                        }
                        // Indice del Npc que esta en este bloque.
                        // Es cero (0) si no hay Npc.
                        f.writeShort((this.cells[x][y].getNpc() != null) ? Util.leShort((short)this.cells[x][y].getNpc().getNumero()) : 0);
                        // Indice del Objeto que esta en este bloque.
                        // Es cero (0) si no hay objeto.
                        if (this.cells[x][y].hayObjeto()) {
                            f.writeShort(Util.leShort(getObjeto((byte)(x+1), (byte)(y+1)).getInfo().ObjIndex));
                            f.writeShort(Util.leShort((short)getObjeto((byte)(x+1), (byte)(y+1)).obj_cant));
                        } else {
                            f.writeInt(0);
                        }
                        f.writeShort(0); // Indice del jugador que esta en este bloque.
                        f.writeShort(0); // Saltear 2 bytes de relleno...
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveInfFile " + this.mapNumber, e);
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
                if (hasObject(x, y) && getObjeto(x, y).getInfo().itemNoEsDeMapa()) {
                    quitarObjeto(x, y);
                }
            }
        }
    }    
    
    public void construirAreaObj(Player player, byte  x, byte y) {
    	MapObject obj = getObjeto(x,y);
    	if (obj != null) player.sendPacket(new ObjectCreateResponse((byte)x, (byte)y, (short)obj.getInfo().GrhIndex));
    }
    
    public void construirAreaNpc(Player player, Npc npc) {
    	player.sendPacket(npc.createCC());
    }
    
    public void doFX() {
        if (getCantUsuarios() > 0 && Util.Azar(1, 150) < 12) {
        	byte sound = -1;
            switch (this.m_terreno) {
                case TERRENO_BOSQUE:
                    int n = Util.Azar(1, 100);
                    switch (this.m_zona) {
                        case ZONA_CAMPO:
                        case ZONA_CIUDAD:
                            if (!this.server.isRaining()) {
                                if (n < 15) {
                                	sound = Constants.SND_AVE2;
                                } else if (n < 30) {
                                	sound = Constants.SND_AVE;
                                } else if (n <= 35) {
                                	sound = Constants.SND_GRILLO;
                                } else if (n <= 40) {
                                	sound = Constants.SND_GRILLO2;
                                } else if (n <= 45) {
                                	sound = Constants.SND_AVE3;
                                }
                            }
                            break;
                    }
            }
            if (sound > -1) {
                for (Player player : this.players) {
            		player.sendPacket(new PlayWaveResponse(sound, player.pos().x, player.pos().y));
                }
            }
        }
    }

    
    @Deprecated
    public void doTileEvents(Player player) {
		// Esto no hace falta por ahora, era para hacer funcionar a los teleports,
		// y estoy haciendo que los teleports trabajen de otra manera.
    	//
    	// En lugar de revisar cada X tiempo si hay alguien parado en un teleport, 
    	// para teletransportarlo, desde la función mover disparamos el TP directamente.
    }
    

    /**
     * Handles the Map passage of Users. Allows the existance
	 * of exclusive maps for Newbies, Royal Army and Caos Legion members
	 * and enables GMs to enter every map without restriction.
	 * Uses: Restringir = "NEWBIE" (newbies), "ARMADA", "CAOS", "FACCION" or "NO".
     * @return true if passage is forbidden, false if passage is allowed
     */
    public boolean isForbbidenMap(Player player) {
    	// ¿Es mapa de newbies?
    	if ("NEWBIE".equalsIgnoreCase(m_restringir)) {
    		if (player.esNewbie() || player.isGM()) {
    			return false; // allowed
    		} else {
    			// no es un newbie/gm, "NO PASARÁS!"
				player.enviarMensaje("Mapa exclusivo para newbies.", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	} 
    	
		// ¿Es mapa de Armadas?
    	if ("ARMADA".equalsIgnoreCase(m_restringir)) {
            // ¿El usuario es Armada?
    		if (player.esArmada() || player.isGM()) {
    			return false; // allowed
    		} else {
    			// no es un armada/gm, "NO PASARÁS!"
				player.enviarMensaje("Mapa exclusivo para miembros del ejército Real", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
		// ¿Es mapa de Caos?
    	if ("CAOS".equalsIgnoreCase(m_restringir)) {
            // ¿El usuario es Caos?
    		if (player.esCaos() || player.isGM()) {
    			return false; // allowed
    		} else {
    			// no es un caos/gm, "NO PASARÁS!"
				player.enviarMensaje("Mapa exclusivo para miembros del ejército Oscuro.", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
		// ¿Es mapa de faccionarios?
    	if ("FACCION".equalsIgnoreCase(m_restringir)) {
            // ¿El usuario es Caos?
    		if (player.esArmada() || player.esCaos() || player.isGM()) {
    			return false; // allowed
    		} else {
    			// no es un armada/caos/gm, "NO PASARÁS!"
				player.enviarMensaje("Solo se permite entrar al Mapa si eres miembro de alguna Facción", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
    	// No es un mapa de newbies, ni Armadas, ni Caos, ni faccionario.
    	// Adelante averturero
    	return false; // allowed;
    }
    
    
}
