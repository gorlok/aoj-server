package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyOnlineRequest extends ClientPacket {
	// PartyOnline
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyOnline;
	}
	public PartyOnlineRequest(){
	}
	public static PartyOnlineRequest decode(ByteBuf in) {    
		try {                                   
			return new PartyOnlineRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

