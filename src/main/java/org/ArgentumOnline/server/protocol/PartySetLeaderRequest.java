package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartySetLeaderRequest extends ClientPacket {
	// PartySetLeader,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartySetLeader;
	}
	public String userName;
	public PartySetLeaderRequest(String userName){
		this.userName = userName;
	}
	public static PartySetLeaderRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new PartySetLeaderRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

