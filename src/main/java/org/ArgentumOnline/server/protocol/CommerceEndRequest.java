package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceEndRequest extends ClientPacket {
	// CommerceEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceEnd;
	}
	public CommerceEndRequest(){
	}
};

