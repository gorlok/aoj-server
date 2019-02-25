package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NightRequest extends ClientPacket {
	// Night
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Night;
	}
	public NightRequest(){
	}
};

