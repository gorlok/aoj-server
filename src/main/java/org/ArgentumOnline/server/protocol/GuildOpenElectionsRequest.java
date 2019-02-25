package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildOpenElectionsRequest extends ClientPacket {
	// GuildOpenElections
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOpenElections;
	}
	public GuildOpenElectionsRequest(){
	}
	public static GuildOpenElectionsRequest decode(ByteBuf in) {    
		try {                                   
			return new GuildOpenElectionsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

