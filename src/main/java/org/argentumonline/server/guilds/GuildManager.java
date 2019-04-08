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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.Skill;
import org.argentumonline.server.protocol.CharacterInfoResponse;
import org.argentumonline.server.protocol.GuildChatResponse;
import org.argentumonline.server.protocol.GuildDetailsResponse;
import org.argentumonline.server.protocol.GuildLeaderInfoResponse;
import org.argentumonline.server.protocol.GuildListResponse;
import org.argentumonline.server.protocol.GuildNewsResponse;
import org.argentumonline.server.protocol.OfferDetailsResponse;
import org.argentumonline.server.protocol.PeaceProposalsListResponse;
import org.argentumonline.server.protocol.PlayWaveResponse;
import org.argentumonline.server.protocol.ShowGuildFundationFormResponse;
import org.argentumonline.server.protocol.ShowUserRequestResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserStorage;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;

/**
 * @author gorlok
 */
public class GuildManager {
	private static Logger log = LogManager.getLogger();

	private GameServer server;
	
	private Map<String, Guild> guilds = new HashMap<String, Guild>();
	
	public GuildManager(GameServer server) {
		this.server = server;
	}

    private Collection<Guild> getGuilds() {
    	return this.guilds.values();    	
    }
    
    private void addGuild(Guild guild) {
        this.guilds.put(guild.guildName.toUpperCase(), guild);
    }
    
    public Guild getGuild(String guildName) {
        return this.guilds.get(guildName.toUpperCase());
    }
    
    public int guildsCount() {
        return this.guilds.values().size();
    }

