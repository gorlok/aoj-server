package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SpawnCreatureRequest extends ClientPacket {
	// SpawnCreature,i:npc
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpawnCreature;
	}
	public short npc;
	public SpawnCreatureRequest(short npc){
		this.npc = npc;
	}
};

