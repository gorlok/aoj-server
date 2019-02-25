package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoLandRequest extends ClientPacket {
	// ChangeMapInfoLand,s:infoLand
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoLand;
	}
	public String infoLand;
	public ChangeMapInfoLandRequest(String infoLand){
		this.infoLand = infoLand;
	}
	public static ChangeMapInfoLandRequest decode(ByteBuf in) {    
		try {                                   
			String infoLand = readStr(in);
			return new ChangeMapInfoLandRequest(infoLand);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

