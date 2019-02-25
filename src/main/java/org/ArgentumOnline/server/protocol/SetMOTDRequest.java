package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SetMOTDRequest extends ClientPacket {
	// SetMOTD,s:newMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetMOTD;
	}
	public String newMOTD;
	public SetMOTDRequest(String newMOTD){
		this.newMOTD = newMOTD;
	}
	public static SetMOTDRequest decode(ByteBuf in) {    
		try {                                   
			String newMOTD = readStr(in);
			return new SetMOTDRequest(newMOTD);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

