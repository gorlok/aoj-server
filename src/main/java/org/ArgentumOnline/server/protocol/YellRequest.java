package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static YellRequest decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			return new YellRequest(chat);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

