package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreateNewGuildRequest extends ClientPacket {
	// CreateNewGuild,s:desc,s:guildName,s:site,s:codex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNewGuild;
	}
	public String desc;
	public String guildName;
	public String site;
	public String codex;
	public CreateNewGuildRequest(String desc,String guildName,String site,String codex){
		this.desc = desc;
		this.guildName = guildName;
		this.site = site;
		this.codex = codex;
	}
};

