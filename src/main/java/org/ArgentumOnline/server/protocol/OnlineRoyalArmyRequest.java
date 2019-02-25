package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OnlineRoyalArmyRequest extends ClientPacket {
	// OnlineRoyalArmy
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineRoyalArmy;
	}
	public OnlineRoyalArmyRequest(){
	}
};

