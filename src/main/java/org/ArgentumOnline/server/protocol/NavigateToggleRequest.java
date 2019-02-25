package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NavigateToggleRequest extends ClientPacket {
	// NavigateToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NavigateToggle;
	}
	public NavigateToggleRequest(){
	}
	public static NavigateToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new NavigateToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

