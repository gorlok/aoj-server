package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankExtractGoldRequest extends ClientPacket {
	// BankExtractGold,l:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankExtractGold;
	}
	public int amount;
	public BankExtractGoldRequest(int amount){
		this.amount = amount;
	}
};

