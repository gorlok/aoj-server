package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharMailRequest extends ClientPacket {
	// RequestCharMail,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharMail;
	}
	public String userName;
	public RequestCharMailRequest(String userName){
		this.userName = userName;
	}
};

