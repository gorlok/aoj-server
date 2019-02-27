package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceInitResponse extends ServerPacket {
	// UserCommerceInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCommerceInit;
	}
	public UserCommerceInitResponse(){
	}
	public static UserCommerceInitResponse decode(ByteBuf in) {    
		try {                                   
			return new UserCommerceInitResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

