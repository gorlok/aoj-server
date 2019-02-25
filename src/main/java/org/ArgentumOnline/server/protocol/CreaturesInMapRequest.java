package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreaturesInMapRequest extends ClientPacket {
	// CreaturesInMap,i:map
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreaturesInMap;
	}
	public short map;
	public CreaturesInMapRequest(short map){
		this.map = map;
	}
};

