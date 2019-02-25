package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GoToCharRequest extends ClientPacket {
	// GoToChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GoToChar;
	}
	public String userName;
	public GoToCharRequest(String userName){
		this.userName = userName;
	}
	public static GoToCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new GoToCharRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

