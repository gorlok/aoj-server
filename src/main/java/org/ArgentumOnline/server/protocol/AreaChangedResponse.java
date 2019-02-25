package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AreaChangedResponse extends ServerPacket {
	// AreaChanged,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.AreaChanged;
	}
	public byte x;
	public byte y;
	public AreaChangedResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
	public static AreaChangedResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			return new AreaChangedResponse(x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

