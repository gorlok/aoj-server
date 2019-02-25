package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankEndResponse extends ServerPacket {
	// BankEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankEnd;
	}
	public BankEndResponse(){
	}
	public static BankEndResponse decode(ByteBuf in) {    
		try {                                   
			return new BankEndResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

