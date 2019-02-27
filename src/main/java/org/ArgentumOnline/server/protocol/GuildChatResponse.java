package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildChatResponse extends ServerPacket {
	// GuildChat,s:chat
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildChat;
	}
	public String chat;
	public GuildChatResponse(String chat){
		this.chat = chat;
	}
	public static GuildChatResponse decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new GuildChatResponse(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,chat);
	}
};

