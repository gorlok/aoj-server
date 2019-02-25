package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MakeDumbRequest extends ClientPacket {
	// MakeDumb,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MakeDumb;
	}
	public String userName;
	public MakeDumbRequest(String userName){
		this.userName = userName;
	}
	public static MakeDumbRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new MakeDumbRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

