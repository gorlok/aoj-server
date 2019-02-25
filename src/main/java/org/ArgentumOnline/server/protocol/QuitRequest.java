package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class QuitRequest extends ClientPacket {
	// Quit
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Quit;
	}
	public QuitRequest(){
	}
	public static QuitRequest decode(ByteBuf in) {    
		try {                                   
			return new QuitRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

