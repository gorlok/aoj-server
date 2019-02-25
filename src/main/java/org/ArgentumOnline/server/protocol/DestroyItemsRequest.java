package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DestroyItemsRequest extends ClientPacket {
	// DestroyItems
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DestroyItems;
	}
	public DestroyItemsRequest(){
	}
};

