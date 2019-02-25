package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class LeaveFactionRequest extends ClientPacket {
	// LeaveFaction
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LeaveFaction;
	}
	public LeaveFactionRequest(){
	}
	public static LeaveFactionRequest decode(ByteBuf in) {    
		try {                                   
			return new LeaveFactionRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

