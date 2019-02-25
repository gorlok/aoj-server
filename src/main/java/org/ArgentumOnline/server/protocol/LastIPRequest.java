package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LastIPRequest extends ClientPacket {
	// LastIP,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LastIP;
	}
	public String userName;
	public LastIPRequest(String userName){
		this.userName = userName;
	}
};

