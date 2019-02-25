package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SpawnListRequestRequest extends ClientPacket {
	// SpawnListRequest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpawnListRequest;
	}
	public SpawnListRequestRequest(){
	}
};

