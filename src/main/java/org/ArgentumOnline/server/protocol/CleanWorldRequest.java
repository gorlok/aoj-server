package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CleanWorldRequest extends ClientPacket {
	// CleanWorld
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CleanWorld;
	}
	public CleanWorldRequest(){
	}
	public static CleanWorldRequest decode(ByteBuf in) {    
		try {                                   
			return new CleanWorldRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

