package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UnbanCharRequest extends ClientPacket {
	// UnbanChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UnbanChar;
	}
	public String userName;
	public UnbanCharRequest(String userName){
		this.userName = userName;
	}
};

