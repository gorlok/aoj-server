package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankOKResponse extends ServerPacket {
	// BankOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankOK;
	}
	public BankOKResponse(){
	}
	public static BankOKResponse decode(ByteBuf in) {    
		try {                                   
			return new BankOKResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

