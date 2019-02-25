package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SOSShowListRequest extends ClientPacket {
	// SOSShowList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SOSShowList;
	}
	public SOSShowListRequest(){
	}
};

