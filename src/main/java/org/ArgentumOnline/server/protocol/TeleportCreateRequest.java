package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TeleportCreateRequest extends ClientPacket {
	// TeleportCreate,i:mapa,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TeleportCreate;
	}
	public short mapa;
	public byte x;
	public byte y;
	public TeleportCreateRequest(short mapa,byte x,byte y){
		this.mapa = mapa;
		this.x = x;
		this.y = y;
	}
	public static TeleportCreateRequest decode(ByteBuf in) {    
		try {                                   
			short mapa = readShort(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new TeleportCreateRequest(mapa,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

