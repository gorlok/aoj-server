package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class HidingRequest extends ClientPacket {
	// Hiding
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Hiding;
	}
	public HidingRequest(){
	}
	public static HidingRequest decode(ByteBuf in) {    
		try {                                   
			return new HidingRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

