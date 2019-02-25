package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CleanWorldRequest extends ClientPacket {
	// CleanWorld
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CleanWorld;
	}
	public CleanWorldRequest(){
	}
};

