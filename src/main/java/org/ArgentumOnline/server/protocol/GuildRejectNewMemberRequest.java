package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRejectNewMemberRequest extends ClientPacket {
	// GuildRejectNewMember,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectNewMember;
	}
	public String userName;
	public String reason;
	public GuildRejectNewMemberRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
	public static GuildRejectNewMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String reason = readStr(in);
			return new GuildRejectNewMemberRequest(userName,reason);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

