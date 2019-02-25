package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResuscitationToggleRequest extends ClientPacket {
	// ResuscitationToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResuscitationToggle;
	}
	public ResuscitationToggleRequest(){
	}
	public static ResuscitationToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new ResuscitationToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

