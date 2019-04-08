package org.argentumonline.server.gm;

import java.util.ArrayList;
import java.util.List;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.protocol.ShowSOSFormResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Log;

public class HelpRequest {
	
	private GameServer server;

    /** User names than asked for help */
    private List<String> helpRequests = new ArrayList<>();

	public HelpRequest(GameServer server) {
		super();
		this.server = server;
	}
    
    public List<String> helpRequests() {
        return this.helpRequests;
    }

	public void clearAllHelpRequestToGm(User admin) {
		// Comando /BORRAR SOS
		// Comando para borrar todos pedidos /GM pendientes
		if (!admin.isGM()) {
			return;
		}
    	this.helpRequests().clear();
		admin.sendMessage("Todos los /GM pendientes han sido eliminados.", FontType.FONTTYPE_INFO);
		Log.logGM(admin.getUserName(), "/BORRAR SOS");
	}

	public void askForHelpToGM(User user) {
		// Comando /GM
		// Pedir ayuda a los GMs.
		var requests = helpRequests();
		if (!requests.contains(user.getUserName())) {
			requests.add(user.getUserName());
			user.sendMessage("El mensaje ha sido entregado, ahora solo debes esperar que se desocupe algun GM.",
					FontType.FONTTYPE_INFO);
		} else {
			requests.remove(user.getUserName());
			requests.add(user.getUserName());
			user.sendMessage(
					"Ya habias mandado un mensaje, tu mensaje ha sido movido al final de la cola de mensajes. Ten paciencia.",
					FontType.FONTTYPE_INFO);
		}
	}

	public void sendHelpRequests(User admin) {
		// Comando /SHOW SOS
		if (!admin.isGM()) {
			return;
		}
		String sosList = String.join("" + Constants.NULL_CHAR, helpRequests);
		admin.sendPacket(new ShowSOSFormResponse(sosList));
	}

	public void removeHelpRequest(User admin, String userName) {
		// Comando SOSDONE
		if (!admin.isGM()) {
			return;
		}
		helpRequests().remove(userName);
	}
    
}
