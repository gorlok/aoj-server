package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TileBlockedToggleRequest extends ClientPacket {
	// TileBlockedToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TileBlockedToggle;
	}
	public TileBlockedToggleRequest(){
	}
	public static TileBlockedToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new TileBlockedToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

