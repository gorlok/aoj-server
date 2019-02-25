package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResuscitationSafeOnResponse extends ServerPacket {
	// ResuscitationSafeOn
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ResuscitationSafeOn;
	}
	public ResuscitationSafeOnResponse(){
	}
	public static ResuscitationSafeOnResponse decode(ByteBuf in) {    
		try {                                   
			return new ResuscitationSafeOnResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

