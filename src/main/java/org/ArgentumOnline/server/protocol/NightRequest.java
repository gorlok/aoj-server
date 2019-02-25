package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NightRequest extends ClientPacket {
	// Night
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Night;
	}
	public NightRequest(){
	}
	public static NightRequest decode(ByteBuf in) {    
		try {                                   
			return new NightRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

