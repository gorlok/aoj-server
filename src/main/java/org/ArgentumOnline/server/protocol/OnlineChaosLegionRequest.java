package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OnlineChaosLegionRequest extends ClientPacket {
	// OnlineChaosLegion
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineChaosLegion;
	}
	public OnlineChaosLegionRequest(){
	}
};

