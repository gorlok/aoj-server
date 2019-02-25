package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceEndRequest extends ClientPacket {
	// UserCommerceEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceEnd;
	}
	public UserCommerceEndRequest(){
	}
	public static UserCommerceEndRequest decode(ByteBuf in) {    
		try {                                   
			return new UserCommerceEndRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

