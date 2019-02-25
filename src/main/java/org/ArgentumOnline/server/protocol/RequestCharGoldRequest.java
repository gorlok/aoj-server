package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharGoldRequest extends ClientPacket {
	// RequestCharGold,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharGold;
	}
	public String userName;
	public RequestCharGoldRequest(String userName){
		this.userName = userName;
	}
};

