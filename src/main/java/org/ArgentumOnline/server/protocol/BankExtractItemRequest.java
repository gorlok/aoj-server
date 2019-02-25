package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankExtractItemRequest extends ClientPacket {
	// BankExtractItem,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankExtractItem;
	}
	public byte slot;
	public short amount;
	public BankExtractItemRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
};

