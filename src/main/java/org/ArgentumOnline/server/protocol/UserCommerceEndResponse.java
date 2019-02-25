package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCommerceEndResponse extends ServerPacket {
	// UserCommerceEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCommerceEnd;
	}
	public UserCommerceEndResponse(){
	}
};

