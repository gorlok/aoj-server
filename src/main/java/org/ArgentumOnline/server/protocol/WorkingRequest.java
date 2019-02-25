package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WorkingRequest extends ClientPacket {
	// Working
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Working;
	}
	public WorkingRequest(){
	}
};

