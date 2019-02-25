package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NickToIPRequest extends ClientPacket {
	// NickToIP,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NickToIP;
	}
	public String userName;
	public NickToIPRequest(String userName){
		this.userName = userName;
	}
	public static NickToIPRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new NickToIPRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

