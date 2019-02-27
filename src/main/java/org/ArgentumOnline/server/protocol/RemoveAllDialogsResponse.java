package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RemoveAllDialogsResponse extends ServerPacket {
	// RemoveAllDialogs
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RemoveAllDialogs;
	}
	public RemoveAllDialogsResponse(){
	}
	public static RemoveAllDialogsResponse decode(ByteBuf in) {    
		try {                                   
			return new RemoveAllDialogsResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

