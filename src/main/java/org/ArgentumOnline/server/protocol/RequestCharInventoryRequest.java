package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharInventoryRequest extends ClientPacket {
	// RequestCharInventory,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharInventory;
	}
	public String userName;
	public RequestCharInventoryRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharInventoryRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharInventoryRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

