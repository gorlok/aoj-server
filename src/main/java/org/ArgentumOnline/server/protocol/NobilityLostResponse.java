package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NobilityLostResponse extends ServerPacket {
	// NobilityLost
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NobilityLost;
	}
	public NobilityLostResponse(){
	}
	public static NobilityLostResponse decode(ByteBuf in) {    
		try {                                   
			return new NobilityLostResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

