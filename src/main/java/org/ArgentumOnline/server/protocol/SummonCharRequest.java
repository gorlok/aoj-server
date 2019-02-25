package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SummonCharRequest extends ClientPacket {
	// SummonChar,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SummonChar;
	}
	public String userName;
	public SummonCharRequest(String userName){
		this.userName = userName;
	}
	public static SummonCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new SummonCharRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

