package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class YellRequest extends ClientPacket {
	// Yell,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Yell;
	}
	public String chat;
	public YellRequest(String chat){
		this.chat = chat;
	}
};

