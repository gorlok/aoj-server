package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PingRequest extends ClientPacket {
	// Ping
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Ping;
	}
	public PingRequest(){
	}
};

