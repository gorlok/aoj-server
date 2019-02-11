/**
 * Guild.java
 *
 * Created on 11 de octubre de 2003, 20:02
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
package org.ArgentumOnline.server.guilds;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;

import org.ArgentumOnline.server.protocol.serverPacketID;

/**
 * @author Pablo F. Lillia
 */
public class Guild {

    public String guildName = "";
    public String URL = "";
    public String founder = "";
    public String fundationDate = "";
    public String description  = "";
    private String codex[] = new String[8];
    public String leader = "";
    public double reputation = 0;
    public double gold = 0;
    public double guildExperience = 0;
    public long electionPeriod = 45;
    public long daysSinceLastElection = 0;
    public String guildNews = "";
    public Set<String> alliedGuilds = new HashSet<String>();
    public Set<String> enemyGuilds  = new HashSet<String>();
    public Set<String> members      = new HashSet<String>();
    public Set<GuildRequest> joinRequest  = new HashSet<GuildRequest>();
    public Set<GuildRequest> peacePropositions = new HashSet<GuildRequest>();
    public boolean elections = false;
    private BallotBox ballotBox = new BallotBox();
    
    private static java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");

    private AojServer server;
    
    /** 
     * Guild of users / Clan de usuarios.
     * @author Gorlok 
     */
    public Guild(AojServer server) {
    	this.server = server;
    }
    
    /** Creates a new instance of Guild */
    public Guild(String guildInfo, String founderName, long rep) 
    throws InvalidGuildNameException {
        // initialize
        parseGuildInfo(guildInfo, founderName, rep);
    }

    public String getCodex(int number) {
        return this.codex[number];
    }
    
    public void resetCodex() {
    	for (int i = 0; i < this.codex.length; i++) {
    		this.codex[i] = "";
    	}
    }
    
    public void setCodex(int slot, String msg) {
    	if (slot < 1 || slot > this.codex.length) {
			return;
		}
    	this.codex[slot-1] = msg;
    }
    
    public void enviarMensaje(String mensaje, FontType font) {
    	// ToGuildMembers
        Client cliente;
        for (String member: this.members) {
            cliente = this.server.getUsuario(member);
            cliente.enviarMensaje(mensaje, font);            
        }
    }
    
    public void enviarSonido(int sonido) {
    	// ToGuildMembers
        Client cliente;
        for (String member: this.members) {
            cliente = this.server.getUsuario(member);
           // cliente.enviar(MSG_TW, sonido);            
        }
    }
    
    public int codexLength() {
    	int cant = 0;
        for (String element : this.codex) {
        	cant++;
            if (element.equals("")) {
				break;
			}
        }
        return cant;
    }
    
    public long daysToNextElection() {
        return this.electionPeriod - this.daysSinceLastElection;
    }

