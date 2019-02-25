package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MakeDumbRequest extends ClientPacket {
	// MakeDumb,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MakeDumb;
	}
	public String userName;
	public MakeDumbRequest(String userName){
		this.userName = userName;
	}
};

