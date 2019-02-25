package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestAtributesRequest extends ClientPacket {
	// RequestAtributes
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestAtributes;
	}
	public RequestAtributesRequest(){
	}
	public static RequestAtributesRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestAtributesRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

