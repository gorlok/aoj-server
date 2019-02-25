package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GoToCharRequest extends ClientPacket {
	// GoToChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GoToChar;
	}
	public String userName;
	public GoToCharRequest(String userName){
		this.userName = userName;
	}
};

