package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForceWAVEToMapRequest extends ClientPacket {
	// ForceWAVEToMap,b:waveId,i:map,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceWAVEToMap;
	}
	public byte waveId;
	public short map;
	public byte x;
	public byte y;
	public ForceWAVEToMapRequest(byte waveId,short map,byte x,byte y){
		this.waveId = waveId;
		this.map = map;
		this.x = x;
		this.y = y;
	}
	public static ForceWAVEToMapRequest decode(ByteBuf in) {    
		try {                                   
			byte waveId = readByte(in);
			short map = readShort(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new ForceWAVEToMapRequest(waveId,map,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

