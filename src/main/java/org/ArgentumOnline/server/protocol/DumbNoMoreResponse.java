package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DumbNoMoreResponse extends ServerPacket {
	// DumbNoMore
	@Override
	public ServerPacketID id() {
		return ServerPacketID.DumbNoMore;
	}
	public DumbNoMoreResponse(){
	}
	public static DumbNoMoreResponse decode(ByteBuf in) {    
		try {                                   
			return new DumbNoMoreResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

