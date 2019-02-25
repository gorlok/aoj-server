package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static CommerceSellRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short amount = readShort(in);
			return new CommerceSellRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

