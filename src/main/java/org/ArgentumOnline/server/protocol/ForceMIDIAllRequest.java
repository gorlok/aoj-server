package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

