package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreateItemRequest extends ClientPacket {
	// CreateItem,i:objectIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateItem;
	}
	public short objectIndex;
	public CreateItemRequest(short objectIndex){
		this.objectIndex = objectIndex;
	}
	public static CreateItemRequest decode(ByteBuf in) {    
		try {                                   
			short objectIndex = readShort(in);
			return new CreateItemRequest(objectIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

