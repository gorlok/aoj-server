package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

