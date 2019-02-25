package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AlianceProposalsListResponse extends ServerPacket {
	// AlianceProposalsList,s:guildsList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.AlianceProposalsList;
	}
	public String guildsList;
	public AlianceProposalsListResponse(String guildsList){
		this.guildsList = guildsList;
	}
	public static AlianceProposalsListResponse decode(ByteBuf in) {    
		try {                                   
			String guildsList = readStr(in);
			return new AlianceProposalsListResponse(guildsList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

