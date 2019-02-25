package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreateNPCWithRespawnRequest extends ClientPacket {
	// CreateNPCWithRespawn,i:npcIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNPCWithRespawn;
	}
	public short npcIndex;
	public CreateNPCWithRespawnRequest(short npcIndex){
		this.npcIndex = npcIndex;
	}
	public static CreateNPCWithRespawnRequest decode(ByteBuf in) {    
		try {                                   
			short npcIndex = readShort(in);
			return new CreateNPCWithRespawnRequest(npcIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

