package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OnlineChaosLegionRequest extends ClientPacket {
	// OnlineChaosLegion
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineChaosLegion;
	}
	public OnlineChaosLegionRequest(){
	}
	public static OnlineChaosLegionRequest decode(ByteBuf in) {    
		try {                                   
			return new OnlineChaosLegionRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

