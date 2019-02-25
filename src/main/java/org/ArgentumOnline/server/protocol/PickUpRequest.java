package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PickUpRequest extends ClientPacket {
	// PickUp
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PickUp;
	}
	public PickUpRequest(){
	}
};

