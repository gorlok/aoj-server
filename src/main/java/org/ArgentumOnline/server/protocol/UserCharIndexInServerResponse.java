package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCharIndexInServerResponse extends ServerPacket {
	// UserCharIndexInServer,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCharIndexInServer;
	}
	public short charIndex;
	public UserCharIndexInServerResponse(short charIndex){
		this.charIndex = charIndex;
	}
	public static UserCharIndexInServerResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			return new UserCharIndexInServerResponse(charIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

