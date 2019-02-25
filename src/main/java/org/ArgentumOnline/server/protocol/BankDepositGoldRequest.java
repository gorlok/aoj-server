package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static BankDepositGoldRequest decode(ByteBuf in) {    
		try {                                   
			int amount = readInt(in);
			return new BankDepositGoldRequest(amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

