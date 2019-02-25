package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RoyalArmyMessageRequest extends ClientPacket {
	// RoyalArmyMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoyalArmyMessage;
	}
	public String message;
	public RoyalArmyMessageRequest(String message){
		this.message = message;
	}
	public static RoyalArmyMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new RoyalArmyMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

