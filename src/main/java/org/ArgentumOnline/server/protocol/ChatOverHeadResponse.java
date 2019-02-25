package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChatOverHeadResponse extends ServerPacket {
	// ChatOverHead,s:chat,i:charIndex,b:red,b:green,b:blue
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChatOverHead;
	}
	public String chat;
	public short charIndex;
	public byte red;
	public byte green;
	public byte blue;
	public ChatOverHeadResponse(String chat,short charIndex,byte red,byte green,byte blue){
		this.chat = chat;
		this.charIndex = charIndex;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public static ChatOverHeadResponse decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			short charIndex = readShort(in);
			byte red = readByte(in);
			byte green = readByte(in);
			byte blue = readByte(in);
			return new ChatOverHeadResponse(chat,charIndex,red,green,blue);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

