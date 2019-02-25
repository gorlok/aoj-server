package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RoyalArmyKickRequest extends ClientPacket {
	// RoyalArmyKick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoyalArmyKick;
	}
	public String userName;
	public RoyalArmyKickRequest(String userName){
		this.userName = userName;
	}
	public static RoyalArmyKickRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RoyalArmyKickRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

