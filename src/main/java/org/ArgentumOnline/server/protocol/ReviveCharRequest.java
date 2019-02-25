package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ReviveCharRequest extends ClientPacket {
	// ReviveChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReviveChar;
	}
	public String userName;
	public ReviveCharRequest(String userName){
		this.userName = userName;
	}
	public static ReviveCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new ReviveCharRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

