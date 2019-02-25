package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MakeDumbNoMoreRequest extends ClientPacket {
	// MakeDumbNoMore,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MakeDumbNoMore;
	}
	public String userName;
	public MakeDumbNoMoreRequest(String userName){
		this.userName = userName;
	}
};

