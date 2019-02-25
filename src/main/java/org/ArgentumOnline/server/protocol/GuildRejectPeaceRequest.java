package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRejectPeaceRequest extends ClientPacket {
	// GuildRejectPeace,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectPeace;
	}
	public String guild;
	public GuildRejectPeaceRequest(String guild){
		this.guild = guild;
	}
};

