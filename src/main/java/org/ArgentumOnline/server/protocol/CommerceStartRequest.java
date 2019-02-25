package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CommerceStartRequest extends ClientPacket {
	// CommerceStart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceStart;
	}
	public CommerceStartRequest(){
	}
	public static CommerceStartRequest decode(ByteBuf in) {    
		try {                                   
			return new CommerceStartRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

