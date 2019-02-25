package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreateItemRequest extends ClientPacket {
	// CreateItem,i:objectIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateItem;
	}
	public short objectIndex;
	public CreateItemRequest(short objectIndex){
		this.objectIndex = objectIndex;
	}
};

