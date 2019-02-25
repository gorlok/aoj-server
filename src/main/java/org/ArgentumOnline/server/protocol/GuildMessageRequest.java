package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildMessageRequest extends ClientPacket {
	// GuildMessage,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildMessage;
	}
	public String chat;
	public GuildMessageRequest(String chat){
		this.chat = chat;
	}
	public static GuildMessageRequest decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new GuildMessageRequest(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

