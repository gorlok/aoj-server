package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RainToggleRequest extends ClientPacket {
	// RainToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RainToggle;
	}
	public RainToggleRequest(){
	}
};

