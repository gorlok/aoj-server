package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CommerceEndRequest extends ClientPacket {
	// CommerceEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CommerceEnd;
	}
	public CommerceEndRequest(){
	}
	public static CommerceEndRequest decode(ByteBuf in) {    
		try {                                   
			return new CommerceEndRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

