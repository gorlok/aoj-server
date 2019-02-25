package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GMRequestRequest extends ClientPacket {
	// GMRequest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMRequest;
	}
	public GMRequestRequest(){
	}
	public static GMRequestRequest decode(ByteBuf in) {    
		try {                                   
			return new GMRequestRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

