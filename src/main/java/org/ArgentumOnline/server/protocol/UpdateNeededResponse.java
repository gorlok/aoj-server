package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateNeededResponse extends ServerPacket {
	// UpdateNeeded
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateNeeded;
	}
	public UpdateNeededResponse(){
	}
};

