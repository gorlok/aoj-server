package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SummonCharRequest extends ClientPacket {
	// SummonChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SummonChar;
	}
	public String userName;
	public SummonCharRequest(String userName){
		this.userName = userName;
	}
};

