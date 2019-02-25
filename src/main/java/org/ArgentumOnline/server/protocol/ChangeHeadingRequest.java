package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeHeadingRequest extends ClientPacket {
	// ChangeHeading,b:heading
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeHeading;
	}
	public byte heading;
	public ChangeHeadingRequest(byte heading){
		this.heading = heading;
	}
	public static ChangeHeadingRequest decode(ByteBuf in) {    
		try {                                   
			byte heading = readByte(in);
			return new ChangeHeadingRequest(heading);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

