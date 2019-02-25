package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PlayMidiResponse extends ServerPacket {
	// PlayMidi,b:midi,i:loops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PlayMidi;
	}
	public byte midi;
	public short loops;
	public PlayMidiResponse(byte midi,short loops){
		this.midi = midi;
		this.loops = loops;
	}
	public static PlayMidiResponse decode(ByteBuf in) {    
		try {                                   
			byte midi = readByte(in);
			short loops = readShort(in);
			return new PlayMidiResponse(midi,loops);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

