package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlockedWithShieldUserResponse extends ServerPacket {
	// BlockedWithShieldUser
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockedWithShieldUser;
	}
	public BlockedWithShieldUserResponse(){
	}
	public static BlockedWithShieldUserResponse decode(ByteBuf in) {    
		try {                                   
			return new BlockedWithShieldUserResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

