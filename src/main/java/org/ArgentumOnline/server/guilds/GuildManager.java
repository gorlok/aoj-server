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

import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class GuildManager {
	private static Logger log = LogManager.getLogger();

	private AojServer server;
	
	public GuildManager(AojServer server) {
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
    private Guild getGuild(String guildName) {
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
    
    public void crearClan(Client cliente, String name) {
        if (this.createGuild(cliente, name)) {
            int n;
            if ((n = this.guildsCount()) == 1) {
                cliente.enviarMensaje("¡¡Felicidades!! Has creado el primer clan de Argentum!!!.", FontType.INFO);
            } else {
                cliente.enviarMensaje("¡Felicidades! Has creado el clan número " + n + " de Argentum!!!.", FontType.INFO);
            }
            this.saveGuildsDB();
        }
    }

    public void computeVote(Client cliente, String voto) {
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
            		Client miembro;
            		for (String member: guild.members) {
            			miembro = this.server.getUsuario(member);
            			if (miembro != null) {
            				miembro.enviarMensajeVotacion();
            			}
            		}
            	} else {
                    if (guild.members.size() > 1) {
                    	// compute elections results
                        String newLeaderName = guild.nuevoLider();
                        String oldLeaderName = guild.leader;
                        guild.elections = false;
                        Client newLeader = this.server.getUsuario(newLeaderName);
                        Client oldLeader = this.server.getUsuario(guild.leader);
                        if (!oldLeaderName.equalsIgnoreCase(newLeaderName)) {
                        	if (oldLeader != null) {
                        		oldLeader.guildInfo().m_esGuildLeader = false;
                        	} else {
                        		// CAMBIAR EN .CHR
                        		Client.changeGuildLeaderChr(oldLeaderName, false);
                        	}
                            if (newLeader != null) {
                                newLeader.guildInfo().m_esGuildLeader = true;
                                newLeader.guildInfo().incVecesFueGuildLeader();
                            } else {
                        		// CAMBIAR EN .CHR
                        		Client.changeGuildLeaderChr(newLeaderName, true);
                            }
                            guild.leader = newLeaderName;
                        }
                        if (newLeader != null) {
	                        guild.enviarMensaje("La elecciones han finalizado!!.", FontType.GUILD);
	                        guild.enviarMensaje("El nuevo lider es " + newLeaderName, FontType.GUILD);
                        }
                        if (newLeader != null) {
                            newLeader.enviarMensaje("¡¡¡Has ganado las elecciones, felicitaciones!!!", FontType.GUILD);
                            newLeader.guildInfo().giveGuildPoints(400);
                        } else {
                    		Client.changeGuildPtsChr(newLeaderName, 400);                        
                        }
                        guild.daysSinceLastElection = 0;
                    }
            	}
            }
        }
    }

    public void acceptPeaceOffer(Client cliente, String guildName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(guildName);
        if (guild == null) {
            return;
        }
        if (!guild.isEnemy(cliente.guildInfo().getGuildName())) {
            cliente.enviarMensaje("No estás en guerra con el clan.", FontType.GUILD);
            return;
        }
        guild.removeEnemy(cliente.guildInfo().getGuildName());
        guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        guild.removeEnemy(guildName);
        guild.removePeaceProposition(guildName);
        Client userGuild = this.server.getUsuario(guildName);
        if (userGuild != null) {
            userGuild.enviarMensaje("El clan firmó la paz con " + cliente.getNick(), FontType.GUILD);
        }
        guild.enviarMensaje("El clan firmó la paz con " +  guildName, FontType.GUILD);
    }
    
    public void sendPeaceRequest(Client cliente, String guildName, String desc) {
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

    public void recievePeaceOffer(Client cliente, String guildName, String desc) {
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
            cliente.enviarMensaje("No estás en guerra con el clan.", FontType.GUILD);
            return;
        }
        if (guild.isAllie(cliente.guildInfo().getGuildName())) {
            cliente.enviarMensaje("Ya estás en paz con el clan.", FontType.GUILD);
            return;
        }
        GuildRequest peaceoffer = new GuildRequest(cliente.guildInfo().getGuildName(), desc);
        if (!guild.includesPeaceOffer(peaceoffer.getUserName())) {
            guild.peacePropositions.add(peaceoffer);
            cliente.enviarMensaje("La propuesta de paz ha sido entregada.", FontType.GUILD);
        } else {
            cliente.enviarMensaje("Ya has enviado una propuesta de paz.", FontType.GUILD);
        }
    }
    public void sendPeacePropositions(Client cliente) {
        if (!cliente.guildInfo().esGuildLeader()) {
			return;
		}
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        if (guild.peacePropositions.isEmpty()) {
			return;
		}
        sb.append(guild.peacePropositions.size()).append(",");
        for (GuildRequest solicitud: guild.peacePropositions) {
        	sb.append(solicitud.getUserName()).append(",");
        }
        //cliente.enviar(MSG_PEACEPR, sb.toString());
    }

    public void echarMember(Client cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        if (guild.founder.equalsIgnoreCase(userName)) {
        	cliente.enviarMensaje("No puedes echar al miembro fundador!", FontType.GUILD);
        	return;
        }
        if (guild.leader.equalsIgnoreCase(userName)) {
        	cliente.enviarMensaje("No puedes echar al lider!", FontType.GUILD);
        	return;
        }
        Client miembro = this.server.getUsuario(userName);
        if (miembro == null) {
            cliente.enviarMensaje("El usuario no esta ONLINE.", FontType.GUILD);
            return;        	
        }
        // El usuario miembro está online.
        miembro.enviarMensaje("Has sido expulsado del clan.", FontType.GUILD);
        miembro.salirClan();
        guild.enviarMensaje(userName + " fue expulsado del clan.", FontType.GUILD);
        guild.removeMember(userName);
    }
    
    public void denyRequest(Client cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        Client solicitante = this.server.getUsuario(userName);
        if (solicitante != null) {
        	// Esta online
        	solicitante.enviarMensaje("Tu solicitud ha sido rechazada.", FontType.GUILD);
        	cliente.guildInfo().incSolicitudesRechazadas();
        }
        guild.joinRequest.remove(solicitud);
    }
    
    public void acceptClanMember(Client cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        // Comprobar si el usuario solicitante está online.
        Client solicitante = this.server.getUsuario(userName);
        if (solicitante == null) {
            cliente.enviarMensaje("Solo podes aceptar solicitudes cuando el solicitante esta ONLINE.", FontType.GUILD);
        	return;
        }
        if (solicitante.guildInfo().esGuildLeader()) {
            cliente.enviarMensaje("No podés aceptar esa solicitud, el pesonaje es lider de otro clan.", FontType.GUILD);
            return;
        }
        // Ingresarlo al clan.
        solicitante.ingresarClan(cliente.guildInfo().getGuildName());
        cliente.enviarMensaje("Felicitaciones, tu solicitud ha sido aceptada.", FontType.GUILD);
        cliente.enviarMensaje("Ahora sos un miembro activo del clan " + cliente.guildInfo().getGuildName(), FontType.GUILD);
        cliente.guildInfo().giveGuildPoints(25);
        guild.members.add(solicitante.getNick());
        guild.joinRequest.remove(solicitud);
        guild.enviarSonido(Constants.SND_ACEPTADOCLAN);
        guild.enviarMensaje(solicitante.getNick() + " ha sido aceptado en el clan.", FontType.GUILD);
    }
    
    public void sendPeticion(Client cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        GuildRequest solicitud = guild.getSolicitudIngreso(userName);
        if (solicitud == null) {
        	return;
        }
        //cliente.enviar(MSG_PETICIO, solicitud.getDesc());
    }
	
    public void solicitudIngresoClan(Client solicitante, String guildName, String desc) {
    	if (solicitante.esNewbie()) {
           solicitante.enviarMensaje("Los newbies no pueden conformar clanes.", FontType.GUILD);
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
            solicitante.enviarMensaje("Tu solicitud ya fue recibida por el lider del clan, ahora debes esperar la respuesta.", FontType.GUILD);
        	return;
        }
        // Nos aseguramos que se acumulen mas de 25 solicitudes pendientes.
        if (guild.joinRequest.size() > 25) {
        	solicitante.enviarMensaje("Hay demasiadas solicitudes pendientes de ingreso a este clan. Envia tu solicitud en otro momento.", FontType.GUILD);
        }
        solicitante.guildInfo().incSolicitudes();
        guild.joinRequest.add(solicitud);
        solicitante.enviarMensaje("La solicitud será entregada al lider del clan, ahora debes esperar su respuesta.", FontType.GUILD);
    }

    public void sendCharInfo(Client cliente, String userName) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        String info = Client.getChrInfo(userName);
        if (info != null) {
		//	cliente.enviar(MSG_CHRINFO, info);
		}
    }

    public void updateGuildNews(Client cliente, String news) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        guild.guildNews = news;
    }

    public void updateCodexAndDesc(Client lider, String data) {
        if (!lider.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(lider.guildInfo().getGuildName());
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
    
    public void sendGuildLeaderInfo(Client cliente) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        //<-------Lista de guilds ---------->
        StringBuffer sb = new StringBuffer(guildsCount() + "¬");
        for (Guild guild: getGuilds()) {
            sb.append(guild.guildName);
            sb.append("¬");
        }
        Guild guild = getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        //<-------Lista de miembros ---------->
        sb.append(guild.members.size());
        sb.append("¬");
        for (String member: guild.members) {
            sb.append(member);
            sb.append("¬");
        }
        //<------- Guild News -------->
        String news = guild.guildNews.replaceAll("º", "\r\n");
        sb.append(news);
        sb.append("¬");
        //<------- Solicitudes de ingreso ------->
        sb.append(guild.joinRequest.size());
        sb.append("¬");
        for (GuildRequest solicitud: guild.joinRequest) {
            sb.append(solicitud.getUserName());
            sb.append("¬");
        }
        //cliente.enviar(MSG_LEADERI, sb.toString());
    }

    public void setNewURL(Client cliente, String newURL) {
        if (!cliente.guildInfo().esGuildLeader()) {
            return;
        }
        Guild guild = this.getGuild(cliente.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        guild.URL = newURL;
        cliente.enviarMensaje("La direccion de la web ha sido actualizada", FontType.INFO);
    }

    public void declareAllie(Client cliente, String guildName) {
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
        Guild leaderGuild = this.getGuild(cliente.guildInfo().getGuildName());
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isAllie(enemyGuild.guildName)) {
            cliente.enviarMensaje("Estas en guerra con éste clan, antes debes firmar la paz.", FontType.GUILD);
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            cliente.enviarMensaje("Ya estas aliado con éste clan.", FontType.GUILD);
            return;
        }
        leaderGuild.alliedGuilds.add(enemyGuild.guildName);
        enemyGuild.alliedGuilds.add(leaderGuild.guildName);
        leaderGuild.enviarMensaje("Tu clan ha firmado una alianza con " + enemyGuild.guildName, FontType.GUILD);
        leaderGuild.enviarSonido(Constants.SND_DECLAREWAR);        
        enemyGuild.enviarMensaje(leaderGuild.guildName + " firmó una alianza con tu clan.", FontType.GUILD);
        enemyGuild.enviarSonido(Constants.SND_DECLAREWAR);        
    }
    
    public void declareWar(Client cliente, String guildName) {
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
        Guild leaderGuild = this.getGuild(cliente.guildInfo().getGuildName());
        if (leaderGuild == null) {
            return;
        }
        if (leaderGuild.isEnemy(enemyGuild.guildName)) {
            cliente.enviarMensaje("Tu clan ya esta en guerra con " + enemyGuild.guildName, FontType.GUILD);
            return;
        }    
        leaderGuild.removeAllie(enemyGuild.guildName);
        enemyGuild.removeAllie(leaderGuild.guildName);
        leaderGuild.enemyGuilds.add(enemyGuild.guildName);
        enemyGuild.enemyGuilds.add(leaderGuild.guildName);
        leaderGuild.enviarMensaje("Tu clan le declaró la guerra a " + enemyGuild.guildName, FontType.GUILD);
        leaderGuild.enviarSonido(Constants.SND_DECLAREWAR);
        enemyGuild.enviarMensaje(leaderGuild.guildName + " le declaradó la guerra a tu clan.", FontType.GUILD);
        enemyGuild.enviarSonido(Constants.SND_DECLAREWAR);        
    }
    
    public void sendGuildNews(Client miembro) {
    	if (!miembro.esMiembroClan()) {
			return;
		}
        Guild guild = this.getGuild(miembro.guildInfo().getGuildName());
        if (guild == null) {
            return;
        }
        StringBuffer sb = new StringBuffer()
		.append(guild.guildNews).append("¬")
		.append(guild.enemyGuilds.size()).append("¬");
        for (String name: guild.enemyGuilds) {
			sb.append(name).append("¬");
		}
		sb.append(guild.alliedGuilds.size()).append("¬");
		for (String name: guild.alliedGuilds) {
			sb.append(name).append("¬");
		}
		//miembro.enviar(MSG_GUILDNE, sb.toString());
		if (guild.elections) {
			miembro.enviarMensajeVotacion();
		}
    }

    public void sendGuildsList(Client cliente) {
        StringBuffer sb = new StringBuffer();
        sb.append(guildsCount());
        sb.append(",");
        for (Guild guild: getGuilds()) {
            sb.append(guild.guildName);
            sb.append(",");
        }
       // cliente.enviar(MSG_GL, sb.toString());
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

    public void sendGuildDetails(Client cliente, String guildName) {
		Guild guild = this.getGuild(guildName);
		if (guild == null) {
			return;
		}
        StringBuffer msg = new StringBuffer()
	        .append(guild.guildName).append("¬")
	        .append(guild.founder).append("¬")
	        .append(guild.fundationDate).append("¬")
	        .append(guild.leader).append("¬")
	    	.append(guild.URL).append("¬")
	    	.append(guild.members.size()).append("¬")
	        .append(guild.daysToNextElection()).append("¬")
	        .append(guild.gold).append("¬")
	        .append(guild.enemyGuilds.size()).append("¬")
	        .append(guild.alliedGuilds.size()).append("¬");
        int cant = guild.codexLength();
        msg.append(cant);
        for (int i = 0; i < cant; i++) {
            msg.append("¬");
            msg.append(guild.getCodex(i));
        }
        msg.append("¬").append(guild.description);
       // cliente.enviar(MSG_CLANDET, msg.toString());
    }
    
    public boolean canCreateGuild(Client cliente) {
        if (cliente.level() < 20) {
            cliente.enviarMensaje("Para fundar un clan debes de ser nivel 20 o superior", FontType.GUILD);
            return false;
        }
        if (cliente.getEstads().userSkills[Skill.SKILL_Liderazgo] < 90) {
            cliente.enviarMensaje("Para fundar un clan necesitás al menos 90 pts en liderazgo", FontType.GUILD);
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
    
    public boolean createGuild(Client cliente, String guildInfo) {
        if (!canCreateGuild(cliente)) {
            return false;
        }
        Guild guild;
        try {
            guild = new Guild(guildInfo, cliente.getNick(), (long) cliente.getReputacion().getPromedio());
        } catch (InvalidGuildNameException e) {
            cliente.enviarMensaje("Los datos del clan son inválidos, asegurate que no contiene caracteres inválidos.", FontType.GUILD);
            return false;
        }
        if (existeGuild(guild.guildName)) {
            cliente.enviarMensaje("Ya existe un clan con ese nombre.", FontType.GUILD);
            return false;
        }
        guild.members.add(cliente.getNick());
        addGuild(guild);
        cliente.guildInfo().fundarClan(guild.guildName);
      //  this.server.enviarATodos(MSG_TW, Constants.SND_CREACIONCLAN);
       // this.server.enviarATodos(MSG_TALK, "¡¡¡" + cliente.getNick() + " fundó el clan '" + guild.guildName + "'!!!" + FontType.GUILD);
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
    
}
