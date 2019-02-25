package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceRejectRequest extends ClientPacket {
	// UserCommerceReject
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceReject;
	}
	public UserCommerceRejectRequest(){
	}
	public static UserCommerceRejectRequest decode(ByteBuf in) {    
		try {                                   
			return new UserCommerceRejectRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

