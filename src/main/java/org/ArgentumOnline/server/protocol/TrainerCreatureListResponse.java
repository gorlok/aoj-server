package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TrainerCreatureListResponse extends ServerPacket {
	// TrainerCreatureList,s:npcList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.TrainerCreatureList;
	}
	public String npcList;
	public TrainerCreatureListResponse(String npcList){
		this.npcList = npcList;
	}
	public static TrainerCreatureListResponse decode(ByteBuf in) {    
		try {                                   
			String npcList = readStr(in);
			return new TrainerCreatureListResponse(npcList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,npcList);
	}
};

