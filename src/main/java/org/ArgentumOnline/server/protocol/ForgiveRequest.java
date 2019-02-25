package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForgiveRequest extends ClientPacket {
	// Forgive,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Forgive;
	}
	public String userName;
	public ForgiveRequest(String userName){
		this.userName = userName;
	}
	public static ForgiveRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new ForgiveRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

