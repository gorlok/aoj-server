package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildUpdateNewsRequest extends ClientPacket {
	// GuildUpdateNews,s:news
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildUpdateNews;
	}
	public String news;
	public GuildUpdateNewsRequest(String news){
		this.news = news;
	}
};

