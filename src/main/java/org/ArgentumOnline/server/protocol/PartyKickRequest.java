package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyKickRequest extends ClientPacket {
	// PartyKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyKick;
	}
	public String userName;
	public PartyKickRequest(String userName){
		this.userName = userName;
	}
};

