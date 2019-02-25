package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ExecuteRequest extends ClientPacket {
	// Execute,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Execute;
	}
	public String userName;
	public ExecuteRequest(String userName){
		this.userName = userName;
	}
	public static ExecuteRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new ExecuteRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

