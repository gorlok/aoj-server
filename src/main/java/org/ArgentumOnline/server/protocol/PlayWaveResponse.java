package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PlayWaveResponse extends ServerPacket {
	// PlayWave,b:wave,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PlayWave;
	}
	public byte wave;
	public byte x;
	public byte y;
	public PlayWaveResponse(byte wave,byte x,byte y){
		this.wave = wave;
		this.x = x;
		this.y = y;
	}
	public static PlayWaveResponse decode(ByteBuf in) {    
		try {                                   
			byte wave = readByte(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new PlayWaveResponse(wave,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,wave);
		writeByte(out,x);
		writeByte(out,y);
	}
};

