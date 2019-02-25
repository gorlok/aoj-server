package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResetNPCInventoryRequest extends ClientPacket {
	// ResetNPCInventory
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetNPCInventory;
	}
	public ResetNPCInventoryRequest(){
	}
	public static ResetNPCInventoryRequest decode(ByteBuf in) {    
		try {                                   
			return new ResetNPCInventoryRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

