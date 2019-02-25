package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TalkRequest extends ClientPacket {
	// Talk,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Talk;
	}
	public String chat;
	public TalkRequest(String chat){
		this.chat = chat;
	}
};

