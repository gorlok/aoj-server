package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WarnUserRequest extends ClientPacket {
	// WarnUser,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarnUser;
	}
	public String userName;
	public String reason;
	public WarnUserRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
	public static WarnUserRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String reason = readStr(in);
			return new WarnUserRequest(userName,reason);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

