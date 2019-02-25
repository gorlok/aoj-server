package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

