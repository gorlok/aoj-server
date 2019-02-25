package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SilenceRequest extends ClientPacket {
	// Silence,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Silence;
	}
	public String userName;
	public SilenceRequest(String userName){
		this.userName = userName;
	}
	public static SilenceRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new SilenceRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

