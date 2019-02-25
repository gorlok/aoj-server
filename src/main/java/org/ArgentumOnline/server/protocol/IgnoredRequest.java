package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class IgnoredRequest extends ClientPacket {
	// Ignored
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Ignored;
	}
	public IgnoredRequest(){
	}
	public static IgnoredRequest decode(ByteBuf in) {    
		try {                                   
			return new IgnoredRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

