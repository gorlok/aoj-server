package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PauseToggleResponse extends ServerPacket {
	// PauseToggle
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PauseToggle;
	}
	public PauseToggleResponse(){
	}
	public static PauseToggleResponse decode(ByteBuf in) {    
		try {                                   
			return new PauseToggleResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

