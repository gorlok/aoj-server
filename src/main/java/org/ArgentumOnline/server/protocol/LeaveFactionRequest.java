package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LeaveFactionRequest extends ClientPacket {
	// LeaveFaction
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LeaveFaction;
	}
	public LeaveFactionRequest(){
	}
};

