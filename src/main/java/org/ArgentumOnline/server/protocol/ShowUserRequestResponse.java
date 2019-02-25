package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowUserRequestResponse extends ServerPacket {
	// ShowUserRequest,s:details
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowUserRequest;
	}
	public String details;
	public ShowUserRequestResponse(String details){
		this.details = details;
	}
	public static ShowUserRequestResponse decode(ByteBuf in) {    
		try {                                   
			String details = readStr(in);
			return new ShowUserRequestResponse(details);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

