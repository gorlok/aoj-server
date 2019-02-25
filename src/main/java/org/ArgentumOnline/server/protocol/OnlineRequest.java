package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OnlineRequest extends ClientPacket {
	// Online
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Online;
	}
	public OnlineRequest(){
	}
};

