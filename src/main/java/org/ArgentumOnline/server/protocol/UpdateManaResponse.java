package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateManaResponse extends ServerPacket {
	// UpdateMana,i:minMan
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateMana;
	}
	public short minMan;
	public UpdateManaResponse(short minMan){
		this.minMan = minMan;
	}
	public static UpdateManaResponse decode(ByteBuf in) {    
		try {                                   
			short minMan = readShort(in);
			return new UpdateManaResponse(minMan);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

