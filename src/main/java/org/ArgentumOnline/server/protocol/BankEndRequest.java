package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankEndRequest extends ClientPacket {
	// BankEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankEnd;
	}
	public BankEndRequest(){
	}
	public static BankEndRequest decode(ByteBuf in) {    
		try {                                   
			return new BankEndRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

