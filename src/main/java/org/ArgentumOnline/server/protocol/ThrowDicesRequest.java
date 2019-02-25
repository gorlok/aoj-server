package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ThrowDicesRequest extends ClientPacket {
	// ThrowDices
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ThrowDices;
	}
	public ThrowDicesRequest(){
	}
};

