package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SpawnCreatureRequest extends ClientPacket {
	// SpawnCreature,i:npc
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpawnCreature;
	}
	public short npc;
	public SpawnCreatureRequest(short npc){
		this.npc = npc;
	}
	public static SpawnCreatureRequest decode(ByteBuf in) {    
		try {                                   
			short npc = readShort(in);
			return new SpawnCreatureRequest(npc);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

