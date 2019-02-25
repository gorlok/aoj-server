package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GoNearbyRequest extends ClientPacket {
	// GoNearby,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GoNearby;
	}
	public String userName;
	public GoNearbyRequest(String userName){
		this.userName = userName;
	}
	public static GoNearbyRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GoNearbyRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

