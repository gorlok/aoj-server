package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowForumFormResponse extends ServerPacket {
	// ShowForumForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowForumForm;
	}
	public ShowForumFormResponse(){
	}
	public static ShowForumFormResponse decode(ByteBuf in) {    
		try {                                   
			return new ShowForumFormResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

