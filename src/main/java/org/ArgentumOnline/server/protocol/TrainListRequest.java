package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TrainListRequest extends ClientPacket {
	// TrainList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TrainList;
	}
	public TrainListRequest(){
	}
	public static TrainListRequest decode(ByteBuf in) {    
		try {                                   
			return new TrainListRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

