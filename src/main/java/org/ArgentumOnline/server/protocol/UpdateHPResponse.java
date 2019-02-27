package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateHPResponse extends ServerPacket {
	// UpdateHP,i:minHP
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateHP;
	}
	public short minHP;
	public UpdateHPResponse(short minHP){
		this.minHP = minHP;
	}
	public static UpdateHPResponse decode(ByteBuf in) {    
		try {                                   
			short minHP = readShort(in);
			return new UpdateHPResponse(minHP);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,minHP);
	}
};

