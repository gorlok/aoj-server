package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NavigateToggleResponse extends ServerPacket {
	// NavigateToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NavigateToggle;
	}
	public NavigateToggleResponse(){
	}
};

