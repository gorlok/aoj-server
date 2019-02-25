package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class KickRequest extends ClientPacket {
	// Kick,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Kick;
	}
	public String userName;
	public KickRequest(String userName){
		this.userName = userName;
	}
	public static KickRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new KickRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

