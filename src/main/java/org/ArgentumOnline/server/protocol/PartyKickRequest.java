package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyKickRequest extends ClientPacket {
	// PartyKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyKick;
	}
	public String userName;
	public PartyKickRequest(String userName){
		this.userName = userName;
	}
	public static PartyKickRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new PartyKickRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

