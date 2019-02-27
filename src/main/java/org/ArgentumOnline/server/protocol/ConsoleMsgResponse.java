package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ConsoleMsgResponse decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			byte fontIndex = readByte(in);
			return new ConsoleMsgResponse(chat,fontIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,chat);
		writeByte(out,fontIndex);
	}
};

