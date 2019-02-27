package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlindResponse extends ServerPacket {
	// Blind
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Blind;
	}
	public BlindResponse(){
	}
	public static BlindResponse decode(ByteBuf in) {    
		try {                                   
			return new BlindResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

