package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NPCFollowRequest extends ClientPacket {
	// NPCFollow
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NPCFollow;
	}
	public NPCFollowRequest(){
	}
	public static NPCFollowRequest decode(ByteBuf in) {    
		try {                                   
			return new NPCFollowRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

