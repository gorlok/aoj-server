package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BannedIPReloadRequest extends ClientPacket {
	// BannedIPReload
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BannedIPReload;
	}
	public BannedIPReloadRequest(){
	}
	public static BannedIPReloadRequest decode(ByteBuf in) {    
		try {                                   
			return new BannedIPReloadRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

