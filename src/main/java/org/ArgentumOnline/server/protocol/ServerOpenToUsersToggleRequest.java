package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ServerOpenToUsersToggleRequest extends ClientPacket {
	// ServerOpenToUsersToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerOpenToUsersToggle;
	}
	public ServerOpenToUsersToggleRequest(){
	}
	public static ServerOpenToUsersToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new ServerOpenToUsersToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

