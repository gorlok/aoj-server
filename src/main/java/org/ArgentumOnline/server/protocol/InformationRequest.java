package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class InformationRequest extends ClientPacket {
	// Information
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Information;
	}
	public InformationRequest(){
	}
};

