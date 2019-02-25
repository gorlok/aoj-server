package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SaveMapRequest extends ClientPacket {
	// SaveMap
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SaveMap;
	}
	public SaveMapRequest(){
	}
	public static SaveMapRequest decode(ByteBuf in) {    
		try {                                   
			return new SaveMapRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

