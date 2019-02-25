package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class KickAllCharsRequest extends ClientPacket {
	// KickAllChars
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KickAllChars;
	}
	public KickAllCharsRequest(){
	}
	public static KickAllCharsRequest decode(ByteBuf in) {    
		try {                                   
			return new KickAllCharsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

