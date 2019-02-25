package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeHeadingRequest extends ClientPacket {
	// ChangeHeading,b:heading
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeHeading;
	}
	public byte heading;
	public ChangeHeadingRequest(byte heading){
		this.heading = heading;
	}
};

