package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PosUpdateResponse extends ServerPacket {
	// PosUpdate,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PosUpdate;
	}
	public byte x;
	public byte y;
	public PosUpdateResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
	public static PosUpdateResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			return new PosUpdateResponse(x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,x);
		writeByte(out,y);
	}
};

