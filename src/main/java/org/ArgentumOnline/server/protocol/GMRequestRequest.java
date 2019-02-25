package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GMRequestRequest extends ClientPacket {
	// GMRequest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMRequest;
	}
	public GMRequestRequest(){
	}
};

