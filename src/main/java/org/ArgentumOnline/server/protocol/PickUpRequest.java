package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PickUpRequest extends ClientPacket {
	// PickUp
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PickUp;
	}
	public PickUpRequest(){
	}
	public static PickUpRequest decode(ByteBuf in) {    
		try {                                   
			return new PickUpRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

