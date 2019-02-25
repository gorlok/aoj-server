package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserIndexInServerResponse extends ServerPacket {
	// UserIndexInServer,i:userIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserIndexInServer;
	}
	public short userIndex;
	public UserIndexInServerResponse(short userIndex){
		this.userIndex = userIndex;
	}
	public static UserIndexInServerResponse decode(ByteBuf in) {    
		try {                                   
			short userIndex = readShort(in);
			return new UserIndexInServerResponse(userIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

