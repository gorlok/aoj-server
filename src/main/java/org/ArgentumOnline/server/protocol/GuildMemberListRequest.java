package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildMemberListRequest extends ClientPacket {
	// GuildMemberList,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildMemberList;
	}
	public String guild;
	public GuildMemberListRequest(String guild){
		this.guild = guild;
	}
};

