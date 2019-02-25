package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BanIPUserRequest extends ClientPacket {
	// BanIPUser,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BanIPUser;
	}
	public String userName;
	public String reason;
	public BanIPUserRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
	public static BanIPUserRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String reason = readStr(in);
			return new BanIPUserRequest(userName,reason);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

