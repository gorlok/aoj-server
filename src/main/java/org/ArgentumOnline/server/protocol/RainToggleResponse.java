package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RainToggleResponse extends ServerPacket {
	// RainToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RainToggle;
	}
	public RainToggleResponse(){
	}
	public static RainToggleResponse decode(ByteBuf in) {    
		try {                                   
			return new RainToggleResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

