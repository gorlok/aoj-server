package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

