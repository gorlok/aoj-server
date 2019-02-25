package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildAlliancePropListRequest extends ClientPacket {
	// GuildAlliancePropList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAlliancePropList;
	}
	public GuildAlliancePropListRequest(){
	}
	public static GuildAlliancePropListRequest decode(ByteBuf in) {    
		try {                                   
			return new GuildAlliancePropListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

