package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ParalizeOKResponse extends ServerPacket {
	// ParalizeOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ParalizeOK;
	}
	public ParalizeOKResponse(){
	}
};

