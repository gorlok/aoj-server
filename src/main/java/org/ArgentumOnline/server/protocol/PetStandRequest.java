package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PetStandRequest extends ClientPacket {
	// PetStand
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PetStand;
	}
	public PetStandRequest(){
	}
};