    private static boolean isValidGuildName(String s) {
        char c;
        s = s.toLowerCase();
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if ((c < 97 || c > 122) && (c != 255) && (c != 32)) {
                return false;
            }
        }
        return true;
    }

    private void parseGuildInfo(String guildInfo, String founderName, long rep) 
    throws InvalidGuildNameException {
        this.founder = founderName;
        this.leader = founderName;
        this.fundationDate = df.format(new java.util.Date());
        StringTokenizer st = new StringTokenizer(guildInfo, "¬");
        this.description = st.nextToken();
        this.guildName = st.nextToken();
        if (!isValidGuildName(this.guildName)) {
            throw new InvalidGuildNameException(this.guildName);
        }
        this.URL = st.nextToken();
        int mandamientos = Integer.parseInt(st.nextToken());
        for (int i = 0; i < mandamientos; i++) {
            this.codex[i] = st.nextToken();
        }
        this.reputation = rep;
        this.gold = 0;
        this.guildExperience = 0;
        this.daysSinceLastElection = 0;
        this.guildNews = "Clan iniciado.";
    }

    public void initializeGuildFromDisk(IniFile ini, int nro) 
    throws java.io.IOException {
    	String seccion = "Guild" + nro;
        this.guildName = ini.getString(seccion, "GuildName");
        this.founder = ini.getString(seccion, "Founder");
        this.fundationDate = ini.getString(seccion, "Date");
        this.description = ini.getString(seccion, "Desc");
        this.codex[0] = ini.getString(seccion, "Codex0");
        this.codex[1] = ini.getString(seccion, "Codex1");
        this.codex[2] = ini.getString(seccion, "Codex2");
        this.codex[3] = ini.getString(seccion, "Codex3");
        this.codex[4] = ini.getString(seccion, "Codex4");
        this.codex[5] = ini.getString(seccion, "Codex5");
        this.codex[6] = ini.getString(seccion, "Codex6");
        this.codex[7] = ini.getString(seccion, "Codex7");
        this.leader = ini.getString(seccion, "Leader");
        this.reputation = ini.getDouble(seccion, "Rep");
        this.gold = ini.getDouble(seccion, "Gold");
        this.URL = ini.getString(seccion, "URL");
        this.guildExperience = ini.getDouble(seccion, "Exp");
        this.daysSinceLastElection = ini.getLong(seccion, "DaysLast");
        this.guildNews = ini.getString(seccion, "GuildNews");
        this.loadGuildMembers();
        this.loadSolicitudes();
        this.loadAlliedGuilds();
        this.loadEnemyGuilds();
        this.loadPeacePropositions();
    }
    
    private void loadGuildMembers() 
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Members.mem");
        short cant = ini.getShort("INIT", "NroMembers");
        for (int i = 1; i <= cant; i++) {
        	this.members.add(ini.getString("Members", "Member"+i, ""));
        }
    }

    private void loadSolicitudes()
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Solicitudes.sol");
        short cant = ini.getShort("INIT", "Nro");
        String nombre, desc;
        for (int i = 1; i <= cant; i++) {
        	nombre = ini.getString("Sol"+i, "Name", "");
        	desc = ini.getString("Sol"+i, "Desc", "");
        	this.joinRequest.add(new GuildRequest(nombre, desc));
        }
    }

    private void loadPeacePropositions() 
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Propositions.pro");
        short cant = ini.getShort("INIT", "Nro");
        String nombre, desc;
        for (int i = 1; i <= cant; i++) {
        	nombre = ini.getString("Pro"+i, "Name", "");
        	desc = ini.getString("Pro"+i, "Desc", "");
        	this.peacePropositions.add(new GuildRequest(nombre, desc));
        }
    }

    private void loadAlliedGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Allied.all");
        short cant = ini.getShort("INIT", "NroAllies");
        for (int i = 1; i <= cant; i++) {
        	this.alliedGuilds.add(ini.getString("Allies", "Allie"+i, ""));
        }
    }

    private void loadEnemyGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Enemys.ene");
        short cant = ini.getShort("INIT", "NroEnemys");
        for (int i = 1; i <= cant; i++) {
        	this.alliedGuilds.add(ini.getString("Enemys", "Enemy"+i, ""));
        }
    }

    public void saveGuild(IniFile ini, int nro)
    throws java.io.IOException {
    	String seccion = "GUILD" + nro;
    	ini.setValue(seccion, "GuildName", this.guildName);
    	ini.setValue(seccion, "Founder", this.founder);
    	ini.setValue(seccion, "GuildName", this.guildName);
    	ini.setValue(seccion, "Date", this.fundationDate);
    	ini.setValue(seccion, "Desc", this.description);
    	ini.setValue(seccion, "Codex0", this.codex[0]);
    	ini.setValue(seccion, "Codex1", this.codex[1]);
    	ini.setValue(seccion, "Codex2", this.codex[2]);
    	ini.setValue(seccion, "Codex3", this.codex[3]);
    	ini.setValue(seccion, "Codex4", this.codex[4]);
    	ini.setValue(seccion, "Codex5", this.codex[5]);
    	ini.setValue(seccion, "Codex6", this.codex[6]);
    	ini.setValue(seccion, "Codex7", this.codex[7]);
    	ini.setValue(seccion, "Leader", this.leader);
    	ini.setValue(seccion, "URL", this.URL);
    	ini.setValue(seccion, "GuildExp", this.guildExperience);
    	ini.setValue(seccion, "DaysLast", this.daysSinceLastElection);
    	ini.setValue(seccion, "GuildNews", this.guildNews);
    	ini.setValue(seccion, "Rep", this.reputation);
		saveAlliedGuilds();
		saveEnemyGuilds();
		saveGuildMembers();
		saveSolicitudes();
		savePeacePropositions();
    }
    
    private void saveAlliedGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "NroAllies", this.alliedGuilds.size());
        int i = 1;
        for (String guild: this.alliedGuilds) {
        	ini.setValue("Allies", "Allie" + i++, guild);
        }
        ini.store(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Allied.all");
    }

    private void saveEnemyGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "NroEnemys", this.enemyGuilds.size());
        int i = 1;
        for (String guild: this.enemyGuilds) {
        	ini.setValue("Enemys", "Enemy" + i++, guild);
        }
        ini.store(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Enemys.ene");
    }

    private void saveGuildMembers() 
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "NroMembers", this.members.size());
        int i = 1;
        for (String member: this.members) {
        	ini.setValue("Members", "Member" + i++, member);
        }
        ini.store(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Members.mem");
    }

    private void saveSolicitudes()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "Nro", this.joinRequest.size());
        int i = 1;
        for (GuildRequest solicitud: this.joinRequest) {
        	ini.setValue("Sol"+i, "Name", solicitud.userName);
        	ini.setValue("Sol"+i, "Desc", solicitud.desc);
        }
        ini.store(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Solicitudes.sol");
    }

    private void savePeacePropositions()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "Nro", this.peacePropositions.size());
        int i = 1;
        for (GuildRequest solicitud: this.peacePropositions) {
        	ini.setValue("Pro"+i, "Name", solicitud.userName);
        	ini.setValue("Pro"+i, "Desc", solicitud.desc);
        }
        ini.store(Constants.GUILDDIR + java.io.File.separator + this.guildName + "-Propositions.pro");
    }

    public boolean isEnemy(String guildName) {
        return this.enemyGuilds.contains(guildName.toUpperCase());
    }
    
    public boolean isAllie(String guildName) {
        return this.alliedGuilds.contains(guildName.toUpperCase());
    }

    public boolean solicitudesIncludes(String userName) {
    	for (GuildRequest solicitud: this.joinRequest) {
    		if (solicitud.getUserName().equalsIgnoreCase(userName)) {
    			return true;
    		}
    	}
    	return false;
    }

    /* TODO
    private void removeSolicitud(String userName) {
    	for (Iterator it = solicitudes.iterator(); it.hasNext(); ) {
    		Solicitud solicitud = (Solicitud) it.next();
    		if (solicitud.getUserName().equalsIgnoreCase(userName)) {
    			solicitudes.remove(solicitud);
    			return;
    		}
    	}
    }
    */

    public void removePeaceProposition(String userName) {
        for (GuildRequest solicitud: this.peacePropositions) {
            if (solicitud.userName.equalsIgnoreCase(userName)) {
                this.peacePropositions.remove(solicitud);
                return;
            }
        }
    }

    public void removeAllie(String name) {
    	for (String allie: this.alliedGuilds) {
    		if (allie.equalsIgnoreCase(name)) {
    			this.alliedGuilds.remove(allie);
    			return;
    		}
    	}
    }

    public void removeEnemy(String name) {
        for (String enemy: this.enemyGuilds) {
            if (enemy.equalsIgnoreCase(name)) {
                this.enemyGuilds.remove(enemy);
                return;
            }
        }
    }

    public void removeMember(String name) {
        Client cliente;
        for (String member: this.members) {
            cliente = this.server.getUsuario(member);
            if (cliente.getNick().equalsIgnoreCase(name)) {
            	this.members.remove(cliente);          
            }
        }
    }

    public GuildRequest getPeaceRequest(String userName) {
        for (GuildRequest solicitud: this.peacePropositions) {
            if (solicitud.userName.equalsIgnoreCase(userName)) {
                return solicitud;
            }
        }
        return null;
    }

    public GuildRequest getSolicitudIngreso(String userName) {
    	for (GuildRequest solicitud: this.joinRequest) {
    		if (solicitud.userName.equalsIgnoreCase(userName)) {
    			return solicitud;
    		}
    	}
    	return null;
	}
 
    public boolean includesPeaceOffer(String userName) {
    	for (GuildRequest solicitud: this.peacePropositions) {
            if (solicitud.getUserName().equalsIgnoreCase(userName)) {
                return true;
            }
        }
        return false;
    }

    public void resetVotes() {
        this.ballotBox.clear();
    }

    public boolean isMember(String name) {
        for (String member: this.members) {
            if (member.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public String nuevoLider() {
        if (this.members.size() < 1) {
            return "CLANCLAUSURADO";
        }
        return this.ballotBox.getWinner();
    }

    public void computeVote(Client cliente, String vote) {
        if (!this.elections) {
            cliente.enviarMensaje("Aun no es período de elecciones.", FontType.GUILD);
            return;
         }
         if (cliente.guildInfo().yaVoto()) {
            cliente.enviarMensaje("Ya has votado!!! Solo se permite un voto por miembro.", FontType.GUILD);
            return;
         }
         if (!this.isMember(vote)) {
            cliente.enviarMensaje("No hay ningún miembro con ese nombre.", FontType.GUILD);
            return;
         }
         this.ballotBox.addVote(vote);
         cliente.guildInfo().voto();
         cliente.enviarMensaje("Tu voto ha sido contabilizado.", FontType.GUILD);
    }
}
