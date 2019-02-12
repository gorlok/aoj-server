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
package org.ArgentumOnline.server;

import static org.ArgentumOnline.server.protocol.serverPacketID.MSG_CCNPC;
import static org.ArgentumOnline.server.protocol.serverPacketID.MSG_HO;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.ArgentumOnline.server.areas.areasAO;
import org.ArgentumOnline.server.protocol.serverPacketID;
import org.ArgentumOnline.server.util.BytesReader;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.util.Util;

/**
 *
 * @author  pablo
 */
public class Map implements Constants {
    
    /** Número de mapa. */
    private short nroMapa;
    areasAO areasData = new areasAO();
    
    // Cabecera archivo .map
    private short version   = 0;
    //private byte desc[] = new byte[255];
    private String desc;
    //private int crc     = 0;
    //private int mw      = 0;
    
    // Información del archivo .dat
    String  m_name  = "";
    String  m_music = "";
    int     m_numUsers = 0;
    int     m_mapVersion = 0;
    short   m_terreno = TERRENO_BOSQUE;
    short   m_zona = ZONA_CAMPO;
    boolean m_pk = false;
    boolean m_restringir = false;
    boolean m_backup = false;
    short   m_version = 0;
    WorldPos m_startPos = new WorldPos();
    
    AojServer server;
    
    /** Bloques o celdas del mapa. */
    MapCell m_cells[][] = new MapCell[MAPA_ANCHO][MAPA_ALTO];
    
    /** Objetos en el mapa. */
    Vector<MapCell> bloquesConObjetos = new Vector<MapCell>();
    
    /** Clientes en el mapa */
    Vector<Client> m_clients = new Vector<Client>();
    
    /** NPCs en el mapa */
    Vector<Npc> m_npcs = new Vector<Npc>();
    
