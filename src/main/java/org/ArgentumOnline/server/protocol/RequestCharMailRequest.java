package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharMailRequest extends ClientPacket {
	// RequestCharMail,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharMail;
	}
	public String userName;
	public RequestCharMailRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharMailRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharMailRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

