package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WhereRequest extends ClientPacket {
	// Where,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Where;
	}
	public String userName;
	public WhereRequest(String userName){
		this.userName = userName;
	}
};

