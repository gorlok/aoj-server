package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ServerOpenToUsersToggleRequest extends ClientPacket {
	// ServerOpenToUsersToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerOpenToUsersToggle;
	}
	public ServerOpenToUsersToggleRequest(){
	}
};

