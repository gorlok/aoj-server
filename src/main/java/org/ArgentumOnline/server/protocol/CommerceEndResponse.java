package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceEndResponse extends ServerPacket {
	// CommerceEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CommerceEnd;
	}
	public CommerceEndResponse(){
	}
};

