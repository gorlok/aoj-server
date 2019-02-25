package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ForgiveRequest extends ClientPacket {
	// Forgive,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Forgive;
	}
	public String userName;
	public ForgiveRequest(String userName){
		this.userName = userName;
	}
};

