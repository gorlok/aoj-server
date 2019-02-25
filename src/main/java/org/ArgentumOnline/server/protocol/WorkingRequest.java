package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WorkingRequest extends ClientPacket {
	// Working
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Working;
	}
	public WorkingRequest(){
	}
	public static WorkingRequest decode(ByteBuf in) {    
		try {                                   
			return new WorkingRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

