package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyJoinRequest extends ClientPacket {
	// PartyJoin
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyJoin;
	}
	public PartyJoinRequest(){
	}
};

