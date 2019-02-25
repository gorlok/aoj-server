package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OnlineGMRequest extends ClientPacket {
	// OnlineGM
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineGM;
	}
	public OnlineGMRequest(){
	}
	public static OnlineGMRequest decode(ByteBuf in) {    
		try {                                   
			return new OnlineGMRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

