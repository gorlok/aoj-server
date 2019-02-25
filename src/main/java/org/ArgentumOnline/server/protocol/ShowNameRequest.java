package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowNameRequest extends ClientPacket {
	// ShowName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowName;
	}
	public ShowNameRequest(){
	}
	public static ShowNameRequest decode(ByteBuf in) {    
		try {                                   
			return new ShowNameRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

