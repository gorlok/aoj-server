package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DestroyItemsRequest extends ClientPacket {
	// DestroyItems
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DestroyItems;
	}
	public DestroyItemsRequest(){
	}
	public static DestroyItemsRequest decode(ByteBuf in) {    
		try {                                   
			return new DestroyItemsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

