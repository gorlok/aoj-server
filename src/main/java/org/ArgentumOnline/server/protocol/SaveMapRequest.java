package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SaveMapRequest extends ClientPacket {
	// SaveMap
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SaveMap;
	}
	public SaveMapRequest(){
	}
};

