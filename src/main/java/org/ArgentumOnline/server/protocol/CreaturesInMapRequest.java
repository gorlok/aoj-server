package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreaturesInMapRequest extends ClientPacket {
	// CreaturesInMap,i:map
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreaturesInMap;
	}
	public short map;
	public CreaturesInMapRequest(short map){
		this.map = map;
	}
	public static CreaturesInMapRequest decode(ByteBuf in) {    
		try {                                   
			short map = readShort(in);
			return new CreaturesInMapRequest(map);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

