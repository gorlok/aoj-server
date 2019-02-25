package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ThrowDicesRequest extends ClientPacket {
	// ThrowDices
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ThrowDices;
	}
	public ThrowDicesRequest(){
	}
	public static ThrowDicesRequest decode(ByteBuf in) {    
		try {                                   
			return new ThrowDicesRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

