package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestGuildLeaderInfoRequest extends ClientPacket {
	// RequestGuildLeaderInfo
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestGuildLeaderInfo;
	}
	public RequestGuildLeaderInfoRequest(){
	}
	public static RequestGuildLeaderInfoRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestGuildLeaderInfoRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

