package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserSwingResponse extends ServerPacket {
	// UserSwing
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserSwing;
	}
	public UserSwingResponse(){
	}
	public static UserSwingResponse decode(ByteBuf in) {    
		try {                                   
			return new UserSwingResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

