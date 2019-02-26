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
			
			CarpenterObjects_DATA[] objects = new CarpenterObjects_DATA[count]; 
			for (int i = 0; i < count; i++) {
				objects[i].name = readStr(in);
				objects[i].madera = readShort(in);
				objects[i].index = readShort(in);
			}
			
			return new CarpenterObjectsResponse(count,objects);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

