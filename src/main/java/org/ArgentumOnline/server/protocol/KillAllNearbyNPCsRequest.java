package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class KillAllNearbyNPCsRequest extends ClientPacket {
	// KillAllNearbyNPCs
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillAllNearbyNPCs;
	}
	public KillAllNearbyNPCsRequest(){
	}
};

