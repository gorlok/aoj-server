package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ItemsInTheFloorRequest extends ClientPacket {
	// ItemsInTheFloor
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ItemsInTheFloor;
	}
	public ItemsInTheFloorRequest(){
	}
	public static ItemsInTheFloorRequest decode(ByteBuf in) {    
		try {                                   
			return new ItemsInTheFloorRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

