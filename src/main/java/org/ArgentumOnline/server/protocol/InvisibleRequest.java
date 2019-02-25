package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class InvisibleRequest extends ClientPacket {
	// Invisible
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Invisible;
	}
	public InvisibleRequest(){
	}
	public static InvisibleRequest decode(ByteBuf in) {    
		try {                                   
			return new InvisibleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

