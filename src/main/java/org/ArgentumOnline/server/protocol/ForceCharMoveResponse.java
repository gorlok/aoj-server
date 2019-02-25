package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ForceCharMoveResponse extends ServerPacket {
	// ForceCharMove,b:heading
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ForceCharMove;
	}
	public byte heading;
	public ForceCharMoveResponse(byte heading){
		this.heading = heading;
	}
};

