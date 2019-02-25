package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GoNearbyRequest extends ClientPacket {
	// GoNearby,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GoNearby;
	}
	public String userName;
	public GoNearbyRequest(String userName){
		this.userName = userName;
	}
};

