package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForceMIDIAllRequest extends ClientPacket {
	// ForceMIDIAll,b:midiId
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceMIDIAll;
	}
	public byte midiId;
	public ForceMIDIAllRequest(byte midiId){
		this.midiId = midiId;
	}
	public static ForceMIDIAllRequest decode(ByteBuf in) {    
		try {                                   
			byte midiId = readByte(in);
			return new ForceMIDIAllRequest(midiId);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

