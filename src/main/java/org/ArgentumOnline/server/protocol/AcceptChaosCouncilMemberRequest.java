package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AcceptChaosCouncilMemberRequest extends ClientPacket {
	// AcceptChaosCouncilMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AcceptChaosCouncilMember;
	}
	public String userName;
	public AcceptChaosCouncilMemberRequest(String userName){
		this.userName = userName;
	}
	public static AcceptChaosCouncilMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new AcceptChaosCouncilMemberRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

