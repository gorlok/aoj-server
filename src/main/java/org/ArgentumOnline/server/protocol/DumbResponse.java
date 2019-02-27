package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DumbResponse extends ServerPacket {
	// Dumb
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Dumb;
	}
	public DumbResponse(){
	}
	public static DumbResponse decode(ByteBuf in) {    
		try {                                   
			return new DumbResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

