package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildNewWebsiteRequest extends ClientPacket {
	// GuildNewWebsite,s:website
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildNewWebsite;
	}
	public String website;
	public GuildNewWebsiteRequest(String website){
		this.website = website;
	}
	public static GuildNewWebsiteRequest decode(ByteBuf in) {    
		try {                                   
			String website = readStr(in);
			return new GuildNewWebsiteRequest(website);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

