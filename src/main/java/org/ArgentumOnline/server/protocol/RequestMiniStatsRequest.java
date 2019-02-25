package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestMiniStatsRequest extends ClientPacket {
	// RequestMiniStats
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestMiniStats;
	}
	public RequestMiniStatsRequest(){
	}
	public static RequestMiniStatsRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestMiniStatsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

