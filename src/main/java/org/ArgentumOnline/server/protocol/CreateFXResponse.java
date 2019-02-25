package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreateFXResponse extends ServerPacket {
	// CreateFX,i:charIndex,i:fx,i:fxLoops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CreateFX;
	}
	public short charIndex;
	public short fx;
	public short fxLoops;
	public CreateFXResponse(short charIndex,short fx,short fxLoops){
		this.charIndex = charIndex;
		this.fx = fx;
		this.fxLoops = fxLoops;
	}
	public static CreateFXResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			short fx = readShort(in);
			short fxLoops = readShort(in);
			return new CreateFXResponse(charIndex,fx,fxLoops);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

