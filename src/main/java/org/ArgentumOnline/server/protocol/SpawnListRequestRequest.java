package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SpawnListRequestRequest extends ClientPacket {
	// SpawnListRequest
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpawnListRequest;
	}
	public SpawnListRequestRequest(){
	}
	public static SpawnListRequestRequest decode(ByteBuf in) {    
		try {                                   
			return new SpawnListRequestRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

