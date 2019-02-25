package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserAttackedSwingResponse extends ServerPacket {
	// UserAttackedSwing,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserAttackedSwing;
	}
	public short charIndex;
	public UserAttackedSwingResponse(short charIndex){
		this.charIndex = charIndex;
	}
	public static UserAttackedSwingResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			return new UserAttackedSwingResponse(charIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

