package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TurnOffServerRequest extends ClientPacket {
	// TurnOffServer
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TurnOffServer;
	}
	public TurnOffServerRequest(){
	}
	public static TurnOffServerRequest decode(ByteBuf in) {    
		try {                                   
			return new TurnOffServerRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

