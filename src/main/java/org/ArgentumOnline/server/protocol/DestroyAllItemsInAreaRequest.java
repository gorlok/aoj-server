package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DestroyAllItemsInAreaRequest extends ClientPacket {
	// DestroyAllItemsInArea
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DestroyAllItemsInArea;
	}
	public DestroyAllItemsInAreaRequest(){
	}
	public static DestroyAllItemsInAreaRequest decode(ByteBuf in) {    
		try {                                   
			return new DestroyAllItemsInAreaRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

