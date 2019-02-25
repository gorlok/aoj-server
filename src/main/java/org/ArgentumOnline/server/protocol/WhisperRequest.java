package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WhisperRequest extends ClientPacket {
	// Whisper,i:targetCharIndex,s:chat
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Whisper;
	}
	public short targetCharIndex;
	public String chat;
	public WhisperRequest(short targetCharIndex,String chat){
		this.targetCharIndex = targetCharIndex;
		this.chat = chat;
	}
};

