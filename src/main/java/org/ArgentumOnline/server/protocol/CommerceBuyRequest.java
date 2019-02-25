package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static CommerceBuyRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short amount = readShort(in);
			return new CommerceBuyRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

