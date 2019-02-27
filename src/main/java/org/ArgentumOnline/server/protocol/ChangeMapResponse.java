package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapResponse extends ServerPacket {
	// ChangeMap,i:map,i:version
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeMap;
	}
	public short map;
	public short version;
	public ChangeMapResponse(short map,short version){
		this.map = map;
		this.version = version;
	}
	public static ChangeMapResponse decode(ByteBuf in) {    
		try {                                   
			short map = readShort(in);
			short version = readShort(in);
			return new ChangeMapResponse(map,version);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,map);
		writeShort(out,version);
	}
};

