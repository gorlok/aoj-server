package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildMemberListRequest extends ClientPacket {
	// GuildMemberList,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildMemberList;
	}
	public String guild;
	public GuildMemberListRequest(String guild){
		this.guild = guild;
	}
	public static GuildMemberListRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildMemberListRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

