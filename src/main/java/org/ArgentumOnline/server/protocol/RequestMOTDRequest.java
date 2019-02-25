package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestMOTDRequest extends ClientPacket {
	// RequestMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestMOTD;
	}
	public RequestMOTDRequest(){
	}
	public static RequestMOTDRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestMOTDRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

