package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PingRequest extends ClientPacket {
	// Ping
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Ping;
	}
	public PingRequest(){
	}
	public static PingRequest decode(ByteBuf in) {    
		try {                                   
			return new PingRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

