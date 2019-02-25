package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestPositionUpdateRequest extends ClientPacket {
	// RequestPositionUpdate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestPositionUpdate;
	}
	public RequestPositionUpdateRequest(){
	}
};

