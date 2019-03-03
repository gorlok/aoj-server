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
package org.ArgentumOnline.server.forum;

import java.util.HashMap;
import java.util.Map;

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.protocol.AddForumMsgResponse;
import org.ArgentumOnline.server.protocol.ShowForumFormResponse;

/**
 * @author gorlok
 */
public class ForumManager {

	private Map<String, Forum> forum = new HashMap<>();

	public void ponerMensajeForo(String foroId, String titulo, String texto) {
		Forum forum = this.forum.get(foroId);
		if (forum == null) {
			this.forum.put(foroId, (forum = new Forum(foroId)));
		}
		forum.addMessage(titulo, texto);
	}

	public void enviarMensajesForo(String foroId, Player player) {
		Forum forum = this.forum.get(foroId);
		if (forum == null) {
			this.forum.put(foroId, (forum = new Forum(foroId)));
		}
		// Enviar mensajes dejados en el foro:
		for (int i = 1; i <= forum.messageCount(); i++) {
			ForumMessage msg = forum.getMessage(i);
			player.sendPacket(new AddForumMsgResponse(msg.getTitle(), msg.getBody()));
		}
		// Enviar fin de foro:
		player.sendPacket(new ShowForumFormResponse());
	}

}
