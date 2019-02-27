package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ParalizeOKResponse extends ServerPacket {
	// ParalizeOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ParalizeOK;
	}
	public ParalizeOKResponse(){
	}
	public static ParalizeOKResponse decode(ByteBuf in) {    
		try {                                   
			return new ParalizeOKResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

