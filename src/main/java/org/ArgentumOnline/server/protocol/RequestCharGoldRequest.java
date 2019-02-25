package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharGoldRequest extends ClientPacket {
	// RequestCharGold,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharGold;
	}
	public String userName;
	public RequestCharGoldRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharGoldRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharGoldRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

