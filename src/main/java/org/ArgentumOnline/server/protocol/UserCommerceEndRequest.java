package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCommerceEndRequest extends ClientPacket {
	// UserCommerceEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceEnd;
	}
	public UserCommerceEndRequest(){
	}
};

