package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResetAutoUpdateRequest extends ClientPacket {
	// ResetAutoUpdate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetAutoUpdate;
	}
	public ResetAutoUpdateRequest(){
	}
	public static ResetAutoUpdateRequest decode(ByteBuf in) {    
		try {                                   
			return new ResetAutoUpdateRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

