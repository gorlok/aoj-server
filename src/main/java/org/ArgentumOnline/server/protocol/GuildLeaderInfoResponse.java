package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildLeaderInfoResponse extends ServerPacket {
	// GuildLeaderInfo,s:guildList,s:memberList,s:guildNews,s:requestsList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildLeaderInfo;
	}
	public String guildList;
	public String memberList;
	public String guildNews;
	public String requestsList;
	public GuildLeaderInfoResponse(String guildList,String memberList,String guildNews,String requestsList){
		this.guildList = guildList;
		this.memberList = memberList;
		this.guildNews = guildNews;
		this.requestsList = requestsList;
	}
};

