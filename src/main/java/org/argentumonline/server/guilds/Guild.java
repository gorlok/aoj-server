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
package org.argentumonline.server.guilds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;

/**
 * @author gorlok
 */
public class Guild {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    
    private static final byte CANTIDAD_MAXIMA_CODEX = 8;

    private static final byte MAX_ASPIRANTES = 10;

	// puntos maximos de antifaccion que un clan tolera antes de ser cambiada su alineacion
    private static final byte MAX_ANTIFACCION = 5;
    
	public String guildName = "";
    public String URL = "";
    public String founder = "";
    public String fundationDate = "";
    public String description  = "";
    private String codex[] = new String[CANTIDAD_MAXIMA_CODEX];
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
    
    boolean eleccionesAbiertas = false;
    int puntosAntifaccion = 0;
    Date eleccionesFinalizanDia;
    
    AlineacionGuild alineacion;
    
	final static byte RELACIONES_GUILD_GUERRA = -1;
	final static byte RELACIONES_GUILD_PAZ = 0;
	final static byte RELACIONES_GUILD_ALIADOS = 1;
    
    private GameServer server;
    
    /** 
     * Guild of users / Clan de usuarios.
     * @author Gorlok 
     */
    public Guild(GameServer server) {
    	this.server = server;
    }
    
    /** Creates a new instance of Guild */
    public Guild(String guildInfo, String founderName, long rep) 
    throws InvalidGuildNameException {
        // initialize
        parseGuildInfo(guildInfo, founderName, rep);
    }
    
    public String antifactionPoints() {
    	return puntosAntifaccion + "/" + MAX_ANTIFACCION;    	
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
    
    public void messageToGuildMembers(String message, FontType font) {
    	// ToGuildMembers
        for (String member: this.members) {
            this.server.userByName(member)
            	.sendMessage(message, font);            
        }
    }
    
    public void sendPlayWave(byte sound) {
    	// ToGuildMembers
    	this.members.stream()
    		.map(GameServer.instance()::userByName)
    		.forEach( u -> u.sendPacket(new PlayWaveResponse(sound, u.pos().x, u.pos().y)));
    }
    
    public int codexLength() {
    	return this.codex.length;
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
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        this.fundationDate = df.format(new Date());
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
        this.alineacion = AlineacionGuild.value(ini.getShort(seccion, "Alineacion"));
        
        this.eleccionesAbiertas = ini.getInt(seccion, "EleccionesAbiertas") == 1;
        if (this.eleccionesAbiertas) {
        	SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        	try {
				this.eleccionesFinalizanDia = df.parse(ini.getString(seccion, "EleccionesFinalizan"));
			} catch (ParseException pe) {
				this.eleccionesFinalizanDia = new Date(); //now
			} 
        }
        this.puntosAntifaccion = ini.getInt(seccion, "Antifaccion");
        
        this.loadGuildMembers();
        this.loadSolicitudes();
        this.loadAlliedGuilds();
        this.loadEnemyGuilds();
        this.loadPeacePropositions();
    }
    
    private void loadGuildMembers() 
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Members.mem");
        short cant = ini.getShort("INIT", "NroMembers");
        for (int i = 1; i <= cant; i++) {
        	this.members.add(ini.getString("Members", "Member"+i, ""));
        }
    }

