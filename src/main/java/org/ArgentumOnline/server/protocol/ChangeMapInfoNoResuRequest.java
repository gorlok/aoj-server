package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoNoResuRequest extends ClientPacket {
	// ChangeMapInfoNoResu,b:noResu
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoResu;
	}
	public byte noResu;
	public ChangeMapInfoNoResuRequest(byte noResu){
		this.noResu = noResu;
	}
	public static ChangeMapInfoNoResuRequest decode(ByteBuf in) {    
		try {                                   
			byte noResu = readByte(in);
			return new ChangeMapInfoNoResuRequest(noResu);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

