package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SafeModeOnResponse extends ServerPacket {
	// SafeModeOn
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SafeModeOn;
	}
	public SafeModeOnResponse(){
	}
	public static SafeModeOnResponse decode(ByteBuf in) {    
		try {                                   
			return new SafeModeOnResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

