package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateNeededResponse extends ServerPacket {
	// UpdateNeeded
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateNeeded;
	}
	public UpdateNeededResponse(){
	}
	public static UpdateNeededResponse decode(ByteBuf in) {    
		try {                                   
			return new UpdateNeededResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

