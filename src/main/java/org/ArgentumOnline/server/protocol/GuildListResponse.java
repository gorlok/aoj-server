package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildListResponse extends ServerPacket {
	// GuildList,s:members
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildList;
	}
	public String members;
	public GuildListResponse(String members){
		this.members = members;
	}
};

