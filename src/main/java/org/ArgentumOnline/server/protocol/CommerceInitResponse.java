package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CommerceInitResponse extends ServerPacket {
	// CommerceInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CommerceInit;
	}
	public CommerceInitResponse(){
	}
	public static CommerceInitResponse decode(ByteBuf in) {    
		try {                                   
			return new CommerceInitResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

