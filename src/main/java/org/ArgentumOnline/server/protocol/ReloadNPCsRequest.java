package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ReloadNPCsRequest extends ClientPacket {
	// ReloadNPCs
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadNPCs;
	}
	public ReloadNPCsRequest(){
	}
	public static ReloadNPCsRequest decode(ByteBuf in) {    
		try {                                   
			return new ReloadNPCsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

