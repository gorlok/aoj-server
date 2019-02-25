package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SilenceRequest extends ClientPacket {
	// Silence,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Silence;
	}
	public String userName;
	public SilenceRequest(String userName){
		this.userName = userName;
	}
};

