package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class HealRequest extends ClientPacket {
	// Heal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Heal;
	}
	public HealRequest(){
	}
	public static HealRequest decode(ByteBuf in) {    
		try {                                   
			return new HealRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

