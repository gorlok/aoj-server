package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChaosLegionMessageRequest extends ClientPacket {
	// ChaosLegionMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosLegionMessage;
	}
	public String message;
	public ChaosLegionMessageRequest(String message){
		this.message = message;
	}
	public static ChaosLegionMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new ChaosLegionMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

