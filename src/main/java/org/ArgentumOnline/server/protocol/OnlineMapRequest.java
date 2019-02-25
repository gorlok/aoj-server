package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OnlineMapRequest extends ClientPacket {
	// OnlineMap,i:map
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineMap;
	}
	public short map;
	public OnlineMapRequest(short map){
		this.map = map;
	}
};

