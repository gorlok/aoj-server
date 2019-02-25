package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestUserListRequest extends ClientPacket {
	// RequestUserList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestUserList;
	}
	public RequestUserListRequest(){
	}
	public static RequestUserListRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestUserListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

