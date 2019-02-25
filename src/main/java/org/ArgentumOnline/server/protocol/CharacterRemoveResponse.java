package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CharacterRemoveResponse extends ServerPacket {
	// CharacterRemove,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterRemove;
	}
	public short charIndex;
	public CharacterRemoveResponse(short charIndex){
		this.charIndex = charIndex;
	}
	public static CharacterRemoveResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			return new CharacterRemoveResponse(charIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