    /** Creates a new instance of Map */
    public Map(short nroMapa, AojServer server) {
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
    
    /** Getter for property nro.
     * @return Value of property nro.
     *
     */
    public int getNroMapa() {
        return this.nroMapa;
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
    
    public MapObject getObjeto(short x, short y) {
    	if (this.m_cells[x-1][y-1].hayObjeto()) {
			return new MapObject(this.m_cells[x-1][y-1].getObjInd(),
    				this.m_cells[x-1][y-1].getObjCant(), x, y);
		}
		return null;
    }
    
    public Npc getNPC(short x, short y) {
        return this.m_cells[x-1][y-1].getNpc();
    }
    
    public short getTrigger(short x, short y) {
        return this.m_cells[x-1][y-1].getTrigger();
    }
    
    public void setTrigger(short x, short y, byte value) {
        this.m_cells[x-1][y-1].setTrigger(value);
    }
    
    public boolean testSpawnTrigger(short x, short y) {
        int trigger = this.m_cells[x-1][y-1].getTrigger();
        return trigger != 3 && trigger != 2 && trigger != 1;
    }
    
    public boolean testSpawnTriggerNpc(short mapa, short x, short y, boolean bajoTecho) {
    	return testSpawnTriggerNpc(new WorldPos(mapa, x, y), bajoTecho);
    }
    
    public boolean testSpawnTriggerNpc(WorldPos pos, boolean bajoTecho) {
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
    public boolean hayAgua(short x, short y) {    	
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
    
    public boolean estaCliente(Client cliente) {
        return this.m_clients.contains(cliente);
    }
    
    public boolean intemperie(short x, short y) {
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
        for (Client cli: this.m_clients) {
            if (!"".equals(cli.getNick())) {
                usuarios.add(cli.getNick());
            }
        }
        return usuarios;
    }
    
    public Client spUser(int value) {
    	return this.m_clients.get(value);
    }
    
    public Npc spNpc(int value) {
    	return this.m_npcs.get(value);
    }
    
    public boolean esZonaSegura() {
        return this.m_pk;
    }
    
    public void load(boolean loadBackup) {
        try {
            String datFile = "Mapa" + this.nroMapa + ".dat";
            String mapFile = "Mapa" + this.nroMapa + ".map";
            String infFile = "Mapa" + this.nroMapa + ".inf";
            loadDatFile(datFile, loadBackup);
            AojServer.showMemoryStatus("loadDatFile ready #" + this.nroMapa);
            loadMapFile(mapFile, loadBackup);
            AojServer.showMemoryStatus("loadMapFile ready #" + this.nroMapa);
            loadInfFile(infFile, loadBackup);
            AojServer.showMemoryStatus("loadInfFile ready #" + this.nroMapa);

        } catch (java.io.FileNotFoundException e) {
            Log.serverLogger().warning("Archivo de mapa " + this.nroMapa + " faltante.");
        } catch (java.io.IOException e) {
            Log.serverLogger().warning("Error leyendo archivo de mapa " + this.nroMapa);
        } catch (Exception e) {
        	Log.serverLogger().warning("Error con mapa " + this.nroMapa);
        }
    }
    
    private void loadDatFile(String datFileName, boolean loadBackup) 
    throws java.io.FileNotFoundException, java.io.IOException {
        // Cargar información del archivo .dat
        IniFile ini = new IniFile();
        if (loadBackup && Util.existeArchivo("worldBackup" + File.separator + datFileName)) {
			ini.load("worldBackup" + File.separator + datFileName);
		} else {
			ini.loadFromJar("mapas/" + datFileName);
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
        String startPos = ini.getString(section, "StartPos");
        if (startPos.length() > 4) {
            String mapa = startPos.substring(0, startPos.indexOf('-'));
            String x = startPos.substring(startPos.indexOf('-')+1, startPos.lastIndexOf('-'));
            String y = startPos.substring(startPos.lastIndexOf('-')+1);
            this.m_startPos.mapa = Short.parseShort(mapa);
            this.m_startPos.x = Short.parseShort(x);
            this.m_startPos.y = Short.parseShort(y);
        }
        if (this.m_startPos.mapa == 0) {
			this.m_startPos.mapa = 45;
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

	    File map = new File("worldBackup" + File.separator + mapFileName);
	    byte[] bytes = null;
	    
	    if (!loadBackup || !map.exists()) {
	        bytes = readBytesFromJar(mapFileName);
	    } else {
	        bytes = Files.readAllBytes(map.toPath());
	    }
	    
	    if (bytes == null) {
	    	System.out.println("Error, archivo de mapa inexistente: " + mapFileName);
	    	return;            	
	    }
	    
	    try {
            	 
            int byflags = 0;
            BytesReader getter = new BytesReader();
                 
            getter.setBytes(bytes);
            
            this.version = getter.readShort();
            
            getter.skipBytes(255);
            
            getter.readInt();
            getter.readInt();
            getter.skipBytes(8);
            
            for (int y = 0; y < MAPA_ALTO; y++) {
                for (int x = 0; x < MAPA_ANCHO; x++) {
                	
                	byflags = getter.readByte();
                	
                	if ((byflags & 1) == 1) {
                		this.m_cells[x][y].setBloqueado(true);
                	}
                    
                	this.m_cells[x][y].setGrh(0, Util.leShort(getter.readShort()));
                    
                    if ((byflags & 2) == 2) {
                    	this.m_cells[x][y].setGrh(1, Util.leShort(getter.readShort()));
                    }
                    
                    if ((byflags & 4) == 4) {
                    	this.m_cells[x][y].setGrh(2, Util.leShort(getter.readShort()));
                    }
                    if ((byflags & 8) == 8) {
                    	this.m_cells[x][y].setGrh(3, Util.leShort(getter.readShort()));
                    }
                    
                    if ((byflags & 16) == 16) {
                    	this.m_cells[x][y].setTrigger((byte) Util.leShort(getter.readShort()));
                    }
                    
                }
            }         
            
        } catch (Exception e) {
        	System.out.println("ERROR LOADING " + mapFileName);
        }
    }

	private byte[] readBytesFromJar(String mapFileName) throws MalformedURLException, IOException {
		byte[] bytes = null;
		URL url = new URL("jar:file:mapas.jar!/mapas/" + mapFileName);
		URLConnection jar;
		InputStream is = null;
		ByteArrayOutputStream buffer = null;
		byte[] data;
		try {
		    jar = url.openConnection();
		    is = jar.getInputStream();
			buffer = new ByteArrayOutputStream();
		    long size = jar.getContentLengthLong();
			int nRead;
			data = new byte[(int)size];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			bytes = buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) is.close();
			if (buffer != null) buffer.close();
		}
		return bytes;
	}
    
    
    /**
     * Carga el archivo .inf de la carpeta backup
     * by Agush
     */
    private void loadInfFile(String infFileName, boolean loadBackup)
    	    throws java.io.IOException {

        File map = new File("worldBackup" + File.separator + infFileName);
        
        if (!map.exists()) {
        	System.out.println("Error, archivo de mapa inexistente: " + infFileName);
        	return;
        }
        
        try {
        	
        Npc npc = null;
        short dest_mapa = 0;
        short dest_x = 0;
        short dest_y = 0;
        int byflags = 0;
        byte[] array = Files.readAllBytes(map.toPath());
        BytesReader getter = new BytesReader();
        
             
        getter.setBytes(array);
        getter.skipBytes(10);
        getter.mark();
        
        for (int y = 0; y < MAPA_ALTO; y++) {
            for (int x = 0; x < MAPA_ANCHO; x++) {
            	
            	byflags = getter.readByte();
            	
            	if ((byflags & 1) == 1) {
					this.m_cells[x][y].setTeleport(new WorldPos(Util.leShort(getter.readShort())
							,Util.leShort(getter.readShort()), Util.leShort(getter.readShort())));
            	}
                
                if ((byflags & 2) == 2) {
                    short npcId = Util.leShort(getter.readShort());
                    if (npcId > 0) {
                        // Crear un nuevo Npc.
                        this.m_cells[x][y].setNpc(this.server.crearNPC(npcId, loadBackup));
                        npc = this.m_cells[x][y].getNpc();
                        this.m_npcs.add(npc);
                        npc.getPos().mapa = this.nroMapa;
                        npc.getPos().x = (short) (x+1);
                        npc.getPos().y = (short) (y+1);
                        npc.getOrig().mapa = this.nroMapa;
                        if (npc.respawnOrigPos()) {
	                        npc.getOrig().x = (short) (x+1);
	                        npc.getOrig().y = (short) (y+1);
                        } else {
	                        npc.getOrig().x = (short)0;
	                        npc.getOrig().y = (short)0;                        
                        }
                        npc.activar();
                        if (npc.getNPCtype() == 5) {
							System.out.println(npc + " " + npc.getPos()); // FIXME
						}
                        // JAO: Sistema de areas!!
                      //  this.areasData.setNpcArea(npc);
                        this.areasData.loadNpc(npc);
                    }
                }
                
                if ((byflags & 4) == 4) {
                    short obj_ind = Util.leShort(getter.readShort());
                    short obj_cant = Util.leShort(getter.readShort());
                        //Objeto obj = agregarObjeto(obj_ind, obj_cant, (short)(x+1), (short)(y+1));
                       agregarObjeto(obj_ind, obj_cant, (short)(x+1), (short)(y+1));
                       enviarPuerta(getObjeto((short)(x+1), (short)(y+1)));
                    
                }
                
            }
        }         
        
        
        
        
        } catch(Exception e) {
        	System.out.println("Error: " + e.getMessage());
        }

    }
    
    public boolean entrar(Client cliente, short x, short y) {
        if (this.m_cells[x-1][y-1].getClienteId() != 0) {
			return false;
		}
        this.m_clients.add(cliente);
        this.m_cells[x-1][y-1].setClienteId(cliente.getId());
        //enviarATodosExc(cliente.getId(), serverPacketID.CC, cliente.ccParams());
        //this.areasData.sendToArea(x, y, cliente.getId(), serverPacketID.CC, cliente.ccParams());
        
        cliente.m_pos = new WorldPos(this.nroMapa, x, y); // REVISAR
        return true;
    }
    
    public boolean salir(Client cliente) {
        short x = cliente.getPos().x;
        short y = cliente.getPos().y;
        try {
	        //if (estaCliente(cliente)) {
	        	try {
		           // enviarATodos(MSG_QDL, cliente.getId());
		            //enviarATodos(serverPacketID.MSG_BP, cliente.getId());
		            //this.areasData.sendToArea(x, y, cliente.getId(), serverPacketID.MSG_BP, cliente.getId());
	        		this.areasData.sendToUserArea(cliente, serverPacketID.MSG_BP, cliente.getId());
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
	        if (this.m_cells[x-1][y-1].getClienteId() != cliente.m_id) {
	            // Hay una inconsistencia: no se encuentra donde deberia :-S
	            return false;
	        	//System.exit(1); // ERROR MUY FEO - FIXME
	        }
	        this.m_cells[x-1][y-1].setClienteId((short) 0);
        }
        return true;
    }
    
    public void moverNpc(Npc npc, short x, short y) {
        if (this.m_cells[x-1][y-1].getNpc() != null) {
			Log.serverLogger().severe("ERRRRRRRRRRORRRRRRRRRRRR en moverNpc: " + npc);
		}
        this.m_cells[npc.getPos().x-1][npc.getPos().y-1].setNpc(null);
        this.m_cells[x-1][y-1].setNpc(npc);
        
        //areasData.updateNpcArea(npc);
		//this.areasData.sendToArea(x, y, npc.getId(), serverPacketID.MSG_MP, npc.getId(), x, y);
        this.areasData.checkUpdateNeededNpc(npc, npc.getInfoChar().getDir());
        this.areasData.sendToNPCArea(npc, serverPacketID.MSG_MP, npc.getId(), x, y);
        
    }
    
    public boolean isFree(short x, short y) {
        return (this.m_cells[x-1][y-1].getClienteId() == 0) && (this.m_cells[x-1][y-1].getNpc() == null);
    }
    
    public boolean estaBloqueado(short x, short y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return true;
		}
        return this.m_cells[x-1][y-1].estaBloqueado();
    }
    
    public boolean entrar(Npc npc, short x, short y) {
        if (this.m_cells[x-1][y-1].getNpc() != null) {
			return false;
		}
        
        this.m_cells[x-1][y-1].setNpc(npc);
        this.m_npcs.add(npc);
        
        npc.m_pos = new WorldPos(this.nroMapa, x, y);
      
        this.areasData.loadNpc(npc);
        
		//this.areasData.setNpcArea(npc);
		//this.areasData.sendToArea(x, y, -1, serverPacketID.MSG_CCNPC, npc.ccParams());
        
        return true;
    }
    
    public boolean salir(Npc npc) {
        int x = npc.getPos().x;
        int y = npc.getPos().y;
        try {
	      //  enviarATodos(MSG_QDL, npc.getId());
	        enviarAlArea((short) x, (short) y, serverPacketID.MSG_BP, npc.getId());
        } finally {
            this.m_cells[x-1][y-1].setNpc(null);
	        this.m_npcs.remove(npc);
	        npc.m_pos = new WorldPos(this.nroMapa, (short)0, (short)0);
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
                	if (npc.getAmo() != null) {
                		Client masterUser = (npc.getAmo());
                		masterUser.quitarMascota(npc);
                	}
                	
                	salir(npc);
                }
            }
        }
    }    
    
    //public Objeto agregarObjeto(short objid, int cant, short x, short y) {
    public boolean agregarObjeto(short objid, int cant, short x, short y) {
        //Objeto obj = new Objeto(objid, cant, x, y);
        //bloques[x-1][y-1].setObj(obj);
    	if (hayObjeto(x, y)) {
            Log.serverLogger().warning("Intento de agregar objeto sobre otro: objid=" + objid + " cant" + cant + " mapa" + this.nroMapa + " x=" + x + " y=" + y);
    		return false;
    	}
    	this.m_cells[x-1][y-1].setObj(objid, cant);
        this.bloquesConObjetos.add(this.m_cells[x-1][y-1]);
        short grhIndex = this.server.getInfoObjeto(objid).GrhIndex;
        
        System.out.println(grhIndex + "-" + x + "-" + y);
        
        //Agush: Nuevo sistema de areas!!
        //this.areasData.setObjArea(x, y, objid);
        //enviarAlArea(x,y, serverPacketID.MSG_HO, grhIndex, x ,y);
        
        this.areasData.sendToAreaByPos(this, x, y,serverPacketID.MSG_HO, grhIndex, x ,y);
        
        //enviarAlArea(x,y, serverPacketID.MSG_HO, grhIndex,x,y);
        
        //enviarATodos(serverPacketID.MSG_HO, grhIndex, x, y);
        
        //return obj;
        return true;
    }
    
    public void quitarObjeto(short x, short y) {
        //objetos.remove(getObjeto(x, y));
    	short obj = this.m_cells[x-1][y-1].getObjInd();
    	//this.areasData.deleteObj(x, y, obj);
    	this.bloquesConObjetos.remove(this.m_cells[x-1][y-1]);
        this.m_cells[x-1][y-1].quitarObjeto();
       // enviarATodos(serverPacketID.MSG_BO, x, y);
        enviarAlArea(x,y,serverPacketID.MSG_BO, x,y);
    }
    
    public void bloquearTerreno(int x, int y) {
        this.m_cells[x-1][y-1].setBloqueado(true);
        this.m_cells[x-1][y-1].setModificado(true);
       // enviarATodos(serverPacketID.MSG_BQ, (short) x, (short) y, (short) 1);
        
        short mx = (short) x;
        short my = (short) y;
        
        enviarAlArea(mx,my, serverPacketID.MSG_BQ, mx, my, (short) 1);
    }
    
    public void desbloquearTerreno(int x, int y) {
        this.m_cells[x-1][y-1].setBloqueado(false);
        this.m_cells[x-1][y-1].setModificado(true);
        //enviarATodos(serverPacketID.MSG_BQ, (short) x, (short) y, (short) 0);
        
        short mx = (short) x;
        short my = (short) y;
        
        enviarAlArea(mx,my, serverPacketID.MSG_BQ, mx, my, (short) 0);
        
    }
    
    public void abrirCerrarPuerta(MapObject obj) {
        if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
            // Es un objeto tipo puerta.
            if (obj.getInfo().estaCerrada()) {
                // Abrir puerta.
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = this.server.getInfoObjeto(obj.getInfo().IndexAbierta);
                //obj = agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            } else {
                // Cerrar puerta
                quitarObjeto(obj.x, obj.y);
                ObjectInfo info = this.server.getInfoObjeto(obj.getInfo().IndexCerrada);
                //obj = agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
                agregarObjeto(info.ObjIndex, obj.obj_cant, obj.x, obj.y);
            }
            obj = getObjeto(obj.x, obj.y);
            enviarPuerta(obj);
            enviarAlArea(obj.x, obj.y, serverPacketID.MSG_TW, (byte) SND_PUERTA, obj.x, obj.y);
        }
    }
    
    private void enviarPuerta(MapObject obj) {
        if (obj.getInfo().ObjType == OBJTYPE_PUERTAS) {
            // Es un objeto tipo puerta.
            if (obj.getInfo().estaCerrada()) {
                bloquearTerreno(obj.x-1, obj.y);
                bloquearTerreno(obj.x, obj.y);
            } else {
                desbloquearTerreno(obj.x-1, obj.y);
                desbloquearTerreno(obj.x, obj.y);
            }
            
          //  System.out.println("Envío puerta: " + obj.x + "-" + obj.y);
            enviarAlArea(obj.x,obj.y,serverPacketID.MSG_HO, obj.getInfo().GrhIndex, obj.x, obj.y);
        }
    }
    
    public void enviarCFX(short x, short y, int id, int fx, int val) {
       // enviarAlArea(x, y, MSG_CFX, id, fx, val);
    	enviarAlArea(x,y,serverPacketID.MSG_FX, (short) id, (short) fx, (short) val);
    }
    
    public void enviarATodos(serverPacketID msg, Object... params) {
        enviarATodosExc(-1, msg, params);
    }
    
    public void enviarATodosExc(int excepto, serverPacketID msg, Object... params) {
        for (Object element : this.m_clients) {
            Client cliente = (Client) element;
            if (cliente.getId() != excepto) {
            	try {
            		cliente.enviar(msg, params); // FIXME: acceso concurrente, ojo.
            	} catch (Exception e) {
            		//
            	}
            }
        }
    }
    
    public void enviarAlArea(short x, short y, serverPacketID msg, Object... params) {
        enviarAlArea(x, y, -1, msg, params);
    }
        
    public void enviarAlArea(short pos_x, short pos_y, int excepto, serverPacketID msg, Object... params) {
    	//areasData.sendToArea(pos_x, pos_y, excepto, msg, params);
    	areasData.sendToAreaButIndex(this, pos_x, pos_y, excepto, msg, params);
    }
    
    public void enviarAlAreaAdminsNoConsejeros(short pos_x, short pos_y, serverPacketID msg, Object... params) {
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
        Client cliente;
        for (short y = y1; y <= y2; y++) {
            for (short x = x1; x <= x2; x++) {
                if (this.m_cells[x-1][y-1].getClienteId() > 0) {
                    cliente = this.server.getCliente(this.m_cells[x-1][y-1].getClienteId());
                    if (cliente != null && (cliente.esDios() || cliente.esSemiDios())) {
						cliente.enviar(msg, params);
					}
                }
            }
        }
    }
    
    public Client buscarEnElArea(short pos_x, short pos_y, short cli_id) {
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
					return this.server.getCliente(this.m_cells[x-1][y-1].getClienteId());
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
    public void enviarObjetos(Client cliente) {
        for (Object element : this.bloquesConObjetos) {
            MapCell b = (MapCell) element;
            cliente.enviarObjeto(b.getObjInd(), b.getX(), b.getY());
        }
    }
    
    /** Enviarme los NPCs del mapa. */
    public void enviarNPCs(Client cliente) {
        for (Object element : this.m_npcs) {
            Npc npc = (Npc) element;
            cliente.enviar(MSG_CCNPC, npc.ccParams());
        }
    }
    
    /** Enviarme las posiciones bloqueadas del mapa. */
    public void enviarBQs(Client cliente) {
        for (int y = 0; y < MAPA_ALTO; y++) {
            for (int x = 0; x < MAPA_ANCHO; x++) {
                if (this.m_cells[x][y].estaModificado()) {
					cliente.enviarBQ(x+1, y+1, this.m_cells[x][y].estaBloqueado());
				}
            }
        }
    }
    
    public void mover(Client cliente, short x, short y) {
        this.m_cells[cliente.getPos().x-1][cliente.getPos().y-1].setClienteId((short) 0);
        this.m_cells[x-1][y-1].setClienteId(cliente.getId());
        cliente.m_pos = new WorldPos(this.nroMapa, x, y);
        
		//JAO: Nuevo sistema de areas !!
		//this.areasData.updateUserArea(cliente);
		//this.areasData.sendToArea(x, y, cliente.getId(), serverPacketID.MSG_MP, cliente.getId(), x, y);
        
        this.areasData.sendToAreaButIndex(this, x, y, cliente.getId(), serverPacketID.MSG_MP, cliente.getId(), x, y);
        this.areasData.checkUpdateNeededUser(cliente, cliente.getInfoChar().getDir());
        
    }
    
    public boolean hayTeleport(short x, short y) {
        return this.m_cells[x-1][y-1].hayTeleport();
    }
    
    public void crearTeleport(short x, short y, short dest_mapa, short dest_x, short dest_y) {
        if (dest_mapa > 0 && dest_x > 0 && dest_y > 0) {
			this.m_cells[x-1][y-1].setTeleport(new WorldPos(dest_mapa, dest_x, dest_y));
		}
        //Objeto obj = agregarObjeto(OBJ_TELEPORT, 1, x, y);
        agregarObjeto(OBJ_TELEPORT, 1, x, y);
    }
    
    public void destruirTeleport(short x, short y) {
        if (!hayTeleport(x, y)) {
			return;
		}
        quitarObjeto(x, y);
        this.m_cells[x-1][y-1].setTeleport(null);
    }    
    
    public boolean hayObjeto(short x, short y) {
        return (this.m_cells[x-1][y-1].hayObjeto());
    }
    
    public boolean hayCliente(short x, short y) {
        return (this.m_cells[x-1][y-1].getClienteId() != 0 && getCliente(x, y) != null); // FIXME
    }
    
    public boolean hayNpc(short x, short y) {
        return (this.m_cells[x-1][y-1].getNpc() != null);
    }
    
    public Client getCliente(short x, short y) {
        return this.server.getCliente(this.m_cells[x-1][y-1].getClienteId());
    }
    
    public Npc getNpc(short x, short y) {
        return this.m_cells[x-1][y-1].getNpc();
    }
    
    public MapObject buscarObjeto(short x, short y) {
        // Ver si hay un objeto en los alrededores...
        if (hayObjeto(x, y)) {
            return getObjeto(x, y);
        }
        if (hayObjeto((short) (x+1), y)) {
            return getObjeto((short) (x+1), y);
        }
        if (hayObjeto((short) (x+1), (short) (y+1))) {
            return getObjeto((short) (x+1), (short) (y+1));
        }
        if (hayObjeto(x, (short) (y+1))) {
            return getObjeto(x, (short) (y+1));
        }
        return null;
    }
    public Client buscarCliente(short x, short y) {
        // Ver si hay un cliente en los alrededores...
        if (hayCliente(x, (short) (y+1))) {
            return getCliente(x, (short) (y+1));
        }
        if (hayCliente(x, y)) {
            return getCliente(x, y);
        }
        return null;
    }
    public Npc buscarNpc(short x, short y) {
        // Ver si hay un NPC en los alrededores...
        if (hayNpc(x, (short) (y+1))) {
            return getNpc(x, (short) (y+1));
        }
        if (hayNpc(x, y)) {
            return getNpc(x, y);
        }
        return null;
    }
/*
    Sub LookatTile(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer)
        'Responde al click del usuario sobre el mapa
        Dim FoundChar As Byte
        Dim FoundSomething As Byte
        Dim TempCharIndex As Integer
        Dim Stat As String
        '¿Posicion valida?
        If InMapBounds(Map, X, Y) Then
            UserList(UserIndex).flags.TargetMap = Map
            UserList(UserIndex).flags.TargetX = X
            UserList(UserIndex).flags.TargetY = Y
            '¿Es un obj?
            If MapData(Map, X, Y).OBJInfo.ObjIndex > 0 Then
                'Informa el nombre
                Call SendData(ToIndex, UserIndex, 0, "||" & ObjData(MapData(Map, X, Y).OBJInfo.ObjIndex).Name & FONTTYPE_INFO)
                UserList(UserIndex).flags.TargetObj = MapData(Map, X, Y).OBJInfo.ObjIndex
                UserList(UserIndex).flags.TargetObjMap = Map
                UserList(UserIndex).flags.TargetObjX = X
                UserList(UserIndex).flags.TargetObjY = Y
                FoundSomething = 1
            ElseIf MapData(Map, X + 1, Y).OBJInfo.ObjIndex > 0 Then
                'Informa el nombre
                If ObjData(MapData(Map, X + 1, Y).OBJInfo.ObjIndex).ObjType = OBJTYPE_PUERTAS Then
                    Call SendData(ToIndex, UserIndex, 0, "||" & ObjData(MapData(Map, X + 1, Y).OBJInfo.ObjIndex).Name & FONTTYPE_INFO)
                    UserList(UserIndex).flags.TargetObj = MapData(Map, X + 1, Y).OBJInfo.ObjIndex
                    UserList(UserIndex).flags.TargetObjMap = Map
                    UserList(UserIndex).flags.TargetObjX = X + 1
                    UserList(UserIndex).flags.TargetObjY = Y
                    FoundSomething = 1
                End If
            ElseIf MapData(Map, X + 1, Y + 1).OBJInfo.ObjIndex > 0 Then
                If ObjData(MapData(Map, X + 1, Y + 1).OBJInfo.ObjIndex).ObjType = OBJTYPE_PUERTAS Then
                    'Informa el nombre
                    Call SendData(ToIndex, UserIndex, 0, "||" & ObjData(MapData(Map, X + 1, Y + 1).OBJInfo.ObjIndex).Name & FONTTYPE_INFO)
                    UserList(UserIndex).flags.TargetObj = MapData(Map, X + 1, Y + 1).OBJInfo.ObjIndex
                    UserList(UserIndex).flags.TargetObjMap = Map
                    UserList(UserIndex).flags.TargetObjX = X + 1
                    UserList(UserIndex).flags.TargetObjY = Y + 1
                    FoundSomething = 1
                End If
            ElseIf MapData(Map, X, Y + 1).OBJInfo.ObjIndex > 0 Then
                If ObjData(MapData(Map, X, Y + 1).OBJInfo.ObjIndex).ObjType = OBJTYPE_PUERTAS Then
                    'Informa el nombre
                    Call SendData(ToIndex, UserIndex, 0, "||" & ObjData(MapData(Map, X, Y + 1).OBJInfo.ObjIndex).Name & FONTTYPE_INFO)
                    UserList(UserIndex).flags.TargetObj = MapData(Map, X, Y).OBJInfo.ObjIndex
                    UserList(UserIndex).flags.TargetObjMap = Map
                    UserList(UserIndex).flags.TargetObjX = X
                    UserList(UserIndex).flags.TargetObjY = Y + 1
                    FoundSomething = 1
                End If
            End If
            '¿Es un personaje?
            If Y + 1 <= YMaxMapSize Then
                If MapData(Map, X, Y + 1).UserIndex > 0 Then
                    TempCharIndex = MapData(Map, X, Y + 1).UserIndex
                    FoundChar = 1
                End If
                If MapData(Map, X, Y + 1).NpcIndex > 0 Then
                    TempCharIndex = MapData(Map, X, Y + 1).NpcIndex
                    FoundChar = 2
                End If
            End If
            '¿Es un personaje?
            If FoundChar = 0 Then
                If MapData(Map, X, Y).UserIndex > 0 Then
                    TempCharIndex = MapData(Map, X, Y).UserIndex
                    FoundChar = 1
                End If
                If MapData(Map, X, Y).NpcIndex > 0 Then
                    TempCharIndex = MapData(Map, X, Y).NpcIndex
                    FoundChar = 2
                End If
            End If
            'Reaccion al personaje
            If FoundChar = 1 Then '  ¿Encontro un Usuario?
               If UserList(TempCharIndex).flags.AdminInvisible = 0 Then
                    If EsNewbie(TempCharIndex) Then
                        Stat = " <NEWBIE>"
                    End If
                    If UserList(TempCharIndex).Faccion.ArmadaReal = 1 Then
                        Stat = Stat & " <Ejercito real> " & "<" & TituloReal(TempCharIndex) & ">"
                    ElseIf UserList(TempCharIndex).Faccion.FuerzasCaos = 1 Then
                        Stat = Stat & " <Fuerzas del caos> " & "<" & TituloCaos(TempCharIndex) & ">"
                    End If
                    If UserList(TempCharIndex).GuildInfo.GuildName <> "" Then
                        Stat = Stat & " <" & UserList(TempCharIndex).GuildInfo.GuildName & ">"
                    End If
                    If Len(UserList(TempCharIndex).Desc) > 1 Then
                        Stat = "||Ves a " & UserList(TempCharIndex).Name & Stat & " - " & UserList(TempCharIndex).Desc
                    Else
                        'Call SendData(ToIndex, UserIndex, 0, "||Ves a " & UserList(TempCharIndex).Name & Stat)
                        Stat = "||Ves a " & UserList(TempCharIndex).Name & Stat
                    End If
                    If UserList(TempCharIndex).flags.Privilegios > 0 Then
                        Stat = Stat & " <GAME MASTER> ~0~185~0~1~0"
                    ElseIf Criminal(TempCharIndex) Then
                        Stat = Stat & " <CRIMINAL> ~255~0~0~1~0"
                    Else
                        Stat = Stat & " <CIUDADANO> ~0~0~200~1~0"
                    End If
                    Call SendData(ToIndex, UserIndex, 0, Stat)
                    FoundSomething = 1
                    UserList(UserIndex).flags.TargetUser = TempCharIndex
                    UserList(UserIndex).flags.TargetNpc = 0
                    UserList(UserIndex).flags.TargetNpcTipo = 0
               End If
            End If
            If FoundChar = 2 Then '¿Encontro un Npc?
                    If Len(Npclist(TempCharIndex).Desc) > 1 Then
                        Call SendData(ToIndex, UserIndex, 0, "||" & vbWhite & "°" & Npclist(TempCharIndex).Desc & "°" & Npclist(TempCharIndex).Char.CharIndex & FONTTYPE_INFO)
                    Else
                        If Npclist(TempCharIndex).MaestroUser > 0 Then
                            Call SendData(ToIndex, UserIndex, 0, "|| " & Npclist(TempCharIndex).Name & " es mascota de " & UserList(Npclist(TempCharIndex).MaestroUser).Name & FONTTYPE_INFO)
                        Else
                            Call SendData(ToIndex, UserIndex, 0, "|| " & Npclist(TempCharIndex).Name & "." & FONTTYPE_INFO)
                        End If
                    End If
                    FoundSomething = 1
                    UserList(UserIndex).flags.TargetNpcTipo = Npclist(TempCharIndex).NPCtype
                    UserList(UserIndex).flags.TargetNpc = TempCharIndex
                    UserList(UserIndex).flags.TargetUser = 0
                    UserList(UserIndex).flags.TargetObj = 0
            End If
            If FoundChar = 0 Then
                UserList(UserIndex).flags.TargetNpc = 0
                UserList(UserIndex).flags.TargetNpcTipo = 0
                UserList(UserIndex).flags.TargetUser = 0
            End If
            '*** NO ENCOTRO NADA ***
            If FoundSomething = 0 Then
                UserList(UserIndex).flags.TargetNpc = 0
                UserList(UserIndex).flags.TargetNpcTipo = 0
                UserList(UserIndex).flags.TargetUser = 0
                UserList(UserIndex).flags.TargetObj = 0
                UserList(UserIndex).flags.TargetObjMap = 0
                UserList(UserIndex).flags.TargetObjX = 0
                UserList(UserIndex).flags.TargetObjY = 0
                Call SendData(ToIndex, UserIndex, 0, "||No ves nada interesante." & FONTTYPE_INFO)
            End If
        Else
            If FoundSomething = 0 Then
                UserList(UserIndex).flags.TargetNpc = 0
                UserList(UserIndex).flags.TargetNpcTipo = 0
                UserList(UserIndex).flags.TargetUser = 0
                UserList(UserIndex).flags.TargetObj = 0
                UserList(UserIndex).flags.TargetObjMap = 0
                UserList(UserIndex).flags.TargetObjX = 0
                UserList(UserIndex).flags.TargetObjY = 0
                Call SendData(ToIndex, UserIndex, 0, "||No ves nada interesante." & FONTTYPE_INFO)
            End If
        End If
    End Sub
 */
    public void consultar(Client cliente, short x, short y) {
        // Sub LookatTile(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer)
    	
    	if (!cliente.getPos().inRangoVision(x, y)) {
            return;
        }
        
        boolean hayAlgo = false;
        // Ver si hay un objeto en los alrededores...
        MapObject obj = buscarObjeto(x, y);
        if (obj != null) {
            cliente.enviarMensaje(obj.getInfo().Nombre + " - " + obj.obj_cant, FontType.INFO);
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
            	// Info para DEBUG
                cliente.enviarMensaje(npc.m_name + " id=" + npc.getId() + 
                " nro=" + npc.m_numero + " type=" + npc.getNPCtype() + 
                " mov=" + npc.m_movement, FontType.DEBUG);
                cliente.enviarMensaje("AI NPC: " + npc.m_name + " id=" + npc.getId() + 
                " nro=" + npc.m_numero + " type=" + npc.getNPCtype() + 
                " mov=" + npc.m_movement + " atby=" + npc.m_attackedBy +
				" oldmov=" + npc.m_oldMovement + " hostil=" + npc.esHostil(), FontType.DEBUG);
            }
            if (npc.m_desc.length() > 0) {
                cliente.enviarHabla(COLOR_BLANCO, npc.m_desc, npc.getId());
            } 
            String msg = "";
            if (npc.getAmo() != null) {
                msg = npc.m_name + " es mascota de " + npc.getAmo().getNick();
            } else {
                msg = npc.m_name;
            }
            msg = msg + " " + npc.estadoVida(cliente);
            cliente.enviarMensaje(msg, FontType.INFO);
            cliente.getFlags().TargetNpc = npc.getId();
            cliente.getFlags().TargetMap = this.nroMapa;
            cliente.getFlags().TargetX = x;
            cliente.getFlags().TargetY = y;
            cliente.getFlags().TargetUser = 0;
            cliente.getFlags().TargetObj = 0;
        }
        
        
        Client cli;
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
            //cliente.enviarMensaje("No ves nada interesante.", FontType.INFO);
        }
        // FIXME: REVISAR SI ESTO VA...
        cliente.getFlags().TargetX = x;
        cliente.getFlags().TargetY = y;
       
        
    }
    
    public WorldPos getTeleport(short x, short y) {
        return this.m_cells[x-1][y-1].getTeleport();
    }
    
    public void accionParaRamita(short x, short y, Client cliente) {
        if (Util.distance(cliente.getPos().x, cliente.getPos().y, x, y) > 2) {
            cliente.enviarMensaje("Estás demasiado lejos.", FontType.INFO);
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
            agregarObjeto(FOGATA, (short)1, x, y);
            cliente.enviarMensaje("Has prendido la fogata.", FontType.INFO);
         //   enviarAlArea(x, y, MSG_FO);
        } else {
            cliente.enviarMensaje("No has podido hacer fuego.", FontType.INFO);
        }
        // Si no tiene hambre o sed quizas suba el skill supervivencia
        if (!cliente.getFlags().Hambre && !cliente.getFlags().Sed) {
			cliente.subirSkill(Skill.SKILL_Supervivencia);
		}
    }
    
    public void accionParaForo(short x, short y, Client cliente) {
        if (Util.distance(cliente.getPos().x, cliente.getPos().y, x, y) > 2) {
            cliente.enviarMensaje("Estás demasiado lejos.", FontType.INFO);
            return;
        }
        // ¿Hay mensajes?
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().ObjType != OBJTYPE_FOROS) {
            return;
        }
        String foroId = obj.getInfo().ForoID;
        this.server.enviarMensajesForo(foroId, cliente);
    }
    
