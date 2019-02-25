package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ReloadObjectsRequest extends ClientPacket {
	// ReloadObjects
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadObjects;
	}
	public ReloadObjectsRequest(){
	}
	public static ReloadObjectsRequest decode(ByteBuf in) {    
		try {                                   
			return new ReloadObjectsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

