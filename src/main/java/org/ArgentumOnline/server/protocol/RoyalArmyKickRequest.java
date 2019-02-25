package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RoyalArmyKickRequest extends ClientPacket {
	// RoyalArmyKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoyalArmyKick;
	}
	public String userName;
	public RoyalArmyKickRequest(String userName){
		this.userName = userName;
	}
};

