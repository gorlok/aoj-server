package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateExpResponse extends ServerPacket {
	// UpdateExp,l:exp
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateExp;
	}
	public int exp;
	public UpdateExpResponse(int exp){
		this.exp = exp;
	}
	public static UpdateExpResponse decode(ByteBuf in) {    
		try {                                   
			int exp = readInt(in);
			return new UpdateExpResponse(exp);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeInt(out,exp);
	}
};

