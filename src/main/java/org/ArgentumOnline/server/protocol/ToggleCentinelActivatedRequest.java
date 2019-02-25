package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ToggleCentinelActivatedRequest extends ClientPacket {
	// ToggleCentinelActivated
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ToggleCentinelActivated;
	}
	public ToggleCentinelActivatedRequest(){
	}
	public static ToggleCentinelActivatedRequest decode(ByteBuf in) {    
		try {                                   
			return new ToggleCentinelActivatedRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

