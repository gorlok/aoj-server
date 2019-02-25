package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DoubleClickRequest extends ClientPacket {
	// DoubleClick,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DoubleClick;
	}
	public byte x;
	public byte y;
	public DoubleClickRequest(byte x,byte y){
		this.x = x;
		this.y = y;
	}
	public static DoubleClickRequest decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			return new DoubleClickRequest(x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

