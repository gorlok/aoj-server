package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CriminalMessageRequest extends ClientPacket {
	// CriminalMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CriminalMessage;
	}
	public String message;
	public CriminalMessageRequest(String message){
		this.message = message;
	}
	public static CriminalMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new CriminalMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

