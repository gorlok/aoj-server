package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CouncilMessageRequest extends ClientPacket {
	// CouncilMessage,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CouncilMessage;
	}
	public String chat;
	public CouncilMessageRequest(String chat){
		this.chat = chat;
	}
	public static CouncilMessageRequest decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new CouncilMessageRequest(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

