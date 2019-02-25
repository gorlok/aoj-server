package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NavigateToggleRequest extends ClientPacket {
	// NavigateToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NavigateToggle;
	}
	public NavigateToggleRequest(){
	}
};

