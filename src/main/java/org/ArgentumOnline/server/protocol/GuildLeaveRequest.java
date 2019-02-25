package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildLeaveRequest extends ClientPacket {
	// GuildLeave
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildLeave;
	}
	public GuildLeaveRequest(){
	}
	public static GuildLeaveRequest decode(ByteBuf in) {    
		try {                                   
			return new GuildLeaveRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

