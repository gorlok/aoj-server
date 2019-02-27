package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TradeOKResponse extends ServerPacket {
	// TradeOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.TradeOK;
	}
	public TradeOKResponse(){
	}
	public static TradeOKResponse decode(ByteBuf in) {    
		try {                                   
			return new TradeOKResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

