package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DenounceRequest extends ClientPacket {
	// Denounce,s:text
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Denounce;
	}
	public String text;
	public DenounceRequest(String text){
		this.text = text;
	}
	public static DenounceRequest decode(ByteBuf in) {    
		try {                                   
			String text = readStr(in);
			return new DenounceRequest(text);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

