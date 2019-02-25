package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildOnlineMembersRequest extends ClientPacket {
	// GuildOnlineMembers,s:guildName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOnlineMembers;
	}
	public String guildName;
	public GuildOnlineMembersRequest(String guildName){
		this.guildName = guildName;
	}
};

