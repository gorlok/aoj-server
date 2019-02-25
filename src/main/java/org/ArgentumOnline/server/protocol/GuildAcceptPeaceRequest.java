package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildAcceptPeaceRequest extends ClientPacket {
	// GuildAcceptPeace,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptPeace;
	}
	public String guild;
	public GuildAcceptPeaceRequest(String guild){
		this.guild = guild;
	}
};

