package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DropRequest extends ClientPacket {
	// Drop,b:slot,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Drop;
	}
	public byte slot;
	public short amount;
	public DropRequest(byte slot,short amount){
		this.slot = slot;
		this.amount = amount;
	}
	public static DropRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short amount = readShort(in);
			return new DropRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

