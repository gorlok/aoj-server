package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankStartRequest extends ClientPacket {
	// BankStart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankStart;
	}
	public BankStartRequest(){
	}
	public static BankStartRequest decode(ByteBuf in) {    
		try {                                   
			return new BankStartRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

