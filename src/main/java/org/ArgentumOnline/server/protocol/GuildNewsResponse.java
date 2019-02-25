package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildNewsResponse extends ServerPacket {
	// GuildNews,s:guildNews,s:enemiesList,s:alliesList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildNews;
	}
	public String guildNews;
	public String enemiesList;
	public String alliesList;
	public GuildNewsResponse(String guildNews,String enemiesList,String alliesList){
		this.guildNews = guildNews;
		this.enemiesList = enemiesList;
		this.alliesList = alliesList;
	}
};

