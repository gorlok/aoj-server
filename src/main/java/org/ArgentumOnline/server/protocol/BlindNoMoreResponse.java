package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlindNoMoreResponse extends ServerPacket {
	// BlindNoMore
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlindNoMore;
	}
	public BlindNoMoreResponse(){
	}
};

