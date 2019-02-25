package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildAcceptAllianceRequest extends ClientPacket {
	// GuildAcceptAlliance,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptAlliance;
	}
	public String guild;
	public GuildAcceptAllianceRequest(String guild){
		this.guild = guild;
	}
};

