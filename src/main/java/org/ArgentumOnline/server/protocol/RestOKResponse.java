package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RestOKResponse extends ServerPacket {
	// RestOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RestOK;
	}
	public RestOKResponse(){
	}
	public static RestOKResponse decode(ByteBuf in) {    
		try {                                   
			return new RestOKResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

