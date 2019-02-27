package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowGMPanelFormResponse extends ServerPacket {
	// ShowGMPanelForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowGMPanelForm;
	}
	public ShowGMPanelFormResponse(){
	}
	public static ShowGMPanelFormResponse decode(ByteBuf in) {    
		try {                                   
			return new ShowGMPanelFormResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

