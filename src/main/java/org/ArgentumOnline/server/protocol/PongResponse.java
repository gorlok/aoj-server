package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PongResponse extends ServerPacket {
	// Pong
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Pong;
	}
	public PongResponse(){
	}
	public static PongResponse decode(ByteBuf in) {    
		try {                                   
			return new PongResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

