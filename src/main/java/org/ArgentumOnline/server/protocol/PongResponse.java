package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PongResponse extends ServerPacket {
	// Pong
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Pong;
	}
	public PongResponse(){
	}
};

