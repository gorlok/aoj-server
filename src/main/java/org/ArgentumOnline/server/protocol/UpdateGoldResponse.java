package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateGoldResponse extends ServerPacket {
	// UpdateGold,l:gold
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateGold;
	}
	public int gold;
	public UpdateGoldResponse(int gold){
		this.gold = gold;
	}
	public static UpdateGoldResponse decode(ByteBuf in) {    
		try {                                   
			int gold = readInt(in);
			return new UpdateGoldResponse(gold);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeInt(out,gold);
	}
};

