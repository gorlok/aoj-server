package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DoBackUpRequest extends ClientPacket {
	// DoBackUp
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DoBackUp;
	}
	public DoBackUpRequest(){
	}
	public static DoBackUpRequest decode(ByteBuf in) {    
		try {                                   
			return new DoBackUpRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

