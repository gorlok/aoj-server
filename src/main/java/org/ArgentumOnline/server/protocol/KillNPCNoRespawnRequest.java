package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class KillNPCNoRespawnRequest extends ClientPacket {
	// KillNPCNoRespawn
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillNPCNoRespawn;
	}
	public KillNPCNoRespawnRequest(){
	}
};

