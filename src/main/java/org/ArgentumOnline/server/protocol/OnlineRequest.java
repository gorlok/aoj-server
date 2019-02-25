package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OnlineRequest extends ClientPacket {
	// Online
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Online;
	}
	public OnlineRequest(){
	}
	public static OnlineRequest decode(ByteBuf in) {    
		try {                                   
			return new OnlineRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

