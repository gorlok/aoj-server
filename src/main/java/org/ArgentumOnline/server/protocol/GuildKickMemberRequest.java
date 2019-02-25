package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildKickMemberRequest extends ClientPacket {
	// GuildKickMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildKickMember;
	}
	public String userName;
	public GuildKickMemberRequest(String userName){
		this.userName = userName;
	}
	public static GuildKickMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GuildKickMemberRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

