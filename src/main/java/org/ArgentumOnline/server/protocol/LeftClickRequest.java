package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class LeftClickRequest extends ClientPacket {
	// LeftClick,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LeftClick;
	}
	public byte x;
	public byte y;
	public LeftClickRequest(byte x,byte y){
		this.x = x;
		this.y = y;
	}
	public static LeftClickRequest decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			return new LeftClickRequest(x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

