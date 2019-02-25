package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SOSShowListRequest extends ClientPacket {
	// SOSShowList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SOSShowList;
	}
	public SOSShowListRequest(){
	}
	public static SOSShowListRequest decode(ByteBuf in) {    
		try {                                   
			return new SOSShowListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

