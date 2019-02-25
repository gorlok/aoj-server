package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharInfoRequest extends ClientPacket {
	// RequestCharInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharInfo;
	}
	public String userName;
	public RequestCharInfoRequest(String userName){
		this.userName = userName;
	}
};

