package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CitizenMessageRequest extends ClientPacket {
	// CitizenMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CitizenMessage;
	}
	public String message;
	public CitizenMessageRequest(String message){
		this.message = message;
	}
	public static CitizenMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new CitizenMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

