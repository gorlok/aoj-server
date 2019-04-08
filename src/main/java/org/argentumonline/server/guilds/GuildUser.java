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

import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;

/**
 * @author gorlok
 */
public class GuildUser {
    
	private User user;
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
    
    public GuildUser(User user) {
        this.user = user;
    }
    
	public void ingresarClan(String guildName) {
		this.m_guildName = guildName;
		incIngresos();
	}

	public void salirClan() {
		incEchadas();
		resetGuild();
	}
    
	public boolean esMiembroClan() {
		return m_guildName.length() != 0;
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
        this.user.sendMessage("¡¡¡Has recibido " + pts + " guildpoints!!!", FontType.FONTTYPE_GUILD);
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

