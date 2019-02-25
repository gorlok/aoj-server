package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class KillNPCRequest extends ClientPacket {
	// KillNPC
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillNPC;
	}
	public KillNPCRequest(){
	}
	public static KillNPCRequest decode(ByteBuf in) {    
		try {                                   
			return new KillNPCRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

