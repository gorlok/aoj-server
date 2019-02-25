package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DumbResponse extends ServerPacket {
	// Dumb
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Dumb;
	}
	public DumbResponse(){
	}
};

