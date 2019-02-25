package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NavigateToggleResponse extends ServerPacket {
	// NavigateToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NavigateToggle;
	}
	public NavigateToggleResponse(){
	}
	public static NavigateToggleResponse decode(ByteBuf in) {    
		try {                                   
			return new NavigateToggleResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