    public void computeVote(User user, String member) {
    	// /VOTO
        Guild guild = getGuild(user.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        guild.computeVote(user, member);
    }

    public void dayElapsed() {
    	for (Guild guild: getGuilds()) {
            if (guild.daysSinceLastElection < guild.electionPeriod) {
            	guild.daysSinceLastElection++;
            } else {
            	if (!guild.elections) {
            		guild.resetVotes();
            		guild.elections = true;
            		guild.members.forEach(s -> sendOpenVotingAnnouncement(this.server.userByName(s)));
            	} else {
                    if (guild.members.size() > 1) {
                    	// compute elections results
                        String newLeaderName = guild.nuevoLider();
                        String oldLeaderName = guild.leader;
                        guild.elections = false;
                        User newLeader = this.server.userByName(newLeaderName);
                        User oldLeader = this.server.userByName(guild.leader);
                        if (!oldLeaderName.equalsIgnoreCase(newLeaderName)) {
                        	if (oldLeader != null) {
                        		oldLeader.guildInfo().m_esGuildLeader = false;
                        	} else {
                        		// CAMBIAR EN .CHR
                        		User.changeGuildLeaderChr(oldLeaderName, false);
                        	}
                            if (newLeader != null) {
                                newLeader.guildInfo().m_esGuildLeader = true;
                                newLeader.guildInfo().incVecesFueGuildLeader();
                            } else {
                        		// CAMBIAR EN .CHR
                        		User.changeGuildLeaderChr(newLeaderName, true);
                            }
                            guild.leader = newLeaderName;
                        }
                        if (newLeader != null) {
	                        guild.messageToGuildMembers("La elecciones han finalizado!!.", FontType.FONTTYPE_GUILD);
	                        guild.messageToGuildMembers("El nuevo lider es " + newLeaderName, FontType.FONTTYPE_GUILD);
                        }
                        if (newLeader != null) {
                            newLeader.sendMessage("¡¡¡Has ganado las elecciones, felicitaciones!!!", FontType.FONTTYPE_GUILD);
                            newLeader.guildInfo().giveGuildPoints(400);
                        } else {
                    		User.changeGuildPtsChr(newLeaderName, 400);                        
                        }
                        guild.daysSinceLastElection = 0;
                    }
            	}
            }
        }
    }

    public void acceptPeaceOffer(User user, String guildName) {
    	// ACEPPEAT
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (!guild.isEnemy(user.guildInfo().getGuildName())) {
            user.sendMessage("No estás en guerra con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        guild.removeEnemy(user.guildInfo().getGuildName());
        guild = getGuild(user);
        if (guild == null) {
            return;
        }
        guild.removeEnemy(guildName);
        guild.removePeaceProposition(guildName);
        User userGuild = this.server.userByName(guildName);
        if (userGuild != null) {
            userGuild.sendMessage("El clan firmó la paz con " + user.getUserName(), FontType.FONTTYPE_GUILD);
        }
        guild.messageToGuildMembers("El clan firmó la paz con " +  guildName, FontType.FONTTYPE_GUILD);
    }
    
    public void sendPeaceRequest(User user, String guildName, String desc) {
    	// PEACEDET
        if (!user.guildInfo().esGuildLeader()) {
			return;
		}
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        GuildRequest solic = guild.getPeaceRequest(desc);
        if (solic == null) {
			return;
		}
        user.sendPacket(new OfferDetailsResponse(solic.desc));
    }

    public void recievePeaceOffer(User user, String guildName, String desc) {
    	// PEACEOFF
        if (!user.guildInfo().esGuildLeader()) {
			return;
		}
        if (user.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (!guild.isEnemy(user.guildInfo().getGuildName())) {
            user.sendMessage("No estás en guerra con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        if (guild.isAllie(user.guildInfo().getGuildName())) {
            user.sendMessage("Ya estás en paz con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        GuildRequest peaceoffer = new GuildRequest(user.guildInfo().getGuildName(), desc);
        if (!guild.includesPeaceOffer(peaceoffer.getUserName())) {
            guild.peacePropositions.add(peaceoffer);
            user.sendMessage("La propuesta de paz ha sido entregada.", FontType.FONTTYPE_GUILD);
        } else {
            user.sendMessage("Ya has enviado una propuesta de paz.", FontType.FONTTYPE_GUILD);
        }
    }
    
    public void sendPeacePropositions(User user) {
    	// ENVPROPP
        if (!user.guildInfo().esGuildLeader()) {
			return;
		}
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        if (guild.peacePropositions.isEmpty()) {
			return;
		}
        
        var sb = new StringBuilder();
        for (GuildRequest solicitud: guild.peacePropositions) {
        	sb.append(solicitud.getUserName())
        		.append(Constants.NULL_CHAR);
        }
        if (sb.length() > 0) {
        	// remove last separator
        	sb.deleteCharAt(sb.length()-1);
        }
        user.sendPacket(new PeaceProposalsListResponse(sb.toString()));
    }

    public void kickMember(User user, String userName) {
    	// ECHARCLA
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        if (guild.founder.equalsIgnoreCase(userName)) {
        	user.sendMessage("No puedes echar al miembro fundador!", FontType.FONTTYPE_GUILD);
        	return;
        }
        if (guild.leader.equalsIgnoreCase(userName)) {
        	user.sendMessage("No puedes echar al lider!", FontType.FONTTYPE_GUILD);
        	return;
        }
        User miembro = this.server.userByName(userName);
        if (miembro == null) {
            user.sendMessage("El usuario no esta ONLINE.", FontType.FONTTYPE_GUILD);
            return;        	
        }
        // El usuario miembro está online.
        miembro.sendMessage("Has sido expulsado del clan.", FontType.FONTTYPE_GUILD);
        
		getGuild(miembro).removeMember(miembro.getUserName());
		miembro.getGuildInfo().salirClan();
        
        guild.messageToGuildMembers(userName + " fue expulsado del clan.", FontType.FONTTYPE_GUILD);
        guild.removeMember(userName);
    }
    
    public void rejectRequest(User user, String userName) {
    	// RECHAZAR
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        User solicitante = this.server.userByName(userName);
        if (solicitante != null) {
        	// Esta online
        	solicitante.sendMessage("Tu solicitud ha sido rechazada.", FontType.FONTTYPE_GUILD);
        	user.guildInfo().incSolicitudesRechazadas();
        }
        guild.joinRequest.remove(solicitud);
    }
    
    public void acceptGuildMember(User user, String userName) {
    	// ACEPTARI
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        User solicitante = this.server.userByName(userName);
        if (solicitante == null) {
            user.sendMessage("Solo podes aceptar solicitudes cuando el solicitante esta ONLINE.", FontType.FONTTYPE_GUILD);
        	return;
        }
        if (solicitante.guildInfo().esGuildLeader()) {
            user.sendMessage("No podés aceptar esa solicitud, el pesonaje es lider de otro clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        // Ingresarlo al clan.
        String guildName = user.guildInfo().getGuildName();
        solicitante.getGuildInfo().ingresarClan(guildName);
        user.sendMessage("Felicitaciones, tu solicitud ha sido aceptada.", FontType.FONTTYPE_GUILD);
        user.sendMessage("Ahora sos un miembro activo del clan " + user.guildInfo().getGuildName(), FontType.FONTTYPE_GUILD);
        user.guildInfo().giveGuildPoints(25);
        guild.addMember(solicitante.getUserName());
        guild.joinRequest.remove(solicitud);
        guild.sendPlayWave(Constants.SOUND_ACEPTADO_CLAN);
        guild.messageToGuildMembers(solicitante.getUserName() + " ha sido aceptado en el clan.", FontType.FONTTYPE_GUILD);
    }

	private Guild getGuild(User user) {
		return this.getGuild(user.guildInfo().getGuildName());
	}
    
    public void showUserRequest(User user, String userName) {
    	// ENVCOMEN
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        user.sendPacket(new ShowUserRequestResponse(solicitud.getDesc()));
    }
	
    public void requestMembership(User applicant, String guildName, String desc) {
    	// SOLICITUD
    	if (applicant.isNewbie()) {
           applicant.sendMessage("Los newbies no pueden conformar clanes.", FontType.FONTTYPE_GUILD);
           return;
    	}
    	GuildRequest solicitud = new GuildRequest(applicant.getUserName(), desc);
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (guild.isMember(applicant.getUserName())) {
        	return;
        }
        if (guild.solicitudesIncludes(applicant.getUserName())) {
            applicant.sendMessage("Tu solicitud ya fue recibida por el lider del clan, ahora debes esperar la respuesta.", FontType.FONTTYPE_GUILD);
        	return;
        }
        // Nos aseguramos que se acumulen mas de 25 solicitudes pendientes.
        if (guild.joinRequest.size() > 25) {
        	applicant.sendMessage("Hay demasiadas solicitudes pendientes de ingreso a este clan. Envia tu solicitud en otro momento.", FontType.FONTTYPE_GUILD);
        }
        applicant.guildInfo().incSolicitudes();
        guild.joinRequest.add(solicitud);
        applicant.sendMessage("La solicitud será entregada al lider del clan, ahora debes esperar su respuesta.", FontType.FONTTYPE_GUILD);
    }

    public void sendCharInfo(User user, String userName) {
    	// 1HRINFO<
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        
        CharacterInfoResponse packet = UserStorage.createCharacterInfoResponse(userName);
        if (packet != null) {
        	user.sendPacket(packet);
		}
    }

    public void updateGuildNews(User user, String news) {
    	// ACTGNEWS
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        guild.guildNews = news;
    }

    public void updateCodexAndDesc(User leader, String data) {
		// Comando DESCOD
        if (!leader.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(leader);
        if (guild == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(data, "¬");
        guild.description = st.nextToken();
        short cantMandamientos = Short.parseShort(st.nextToken());
		guild.resetCodex();
        for (int i = 1; i < cantMandamientos; i++) {
            guild.setCodex(i, st.nextToken());
        }
    }
    
    private void sendGuildLeaderInfo(User user) {
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
		Guild guild = getGuild(user.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        
        String guildsList = guildListAsString();
        String membersList = memberListAsString(guild);
        String news = guildNewsAsString(guild);
        String requestsList = guildRequestAsString(guild);
        
        user.sendPacket(new GuildLeaderInfoResponse(guildsList, membersList, news, requestsList));
    }
    
    private String guildRequestAsString(Guild guild) {
        var requestsList = new StringBuilder();
        for (GuildRequest solicitud: guild.joinRequest) {
        	requestsList.append(solicitud.getUserName());
        	requestsList.append(Constants.NULL_CHAR);
        }
        if (requestsList.length()>0) {
        	requestsList.deleteCharAt(requestsList.length()-1); // remove extra separator
        }
        return requestsList.toString();
    }
    
    private String guildNewsAsString(Guild guild) {
        var news = new StringBuilder()
		        .append(guild.guildNews)
		        .append(Constants.NULL_CHAR);
    	return news.toString();
    }

    private String guildListAsString() {
        var guildsList = new StringBuilder();
        for (Guild g: getGuilds()) {
            guildsList.append(g.guildName);
            guildsList.append(Constants.NULL_CHAR);
        }
        if (guildsList.length()>0) {
        	guildsList.deleteCharAt(guildsList.length()-1); // remove extra separator
        }
        return guildsList.toString();
	}

	private String memberListAsString(Guild guild) {
    	var membersList = new StringBuilder();
        for (String member: guild.members) {
        	membersList.append(member);
        	membersList.append(Constants.NULL_CHAR);        }
        if (membersList.length()>0) {
        	membersList.deleteCharAt(membersList.length()-1); // remove extra separator
        }
        return membersList.toString();
    }

	private String alliesListAsString(Guild guild) {
    	var guilds = new StringBuilder();
        for (String allied: guild.alliedGuilds) {
        	guilds.append(allied);
        	guilds.append(Constants.NULL_CHAR);        }
        if (guilds.length()>0) {
        	guilds.deleteCharAt(guilds.length()-1); // remove extra separator
        }
        return guilds.toString();
    }

	private String enemiesListAsString(Guild guild) {
    	var guilds = new StringBuilder();
        for (String allied: guild.enemyGuilds) {
        	guilds.append(allied);
        	guilds.append(Constants.NULL_CHAR);        }
        if (guilds.length()>0) {
        	guilds.deleteCharAt(guilds.length()-1); // remove extra separator
        }
        return guilds.toString();
    }

	public void setNewURL(User user, String newURL) {
		// NEWWEBSI
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(user);
        if (guild == null) {
            return;
        }
        guild.URL = newURL;
        user.sendMessage("La direccion de la web ha sido actualizada", FontType.FONTTYPE_INFO);
    }

    public void declareAllie(User user, String guildName) {
    	// DECALIAD
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        if (user.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild enemyGuild = this.getGuild(guildName);
        if (enemyGuild == null) {
            return;
        }
        Guild leaderGuild = getGuild(user);
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isAllie(enemyGuild.guildName)) {
            user.sendMessage("Estas en guerra con éste clan, antes debes firmar la paz.", FontType.FONTTYPE_GUILD);
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            user.sendMessage("Ya estas aliado con éste clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        leaderGuild.alliedGuilds.add(enemyGuild.guildName);
        enemyGuild.alliedGuilds.add(leaderGuild.guildName);
        leaderGuild.messageToGuildMembers("Tu clan ha firmado una alianza con " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
        leaderGuild.sendPlayWave(Constants.SOUND_DECLARE_WAR);        
        enemyGuild.messageToGuildMembers(leaderGuild.guildName + " firmó una alianza con tu clan.", FontType.FONTTYPE_GUILD);
        enemyGuild.sendPlayWave(Constants.SOUND_DECLARE_WAR);        
    }
    
    public void declareWar(User user, String guildName) {
    	// DECGUERR
        if (!user.guildInfo().esGuildLeader()) {
            return;
        }
        if (user.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild enemyGuild = this.getGuild(guildName);
        if (enemyGuild == null) {
            return;
        }
        Guild leaderGuild = getGuild(user);
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            user.sendMessage("Tu clan ya esta en guerra con " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
            return;
        }    
        leaderGuild.removeAllie(enemyGuild.guildName);
        enemyGuild.removeAllie(leaderGuild.guildName);
        leaderGuild.enemyGuilds.add(enemyGuild.guildName);
        enemyGuild.enemyGuilds.add(leaderGuild.guildName);
        leaderGuild.messageToGuildMembers("Tu clan le declaró la guerra a " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
        leaderGuild.sendPlayWave(Constants.SOUND_DECLARE_WAR);
        enemyGuild.messageToGuildMembers(leaderGuild.guildName + " le declaradó la guerra a tu clan.", FontType.FONTTYPE_GUILD);
        enemyGuild.sendPlayWave(Constants.SOUND_DECLARE_WAR);        
    }
    
    public void sendGuildNews(User member) {
    	if (!member.getGuildInfo().esMiembroClan()) {
			return;
		}
		
		Guild guild = getGuild(member);
        if (guild == null) {
            return;
        }
        
        String news = guildNewsAsString(guild);
    	String allies = alliesListAsString(guild);
    	String enemies = enemiesListAsString(guild);

    	member.sendPacket(new GuildNewsResponse(news, enemies, allies));
		
		if (guild.elections) {
			sendOpenVotingAnnouncement(member);
		}
    }

    private void sendGuildsList(User user) {
        var sb = new StringBuilder();
        for (Guild guild: getGuilds()) {
            sb.append(guild.guildName);
            sb.append(Constants.NULL_CHAR);
        }
        if (sb.length()>0) {
        	sb.deleteCharAt(sb.length()-1);
        }
        
        user.sendPacket(new GuildListResponse(sb.toString()));
    }

    public void loadGuildsDB() {
        try {
        	this.guilds.clear();
            IniFile ini = new IniFile(Constants.GUILD_DIR + java.io.File.separator + "GuildsInfo.ini");
            short cant = ini.getShort("INIT", "NroGuilds");
            Guild guild;
            for (int i = 1; i <= cant; i++) {
            	guild = new Guild(this.server);
            	guild.initializeGuildFromDisk(ini, i);
            	addGuild(guild);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGuildDetails(User user, String guildName) {
    	// CLANDETAILS
		Guild guild = this.getGuild(guildName);
		if (guild == null) {
			return;
		}
		
		var codex = new StringBuilder();
		for (var i = 0; i<guild.codexLength(); i++) {
			codex.append(guild.getCodex(i))
				.append(Constants.NULL_CHAR);
		}
		if (codex.length()>0) {
			codex.deleteCharAt(codex.length()-1);
		}
		
        user.sendPacket(new GuildDetailsResponse(
					guild.guildName, 
					guild.founder,
					guild.fundationDate,
					guild.leader,
					guild.URL,
					(short) guild.members.size(), 
					(byte) (guild.eleccionesAbiertas ? 1 : 0),
					guild.alineacion.toString(), 
					(short) guild.enemyGuilds.size(), 
					(short) guild.alliedGuilds.size(), 
					guild.antifactionPoints(), 
					codex.toString(),
					guild.description));
    }
    
    private boolean canCreateGuild(User user) {
        if (user.getStats().ELV < 20) {
            user.sendMessage("Para fundar un clan debes de ser nivel 20 o superior", FontType.FONTTYPE_GUILD);
            return false;
        }
        if (user.skills().get(Skill.SKILL_Liderazgo) < 90) {
            user.sendMessage("Para fundar un clan necesitás al menos 90 pts en liderazgo", FontType.FONTTYPE_GUILD);
            return false;
        }
        return true;
    }

    private boolean existeGuild(String name) {
        for (Guild guild: getGuilds()) {
            if (guild.guildName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void createGuild(User user, String guildInfo) {
    	// CIG
        if (!canCreateGuild(user)) {
            return;
        }
        Guild guild;
        try {
            guild = new Guild(guildInfo, user.getUserName(), (long) user.getReputation().getPromedio());
        } catch (InvalidGuildNameException e) {
            user.sendMessage("Los datos del clan son inválidos, asegurate que no contiene caracteres inválidos.", FontType.FONTTYPE_GUILD);
            return;
        }
        if (existeGuild(guild.guildName)) {
            user.sendMessage("Ya existe un clan con ese nombre.", FontType.FONTTYPE_GUILD);
            return;
        }
        guild.members.add(user.getUserName());
        addGuild(guild);
        user.guildInfo().fundarClan(guild.guildName);
        this.server.sendToAll(new PlayWaveResponse(Constants.SOUND_CREACION_CLAN, (byte)50, (byte)50));
        this.server.sendToAll(new GuildChatResponse("¡¡¡" + user.getUserName() + " fundó el clan '" + guild.guildName + "'!!!"));
        
        if (guildsCount() == 1) {
            user.sendMessage("¡¡Felicidades!! Has creado el primer clan de Argentum!!!.", FontType.FONTTYPE_INFO);
        } else {
            user.sendMessage("¡Felicidades! Has creado el clan número " + guildsCount() + " de Argentum!!!.", FontType.FONTTYPE_INFO);
        }
        this.saveGuildsDB();
    }

    public void saveGuildsDB() {
        try {
            IniFile ini = new IniFile();
            ini.setValue("INIT", "NroGuilds", guildsCount());
            int i = 1;
            for (Guild guild: getGuilds()) {
                guild.saveGuild(ini, i++);
            }
            ini.store(Constants.GUILD_DIR + java.io.File.separator + "GuildsInfo.ini");
        } catch (Exception e) {
            log.fatal("ERROR EN guardarMOTD()", e);
        }
    }

	public void guildFundation(User user) {
		// Comando /FUNDARCLAN
		if (user.getGuildInfo().m_fundoClan) {
			user.sendMessage("Ya has fundado un clan, solo se puede fundar uno por personaje.", FontType.FONTTYPE_INFO);
			return;
		}
		if (canCreateGuild(user)) {
			user.sendPacket(new ShowGuildFundationFormResponse());
		}
	}

	public void sendGuilds(User user) {
		// Comando GLINFO
		if (user.getGuildInfo().m_esGuildLeader) {
			sendGuildLeaderInfo(user);
		} else {
			sendGuildsList(user);
		}
	}

	public void doSalirClan(User user) {
		// Salir del clan.
		// Comando /SALIRCLAN
		if (user.getGuildInfo().esGuildLeader()) {
			user.sendMessage("Eres líder del clan, no puedes salir del mismo.", FontType.FONTTYPE_INFO);
		} else if (!user.getGuildInfo().esMiembroClan()) {
			user.sendMessage("No perteneces a ningún clan.", FontType.FONTTYPE_INFO);
		} else {
			getGuild(user).removeMember(user.getUserName());
			user.getGuildInfo().salirClan();
			getGuild(user).messageToGuildMembers(user.getUserName() + " decidió dejar el clan.", FontType.FONTTYPE_GUILD);
		}
	}

	public void sendOpenVotingAnnouncement(User user) {
		if (user == null) {
			return;
		}
		user.sendMessage("Hoy es la votación para elegir un nuevo lider del clan!!.", FontType.FONTTYPE_GUILD);
		user.sendMessage("La eleccion durara 24 horas, se puede votar a cualquier miembro del clan.", FontType.FONTTYPE_GUILD);
		user.sendMessage("Para votar escribe /VOTO NICKNAME.", FontType.FONTTYPE_GUILD);
		user.sendMessage("Solo se computarÁ un voto por miembro.", FontType.FONTTYPE_GUILD);
	}

/*
'
'
'ELECCIONES
'
'

Public Function EleccionesAbiertas() As Boolean
Dim ee As String
    ee = GetVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesAbiertas")
    EleccionesAbiertas = (ee = "1")     'cualquier otra cosa da falso
End Function

Public Sub AbrirElecciones()
    Call WriteVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesAbiertas", "1")
    Call WriteVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesFinalizan", DateAdd("d", 1, Now))
    Call WriteVar(VOTACIONESFILE, "INIT", "NumVotos", "0")
End Sub

Private Sub CerrarElecciones()  'solo pueden cerrarse mediante recuento de votos
    Call WriteVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesAbiertas", "0")
    Call WriteVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesFinalizan", vbNullString)
    Call Kill(VOTACIONESFILE)   'borramos toda la evidencia ;-)
End Sub

Public Sub ContabilizarVoto(ByRef Votante As String, ByRef Votado As String)
Dim q       As Integer
Dim Temps   As String

    Temps = GetVar(VOTACIONESFILE, "INIT", "NumVotos")
    q = IIf(IsNumeric(Temps), CInt(Temps), 0)
    Call WriteVar(VOTACIONESFILE, "VOTOS", Votante, Votado)
    Call WriteVar(VOTACIONESFILE, "INIT", "NumVotos", CStr(q + 1))
End Sub

Public Function YaVoto(ByRef Votante) As Boolean
    YaVoto = ((LenB(Trim$(GetVar(VOTACIONESFILE, "VOTOS", Votante)))) <> 0)
End Function

Private Function ContarVotos(ByRef CantGanadores As Integer) As String
Dim q           As Integer
Dim i           As Integer
Dim Temps       As String
Dim tempV       As String
Dim d           As diccionario

On Error GoTo errh
    ContarVotos = vbNullString
    CantGanadores = 0
    Temps = GetVar(MEMBERSFILE, "INIT", "NroMembers")
    q = IIf(IsNumeric(Temps), CInt(Temps), 0)
    If q > 0 Then
        'el diccionario tiene clave el elegido y valor la #votos
        Set d = New diccionario
        
        For i = 1 To q
            'miembro del clan
            Temps = GetVar(MEMBERSFILE, "MEMBERS", "Member" & i)
            
            'a quienvoto
            tempV = GetVar(VOTACIONESFILE, "VOTOS", Temps)
            
            'si voto a alguien contabilizamos el voto
            If LenB(tempV) <> 0 Then
                If Not IsNull(d.At(tempV)) Then  'cuantos votos tiene?
                    Call d.AtPut(tempV, CInt(d.At(tempV)) + 1)
                Else
                    Call d.AtPut(tempV, 1)
                End If
            End If
        Next i
    
        'quien quedo con mas votos, y cuantos tuvieron esos votos?
        ContarVotos = d.MayorValor(CantGanadores)
    
        Set d = Nothing
    End If
    
Exit Function
errh:
    LogError ("clsClan.Contarvotos: " & Err.description)
    If Not d Is Nothing Then Set d = Nothing
    ContarVotos = vbNullString
End Function

Public Function RevisarElecciones() As Boolean
Dim FechaSufragio   As Date
Dim Temps           As String
Dim Ganador         As String
Dim CantGanadores   As Integer
Dim list()          As String
Dim i               As Long

    RevisarElecciones = False
    Temps = Trim$(GetVar(GUILDINFOFILE, "GUILD" & p_GuildNumber, "EleccionesFinalizan"))
    
    If Temps = vbNullString Then Exit Function
    
    If IsDate(Temps) Then
        FechaSufragio = CDate(Temps)
        If FechaSufragio < Now Then     'toca!
            Ganador = ContarVotos(CantGanadores)

            If CantGanadores > 1 Then
                'empate en la votacion
                Call SetGuildNews("*Empate en la votación. " & Ganador & " con " & CantGanadores & " votos ganaron las elecciones del clan")
            ElseIf CantGanadores = 1 Then
                list = Me.GetMemberList()
                
                For i = 0 To UBound(list())
                    If Ganador = list(i) Then Exit For
                Next i
                
                If i <= UBound(list()) Then
                    Call SetGuildNews("*" & Ganador & " ganó la elección del clan*")
                    Call Me.SetLeader(Ganador)
                    RevisarElecciones = True
                Else
                    Call SetGuildNews("*" & Ganador & " ganó la elección del clan pero abandonó las filas por lo que la votación queda desierta*")
                End If
            Else
                Call SetGuildNews("*El período de votación se cerró sin votos*")
            End If
            
            Call CerrarElecciones
            
        End If
    Else
        Call LogError("clsClan.RevisarElecciones: tempS is not Date")
    End If

End Function

'/VOTACIONES
    
 */
}
