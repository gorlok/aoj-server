package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoPKRequest extends ClientPacket {
	// ChangeMapInfoPK,b:isMapPk
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoPK;
	}
	public byte isMapPk;
	public ChangeMapInfoPKRequest(byte isMapPk){
		this.isMapPk = isMapPk;
	}
	public static ChangeMapInfoPKRequest decode(ByteBuf in) {    
		try {                                   
			byte isMapPk = readByte(in);
			return new ChangeMapInfoPKRequest(isMapPk);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

