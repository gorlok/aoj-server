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
package org.ArgentumOnline.server.quest;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcType;
import org.ArgentumOnline.server.util.FontType;

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
    
    public void checkNpcEnemigo(Player player, Npc npc) {
        if (this.m_enQuest && npc.esDeQuest()) {
            Quest quest  = this.server.quest(this.m_nroQuest);
            if (quest.Objetivo == 4) {
                this.m_realizoQuest = true;
                player.sendMessage("Has encontrado y eliminado a la criatura de la quest. ¡Ahora ve por tu recompensa!", FontType.FONTTYPE_FIGHT);
            }
        }
    }

	public void doIniciarAventura(Player player) {
		// Comando /AVENTURA
		// Se asegura que el target es un npc
		Npc npc = player.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!player.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			player.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().hacerQuest(player, npc);
	}

	public void doRecompensaAventura(Player player) {
		// Comando /REWARD
		// Se asegura que el target es un npc
		Npc npc = player.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!player.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			player.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().recibirRecompensaQuest(player);
	}

	public void doInfoAventura(Player player) {
		// Comando /INFOQ
		// Se asegura que el target es un npc
		Npc npc = player.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!player.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			player.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().sendInfoQuest(player);
	}

	public void doRendirseAventura(Player player) {
		// Comando /MERINDO
		// Se asegura que el target es un npc
		Npc npc = player.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!player.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			player.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().userSeRinde(player);
	}

	public void doAdivinarAventura(Player player) {
		// Comando /ADIVINA
		// Se asegura que el target es un npc
		Npc npc = player.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!player.checkAlive()) {
			return;
		}
		if (npc.npcType() != NpcType.NPCTYPE_QUEST) {
			player.sendMessage("Busca aventuras en otro lado.", FontType.FONTTYPE_INFO);
			return;
		}
		getQuest().checkNpcAmigo(player);
	}

}
