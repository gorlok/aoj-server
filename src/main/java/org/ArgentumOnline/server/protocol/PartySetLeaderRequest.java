package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartySetLeaderRequest extends ClientPacket {
	// PartySetLeader,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartySetLeader;
	}
	public String userName;
	public PartySetLeaderRequest(String userName){
		this.userName = userName;
	}
};

