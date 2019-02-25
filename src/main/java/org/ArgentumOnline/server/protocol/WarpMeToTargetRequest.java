package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WarpMeToTargetRequest extends ClientPacket {
	// WarpMeToTarget
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarpMeToTarget;
	}
	public WarpMeToTargetRequest(){
	}
	public static WarpMeToTargetRequest decode(ByteBuf in) {    
		try {                                   
			return new WarpMeToTargetRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

