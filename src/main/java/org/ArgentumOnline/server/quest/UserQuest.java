/**
 * UserQuest.java
 *
 * Created on 21 de marzo de 2004, 12:22
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
package org.ArgentumOnline.server.quest;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.util.*;

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
        return this.server.getQuest(this.m_nroQuest);
    }
    
    public void checkNpcEnemigo(Player cliente, Npc npc) {
        if (this.m_enQuest && npc.esDeQuest()) {
            Quest quest  = this.server.getQuest(this.m_nroQuest);
            if (quest.Objetivo == 4) {
                this.m_realizoQuest = true;
                cliente.enviarMensaje("Has encontrado y eliminado a la criatura de la quest. ¡Ahora ve por tu recompensa!", FontType.FIGHT);
            }
        }
    }

	public void doIniciarAventura(Player client) {
		// Comando /AVENTURA
		// Se asegura que el target es un npc
		Npc npc = client.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!client.checkAlive()) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_QUEST) {
			client.enviarMensaje("Busca aventuras en otro lado.", FontType.INFO);
			return;
		}
		getQuest().hacerQuest(client, npc);
	}

	public void doRecompensaAventura(Player client) {
		// Comando /REWARD
		// Se asegura que el target es un npc
		Npc npc = client.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!client.checkAlive()) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_QUEST) {
			client.enviarMensaje("Busca aventuras en otro lado.", FontType.INFO);
			return;
		}
		getQuest().recibirRecompensaQuest(client);
	}

	public void doInfoAventura(Player client) {
		// Comando /INFOQ
		// Se asegura que el target es un npc
		Npc npc = client.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!client.checkAlive()) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_QUEST) {
			client.enviarMensaje("Busca aventuras en otro lado.", FontType.INFO);
			return;
		}
		getQuest().sendInfoQuest(client);
	}

	public void doRendirseAventura(Player client) {
		// Comando /MERINDO
		// Se asegura que el target es un npc
		Npc npc = client.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!client.checkAlive()) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_QUEST) {
			client.enviarMensaje("Busca aventuras en otro lado.", FontType.INFO);
			return;
		}
		getQuest().userSeRinde(client);
	}

	public void doAdivinarAventura(Player client) {
		// Comando /ADIVINA
		// Se asegura que el target es un npc
		Npc npc = client.getNearNpcSelected(Player.DISTANCE_QUEST);
		if (npc == null) {
			return;
		}
		if (!client.checkAlive()) {
			return;
		}
		if (npc.getNPCtype() != Npc.NPCTYPE_QUEST) {
			client.enviarMensaje("Busca aventuras en otro lado.", FontType.INFO);
			return;
		}
		getQuest().checkNpcAmigo(client);
	}

}
