package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WarpCharRequest extends ClientPacket {
	// WarpChar,s:userName,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarpChar;
	}
	public String userName;
	public byte x;
	public byte y;
	public WarpCharRequest(String userName,byte x,byte y){
		this.userName = userName;
		this.x = x;
		this.y = y;
	}
	public static WarpCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new WarpCharRequest(userName,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

