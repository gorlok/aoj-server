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
package org.argentumonline.server.map;

import static org.argentumonline.server.util.Color.COLOR_BLANCO;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjType;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Pos;
import org.argentumonline.server.Skill;
import org.argentumonline.server.areas.AreasAO;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Tile.Trigger;
import org.argentumonline.server.net.ServerPacket;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.protocol.BlockPositionResponse;
import org.argentumonline.server.protocol.CharacterMoveResponse;
import org.argentumonline.server.protocol.CharacterRemoveResponse;
import org.argentumonline.server.protocol.CreateFXResponse;
import org.argentumonline.server.protocol.ForceCharMoveResponse;
import org.argentumonline.server.protocol.ObjectCreateResponse;
import org.argentumonline.server.protocol.ObjectDeleteResponse;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.protocol.RemoveCharDialogResponse;
import org.argentumonline.server.protocol.ShowSignalResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.BytesReader;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

/**
 * @author gorlok
 */
public class Map implements Constants {
	private static Logger log = LogManager.getLogger();

    final static String FOLDER_MAPS = "maps";
    final static String FOLDER_WORLDBACKUP = "worldBackup";

	private static final byte FLAG_TELEPORT = 1;
	private static final byte FLAG_NPC 		= 2;
	private static final byte FLAG_OBJECT 	= 4;
    
    /** Número de mapa. */
    private short mapNumber;
    
    // Cabecera archivo .map
    private short version   = 0;
    private String desc = "";
    private int crc = 0;
    private int magicWord = 0;
    
    // Información del archivo .dat
    String  name  = "";
    int music = 0;
    
    int     numUsers = 0; // FIXME se usa?
    int     mapVersion = 0; // FIXME se usa?
    boolean noEncriptarMP = false; // sin uso
    
    /** User killing enabled */
    private boolean pk = false;
    private boolean magiaSinEfecto = false;
    private boolean inviSinEfecto = false;
    private boolean resuSinEfecto = false;
    private boolean backup = false;
    
    private Terrain terrain = Terrain.FOREST;
    private Zone zone = Zone.COUNTRY;
    private MapConstraint restricted = MapConstraint.NONE;
    
    private MapPos startPos = MapPos.empty(); // FIXME se usa?
    
    private GameServer server;
    
    private Tile tiles[][] = new Tile[MAP_WIDTH][MAP_HEIGHT];
    
    private List<Tile> objects = new ArrayList<Tile>();
    private List<User> users = new ArrayList<User>();
    private List<Npc> npcs = new ArrayList<Npc>();
    
