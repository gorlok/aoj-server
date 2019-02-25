package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TileBlockedToggleRequest extends ClientPacket {
	// TileBlockedToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TileBlockedToggle;
	}
	public TileBlockedToggleRequest(){
	}
};

