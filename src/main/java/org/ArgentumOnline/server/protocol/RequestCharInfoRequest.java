package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharInfoRequest extends ClientPacket {
	// RequestCharInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharInfo;
	}
	public String userName;
	public RequestCharInfoRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharInfoRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharInfoRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

