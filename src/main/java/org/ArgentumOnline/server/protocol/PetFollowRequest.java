package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PetFollowRequest extends ClientPacket {
	// PetFollow
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PetFollow;
	}
	public PetFollowRequest(){
	}
};

