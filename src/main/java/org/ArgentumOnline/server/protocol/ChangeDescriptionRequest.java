package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeDescriptionRequest extends ClientPacket {
	// ChangeDescription,s:description
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeDescription;
	}
	public String description;
	public ChangeDescriptionRequest(String description){
		this.description = description;
	}
	public static ChangeDescriptionRequest decode(ByteBuf in) {    
		try {                                   
			String description = readStr(in);
			return new ChangeDescriptionRequest(description);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

