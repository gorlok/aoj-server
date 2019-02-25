package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RemoveCharFromGuildRequest extends ClientPacket {
	// RemoveCharFromGuild,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RemoveCharFromGuild;
	}
	public String userName;
	public RemoveCharFromGuildRequest(String userName){
		this.userName = userName;
	}
	public static RemoveCharFromGuildRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RemoveCharFromGuildRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

