package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceOkRequest extends ClientPacket {
	// UserCommerceOk
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceOk;
	}
	public UserCommerceOkRequest(){
	}
	public static UserCommerceOkRequest decode(ByteBuf in) {    
		try {                                   
			return new UserCommerceOkRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

