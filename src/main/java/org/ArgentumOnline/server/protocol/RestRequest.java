package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RestRequest extends ClientPacket {
	// Rest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Rest;
	}
	public RestRequest(){
	}
	public static RestRequest decode(ByteBuf in) {    
		try {                                   
			return new RestRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

