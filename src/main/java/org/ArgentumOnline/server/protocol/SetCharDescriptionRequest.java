package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SetCharDescriptionRequest extends ClientPacket {
	// SetCharDescription,s:desc
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetCharDescription;
	}
	public String desc;
	public SetCharDescriptionRequest(String desc){
		this.desc = desc;
	}
	public static SetCharDescriptionRequest decode(ByteBuf in) {    
		try {                                   
			String desc = readStr(in);
			return new SetCharDescriptionRequest(desc);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

