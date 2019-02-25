package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ToggleCentinelActivatedRequest extends ClientPacket {
	// ToggleCentinelActivated
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ToggleCentinelActivated;
	}
	public ToggleCentinelActivatedRequest(){
	}
};

