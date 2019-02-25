package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MakeDumbNoMoreRequest extends ClientPacket {
	// MakeDumbNoMore,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MakeDumbNoMore;
	}
	public String userName;
	public MakeDumbNoMoreRequest(String userName){
		this.userName = userName;
	}
	public static MakeDumbNoMoreRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new MakeDumbNoMoreRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

