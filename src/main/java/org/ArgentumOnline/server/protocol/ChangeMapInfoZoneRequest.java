package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoZoneRequest extends ClientPacket {
	// ChangeMapInfoZone,s:infoZone
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoZone;
	}
	public String infoZone;
	public ChangeMapInfoZoneRequest(String infoZone){
		this.infoZone = infoZone;
	}
	public static ChangeMapInfoZoneRequest decode(ByteBuf in) {    
		try {                                   
			String infoZone = readStr(in);
			return new ChangeMapInfoZoneRequest(infoZone);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

