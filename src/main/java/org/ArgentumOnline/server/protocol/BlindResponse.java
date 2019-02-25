package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlindResponse extends ServerPacket {
	// Blind
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Blind;
	}
	public BlindResponse(){
	}
};

