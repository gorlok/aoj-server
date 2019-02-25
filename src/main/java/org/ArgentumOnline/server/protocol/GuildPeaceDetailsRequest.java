package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildPeaceDetailsRequest extends ClientPacket {
	// GuildPeaceDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildPeaceDetails;
	}
	public String guild;
	public GuildPeaceDetailsRequest(String guild){
		this.guild = guild;
	}
};

