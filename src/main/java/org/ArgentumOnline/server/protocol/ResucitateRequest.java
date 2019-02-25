package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResucitateRequest extends ClientPacket {
	// Resucitate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Resucitate;
	}
	public ResucitateRequest(){
	}
	public static ResucitateRequest decode(ByteBuf in) {    
		try {                                   
			return new ResucitateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

