package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class KillAllNearbyNPCsRequest extends ClientPacket {
	// KillAllNearbyNPCs
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillAllNearbyNPCs;
	}
	public KillAllNearbyNPCsRequest(){
	}
	public static KillAllNearbyNPCsRequest decode(ByteBuf in) {    
		try {                                   
			return new KillAllNearbyNPCsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

