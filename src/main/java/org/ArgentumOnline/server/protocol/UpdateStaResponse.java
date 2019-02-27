package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateStaResponse extends ServerPacket {
	// UpdateSta,i:minSta
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateSta;
	}
	public short minSta;
	public UpdateStaResponse(short minSta){
		this.minSta = minSta;
	}
	public static UpdateStaResponse decode(ByteBuf in) {    
		try {                                   
			short minSta = readShort(in);
			return new UpdateStaResponse(minSta);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,minSta);
	}
};

