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
package org.argentumonline.server.quest;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcType;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;

/**
 *
 * @author  gorlok
 */
public class UserQuest {
	
    public short m_nroQuest = 1;
    public short m_recompensa = 0;
    public boolean m_enQuest = false;
    public boolean m_realizoQuest = false;
    
    private GameServer server;
    
    /** Creates a new instance of UserQuest */
    public UserQuest(GameServer server) {
    	this.server = server;
    }
    
    public Quest getQuest() {
        return this.server.quest(this.m_nroQuest);
    }
    
    public void checkNpcEnemigo(User user, Npc npc) {
        if (this.m_enQuest && npc.isQuest()) {
            Quest quest  = this.server.quest(this.m_nroQuest);
            if (quest.Objetivo == 4) {
                this.m_realizoQuest = true;
                user.sendMessage("Has encontrado y eliminado a la criatura de la quest. ¡Ahora ve por tu recompensa!", FontType.FONTTYPE_FIGHT);
            }
        }
    }

	public void doIniciarAventura(User user) {
		// Comando /AVENTURA
		// Se asegura que el target es un npc
		Npc npc = user.getNearNpcSelected(User.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!user.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			user.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().hacerQuest(user, npc);
	}

	public void doRecompensaAventura(User user) {
		// Comando /REWARD
		// Se asegura que el target es un npc
		Npc npc = user.getNearNpcSelected(User.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!user.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			user.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().recibirRecompensaQuest(user);
	}

	public void doInfoAventura(User user) {
		// Comando /INFOQ
		// Se asegura que el target es un npc
		Npc npc = user.getNearNpcSelected(User.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!user.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			user.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().sendInfoQuest(user);
	}

	public void doRendirseAventura(User user) {
		// Comando /MERINDO
		// Se asegura que el target es un npc
		Npc npc = user.getNearNpcSelected(User.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!user.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			user.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().userSeRinde(user);
	}

	public void doAdivinarAventura(User user) {
		// Comando /ADIVINA
		// Se asegura que el target es un npc
		Npc npc = user.getNearNpcSelected(User.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!user.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			user.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().checkNpcAmigo(user);
	}

}
