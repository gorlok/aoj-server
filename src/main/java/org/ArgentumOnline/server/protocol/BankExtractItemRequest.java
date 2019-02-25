package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static BankExtractItemRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short amount = readShort(in);
			return new BankExtractItemRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

