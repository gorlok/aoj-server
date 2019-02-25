package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceEndResponse extends ServerPacket {
	// UserCommerceEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCommerceEnd;
	}
	public UserCommerceEndResponse(){
	}
	public static UserCommerceEndResponse decode(ByteBuf in) {    
		try {                                   
			return new UserCommerceEndResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

