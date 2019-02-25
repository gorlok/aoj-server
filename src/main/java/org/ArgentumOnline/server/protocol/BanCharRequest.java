package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BanCharRequest extends ClientPacket {
	// BanChar,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BanChar;
	}
	public String userName;
	public String reason;
	public BanCharRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
	public static BanCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String reason = readStr(in);
			return new BanCharRequest(userName,reason);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

