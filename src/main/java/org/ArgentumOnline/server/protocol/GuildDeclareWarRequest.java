package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildDeclareWarRequest extends ClientPacket {
	// GuildDeclareWar,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildDeclareWar;
	}
	public String guild;
	public GuildDeclareWarRequest(String guild){
		this.guild = guild;
	}
};

