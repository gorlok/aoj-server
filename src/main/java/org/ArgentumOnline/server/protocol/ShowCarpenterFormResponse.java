package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowCarpenterFormResponse extends ServerPacket {
	// ShowCarpenterForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowCarpenterForm;
	}
	public ShowCarpenterFormResponse(){
	}
	public static ShowCarpenterFormResponse decode(ByteBuf in) {    
		try {                                   
			return new ShowCarpenterFormResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

