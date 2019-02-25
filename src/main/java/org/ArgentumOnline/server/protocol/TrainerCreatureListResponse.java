package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TrainerCreatureListResponse extends ServerPacket {
	// TrainerCreatureList,s:npcList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.TrainerCreatureList;
	}
	public String npcList;
	public TrainerCreatureListResponse(String npcList){
		this.npcList = npcList;
	}
};

