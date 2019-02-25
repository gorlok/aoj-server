package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRequestDetailsRequest extends ClientPacket {
	// GuildRequestDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestDetails;
	}
	public String guild;
	public GuildRequestDetailsRequest(String guild){
		this.guild = guild;
	}
};

