package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestPositionUpdateRequest extends ClientPacket {
	// RequestPositionUpdate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestPositionUpdate;
	}
	public RequestPositionUpdateRequest(){
	}
	public static RequestPositionUpdateRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestPositionUpdateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

