package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMOTDRequest extends ClientPacket {
	// ChangeMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMOTD;
	}
	public ChangeMOTDRequest(){
	}
};

