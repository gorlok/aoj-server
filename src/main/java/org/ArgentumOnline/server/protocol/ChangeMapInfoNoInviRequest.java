package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoNoInviRequest extends ClientPacket {
	// ChangeMapInfoNoInvi,b:noInvisible
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoInvi;
	}
	public byte noInvisible;
	public ChangeMapInfoNoInviRequest(byte noInvisible){
		this.noInvisible = noInvisible;
	}
	public static ChangeMapInfoNoInviRequest decode(ByteBuf in) {    
		try {                                   
			byte noInvisible = readByte(in);
			return new ChangeMapInfoNoInviRequest(noInvisible);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

