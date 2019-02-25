package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CouncilKickRequest extends ClientPacket {
	// CouncilKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CouncilKick;
	}
	public String userName;
	public CouncilKickRequest(String userName){
		this.userName = userName;
	}
};

