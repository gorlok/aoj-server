package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestAccountStateRequest extends ClientPacket {
	// RequestAccountState
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestAccountState;
	}
	public RequestAccountStateRequest(){
	}
};

