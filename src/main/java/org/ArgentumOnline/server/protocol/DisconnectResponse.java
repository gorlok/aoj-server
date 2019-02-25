package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DisconnectResponse extends ServerPacket {
	// Disconnect
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Disconnect;
	}
	public DisconnectResponse(){
	}
};

