package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static BankExtractGoldRequest decode(ByteBuf in) {    
		try {                                   
			int amount = readInt(in);
			return new BankExtractGoldRequest(amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

