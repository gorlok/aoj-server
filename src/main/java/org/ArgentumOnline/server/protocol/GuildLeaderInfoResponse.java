package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static GuildLeaderInfoResponse decode(ByteBuf in) {    
		try {                                   
			String guildList = readStr(in);
			String memberList = readStr(in);
			String guildNews = readStr(in);
			String requestsList = readStr(in);
			return new GuildLeaderInfoResponse(guildList,memberList,guildNews,requestsList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

