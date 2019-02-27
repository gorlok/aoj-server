package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowBlacksmithFormResponse extends ServerPacket {
	// ShowBlacksmithForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowBlacksmithForm;
	}
	public ShowBlacksmithFormResponse(){
	}
	public static ShowBlacksmithFormResponse decode(ByteBuf in) {    
		try {                                   
			return new ShowBlacksmithFormResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

