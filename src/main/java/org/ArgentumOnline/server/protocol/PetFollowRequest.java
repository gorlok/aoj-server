package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PetFollowRequest extends ClientPacket {
	// PetFollow
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PetFollow;
	}
	public PetFollowRequest(){
	}
	public static PetFollowRequest decode(ByteBuf in) {    
		try {                                   
			return new PetFollowRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

