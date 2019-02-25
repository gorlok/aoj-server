package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestStatsRequest extends ClientPacket {
	// RequestStats
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestStats;
	}
	public RequestStatsRequest(){
	}
	public static RequestStatsRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestStatsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

