package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CharacterMoveResponse extends ServerPacket {
	// CharacterMove,i:charIndex,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterMove;
	}
	public short charIndex;
	public byte x;
	public byte y;
	public CharacterMoveResponse(short charIndex,byte x,byte y){
		this.charIndex = charIndex;
		this.x = x;
		this.y = y;
	}
	public static CharacterMoveResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new CharacterMoveResponse(charIndex,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,charIndex);
		writeByte(out,x);
		writeByte(out,y);
	}
};

