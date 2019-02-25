package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCommerceRejectRequest extends ClientPacket {
	// UserCommerceReject
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceReject;
	}
	public UserCommerceRejectRequest(){
	}
};

