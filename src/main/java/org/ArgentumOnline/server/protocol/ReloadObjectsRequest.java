package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ReloadObjectsRequest extends ClientPacket {
	// ReloadObjects
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadObjects;
	}
	public ReloadObjectsRequest(){
	}
};

