package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NPCSwingResponse extends ServerPacket {
	// NPCSwing
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NPCSwing;
	}
	public NPCSwingResponse(){
	}
	public static NPCSwingResponse decode(ByteBuf in) {    
		try {                                   
			return new NPCSwingResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

