package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SetInvisibleResponse extends ServerPacket {
	// SetInvisible,i:charIndex,b:invisible
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SetInvisible;
	}
	public short charIndex;
	public byte invisible;
	public SetInvisibleResponse(short charIndex,byte invisible){
		this.charIndex = charIndex;
		this.invisible = invisible;
	}
	public static SetInvisibleResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			byte invisible = readByte(in);
			return new SetInvisibleResponse(charIndex,invisible);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

