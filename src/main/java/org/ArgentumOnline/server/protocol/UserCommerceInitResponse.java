package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCommerceInitResponse extends ServerPacket {
	// UserCommerceInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCommerceInit;
	}
	public UserCommerceInitResponse(){
	}
};

