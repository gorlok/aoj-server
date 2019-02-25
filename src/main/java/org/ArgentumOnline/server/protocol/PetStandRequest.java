package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PetStandRequest extends ClientPacket {
	// PetStand
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PetStand;
	}
	public PetStandRequest(){
	}
	public static PetStandRequest decode(ByteBuf in) {    
		try {                                   
			return new PetStandRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

