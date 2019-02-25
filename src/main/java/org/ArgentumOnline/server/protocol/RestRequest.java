package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RestRequest extends ClientPacket {
	// Rest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Rest;
	}
	public RestRequest(){
	}
};

