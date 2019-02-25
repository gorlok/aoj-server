package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateGoldResponse extends ServerPacket {
	// UpdateGold,l:gold
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateGold;
	}
	public int gold;
	public UpdateGoldResponse(int gold){
		this.gold = gold;
	}
};

