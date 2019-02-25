package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CommerceEndResponse extends ServerPacket {
	// CommerceEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CommerceEnd;
	}
	public CommerceEndResponse(){
	}
	public static CommerceEndResponse decode(ByteBuf in) {    
		try {                                   
			return new CommerceEndResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

