package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCommerceOkRequest extends ClientPacket {
	// UserCommerceOk
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceOk;
	}
	public UserCommerceOkRequest(){
	}
};

