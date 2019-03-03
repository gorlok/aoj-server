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
	
	private Map<String, Forum> m_foros = new HashMap<String, Forum>();

	public void ponerMensajeForo(String foroId, String titulo, String texto) {
		Forum forum = this.m_foros.get(foroId);
		if (forum == null) {
			this.m_foros.put(foroId, (forum = new Forum(foroId)));
		}
		forum.addMessage(titulo, texto);
	}

	public void enviarMensajesForo(String foroId, Player player) {
		Forum forum = this.m_foros.get(foroId);
		if (forum == null) {
			this.m_foros.put(foroId, (forum = new Forum(foroId)));
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
