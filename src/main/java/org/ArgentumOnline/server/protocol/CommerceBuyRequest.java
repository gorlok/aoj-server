package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommerceBuyRequest extends ClientPacket {
	// CommerceBuy,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceBuy;
	}
	public byte slot;
	public short amount;
	public CommerceBuyRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
};

