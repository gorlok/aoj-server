package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceInitResponse extends ServerPacket {
	// CommerceInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CommerceInit;
	}
	public CommerceInitResponse(){
	}
};

