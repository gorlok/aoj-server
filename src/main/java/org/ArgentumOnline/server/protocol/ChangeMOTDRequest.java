package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMOTDRequest extends ClientPacket {
	// ChangeMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMOTD;
	}
	public ChangeMOTDRequest(){
	}
	public static ChangeMOTDRequest decode(ByteBuf in) {    
		try {                                   
			return new ChangeMOTDRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

