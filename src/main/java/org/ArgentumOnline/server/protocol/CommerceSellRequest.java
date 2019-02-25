package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceSellRequest extends ClientPacket {
	// CommerceSell,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceSell;
	}
	public byte slot;
	public short amount;
	public CommerceSellRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
};

