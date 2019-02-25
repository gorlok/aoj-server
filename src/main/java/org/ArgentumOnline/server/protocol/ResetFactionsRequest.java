package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ResetFactionsRequest extends ClientPacket {
	// ResetFactions,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetFactions;
	}
	public String userName;
	public ResetFactionsRequest(String userName){
		this.userName = userName;
	}
	public static ResetFactionsRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new ResetFactionsRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

