package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BannedIPListRequest extends ClientPacket {
	// BannedIPList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BannedIPList;
	}
	public BannedIPListRequest(){
	}
};

