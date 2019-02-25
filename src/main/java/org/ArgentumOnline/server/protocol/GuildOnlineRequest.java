package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildOnlineRequest extends ClientPacket {
	// GuildOnline
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOnline;
	}
	public GuildOnlineRequest(){
	}
	public static GuildOnlineRequest decode(ByteBuf in) {    
		try {                                   
			return new GuildOnlineRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

