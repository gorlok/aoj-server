package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ItemsInTheFloorRequest extends ClientPacket {
	// ItemsInTheFloor
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ItemsInTheFloor;
	}
	public ItemsInTheFloorRequest(){
	}
};

