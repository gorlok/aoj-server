package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

