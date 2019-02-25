package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyAcceptMemberRequest extends ClientPacket {
	// PartyAcceptMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyAcceptMember;
	}
	public String userName;
	public PartyAcceptMemberRequest(String userName){
		this.userName = userName;
	}
};

