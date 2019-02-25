package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static BankDepositRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short amount = readShort(in);
			return new BankDepositRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

