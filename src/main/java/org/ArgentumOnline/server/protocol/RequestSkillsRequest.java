package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestSkillsRequest extends ClientPacket {
	// RequestSkills
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestSkills;
	}
	public RequestSkillsRequest(){
	}
	public static RequestSkillsRequest decode(ByteBuf in) {    
		try {                                   
			return new RequestSkillsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

