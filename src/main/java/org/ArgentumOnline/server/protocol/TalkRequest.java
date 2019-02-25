package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static TalkRequest decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new TalkRequest(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

