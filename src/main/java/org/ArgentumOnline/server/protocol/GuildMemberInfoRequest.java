package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildMemberInfoRequest extends ClientPacket {
	// GuildMemberInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildMemberInfo;
	}
	public String userName;
	public GuildMemberInfoRequest(String userName){
		this.userName = userName;
	}
	public static GuildMemberInfoRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GuildMemberInfoRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

