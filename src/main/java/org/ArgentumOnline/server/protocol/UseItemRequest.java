package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UseItemRequest extends ClientPacket {
	// UseItem,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UseItem;
	}
	public byte slot;
	public UseItemRequest(byte slot){
		this.slot = slot;
	}
};

