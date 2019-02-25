package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CouncilKickRequest extends ClientPacket {
	// CouncilKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CouncilKick;
	}
	public String userName;
	public CouncilKickRequest(String userName){
		this.userName = userName;
	}
	public static CouncilKickRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new CouncilKickRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

