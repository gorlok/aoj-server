package org.ArgentumOnline.server.forum;

import java.util.HashMap;

import org.ArgentumOnline.server.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForumManager {
	private static Logger log = LogManager.getLogger();

    private HashMap<String, Forum> 	m_foros       = new HashMap<String, Forum>();

    public void ponerMensajeForo(String foroId, String titulo, String texto) {
        Forum forum = this.m_foros.get(foroId);
        if (forum == null) {
            this.m_foros.put(foroId, (forum = new Forum(foroId)));
        }
        forum.addMessage(titulo, texto);
    }
    
    public void enviarMensajesForo(String foroId, Player cliente) {
        Forum forum = this.m_foros.get(foroId);
        if (forum == null) {
            this.m_foros.put(foroId, (forum = new Forum(foroId)));
        }
        // Enviar mensajes dejados en el foro:
        for (int i = 1; i <= forum.messageCount(); i++) {
            ForumMessage msg = forum.getMessage(i);
           // cliente.enviar(MSG_FMSG, msg.getTitle(), msg.getBody());
            // FIXME
        }
        // Enviar fin de foro:
       // cliente.enviar(MSG_MFOR);
        // FIXME
    }

}
