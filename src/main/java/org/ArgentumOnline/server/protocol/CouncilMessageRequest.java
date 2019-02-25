package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

