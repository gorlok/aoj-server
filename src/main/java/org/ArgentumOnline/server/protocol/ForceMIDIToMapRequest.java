package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

