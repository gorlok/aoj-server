package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NobilityLostResponse extends ServerPacket {
	// NobilityLost
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NobilityLost;
	}
	public NobilityLostResponse(){
	}
};

