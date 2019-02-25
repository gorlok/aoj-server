package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UptimeRequest extends ClientPacket {
	// Uptime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Uptime;
	}
	public UptimeRequest(){
	}
	public static UptimeRequest decode(ByteBuf in) {    
		try {                                   
			return new UptimeRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

