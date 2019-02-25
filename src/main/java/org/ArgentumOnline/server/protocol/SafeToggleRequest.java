package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SafeToggleRequest extends ClientPacket {
	// SafeToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SafeToggle;
	}
	public SafeToggleRequest(){
	}
};

