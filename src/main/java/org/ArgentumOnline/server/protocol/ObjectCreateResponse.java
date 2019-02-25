package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ObjectCreateResponse extends ServerPacket {
	// ObjectCreate,b:x,b:y,i:grhIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ObjectCreate;
	}
	public byte x;
	public byte y;
	public short grhIndex;
	public ObjectCreateResponse(byte x,byte y,short grhIndex){
		this.x = x;
		this.y = y;
		this.grhIndex = grhIndex;
	}
	public static ObjectCreateResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			short grhIndex = readShort(in);
			return new ObjectCreateResponse(x,y,grhIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

