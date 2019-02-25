package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyCreateRequest extends ClientPacket {
	// PartyCreate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyCreate;
	}
	public PartyCreateRequest(){
	}
};

