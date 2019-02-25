package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SystemMessageRequest extends ClientPacket {
	// SystemMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SystemMessage;
	}
	public String message;
	public SystemMessageRequest(String message){
		this.message = message;
	}
	public static SystemMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new SystemMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

