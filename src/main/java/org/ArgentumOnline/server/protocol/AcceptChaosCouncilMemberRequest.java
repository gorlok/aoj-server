package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AcceptChaosCouncilMemberRequest extends ClientPacket {
	// AcceptChaosCouncilMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AcceptChaosCouncilMember;
	}
	public String userName;
	public AcceptChaosCouncilMemberRequest(String userName){
		this.userName = userName;
	}
};

