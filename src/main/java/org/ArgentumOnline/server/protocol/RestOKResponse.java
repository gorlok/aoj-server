package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RestOKResponse extends ServerPacket {
	// RestOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RestOK;
	}
	public RestOKResponse(){
	}
};