    private void loadSolicitudes()
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Solicitudes.sol");
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
        IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Propositions.pro");
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
        IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Allied.all");
        short cant = ini.getShort("INIT", "NroAllies");
        for (int i = 1; i <= cant; i++) {
        	this.alliedGuilds.add(ini.getString("Allies", "Allie"+i, ""));
        }
    }

    private void loadEnemyGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Enemys.ene");
        short cant = ini.getShort("INIT", "NroEnemys");
        for (int i = 1; i <= cant; i++) {
        	this.alliedGuilds.add(ini.getString("Enemys", "Enemy"+i, ""));
        }
    }

    public void saveGuild(IniFile ini, int nro)
    throws java.io.IOException {
    	SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
    	
    	String seccion = "GUILD" + nro;
    	ini.setValue(seccion, "GuildName", this.guildName);
    	ini.setValue(seccion, "Founder",   this.founder);
    	ini.setValue(seccion, "GuildName", this.guildName);
    	ini.setValue(seccion, "Date",   this.fundationDate);
    	ini.setValue(seccion, "Desc",   this.description);
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
    	ini.setValue(seccion, "EleccionesAbiertas", this.eleccionesAbiertas);
    	ini.setValue(seccion, "EleccionesFinalizan", eleccionesAbiertas ? df.format(this.eleccionesFinalizanDia) : "");
    	ini.setValue(seccion, "Alineacion", this.alineacion.value());
    	ini.setValue(seccion, "Antifaccion", this.puntosAntifaccion);
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
        ini.store(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Allied.all");
    }

    private void saveEnemyGuilds()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "NroEnemys", this.enemyGuilds.size());
        int i = 1;
        for (String guild: this.enemyGuilds) {
        	ini.setValue("Enemys", "Enemy" + i++, guild);
        }
        ini.store(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Enemys.ene");
    }

    private void saveGuildMembers() 
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "NroMembers", this.members.size());
        int i = 1;
        for (String member: this.members) {
        	ini.setValue("Members", "Member" + i++, member);
        }
        ini.store(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Members.mem");
    }

    private void saveSolicitudes()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "Nro", this.joinRequest.size());
        int i = 1;
        for (GuildRequest solicitud: this.joinRequest) {
        	ini.setValue("Sol"+i, "Name", solicitud.getUserName());
        	ini.setValue("Sol"+i, "Desc", solicitud.getDesc());
        }
        ini.store(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Solicitudes.sol");
    }

    private void savePeacePropositions()
    throws java.io.IOException {
        IniFile ini = new IniFile();
        ini.setValue("INIT", "Nro", this.peacePropositions.size());
        int i = 1;
        for (GuildRequest solicitud: this.peacePropositions) {
        	ini.setValue("Pro"+i, "Name", solicitud.getUserName());
        	ini.setValue("Pro"+i, "Desc", solicitud.getDesc());
        }
        ini.store(Constants.GUILD_DIR + java.io.File.separator + this.guildName + "-Propositions.pro");
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
            if (solicitud.isFromUserName(userName)) {
                this.peacePropositions.remove(solicitud);
                return;
            }
        }
    }

    public void removeAllie(String userName) {
    	for (String allie: this.alliedGuilds) {
    		if (allie.equalsIgnoreCase(userName)) {
    			this.alliedGuilds.remove(allie);
    			return;
    		}
    	}
    }

    public void removeEnemy(String userName) {
        for (String enemy: this.enemyGuilds) {
            if (enemy.equalsIgnoreCase(userName)) {
                this.enemyGuilds.remove(enemy);
                return;
            }
        }
    }

    public void removeMember(String userName) {
    	Optional<User> user = this.members.stream()
			.map(GameServer.instance()::userByName)
			.filter(u -> u.getUserName().equalsIgnoreCase(userName))
			.findFirst();
    	if (user.isPresent()) {
    		this.members.remove(user.get().getUserName());          
    	}
    }
    
    // FIXME ?
    public void addMember(String name) {
    	this.members.add(name);
    }

    public GuildRequest getPeaceRequest(String userName) {
        for (GuildRequest solicitud: this.peacePropositions) {
            if (solicitud.isFromUserName(userName)) {
                return solicitud;
            }
        }
        return null;
    }

    public GuildRequest getSolicitudIngreso(String userName) {
    	for (GuildRequest solicitud: this.joinRequest) {
    		if (solicitud.isFromUserName(userName)) {
    			return solicitud;
    		}
    	}
    	return null;
	}
 
    public boolean includesPeaceOffer(String userName) {
    	for (GuildRequest solicitud: this.peacePropositions) {
            if (solicitud.isFromUserName(userName)) {
                return true;
            }
        }
        return false;
    }

    public void resetVotes() {
        this.ballotBox.clear();
    }

    public boolean isMember(String userName) {
        for (String member: this.members) {
            if (member.equalsIgnoreCase(userName)) {
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

    public void computeVote(User user, String member) {
        if (!this.elections) {
            user.sendMessage("Aun no es período de elecciones.", FontType.FONTTYPE_GUILD);
            return;
         }
         if (user.guildInfo().yaVoto()) {
            user.sendMessage("Ya has votado!!! Solo se permite un voto por miembro.", FontType.FONTTYPE_GUILD);
            return;
         }
         if (!this.isMember(member)) {
            user.sendMessage("No hay ningún miembro con ese nombre.", FontType.FONTTYPE_GUILD);
            return;
         }
         this.ballotBox.addVote(member);
         user.guildInfo().voto();
         user.sendMessage("Tu voto ha sido contabilizado.", FontType.FONTTYPE_GUILD);
    }
}
