package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RestartRequest extends ClientPacket {
	// Restart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Restart;
	}
	public RestartRequest(){
	}
};

