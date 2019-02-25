package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UseItemRequest extends ClientPacket {
	// UseItem,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UseItem;
	}
	public byte slot;
	public UseItemRequest(byte slot){
		this.slot = slot;
	}
	public static UseItemRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			return new UseItemRequest(slot);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

