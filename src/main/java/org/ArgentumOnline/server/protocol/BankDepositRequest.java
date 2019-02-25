package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankDepositRequest extends ClientPacket {
	// BankDeposit,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankDeposit;
	}
	public byte slot;
	public short amount;
	public BankDepositRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
};

