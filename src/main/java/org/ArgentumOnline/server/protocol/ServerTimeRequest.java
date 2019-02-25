package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ServerTimeRequest extends ClientPacket {
	// ServerTime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerTime;
	}
	public ServerTimeRequest(){
	}
};

