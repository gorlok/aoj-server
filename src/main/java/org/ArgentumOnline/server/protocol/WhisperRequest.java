package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static WhisperRequest decode(ByteBuf in) {    
		try {                                   
			short targetCharIndex = readShort(in);
			String chat = readStr(in);
			return new WhisperRequest(targetCharIndex,chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

