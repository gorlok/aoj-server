package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SetTriggerRequest extends ClientPacket {
	// SetTrigger
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetTrigger;
	}
	public SetTriggerRequest(){
	}
	public static SetTriggerRequest decode(ByteBuf in) {    
		try {                                   
			return new SetTriggerRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

