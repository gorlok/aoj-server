package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForceMIDIToMapRequest extends ClientPacket {
	// ForceMIDIToMap,b:midiId,i:map
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceMIDIToMap;
	}
	public byte midiId;
	public short map;
	public ForceMIDIToMapRequest(byte midiId,short map){
		this.midiId = midiId;
		this.map = map;
	}
	public static ForceMIDIToMapRequest decode(ByteBuf in) {    
		try {                                   
			byte midiId = readByte(in);
			short map = readShort(in);
			return new ForceMIDIToMapRequest(midiId,map);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

