package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RainToggleResponse extends ServerPacket {
	// RainToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RainToggle;
	}
	public RainToggleResponse(){
	}
};

