package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WhereRequest extends ClientPacket {
	// Where,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Where;
	}
	public String userName;
	public WhereRequest(String userName){
		this.userName = userName;
	}
	public static WhereRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new WhereRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

