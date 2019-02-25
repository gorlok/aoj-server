package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

