package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MeditateRequest extends ClientPacket {
	// Meditate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Meditate;
	}
	public MeditateRequest(){
	}
	public static MeditateRequest decode(ByteBuf in) {    
		try {                                   
			return new MeditateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

