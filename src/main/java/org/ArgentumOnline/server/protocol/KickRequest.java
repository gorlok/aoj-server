package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class KickRequest extends ClientPacket {
	// Kick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Kick;
	}
	public String userName;
	public KickRequest(String userName){
		this.userName = userName;
	}
};

