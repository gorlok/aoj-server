package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestFameRequest extends ClientPacket {
	// RequestFame
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestFame;
	}
	public RequestFameRequest(){
	}
};

