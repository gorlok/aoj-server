package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GMMessageRequest extends ClientPacket {
	// GMMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMMessage;
	}
	public String message;
	public GMMessageRequest(String message){
		this.message = message;
	}
	public static GMMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new GMMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

