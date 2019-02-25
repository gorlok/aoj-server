package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildPeacePropListRequest extends ClientPacket {
	// GuildPeacePropList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildPeacePropList;
	}
	public GuildPeacePropListRequest(){
	}
	public static GuildPeacePropListRequest decode(ByteBuf in) {    
		try {                                   
			return new GuildPeacePropListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

