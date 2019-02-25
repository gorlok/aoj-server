package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestAtributesRequest extends ClientPacket {
	// RequestAtributes
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestAtributes;
	}
	public RequestAtributesRequest(){
	}
};

