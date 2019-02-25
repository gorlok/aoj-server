package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ServerTimeRequest extends ClientPacket {
	// ServerTime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerTime;
	}
	public ServerTimeRequest(){
	}
	public static ServerTimeRequest decode(ByteBuf in) {    
		try {                                   
			return new ServerTimeRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

