package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CleanSOSRequest extends ClientPacket {
	// CleanSOS
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CleanSOS;
	}
	public CleanSOSRequest(){
	}
	public static CleanSOSRequest decode(ByteBuf in) {    
		try {                                   
			return new CleanSOSRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

