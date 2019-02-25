package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChaosLegionKickRequest extends ClientPacket {
	// ChaosLegionKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosLegionKick;
	}
	public String userName;
	public ChaosLegionKickRequest(String userName){
		this.userName = userName;
	}
};

