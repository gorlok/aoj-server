package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class KillNPCNoRespawnRequest extends ClientPacket {
	// KillNPCNoRespawn
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillNPCNoRespawn;
	}
	public KillNPCNoRespawnRequest(){
	}
	public static KillNPCNoRespawnRequest decode(ByteBuf in) {    
		try {                                   
			return new KillNPCNoRespawnRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

