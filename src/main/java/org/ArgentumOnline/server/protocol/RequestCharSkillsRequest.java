package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharSkillsRequest extends ClientPacket {
	// RequestCharSkills,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharSkills;
	}
	public String userName;
	public RequestCharSkillsRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharSkillsRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharSkillsRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

