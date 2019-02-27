package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlockPositionResponse extends ServerPacket {
	// BlockPosition,b:x,b:y,b:blocked
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockPosition;
	}
	public byte x;
	public byte y;
	public byte blocked;
	public BlockPositionResponse(byte x,byte y,byte blocked){
		this.x = x;
		this.y = y;
		this.blocked = blocked;
	}
	public static BlockPositionResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			byte blocked = readByte(in);
			return new BlockPositionResponse(x,y,blocked);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,x);
		writeByte(out,y);
		writeByte(out,blocked);
	}
};

