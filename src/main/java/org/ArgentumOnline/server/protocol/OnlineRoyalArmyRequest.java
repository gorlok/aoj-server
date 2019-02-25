package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OnlineRoyalArmyRequest extends ClientPacket {
	// OnlineRoyalArmy
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineRoyalArmy;
	}
	public OnlineRoyalArmyRequest(){
	}
	public static OnlineRoyalArmyRequest decode(ByteBuf in) {    
		try {                                   
			return new OnlineRoyalArmyRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

