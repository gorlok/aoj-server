package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TeleportDestroyRequest extends ClientPacket {
	// TeleportDestroy
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TeleportDestroy;
	}
	public TeleportDestroyRequest(){
	}
	public static TeleportDestroyRequest decode(ByteBuf in) {    
		try {                                   
			return new TeleportDestroyRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

