package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WarpMeToTargetRequest extends ClientPacket {
	// WarpMeToTarget
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarpMeToTarget;
	}
	public WarpMeToTargetRequest(){
	}
};

