package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestFameRequest extends ClientPacket {
	// RequestFame
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestFame;
	}
	public RequestFameRequest(){
	}
	public static RequestFameRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestFameRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

