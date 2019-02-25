package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LevelUpResponse extends ServerPacket {
	// LevelUp,i:skillPoints
	@Override
	public ServerPacketID id() {
		return ServerPacketID.LevelUp;
	}
	public short skillPoints;
	public LevelUpResponse(short skillPoints){
		this.skillPoints = skillPoints;
	}
};

