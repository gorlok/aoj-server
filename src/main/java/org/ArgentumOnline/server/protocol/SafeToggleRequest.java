package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SafeToggleRequest extends ClientPacket {
	// SafeToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SafeToggle;
	}
	public SafeToggleRequest(){
	}
	public static SafeToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new SafeToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

