package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowGuildFundationFormResponse extends ServerPacket {
	// ShowGuildFundationForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowGuildFundationForm;
	}
	public ShowGuildFundationFormResponse(){
	}
	public static ShowGuildFundationFormResponse decode(ByteBuf in) {    
		try {                                   
			return new ShowGuildFundationFormResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

