package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ConsoleMsgResponse extends ServerPacket {
	// ConsoleMsg,s:chat,b:fontIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ConsoleMsg;
	}
	public String chat;
	public byte fontIndex;
	public ConsoleMsgResponse(String chat,byte fontIndex){
		this.chat = chat;
		this.fontIndex = fontIndex;
	}
};

