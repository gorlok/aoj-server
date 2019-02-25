package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildListResponse extends ServerPacket {
	// GuildList,s:members
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildList;
	}
	public String members;
	public GuildListResponse(String members){
		this.members = members;
	}
	public static GuildListResponse decode(ByteBuf in) {    
		try {                                   
			String members = readStr(in);
			return new GuildListResponse(members);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

