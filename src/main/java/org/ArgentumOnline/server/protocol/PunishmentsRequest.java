package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PunishmentsRequest extends ClientPacket {
	// Punishments,s:name
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Punishments;
	}
	public String name;
	public PunishmentsRequest(String name){
		this.name = name;
	}
	public static PunishmentsRequest decode(ByteBuf in) {    
		try {                                   
			String name = readStr(in);
			return new PunishmentsRequest(name);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

