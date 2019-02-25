package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyOnlineRequest extends ClientPacket {
	// PartyOnline
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyOnline;
	}
	public PartyOnlineRequest(){
	}
};

