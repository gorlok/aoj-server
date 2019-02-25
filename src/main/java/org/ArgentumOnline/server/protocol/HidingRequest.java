package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class HidingRequest extends ClientPacket {
	// Hiding
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Hiding;
	}
	public HidingRequest(){
	}
};

