package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyMessageRequest extends ClientPacket {
	// PartyMessage,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyMessage;
	}
	public String chat;
	public PartyMessageRequest(String chat){
		this.chat = chat;
	}
	public static PartyMessageRequest decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new PartyMessageRequest(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

