package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ForceWAVEAllRequest extends ClientPacket {
	// ForceWAVEAll,b:waveId
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceWAVEAll;
	}
	public byte waveId;
	public ForceWAVEAllRequest(byte waveId){
		this.waveId = waveId;
	}
};

