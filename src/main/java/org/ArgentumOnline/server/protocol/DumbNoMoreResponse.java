package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DumbNoMoreResponse extends ServerPacket {
	// DumbNoMore
	@Override
	public ServerPacketID id() {
		return ServerPacketID.DumbNoMore;
	}
	public DumbNoMoreResponse(){
	}
};

