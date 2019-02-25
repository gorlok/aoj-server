package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResuscitationSafeOffResponse extends ServerPacket {
	// ResuscitationSafeOff
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ResuscitationSafeOff;
	}
	public ResuscitationSafeOffResponse(){
	}
	public static ResuscitationSafeOffResponse decode(ByteBuf in) {    
		try {                                   
			return new ResuscitationSafeOffResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

