package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowServerFormRequest extends ClientPacket {
	// ShowServerForm
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowServerForm;
	}
	public ShowServerFormRequest(){
	}
	public static ShowServerFormRequest decode(ByteBuf in) {    
		try {                                   
			return new ShowServerFormRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

