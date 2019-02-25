package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class InvisibleRequest extends ClientPacket {
	// Invisible
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Invisible;
	}
	public InvisibleRequest(){
	}
};

