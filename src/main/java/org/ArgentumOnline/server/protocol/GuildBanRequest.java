package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildBanRequest extends ClientPacket {
	// GuildBan,s:guildName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildBan;
	}
	public String guildName;
	public GuildBanRequest(String guildName){
		this.guildName = guildName;
	}
	public static GuildBanRequest decode(ByteBuf in) {    
		try {                                   
			String guildName = readStr(in);
			return new GuildBanRequest(guildName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

