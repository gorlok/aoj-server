package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static GuildNewsResponse decode(ByteBuf in) {    
		try {                                   
			String guildNews = readStr(in);
			String enemiesList = readStr(in);
			String alliesList = readStr(in);
			return new GuildNewsResponse(guildNews,enemiesList,alliesList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,guildNews);
		writeStr(out,enemiesList);
		writeStr(out,alliesList);
	}
};

