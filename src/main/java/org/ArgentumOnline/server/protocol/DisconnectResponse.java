package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DisconnectResponse extends ServerPacket {
	// Disconnect
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Disconnect;
	}
	public DisconnectResponse(){
	}
	public static DisconnectResponse decode(ByteBuf in) {    
		try {                                   
			return new DisconnectResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

