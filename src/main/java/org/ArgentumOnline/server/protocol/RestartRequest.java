package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RestartRequest extends ClientPacket {
	// Restart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Restart;
	}
	public RestartRequest(){
	}
	public static RestartRequest decode(ByteBuf in) {    
		try {                                   
			return new RestartRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

