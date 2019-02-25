package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DestroyAllItemsInAreaRequest extends ClientPacket {
	// DestroyAllItemsInArea
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DestroyAllItemsInArea;
	}
	public DestroyAllItemsInAreaRequest(){
	}
};