    public void accionParaPuerta(short x, short y, Client cliente) {
        if (Util.distance(cliente.getPos().x, cliente.getPos().y, x, y) > 2) {
            cliente.enviarMensaje("Estas demasiado lejos.", FontType.INFO);
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
            cliente.enviarMensaje("La puerta esta cerrada con llave.", FontType.INFO);
        }
    }
    
    public void accionParaCartel(short x, short y, Client cliente) {
        MapObject obj = getObjeto(x, y);
        if (obj == null || obj.getInfo().ObjType != OBJTYPE_CARTELES) {
			return;
		}
        if (obj.getInfo().Texto.length() > 0) {
           // cliente.enviar(MSG_MCAR, obj.getInfo().Texto, obj.getInfo().GrhSecundario);
        }
    }
    
    public WorldPos tirarItemAlPiso(short x, short y, InventoryObject obj) {
        WorldPos nuevaPos = tilelibre(x, y);
        //Log.debug("tirarItemAlPiso: x=" + nuevaPos.x + " y=" + nuevaPos.y);
        if (nuevaPos != null) {
            if (agregarObjeto(obj.objid, obj.cant, nuevaPos.x, nuevaPos.y)) {
                return nuevaPos;
            }
        }
        return null;
    }
    
    public boolean esPosLibreObjeto(short x, short y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() &&
        !hayAgua(x,y) &&
        !hayObjeto(x, y) && 
		!hayTeleport(x, y);
    }
    
