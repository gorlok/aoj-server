package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WalkRequest extends ClientPacket {
	// Walk,b:heading
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Walk;
	}
	public byte heading;
	public WalkRequest(byte heading){
		this.heading = heading;
	}
	public static WalkRequest decode(ByteBuf in) {    
		try {                                   
			byte heading = readByte(in);
			return new WalkRequest(heading);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

