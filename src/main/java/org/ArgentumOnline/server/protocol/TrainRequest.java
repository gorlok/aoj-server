package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TrainRequest extends ClientPacket {
	// Train,b:petIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Train;
	}
	public byte petIndex;
	public TrainRequest(byte petIndex){
		this.petIndex = petIndex;
	}
	public static TrainRequest decode(ByteBuf in) {    
		try {                                   
			byte petIndex = readByte(in);
			return new TrainRequest(petIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

