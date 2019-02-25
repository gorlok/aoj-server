package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WalkRequest extends ClientPacket {
	// Walk,b:heading
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Walk;
	}
	public byte heading;
	public WalkRequest(byte heading){
		this.heading = heading;
	}
};

