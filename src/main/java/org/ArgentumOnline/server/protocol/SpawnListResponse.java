package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SpawnListResponse extends ServerPacket {
	// SpawnList,s:npcNamesList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SpawnList;
	}
	public String npcNamesList;
	public SpawnListResponse(String npcNamesList){
		this.npcNamesList = npcNamesList;
	}
	public static SpawnListResponse decode(ByteBuf in) {    
		try {                                   
			String npcNamesList = readStr(in);
			return new SpawnListResponse(npcNamesList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

