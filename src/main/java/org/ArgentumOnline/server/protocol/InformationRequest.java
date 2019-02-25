package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class InformationRequest extends ClientPacket {
	// Information
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Information;
	}
	public InformationRequest(){
	}
	public static InformationRequest decode(ByteBuf in) {    
		try {                                   
			return new InformationRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

