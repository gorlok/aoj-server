package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OnlineGMRequest extends ClientPacket {
	// OnlineGM
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineGM;
	}
	public OnlineGMRequest(){
	}
};

