package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestUserListRequest extends ClientPacket {
	// RequestUserList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestUserList;
	}
	public RequestUserListRequest(){
	}
};

