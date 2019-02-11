/**
 * GuildInfo.java
 *
 * Created on 23 de febrero de 2004, 21:37
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

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.util.FontType;


/**
 * @author gorlok
 */
public class GuildInfo {
    Client cliente;
    public String  m_guildName = "";
    public long    m_solicitudes = 0;
    public long    m_solicitudesRechazadas = 0;
    public long    m_echadas = 0;
    public long    m_vecesFueGuildLeader = 0;
    public boolean m_yaVoto = false;
    public boolean m_esGuildLeader = false;
    public boolean m_fundoClan = false;
    public String  m_clanFundado = "";
    public long    m_clanesParticipo = 0;
    public long    m_guildPoints = 0;
    
    public GuildInfo(Client cliente) {
        this.cliente = cliente;
    }
    
    public String getGuildName() {
        return this.m_guildName;
    }
    
    public boolean esGuildLeader() {
        return this.m_esGuildLeader;
    }
    
    public boolean yaVoto() {
        return this.m_yaVoto;
    }
    
    public void voto() {
        this.m_yaVoto = true;
    }
    
    public void fundarClan(String guildName) {
        this.m_fundoClan = true;
        this.m_esGuildLeader = true;
        this.m_vecesFueGuildLeader++;
        this.m_clanesParticipo++;
        this.m_clanFundado = guildName;
        this.m_guildName = guildName;        
        giveGuildPoints(5000);
    }
    
    public void giveGuildPoints(int pts) {
        this.cliente.enviarMensaje("¡¡¡Has recibido " + pts + " guildpoints!!!", FontType.GUILD);
        this.m_guildPoints += pts;
        if (this.m_guildPoints > 9000000) {
            this.m_guildPoints = 9000000;
        }
    }
    
    public void resetGuild() {
    	this.m_guildName = "";
        this.m_guildPoints = 0;
    }
    
    public void incEchadas() {
    	if (this.m_echadas < 1000) {
			this.m_echadas++;
		}
    }
    
    public void incIngresos() {
    	if (this.m_clanesParticipo < 1000) {
			this.m_clanesParticipo++;
		}
    }
    
    public void incSolicitudesRechazadas() {
    	if (this.m_solicitudesRechazadas < 1000) {
			this.m_solicitudesRechazadas++;
		}
    }
    
    public void incSolicitudes() {
    	if (this.m_solicitudes < 1000) {
			this.m_solicitudes++;
		}
    }

    public void incVecesFueGuildLeader() {
    	if (this.m_vecesFueGuildLeader < 10000) {
			this.m_vecesFueGuildLeader++;
		}
    }
 }

