package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RewardRequest extends ClientPacket {
	// Reward
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Reward;
	}
	public RewardRequest(){
	}
	public static RewardRequest decode(ByteBuf in) {    
		try {                                   
			return new RewardRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

