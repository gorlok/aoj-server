package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class IgnoredRequest extends ClientPacket {
	// Ignored
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Ignored;
	}
	public IgnoredRequest(){
	}
};

