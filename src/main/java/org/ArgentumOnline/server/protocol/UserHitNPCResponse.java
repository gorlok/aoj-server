package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserHitNPCResponse extends ServerPacket {
	// UserHitNPC,l:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserHitNPC;
	}
	public int damage;
	public UserHitNPCResponse(int damage){
		this.damage = damage;
	}
};

