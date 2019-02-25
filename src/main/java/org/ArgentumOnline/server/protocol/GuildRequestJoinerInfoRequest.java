package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRequestJoinerInfoRequest extends ClientPacket {
	// GuildRequestJoinerInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestJoinerInfo;
	}
	public String userName;
	public GuildRequestJoinerInfoRequest(String userName){
		this.userName = userName;
	}
	public static GuildRequestJoinerInfoRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GuildRequestJoinerInfoRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

