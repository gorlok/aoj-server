package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RainToggleRequest extends ClientPacket {
	// RainToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RainToggle;
	}
	public RainToggleRequest(){
	}
	public static RainToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new RainToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

