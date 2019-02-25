package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyCreateRequest extends ClientPacket {
	// PartyCreate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyCreate;
	}
	public PartyCreateRequest(){
	}
	public static PartyCreateRequest decode(ByteBuf in) {    
		try {                                   
			return new PartyCreateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

