package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildOnlineMembersRequest extends ClientPacket {
	// GuildOnlineMembers,s:guildName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOnlineMembers;
	}
	public String guildName;
	public GuildOnlineMembersRequest(String guildName){
		this.guildName = guildName;
	}
	public static GuildOnlineMembersRequest decode(ByteBuf in) {    
		try {                                   
			String guildName = readStr(in);
			return new GuildOnlineMembersRequest(guildName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

