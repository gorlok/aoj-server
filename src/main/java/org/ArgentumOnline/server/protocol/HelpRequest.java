package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class HelpRequest extends ClientPacket {
	// Help
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Help;
	}
	public HelpRequest(){
	}
	public static HelpRequest decode(ByteBuf in) {    
		try {                                   
			return new HelpRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

