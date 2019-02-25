package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankDepositGoldRequest extends ClientPacket {
	// BankDepositGold,l:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankDepositGold;
	}
	public int amount;
	public BankDepositGoldRequest(int amount){
		this.amount = amount;
	}
};

