package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChaosLegionKickRequest extends ClientPacket {
	// ChaosLegionKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosLegionKick;
	}
	public String userName;
	public ChaosLegionKickRequest(String userName){
		this.userName = userName;
	}
	public static ChaosLegionKickRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new ChaosLegionKickRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

