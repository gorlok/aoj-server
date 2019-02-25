package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

