package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SaveCharsRequest extends ClientPacket {
	// SaveChars
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SaveChars;
	}
	public SaveCharsRequest(){
	}
	public static SaveCharsRequest decode(ByteBuf in) {    
		try {                                   
			return new SaveCharsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

