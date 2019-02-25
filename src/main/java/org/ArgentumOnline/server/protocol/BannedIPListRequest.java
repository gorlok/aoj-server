package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BannedIPListRequest extends ClientPacket {
	// BannedIPList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BannedIPList;
	}
	public BannedIPListRequest(){
	}
	public static BannedIPListRequest decode(ByteBuf in) {    
		try {                                   
			return new BannedIPListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