    /** Creates a new instance of Map */
    public Map(short nroMap, GameServer server) {
    	this.server = server;
        this.mapNumber = nroMap;
        
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                this.tiles[x][y] = new Tile((short)(x+1), (short)(y+1));
            }
        }
    }
    
    public void setRestricted(MapConstraint restricted) {
		this.restricted = restricted;
	}
    
    public boolean isBackup() {
		return backup;
	}
    
    public void setBackup(boolean backup) {
    	this.backup = backup;
    }
    
    public boolean isMagiaSinEfecto() {
		return magiaSinEfecto;
	}
    
    public void setMagiaSinEfecto(boolean magiaSinEfecto) {
		this.magiaSinEfecto = magiaSinEfecto;
	}
    
    public boolean isInviSinEfecto() {
		return inviSinEfecto;
	}
    
    public void setInviSinEfecto(boolean inviSinEfecto) {
		this.inviSinEfecto = inviSinEfecto;
	}
    
    public boolean isResuSinEfecto() {
		return resuSinEfecto;
	}
    
    public void setResuSinEfecto(boolean resuSinEfecto) {
		this.resuSinEfecto = resuSinEfecto;
	}
    
    private AreasAO area() {
    	return AreasAO.instance();
    }
    
    public List<User> getUsers() {
    	return this.users;
    }
    
    public List<Npc> getNpcs() {
    	return this.npcs;
    }
    
    public int getMapNumber() {
        return this.mapNumber;
    }
    
    public short getVersion() {
		return version;
	}
    
    public Zone getZone() {
        return this.zone;
    }
    
    public void setZone(Zone zone) {
		this.zone = zone;
	}
    
    public int getMusic() {
        return this.music;
    }
    
    public Terrain getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
    
    public MapObject getObject(byte x, byte y) {
    	if (tile(x, y).hasObject()) {
			return new MapObject(tile(x, y).objIndex(), tile(x, y).objCount(), x, y);
		}
		return null;
    }
    
	private ObjectInfo findObj(int oid) {
		return server.getObjectInfoStorage().getInfoObjeto(oid);		
	}
	
    public Npc getNPC(byte  x, byte y) {
        return tile(x, y).npc();
    }
    
    // FIXME
    public Trigger getTrigger(byte  x, byte y) {
        return tile(x, y).trigger();
    }
    
    public boolean isUnderRoof(byte x, byte y) {
    	return tile(x, y).isUnderRoof();
    }
    
    public boolean isAntiPiquete(byte x, byte y) {
    	return tile(x, y).isAntiPiquete();
    }
    
    public void setTrigger(byte  x, byte y, Trigger value) {
        tile(x, y).trigger(value);
    }
    
    public boolean testSpawnTriggerNpc(byte  x, byte y, boolean underRoof) {
    	return tile(x, y).testSpawnTriggerNpc(underRoof);
    }
    
    public boolean testSpawnTriggerNpc(MapPos pos, boolean underRoof) {
    	return testSpawnTriggerNpc(pos.x, pos.y, underRoof);
    }
    
    public boolean isWater(byte x, byte y) {
    	if (Pos.isValid(x, y)) {
    		return tile(x, y).isWater();
    	}
    	return false;
    }
    
    public boolean hasUser(User user) {
        return this.users.contains(user);
    }
    
    /** intemperie */
    public boolean isOutdoor(byte  x, byte y) {
       	return (this.zone != Zone.DUNGEON) && tile(x, y).isOutdoor();
    }
    
    public boolean isHasUsers() {
    	return !this.users.isEmpty();
    }
    
    public int getUsersCount() {
        return this.users.size();
    }
    
    public boolean isSafeMap() {
        return this.pk == false;
    }
    
    public void setSafeMap(boolean safe) {
    	this.pk = !safe;
    }
    
    public boolean isSafeZone(byte x, byte y) {
    	return tile(x, y).isSafeZone();
    }
    
    public boolean isArenaZone(byte x, byte y) {
    	return tile(x, y).isArenaZone();
    }
    
    public boolean isTournamentZone(byte x, byte y) {
    	return tile(x, y).isTournamentZone();
    }
    
    public void load(boolean loadBackup) {
        try {
            loadDatFile(loadBackup);
            loadMapFile(loadBackup);
            loadInfFile(loadBackup);
        } catch (java.io.FileNotFoundException e) {
            log.warn("Archivo de mapa %d faltante.", this.mapNumber);
        } catch (java.io.IOException e) {
            log.warn("Error leyendo archivo de mapa %d", this.mapNumber);
        } catch (Exception e) {
        	log.warn("Error con mapa " + this.mapNumber, e);
        }
    }
    
    
    private void loadDatFile(boolean loadBackup) 
    throws java.io.FileNotFoundException, java.io.IOException {
    	String datFileName = "Mapa" + this.mapNumber + ".dat";
    	log.trace("loading file %s", datFileName);
        // Cargar información del archivo .dat
        IniFile ini = new IniFile();
        if (loadBackup && Files.exists(Paths.get(FOLDER_WORLDBACKUP + File.separator + datFileName))) {
			ini.load(FOLDER_WORLDBACKUP + File.separator + datFileName);
		} else {
			ini.load(FOLDER_MAPS + File.separator + datFileName);			
		}
        String section = "Mapa" + this.mapNumber;
        this.name = ini.getString(section, "Name");
        this.music = ini.getInt(section, "MusicNum");
        
        // User Kiling está invertido 0 habilita PK, 1 deshabilita PK.
        this.pk = (ini.getInt(section, "PK") == 0); 
        
        this.magiaSinEfecto = (ini.getInt(section, "MagiaSinEfecto") == 1);
        this.inviSinEfecto = (ini.getInt(section, "InviSinEfecto") == 1);
        this.resuSinEfecto = (ini.getInt(section, "ResuSinEfecto") == 1);
        this.noEncriptarMP = (ini.getInt(section, "NoEncriptarMP") == 1);
        
        this.restricted = MapConstraint.fromName(ini.getString(section, "Restringir"));
        this.backup = (ini.getInt(section, "BackUp") == 1);
        String tipo_terreno = ini.getString(section, "Terreno").toUpperCase();
        if (tipo_terreno.equals("BOSQUE")) {
            this.terrain = Terrain.FOREST;
        } else if (tipo_terreno.equals("DESIERTO")) {
            this.terrain = Terrain.DESERT;
        } else if (tipo_terreno.equals("NIEVE")) {
            this.terrain = Terrain.SNOW;
        } else {
            this.terrain = Terrain.FOREST;
        }
        String tipo_zona = ini.getString(section, "Zona");
        if (tipo_zona.equals("CAMPO")) {
            this.zone = Zone.COUNTRY;
        } else if (tipo_zona.equals("CIUDAD")) {
            this.zone = Zone.CITY;
        } else if (tipo_zona.equals("DUNGEON")) {
            this.zone = Zone.DUNGEON;
        } else {
            this.zone = Zone.COUNTRY;
        }
        
        // FIXME esta información sigue estando en los mapas?
        String startPos = ini.getString(section, "StartPos");
        if (startPos.length() > 4) {
            String mapa = startPos.substring(0, startPos.indexOf('-'));
            String x = startPos.substring(startPos.indexOf('-')+1, startPos.lastIndexOf('-'));
            String y = startPos.substring(startPos.lastIndexOf('-')+1);
            this.startPos.map = Short.parseShort(mapa);
            this.startPos.x = Byte.parseByte(x);
            this.startPos.y = Byte.parseByte(y);
        }
        if (this.startPos.map == 0) {
			this.startPos.map = this.mapNumber;
		}
        if (this.startPos.x == 0) {
			this.startPos.x = 45;
		}
        if (this.startPos.y == 0) {
			this.startPos.y = 45;
		}
    }
    
    
    /**
     * Carga el archivo .map dentro de la carpeta backup
     * by Agush
     */
    private void loadMapFile(boolean loadBackup)
    throws java.io.IOException {
    	String mapFileName = "Mapa" + this.mapNumber + ".map";
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
            
            // HEADER
            this.version = Short.reverseBytes(reader.readShort());
            this.desc = new String(reader.getBytes(255));
            reader.skipBytes(255);// getBytes doesn't avance pointer
            this.crc = Integer.reverseBytes(reader.readInt());
            this.magicWord = Integer.reverseBytes(reader.readInt());
            reader.skipBytes(8); // FILL 8 bytes
            
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                	
                	byflags = reader.readByte();
                	
                	if ((byflags & 1) == 1) {
                		this.tiles[x][y].blocked(true);
                	}
                    
                	this.tiles[x][y].setGrh(0, Util.leShort(reader.readShort()));
                    
                    if ((byflags & 2) == 2) {
                    	this.tiles[x][y].setGrh(1, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 4) == 4) {
                    	this.tiles[x][y].setGrh(2, Util.leShort(reader.readShort()));
                    }
                    if ((byflags & 8) == 8) {
                    	this.tiles[x][y].setGrh(3, Util.leShort(reader.readShort()));
                    }
                    
                    if ((byflags & 16) == 16) {
                    	short value = Util.leShort(reader.readShort()); // byte ?
                    	if (value > Trigger.values().length-1 || value < 0) {
                    		log.warn("Trigger fuera de rango: " + value + " en mapa:" + mapFileName + " x:" + (x+1) + " y:" +(y+1));
                    	} else {
                    		this.tiles[x][y].trigger(Trigger.values()[value]);
                    	}
                    }
                    
                }
            }         
            
        } catch (Exception e) {
        	log.fatal("ERROR LOADING " + mapFileName, e);
        }
    }

    /**
     * Carga el archivo .inf de la carpeta backup
     * by Agush
     */
    private void loadInfFile(boolean loadBackup) 
    throws java.io.IOException {
    	String infFileName = "Mapa" + this.mapNumber + ".inf";
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
	        
	        for (int y = 0; y < MAP_HEIGHT; y++) {
	            for (int x = 0; x < MAP_WIDTH; x++) {
	            	
	            	byflags = reader.readByte();
	            	
	            	if ((byflags & FLAG_TELEPORT) > 0) {
						this.tiles[x][y].teleport(MapPos.mxy(
							Util.leShort(reader.readShort()),
							Util.leShort(reader.readShort()),
							Util.leShort(reader.readShort())));
	            	}
	                
	                if ((byflags & FLAG_NPC) > 0) {
	                    short npcId = Util.leShort(reader.readShort());
	                    if (npcId > 0) {
	                        // Crear un nuevo Npc.
	                        this.tiles[x][y].npc(this.server.createNpc(npcId));
	                        npc = this.tiles[x][y].npc();
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
	                        area().loadNpc(this, npc);
	                        npc.activate();
	                    }
	                }
	                
	                if ((byflags & FLAG_OBJECT) > 0) {
						short obj_ind = Util.leShort(reader.readShort());
						short obj_cant = Util.leShort(reader.readShort());
						addObject(obj_ind, obj_cant, (byte)(x+1), (byte)(y+1)); // FIXME ignora el resultado ? y si no pudo agregarlo?
						sendDoorUpdate(getObject((byte)(x+1), (byte)(y+1)));
	                }
	            }
	        }         
        
        } catch (Exception e) {
        	log.error("Error, cargando " + infFileName, e);
        }
    }
    
    public boolean enterMap(User user, byte  x, byte y) {
        if (tile(x, y).userId() != 0) {
			return false;
		}
        this.users.add(user);
        tile(x, y).userId(user.getId());
        user.pos().set(this.mapNumber, x, y);
        return true;
    }
    
    public boolean exitMap(User user) {
        short x = user.pos().x;
        short y = user.pos().y;
        try {
        	try {
        		area().sendToUserArea(this, user, new RemoveCharDialogResponse(user.getId()));
        		
        		if (user.getFlags().AdminInvisible) {
        			user.sendPacket(new CharacterRemoveResponse(user.getId()));
        		} else {
        			sendToArea(user.pos().x, user.pos().y, new CharacterRemoveResponse(user.getId()));
        		}
        		
        	} finally {
        		this.users.remove(user);
        		user.charArea().reset();
        	}
        } finally {
	        if (tile(x,y).userId() != user.getId()) {
	        	log.fatal("INCONSISTENCIA: el jugador no se encuentra donde debería");
	            return false;
	        }
	        tile(x,y).userId((short) 0);
        }
        return true;
    }
    
    public void moveNpc(Npc npc, byte x, byte y) {
        if (tile(x, y).npc() != null) {
			log.fatal("ERRRRRRRRRRORRRRRRRRRRRR en moverNpc: " + npc);
		}
        this.tiles[npc.pos().x-1][npc.pos().y-1].npc(null);
        tile(x, y).npc(npc);
        
        area().checkUpdateNeededNpc(this, npc, npc.infoChar().getHeading());
        area().sendToNPCArea(this, npc, new CharacterMoveResponse(npc.getId(), x, y));
    }
    
    public boolean isFree(byte  x, byte y) {
        return (tile(x, y).userId() == 0) && (tile(x, y).npc() == null);
    }
    
    public boolean isBlocked(byte  x, byte y) {
    	if (!Pos.isValid(x, y)) {
    		return true;
    	}
        return tile(x, y).isBlocked();
    }
    
    public boolean enterNpc(Npc npc, byte  x, byte y) {
        if (tile(x, y).npc() != null) {
			return false;
		}
        
        tile(x, y).npc(npc);
        this.npcs.add(npc);
        npc.pos().set(this.mapNumber, x, y);
      
        area().loadNpc(this, npc);
        sendToArea(x, y, npc.characterCreate());
        
        return true;
    }
    
    public boolean exitNpc(Npc npc) {
        byte x = npc.pos().x;
        byte y = npc.pos().y;
        try {
        	sendToArea(x, y, new RemoveCharDialogResponse(npc.getId()));
	        sendToArea(x, y, new CharacterRemoveResponse(npc.getId()));
        } finally {
            tile(x, y).npc(null);
	        this.npcs.remove(npc);
	        npc.pos().set(this.mapNumber, (short)0, (short)0);
        }
        npc.charArea().reset();
        return true;
    }
    
    public void removeNpcsArea(User admin) {
        short x1 = (short) (admin.pos().x - MinXBorder + 1);
        short x2 = (short) (admin.pos().x + MaxXBorder + 1);
        short y1 = (short) (admin.pos().y - MinYBorder + 1);
        short y2 = (short) (admin.pos().y + MaxYBorder + 1);
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
        int count = 0;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
            	npc = tile(x,y).npc();
            	// gorlok: por seguridad, lo limito a sólo NPC hostiles que no sean mascotas
                if (npc != null && npc.getPetUserOwner() == null && npc.isHostile()) {
            		exitNpc(npc);
            		count++;
                }
            }
        }
        String msg;
        if (count == 0) {
        	msg = "No se encontraron criaturas para eliminar";
        } else if (count == 1) {
        	msg = "Se ha eliminado una criatura";
        } else {
        	msg = "Se han eliminado " + count + " criaturas";
        }
        admin.sendMessage(msg, FontType.FONTTYPE_INFO);
    }    

    public void sendItemsInTheFloor(User admin) {
    	// List of objects by name
    	var objects = new TreeMap<String, List<String>>();
    	for (int x = 5; x < MAP_WIDTH - 5; x++) {
    		for (int y = 5; y < MAP_HEIGHT - 5; y++) {
    			if (hasObject((byte)x, (byte)y)) {
    				var oi = getObject((byte)x, (byte)y).objInfo();
    				if (oi.objType != ObjType.Arboles
    						&& oi.objType != ObjType.Llaves
    						&& oi.objType != ObjType.Carteles) {
    					if (!objects.containsKey(oi.Nombre)) {
    						objects.put(oi.Nombre, new ArrayList<>());
    					}
    					objects.get(oi.Nombre).add(Pos.xy(x, y).toStringShort());
    				}
    			}
    		}
    	}
    	
    	admin.sendMessage("Objetos en el mapa:", FontType.FONTTYPE_WARNING);
    	for (String name: objects.keySet()) {
    		admin.sendMessage(String.format("%2d %s: %s", 
    				objects.get(name).size(), name, String.join(", ", objects.get(name))), 
    				FontType.FONTTYPE_INFO);
    	}
    	admin.sendMessage("Fueron encontrados " + objects.values().size() + " objetos", 
    			FontType.FONTTYPE_WARNING);
    }
    
    public boolean addObject(short objid, int cant, byte x, byte y) {
    	if (hasObject(x, y)) {
            log.warn("Intento de agregar objeto sobre otro: objid=" + objid + " cant" + cant + " mapa" + this.mapNumber + " x=" + x + " y=" + y);
    		return false;
    	}
    	tile(x, y).setObj(objid, cant);
        this.objects.add(tile(x, y));
        short grhIndex = findObj(objid).GrhIndex;
        
        area().sendToAreaByPos(this, x, y, 
        		new ObjectCreateResponse(x , y, grhIndex));
        return true;
    }
    
    public void removeObject(byte  x, byte y) {
    	this.objects.remove(tile(x, y));
        tile(x, y).removeObject();
        sendToArea(x, y, new ObjectDeleteResponse(x,y));
    }
    
    public void blockTile(byte x, byte y) {
        tile(x, y).blocked(true);
        tile(x, y).modified(true);
        sendToArea(x, y, new BlockPositionResponse(x, y, (byte)1));
    }
    
    public void unblockTile(byte x, byte y) {
        tile(x, y).blocked(false);
        tile(x, y).modified(true);
        sendToArea(x, y, new BlockPositionResponse(x, y, (byte)0));
    }
    
    public void toggleDoor(MapObject obj) {
        if (obj.objInfo().objType == ObjType.Puertas) {
            // Es un objeto tipo puerta.
            if (obj.objInfo().estaCerrada()) {
                // Abrir puerta.
                removeObject(obj.x, obj.y);
                ObjectInfo info = findObj(obj.objInfo().IndexAbierta);
                addObject(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            } else {
                // Cerrar puerta
                removeObject(obj.x, obj.y);
                ObjectInfo info = findObj(obj.objInfo().IndexCerrada);
                addObject(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            }
            obj = getObject(obj.x, obj.y);
            sendDoorUpdate(obj);
            sendPlayWave(SOUND_PUERTA, obj.x, obj.y);
        }
    }
    
    private void sendDoorUpdate(MapObject obj) {
        if (obj.objInfo().objType == ObjType.Puertas) {
            // Es un objeto tipo puerta.
            if (obj.objInfo().estaCerrada()) {
                blockTile((byte) (obj.x-1), obj.y);
                blockTile(obj.x, obj.y);
            } else {
                unblockTile((byte) (obj.x-1), obj.y);
                unblockTile(obj.x, obj.y);
            }
            sendToArea(obj.x,obj.y, new ObjectCreateResponse(obj.x, obj.y, (short)obj.objInfo().GrhIndex));
        }
    }
    
    public void sendPlayWave(byte waveId, byte x, byte y) {
    	sendToArea(x, y, new PlayWaveResponse(waveId, x, y));
    }
    
    public void sendCreateFX(byte  x, byte y, int id, int fx, int val) {
    	sendToArea(x,y, new CreateFXResponse((short) id, (short) fx, (short) val));
    }
    
    public void sendToAll(ServerPacket packet) {
        sendToAllButIndex(-1, packet);
    }
    
    public void sendToAllButIndex(int exceptId, ServerPacket packet) {
    	this.users.stream()
    		.filter( u -> u.getId() != exceptId )
    		.forEach( u -> u.sendPacket(packet));
    }
    
    public void sendToArea(byte x, byte y, ServerPacket packet) {
        sendToAreaButIndex(x, y, -1, packet);
    }
        
    public void sendToAreaButIndex(byte pos_x, byte pos_y, int excepto, ServerPacket packet) {
    	area().sendToAreaButIndex(this, pos_x, pos_y, excepto, packet);
    }
    
    public void sendToAreaToAdminsButCounselor(short pos_x, short pos_y, ServerPacket packet) {
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
        User user;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
                if (tile(x,y).userId() > 0) {
                    user = this.server.userById(tile(x,y).userId());
                    if (user != null && (user.getFlags().isGod() || user.getFlags().isDemiGod())) {
						user.sendPacket(packet);
					}
                }
            }
        }
    }
    
    public User lookForUserAtArea(short pos_x, short pos_y, short cli_id) {
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
                if (tile(x, y).userId() == cli_id) {
					return this.server.userById(tile(x,y).userId());
				}
            }
        }
        return null;
    }
    
    /** Send user chars in map */
    public void sendUsers(User user) {
    	this.users.stream()
    		.filter( u -> !u.equals(user))
    		.forEach( u -> user.sendPacket(u.characterCreate()));
    }
    
    /** Send objects in map */
    public void sendObjects(User user) {
        for (Tile object : this.objects) {
            user.sendObject(object.objIndex(), object.x(), object.y());
        }
    }
    
    /** send NPCs in map */
    public void sendNpcs(User user) {
        for (Npc npc : this.npcs) {
            user.sendPacket(npc.characterCreate());
        }
    }
    
    /** send blocked positions in map */
    public void sendBlockedPositions(User user) {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (this.tiles[x][y].isModified()) {
					user.sendBlockedPosition(x+1, y+1, this.tiles[x][y].isBlocked());
				}
            }
        }
    }
    
    public void moveUser(User user, MapPos newPos) {
        tile(user.pos().x, user.pos().y).userId((short) 0);
        tile(newPos.x, newPos.y).userId(user.getId());
        
        user.pos().set(this.mapNumber, newPos.x, newPos.y);
        
        area().sendToAreaButIndex(this, newPos.x, newPos.y, user.getId(), 
        		new CharacterMoveResponse(user.getId(), newPos.x, newPos.y));
        area().checkUpdateNeededUser(this, user, user.infoChar().getHeading());
    }
    
    public void moveUserSwapping(User user, MapPos newPos, User casper) {
    	MapPos oldPos = user.pos().copy();
    	
        tile(oldPos.x, oldPos.y).userId(casper.getId());
        tile(newPos.x, newPos.y).userId(user.getId());
        
        user.pos().set(this.mapNumber, newPos.x, newPos.y);
        casper.pos().set(this.mapNumber, oldPos.x, oldPos.y);
        
    	area().sendToAreaButIndex(this, newPos.x, newPos.y, user.getId(), 
    		new CharacterMoveResponse(user.getId(), newPos.x, newPos.y));
        area().checkUpdateNeededUser(this, user, user.infoChar().getHeading());
        
        if (!casper.getFlags().AdminInvisible) {
        	area().sendToAreaButIndex(this, oldPos.x, oldPos.y, casper.getId(), 
            		new CharacterMoveResponse(casper.getId(), oldPos.x, oldPos.y));
        }
        casper.sendPacket(new ForceCharMoveResponse(casper.infoChar().getHeading().value()));
        area().checkUpdateNeededUser(this, casper, casper.infoChar().getHeading());
    }
    
    public boolean isTeleport(byte  x, byte y) {
        return tile(x, y).isTeleport();
    }
    
    public boolean isTeleportObject(byte x, byte y) {
    	return isTeleport(x, y) 
    			&& hasObject(x, y) 
				&& (getObject(x, y).objInfo().objType == ObjType.Teleport);    	
    }
    
    public void createTeleport(byte x, byte y, short dest_mapa, byte dest_x, byte dest_y) {
        if (dest_mapa > 0 && dest_x > 0 && dest_y > 0) {
			tile(x, y).teleport(MapPos.mxy(dest_mapa, dest_x, dest_y));
		}
        addObject(OBJ_TELEPORT, 1, x, y);
    }
    
    public void destroyTeleport(byte  x, byte y) {
        if (!isTeleport(x, y)) {
			return;
		}
        removeObject(x, y);
        tile(x, y).teleport(null);
    }    
    
    public boolean hasObject(byte  x, byte y) {
        return Pos.isValid(x, y) 
        		&& (tile(x, y).hasObject());
    }
    
    public boolean hasUser(byte  x, byte y) {
        return Pos.isValid(x, y) 
        		&& (tile(x, y).userId() != 0 
        		&& getUser(x, y) != null);
    }
    
    public boolean hasNpc(byte  x, byte y) {
    	if (!Pos.isValid(x, y)) {
    		return false;
    	}
        return (tile(x, y).npc() != null);
    }
    
    public User getUser(byte  x, byte y) {
        return this.server.userById(tile(x, y).userId());
    }
    
    public Npc getNpc(byte  x, byte y) {
        return tile(x, y).npc();
    }
    
    public MapObject lookForNearbyObject(byte  x, byte y) {
        // Ver si hay un objeto en los alrededores...
        if (hasObject(x, y)) {
            return getObject(x, y);
        }
        if (hasObject((byte) (x+1), y)) {
            return getObject((byte) (x+1), y);
        }
        if (hasObject((byte) (x+1), (byte) (y+1))) {
            return getObject((byte) (x+1), (byte) (y+1));
        }
        if (hasObject(x, (byte) (y+1))) {
            return getObject(x, (byte) (y+1));
        }
        return null;
    }
    
    public User lookForNearbyUser(byte  x, byte y) {
        // Ver si hay un jugador en los alrededores...
        if (hasUser(x, (byte) (y+1))) {
            return getUser(x, (byte) (y+1));
        }
        if (hasUser(x, y)) {
            return getUser(x, y);
        }
        return null;
    }
    
    public Npc lookForNearbyNpc(byte  x, byte y) {
        // Ver si hay un NPC en los alrededores...
        if (hasNpc(x, (byte) (y+1))) {
            return getNpc(x, (byte) (y+1));
        }
        if (hasNpc(x, y)) {
            return getNpc(x, y);
        }
        return null;
    }
    
    public void lookAtTile(User user, byte  x, byte y) {
        System.out.println("areaID:" + user.charArea().areaID);
    	if (!user.pos().inRangoVision(x, y)) {
            return;
        }
        boolean foundSomething = false;
        
        // Ver si hay un objeto en los alrededores...
        MapObject obj = lookForNearbyObject(x, y);
        if (obj != null) {
        	if (obj.objInfo().mostrarCantidad()) {
        		user.sendMessage(obj.objInfo().Nombre + " - " + obj.obj_cant, FontType.FONTTYPE_INFO);
        	} else {
        		user.sendMessage(obj.objInfo().Nombre, FontType.FONTTYPE_INFO);
        	}
            user.getFlags().TargetObj = obj.objInfo().ObjIndex;
            user.getFlags().TargetObjMap = this.mapNumber;
            user.getFlags().TargetObjX = obj.x;
            user.getFlags().TargetObjY = obj.y;
            foundSomething = true;
            System.out.println("OBJ " + obj.objInfo().Nombre + " OID:" + obj.objInfo().ObjIndex + " x="+obj.x + ",y="+obj.y);
        }
        
        // Ver si hay un jugador
        User anotherUser;
        if ((anotherUser = lookForNearbyUser(x, y)) != null) {
        	if (!anotherUser.getFlags().AdminInvisible || user.getFlags().isGM()) {
        		
        		if (anotherUser.descRM.length() > 0 || !anotherUser.showName) {
        			// tiene descRM, o no quiere que se vea su nombre.
                    user.sendMessage(anotherUser.descRM, FontType.FONTTYPE_INFOBOLD);
        		} else {
        			user.sendMessage("Ves a " + anotherUser.userNameTagDesc(), anotherUser.getTagColor());
        		}
        		
        		user.getFlags().TargetUser = anotherUser.getId();
        		user.getFlags().TargetNpc = 0;
        		user.getFlags().TargetObj = 0;
        		user.getFlags().TargetMap = this.mapNumber;
        		user.getFlags().TargetX = x;
        		user.getFlags().TargetY = y;
        		foundSomething = true;
        	}
        }

        // Ver si hay un Npc...
        Npc npc;
        if ((npc = lookForNearbyNpc(x, y)) != null) {
            foundSomething = true;
            System.out.println("NPC id:" + npc.getId() + " name:" + npc.getName() + " areaID:" + npc.charArea().areaID + " " + npc.pos().toString());
            if (npc.description.length() > 0) {
            	// tiene algo para decir
            	user.sendTalk(COLOR_BLANCO, npc.description, npc.getId());
            } else if (server.getWorkWatcher().getNpc() != null && npc.getId() == server.getWorkWatcher().getNpc().getId()) {
                // enviamos nuevamente el mensaje del Centinela, según quien pregunta.
            	server.getWorkWatcher().sendCode(user);
            } else {
            	String npcName;
            	if (npc.getPetUserOwner() != null) {
            		npcName = npc.name + " es mascota de " + npc.getPetUserOwner().getUserName();
            	} else {
            		npcName = npc.name;
            	}
            	user.sendMessage(npcName + " " + npc.healthDescription(user), FontType.FONTTYPE_INFO);
            	if (user.getFlags().isGM() && npc.attackedFirstBy != "") {
            		user.sendMessage("Le pegó primero: " + npc.attackedFirstBy + ".", FontType.FONTTYPE_INFO);
            	}
            }
            
            user.getFlags().TargetNpc = npc.getId();
            user.getFlags().TargetMap = this.mapNumber;
            user.getFlags().TargetX = x;
            user.getFlags().TargetY = y;
            user.getFlags().TargetUser = 0;
            user.getFlags().TargetObj = 0;
        }
        
        if (!foundSomething) {
            user.getFlags().TargetNpc = 0;
            user.getFlags().TargetNpcTipo = 0;
            user.getFlags().TargetUser = 0;
            user.getFlags().TargetObj = 0;
            user.getFlags().TargetObjMap = 0;
            user.getFlags().TargetObjX = 0;
            user.getFlags().TargetObjY = 0;
            user.getFlags().TargetMap = this.mapNumber;
            user.getFlags().TargetX = x;
            user.getFlags().TargetY = y;
            user.sendMessage("No ves nada interesante.", FontType.FONTTYPE_INFO);
        }
    }
    
    public MapPos teleportTarget(byte  x, byte y) {
        return tile(x, y).teleport();
    }
    
    public void accionParaRamita(byte x, byte y, User user) {
        if (Util.distance(user.pos().x, user.pos().y, x, y) > 2) {
            user.sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        
        if (tile(x, y).isSafeZone() && isSafeMap()) {
            user.sendMessage("En zona segura no puedes hacer fogatas.", FontType.FONTTYPE_INFO);
            return;
        }
        
        int suerte = 0;
        int skillSupervivencia = user.skills().get(Skill.SKILL_Supervivencia);        
        if (skillSupervivencia == 0) {
			suerte = 0;
		} else if (skillSupervivencia < 6) {
			suerte = 3;
		} else if (skillSupervivencia <= 10) {
			suerte = 2;
		} else {
			suerte = 1;
		}
        if (Util.random(1, suerte) == 1) {
        	removeObject(x, y);
            addObject(FOGATA, 1, x, y);
            user.sendMessage("Has prendido la fogata.", FontType.FONTTYPE_INFO);
        } else {
            user.sendMessage("No has podido hacer fuego.", FontType.FONTTYPE_INFO);
        }
		user.riseSkill(Skill.SKILL_Supervivencia);
    }

	private Tile tile(int x, int y) {
		return this.tiles[x-1][y-1];
	}
    
    public void accionParaForo(byte  x, byte y, User user) {
        if (Util.distance(user.pos().x, user.pos().y, x, y) > 2) {
            user.sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        // ¿Hay mensajes?
        MapObject obj = getObject(x, y);
        if (obj == null || obj.objInfo().objType != ObjType.Foros) {
            return;
        }
        String foroId = obj.objInfo().ForoID;
        this.server.getForumManager().sendForumPosts(foroId, user);
    }
    
    public void accionParaPuerta(byte  x, byte y, User user) {
        if (Util.distance(user.pos().x, user.pos().y, x, y) > 2) {
            user.sendMessage("Estas demasiado lejos.", FontType.FONTTYPE_INFO);
            return;
        }
        MapObject obj = getObject(x, y);
        if (obj == null || obj.objInfo().objType != ObjType.Puertas) {
			return;
		}
        if (obj.objInfo().Clave == 0) {
            toggleDoor(obj);
            user.getFlags().TargetObj = obj.objInfo().ObjIndex;
        } else {
            user.sendMessage("La puerta esta cerrada con llave.", FontType.FONTTYPE_INFO);
        }
    }
    
    public void accionParaCartel(byte  x, byte y, User user) {
        MapObject obj = getObject(x, y);
        if (obj == null || obj.objInfo().objType != ObjType.Carteles) {
			return;
		}
        if (obj.objInfo().Texto.length() > 0) {
        	user.sendPacket(new ShowSignalResponse(obj.objInfo().Texto, obj.objInfo().GrhSecundario));
        }
    }
    
    public MapPos dropItemOnFloor(byte  x, byte y, InventoryObject obj) {
    	// tirarItemAlPiso
        MapPos newPos = freeTile(x, y);
        if (newPos != null) {
	        log.debug("tirarItemAlPiso: x=" + newPos.x + " y=" + newPos.y);
	        if (newPos != null) {
	            if (addObject(obj.objid, obj.cant, newPos.x, newPos.y)) {
	                return newPos;
	            }
	        }
        }
        return null;
    }
    
    public boolean isFreeForObject(byte  x, byte y) {
        return Pos.isValid(x, y) 
        		&& tile(x, y).isFreeForObject();
    }
    
    /** 
     * Look for a free tile to drop an object, that it's closest to the original position. 
     */
    private MapPos freeTile(byte orig_x, byte orig_y) {
        if (isFreeForObject(orig_x, orig_y)) {
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
                if (isFreeForObject(x, y1)) {
					return MapPos.mxy(this.mapNumber, x, y1);
				}
                // lado inferior
                if (isFreeForObject(x, y2)) {
					return MapPos.mxy(this.mapNumber, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (isFreeForObject(x1, y)) {
					return MapPos.mxy(this.mapNumber, x1, y);
				}
                // lado derecho
                if (isFreeForObject(x2, y)) {
					return MapPos.mxy(this.mapNumber, x2, y);
				}
            }
        }
        return null;
    }

    
    public boolean isLegalPos(MapPos pos, boolean canWater, boolean canLand) {
        return pos.isValid() 
        		&& tile(pos.x,pos.y).isLegalPos(canWater, canLand);
    }
    
    public boolean isLegalPosNPC(MapPos pos, boolean canWater) {
        return pos.isValid() 
        		&& tile(pos.x,pos.y).isFreeForNpc(canWater);
    }
    
    private boolean isFreePosForUser(byte  x, byte y, boolean sailing, boolean isAdmin) {
    	if (isAdmin) {
			return isFreePosForAdmin(x, y); // Los Admins no respetan las leyes de la física :P
			
		} else if (sailing) {
			return isFreePosWithWater(x, y) 
					&& !isTeleport(x, y) 
					&& !isBlocked(x, y);
			
		} else {
			return isFreePosWithoutWater(x, y) 
					&& !isTeleport(x, y) 
					&& !isBlocked(x, y);
		}
    }
    
    /** 
     * Busca una posicion válida para un jugador, y que sea lo más cercana a la posición original 
     */
    public MapPos closestLegalPosUser(byte orig_x, byte orig_y, boolean sailing, boolean isAdmin) {
        if (isFreePosForUser(orig_x, orig_y, sailing, isAdmin)) {
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
                if (isFreePosForUser(x, y1, sailing, isAdmin)) {
					return MapPos.mxy(this.mapNumber, x, y1);
				}
                // lado inferior
                if (isFreePosForUser(x, y2, sailing, isAdmin)) {
					return MapPos.mxy(this.mapNumber, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (byte y = (byte) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (isFreePosForUser(x1, y, sailing, isAdmin)) {
					return MapPos.mxy(this.mapNumber, x1, y);
				}
                // lado derecho
                if (isFreePosForUser(x2, y, sailing, isAdmin)) {
					return MapPos.mxy(this.mapNumber, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean isFreePosForNpc(byte  x, byte y, boolean canWater, boolean canLand, boolean underRoof) {
    	if (isWater(x, y)) {
        	// Si el npc puede estar sobre agua, comprobamos si es una posicion libre con agua
            if (canWater) {
				return isFreePosWithWater(x, y) 
						&& !isTeleport(x, y) 
						&& !hasObject(x, y) 
						&& testSpawnTriggerNpc(x, y, underRoof);
			}
    	} else {
            // Comprobamos si es una posición libre sin agua
            if (!canLand) {
				return isFreePosWithoutWater(x, y) 
						&& !isTeleport(x, y) 
						&& !hasObject(x, y) 
						&& testSpawnTriggerNpc(x, y, underRoof);
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
        return Pos.isValid(x, y) 
        		&& tile(x, y).isFreePosWithWater();
    }
    
    private boolean isFreePosWithoutWater(byte  x, byte y) {
        return Pos.isValid(x, y) 
        		&& tile(x, y).isFreePosWithoutWater();
    }
    
    private boolean isFreePosForAdmin(byte  x, byte y) {
        // Los Admins no respetan las leyes de la física :P
        return Pos.isValid(x, y) 
        		&& tile(x, y).isFreePosForAdmin();
    }
    
    
    public void sendCreaturesInMap(User admin) {
    	// List of creatures per npc name
    	var hostiles = new TreeMap<String, List<Npc>>();
    	var others = new TreeMap<String, List<Npc>>();
    	
        for (Npc npc : this.npcs) {
            // ¿esta vivo?
            if (npc.isNpcActive() && npc.isHostile() && npc.stats.alineacion == 2) {
            	// Ya estaba este número de NPC en la lista?
                if (!hostiles.containsKey(npc.getName())) {
                	hostiles.put(npc.getName(), new ArrayList<Npc>());
                }
            	hostiles.get(npc.getName()).add(npc);
            } else {
            	if (!others.containsKey(npc.getName())) {
            		others.put(npc.getName(), new ArrayList<Npc>());
            	}
        		others.get(npc.getName()).add(npc);
            }
        }
        
        admin.sendMessage("Npcs Hostiles en mapa: ", FontType.FONTTYPE_WARNING);
        
        if (hostiles.isEmpty()) {
        	admin.sendMessage("No hay.", FontType.FONTTYPE_INFO);
        } else {
        	for (List<Npc> list : hostiles.values()) {
        		StringBuffer sb = new StringBuffer();
        		sb.append(String.format("%3d ", list.size()));
        		sb.append(list.get(0).getName());
        		String sep = ": "; 
        		for (Npc npc : list) {
        			sb.append(sep).append("(").append(npc.pos().x).append(",").append(npc.pos().y).append(")");
        			sep = ", ";
        		}
        		admin.sendMessage(sb.toString(), FontType.FONTTYPE_INFO);
        	}
        }
        
        admin.sendMessage("Otros Npcs en mapa: ", FontType.FONTTYPE_WARNING);
        if (others.isEmpty()) {
    		admin.sendMessage("No hay.", FontType.FONTTYPE_INFO);
        } else {
        	for (List<Npc> list : others.values()) {
        		StringBuffer sb = new StringBuffer();
        		sb.append(String.format("%3d ", list.size()));
        		sb.append(list.get(0).getName());
        		String sep = ": "; 
        		for (Npc npc : list) {
        			sb.append(sep).append("(").append(npc.pos().x).append(",").append(npc.pos().y).append(")");
        			sep = ", ";
        		}
        		admin.sendMessage(sb.toString(), FontType.FONTTYPE_INFO);
        	}
        }
        
        admin.sendMessage("El total de NPCs en el mapa " + this.getMapNumber() + " es de " + this.npcs.size(), 
        		FontType.FONTTYPE_WARNING);
    }
    
    public void saveMapBackup() {
        saveInfFile();
        saveMapFile();
        saveDatFile();
    }
    
    public void saveDatFile() {
        // Escribir archivo .dat
    	String filename = "worldBackup" + File.separator + "Mapa" + this.mapNumber + ".dat";
        try {
            IniFile ini = new IniFile();
            String section = "Mapa" + this.mapNumber;
            ini.setValue(section, "Name", this.name);
            ini.setValue(section, "MusicNum", this.music);
            // PK está invertido
            ini.setValue(section, "PK", !this.pk);
            
            ini.setValue(section, "MagiaSinEfecto", magiaSinEfecto);
            ini.setValue(section, "InviSinEfecto", inviSinEfecto);
            ini.setValue(section, "ResuSinEfecto", resuSinEfecto);
            ini.setValue(section, "NoEncriptarMP", noEncriptarMP);
            
            ini.setValue(section, "Restringir", this.restricted.toString());
            ini.setValue(section, "BackUp", this.backup);
            ini.setValue(section, "Terreno", this.terrain.toString());
            ini.setValue(section, "Zona", this.zone.toString());
            
            ini.setValue(section, "StartPos", 
            		this.startPos.map + "-" + this.startPos.x + "-" + this.startPos.y);
            	    
            ini.store(filename);
        } catch (Exception e) {
            log.fatal("ERROR GUARDANDO MAPA " + this.mapNumber, e);
        }
    }
    
    private void saveMapFile() {
        ////////// ARCHIVO .MAP
        // guardar cabecera del archivo .map
    	String filename = "worldBackup" + File.separator + "Mapa" + this.mapNumber + ".map";
        log.info("Guardando mapa: " + filename);
        try {
            DataOutputStream f =
            new DataOutputStream(
            new BufferedOutputStream(
            new FileOutputStream(filename)));
            try {
            	// HEADER
                f.writeShort(Short.reverseBytes(this.version));
                byte descBytes[] = new byte[255];
                for (int i = 0; i < this.desc.length() && i < 256; i++) {
					descBytes[i] = (byte)this.desc.charAt(i);
				}
                f.write(descBytes); // 255 bytes... por que no 256??
                f.writeInt(Integer.reverseBytes(this.crc));
                f.writeInt(Integer.reverseBytes(this.magicWord));
                f.writeLong(0); // FILL 8 bytes
                
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    for (int x = 0; x < MAP_WIDTH; x++) {
                    	byte flags = 0;
                    	
                    	if (this.tiles[x][y].isBlocked()) {
                    		flags += 1;
                    	}
                    	if (this.tiles[x][y].getGrh(1) != 0) {
                    		flags += 2;                    		
                    	}
                    	if (this.tiles[x][y].getGrh(2) != 0) {
                    		flags += 4;                    		
                    	}
                    	if (this.tiles[x][y].getGrh(3) != 0) {
                    		flags += 8;                    		
                    	}
                    	if (this.tiles[x][y].trigger() != Trigger.TRIGGER_NADA) {
                    		flags += 16;                    		
                    	}
                    	
                    	f.writeByte(flags);
                    	f.writeShort(Util.leShort((short) this.tiles[x][y].getGrh(0)));
                    	if ((flags & 2) > 0) {
                        	f.writeShort(Util.leShort((short) this.tiles[x][y].getGrh(1)));
                    	}
                    	if ((flags & 4) > 0) {
                        	f.writeShort(Util.leShort((short) this.tiles[x][y].getGrh(2)));
                    	}
                    	if ((flags & 8) > 0) {
                        	f.writeShort(Util.leShort((short) this.tiles[x][y].getGrh(3)));
                    	}
                    	if ((flags & 16) > 0) { // byte?
                        	f.writeShort(Util.leShort((short) this.tiles[x][y].trigger().ordinal()));
                    	}
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveMapFile " + this.mapNumber, e);
        }
    }
    
    private void saveInfFile() {
        //////// ARCHIVO .INF
    	String filename = "worldBackup" + File.separator + "Mapa" + this.mapNumber + ".inf";
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
                for (short y = 0; y < MAP_HEIGHT; y++) {
                    for (short x = 0; x < MAP_WIDTH; x++) {
                    	byte flags = 0;
                    	if (this.tiles[x][y].isTeleport()) {
                    		flags += FLAG_TELEPORT;
                    	}
                    	// FIXME todos los npcs se guardan?
                    	if (this.tiles[x][y].hasNpc()) {
                    		flags += FLAG_NPC;
                    	}
                        if (this.tiles[x][y].hasObject()) {
                        	flags += FLAG_OBJECT;
                        }
                        
                        f.writeByte(flags);
                    	
                        if ((flags & FLAG_TELEPORT) > 0) {
                            f.writeShort(Util.leShort(this.tiles[x][y].teleport().map));
                            f.writeShort(Util.leShort(this.tiles[x][y].teleport().x));
                            f.writeShort(Util.leShort(this.tiles[x][y].teleport().y));
                        }
                        
                        if ((flags & FLAG_NPC) > 0) {
                        	f.writeShort(Util.leShort((short)this.tiles[x][y].npc().getNumber()));
                        }
                        
                        if ((flags & FLAG_OBJECT) > 0) {
                        	MapObject mObj = getObject((byte)(x+1), (byte)(y+1));
                            f.writeShort(Util.leShort(mObj.objInfo().ObjIndex));
                            f.writeShort(Util.leShort((short)mObj.obj_cant));
                        }
                    }
                }
            } finally {
                f.close();
            }
        } catch (java.io.IOException e) {
            log.fatal("ERROR EN saveInfFile " + this.mapNumber, e);
        }
    }
    
    public void objectMassDestroy(User admin, byte pos_x, byte pos_y) {
        int x1 = pos_x - MinXBorder + 1;
        int x2 = pos_x + MaxXBorder + 1;
        int y1 = pos_y - MinYBorder + 1;
        int y2 = pos_y + MaxYBorder + 1;
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
        int count = 0;
        for (int y = y1; y <= y2; y++) {
        	for (int x = x1; x <= x2; x++) {
                if (hasObject((byte) x, (byte) y) 
                		&& getObject((byte) x, (byte) y).objInfo().itemNoEsDeMapa()) {
                    removeObject((byte) x, (byte) y);
                    count++;
                }
            }
        }
        if (count == 0) {
        	admin.sendMessage("Se no encontraron objectos para eliminar.", FontType.FONTTYPE_WARNING);
        } else if (count == 1) {
        	admin.sendMessage("Se eliminaron objetos de un tile.", FontType.FONTTYPE_WARNING);
        } else {
        	admin.sendMessage("Se han eliminado objetos en " + count + " tiles.", FontType.FONTTYPE_WARNING);
        }
    }    
    
    public void construirAreaObj(User user, byte  x, byte y) {
    	MapObject obj = getObject(x,y);
    	if (obj != null) user.sendPacket(new ObjectCreateResponse((byte)x, (byte)y, (short)obj.objInfo().GrhIndex));
    }
    
    public void construirAreaNpc(User user, Npc npc) {
    	user.sendPacket(npc.characterCreate());
    }
    
    public void soundFx() {
        if (getUsersCount() > 0 && Util.random(1, 150) < 12) {
        	final byte sound = randomSoundFx();
            if (sound > -1) {
            	this.users.stream()
            		.forEach(u -> u.sendPacket(new PlayWaveResponse(sound, u.pos().x, u.pos().y)));
            }
        }
    }

	private byte randomSoundFx() {
		byte sound = -1;
		switch (this.terrain) {
		    case FOREST:
		        int n = Util.random(1, 100);
		        switch (this.zone) {
		            case COUNTRY:
		            case CITY:
		                if (!this.server.isRaining()) {
		                    if (n < 15) {
		                    	sound = Constants.SOUND_AVE2;
		                    } else if (n < 30) {
		                    	sound = Constants.SOUND_AVE;
		                    } else if (n <= 35) {
		                    	sound = Constants.SOUND_GRILLO;
		                    } else if (n <= 40) {
		                    	sound = Constants.SOUND_GRILLO2;
		                    } else if (n <= 45) {
		                    	sound = Constants.SOUND_AVE3;
		                    }
		                }
		                break;
				default:
					break;
		        }
		default:
			break;
		}
		return sound;
	}

    
    @Deprecated
    public void doTileEvents(User user) {
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
    public boolean isForbbidenMap(User user) {
    	// ¿Es mapa de newbies?
    	if (isNewbieMap()) {
    		if (user.isNewbie() || user.getFlags().isGM()) {
    			return false; // allowed
    		} else {
    			// no es un newbie/gm, "NO PASARÁS!"
				user.sendMessage("Mapa exclusivo para newbies.", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	} 
    	
		// ¿Es mapa de Armadas?
    	if (isRoyalArmyMap()) {
            // ¿El usuario es Armada?
    		if (user.isRoyalArmy() || user.getFlags().isGM()) {
    			return false; // allowed
    		} else {
    			// no es un armada/gm, "NO PASARÁS!"
				user.sendMessage("Mapa exclusivo para miembros del ejército Real", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
		// ¿Es mapa de Caos?
    	if (isDarkLegionMap()) {
            // ¿El usuario es Caos?
    		if (user.isDarkLegion() || user.getFlags().isGM()) {
    			return false; // allowed
    		} else {
    			// no es un caos/gm, "NO PASARÁS!"
				user.sendMessage("Mapa exclusivo para miembros del ejército Oscuro.", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
		// ¿Es mapa de faccionarios?
    	if (isFactionMap()) {
            // ¿El usuario es Caos?
    		if (user.isRoyalArmy() || user.isDarkLegion() || user.getFlags().isGM()) {
    			return false; // allowed
    		} else {
    			// no es un armada/caos/gm, "NO PASARÁS!"
				user.sendMessage("Solo se permite entrar al Mapa si eres miembro de alguna Facción", FontType.FONTTYPE_INFO);
    			return true;
    		}
    	}
    	
    	// No es un mapa de newbies, ni Armadas, ni Caos, ni faccionario.
    	// Adelante averturero
    	return false; // allowed;
    }

	public boolean isFactionMap() {
		return this.restricted == MapConstraint.FACTION;
	}

	public boolean isDarkLegionMap() {
		return this.restricted == MapConstraint.DARK_LEGION;
	}

	public boolean isRoyalArmyMap() {
		return this.restricted == MapConstraint.ROYAL_ARMY;
	}

	public boolean isNewbieMap() {
		return this.restricted == MapConstraint.NEWBIE;
	}

}
