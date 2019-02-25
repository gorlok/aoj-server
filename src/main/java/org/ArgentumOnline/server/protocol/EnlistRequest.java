package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class EnlistRequest extends ClientPacket {
	// Enlist
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Enlist;
	}
	public EnlistRequest(){
	}
	public static EnlistRequest decode(ByteBuf in) {    
		try {                                   
			return new EnlistRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

