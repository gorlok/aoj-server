package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CantUseWhileMeditatingResponse extends ServerPacket {
	// CantUseWhileMeditating
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CantUseWhileMeditating;
	}
	public CantUseWhileMeditatingResponse(){
	}
	public static CantUseWhileMeditatingResponse decode(ByteBuf in) {    
		try {                                   
			return new CantUseWhileMeditatingResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

