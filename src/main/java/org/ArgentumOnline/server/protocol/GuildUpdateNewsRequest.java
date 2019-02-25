package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static GuildUpdateNewsRequest decode(ByteBuf in) {    
		try {                                   
			String news = readStr(in);
			return new GuildUpdateNewsRequest(news);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

