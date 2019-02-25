package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OnlineMapRequest extends ClientPacket {
	// OnlineMap,i:map
	@Override
	public ClientPacketID id() {
		return ClientPacketID.OnlineMap;
	}
	public short map;
	public OnlineMapRequest(short map){
		this.map = map;
	}
	public static OnlineMapRequest decode(ByteBuf in) {    
		try {                                   
			short map = readShort(in);
			return new OnlineMapRequest(map);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

