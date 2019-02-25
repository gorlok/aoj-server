package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoRestrictedRequest extends ClientPacket {
	// ChangeMapInfoRestricted,s:status
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoRestricted;
	}
	public String status;
	public ChangeMapInfoRestrictedRequest(String status){
		this.status = status;
	}
	public static ChangeMapInfoRestrictedRequest decode(ByteBuf in) {    
		try {                                   
			String status = readStr(in);
			return new ChangeMapInfoRestrictedRequest(status);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

