package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PauseToggleResponse extends ServerPacket {
	// PauseToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PauseToggle;
	}
	public PauseToggleResponse(){
	}
};

