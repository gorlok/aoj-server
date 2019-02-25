package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

