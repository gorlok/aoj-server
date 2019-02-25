package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AttackRequest extends ClientPacket {
	// Attack
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Attack;
	}
	public AttackRequest(){
	}
	public static AttackRequest decode(ByteBuf in) {    
		try {                                   
			return new AttackRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