    /** Busca una posicion libre para depositar un objeto
     * y que sea lo más cercana a la posición original */
    public WorldPos tilelibre(short orig_x, short orig_y) {
        if (esPosLibreObjeto(orig_x, orig_y)) {
			return new WorldPos(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 15; radio++) {
            short x1 = (short) (orig_x - radio);
            short x2 = (short) (orig_x + radio);
            short y1 = (short) (orig_y - radio);
            short y2 = (short) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (short x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibreObjeto(x, y1)) {
					return new WorldPos(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibreObjeto(x, y2)) {
					return new WorldPos(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (short y = (short) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibreObjeto(x1, y)) {
					return new WorldPos(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibreObjeto(x2, y)) {
					return new WorldPos(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    public boolean isLegalPos(WorldPos pos, boolean puedeAgua) {
        // Function LegalPos(ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer, Optional ByVal PuedeAgua = False) As Boolean
        // ¿Es un mapa valido?
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
    
    public boolean isLegalPosNPC(WorldPos pos, boolean puedeAgua) {
        // Function LegalPosNPC(ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer, ByVal AguaValida As Byte) As Boolean
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
    
    public boolean existIndex(WorldPos pos) {
        return this.m_cells[pos.x][pos.y].getClienteId() == 0;
    }
    
    private boolean esPosLibrePj(short x, short y, boolean navegando, boolean esAdmin) {
    	if (esAdmin) {
			return esPosLibreAdmin(x, y); // Los Admins no respetan las leyes de la física :P
		} else if (navegando) {
			return esPosLibreConAgua(x, y) && !hayTeleport(x, y) && !estaBloqueado(x, y);
		} else {
			return esPosLibreSinAgua(x, y) && !hayTeleport(x, y) && !estaBloqueado(x, y);
		}
    }
    
    /** Busca una posicion válida para un PJ,
     * y que sea lo más cercana a la posición original */
    public WorldPos closestLegalPosPj(short orig_x, short orig_y, boolean navegando, boolean esAdmin) {
        if (esPosLibrePj(orig_x, orig_y, navegando, esAdmin)) {
			return new WorldPos(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            short x1 = (short) (orig_x - radio);
            short x2 = (short) (orig_x + radio);
            short y1 = (short) (orig_y - radio);
            short y2 = (short) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (short x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibrePj(x, y1, navegando, esAdmin)) {
					return new WorldPos(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibrePj(x, y2, navegando, esAdmin)) {
					return new WorldPos(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (short y = (short) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibrePj(x1, y, navegando, esAdmin)) {
					return new WorldPos(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibrePj(x2, y, navegando, esAdmin)) {
					return new WorldPos(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean esPosLibreNpc(short x, short y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
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
    
    /** Busca una posicion válida para un Npc,
     * y que sea lo más cercana a la posición original */
    public WorldPos closestLegalPosNpc(short orig_x, short orig_y, boolean esAguaValida, boolean esTierraInvalida, boolean bajoTecho) {
        if (esPosLibreNpc(orig_x, orig_y, esAguaValida, esTierraInvalida, bajoTecho)) {
			return new WorldPos(this.nroMapa, orig_x, orig_y);
		}
        for (int radio = 1; radio < 13; radio++) {
            short x1 = (short) (orig_x - radio);
            short x2 = (short) (orig_x + radio);
            short y1 = (short) (orig_y - radio);
            short y2 = (short) (orig_y + radio);
            // Recorrer los lados superior e inferior del borde.
            for (short x = x1; x <= x2; x++) {
                // lado superior
                if (esPosLibreNpc(x, y1, esAguaValida, esTierraInvalida, bajoTecho)) {
					return new WorldPos(this.nroMapa, x, y1);
				}
                // lado inferior
                if (esPosLibreNpc(x, y2, esAguaValida, esTierraInvalida, bajoTecho)) {
					return new WorldPos(this.nroMapa, x, y2);
				}
            }
            // Recorrer los lados izquierdo y derecho del borde.
            for (short y = (short) (y1+1); y < y2; y++) {
                // lado izquierdo
                if (esPosLibreNpc(x1, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return new WorldPos(this.nroMapa, x1, y);
				}
                // lado derecho
                if (esPosLibreNpc(x2, y, esAguaValida, esTierraInvalida, bajoTecho)) {
					return new WorldPos(this.nroMapa, x2, y);
				}
            }
        }
        return null;
    }
    
    private boolean esPosLibreConAgua(short x, short y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() && 
        this.m_cells[x-1][y-1].getClienteId() == 0 &&
        this.m_cells[x-1][y-1].getNpc() == null && 
        hayAgua(x,y);
    }
    
    private boolean esPosLibreSinAgua(short x, short y) {
        if (x < 1 || x > 100 || y < 1 || y > 100) {
			return false;
		}
        return !this.m_cells[x-1][y-1].estaBloqueado() &&
        this.m_cells[x-1][y-1].getClienteId() == 0 &&
        this.m_cells[x-1][y-1].getNpc() == null &&
        !hayAgua(x,y);
    }
    
    private boolean esPosLibreAdmin(short x, short y) {
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
            if (npc.estaActivo() && npc.esHostil() && npc.m_estads.Alineacion == 2) {
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
            ini.setValue(section, "StartPos", this.m_startPos.mapa + "-" + this.m_startPos.x + "-" + this.m_startPos.y);
            ini.setValue(section, "Terreno", TERRENOS[this.m_terreno]);
            ini.setValue(section, "Zona", ZONAS[this.m_zona]);
            ini.setValue(section, "Restringir", (this.m_restringir ? "Si" : "No"));
            ini.setValue(section, "BackUp", this.m_backup);
            ini.setValue(section, "PK", this.m_pk);
            ini.store(datFileName);
        } catch (Exception e) {
            Log.serverLogger().log(Level.SEVERE, "ERROR GUARDANDO MAPA " + this.nroMapa, e);
        }
    }
    
    private void saveMapFile(String filename) {
        ////////// ARCHIVO .MAP
        // guardar cabecera del archivo .map
        Log.serverLogger().info("Guardando mapa: " + filename);
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
            Log.serverLogger().log(Level.SEVERE, "ERROR EN saveMapFile " + this.nroMapa, e);
        }
    }
    
    private void saveInfFile(String filename) {
        //////// ARCHIVO .INF
        Log.serverLogger().info("Guardando mapa: " + filename);
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
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().mapa));
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().x));
                            f.writeShort(Util.leShort(this.m_cells[x][y].getTeleport().y));
                        } else {
                            f.writeShort(0);
                            f.writeShort(0);
                            f.writeShort(0);
                        }
                        // Indice del Npc que esta en este bloque.
                        // Es cero (0) si no hay Npc.
                        f.writeShort((this.m_cells[x][y].getNpc() != null) ? Util.leShort((short)this.m_cells[x][y].getNpc().m_numero) : 0);
                        // Indice del Objeto que esta en este bloque.
                        // Es cero (0) si no hay objeto.
                        if (this.m_cells[x][y].hayObjeto()) {
                            f.writeShort(Util.leShort(getObjeto((short)(x+1), (short)(y+1)).getInfo().ObjIndex));
                            f.writeShort(Util.leShort((short)getObjeto((short)(x+1), (short)(y+1)).obj_cant));
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
            Log.serverLogger().log(Level.SEVERE, "ERROR EN saveInfFile " + this.nroMapa, e);
        }
    }
    
    public void objectMassDestroy(short pos_x, short pos_y) {
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
                if (hayObjeto(x, y) && getObjeto(x, y).getInfo().itemNoEsDeMapa()) {
                    quitarObjeto(x, y);
                }
            }
        }
    }    
    
    //public void construirAreaChar(Client cliente, Client other) {
       // cliente.enviar(MSG_CC, other.ccParams());
    	//cliente.enviar(serverPacketID.CC, other.ccParams());
        
   //}
    
    public void construirAreaObj(Client cliente, short x, short y) {
    	MapObject obj = getObjeto(x,y);
    	if (obj != null) cliente.enviar(MSG_HO, obj.getInfo().GrhIndex, x, y);
    }
    
    public void construirAreaNpc(Client cliente, Npc npc) {
    //	cliente.enviar(MSG_CC, npc.ccParams());
    	cliente.enviar(serverPacketID.MSG_CCNPC, npc.ccParams());
    }
    
    //when user enter in the game, or change map
   // public void areasLogged(Client client) {
    //	int minY = client.getPos().y - areasData.getTileY();
    	//int maxY = client.getPos().y + areasData.getTileY();
    	//int minX = client.getPos().x - areasData.getTileX();
    	//int maxX = client.getPos().x + areasData.getTileX();
    	
    	//if (maxY > YMaxMapSize - 1) maxY = YMaxMapSize - 1;
    	//if (minY < YMinMapSize) minY = YMinMapSize;

    	//if (maxX > XMaxMapSize - 1) maxX = XMaxMapSize - 1;
    	//if (minX < XMinMapSize) minX = XMinMapSize;
    	
    	//if (client.getArea() != 0) client.enviar(serverPacketID.areasChange);
    	
    	//for (short x = (short) minX; x <= maxX; ++x) {
			//for (short y = (short) minY; y <= maxY; ++y) {
				
				//if (this.m_cells[x-1][y-1].getClienteId() > 0) {
				//	Client user = server.getCliente(this.m_cells[x-1][y-1].getClienteId());
				//	user.enviar(serverPacketID.CC, client.ccParams());
				//    client.enviar(serverPacketID.CC, user.ccParams());
				//}
				
				//if (hayNpc(x,y)) client.enviar(serverPacketID.MSG_CCNPC, getNpc(x,y).ccParams());
				
				//if (hayObjeto(x,y)) client.enviar(serverPacketID.MSG_HO, getObjeto(x,y).getInfo().GrhIndex, x, y);
				
				
			//}
		//}
    //}
    
}

