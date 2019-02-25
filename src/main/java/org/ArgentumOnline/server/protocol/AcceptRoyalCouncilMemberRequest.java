package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AcceptRoyalCouncilMemberRequest extends ClientPacket {
	// AcceptRoyalCouncilMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AcceptRoyalCouncilMember;
	}
	public String userName;
	public AcceptRoyalCouncilMemberRequest(String userName){
		this.userName = userName;
	}
};

