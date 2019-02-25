package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyJoinRequest extends ClientPacket {
	// PartyJoin
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyJoin;
	}
	public PartyJoinRequest(){
	}
	public static PartyJoinRequest decode(ByteBuf in) {    
		try {                                   
			return new PartyJoinRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

