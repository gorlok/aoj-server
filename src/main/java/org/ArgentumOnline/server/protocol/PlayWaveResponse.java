package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

