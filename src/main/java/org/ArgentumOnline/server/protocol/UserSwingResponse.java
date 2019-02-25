package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserSwingResponse extends ServerPacket {
	// UserSwing
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserSwing;
	}
	public UserSwingResponse(){
	}
};

