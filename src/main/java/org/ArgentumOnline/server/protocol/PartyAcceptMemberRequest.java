package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyAcceptMemberRequest extends ClientPacket {
	// PartyAcceptMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyAcceptMember;
	}
	public String userName;
	public PartyAcceptMemberRequest(String userName){
		this.userName = userName;
	}
	public static PartyAcceptMemberRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new PartyAcceptMemberRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

