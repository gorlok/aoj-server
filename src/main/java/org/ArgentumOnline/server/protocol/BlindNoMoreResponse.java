package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlindNoMoreResponse extends ServerPacket {
	// BlindNoMore
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlindNoMore;
	}
	public BlindNoMoreResponse(){
	}
	public static BlindNoMoreResponse decode(ByteBuf in) {    
		try {                                   
			return new BlindNoMoreResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

