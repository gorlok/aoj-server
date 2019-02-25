package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AskTriggerRequest extends ClientPacket {
	// AskTrigger
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AskTrigger;
	}
	public AskTriggerRequest(){
	}
	public static AskTriggerRequest decode(ByteBuf in) {    
		try {                                   
			return new AskTriggerRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

