package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildBanRequest extends ClientPacket {
	// GuildBan,s:guildName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildBan;
	}
	public String guildName;
	public GuildBanRequest(String guildName){
		this.guildName = guildName;
	}
};

