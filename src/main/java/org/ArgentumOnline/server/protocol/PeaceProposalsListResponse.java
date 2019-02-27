package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PeaceProposalsListResponse extends ServerPacket {
	// PeaceProposalsList,s:guildsList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PeaceProposalsList;
	}
	public String guildsList;
	public PeaceProposalsListResponse(String guildsList){
		this.guildsList = guildsList;
	}
	public static PeaceProposalsListResponse decode(ByteBuf in) {    
		try {                                   
			String guildsList = readStr(in);
			return new PeaceProposalsListResponse(guildsList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,guildsList);
	}
};

