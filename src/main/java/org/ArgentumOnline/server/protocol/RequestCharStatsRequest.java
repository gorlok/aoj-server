package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharStatsRequest extends ClientPacket {
	// RequestCharStats,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharStats;
	}
	public String userName;
	public RequestCharStatsRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharStatsRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharStatsRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

