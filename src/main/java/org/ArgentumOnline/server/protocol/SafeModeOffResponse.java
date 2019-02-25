package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SafeModeOffResponse extends ServerPacket {
	// SafeModeOff
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SafeModeOff;
	}
	public SafeModeOffResponse(){
	}
	public static SafeModeOffResponse decode(ByteBuf in) {    
		try {                                   
			return new SafeModeOffResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

