package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CarpenterObjectsResponse extends ServerPacket {
	// CarpenterObjects,i:count,(s:name,i:madera,i:index)[.]:objects
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CarpenterObjects;
	}
	public short count;
	public CarpenterObjects_DATA[] objects;
	public CarpenterObjectsResponse(short count,CarpenterObjects_DATA[] objects){
		this.count = count;
		this.objects = objects;
	}
	public static CarpenterObjectsResponse decode(ByteBuf in) {    
		try {                                   
			short count = readShort(in);
			CarpenterObjects_DATA[] objects = readCarpenterObjects_DATA[](in);
			return new CarpenterObjectsResponse(count,objects);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

