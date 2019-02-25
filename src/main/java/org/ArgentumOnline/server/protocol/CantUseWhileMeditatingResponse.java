package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CantUseWhileMeditatingResponse extends ServerPacket {
	// CantUseWhileMeditating
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CantUseWhileMeditating;
	}
	public CantUseWhileMeditatingResponse(){
	}
};

