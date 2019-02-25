package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ObjectDeleteResponse extends ServerPacket {
	// ObjectDelete,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ObjectDelete;
	}
	public byte x;
	public byte y;
	public ObjectDeleteResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
	public static ObjectDeleteResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			return new ObjectDeleteResponse(x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

