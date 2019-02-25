package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceStartRequest extends ClientPacket {
	// CommerceStart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceStart;
	}
	public CommerceStartRequest(){
	}
};

