package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MeditateToggleResponse extends ServerPacket {
	// MeditateToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.MeditateToggle;
	}
	public MeditateToggleResponse(){
	}
	public static MeditateToggleResponse decode(ByteBuf in) {    
		try {                                   
			return new MeditateToggleResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

