package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SpawnListResponse extends ServerPacket {
	// SpawnList,s:npcNamesList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SpawnList;
	}
	public String npcNamesList;
	public SpawnListResponse(String npcNamesList){
		this.npcNamesList = npcNamesList;
	}
};

