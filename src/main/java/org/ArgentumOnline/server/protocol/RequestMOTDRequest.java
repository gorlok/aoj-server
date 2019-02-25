package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestMOTDRequest extends ClientPacket {
	// RequestMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestMOTD;
	}
	public RequestMOTDRequest(){
	}
};

