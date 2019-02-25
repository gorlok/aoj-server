package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreateNPCWithRespawnRequest extends ClientPacket {
	// CreateNPCWithRespawn,i:npcIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNPCWithRespawn;
	}
	public short npcIndex;
	public CreateNPCWithRespawnRequest(short npcIndex){
		this.npcIndex = npcIndex;
	}
};

