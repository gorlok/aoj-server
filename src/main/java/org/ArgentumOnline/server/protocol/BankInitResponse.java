package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankInitResponse extends ServerPacket {
	// BankInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankInit;
	}
	public BankInitResponse(){
	}
	public static BankInitResponse decode(ByteBuf in) {    
		try {                                   
			return new BankInitResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

