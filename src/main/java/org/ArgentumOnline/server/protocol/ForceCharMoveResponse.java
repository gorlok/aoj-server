package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForceCharMoveResponse extends ServerPacket {
	// ForceCharMove,b:heading
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ForceCharMove;
	}
	public byte heading;
	public ForceCharMoveResponse(byte heading){
		this.heading = heading;
	}
	public static ForceCharMoveResponse decode(ByteBuf in) {    
		try {                                   
			byte heading = readByte(in);
			return new ForceCharMoveResponse(heading);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,heading);
	}
};

