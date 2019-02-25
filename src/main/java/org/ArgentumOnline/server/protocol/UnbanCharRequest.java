package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UnbanCharRequest extends ClientPacket {
	// UnbanChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UnbanChar;
	}
	public String userName;
	public UnbanCharRequest(String userName){
		this.userName = userName;
	}
	public static UnbanCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new UnbanCharRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

