package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MeditateToggleResponse extends ServerPacket {
	// MeditateToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.MeditateToggle;
	}
	public MeditateToggleResponse(){
	}
};

