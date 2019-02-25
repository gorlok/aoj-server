package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildAcceptNewMemberRequest extends ClientPacket {
	// GuildAcceptNewMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptNewMember;
	}
	public String userName;
	public GuildAcceptNewMemberRequest(String userName){
		this.userName = userName;
	}
	public static GuildAcceptNewMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GuildAcceptNewMemberRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

