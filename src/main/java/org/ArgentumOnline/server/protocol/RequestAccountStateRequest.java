package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestAccountStateRequest extends ClientPacket {
	// RequestAccountState
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestAccountState;
	}
	public RequestAccountStateRequest(){
	}
	public static RequestAccountStateRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestAccountStateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

