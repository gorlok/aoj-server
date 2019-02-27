package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlockedWithShieldOtherResponse extends ServerPacket {
	// BlockedWithShieldOther
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockedWithShieldOther;
	}
	public BlockedWithShieldOtherResponse(){
	}
	public static BlockedWithShieldOtherResponse decode(ByteBuf in) {    
		try {                                   
			return new BlockedWithShieldOtherResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

