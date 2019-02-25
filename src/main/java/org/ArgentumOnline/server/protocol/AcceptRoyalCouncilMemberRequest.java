package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AcceptRoyalCouncilMemberRequest extends ClientPacket {
	// AcceptRoyalCouncilMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AcceptRoyalCouncilMember;
	}
	public String userName;
	public AcceptRoyalCouncilMemberRequest(String userName){
		this.userName = userName;
	}
	public static AcceptRoyalCouncilMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new AcceptRoyalCouncilMemberRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

