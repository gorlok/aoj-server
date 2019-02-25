package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class LastIPRequest extends ClientPacket {
	// LastIP,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LastIP;
	}
	public String userName;
	public LastIPRequest(String userName){
		this.userName = userName;
	}
	public static LastIPRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new LastIPRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

