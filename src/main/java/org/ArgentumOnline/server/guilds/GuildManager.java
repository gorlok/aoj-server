/**
 * GuildManager.java
 *
 * Created on 26/mayo/2007.
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

//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_CHRINFO;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_CLANDET;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_GL;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_GUILDNE;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_LEADERI;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_PEACEDE;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_PEACEPR;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_PETICIO;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_TALK;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_TW;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.protocol.GuildDetailsResponse;
import org.ArgentumOnline.server.protocol.GuildLeaderInfoResponse;
import org.ArgentumOnline.server.protocol.GuildListResponse;
import org.ArgentumOnline.server.protocol.GuildNewsResponse;
import org.ArgentumOnline.server.protocol.PeaceProposalsListResponse;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class GuildManager {
	private static Logger log = LogManager.getLogger();

	private GameServer server;
	
	public GuildManager(GameServer server) {
		this.server = server;
	}
	
    /** Guilds collection */
    private Map<String, Guild> guilds = new HashMap<String, Guild>();

    private Collection<Guild> getGuilds() {
    	return this.guilds.values();    	
    }
    
    /**
     * Add a guild into the guilds collection.
     * @param guild
     */
    private void addGuild(Guild guild) {
        this.guilds.put(guild.guildName.toUpperCase(), guild);
    }
    
    /**
     * Get a guild by name.
     * @param guildName
     * @return a Guild
     */
    public Guild getGuild(String guildName) {
        return this.guilds.get(guildName.toUpperCase());
    }
    
    /**
     * Gives guilds count.
     * @return guilds count
     */
    public int guildsCount() {
        return this.guilds.values().size();
    }

    /**
     * Remove all guilds from guilds collection.
     */
    private void guildsClear() {
    	this.guilds.clear();
    }
    
    private void crearClan(Player cliente, String name) {
        if (this.createGuild(cliente, name)) {
            int n;
            if ((n = this.guildsCount()) == 1) {
                cliente.enviarMensaje("¡¡Felicidades!! Has creado el primer clan de Argentum!!!.", FontType.FONTTYPE_INFO);
            } else {
                cliente.enviarMensaje("¡Felicidades! Has creado el clan número " + n + " de Argentum!!!.", FontType.FONTTYPE_INFO);
            }
            this.saveGuildsDB();
        }
    }

    private void computeVote(Player cliente, String voto) {
        Guild guild = getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        guild.computeVote(cliente, voto);
    }

    public void dayElapsed() {
    	for (Guild guild: getGuilds()) {
            if (guild.daysSinceLastElection < guild.electionPeriod) {
            	guild.daysSinceLastElection++;
            } else {
            	if (!guild.elections) {
            		guild.resetVotes();
            		guild.elections = true;
            		guild.members.forEach(s -> enviarMensajeVotacion(this.server.getUsuario(s)));
            	} else {
                    if (guild.members.size() > 1) {
                    	// compute elections results
                        String newLeaderName = guild.nuevoLider();
                        String oldLeaderName = guild.leader;
                        guild.elections = false;
                        Player newLeader = this.server.getUsuario(newLeaderName);
                        Player oldLeader = this.server.getUsuario(guild.leader);
                        if (!oldLeaderName.equalsIgnoreCase(newLeaderName)) {
                        	if (oldLeader != null) {
                        		oldLeader.guildInfo().m_esGuildLeader = false;
                        	} else {
                        		// CAMBIAR EN .CHR
                        		Player.changeGuildLeaderChr(oldLeaderName, false);
                        	}
                            if (newLeader != null) {
                                newLeader.guildInfo().m_esGuildLeader = true;
                                newLeader.guildInfo().incVecesFueGuildLeader();
                            } else {
                        		// CAMBIAR EN .CHR
                        		Player.changeGuildLeaderChr(newLeaderName, true);
                            }
                            guild.leader = newLeaderName;
                        }
                        if (newLeader != null) {
	                        guild.enviarMensaje("La elecciones han finalizado!!.", FontType.FONTTYPE_GUILD);
	                        guild.enviarMensaje("El nuevo lider es " + newLeaderName, FontType.FONTTYPE_GUILD);
                        }
                        if (newLeader != null) {
                            newLeader.enviarMensaje("¡¡¡Has ganado las elecciones, felicitaciones!!!", FontType.FONTTYPE_GUILD);
                            newLeader.guildInfo().giveGuildPoints(400);
                        } else {
                    		Player.changeGuildPtsChr(newLeaderName, 400);                        
                        }
                        guild.daysSinceLastElection = 0;
                    }
            	}
            }
        }
    }

    private void acceptPeaceOffer(Player cliente, String guildName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (!guild.isEnemy(cliente.guildInfo().getGuildName())) {
            cliente.enviarMensaje("No estás en guerra con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        guild.removeEnemy(cliente.guildInfo().getGuildName());
        guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        guild.removeEnemy(guildName);
        guild.removePeaceProposition(guildName);
        Player userGuild = this.server.getUsuario(guildName);
        if (userGuild != null) {
            userGuild.enviarMensaje("El clan firmó la paz con " + cliente.getNick(), FontType.FONTTYPE_GUILD);
        }
        guild.enviarMensaje("El clan firmó la paz con " +  guildName, FontType.FONTTYPE_GUILD);
    }
    
    private void sendPeaceRequest(Player cliente, String guildName, String desc) {
        if (!cliente.guildInfo().esGuildLeader()) {
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
        //cliente.enviar(MSG_PEACEDE, solic.desc);
    }

    private void recievePeaceOffer(Player cliente, String guildName, String desc) {
        if (!cliente.guildInfo().esGuildLeader()) {
			return;
		}
        if (cliente.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (!guild.isEnemy(cliente.guildInfo().getGuildName())) {
            cliente.enviarMensaje("No estás en guerra con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        if (guild.isAllie(cliente.guildInfo().getGuildName())) {
            cliente.enviarMensaje("Ya estás en paz con el clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        GuildRequest peaceoffer = new GuildRequest(cliente.guildInfo().getGuildName(), desc);
        if (!guild.includesPeaceOffer(peaceoffer.getUserName())) {
            guild.peacePropositions.add(peaceoffer);
            cliente.enviarMensaje("La propuesta de paz ha sido entregada.", FontType.FONTTYPE_GUILD);
        } else {
            cliente.enviarMensaje("Ya has enviado una propuesta de paz.", FontType.FONTTYPE_GUILD);
        }
    }
    
    private void sendPeacePropositions(Player cliente) {
        if (!cliente.guildInfo().esGuildLeader()) {
			return;
		}
        Guild guild = getGuild(cliente);
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
        cliente.sendPacket(new PeaceProposalsListResponse(sb.toString()));
    }

    private void echarMember(Player cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        if (guild.founder.equalsIgnoreCase(userName)) {
        	cliente.enviarMensaje("No puedes echar al miembro fundador!", FontType.FONTTYPE_GUILD);
        	return;
        }
        if (guild.leader.equalsIgnoreCase(userName)) {
        	cliente.enviarMensaje("No puedes echar al lider!", FontType.FONTTYPE_GUILD);
        	return;
        }
        Player miembro = this.server.getUsuario(userName);
        if (miembro == null) {
            cliente.enviarMensaje("El usuario no esta ONLINE.", FontType.FONTTYPE_GUILD);
            return;        	
        }
        // El usuario miembro está online.
        miembro.enviarMensaje("Has sido expulsado del clan.", FontType.FONTTYPE_GUILD);
        
		getGuild(miembro).removeMember(miembro.getNick());
		miembro.getGuildInfo().salirClan();
        
        guild.enviarMensaje(userName + " fue expulsado del clan.", FontType.FONTTYPE_GUILD);
        guild.removeMember(userName);
    }
    
    private void denyRequest(Player cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        Player solicitante = this.server.getUsuario(userName);
        if (solicitante != null) {
        	// Esta online
        	solicitante.enviarMensaje("Tu solicitud ha sido rechazada.", FontType.FONTTYPE_GUILD);
        	cliente.guildInfo().incSolicitudesRechazadas();
        }
        guild.joinRequest.remove(solicitud);
    }
    
    private void acceptClanMember(Player cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        Player solicitante = this.server.getUsuario(userName);
        if (solicitante == null) {
            cliente.enviarMensaje("Solo podes aceptar solicitudes cuando el solicitante esta ONLINE.", FontType.FONTTYPE_GUILD);
        	return;
        }
        if (solicitante.guildInfo().esGuildLeader()) {
            cliente.enviarMensaje("No podés aceptar esa solicitud, el pesonaje es lider de otro clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        // Ingresarlo al clan.
        String guildName = cliente.guildInfo().getGuildName();
        solicitante.getGuildInfo().ingresarClan(guildName);
        cliente.enviarMensaje("Felicitaciones, tu solicitud ha sido aceptada.", FontType.FONTTYPE_GUILD);
        cliente.enviarMensaje("Ahora sos un miembro activo del clan " + cliente.guildInfo().getGuildName(), FontType.FONTTYPE_GUILD);
        cliente.guildInfo().giveGuildPoints(25);
        guild.addMember(solicitante.getNick());
        guild.joinRequest.remove(solicitud);
        guild.enviarSonido(Constants.SND_ACEPTADOCLAN);
        guild.enviarMensaje(solicitante.getNick() + " ha sido aceptado en el clan.", FontType.FONTTYPE_GUILD);
    }

	private Guild getGuild(Player user) {
		return this.getGuild(user.guildInfo().getGuildName());
	}
    
    private void sendPeticion(Player cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        //cliente.enviar(MSG_PETICIO, solicitud.getDesc());
    }
	
    private void solicitudIngresoClan(Player solicitante, String guildName, String desc) {
    	if (solicitante.esNewbie()) {
           solicitante.enviarMensaje("Los newbies no pueden conformar clanes.", FontType.FONTTYPE_GUILD);
           return;
    	}
    	GuildRequest solicitud = new GuildRequest(solicitante.getNick(), desc);
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (guild.isMember(solicitante.getNick())) {
        	return;
        }
        if (guild.solicitudesIncludes(solicitante.getNick())) {
            solicitante.enviarMensaje("Tu solicitud ya fue recibida por el lider del clan, ahora debes esperar la respuesta.", FontType.FONTTYPE_GUILD);
        	return;
        }
        // Nos aseguramos que se acumulen mas de 25 solicitudes pendientes.
        if (guild.joinRequest.size() > 25) {
        	solicitante.enviarMensaje("Hay demasiadas solicitudes pendientes de ingreso a este clan. Envia tu solicitud en otro momento.", FontType.FONTTYPE_GUILD);
        }
        solicitante.guildInfo().incSolicitudes();
        guild.joinRequest.add(solicitud);
        solicitante.enviarMensaje("La solicitud será entregada al lider del clan, ahora debes esperar su respuesta.", FontType.FONTTYPE_GUILD);
    }

    private void sendCharInfo(Player cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        String info = Player.getChrInfo(userName);
        if (info != null) {
		//	cliente.enviar(MSG_CHRINFO, info);
		}
    }

    private void updateGuildNews(Player cliente, String news) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        guild.guildNews = news;
    }

    public void updateCodexAndDesc(Player lider, String data) {
		// Comando DESCOD
        if (!lider.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(lider);
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
    
    private void sendGuildLeaderInfo(Player player) {
        if (!player.guildInfo().esGuildLeader()) {
            return;
        }
		Guild playerGuild = getGuild(player.guildInfo().getGuildName());
        if (playerGuild == null) {
            return;
        }
        
        String guildsList = guildListAsString();
        String membersList = memberListAsString(playerGuild);
        String news = guildNewsAsString(playerGuild);
        String requestsList = guildRequestAsString(playerGuild);
        
        player.sendPacket(new GuildLeaderInfoResponse(guildsList, membersList, news, requestsList));
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

	private void setNewURL(Player cliente, String newURL) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = getGuild(cliente);
        if (guild == null) {
            return;
        }
        guild.URL = newURL;
        cliente.enviarMensaje("La direccion de la web ha sido actualizada", FontType.FONTTYPE_INFO);
    }

    private void declareAllie(Player cliente, String guildName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        if (cliente.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild enemyGuild = this.getGuild(guildName);
        if (enemyGuild == null) {
            return;
        }
        Guild leaderGuild = getGuild(cliente);
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isAllie(enemyGuild.guildName)) {
            cliente.enviarMensaje("Estas en guerra con éste clan, antes debes firmar la paz.", FontType.FONTTYPE_GUILD);
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            cliente.enviarMensaje("Ya estas aliado con éste clan.", FontType.FONTTYPE_GUILD);
            return;
        }
        leaderGuild.alliedGuilds.add(enemyGuild.guildName);
        enemyGuild.alliedGuilds.add(leaderGuild.guildName);
        leaderGuild.enviarMensaje("Tu clan ha firmado una alianza con " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
        leaderGuild.enviarSonido(Constants.SND_DECLAREWAR);        
        enemyGuild.enviarMensaje(leaderGuild.guildName + " firmó una alianza con tu clan.", FontType.FONTTYPE_GUILD);
        enemyGuild.enviarSonido(Constants.SND_DECLAREWAR);        
    }
    
    private void declareWar(Player cliente, String guildName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        if (cliente.guildInfo().getGuildName().equalsIgnoreCase(guildName)) {
			return;
		}
        Guild enemyGuild = this.getGuild(guildName);
        if (enemyGuild == null) {
            return;
        }
        Guild leaderGuild = getGuild(cliente);
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            cliente.enviarMensaje("Tu clan ya esta en guerra con " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
            return;
        }    
        leaderGuild.removeAllie(enemyGuild.guildName);
        enemyGuild.removeAllie(leaderGuild.guildName);
        leaderGuild.enemyGuilds.add(enemyGuild.guildName);
        enemyGuild.enemyGuilds.add(leaderGuild.guildName);
        leaderGuild.enviarMensaje("Tu clan le declaró la guerra a " + enemyGuild.guildName, FontType.FONTTYPE_GUILD);
        leaderGuild.enviarSonido(Constants.SND_DECLAREWAR);
        enemyGuild.enviarMensaje(leaderGuild.guildName + " le declaradó la guerra a tu clan.", FontType.FONTTYPE_GUILD);
        enemyGuild.enviarSonido(Constants.SND_DECLAREWAR);        
    }
    
    public void sendGuildNews(Player member) {
    	if (!member.getGuildInfo().esMiembroClan()) {
			return;
		}
		
		Guild playerGuild = getGuild(member);
        if (playerGuild == null) {
            return;
        }
        
        String news = guildNewsAsString(playerGuild);
    	String allies = alliesListAsString(playerGuild);
    	String enemies = enemiesListAsString(playerGuild);

    	member.sendPacket(new GuildNewsResponse(news, enemies, allies));
		
		if (playerGuild.elections) {
			enviarMensajeVotacion(member);
		}
    }

    private void sendGuildsList(Player cliente) {
        var sb = new StringBuilder();
        for (Guild guild: getGuilds()) {
            sb.append(guild.guildName);
            sb.append(Constants.NULL_CHAR);
        }
        if (sb.length()>0) {
        	sb.deleteCharAt(sb.length()-1);
        }
        
        cliente.sendPacket(new GuildListResponse(sb.toString()));
    }

    public void loadGuildsDB() {
        try {
        	guildsClear();
            IniFile ini = new IniFile(Constants.GUILDDIR + java.io.File.separator + "GuildsInfo.ini");
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

    private void sendGuildDetails(Player cliente, String guildName) {
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
		
        cliente.sendPacket(new GuildDetailsResponse(
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
    
    private boolean canCreateGuild(Player cliente) {
        if (cliente.stats().ELV < 20) {
            cliente.enviarMensaje("Para fundar un clan debes de ser nivel 20 o superior", FontType.FONTTYPE_GUILD);
            return false;
        }
        if (cliente.stats().userSkills[Skill.SKILL_Liderazgo] < 90) {
            cliente.enviarMensaje("Para fundar un clan necesitás al menos 90 pts en liderazgo", FontType.FONTTYPE_GUILD);
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
    
    public boolean createGuild(Player cliente, String guildInfo) {
        if (!canCreateGuild(cliente)) {
            return false;
        }
        Guild guild;
        try {
            guild = new Guild(guildInfo, cliente.getNick(), (long) cliente.reputation().getPromedio());
        } catch (InvalidGuildNameException e) {
            cliente.enviarMensaje("Los datos del clan son inválidos, asegurate que no contiene caracteres inválidos.", FontType.FONTTYPE_GUILD);
            return false;
        }
        if (existeGuild(guild.guildName)) {
            cliente.enviarMensaje("Ya existe un clan con ese nombre.", FontType.FONTTYPE_GUILD);
            return false;
        }
        guild.members.add(cliente.getNick());
        addGuild(guild);
        cliente.guildInfo().fundarClan(guild.guildName);
      //  this.server.enviarATodos(MSG_TW, Constants.SND_CREACIONCLAN);
       // this.server.enviarATodos(MSG_TALK, "¡¡¡" + cliente.getNick() + " fundó el clan '" + guild.guildName + "'!!!" + FontType.FONTTYPE_GUILD);
        return true;
    }

    public void saveGuildsDB() {
        try {
            IniFile ini = new IniFile();
            ini.setValue("INIT", "NroGuilds", guildsCount());
            int i = 1;
            for (Guild guild: getGuilds()) {
                guild.saveGuild(ini, i++);
            }
            ini.store(Constants.GUILDDIR + java.io.File.separator + "GuildsInfo.ini");
        } catch (Exception e) {
            log.fatal("ERROR EN guardarMOTD()", e);
        }
    }

	public void doFundarClan(Player user) {
		// Comando /FUNDARCLAN
		if (user.getGuildInfo().m_fundoClan) {
			user.enviarMensaje("Ya has fundado un clan, solo se puede fundar uno por personaje.", FontType.FONTTYPE_INFO);
			return;
		}
		if (canCreateGuild(user)) {
			// enviar(MSG_SHOWFUN);
		}
	}

	public void doInfoClan(Player user) {
		// Comando GLINFO
		if (user.getGuildInfo().m_esGuildLeader) {
			sendGuildLeaderInfo(user);
		} else {
			sendGuildsList(user);
		}
	}

	public void doCrearClan(Player user, String guildName) {
		// Comando CIG
		crearClan(user, guildName);
	}

	public void doSalirClan(Player user) {
		// Salir del clan.
		// Comando /SALIRCLAN
		if (user.getGuildInfo().esGuildLeader()) {
			user.enviarMensaje("Eres líder del clan, no puedes salir del mismo.", FontType.FONTTYPE_INFO);
		} else if (!user.getGuildInfo().esMiembroClan()) {
			user.enviarMensaje("No perteneces a ningún clan.", FontType.FONTTYPE_INFO);
		} else {
			getGuild(user).removeMember(user.getNick());
			user.getGuildInfo().salirClan();
			getGuild(user).enviarMensaje(user.getNick() + " decidió dejar el clan.", FontType.FONTTYPE_GUILD);
		}
	}

	public void doVotarClan(Player user, String s) {
		// Comando /VOTO
		computeVote(user, s);
	}

	public void enviarMensajeVotacion(Player user) {
		if (user == null) {
			return;
		}
		user.enviarMensaje("Hoy es la votación para elegir un nuevo lider del clan!!.", FontType.FONTTYPE_GUILD);
		user.enviarMensaje("La eleccion durara 24 horas, se puede votar a cualquier miembro del clan.", FontType.FONTTYPE_GUILD);
		user.enviarMensaje("Para votar escribe /VOTO NICKNAME.", FontType.FONTTYPE_GUILD);
		user.enviarMensaje("Solo se computarÁ un voto por miembro.", FontType.FONTTYPE_GUILD);
	}

	public void doAceptarOfertaPaz(Player user, String s) {
		// Comando ACEPPEAT
		acceptPeaceOffer(user, s);
	}

	public void doRecibirOfertaPaz(Player user, String s) {
		// Comando PEACEOFF
		StringTokenizer st = new StringTokenizer(s, ",");
		String userName = st.nextToken();
		String desc = st.nextToken();
		recievePeaceOffer(user, userName, desc);
	}

	public void doEnviarPedidoPaz(Player user, String s) {
		// Comnando PEACEDET
		StringTokenizer st = new StringTokenizer(s, ",");
		String userName = st.nextToken();
		String desc = st.nextToken();
		sendPeaceRequest(user, userName, desc);
	}

	public void doEnviarPeticion(Player user, String s) {
		// Comando ENVCOMEN
		sendPeticion(user, s);
	}

	public void doEnviarProposiciones(Player user) {
		// Comando ENVPROPP
		sendPeacePropositions(user);
	}

	public void doDeclararGuerra(Player user, String s) {
		// Comando DECGUERR
		declareWar(user, s);
	}

	public void doDeclararAlianza(Player user, String s) {
		// Comando DECALIAD
		declareAllie(user, s);
	}

	public void doSetNewURL(Player user, String s) {
		// Comando NEWWEBSI
		setNewURL(user, s);
	}

	public void doAceptarMiembroClan(Player user, String s) {
		// Comando ACEPTARI
		acceptClanMember(user, s);
	}

	public void doRechazarPedido(Player user, String s) {
		// Comando RECHAZAR
		denyRequest(user, s);
	}

	public void doEcharMiembro(Player user, String s) {
		// Comando ECHARCLA
		echarMember(user, s);
	}

	public void doActualizarGuildNews(Player user, String s) {
		// Comando ACTGNEWS
		updateGuildNews(user, s);
	}

	public void doCharInfoClan(Player user, String s) {
		// Comando 1HRINFO<
		sendCharInfo(user, s);
	}

	public void doSolicitudIngresoClan(Player user, String guildName, String desc) {
		// Comando SOLICITUD
		solicitudIngresoClan(user, guildName, desc);
	}

	public void doClanDetails(Player user, String s) {
		// Comando CLANDETAILS
		sendGuildDetails(user, s);
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
