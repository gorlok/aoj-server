package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RoleMasterRequestRequest extends ClientPacket {
	// RoleMasterRequest,s:request
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoleMasterRequest;
	}
	public String request;
	public RoleMasterRequestRequest(String request){
		this.request = request;
	}
	public static RoleMasterRequestRequest decode(ByteBuf in) {    
		try {                                   
			String request = readStr(in);
			return new RoleMasterRequestRequest(request);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

