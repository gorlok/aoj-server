package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DropRequest extends ClientPacket {
	// Drop,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Drop;
	}
	public byte slot;
	public short amount;
	public DropRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
};

