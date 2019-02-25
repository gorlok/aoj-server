package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildAllianceDetailsRequest extends ClientPacket {
	// GuildAllianceDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAllianceDetails;
	}
	public String guild;
	public GuildAllianceDetailsRequest(String guild){
		this.guild = guild;
	}
};

