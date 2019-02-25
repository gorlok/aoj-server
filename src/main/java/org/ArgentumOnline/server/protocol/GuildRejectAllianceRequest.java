package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRejectAllianceRequest extends ClientPacket {
	// GuildRejectAlliance,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectAlliance;
	}
	public String guild;
	public GuildRejectAllianceRequest(String guild){
		this.guild = guild;
	}
};

