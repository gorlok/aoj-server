package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChatColorRequest extends ClientPacket {
	// ChatColor,b:red,b:green,b:blue
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChatColor;
	}
	public byte red;
	public byte green;
	public byte blue;
	public ChatColorRequest(byte red,byte green,byte blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public static ChatColorRequest decode(ByteBuf in) {    
		try {                                   
			byte red = readByte(in);
			byte green = readByte(in);
			byte blue = readByte(in);
			return new ChatColorRequest(red,green,blue);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

