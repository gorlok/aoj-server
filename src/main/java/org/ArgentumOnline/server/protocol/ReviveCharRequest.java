package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ReviveCharRequest extends ClientPacket {
	// ReviveChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReviveChar;
	}
	public String userName;
	public ReviveCharRequest(String userName){
		this.userName = userName;
	}
};

